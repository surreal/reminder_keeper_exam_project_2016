package com.reminder_keeper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;

import static com.reminder_keeper.AuthorityClass.expandedGroupNameDL;
import static com.reminder_keeper.AuthorityClass.expandedGroupNameSLV;
import static com.reminder_keeper.AuthorityClass.selectedChildTitleDL;
import static com.reminder_keeper.AuthorityClass.selectedChildNameSLV;
import static com.reminder_keeper.AuthorityClass.selectedListTitleDL;
import static com.reminder_keeper.AuthorityClass.selectedListSLV;

public class CursorsDBMethods
{
    public static Cursor cursor, cursorGroups, cursorGroupsLists, cursorChildren, cursorRecyclingBin;
    private static Activity activity;
    private ContentValues contentValues;

    public CursorsDBMethods() {}
    public CursorsDBMethods(Activity activity) { this.activity = activity; }

    //TODO: getCursors methods...

    public Cursor getCursorToDo()
    {
        cursor = activity.getContentResolver().query(DBProvider.TODO_TABLE_PATH_URI, null, null, null, null);
        return cursor;
    }
    public Cursor getCursorToDo(Context context)
    {
        cursor = context.getContentResolver().query(DBProvider.TODO_TABLE_PATH_URI, null, null, null, null);
        return cursor;
    }

    public Cursor getCursorChecked()
    {
        cursor = activity.getContentResolver().query(DBProvider.CHECKED_TABLE_PATH_URI, null, null, null, null);
        return cursor;
    }

    public Cursor getCursorChildren()
    {
        cursorChildren = activity.getContentResolver().query(DBProvider.CHILDREN_TABLE_PATH_URI, null, null, null, null);
        return cursorChildren;
    }
    public Cursor getCursorChildren(int selectedId)
    {
        String where = DBOpenHelper.COLUMN_ID + "=" + selectedId;
        cursorChildren = activity.getContentResolver().query(DBProvider.CHILDREN_TABLE_PATH_URI, null, where, null, null);
        return cursorChildren;
    }

    public Cursor getCursorGroupsLists()
    {
        cursorGroupsLists = activity.getContentResolver().query(DBProvider.GROUPS_TABLE_PATH_URI, null,null,null,null);
        return cursorGroupsLists;
    }
    public Cursor getCursorGroupsLists(int selectedId)
    {
        String where = DBOpenHelper.COLUMN_ID + "=" + selectedId;
        cursorGroupsLists = activity.getContentResolver().query(DBProvider.GROUPS_TABLE_PATH_URI, null, where,null,null);
        return cursorGroupsLists;
    }

    public Cursor getCursorGroups()
    {
        cursorGroups = activity.getContentResolver().query(DBProvider.GROUPS_TABLE_PATH_URI, new String[]{DBOpenHelper.COLUMN_GROUP},null,null,null);
        return cursorGroups;
    }

    public Cursor getCursorRecyclingBin()
    {
        cursorRecyclingBin = activity.getContentResolver().query(DBProvider.RECYCLING_BIN_TABLE_PATH_URI, null, null, null, null);
        return cursorRecyclingBin;
    }

    //TODO: move to relevant DB if checked or swiped on MainActivity
    public void moveToDB(int idToDo, int idChecked, boolean isCheckedUnchecked)
    {
        String where = idToDo != -1 ? DBOpenHelper.COLUMN_ID + "=" + idToDo : DBOpenHelper.COLUMN_ID + "=" + idChecked;
        Uri uri = idToDo != -1 ? DBProvider.TODO_TABLE_PATH_URI : DBProvider.CHECKED_TABLE_PATH_URI;
        cursor = activity.getContentResolver().query(uri, null, where, null, null);
        contentValues = new ContentValues();
        cursor.moveToFirst();
        contentValues.put(DBOpenHelper.COLUMN_REMINDER, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER)));
        if (isCheckedUnchecked)
        {
            String groupTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_GROUP));
            String childTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CHILD));
            String listTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_LIST));
            if (groupTitle != null) {
                contentValues.put(DBOpenHelper.COLUMN_GROUP, groupTitle);
                contentValues.put(DBOpenHelper.COLUMN_CHILD, childTitle);
            } else {
                contentValues.put(DBOpenHelper.COLUMN_LIST, listTitle); }
                if (idToDo != -1) { contentValues.put(DBOpenHelper.COLUMN_DATE_TIME, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME))); }
            uri = idToDo != -1 ? DBProvider.CHECKED_TABLE_PATH_URI : DBProvider.TODO_TABLE_PATH_URI;
            activity.getContentResolver().insert(uri, contentValues);
            removeFromDB(idToDo, idChecked);
        } else {
            selectedListSLV = selectedListSLV != null && selectedListSLV.equals(activity.getString(R.string.unclassified)) ? AuthorityClass.UNCLASSIFIED : selectedListSLV;
            contentValues.put(DBOpenHelper.COLUMN_GROUP, expandedGroupNameSLV);
            contentValues.put(DBOpenHelper.COLUMN_CHILD, selectedChildNameSLV);
            contentValues.put(DBOpenHelper.COLUMN_LIST, selectedListSLV);
            contentValues.put(DBOpenHelper.COLUMN_DATE_TIME, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME)));
            activity.getContentResolver().update(uri, contentValues, where, null);
        }
    }

    public void removeFromDB(int idToDo, int idChecked)
    {
        String where = idToDo != -1 ? DBOpenHelper.COLUMN_ID + "=" + idToDo : DBOpenHelper.COLUMN_ID + "=" + idChecked;
        Uri uri = idToDo != -1 ? DBProvider.TODO_TABLE_PATH_URI : DBProvider.CHECKED_TABLE_PATH_URI;
        activity.getContentResolver().delete(uri, where, null);
    }

    public String setWhere(int selectedIdToDo, int selectedIdChecked)
    {
        String where = selectedIdToDo != -1 ? DBOpenHelper.COLUMN_ID + "=" + selectedIdToDo : DBOpenHelper.COLUMN_ID + "=" + selectedIdChecked;
        return where;
    }

    public Uri setUri(int selectedIdToDo)
    {
        Uri uri = selectedIdToDo != -1 ? DBProvider.TODO_TABLE_PATH_URI : DBProvider.CHECKED_TABLE_PATH_URI;
        return uri;
    }

    public void moveToRecyclingBin(String group, String child, String list, String reminderText, String timeDate)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.COLUMN_LIST, list);
        contentValues.put(DBOpenHelper.COLUMN_GROUP, group);
        contentValues.put(DBOpenHelper.COLUMN_CHILD, child);
        contentValues.put(DBOpenHelper.COLUMN_REMINDER, reminderText);
        contentValues.put(DBOpenHelper.COLUMN_DATE_TIME, timeDate);
        activity.getContentResolver().insert(DBProvider.RECYCLING_BIN_TABLE_PATH_URI, contentValues);
    }
}
