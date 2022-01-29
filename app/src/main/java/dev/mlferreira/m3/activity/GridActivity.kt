package dev.mlferreira.m3.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract.EXTRA_INITIAL_URI
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.MimeTypeFilter
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import com.github.angads25.filepicker.controller.DialogSelectionListener
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import dev.mlferreira.m3.ImageAdapter
import dev.mlferreira.m3.NFCApp
import dev.mlferreira.m3.R
import dev.mlferreira.m3.entity.Amiibo.Companion.DUMMY
import dev.mlferreira.m3.rest.FolderController
import dev.mlferreira.m3.util.ActionEnum
import java.io.File


class GridActivity : AppCompatActivity() {

    private lateinit var app: NFCApp
    private lateinit var confirmation: Intent
    private lateinit var readForBank: ActivityResultLauncher<Array<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "[onCreate] started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid)

        app = application as NFCApp
        confirmation = Intent(this, NFCTapActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        readForBank = registerForActivityResult(openFile(getString(R.string.key_restore_folder))) { uri: Uri ->
            Log.d(this::class.simpleName, "[readForBank] uri $uri")

            Log.d(this::class.simpleName, "[readForBank] uri type: ${contentResolver.getType(uri)}")

            app.writeFile = uri.encodedPath
            app.currentAction = ActionEnum.WRITE

            Log.d(this::class.simpleName, "[readForBank] got filename: " + app.writeFile)

            startActivity(confirmation)

        }


        val gridView: GridView = findViewById(R.id.gridview)

        gridView.adapter = ImageAdapter(
            this,
            app.currentBank,
            app.bankCount,
            app.banks
        ) as ListAdapter

        gridView.onItemClickListener = AdapterView
            .OnItemClickListener { adapterView, view, i, j ->  this.select(i) }

        registerForContextMenu(gridView)
    }

    private fun select(i: Int) {
        Log.d(this::class.simpleName, "[activate] started")

        app.writeBank = i

        // empty bank - add new amiibo
        if (app.banks[i].amiibo.id.equals(DUMMY, ignoreCase = true)) {
            Log.d(this::class.simpleName, "[activate] bank #$i is empty - skip to add new")
//            writeToBank()
            readForBank.launch(arrayOf("*/*"))
            return
        }

        // set bank as active
        Log.d(this::class.simpleName, "[activate] will activate #$i")
        app.currentAction = ActionEnum.ACTIVATE
        startActivity(confirmation)

    }

//        private boolean backup(int i) {
//            NFCApp nFCApp = (NFCApp) getApplication();
//            this.app.writeBank = (byte) i;
//            nFCApp.setStatus("Please tap and hold to backup bank!");
//            nFCApp.setAction(NFCApp.AppAction.ACTION_BACKUP);
//            setResult(-1, new Intent(this, MainActivity.class));
//            finish();
//            return true;
//        }
//
//        private boolean restore(int i) {
//            this.app.writeBank = (byte) i;
//            restoreFileChooser();
//            return true;
//        }

        private fun empty(bank: Int) {
            app.currentAction = ActionEnum.EMPTY
            app.writeBank = bank
            startActivity(confirmation)
        }

//        private boolean cheat(int i) {
//            NFCApp nFCApp = (NFCApp) getApplication();
//            this.app.writeBank = (byte) i;
//            nFCApp.setStatus("Please tap and hold to upload bank!");
//            nFCApp.setAction(NFCApp.AppAction.ACTION_CHEAT_START);
//            setResult(-1, new Intent(this, MainActivity.class));
//            finish();
//            return true;
//        }
//
//        private boolean randomize(int i) {
//            NFCApp nFCApp = (NFCApp) getApplication();
//            this.app.writeBank = (byte) i;
//            nFCApp.setStatus("Please tap and hold to upload bank!");
//            nFCApp.setAction(NFCApp.AppAction.ACTION_RANDMOIZE_SERIAL);
//            setResult(-1, new Intent(this, MainActivity.class));
//            finish();
//            return true;
//        }
//
//        private boolean information(int i) {
//            new InformationActivity(((NFCApp) getApplication()).getBank(i), this, this);
//            return false;
//        }
//
//        public boolean onContextItemSelected(MenuItem menuItem) {
//            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
//            switch (menuItem.getItemId()) {
//                case R.id.action_activate /*2131165191*/:
//                return activate(adapterContextMenuInfo.position);
//                case R.id.action_backup /*2131165192*/:
//                return backup(adapterContextMenuInfo.position);
//                case R.id.action_cheat /*2131165200*/:
//                return cheat(adapterContextMenuInfo.position);
//                case R.id.action_empty /*2131165204*/:
//                return empty(adapterContextMenuInfo.position);
//                case R.id.action_information /*2131165206*/:
//                return information(adapterContextMenuInfo.position);
//                case R.id.action_randomize /*2131165212*/:
//                return randomize(adapterContextMenuInfo.position);
//                case R.id.action_restore /*2131165213*/:
//                return restore(adapterContextMenuInfo.position);
//                default:
//                return super.onContextItemSelected(menuItem);
//            }
//        }
//
//        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//            super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
//            if (!((NFCApp) getApplication()).getBank(((AdapterView.AdapterContextMenuInfo) contextMenuInfo).position).getAmiibo().statueId().toLowerCase().equals("ffffffffffffffff")) {
//                getMenuInflater().inflate(R.menu.menu_grid_item, contextMenu);
//            }
//        }
//
//        public boolean onKeyDown(int i, KeyEvent keyEvent) {
//            if (i == 4) {
//                finish();
//            }
//            return super.onKeyDown(i, keyEvent);
//        }


    private fun openFile(key: String) = object : ActivityResultContracts.OpenDocument() {

        override fun createIntent(context: Context, input: Array<String>): Intent {
            Log.d(this::class.simpleName, "[openFile] [createIntent] STARTED - $key")
            val startingUriString = FolderController(context).getDirectory(key)
            val startingUri = DocumentFile.fromTreeUri(context, Uri.parse(startingUriString))
            return super.createIntent(context, input).putExtra(EXTRA_INITIAL_URI, startingUri?.uri)
        }


    }

}