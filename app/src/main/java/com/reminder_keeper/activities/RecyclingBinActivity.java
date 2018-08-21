package com.reminder_keeper.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;

import com.reminder_keeper.views.ToolbarView;
import com.reminder_keeper.adapters.AdaptersRV.AdapterRB_RV;
import com.reminder_keeper.AuthorityClass;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;
import com.reminder_keeper.R;

public class RecyclingBinActivity extends AppCompatActivity
{
    public static final String RECYCLING_BIN_ACTIVITY = "RecyclingBinActivity";
    private CursorsDBMethods cursors;
    private AdapterRB_RV adapterRBRV;
    private RecyclerView recyclerView;
    private ContentValues contentValues;
    public static boolean isListItemSelected;
    private String dialogQuestion;
    private static ArrayList<Integer> selectedListIds;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycing_bin);

        isListItemSelected = false;
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_custom));
        new ToolbarView(this, getSupportActionBar(), RECYCLING_BIN_ACTIVITY);
        selectedListIds = new ArrayList<>();
        cursors = new CursorsDBMethods(this);
        cursors.getCursorRecyclingBin();
        cursor = CursorsDBMethods.cursorRecyclingBin;
        setAdaptersAndLayouts();
    }

    //TODO: set adapters and layouts
    public void setAdaptersAndLayouts()
    {
        adapterRBRV = new AdapterRB_RV(this, cursor, null, RECYCLING_BIN_ACTIVITY);
        recyclerView = (RecyclerView) findViewById(R.id.activity_recycling_bin_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterRBRV);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_layer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (isListItemSelected)
        {
            menu.findItem(R.id.menuItemRemoveSelected).setVisible(true);
            menu.findItem(R.id.menuItemRestoreSelected).setVisible(true);
            menu.findItem(R.id.menuItemUnSelect).setVisible(true);
            menu.findItem(R.id.menuItemRestoreAll).setVisible(false);
            menu.findItem(R.id.menuItemRemoveAll).setVisible(false);
        } else if (selectedListIds.size() == 0)
        {
            menu.findItem(R.id.menuItemRestoreAll).setVisible(true);
            menu.findItem(R.id.menuItemRemoveAll).setVisible(true);
            menu.findItem(R.id.menuItemUnSelect).setVisible(false);
            menu.findItem(R.id.menuItemRemoveSelected).setVisible(false);
            menu.findItem(R.id.menuItemRestoreSelected).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                setResult(AuthorityClass.RESULT_INIT_ADAPTERS);
                finish();
                break;
            case R.id.menuItemRestoreAll:
                if (cursor.getCount() > 0)
                {
                    dialogQuestion = getString(R.string.restore_all_reminders_msg);
                    moveRemindersToDBToDo();
                }
                break;
            case R.id.menuItemRemoveAll:
                if (cursor.getCount() > 0)
                {
                    dialogQuestion = getString(R.string.delete_all_reminders_permanently);
                    deleteData();
                }
                break;
            case R.id.menuItemRestoreSelected:
                dialogQuestion = getString(R.string.restore_selected_reminders_msg);
                moveRemindersToDBToDo();
                break;
            case R.id.menuItemRemoveSelected:
                dialogQuestion = getString(R.string.delete_selected_reminders_msg);
                deleteData();
                break;
            case R.id.menuItemUnSelect:
                refreshAdapter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: restore reminders (selected / all)
    private void moveRemindersToDBToDo()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.setTitle(dialogQuestion);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.restore), new DialogInterface.OnClickListener()
        {   @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                contentValues = new ContentValues();
                if (selectedListIds.size() > 0)
                {
                    cursor = getContentResolver().query(DBProvider.RECYCLING_BIN_TABLE_PATH_URI, null, AuthorityClass.setStringIdsForDB(selectedListIds), null, null);
                    while (cursor.moveToNext()) { setContentValuesInsertToDB(); }
                    getContentResolver().delete(DBProvider.RECYCLING_BIN_TABLE_PATH_URI, AuthorityClass.setStringIdsForDB(selectedListIds), null);
                } else {
                    cursors.getCursorRecyclingBin();
                    cursor = CursorsDBMethods.cursorRecyclingBin;
                    while (cursor.moveToNext()) { setContentValuesInsertToDB(); }
                    deleteAllData();
                }
                isListItemSelected = false;
                refreshAdapter();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialog.dismiss();
                isListItemSelected = false;
                refreshAdapter();
            }
        });
        dialog.show();
    }

    //TODO: delete reminders (selected / all)
    private void deleteData()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.setTitle(dialogQuestion);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.delete), new DialogInterface.OnClickListener()
        {   @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (selectedListIds.size() > 0)
                {
                    getContentResolver().delete(DBProvider.RECYCLING_BIN_TABLE_PATH_URI, AuthorityClass.setStringIdsForDB(selectedListIds), null);
                } else {
                    deleteAllData();
                }
                isListItemSelected = false;
                refreshAdapter();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialog.dismiss();
                isListItemSelected = false;
                refreshAdapter();
            }
        });
        dialog.show();
    }

    private void setContentValuesInsertToDB()
    {
        boolean childIsHasEquals = false;
        boolean listIsHasEquals = false;
        Cursor cursorFullTable;

        String child = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CHILD));
        if (child != null)
        {
            cursorFullTable = getContentResolver().query(DBProvider.CHILDREN_TABLE_PATH_URI, new String[]{ DBOpenHelper.COLUMN_CHILD}, null, null, null);
            while (cursorFullTable.moveToNext())
            {
                String childTemp = cursorFullTable.getString(cursorFullTable.getColumnIndex(DBOpenHelper.COLUMN_CHILD));
                if (childTemp != null && childTemp.equals(child))
                {
                    contentValues.put(DBOpenHelper.COLUMN_CHILD, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CHILD)));
                    childIsHasEquals = true;
                    break;
                }
            }
        }

        String list = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_LIST));
        if (list != null)
        {
            cursorFullTable = getContentResolver().query(DBProvider.GROUPS_TABLE_PATH_URI, new String[]{ DBOpenHelper.COLUMN_LIST}, null, null, null);
            while (cursorFullTable.moveToNext())
            {
                String listTemp = cursorFullTable.getString(cursorFullTable.getColumnIndex(DBOpenHelper.COLUMN_LIST));
                if (listTemp != null && listTemp.equals(list))
                {
                    contentValues.put(DBOpenHelper.COLUMN_CHILD, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CHILD)));
                    listIsHasEquals = true;
                    break;
                }
            }
        }

        if (childIsHasEquals || listIsHasEquals)
        {
            if (childIsHasEquals)
            {
                contentValues.put(DBOpenHelper.COLUMN_CHILD, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CHILD)));
            } else if (listIsHasEquals){
                contentValues.put(DBOpenHelper.COLUMN_LIST, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_LIST)));
            }
        } else {
            contentValues.put(DBOpenHelper.COLUMN_LIST, AuthorityClass.UNCLASSIFIED);
        }

        contentValues.put(DBOpenHelper.COLUMN_GROUP, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_GROUP)));
        contentValues.put(DBOpenHelper.COLUMN_REMINDER, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER)));
        getContentResolver().insert(DBProvider.TODO_TABLE_PATH_URI, contentValues);
    }

    //TODO: refresh adapter
    private void refreshAdapter()
    {
        selectedListIds.clear();
        isListItemSelected = false;
        adapterRBRV.reBindCursor(cursors.getCursorRecyclingBin());
        recyclerView.setAdapter(adapterRBRV);
    }

    //TODO: delete all reminder
    private void deleteAllData()
    {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(RecyclingBinActivity.this);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + DBOpenHelper.RECYCLING_BIN_TABLE);
        sqLiteDatabase.close();
    }

    //TODO: on item click - listen for clicked items ids
    public void onItemClicked(int itemId)
    {
        if (!selectedListIds.contains(itemId))
        {
            selectedListIds.add(itemId);
        } else {
            selectedListIds.remove(selectedListIds.indexOf(itemId));
        }
    }
}
