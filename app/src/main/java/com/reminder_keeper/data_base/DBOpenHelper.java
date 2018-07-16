package com.reminder_keeper.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Andrey on 08/07/2017.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME_FILE = "remindersDB.db";
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


        /*
        sqLiteDatabase.execSQL
                (
                        "INSERT INTO " + GROUPS_TABLE +
                                " (" + COLUMN_GROUP + ", " + COLUMN_LIST + ")" +
                                " VALUES " +
                                "('FIRST', null), " +
                                "('SECOND', null), " +
                                "(null, 'outside list ONE'), " +
                                "(null, 'outside list TWO');"
                );

        sqLiteDatabase.execSQL
                (
                        "INSERT INTO " + CHILDREN_TABLE +
                                " (" + COLUMN_CHILD + ", " + COLUMN_GROUP + ")" +
                                " VALUES " +
                                "('one in FIRST', 'FIRST'), " +
                                "('two in FIRST', 'FIRST'), " +
                                "('one in SECOND', 'SECOND'), " +
                                "('two in SECOND', 'SECOND');"
                );

        sqLiteDatabase.execSQL
                (
                        "INSERT INTO " + TODO_TABLE +
                                " (" + COLUMN_REMINDER + ", " + COLUMN_DATE_TIME + ", " + COLUMN_GROUP + ", " + COLUMN_CHILD + ", " + COLUMN_LIST + ")" +
                                " VALUES " +
                                "('to by bracelet', '10:30\n12/12/2012', 'FIRST', 'one in FIRST', null), " +
                                "('recyclerView explain', '15:20\n12/12/2012', 'FIRST', 'one in FIRST', null), " +
                                "('Expandable RecyclerView', '15:20\n12/12/2012', 'FIRST', 'one in FIRST', null), " +
                                "('View Holder', '15:20\n12/12/2012', 'SECOND', 'one in FIRST', null), " +
                                "('pochinit comp uchilke', '15:20\n12/12/2012', 'SECOND', 'one in SECOND', null), " +
                                "('sdelat prezentaciu', '15:20\n12/12/2012', 'SECOND', 'one in SECOND', null), " +
                                "('To Outside', '15:20\n12/12/2012', null, null, 'outside list ONE'), " +
                                "('To Outside', '15:20\n12/12/2012', null, null, 'outside list ONE'), " +
                                "('To Outside', '15:20\n12/12/2012', null, null, 'outside list TWO'), " +
                                "('bank leumi', '18:10\n12/12/2012', null, null, 'outside list TWO');"
                );
                */
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(sqLiteDatabase);
    }

}
