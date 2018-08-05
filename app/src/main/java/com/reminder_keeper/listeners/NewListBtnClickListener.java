package com.reminder_keeper.listeners;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.adapters.AdapterB_Folders;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;
import com.reminder_keeper.views.DrawerLayoutView;
import com.reminder_keeper.R;
import com.reminder_keeper.views.SelectListView;

import java.util.ArrayList;

public class NewListBtnClickListener implements View.OnClickListener
{
    private Activity activity;
    private ListView groupsLV;
    private ArrayList<String> selectFolderArray;
    private CursorsDBMethods cursors;
    private String requestFrom;
    private EditText selectFolderET;
    private Button createListButton;
    private EditText editTextNewListName;
    private AlertDialog newListViewDialog;

    public NewListBtnClickListener(Activity activity, String requestFrom)
    {
        this.activity = activity;
        cursors = new CursorsDBMethods(activity);
        this.requestFrom = requestFrom;
    }

    @Override
    public void onClick(View view)
    {
        if (newListViewDialog == null)
        {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            newListViewDialog = alertDialogBuilder.create();
            final View newListView = LayoutInflater.from(activity).inflate(R.layout.dialog_new_list, null);
            newListViewDialog.setView(newListView);
            newListViewDialog.show();
            newListViewDialog.setOnDismissListener(onDismissNewListViewDialogListener);
            createListButton = (Button) newListView.findViewById(R.id.new_list_view_create_list_button);
            editTextNewListName = (EditText) newListView.findViewById(R.id.new_list_view_new_list_name_ET);
            selectFolderET = (EditText) newListView.findViewById(R.id.new_list_view_select_group_et);
            selectFolderET.setOnClickListener(onItemsClickListener);
            createListButton.setOnClickListener(onItemsClickListener);
        }
    }

    View.OnClickListener onItemsClickListener = new View.OnClickListener()
    { @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                //TODO: Select folder EditText from sideView click listener
                case R.id.new_list_view_select_group_et:
                    AlertDialog.Builder chooseFolderDialogBuilder = new AlertDialog.Builder(activity);
                    final AlertDialog chooseFolderDialog = chooseFolderDialogBuilder.create();
                    View selectFolderView = LayoutInflater.from(activity).inflate(R.layout.dialog_select_group, null, false);
                    groupsLV = (ListView) selectFolderView.findViewById(R.id.select_group_list_view);
                    chooseFolderDialog.setView(selectFolderView);
                    chooseFolderDialog.show();

                    loadGroupsSetAdapter();

                    //TODO: groupsLV item clicked
                    groupsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                        {
                            Toast.makeText(activity, selectFolderArray.get(i) + "", Toast.LENGTH_SHORT).show();
                            selectFolderET.setText(selectFolderArray.get(i));
                            chooseFolderDialog.dismiss();
                        }
                    });

                    Button newFolderButton = (Button) selectFolderView.findViewById(R.id.select_group_view_new_folder_button);
                    //TODO: NEW FOLDER Button clicked
                    newFolderButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view) {
                            View createFolderView = LayoutInflater.from(activity).inflate(R.layout.dialog_new_folder, null, false);
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                            final AlertDialog createFolderViewDialog = dialogBuilder.create();
                            createFolderViewDialog.setView(createFolderView);
                            createFolderViewDialog.show();

                            final EditText folderNameET = (EditText) createFolderView.findViewById(R.id.input_title_view_title_et);
                            Button createButton = (Button) createFolderView.findViewById(R.id.input_title_view_button);

                            //TODO: CREATE button clicked
                            createButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (folderNameET.getText().toString().trim().equals(""))
                                    {
                                        Toast.makeText(activity, R.string.empty_folder_name_msg, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, folderNameET.getText(), Toast.LENGTH_SHORT).show();
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(DBOpenHelper.COLUMN_GROUP, folderNameET.getText().toString());
                                        activity.getContentResolver().insert(DBProvider.GROUPS_TABLE_PATH_URI, contentValues);
                                        loadGroupsSetAdapter();
                                        createFolderViewDialog.dismiss();
                                    }

                                    //TODO: FOLDERS list item clicked
                                    groupsLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                    { @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                                    {
                                        final ArrayList<String> chooseFolderArray = new ArrayList<>();
                                        cursors.getCursorGroups();
                                        while (cursors.cursorGroups.moveToNext())
                                        {
                                            chooseFolderArray.add(cursors.cursorGroups.getString(cursors.cursorGroups.getColumnIndex(DBOpenHelper.COLUMN_GROUP)));
                                        }
                                        selectFolderET.setText(chooseFolderArray.get(i));
                                        chooseFolderDialog.dismiss();
                                    }
                                    });
                                }
                            });
                        }
                    });
                    break;

                //TODO: CREATE LIST Button Clicked
                case R.id.new_list_view_create_list_button:
                    ContentValues contentValues = new ContentValues();
                    if (editTextNewListName.getText().toString().trim().equals(""))
                    {
                        Toast.makeText(activity, R.string.empty_list_massage, Toast.LENGTH_SHORT).show();
                    } else if (selectFolderET.getText().toString().equals(activity.getString(R.string.select_folder))) {
                        contentValues.put(DBOpenHelper.COLUMN_LIST, editTextNewListName.getText().toString());
                        activity.getContentResolver().insert(DBProvider.GROUPS_TABLE_PATH_URI, contentValues);
                        Toast.makeText(activity, editTextNewListName.getText().toString(), Toast.LENGTH_LONG).show();
                        newListViewDialog.cancel();
                    } else {
                        contentValues.put(DBOpenHelper.COLUMN_CHILD, editTextNewListName.getText().toString());
                        contentValues.put(DBOpenHelper.COLUMN_GROUP, selectFolderET.getText().toString());
                        activity.getContentResolver().insert(DBProvider.CHILDREN_TABLE_PATH_URI, contentValues);
                        newListViewDialog.cancel();
                    }
                    break;
            }
        }
    };

    private void loadGroupsSetAdapter()
    {
        selectFolderArray = new ArrayList<>();
        cursors.getCursorGroups();
        while (cursors.cursorGroups.moveToNext())
        {
            String groupTitle = cursors.cursorGroups.getString(cursors.cursorGroups.getColumnIndex(DBOpenHelper.COLUMN_GROUP));
            if (groupTitle != null)
            {
                selectFolderArray.add(groupTitle);
            }
        }
        groupsLV.setAdapter(new AdapterB_Folders(activity, selectFolderArray));
    }

    //TODO: on new list dialog dismiss listener
    DialogInterface.OnDismissListener onDismissNewListViewDialogListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            Log.d("checkOnDismiss", "onDismiss()");
            newListViewDialog = null;
            if (requestFrom.equals(DrawerLayoutView.DRAWER_LAYOUT_VIEW)) {
                new DrawerLayoutView(activity).setDrawerAdapterERV();
            } else if (requestFrom.equals(MainActivity.MAIN_ACTIVITY)) {
                new SelectListView(activity, requestFrom).setAdapter();
                new DrawerLayoutView(activity).setDrawerAdapterERV();
            } else {
                new SelectListView(activity, requestFrom).setAdapter();
            }
        }
    };
}
