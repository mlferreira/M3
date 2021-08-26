package dev.mlferreira.n2eliteunofficial

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.smartrac.nfc.NfcNtag
import dev.mlferreira.n2eliteunofficial.util.ActionEnum
import java.lang.IllegalStateException


class NFCTapActivity : AppCompatActivity() {

    private lateinit var app: NFCApp
    var nfcNtag: NfcNtag? = null
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
        nfcNtag = NfcNtag.get(nfcTag)

        if (!nfcNtag!!.isConnected) {
            nfcNtag!!.connect()
        }

        return when(app.currentAction) {
            ActionEnum.BANK_COUNT -> {
                Log.d(this::class.simpleName, "[handleNFC] changing bank count to ${app.pickerValue}")
                if (nfcNtag!!.amiiboSetBankcount(app.pickerValue) == null) {
                    Log.e(this::class.simpleName, "[handleNFC] setting bank count returned null")
                    showErrorAndReturn("ERROR: Failed to set bank count! Please try again.")
                } else {
                    app.bankCount = app.pickerValue
                }
                return Intent(this, MenuActivity::class.java)
            }
            ActionEnum.ACTION_ACTIVATE -> {
                Log.d(this::class.simpleName, "[handleNFC] changing bank count to ${app.writeBank}")
                if (nfcNtag!!.amiiboActivateBank(app.writeBank) == null) {
                    Log.e(this::class.simpleName, "[handleNFC] activating bank count returned null")
                    showErrorAndReturn("ERROR: Failed to set bank count! Please try again.")
                } else {
                    app.currentBank = app.writeBank
                }
                return Intent(this, GridActivity::class.java)
            }
            else -> {
                Log.w(this::class.simpleName, "[handleNFC] action not mapped - ${app.currentAction}")
                Intent(this, MenuActivity::class.java)
            }
        }
    }

    private fun showErrorAndReturn(msg: String? = null) {
        Log.e(this::class.simpleName, "[showErrorAndReturn] $msg")
        Toast.makeText(this, (msg ?: "ERROR!"), Toast.LENGTH_LONG).show()
        this.finish()
    }

}