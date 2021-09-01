package dev.mlferreira.m3.activity

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import dev.mlferreira.m3.R
import dev.mlferreira.m3.rest.FolderController
import java.io.File


class SettingsActivity : AppCompatActivity() {

//    private var folderController: FolderController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "[onCreate] started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setTitle(R.string.title_settings)

        if (findViewById<FrameLayout>(R.id.settings_frame_layout) != null) {
            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager
                .beginTransaction()
                .add(R.id.settings_frame_layout, SettingsFragment())
                .commit()
        }

    }

}



class SettingsFragment : PreferenceFragmentCompat() {

//    private var folderController: FolderController? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(this::class.simpleName, "[onCreatePreferences] started")
        setPreferencesFromResource(R.xml.preferences, rootKey)
//        folderController = FolderController(this)

//        defaultClick("restore_pref", "Select a Restore Folder", FolderController.DIRECTORY_RESTORE)
//        defaultClick(
//            "backup_pref",
//            "Select a Backup/Dump Folder",
//            FolderController.DIRECTORY_BACKUP
//        )

        checkDarkMode()
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

    private fun checkDarkMode() {
        val key: String = getString(R.string.key_dark_mode)
        val checkBoxPreference = preferenceManager.findPreference(key) as CheckBoxPreference?

        if (checkBoxPreference != null) {
            checkBoxPreference.onPreferenceChangeListener = Preference
                .OnPreferenceChangeListener { preference, obj ->
                    obj as Boolean
                    if (obj) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    requireActivity().recreate()
                    true
                }
        }
    }

}