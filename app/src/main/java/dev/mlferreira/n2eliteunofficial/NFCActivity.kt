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
import java.io.IOException
import java.lang.IllegalStateException


class NFCActivity : AppCompatActivity() {

    private lateinit var app: NFCApp
    var nfcNtag: NfcNtag? = null
    private var nfcTag: Tag? = null



    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null

    private val writeTagFilters = mutableListOf<IntentFilter>()



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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        app = application as NFCApp

        nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        if (nfcTag == null) {
            Toast
                .makeText(this, "nfcTag is null????", Toast.LENGTH_LONG)
                .show()
        }

        getTagInfo()

    }

    fun getTagInfo() {
        nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        nfcNtag = NfcNtag.get(nfcTag)

        nfcNtag?.connect()

        if (nfcNtag?.version == null) {
            showErrorAndReturn("ERROR: Unsupported NFC tag found!")
        }

        val info = this.nfcNtag!!.amiiboGetVersion()

        if (info == null || info.size < 2) {
            Toast
                .makeText(this, "info wrong size ($info)", Toast.LENGTH_LONG)
                .show()
        }

        currentBank = info[0].toInt()
        numBanks = info[1].toInt()

        findViewById<TextView>(R.id.teste_numbanks).text = "Number of banks = $numBanks"
        findViewById<TextView>(R.id.teste_curbank).text = "Current bank = $currentBank"


        for (i in 0..numBanks) {
            val amiiboHex = nfcNtag?.amiiboFastRead(21, 22, i)
            Toast.makeText(this, "read bank $amiiboHex", Toast.LENGTH_LONG).show()
            if (amiiboHex != null && amiiboHex.size == 8) {
                val amiibo = Amiibo(amiiboHex)
                val tagId = nfcNtag?.amiiboFastRead(0, 1, i)
//                Toast.makeText(this, "bank id $tagId", Toast.LENGTH_LONG).show()
                if (tagId != null && tagId.size == 8) {
                    app.banks[i] = Bank(amiibo, tagId)
                }
            }
        }

    }



    fun handleTag() {
        nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        nfcNtag = NfcNtag(nfcTag)

        if (nfcNtag?.version == null) {
            showErrorAndReturn("ERROR: Unsupported NFC tag found!")
        }

        // tagId()

        try {
            nfcNtag!!.connect()

            when (app.currentAction as NFCAction) {
                NFCAction.ACTION_ID -> TODO()
                NFCAction.ACTION_WRITE -> TODO()
                NFCAction.ACTION_EMPTY -> TODO()
                NFCAction.ACTION_SETBANKCOUNT -> writeBankCount()
                else -> TODO()
            }
        } catch (e: Exception) {
            this.nfcTag = null
        }

    }

    fun tagId() {
        app.resetAmiibos()

        if (nfcNtag?.version == null) {
            showErrorAndReturn("ERROR: Unsupported NFC tag found!")
        }


        val amiiboGetVersion = this.nfcNtag?.amiiboGetVersion()

        if (amiiboGetVersion == null || amiiboGetVersion.size < 2) {
            reconnectTag()
//            enableNtagButtons() // -> mostra só os botoes de dump e unlock
//            return 0 // -> tagId = 0 na ACTION_ID vai pra funcao readAmiibo()
            return
        }

        currentBank = amiiboGetVersion[0]!!.toInt()
        numBanks = amiiboGetVersion[1]!!.toInt()


//        TextView textView = this.status;
//        textView.setText(String.format(getAppName() + " found! (%d slots, #%d selected)", new Object[]{Integer.valueOf(this.numBanks & 255), Integer.valueOf((this.currentBank & 255) + 1)}));

        if ((amiiboGetVersion.size != 4 || amiiboGetVersion[3].toInt() == 3)
            && amiiboGetVersion.size != 2 && this.currentBank != 100 && this.numBanks != 0
        ) {
            val amiiboReadSig = nfcNtag?.amiiboReadSig()
            if (amiiboReadSig != null) {
                this.m_id = amiiboReadSig.toHex()
            }

            for (i in 0..numBanks) {
                try {
                    val amiiboFastRead = nfcNtag?.amiiboFastRead(21, 22, i)
                    if (amiiboFastRead != null) {
                        if (amiiboFastRead.size == 8) {
                            val FromStatueId = Amiibo(amiiboFastRead!!)
                            val amiiboFastRead2 = nfcNtag?.amiiboFastRead(0, 1, i)
                            if (amiiboFastRead2 != null) {
                                if (amiiboFastRead2.size == 8) {
                                    app.banks[i] = Bank(FromStatueId, amiiboFastRead2)
                                }
                            }
                            throw Exception();
                        }
                    }
                    throw Exception();
                } catch (e: Exception) {
//                    this.status.setText("Failed to parse bank $i");
                    this.app.banks[i] = Bank()
                }

            }

//            this.logo.setImageResource(R.drawable.logo);
//            enableAmiigoButtons();
//            return 1;

        }

    }


    private fun writeBankCount() {
        try {
            if (nfcNtag!!.amiiboSetBankcount(pickerValue) == null) {
                showErrorAndReturn("ERROR: Failed to set bank count! Please try again.")
                return
            }
            // TODO: success message
//            this.status.setText(
//                String.format(
//                    "OK! Bank count updated! (%d)", *arrayOf<Any>(
//                        Integer.valueOf(
//                            pickerValue
//                        )
//                    )
//                )
//            )
            numBanks = pickerValue
        } catch (unused: IllegalStateException) {
            showErrorAndReturn("Please try scanning again.")
        }
    }


    fun btnSetBanksNoClick(view: View?) {
        val relativeLayout = RelativeLayout(applicationContext)
        val numberPicker = NumberPicker(this)
        numberPicker.maxValue = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION
        numberPicker.minValue = 1
        numberPicker.value = numBanks and 255
        numberPicker.setOnValueChangedListener { np, i, i2 ->
            pickerValue = np.value
            app.currentAction = NFCAction.ACTION_SETBANKCOUNT
            // TODO: scan message
//            val `access$200`: TextView = status
//            `access$200`.text = String.format(
//                "Please tap the tag to update the bank count!",
//                *arrayOfNulls<Any>(0)
//            )
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
            app.currentAction = NFCAction.ACTION_SETBANKCOUNT
//            val `access$200`: TextView = this@MainActivity.status
//            `access$200`.text = String.format(
//                "Please tap " + this@MainActivity.getAppName() + " to update bank count!",
//                *arrayOfNulls<Any>(0)
//            )
        }.setNegativeButton(
            "Cancel"
        ) { dialogInterface, i -> dialogInterface.cancel() }
        builder.create().show()
    }

    fun btnSettingClick(view: View?) = startActivity(Intent(this, SettingActivity::class.java))

    fun btnManageBanksClick(view: View?) = startActivity(Intent(this, GridActivity::class.java))


    private fun reconnectTag() {
        try {
            nfcNtag!!.close()
            nfcNtag!!.connect()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun showErrorAndReturn(msg: String? = null) {
        Toast.makeText(this, (msg ?: "ERROR!"), Toast.LENGTH_LONG).show()
        this.finish()
    }



    fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

}