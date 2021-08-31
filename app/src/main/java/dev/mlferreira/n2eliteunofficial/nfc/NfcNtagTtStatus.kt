package dev.mlferreira.n2eliteunofficial.nfc

import java.util.*


class NfcNtagTtStatus(ttStatusBytes: ByteArray) {
    val isTampered: Boolean
        get() = !TT_INITIAL.contentEquals(message) || STATE_CLOSED != currentLoopState

    fun getMessage(): ByteArray = message.clone()


    private val message: ByteArray
    val currentLoopState: Byte

    companion object {
        const val STATE_CLOSED: Byte = 0x43
        const val STATE_OPEN: Byte = 0x4F
        const val STATE_INCORRECT: Byte = 0x49
        private val TT_INITIAL = byteArrayOf(0x30, 0x30, 0x30, 0x30)
    }

    init {
        require(ttStatusBytes.size == 5) { "Read TT status response length must be 5" }
        message = ByteArray(4)
        currentLoopState = ttStatusBytes[4]
        System.arraycopy(ttStatusBytes, 0, message, 0, message.size)
    }
}