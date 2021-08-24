package dev.mlferreira.n2eliteunofficial

import android.content.Context
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import java.io.File

class SettingActivity : PreferenceActivity() {

//    private var folderController: FolderController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TODO()
    }

    /*
    public override fun onCreate2(bundle: Bundle?) {
        super.onCreate(bundle)
        addPreferencesFromResource(R.xml.preference)
        folderController = FolderController(this)
        defaultClick("restore_pref", "Select a Restore Folder", FolderController.DIRECTORY_RESTORE)
        defaultClick(
            "backup_pref",
            "Select a Backup/Dump Folder",
            FolderController.DIRECTORY_BACKUP
        )
        checkFastWrite(this)
    }

    /* access modifiers changed from: private */
    fun chooseFolder(preference: Preference, str: String?, str2: String?) {
        val dialogProperties = DialogProperties()
        dialogProperties.selection_mode = 0
        dialogProperties.selection_type = 1
        dialogProperties.root = File(DialogConfigs.DIRECTORY_SEPERATOR)
        dialogProperties.error_dir = File(DialogConfigs.DEFAULT_DIR)
        dialogProperties.extensions = null
        val filePickerDialog = FilePickerDialog(this, dialogProperties)
        filePickerDialog.setTitle(str)
        filePickerDialog.setDialogSelectionListener(object : DialogSelectionListener() {
            var folderController: FolderController =
                FolderController(this@SettingActivity.applicationContext)

            fun onSelectedFilePaths(strArr: Array<String?>) {
                val str = if (strArr.size != 0) strArr[0] else DialogConfigs.DEFAULT_DIR
                preference.summary = str
                this.folderController.saveDirectory(str2, str)
            }
        })
        filePickerDialog.show()
    }

    private fun defaultClick(str: String, str2: String, str3: String) {
        val findPreference = preferenceScreen.findPreference(str)
        findPreference.setSummary(folderController.getDirectory(str3))
        findPreference.onPreferenceClickListener = OnPreferenceClickListener {
            chooseFolder(findPreference, str2, str3)
            true
        }
    }

    private fun checkFastWrite(context: Context) {
        (preferenceManager.findPreference("useFastWrite_pref") as CheckBoxPreference?)!!.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, obj ->
                val edit = context.getSharedPreferences(FolderController.SETTINGS_KEY, 0).edit()
                edit.putBoolean("useFastWrite_pref", (obj as Boolean).toBoolean())
                edit.commit()
                true
            }
    }
    */
}