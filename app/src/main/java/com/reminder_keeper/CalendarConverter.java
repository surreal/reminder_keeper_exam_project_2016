package com.reminder_keeper;

import android.content.Context;
import android.database.Cursor;

import com.reminder_keeper.adapters.AdaptersRV.Models.DayModel;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class CalendarConverter
{
    public static Calendar currentCalWithTD, calIsSetWithTD, currentCalNoTD, calIsSetNoTD;
    public static int year, monthForDB, monthForCalendar, dayOfMonth, hour, minutes;
    public static String timeString, dateString, timeDateString,  timeDateStringForNotification;
    private static Context context;
    private static Cursor cursor;
    private String dayString, monthString;
    public static boolean isHaveFutureTimeDate;

    public CalendarConverter(Context context)
    {
        this.context = context;
        calIsSetWithTD = new GregorianCalendar();
        currentCalWithTD = new GregorianCalendar();
        calIsSetNoTD = new GregorianCalendar();
        currentCalNoTD = new GregorianCalendar(currentCalWithTD.get(Calendar.YEAR), currentCalWithTD.get(Calendar.MONTH), currentCalWithTD.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    public String getSelectedDateString(int dayOfWeek, int month)
    {
        if (dayOfWeek == -1 && month == -1)
        {
            getDayString(currentCalWithTD.get(Calendar.DAY_OF_WEEK));
            getMonthString(currentCalWithTD.get(Calendar.MONTH) +1);
        } else {
            getDayString(dayOfWeek);
            getMonthString(month);
        }
        return dayString + ", " + monthString;
    }

    private String getMonthString(int month)
    {
        switch (month)
        {
            case 1:
                monthString = context.getString(R.string.january);
                break;
            case 2:
                monthString = context.getString(R.string.february);
                break;
            case 3:
                monthString = context.getString(R.string.march);
                break;
            case 4:
                monthString = context.getString(R.string.april);
                break;
            case 5:
                monthString = context.getString(R.string.may);
                break;
            case 6:
                monthString = context.getString(R.string.jun);
                break;
            case 7:
                monthString = context.getString(R.string.july);
                break;
            case 8:
                monthString = context.getString(R.string.august);
                break;
            case 9:
                monthString = context.getString(R.string.september);
                break;
            case 10:
                monthString = context.getString(R.string.october);
                break;
            case 11:
                monthString = context.getString(R.string.november);
                break;
            case 12:
                monthString = context.getString(R.string.december);
                break;
        }
        return monthString;
    }

    public String getDayString(int day)
    {
        switch (day)
        {
            case 1:
                dayString = context.getString(R.string.sunday);
                break;
            case 2:
                dayString = context.getString(R.string.monday);
                break;
            case 3:
                dayString = context.getString(R.string.tuesday);
                break;
            case 4:
                dayString = context.getString(R.string.wednesday);
                break;
            case 5:
                dayString = context.getString(R.string.thursday);
                break;
            case 6:
                dayString = context.getString(R.string.friday);
                break;
            case 7:
                dayString = context.getString(R.string.saturday);
                break;
        }
        return dayString;
    }

    //TODO: init date and time if action is new Note
    public void convertDateTimeForDB()
    {
        year = currentCalWithTD.get(Calendar.YEAR);
        monthForDB = currentCalWithTD.get(Calendar.MONTH) +1;
        monthForCalendar = currentCalWithTD.get(Calendar.MONTH);
        dayOfMonth = currentCalWithTD.get(Calendar.DAY_OF_MONTH);
        hour = currentCalWithTD.get(Calendar.HOUR_OF_DAY);
        minutes = currentCalWithTD.get(Calendar.MINUTE);
        timeString =  "00:00";
        dateString = ((dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "/" + (monthForDB < 10 ? "0" + monthForDB : monthForDB)  + "/" + year);
        timeDateString = timeString + "\n" + dateString;
    }

    /* converting time and date from DataBase to Calendar format */
    //TODO: sorting and converting time and date from ToDo
    public void convertDateTimeFromDB()
    {
        String hourFromDB = timeDateString.substring(0,2);
        String minuteFromDB = timeDateString.substring(3,5);
        String dayFromDB = timeDateString.substring(6,8);
        String monthFromDB = timeDateString.substring(9,11);
        String yearFromDB = timeDateString.substring(12,16);

        year = Integer.parseInt(yearFromDB);
        monthForCalendar = Integer.parseInt(monthFromDB) -1;
        monthForDB = Integer.parseInt(monthFromDB);
        dayOfMonth = Integer.parseInt(dayFromDB);
        hour = Integer.parseInt(hourFromDB);
        minutes = Integer.parseInt(minuteFromDB);

        timeString = hourFromDB + ":" + minuteFromDB;
        dateString = dayFromDB + "/" + monthFromDB + "/" + yearFromDB;

        calIsSetWithTD = new GregorianCalendar(year, monthForCalendar, dayOfMonth, hour, minutes);
        calIsSetNoTD = new GregorianCalendar(year, monthForCalendar, dayOfMonth, 0, 0);
    }

    public String setTimeString(int hour, int minute)
    {
        timeString = ((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute));
        return timeString;
    }

    public String setTimeString(Calendar calendar)
    {

        timeString = (calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + (calendar.get(Calendar.HOUR_OF_DAY)) : (calendar.get(Calendar.HOUR_OF_DAY)) + ":" + ((calendar.get(Calendar.MINUTE) < 10 ? "0" + (calendar.get(Calendar.MINUTE)) : (calendar.get(Calendar.MINUTE)))));
        return timeString;
    }

    public String setDateString(int day, int month, int year)
    {
        dateString = ((day < 10 ? "0" + day : day) + "/" + ((month + 1) < 10 ? "0" + (month + 1) : (month + 1))  + "/" + year);
        return dateString;
    }

    public void setTimeDateForDB()
    {
        timeDateString = timeString + "\n" + dateString;
        setTimeDateForNotification();
    }
    public String setTimeDateForDB(Calendar calendar)
    {
        timeDateString = setTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                + "\n" + setDateString(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        return timeDateString;
    }

    private void setTimeDateForNotification()
    {
        timeDateStringForNotification = timeString + " -> " + dateString;
    }

    public String setTimeDateForNotification(Calendar calendar)
    {
        String timeDateStringForNotification = context.getString(R.string.next_repeat) + " ";
        timeDateStringForNotification += setTimeString(calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE))
                + " -> "
                + setDateString(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        return timeDateStringForNotification;
    }

    public ArrayList<DayModel> getItemsDatesContainFromDBConvertedArray(Context context)
    {
        ArrayList<DayModel> datesModelsArray = new ArrayList<>();
        cursor = context.getContentResolver().query(DBProvider.TODO_TABLE_PATH_URI, new String[]{DBOpenHelper.COLUMN_DATE_TIME, DBOpenHelper.COLUMN_ID, DBOpenHelper.COLUMN_REMINDER}, null, null, null);
        while (cursor.moveToNext())
        {
            String timeDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME));
            int id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
            if (timeDate != null)
            {
                timeDateString = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME));
                convertDateTimeFromDB();
                datesModelsArray.add(new DayModel(calIsSetWithTD, id, -1, false, false, cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER)), -1));
            }
        }
        return datesModelsArray;
    }

    public ArrayList<DayModel> isHaveFutureTimeDateArray(Context context)
    {
        ArrayList<DayModel> tempItemsWithTimeDateArray = getItemsDatesContainFromDBConvertedArray(context);
        ArrayList<DayModel> itemsWithTimeDateArray = new ArrayList<>();
        Calendar currentCal = new GregorianCalendar();
        if (tempItemsWithTimeDateArray.size() > 0) {
            for (DayModel model : tempItemsWithTimeDateArray) {
                if (model.getCalendar().compareTo(currentCal) > 0) {
                    itemsWithTimeDateArray.add(model);
                    isHaveFutureTimeDate = true;
                }
            }
            if (itemsWithTimeDateArray.size() == 0) {
                isHaveFutureTimeDate = false;
                itemsWithTimeDateArray = tempItemsWithTimeDateArray;
            }
        }
        Collections.sort(itemsWithTimeDateArray);
        return itemsWithTimeDateArray;
    }
}
