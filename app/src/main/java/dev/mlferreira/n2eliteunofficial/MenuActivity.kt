package dev.mlferreira.n2eliteunofficial

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import com.smartrac.nfc.NfcNtag
import dev.mlferreira.n2eliteunofficial.entity.Amiibo
import dev.mlferreira.n2eliteunofficial.entity.Bank
import dev.mlferreira.n2eliteunofficial.util.ActionEnum
import dev.mlferreira.n2eliteunofficial.util.toHex
import dev.mlferreira.n2eliteunofficial.util.toHexBigInt
import java.io.IOException
import java.lang.IllegalStateException


class MenuActivity : AppCompatActivity() {

    private lateinit var app: NFCApp
    var nfcNtag: NfcNtag? = null
    private var nfcTag: Tag? = null

    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null
    private val intentFilters = mutableListOf<IntentFilter>()


    private var currentBank: Int = -1
        set (value) {
            field = value
            app.currentBank = value
        }
    private var numBanks: Int = 0
        set (value) {
            field = (value and 255)
            app.bankCount = (value and 255)
        }
    private var pickerValue: Int = 0
    private var m_id: String? = null

    val numberPicker = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "[onCreate] started")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        findViewById<TextView>(R.id.alert_tap).visibility = View.GONE

        app = application as NFCApp
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        if (nfcTag == null) {
            Toast.makeText(this, "nfcTag is null", Toast.LENGTH_LONG).show()
        }

        getTagInfo()
    }



    fun getTagInfo() {
        Log.d(this::class.simpleName, "[getTagInfo] started")
        nfcNtag?.close()

        nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        nfcNtag = NfcNtag.get(nfcTag)

        if (nfcNtag?.isConnected == false) {
            nfcNtag?.connect()
        }

        if (nfcNtag?.version == null) {
            reconnectTag()
        }

        val info = this.nfcNtag!!.amiiboGetVersion()

        if (info == null || info.size < 2) {
            Toast
                .makeText(this, "info wrong size ($info)", Toast.LENGTH_LONG)
                .show()
        }

        currentBank = info[0].toInt()
        numBanks = info[1].toInt()
        val tagID = nfcNtag?.amiiboReadSig()?.toHex()

        findViewById<TextView>(R.id.text_tag_id).text = getString(R.string.tag_id, tagID)
        findViewById<TextView>(R.id.text_bank_count).text = getString(R.string.bank_count, numBanks)
        findViewById<TextView>(R.id.text_current_bank).text = getString(R.string.current_bank, currentBank, "?")


        for (i in 0..numBanks) {
            val amiiboHex = nfcNtag?.amiiboFastRead(21, 22, i)
            if (amiiboHex != null && amiiboHex.size == 8) {
                val amiibo = Amiibo(amiiboHex)
                val tagId = nfcNtag?.amiiboFastRead(0, 1, i)
                if (tagId != null && tagId.size == 8) {
                    app.banks[i] = Bank(amiibo, tagId)
                }
            }
        }

        findViewById<TextView>(R.id.text_current_bank).text = getString(R.string.current_bank, currentBank, app.banks[currentBank].amiibo.name)

    }



    fun btnSetBanksNoClick(view: View?) {
        Log.d(this::class.simpleName, "[btnSetBanksNoClick] started")
        val relativeLayout = RelativeLayout(applicationContext)
        val numberPicker = NumberPicker(this)
        numberPicker.maxValue = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION
        numberPicker.minValue = 1
        numberPicker.value = numBanks and 255
        numberPicker.setOnValueChangedListener { np, i, i2 ->
            pickerValue = np.value
            app.currentAction = ActionEnum.BANK_COUNT
        }
        val layoutParams = RelativeLayout.LayoutParams(50, 50)
        val layoutParams2 = RelativeLayout.LayoutParams(-2, -2)
        layoutParams2.addRule(14)
        relativeLayout.layoutParams = layoutParams
        relativeLayout.addView(numberPicker, layoutParams2)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select number of banks")
        builder.setView(relativeLayout)
        builder.setCancelable(false).setPositiveButton(
            "Ok"
        ) { dialogInterface, i ->
            Log.e("", "New Quantity Value : " + numberPicker.value)
            pickerValue = numberPicker.value
            app.pickerValue = numberPicker.value
            app.currentAction = ActionEnum.BANK_COUNT
//            nfcPendingIntent = PendingIntent
//                .getActivity(
//                    this,
//                    87412,
//                    Intent(this, NFCTapActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
//                    0
//                )
//            intentFilters.add(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
//            findViewById<TextView>(R.id.alert_tap).visibility = View.VISIBLE
//            nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, intentFilters.toTypedArray(), null)
            val menuIntent = Intent(this, NFCTapActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(menuIntent)
        }.setNegativeButton(
            "Cancel"
        ) { dialogInterface, i -> dialogInterface.cancel() }
        builder.create().show()
    }

    fun btnSettingClick(view: View?) = startActivity(Intent(this, SettingActivity::class.java))

    fun btnManageBanksClick(view: View?) = startActivity(Intent(this, GridActivity::class.java))

    fun btnLockClick(view: View?) : Nothing = TODO()


    private fun reconnectTag() {
        Log.d(this::class.simpleName, "[reconnectTag] started")
        try {
            nfcNtag!!.close()
            nfcNtag!!.connect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        Log.d(this::class.simpleName, "[onResume] started")
        super.onResume()
        if (nfcPendingIntent != null) {
            nfcAdapter?.enableForegroundDispatch(
                this,
                nfcPendingIntent,
                intentFilters.toTypedArray(),
                null
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        Log.d(this::class.simpleName, "[onNewIntent] started")
        super.onNewIntent(intent)

        nfcPendingIntent = null
        setIntent(intent)

        nfcNtag?.close()

        nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        nfcNtag = NfcNtag.get(nfcTag)

        if (!nfcNtag!!.isConnected) {
            nfcNtag!!.connect()
        }

        when(app.currentAction) {
            ActionEnum.BANK_COUNT -> {
                Log.d(this::class.simpleName, "[onNewIntent] changing bank count to ${app.pickerValue}")
                try {
                    val response = nfcNtag!!.amiiboSetBankcount(app.pickerValue)
                    if (response == null) {
                        Log.d(this::class.simpleName, "[onNewIntent] setting bank count returned null")
                        showErrorAndReturn("ERROR: Failed to set bank count! Please try again.")
                        return
                    }
                    Log.d(this::class.simpleName, "[onNewIntent] response = ${response.toHex()}")
                    app.currentAction = ActionEnum.NONE
                    findViewById<TextView>(R.id.alert_tap).visibility = View.GONE
                } catch (unused: IllegalStateException) {
                    Log.d(this::class.simpleName, "[onNewIntent] setting bank threw error")
                    showErrorAndReturn("Please try scanning again.")
                }
            }
            else -> {
                Log.w(this::class.simpleName, "[onNewIntent] action not mapped - ${app.currentAction}")
            }
        }

        findViewById<TextView>(R.id.alert_tap).visibility = View.GONE

        getTagInfo()
    }

    override fun onPause() {
        Log.d(this::class.simpleName, "[onPause] started")
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun showErrorAndReturn(msg: String? = null) {
        Log.d(this::class.simpleName, "[showErrorAndReturn] started")
        Toast.makeText(this, (msg ?: "ERROR!"), Toast.LENGTH_LONG).show()
        this.finish()
    }

}