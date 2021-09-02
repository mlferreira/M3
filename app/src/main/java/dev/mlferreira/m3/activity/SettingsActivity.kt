package dev.mlferreira.m3.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.mlferreira.m3.R


class SettingsActivity : AppCompatActivity() {

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

    private lateinit var restoreFolder: ActivityResultLauncher<Uri>
    private lateinit var backupFolder: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "[onCreate] started")
        super.onCreate(savedInstanceState)

        restoreFolder = registerFolderActivity(getString(R.string.key_restore_folder))
        backupFolder = registerFolderActivity(getString(R.string.key_backup_folder))

    }

    private fun registerFolderActivity(key: String) = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri ->
        Log.d(this::class.simpleName, "[registerFolderActivity] key $key")
        Log.d(this::class.simpleName, "[registerFolderActivity] uri $uri")
        Log.d(this::class.simpleName, "[registerFolderActivity] uri.path ${uri.path}")
        Log.d(this::class.simpleName, "[registerFolderActivity] uri.encodedPath ${uri.encodedPath}")

        preferenceManager.sharedPreferences.edit().putString(key, uri.toString()).apply()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(this::class.simpleName, "[onCreatePreferences] started")
        setPreferencesFromResource(R.xml.preferences, rootKey)

        checkFolder(getString(R.string.key_restore_folder))
        checkFolder(getString(R.string.key_backup_folder))

        checkDarkMode()
    }

    private fun checkFolder(key: String) {
        Log.d(this::class.simpleName, "[checkFolder] started")
        val preference: Preference? = findPreference(key)

        preference?.onPreferenceClickListener = Preference
            .OnPreferenceClickListener {
                Log.d(this::class.simpleName, "[checkFolder] OnPreferenceClickListener")

                if(key == getString(R.string.key_restore_folder)) {
                    restoreFolder.launch(null)
                } else if(key == getString(R.string.key_backup_folder)) {
                    backupFolder.launch(null)
                } else {
                    return@OnPreferenceClickListener false
                }

                true
            }

    }

    private fun checkDarkMode() {
        Log.d(this::class.simpleName, "[checkDarkMode] started")
        val key: String = getString(R.string.key_dark_mode)
        val checkBoxPreference = findPreference(key) as CheckBoxPreference?

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