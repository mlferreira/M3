package dev.mlferreira.n2eliteunofficial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import dev.mlferreira.n2eliteunofficial.entity.Amiibo.Companion.DUMMY
import dev.mlferreira.n2eliteunofficial.util.ActionEnum


class GridActivity : AppCompatActivity() {

    var app: NFCApp? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid)

        app = application as NFCApp

        val gridView: GridView = findViewById(R.id.gridview)

        gridView.adapter = ImageAdapter(
            this,
            app!!.currentBank,
            app!!.bankCount,
            app!!.banks
        ) as ListAdapter

        gridView.onItemClickListener = AdapterView
            .OnItemClickListener { adapterView, view, i, j ->  this.activate(i) }

        registerForContextMenu(gridView)
    }

    private fun activate(i: Int): Boolean {
        val nFCApp: NFCApp = application as NFCApp

        if (nFCApp.banks[i].amiibo.characterIdHex != DUMMY) {
            nFCApp.writeBank = i.toByte()
//            nFCApp.setStatus("Please tap and hold to activate bank!");
            nFCApp.currentAction = ActionEnum.ACTION_ACTIVATE


            setResult(-1, Intent(this, MenuActivity::class.java))

            finish()
            return true
        }

//        restore(i)
        return false
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
//
//        private void restoreFileChooser() {
//            FolderController folderController = new FolderController(this);
//            DialogProperties dialogProperties = new DialogProperties();
//            dialogProperties.selection_mode = 0;
//            dialogProperties.selection_type = 0;
//            dialogProperties.offset = new File(folderController.getDirectory(FolderController.DIRECTORY_RESTORE));
//            dialogProperties.root = new File(DialogConfigs.DIRECTORY_SEPERATOR);
//            dialogProperties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
//            dialogProperties.extensions = new String[]{"bin"};
//            FilePickerDialog filePickerDialog = new FilePickerDialog(this, dialogProperties);
//            filePickerDialog.setTitle("Select a File");
//            filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
//                FolderController folderController = new FolderController(GridActivity.this.getApplicationContext());
//
//                public void onSelectedFilePaths(String[] strArr) {
//                    if (strArr.length != 0) {
//                        NFCApp nFCApp = (NFCApp) GridActivity.this.getApplication();
//                        nFCApp.writeFile = strArr[0].toString();
//                        String access$100 = GridActivity.this.log_name;
//                        Log.d(access$100, "got filename: " + nFCApp.writeFile);
//                        nFCApp.setStatus("Please tap and hold to write bank!");
//                        nFCApp.setAction(NFCApp.AppAction.ACTION_WRITE);
//                        GridActivity.this.setResult(-1, new Intent(GridActivity.this, MainActivity.class));
//                        GridActivity.this.finish();
//                        return;
//                    }
//                    Toast.makeText(GridActivity.this, "Please select a file", 1).show();
//                }
//            });
//            filePickerDialog.show();
//        }
//    }



}