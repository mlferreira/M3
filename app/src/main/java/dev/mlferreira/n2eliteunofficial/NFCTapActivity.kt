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

        handleNFC(intent)

        if (nfcTag != null) {
            val menuIntent = Intent(this, MenuActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("tag", nfcTag)
            startActivity(menuIntent)
        }
    }

    override fun onPause() {
        Log.d(this::class.simpleName, "[onPause] started")
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun handleNFC(nIntent: Intent) {
        Log.d(this::class.simpleName, "[handleNFC] started")

        nfcTag = nIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        nfcNtag = NfcNtag.get(nfcTag)

        if (!nfcNtag!!.isConnected) {
            nfcNtag!!.connect()
        }

        when(app.currentAction) {
            ActionEnum.BANK_COUNT -> {
                Log.d(this::class.simpleName, "[handleNFC] changing bank count")
                try {
                    if (nfcNtag!!.amiiboSetBankcount(app.pickerValue) == null) {
                        Log.d(this::class.simpleName, "[handleNFC] setting bank count returned null")
                        showErrorAndReturn("ERROR: Failed to set bank count! Please try again.")
                        return
                    }
                    app.currentAction = ActionEnum.NONE
                    findViewById<TextView>(R.id.alert_tap).visibility = View.GONE
                } catch (unused: IllegalStateException) {
                    Log.d(this::class.simpleName, "[handleNFC] setting bank threw error")
                    showErrorAndReturn("Please try scanning again.")
                }
            }
            else -> {
                Log.w(this::class.simpleName, "[handleNFC] action not mapped - ${app.currentAction}")
            }
        }
    }

    private fun showErrorAndReturn(msg: String? = null) {
        Log.d(this::class.simpleName, "[showErrorAndReturn] started")
        Toast.makeText(this, (msg ?: "ERROR!"), Toast.LENGTH_LONG).show()
        this.finish()
    }

}