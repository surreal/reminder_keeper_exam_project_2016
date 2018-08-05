package com.reminder_keeper.views;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.activities.ReminderActivity;
import com.reminder_keeper.AuthorityClass;
import com.reminder_keeper.broadcasts.NotificationItemBroadcastReceiver;
import com.reminder_keeper.R;

public class NotificationItemView
{

    public static RemoteViews remoteViews;

    public NotificationItemView(Context context, String reminderText, String timeDate, int id, boolean isGreen)
    { initViews(context, reminderText, timeDate, id, isGreen); }

    private void initViews(Context context, String reminderText, String timeDate, int id, boolean isGreen)
    {
        reminderText = reminderText == null ? context.getString(R.string.no_reminders_yet) : reminderText;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_notification);
        remoteViews.setTextViewText(R.id.item_notification_reminder_text,  reminderText);
        remoteViews.setTextViewText(R.id.item_notification_time_date, timeDate);

        if (isGreen) { setGreenColor(context); } else { setGrayColor(context);}

        Intent itemClickedIntent = new Intent(context, MainActivity.class);
        Intent timeDateClickedIntent = new Intent(context, ReminderActivity.class).putExtra(AuthorityClass.ID_TO_DO, id);
        Intent plusClickedIntent = new Intent(context, ReminderActivity.class);
        Intent nextReminderIntent = new Intent(context, NotificationItemBroadcastReceiver.class);
        nextReminderIntent.putExtra("isNextClicked", true);
        Intent previousReminderIntent = new Intent(context, NotificationItemBroadcastReceiver.class);

        PendingIntent onPlusClickPendingIntent = PendingIntent.getActivity(context, 1, plusClickedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent onItemClickPendingIntent = PendingIntent.getActivity(context, 2, itemClickedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent timeDateClickPendingIntent = PendingIntent.getActivity(context, 3, timeDateClickedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent previousReminderPendingIntent = PendingIntent.getBroadcast(context, 4, previousReminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent nextReminderPendingIntent = PendingIntent.getBroadcast(context, 5, nextReminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.item_notification_plus, onPlusClickPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.item_notification_reminder_text, timeDateClickPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.item_notification_time_date, timeDateClickPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.item_notification_next_reminder, nextReminderPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.item_notification_previous_reminder, previousReminderPendingIntent);

        Notification notification;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        {
            notification = new NotificationCompat.Builder(context)
                    .setAutoCancel(false)
                    .setSmallIcon(R.mipmap.main_icon_no_bg)
                    .setContentIntent(onItemClickPendingIntent)
                    .setCustomBigContentView(remoteViews)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
        } else {
            if (MainActivity.isRTL)
            {
                remoteViews.setTextViewText(R.id.item_notification_next_reminder,  "<");
                remoteViews.setTextViewText(R.id.item_notification_previous_reminder, ">");
            }

            notification = new NotificationCompat.Builder(context)
                    .setAutoCancel(false)
                    .setSmallIcon(R.mipmap.main_icon_no_bg)
                    .setContentIntent(onItemClickPendingIntent)
                    .setCustomContentView(remoteViews)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private void setGrayColor(Context context)
    {
        remoteViews.setTextColor(R.id.item_notification_reminder_text, context.getResources().getColor(android.R.color.tertiary_text_dark));
        remoteViews.setTextColor(R.id.item_notification_time_date, context.getResources().getColor(android.R.color.tertiary_text_dark));
    }

    public void setGreenColor(Context context)
    {
        remoteViews.setTextColor(R.id.item_notification_reminder_text, context.getResources().getColor(R.color.colorGreen));
        remoteViews.setTextColor(R.id.item_notification_time_date, context.getResources().getColor(R.color.colorGreen));
    }

}
