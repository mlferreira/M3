package dev.mlferreira.n2eliteunofficial.nfc

import android.nfc.Tag
import android.nfc.tech.NfcA
import android.nfc.tech.TagTechnology
import android.util.Log
import dev.mlferreira.n2eliteunofficial.util.toHex
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException


/*
 * ADAPTED FROM SMARTRAC SDK
 * github.com/SMARTRACTECHNOLOGY-PUBLIC/smartrac-sdk-java-android-nfc
 *
 */

class N2Tag(
    private val tag: Tag
) : TagTechnology {

    private val nfca: NfcA = NfcA.get(tag)
//    private int maxTranscieveLength = ((this.nfca.getMaxTransceiveLength() / 4) + 1);
    val maxTransceiveLength: Int = nfca.maxTransceiveLength


    val version: ByteArray?
        get() {
            val req = ByteArray(1)
            req[0] = N2TagOpCode.GET_VERSION.byte
            return transceive(req)
        }

    val status: ByteArray?
        get() {
            val req = ByteArray(1)
            req[0] = N2TagOpCode.AMIIBO_GET_STATUS.byte
            return transceive(req)
        }

    val signature: ByteArray?
        get() {
            val req = ByteArray(1)
            req[0] = N2TagOpCode.AMIIBO_READ_SIG.byte
            return transceive(req)
        }

    private fun transceive(request: ByteArray): ByteArray? {
        return try {
            nfca.transceive(request)
        } catch (ex: IOException) {
            val op = N2TagOpCode.fromByte(request[0])
            Log.e(this::class.simpleName, "Error on $op request! - ${ex.message}")
            null
        } catch (ex: Exception) {
            throw ex
        }
    }

    override fun connect() {
        if (!isConnected()) {
            nfca.connect()
        }
    }

    override fun close() {
        if (isConnected()) {
            nfca.close()
        }
    }

    override fun getTag(): Tag = tag

    override fun isConnected(): Boolean = nfca.isConnected

    // NTAG READ_CNT
    fun readCnt(): Int {
        val req = ByteArray(2)
        val resp: ByteArray
        val cnt: Int
        req[0] = N2TagOpCode.READ_CNT.byte
        req[1] = 0x02
        try {
            resp = nfca.transceive(req)
            cnt = resp[0] + resp[1] * 256 + resp[2] * 65536
        } catch (ex: IOException) {
            return -1
        }
        return cnt
    }

    // NTAG PWD_AUTH
    fun pwdAuth(pwd: ByteArray?): ByteArray? {
        if (pwd == null || pwd.size != 4) {
            Log.w(this::class.simpleName, "[pwdAuth] Password <${pwd?.toHex()}> is not valid.")
            return null
        }
        val req = ByteArray(5)
        req[0] = N2TagOpCode.PWD_AUTH.byte
        System.arraycopy(pwd, 0, req, 1, 4)
        // result will be the PACK
        return transceive(req)
    }

    // NTAG SECTOR_SELECT
    fun sectorSelect(sector: Byte): Boolean {
        val req1 = ByteArray(2)
        val req2 = ByteArray(4)
        req1[0] = N2TagOpCode.SECTOR_SELECT.byte
        req1[1] = 0xFF.toByte()
        try {
            nfca.transceive(req1)
        } catch (ex: IOException) {
            return false
        }
        req2[0] = sector
        req2[1] = 0x00

        // The second part of this command works with negative acknowledge:
        // If the tag does not respond, the command worked OK.
        try {
            nfca.transceive(req2)
        } catch (ex: IOException) {
            return true
        }
        return false
    }

    // NTAG READ_TT_STATUS
    fun readTtStatus(): NfcNtagTtStatus? {
        val req = ByteArray(2)
        val resp: ByteArray
        req[0] = N2TagOpCode.READ_TT_STATUS.byte
        req[1] = 0x00
        resp = try {
            nfca.transceive(req)
        } catch (ex: IOException) {
            return null
        }
        val ttStatus: NfcNtagTtStatus
        ttStatus = try {
            NfcNtagTtStatus(resp)
        } catch (ex: IllegalArgumentException) {
            return null
        }
        return ttStatus // result will be the NTAG signature
    }

    // MF UL-C AUTHENTICATE part 1
    fun mfulcAuth1(): ByteArray? {
        val req = ByteArray(2)
        val resp: ByteArray?
        req[0] = N2TagOpCode.MFULC_AUTH1.byte
        req[1] = 0x00
        resp = try {
            nfca.transceive(req)
        } catch (ex: IOException) {
            null
        }
        return resp // result will be "AFh" + ekRndB
    }



    // MF UL-C AUTHENTICATE part 2
    fun mfulcAuth2(ekRndAB: ByteArray?): ByteArray? {
        if (ekRndAB == null) {
            return null
        }
        if (ekRndAB.size != 16) {
            return null
        }
        val req = ByteArray(17)
        val resp: ByteArray
        req[0] = N2TagOpCode.MFULC_AUTH2.byte
        resp = try {
            System.arraycopy(ekRndAB, 0, req, 1, 16)
            nfca.transceive(req)
        } catch (ex: IOException) {
            return null
        }
        return resp // result will be ekRndA'
    }

    fun fastRead(startAddr: Int, endAddr: Int, bank: Int): ByteArray? {
        if (endAddr < startAddr) return null
        var bOk = true
        val resp = ByteArray(4 * (endAddr - startAddr + 1))
        val maxReadLength = maxTransceiveLength / 4 - 1
        if (maxReadLength < 1) return null
        val iNumReads = 1 + (endAddr - startAddr + 1) / maxReadLength
        var i = 0
        while (i < iNumReads && bOk) {
            var endSnippet: Int
            val startSnippet = startAddr + i * maxReadLength
            endSnippet = if (i == iNumReads - 1) endAddr else startSnippet + maxReadLength - 1
            val respSnippet = fastReadHelper(startSnippet, endSnippet, bank)
            if (respSnippet == null) {
                bOk = false
            } else {
                if (respSnippet.size != 4 * (endSnippet - startSnippet + 1)) {
                    bOk = false
                } else {
                    System.arraycopy(respSnippet, 0, resp, i * maxReadLength, respSnippet.size)
                }
            }
            i++
        }
        return if (bOk) {
            resp
        } else null
    }

    private fun fastReadHelper(startAddr: Int, endAddr: Int, bank: Int): ByteArray? {
        val req = ByteArray(4)
        req[0] = N2TagOpCode.AMIIBO_FAST_READ.byte
        req[1] = (startAddr and 0xFF).toByte()
        req[2] = (endAddr and 0xFF).toByte()
        req[3] = (bank and 0xFF).toByte()
        return transceive(req)
    }

    fun write(addr: Int, bank: Int, data: ByteArray?): Boolean {
        if (data == null || data.size % 4 != 0) {
            Log.w(this::class.simpleName, "[write] Data (size ${data?.size}) is not valid.")
            return false
        } else {
            Log.i(this::class.simpleName, "[write] Data has size ${data.size}")
        }

        val totalChunks = data.size / 4

        for (i in 0 until totalChunks) {
            val chunk = ByteArray(4)
            System.arraycopy(data, i * 4, chunk, 0, 4)
            if (!writeHelper(addr, bank, chunk)) {
                return false
            }
        }

        return true
    }

    private fun writeHelper(addr: Int, bank: Int, data: ByteArray): Boolean {
        if (data.size != 4) {
            Log.w(this::class.simpleName, "[writeHelper] Data (size ${data?.size}) is not valid.")
            return false
        }

        val req = ByteArray(7)
        req[0] = N2TagOpCode.AMIIBO_WRITE.byte
        req[1] = (addr and 0xFF).toByte()
        req[2] = (bank and 0xFF).toByte()
        try {
            System.arraycopy(data, 0, req, 3, 4)
            nfca.transceive(req)
        } catch (ex: IOException) {
            Log.e(this::class.simpleName, "Error on write request! - ${ex.message}")
            return false
        } catch (ex: Exception) {
            throw ex
        }

        return true
    }

    // TODO??
    fun fastWrite(addr: Int, bank: Int, data: ByteArray?): Boolean {
        if (data == null) {
            Log.w(this::class.simpleName, "[write] Data is null.")
            return false
        } else {
            Log.i(this::class.simpleName, "[write] Data has size ${data.size}")
        }

        // ????????????????????????
        var i = addr
        val length = data.size / 4 + i
        var i3 = 16
        var i4 = 0
        while (i <= length) {
            val i5 = i + 4
            if (i5 >= length) {
                i3 = data.size % i3
            }
            if (i3 == 0) {
                return true
            }
            val bArr2 = ByteArray(i3)
            System.arraycopy(data, i4, bArr2, 0, i3)
            if (fastWriteHelper(i, bank, bArr2)) {
                return false
            }
            i4 += i3
            i = i5
        }

        return true
    }

    private fun fastWriteHelper(addr: Int, bank: Int, data: ByteArray): Boolean {
        val req = ByteArray(4 + data.size)
        req[0] = N2TagOpCode.AMIIBO_FAST_WRITE.byte
        req[1] = (addr and 0xFF).toByte()
        req[2] = (bank and 0xFF).toByte()
        req[3] = (data.size and 0xFF).toByte()
        try {
            System.arraycopy(data, 0, req, 4, data.size)
            nfca.transceive(req)
        } catch (ex: IOException) {
            Log.e(this::class.simpleName, "Error on write request! - ${ex.message}")
            return false
        } catch (ex: Exception) {
            throw ex
        }

        return true
    }


    fun setBankCount(i: Int): ByteArray? {
        val req = ByteArray(2)
        req[0] = N2TagOpCode.AMIIBO_SET_BANKCOUNT.byte
        req[1] = (i and 0xFF).toByte()
        return transceive(req)
    }

    fun setActiveBank(i: Int): ByteArray? {
        val req = ByteArray(2)
        req[0] = N2TagOpCode.AMIIBO_ACTIVATE_BANK.byte
        req[1] = (i and 0xFF).toByte()
        return transceive(req)
    }


}
