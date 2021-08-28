package dev.mlferreira.n2eliteunofficial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.github.angads25.filepicker.controller.DialogSelectionListener
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import dev.mlferreira.n2eliteunofficial.entity.Amiibo.Companion.DUMMY
import dev.mlferreira.n2eliteunofficial.util.ActionEnum
import java.io.File


class GridActivity : AppCompatActivity() {

    private lateinit var app: NFCApp
    private lateinit var confirmation: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.simpleName, "[onCreate] started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid)

        app = application as NFCApp
        confirmation = Intent(this, NFCTapActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

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
        if (app.banks[i].amiibo.characterIdHex == DUMMY) {
            Log.d(this::class.simpleName, "[activate] bank #$i is empty - skip to add new")
            write()
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
//
//        private boolean empty(int i) {
//            NFCApp nFCApp = (NFCApp) getApplication();
//            this.app.writeBank = (byte) i;
//            nFCApp.setStatus("Please tap and hold to empty bank!");
//            nFCApp.setAction(NFCApp.AppAction.ACTION_EMPTY);
//            setResult(-1, new Intent(this, MainActivity.class));
//            finish();
//            return true;
//        }
//
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

    private fun write() {


        // TODO: rever os write!!!
        val dialogProperties = DialogProperties()
        dialogProperties.selection_mode = 0;
        dialogProperties.selection_type = 0;
        dialogProperties.offset = File(app.folderController.getDirectory(FolderController.DIRECTORY_RESTORE)!!)
        dialogProperties.root = File(DialogConfigs.DIRECTORY_SEPERATOR)

        dialogProperties.error_dir = File(DialogConfigs.DEFAULT_DIR);
        dialogProperties.extensions = arrayOf("bin")

        val filePickerDialog = FilePickerDialog(this, dialogProperties);
        filePickerDialog.setTitle("Select a File");
        filePickerDialog.setDialogSelectionListener(DialogSelectionListener() {
            fun onSelectedFilePaths(strArr: Array<String>) {
                if (strArr.isNotEmpty()) {
                    app.writeFile = strArr[0]
                    app.currentAction = ActionEnum.WRITE

                    Log.d(this::class.simpleName, "[write] got filename: " + app.writeFile)

                    startActivity(confirmation)
                }
            }
        })
        filePickerDialog.show()
    }



}