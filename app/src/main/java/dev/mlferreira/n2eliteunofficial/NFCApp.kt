package dev.mlferreira.n2eliteunofficial

import android.app.Application
import dev.mlferreira.n2eliteunofficial.Bank.Companion.MAX_BANKS

class NFCApp : Application() {

    val banks: MutableList<Bank> = MutableList(MAX_BANKS) { Bank() }
    var bankCount = MAX_BANKS
    var currentBank = -1
    var currentAction: NFCAction = NFCAction.ACTION_ID
//    private val folderController: FolderController = null
    var lastPick = 0
    var status: String? = null
        set(value) {
            updateStatus = true
            field = value
        }
    var updateStatus = false
    var writeBank: Byte = 0
    var writeFile: String? = null
    var writeGuid: String? = null


    override fun onCreate() {
        super.onCreate()
        resetAmiibos()
    }

    fun resetAmiibos() = banks.replaceAll { Bank() }

}