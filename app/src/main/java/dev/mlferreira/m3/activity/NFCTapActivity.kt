package dev.mlferreira.m3.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import dev.mlferreira.m3.NFCApp
import dev.mlferreira.m3.R
import dev.mlferreira.m3.nfc.N2Tag
import dev.mlferreira.m3.rest.FolderController
import dev.mlferreira.m3.util.ActionEnum


class NFCTapActivity : AppCompatActivity() {

    private lateinit var app: NFCApp
    var nfcNtag: N2Tag? = null
    private var nfcTag: Tag? = null
    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null
    private val intentFilters = mutableListOf<IntentFilter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "[onCreate] started")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap)

        app = application as NFCApp
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // wait for tag to be scanned
        nfcPendingIntent = PendingIntent
            .getActivity(
                this,
                7984,
                Intent(this, NFCTapActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_ONE_SHOT
            )
        intentFilters.add(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
    }

    override fun onResume() {
        Log.d(this::class.simpleName, "[onResume] started")
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, intentFilters.toTypedArray(), null)
    }

    override fun onNewIntent(intent: Intent) {
        Log.d(this::class.simpleName, "[onNewIntent] started")

        super.onNewIntent(intent)

        val menuIntent = handleNFC(intent)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .putExtra("tag", nfcTag)

        app.currentAction = ActionEnum.NONE


        startActivity(menuIntent)
    }

    override fun onPause() {
        Log.d(this::class.simpleName, "[onPause] started")
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun handleNFC(nIntent: Intent): Intent {
        Log.d(this::class.simpleName, "[handleNFC] started")

        nfcTag = nIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        nfcNtag = N2Tag(nfcTag!!)

        if (!nfcNtag!!.isConnected) {
            nfcNtag!!.connect()
        }

        return when(app.currentAction) {
            ActionEnum.BANK_COUNT -> {
                Log.d(this::class.simpleName, "[handleNFC] changing bank count to ${app.pickerValue}")
                if (nfcNtag!!.setBankCount(app.pickerValue) == null) {
                    Log.e(this::class.simpleName, "[handleNFC] setting bank count returned null")
                    showErrorAndReturn("ERROR: Failed to set bank count! Please try again.")
                } else {
                    app.bankCount = app.pickerValue
                }
                return Intent(this, MenuActivity::class.java)
            }
            ActionEnum.ACTIVATE -> {
                Log.d(this::class.simpleName, "[handleNFC] changing bank count to ${app.writeBank}")
                if (nfcNtag!!.setActiveBank(app.writeBank) == null) {
                    Log.e(this::class.simpleName, "[handleNFC] activating bank count returned null")
                    showErrorAndReturn("ERROR: Failed to set bank count! Please try again.")
                } else {
                    app.currentBank = app.writeBank
                }
                return Intent(this, GridActivity::class.java)
            }
            ActionEnum.WRITE -> {
                Log.d(this::class.simpleName, "[handleNFC] writing to bank ${app.writeBank}")
                val data = app.folderController.readBlobFromFile(app.writeFile!!)
                if (!write(data)) {
                    Log.e(this::class.simpleName, "[handleNFC] writing amiibo returned false")
                    showErrorAndReturn("ERROR: Failed to set bank count! Please try again.")
                }
                return Intent(this, GridActivity::class.java)
            }
            else -> {
                Log.w(this::class.simpleName, "[handleNFC] action not mapped - ${app.currentAction}")
                Intent(this, MenuActivity::class.java)
            }
        }
    }

    private fun write(data: ByteArray?): Boolean {
        if (data == null) {
            showErrorAndReturn("The file is null.")
            return false
        }

        if (data.size < 540) {
            showErrorAndReturn("The file seems to be invalid. It must have at least 540 bytes.")
            return false
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val fastWrite = pref.getBoolean(getString(R.string.key_fast_write), true)
        Log.w(this::class.simpleName, "[write] using fast write: $fastWrite")

        val bArr = ByteArray(540)
        System.arraycopy(data, 0, bArr, 0, 540)

        // TODO: check tagAuth()?


        if (fastWrite) {
            if (nfcNtag!!.fastWrite(0, app.writeBank, bArr)) {
                showErrorAndReturn("Could not write amiibo to bank #" + (app.writeBank and 255) + 1)
                return false
            }
        } else {
            if (nfcNtag!!.write(0, app.writeBank, bArr)) {
                showErrorAndReturn("Could not write amiibo to bank #" + (app.writeBank and 255) + 1)
                return false
            }
        }


        return true
    }

    private fun showErrorAndReturn(msg: String? = null) {
        Log.e(this::class.simpleName, "[showErrorAndReturn] $msg")
        Toast.makeText(this, (msg ?: "ERROR!"), Toast.LENGTH_LONG).show()
        this.finish()
    }

}