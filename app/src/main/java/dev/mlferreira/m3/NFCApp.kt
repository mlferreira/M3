package dev.mlferreira.m3

import android.app.Application
import dev.mlferreira.m3.entity.Bank
import dev.mlferreira.m3.entity.Bank.Companion.MAX_BANKS
import dev.mlferreira.m3.rest.FolderController
import dev.mlferreira.m3.util.ActionEnum

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

    var intParam: Int = -1
    var stringParam: String? = null


    override fun onCreate() {
        super.onCreate()
        resetAmiibos()
        folderController = FolderController(this)
    }

    fun resetAmiibos() = banks.replaceAll { Bank() }

}