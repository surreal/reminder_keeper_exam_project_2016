package com.reminder_keeper.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME_FILE = "remindersDB.db";
    private static final int DB_VERSION = 1;

    public static final String TODO_TABLE = "todoTable";
    public static final String CHECKED_TABLE = "checkedTable";
    public static final String GROUPS_TABLE = "groupsTable";
    public static final String CHILDREN_TABLE = "childrenTable";
    public static final String RECYCLING_BIN_TABLE = "RecyclingBinTable";
    public static final String SEARCH_TABLE = "SearchTable";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_REMINDER = "reminder";
    public static final String COLUMN_DATE_TIME = "dateTime";
    public static final String COLUMN_GROUP = "groupName";
    public static final String COLUMN_LIST = "listName";
    public static final String COLUMN_CHILD = "childrenName";
    public static final String COLUMN_SEARCH_KEY = "searchKeyWord";
    public static final String COLUMN_REPEAT_OPTION = "repeatAction";
    public static final String COLUMN_REPEAT_CUSTOM_DAYS_OR_DATE = "repeatCustomDays";

    public DBOpenHelper(Context context)
    {
        super(context, DB_NAME_FILE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL
            ("CREATE TABLE " + TODO_TABLE +
                " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_REMINDER + " TEXT, " +
                    COLUMN_GROUP + " TEXT, " +
                    COLUMN_CHILD + " TEXT, " +
                    COLUMN_LIST + " TEXT, " +
                    COLUMN_DATE_TIME + " TEXT, " +
                    COLUMN_REPEAT_OPTION + " TEXT, " +
                    COLUMN_REPEAT_CUSTOM_DAYS_OR_DATE + " TEXT" +
                    ");"
            );

        sqLiteDatabase.execSQL
                ("CREATE TABLE " + CHECKED_TABLE +
                        " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_REMINDER + " TEXT, " +
                        COLUMN_DATE_TIME + " TEXT, " +
                        COLUMN_GROUP + " TEXT, " +
                        COLUMN_CHILD + " TEXT, " +
                        COLUMN_LIST + " TEXT" +
                        ");"
                );

        sqLiteDatabase.execSQL
                ("CREATE TABLE " + RECYCLING_BIN_TABLE +
                        " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_REMINDER + " TEXT, " +
                        COLUMN_DATE_TIME + " TEXT, " +
                        COLUMN_GROUP + " TEXT, " +
                        COLUMN_CHILD + " TEXT, " +
                        COLUMN_LIST + " TEXT" +
                        ");"
                );

        sqLiteDatabase.execSQL
                ("CREATE TABLE " + GROUPS_TABLE +
                        " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_GROUP + " TEXT UNIQUE, " +
                        COLUMN_LIST + " TEXT UNIQUE" +
                        ");"
                );

        sqLiteDatabase.execSQL
                ("CREATE TABLE " + CHILDREN_TABLE +
                        " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_CHILD + " TEXT, " +
                        COLUMN_GROUP + " TEXT " +
                        ");"
                );

        sqLiteDatabase.execSQL
                ("CREATE TABLE " + SEARCH_TABLE +
                        " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_SEARCH_KEY + " TEXT UNIQUE" +
                        ");"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CHECKED_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GROUPS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CHILDREN_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RECYCLING_BIN_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SEARCH_TABLE);
        onCreate(sqLiteDatabase);
    }

}
