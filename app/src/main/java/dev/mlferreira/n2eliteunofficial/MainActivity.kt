package dev.mlferreira.n2eliteunofficial

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.smartrac.nfc.NfcNtag


class MainActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null

    private val writeTagFilters = mutableListOf<IntentFilter>()


    override fun onCreate(savedInstanceState: Bundle?) {
        println("MAIN ACTIVITY ON CREATE")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            // if phone has no NFC
            Toast
                .makeText(this, "Sorry, your device does not support NFC! :(", Toast.LENGTH_LONG)
                .show()
            finish()
        } else if (nfcAdapter?.isEnabled == false) {
            findViewById<TextView>(R.id.enable_nfc).visibility = View.VISIBLE
        }

        // check and request permissions
        PermissionController(this)

        // wait for tag to be scanned
        nfcPendingIntent = PendingIntent
            .getActivity(
                this,
                14759,
                Intent(this, NFCActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                FLAG_ONE_SHOT
            )
        writeTagFilters.add(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))

    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters.toTypedArray(), null)

        if (nfcAdapter?.isEnabled == false) {
            findViewById<TextView>(R.id.enable_nfc).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.enable_nfc).visibility = View.INVISIBLE
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processNfcTag(intent)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun processNfcTag(intent: Intent) {
        val nfcTag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        if (nfcTag != null) {
            val nfcNtag: NfcNtag = NfcNtag(nfcTag)
            intent.putExtra("tag", nfcTag)

            Toast
                .makeText(this, "processing nfc", Toast.LENGTH_LONG)
                .show()

            startActivity(intent)

        }

    }


    fun btnSettingClick(view: View?) = startActivity(Intent(this, SettingActivity::class.java))

    fun openNFCSettings(view: View?) = startActivity(Intent(Settings.ACTION_NFC_SETTINGS))

}