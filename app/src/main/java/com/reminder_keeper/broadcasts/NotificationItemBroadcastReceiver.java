package com.reminder_keeper.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.reminder_keeper.adapters.AdaptersRV.Models.DayModel;
import com.reminder_keeper.CalendarConverter;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;
import com.reminder_keeper.R;
import com.reminder_keeper.views.NotificationItemView;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationItemBroadcastReceiver extends BroadcastReceiver
{
    private static int index = 0;
    private static Cursor cursor;
    private static String reminderText, timeDate, fullTimeDate = "";
    public static ArrayList<DayModel> itemsWithTimeDateArray;
    private static CalendarConverter calendarConverter;
    private static Context context;
    private static int id;
    private static int counter = 0;
    private static boolean isGreen;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;
        counter++;
        boolean isNextClicked = intent.getBooleanExtra("isNextClicked", false);
        boolean isForStartIntent = intent.getBooleanExtra("isForStartIntent", false);
        if (isForStartIntent || counter == 1)
        {
            calendarConverter = new CalendarConverter(context);
            itemsWithTimeDateArray = calendarConverter.isHaveFutureTimeDateArray(context);
            isGreen = CalendarConverter.isHaveFutureTimeDate;

            if (itemsWithTimeDateArray.size() > 0){
                convertValuesFromModel();
            } else {
                cursor = context.getContentResolver().query(DBProvider.TODO_TABLE_PATH_URI, null, null, null, null);
                if (cursor.getCount() > 0)
                {
                    cursor.moveToFirst();
                    convertTimeDateFromDB();
                } else {
                    reminderText = context.getResources().getString(R.string.no_reminders_yet);
                }
            }
            new NotificationItemView(context, reminderText, timeDate, id, isGreen);
        } else {
            if (itemsWithTimeDateArray.size() >= 1) {
                if (itemsWithTimeDateArray.size() > 1) {
                    if (isNextClicked) {
                        index++;
                    } else {
                        index--;
                    }
                }
                convertValuesFromModel();
                reminderText = itemsWithTimeDateArray.get(index).getReminderText();
            } else {
                if (cursor.getCount() > 0) {
                    if (isNextClicked) {
                        if(!cursor.moveToNext()) { cursor.moveToFirst(); }
                    } else {
                        if(!cursor.moveToPrevious()) { cursor.moveToLast(); }
                    }
                    convertTimeDateFromDB();
                }
            }
            new NotificationItemView(context, reminderText, timeDate, id, isGreen);
            //printArray(itemsWithTimeDateArray);
        }
    }

    private void convertValuesFromModel()
    {
        if (index >= itemsWithTimeDateArray.size())
        {
            this.index = 0;
        } else if (index < 0)
        {
            index = itemsWithTimeDateArray.size() -1;
        }
        reminderText = itemsWithTimeDateArray.get(index).getReminderText();
        DayModel model = itemsWithTimeDateArray.get(index);
        id = model.getIdToDo();
        timeDate = calendarConverter.setDateString(model.getCalendar().get(Calendar.DAY_OF_MONTH),
                model.getCalendar().get(Calendar.MONTH),
                model.getCalendar().get(Calendar.YEAR))
                + " -> "
                + calendarConverter.setTimeString(model.getCalendar().get(Calendar.HOUR_OF_DAY),
                model.getCalendar().get(Calendar.MINUTE));
    }

    private void convertTimeDateFromDB()
    {
        id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
        reminderText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER));
        fullTimeDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME));
        timeDate = fullTimeDate != null ? fullTimeDate.substring(6, 16) + " -> " + fullTimeDate.substring(0,5) : "";
    }
}