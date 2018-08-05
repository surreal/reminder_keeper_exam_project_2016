package com.reminder_keeper.broadcasts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.support.v7.app.NotificationCompat;

import com.reminder_keeper.AuthorityClass;
import com.reminder_keeper.CalendarConverter;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.R;
import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.views.OnNotifyAlertDialogTransparentActivity;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NotifierNotificationReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String reminderText = intent.getStringExtra(AuthorityClass.REMINDER_TEXT);
        String dateTime = intent.getStringExtra(AuthorityClass.DATE_TIME);
        String repeatAction = intent.getStringExtra(AuthorityClass.REPEAT_ACTION);
        int requestCode = intent.getIntExtra(AuthorityClass.REQUEST_CODE, -1);
        int idToDo = intent.getIntExtra(AuthorityClass.ID_TO_DO, -1);
        ArrayList<Integer> weekDaysIntArray = intent.getIntegerArrayListExtra(AuthorityClass.REPEAT_CUSTOM_DAYS_ARRAY);

       if (!repeatAction.equals(AuthorityClass.REPEAT_NO_REPEAT))
       {
           CalendarConverter calendarConverter = new CalendarConverter(context);
           Calendar calendar = new GregorianCalendar();
           switch (repeatAction)
           {
               case AuthorityClass.REPEAT_EVERY_DAY:
                   calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) +1);
                   calendar.set(Calendar.SECOND, 0);
                   break;
               case AuthorityClass.REPEAT_EVERY_WORK_DAY:
                   if (calendar.get(Calendar.DAY_OF_WEEK) < 5) {
                       calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) +1);
                   } else if (calendar.get(Calendar.DAY_OF_WEEK) > 4){
                       int plusDays = 8 - Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                       calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH) +plusDays);
                   }
                   calendar.set(Calendar.SECOND, 0);
                   break;
               case AuthorityClass.REPEAT_EVERY_WEEK:
                   calendar.set(Calendar.WEEK_OF_MONTH, Calendar.getInstance().get(Calendar.WEEK_OF_MONTH) +1);
                   calendar.set(Calendar.SECOND, 0);
                   break;
               case AuthorityClass.REPEAT_EVERY_MONTH:
                   calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) +1);
                   calendar.set(Calendar.SECOND, 0);
                   break;
               case AuthorityClass.REPEAT_CUSTOM:
                   int nextDay = calendar.get(Calendar.DAY_OF_WEEK) == 7 ? 1 : calendar.get(Calendar.DAY_OF_WEEK) +1;
                   for (int i = nextDay; i <= 7; i++)
                   {
                       calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) +1);
                       if (weekDaysIntArray.contains(i)) { break; }
                       if (i == 7){
                           for (int j = 1; j < nextDay -1; j++) {
                               calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) +1);
                               if (weekDaysIntArray.contains(j)) { break; }
                           }
                       }
                   }
                   break;
           }

           dateTime = calendarConverter.setTimeDateForNotificationItem(calendar);
           String nextDateTimeForDB = calendarConverter.setTimeDateForDB(calendar);

           ContentValues contentValues = new ContentValues();
           contentValues.put(DBOpenHelper.COLUMN_DATE_TIME, nextDateTimeForDB);
           String where = DBOpenHelper.COLUMN_ID + "=" + idToDo;
           context.getContentResolver().update(DBProvider.TODO_TABLE_PATH_URI, contentValues, where, null);

           CursorsDBMethods cursors = new CursorsDBMethods();
           Cursor cursor = cursors.getCursorToDo(context);
           cursor.moveToLast();
           new MainActivity().setNotificationAlarm(context ,reminderText, dateTime, requestCode, idToDo, calendar, repeatAction, weekDaysIntArray);
       }

        Intent transparentActivityIntent = new Intent(context, OnNotifyAlertDialogTransparentActivity.class)
                .putExtra(AuthorityClass.REMINDER_TEXT, reminderText)
                .putExtra(AuthorityClass.ID_TO_DO, idToDo)
                .putExtra(AuthorityClass.DATE_TIME, dateTime)
                .putExtra(AuthorityClass.REQUEST_CODE, requestCode);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, idToDo, transparentActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(reminderText)
                .setContentText(dateTime)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.main_icon_no_bg)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(idToDo, notification);

        context.startActivity(transparentActivityIntent);
    }
}
