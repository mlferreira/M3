package dev.mlferreira.n2eliteunofficial.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.angads25.filepicker.controller.DialogSelectionListener
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import dev.mlferreira.n2eliteunofficial.R
import dev.mlferreira.n2eliteunofficial.rest.FolderController
import java.io.File


class SettingActivity : AppCompatActivity() {

    private var folderController: FolderController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        return

//        setContentView(R.layout.preferences)

        //If you want to insert data in your settings
        val settingsFragment: SettingFragment = SettingFragment()
//        settingsFragment. ...
//        getSupportFragmentManager().beginTransaction().replace(R.id.<YourFrameLayout>,settingsFragment).commit()

        //Else
//        getSupportFragmentManager().beginTransaction().replace(R.id.<YourFrameLayout>,new <YourSettingsFragmentClass>()).commit();
    }

}

class SettingFragment : PreferenceFragmentCompat() {

    private var folderController: FolderController? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
//        folderController = FolderController(this)

//        defaultClick("restore_pref", "Select a Restore Folder", FolderController.DIRECTORY_RESTORE)
//        defaultClick(
//            "backup_pref",
//            "Select a Backup/Dump Folder",
//            FolderController.DIRECTORY_BACKUP
//        )
//        checkFastWrite(this)
    }


    /* access modifiers changed from: private */
    fun chooseFolder(preference: Preference, str: String?, str2: String?) {
        val dialogProperties = DialogProperties()
        dialogProperties.selection_mode = 0
        dialogProperties.selection_type = 1
        dialogProperties.root = File(DialogConfigs.DIRECTORY_SEPERATOR)
        dialogProperties.error_dir = File(DialogConfigs.DEFAULT_DIR)
        dialogProperties.extensions = null
//        val filePickerDialog = FilePickerDialog(this, dialogProperties)
//        filePickerDialog.setTitle(str)
//        filePickerDialog.setDialogSelectionListener(object : DialogSelectionListener {
////            var folderController: FolderController =
////                FolderController(this.applicationContext)
//
//            override fun onSelectedFilePaths(strArr: Array<String?>) {
//                val str = if (strArr.size != 0) strArr[0] else DialogConfigs.DEFAULT_DIR
//                preference.summary = str
//                this.folderController.saveDirectory(str2!!, str!!)
//            }
//        })
//        filePickerDialog.show()
    }

//    private fun defaultClick(str: String, str2: String, str3: String) {
//        val findPreference = preferenceScreen.findPreference(str)
//        findPreference.setSummary(folderController!!.getDirectory(str3))
//        findPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
//            chooseFolder(findPreference, str2, str3)
//            true
//        }
//    }

    private fun checkFastWrite(context: Context) {
        (preferenceManager.findPreference("useFastWrite_pref") as CheckBoxPreference?)!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, obj ->
                val edit = context.getSharedPreferences(FolderController.SETTINGS_KEY, 0).edit()
                edit.putBoolean("useFastWrite_pref", (obj as Boolean))
                edit.commit()
                true
            }
    }

}