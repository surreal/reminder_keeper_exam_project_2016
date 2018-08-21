package com.reminder_keeper.data_base;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class DBProvider extends ContentProvider
{
    private static final String PROVIDER_CLASS_LINK = "com.reminder_keeper.data_base.DBProvider";

    //TODO: ToDo Table
    private static final String TODO_TABLE_BASE_PATH = "todoTableBasePath";
    public static final Uri TODO_TABLE_PATH_URI = Uri.parse("content://" + PROVIDER_CLASS_LINK + "/" + TODO_TABLE_BASE_PATH);

    //TODO: Checked Table
    private static final String CHECKED_TABLE_BASE_PATH = "checkedTableBasePath";
    public static final Uri CHECKED_TABLE_PATH_URI = Uri.parse("content://" + PROVIDER_CLASS_LINK + "/" + CHECKED_TABLE_BASE_PATH);

    //TODO: Groups Table
    private static final  String GROUPS_TABLE_BASE_PATH = "groupsTableBasePath";
    public static final Uri GROUPS_TABLE_PATH_URI = Uri.parse("content://" + PROVIDER_CLASS_LINK + "/" + GROUPS_TABLE_BASE_PATH);

    //TODO: CHILDREN Table
    private static final String CHILDREN_TABLE_BASE_PATH = "childrenTableBasePath";
    public static final Uri CHILDREN_TABLE_PATH_URI = Uri.parse("content://" + PROVIDER_CLASS_LINK + "/" + CHILDREN_TABLE_BASE_PATH);

    //TODO: Children Table
    private static final String RECYCLING_BIN_TABLE_BASE_PATH = "RecyclingBinTableBasePath";
    public static final Uri RECYCLING_BIN_TABLE_PATH_URI = Uri.parse("content://" + PROVIDER_CLASS_LINK + "/" + RECYCLING_BIN_TABLE_BASE_PATH);

    //TODO: CONSTANTS numbers to get wright Table
    private static final int TODO_TABLE_MATCHER_ID = 1;
    private static final int CHECKED_TABLE_MATCHER_ID = 2;
    private static final int GROUPS_TABLE_MATCHER_ID = 3;
    private static final int CHILDREN_TABLE_MATCHER_ID = 4;
    private static final int RECYCLING_BIN_TABLE_MATCHER_ID = 5;

    private static SQLiteDatabase sqLiteDatabase;
    private static UriMatcher uriMatcher = matchUri();

    private static UriMatcher matchUri()
    {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(PROVIDER_CLASS_LINK, TODO_TABLE_BASE_PATH, TODO_TABLE_MATCHER_ID);
        matcher.addURI(PROVIDER_CLASS_LINK, CHECKED_TABLE_BASE_PATH, CHECKED_TABLE_MATCHER_ID);
        matcher.addURI(PROVIDER_CLASS_LINK, GROUPS_TABLE_BASE_PATH, GROUPS_TABLE_MATCHER_ID);
        matcher.addURI(PROVIDER_CLASS_LINK, CHILDREN_TABLE_BASE_PATH, CHILDREN_TABLE_MATCHER_ID);
        matcher.addURI(PROVIDER_CLASS_LINK, RECYCLING_BIN_TABLE_BASE_PATH, RECYCLING_BIN_TABLE_MATCHER_ID);
        return matcher;
    }

    @Override
    public boolean onCreate()
    {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getContext());
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        Cursor retCursor;
        switch (uriMatcher.match(uri))
        {
            case TODO_TABLE_MATCHER_ID:
                retCursor = sqLiteDatabase.query
                        (
                                DBOpenHelper.TODO_TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                DBOpenHelper.COLUMN_ID + " DESC"
                        );
                break;
            case CHECKED_TABLE_MATCHER_ID:
                retCursor = sqLiteDatabase.query
                        (
                                DBOpenHelper.CHECKED_TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                DBOpenHelper.COLUMN_ID + " DESC"
                        );
                break;
            case GROUPS_TABLE_MATCHER_ID:
                retCursor = sqLiteDatabase.query
                        (
                                DBOpenHelper.GROUPS_TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                DBOpenHelper.COLUMN_ID + " DESC"
                        );
                break;
            case CHILDREN_TABLE_MATCHER_ID:
                retCursor = sqLiteDatabase.query
                        (
                                DBOpenHelper.CHILDREN_TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                DBOpenHelper.COLUMN_ID + " DESC"
                        );
                break;
            case RECYCLING_BIN_TABLE_MATCHER_ID:
                retCursor = sqLiteDatabase.query
                        (
                                DBOpenHelper.RECYCLING_BIN_TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                DBOpenHelper.COLUMN_ID + " DESC"
                        );
                break;
            default: throw new UnsupportedOperationException("unknown uri " + uri);
        }

        assert getContext() != null;
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        switch (uriMatcher.match(uri))
        {
            case TODO_TABLE_MATCHER_ID:
                long newToDoId = sqLiteDatabase.insert(DBOpenHelper.TODO_TABLE, null, contentValues);
                return Uri.parse(TODO_TABLE_BASE_PATH + "/" + newToDoId);
            case CHECKED_TABLE_MATCHER_ID:
                long newCheckedId = sqLiteDatabase.insert(DBOpenHelper.CHECKED_TABLE, null, contentValues);
                return Uri.parse(CHECKED_TABLE_BASE_PATH + "/" + newCheckedId);
            case GROUPS_TABLE_MATCHER_ID:
                long newGroupId = sqLiteDatabase.insert(DBOpenHelper.GROUPS_TABLE, null, contentValues);
                return Uri.parse(GROUPS_TABLE_BASE_PATH + "/" + newGroupId);
            case CHILDREN_TABLE_MATCHER_ID:
                long newChildId = sqLiteDatabase.insert(DBOpenHelper.CHILDREN_TABLE, null, contentValues);
                return Uri.parse(CHILDREN_TABLE_BASE_PATH + "/" + newChildId);
            case RECYCLING_BIN_TABLE_MATCHER_ID:
                long newRecyclingBinId = sqLiteDatabase.insert(DBOpenHelper.RECYCLING_BIN_TABLE, null, contentValues);
                return Uri.parse(RECYCLING_BIN_TABLE_BASE_PATH + "/" + newRecyclingBinId);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        switch (uriMatcher.match(uri))
        {
            case TODO_TABLE_MATCHER_ID:
                return sqLiteDatabase.delete(DBOpenHelper.TODO_TABLE, selection, selectionArgs);
            case CHECKED_TABLE_MATCHER_ID:
                return sqLiteDatabase.delete(DBOpenHelper.CHECKED_TABLE, selection, selectionArgs);
            case GROUPS_TABLE_MATCHER_ID:
                return sqLiteDatabase.delete(DBOpenHelper.GROUPS_TABLE, selection, selectionArgs);
            case CHILDREN_TABLE_MATCHER_ID:
                return sqLiteDatabase.delete(DBOpenHelper.CHILDREN_TABLE, selection, selectionArgs);
            case RECYCLING_BIN_TABLE_MATCHER_ID:
                return sqLiteDatabase.delete(DBOpenHelper.RECYCLING_BIN_TABLE, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String where, @Nullable String[] args)
    {
        switch (uriMatcher.match(uri))
        {
            case TODO_TABLE_MATCHER_ID:
                return sqLiteDatabase.update(DBOpenHelper.TODO_TABLE, contentValues, where, args);
            case CHECKED_TABLE_MATCHER_ID:
                return sqLiteDatabase.update(DBOpenHelper.CHECKED_TABLE, contentValues, where, args);
            case GROUPS_TABLE_MATCHER_ID:
                return sqLiteDatabase.update(DBOpenHelper.GROUPS_TABLE, contentValues, where, args);
            case CHILDREN_TABLE_MATCHER_ID:
                return sqLiteDatabase.update(DBOpenHelper.CHILDREN_TABLE, contentValues, where, args);
            case RECYCLING_BIN_TABLE_MATCHER_ID:
                return sqLiteDatabase.update(DBOpenHelper.RECYCLING_BIN_TABLE, contentValues, where, args);
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Nullable @Override public String getType(@NonNull Uri uri) { throw new UnsupportedOperationException("Uri:" + uri); }
}
