package dev.mlferreira.n2eliteunofficial

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null
    private val intentFilters = mutableListOf<IntentFilter>()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "[onCreate] started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // if phone has no NFC
        if (nfcAdapter == null) {
            Toast
                .makeText(this, "Sorry, your device does not support NFC! :(", Toast.LENGTH_LONG)
                .show()
            finish()
        } else if (nfcAdapter?.isEnabled == false) {
            // if NFC is disabled, show message to enable it
            findViewById<TextView>(R.id.enable_nfc).visibility = View.VISIBLE
        }

        // check and request permissions
        PermissionController(this)

        // wait for tag to be scanned
        nfcPendingIntent = PendingIntent
            .getActivity(
                this,
                14759,
                Intent(this, MenuActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_ONE_SHOT
            )
        intentFilters.add(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))

    }

    override fun onResume() {
        Log.d(this::class.simpleName, "[onResume] started")
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, intentFilters.toTypedArray(), null)

        if (nfcAdapter?.isEnabled == false) {
            findViewById<TextView>(R.id.enable_nfc).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.enable_nfc).visibility = View.INVISIBLE
        }
    }

    override fun onPause() {
        Log.d(this::class.simpleName, "[onPause] started")
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }


    fun btnSettingClick(view: View?) = startActivity(Intent(this, SettingActivity::class.java))

    fun openNFCSettings(view: View?) = startActivity(Intent(Settings.ACTION_NFC_SETTINGS))

}