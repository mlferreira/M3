package dev.mlferreira.n2eliteunofficial.activity

import android.app.AlertDialog
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import dev.mlferreira.n2eliteunofficial.NFCApp
import dev.mlferreira.n2eliteunofficial.R
import dev.mlferreira.n2eliteunofficial.entity.Amiibo
import dev.mlferreira.n2eliteunofficial.entity.Bank
import dev.mlferreira.n2eliteunofficial.nfc.N2Tag
import dev.mlferreira.n2eliteunofficial.util.ActionEnum
import dev.mlferreira.n2eliteunofficial.util.toHex


class MenuActivity : AppCompatActivity() {

    private lateinit var app: NFCApp
    private lateinit var confirmation: Intent
    private var tag: N2Tag? = null

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


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "[onCreate] started")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        app = application as NFCApp
        confirmation = Intent(this, NFCTapActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        getTagInfo()
    }



    private fun getTagInfo() {
        Log.d(this::class.simpleName, "[getTagInfo] started")

        getNFCTag()

        val info = tag?.status

        if (info == null || info.size < 2) {
            Log.e(this::class.simpleName, "[getTagInfo] info wrong size (${info?.toHex()})")
            return
        }

        currentBank = info[0].toInt()
        numBanks = info[1].toInt()
        val tagID = tag?.signature?.toHex() ?: ""

        findViewById<TextView>(R.id.text_tag_id).text = getString(R.string.tag_id, tagID)
        findViewById<TextView>(R.id.text_bank_count).text = getString(R.string.bank_count, numBanks)
        findViewById<TextView>(R.id.text_current_bank).text = getString(R.string.current_bank, currentBank + 1)


        for (i in 0..numBanks) {
            val amiiboHex = tag?.fastRead(21, 22, i)
            if (amiiboHex != null && amiiboHex.size == 8) {
                val amiibo = Amiibo(amiiboHex, this)
                val tagId = tag?.fastRead(0, 1, i)
                if (tagId != null && tagId.size == 8) {
                    app.banks[i] = Bank(amiibo, tagId)
                }
            }
        }

    }


    fun btnSetBanksNoClick(view: View?) {
        Log.d(this::class.simpleName, "[btnSetBanksNoClick] started")
//        val relativeLayout = findViewById<RelativeLayout>(R.id.layout_picker_bank_count)
        val relativeLayout = RelativeLayout(applicationContext)
//        val numberPicker = findViewById<NumberPicker>(R.id.picker_bank_count)
        val numberPicker = NumberPicker(this)
        numberPicker.maxValue = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION
        numberPicker.minValue = 1
        numberPicker.value = numBanks and 255
        numberPicker.setOnValueChangedListener { np, i, i2 ->
            app.pickerValue = np.value
            app.currentAction = ActionEnum.BANK_COUNT
        }
        val layoutParams = RelativeLayout.LayoutParams(50, 50)
        val layoutParams2 = RelativeLayout.LayoutParams(-2, -2)
        layoutParams2.addRule(14)
        relativeLayout.layoutParams = layoutParams
        relativeLayout.addView(numberPicker, layoutParams2)
        val builder = AlertDialog.Builder(this)
            .setTitle("Select number of banks")
            .setView(relativeLayout)
            .setCancelable(false).setPositiveButton("Ok") { dialogInterface, i ->
                app.pickerValue = numberPicker.value
                app.currentAction = ActionEnum.BANK_COUNT
                startActivity(confirmation)
            }
            .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.cancel() }
        builder.create().show()
    }

    fun btnSettingClick(view: View?) = startActivity(Intent(this, SettingActivity::class.java))

    fun btnManageBanksClick(view: View?) = startActivity(Intent(this, GridActivity::class.java))

    fun btnLockClick(view: View?) : Nothing = TODO()


    private fun reconnectTag() {
        Log.d(this::class.simpleName, "[reconnectTag] started")
        if (tag?.isConnected == true) {
            tag?.close()
        }
        tag?.connect()
    }

    private fun getNFCTag() {
        tag?.close()

        val nfcTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return

        tag = N2Tag(nfcTag)

        reconnectTag()
    }
}