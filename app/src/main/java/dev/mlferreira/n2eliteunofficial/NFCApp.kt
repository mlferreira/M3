package dev.mlferreira.n2eliteunofficial

import android.app.Application
import dev.mlferreira.n2eliteunofficial.entity.Bank
import dev.mlferreira.n2eliteunofficial.entity.Bank.Companion.MAX_BANKS
import dev.mlferreira.n2eliteunofficial.util.ActionEnum

class NFCApp : Application() {

    val banks: MutableList<Bank> = MutableList(MAX_BANKS) { Bank() }
    var bankCount = MAX_BANKS
    var currentBank = -1
    var currentAction: ActionEnum = ActionEnum.NONE
    var pickerValue = 0
    lateinit var folderController: FolderController
    var lastPick = 0
    var writeBank: Int = -1

    var writeFile: String? = null
    var writeGuid: String? = null


    override fun onCreate() {
        super.onCreate()
        resetAmiibos()
        folderController = FolderController(this)
    }

    fun resetAmiibos() = banks.replaceAll { Bank() }

}