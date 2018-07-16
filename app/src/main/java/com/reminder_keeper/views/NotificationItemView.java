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

    public NotificationItemView(Context context, String note, String timeDate, int id, boolean isGreen)
    {
        initViews(context, note, timeDate, id, isGreen);
    }

    private void initViews(Context context, String note, String timeDate, int id, boolean isGreen)
    {
        note = note == null ? context.getString(R.string.no_notifications_yet) : note;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_notification);
        remoteViews.setTextViewText(R.id.item_notification_note,  note);
        remoteViews.setTextViewText(R.id.item_notification_time_date, timeDate);

        if (isGreen) { setGreenColor(context); } else { setGrayColor(context);}

        Intent itemClickedIntent = new Intent(context, MainActivity.class);
        Intent noteTimeClickedIntent = new Intent(context, ReminderActivity.class).putExtra(AuthorityClass.ID_TO_DO, id);
        Intent plusClickedIntent = new Intent(context, ReminderActivity.class);
        Intent nextNoteIntent = new Intent(context, NotificationItemBroadcastReceiver.class);
        nextNoteIntent.putExtra("isNextClicked", true);
        Intent previousNoteIntent = new Intent(context, NotificationItemBroadcastReceiver.class);

        PendingIntent pendingIntentOnPlusClick = PendingIntent.getActivity(context, 1, plusClickedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentOnItemClick = PendingIntent.getActivity(context, 2, itemClickedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingNoteTimeClick = PendingIntent.getActivity(context, 3, noteTimeClickedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent previousNotePendingIntent = PendingIntent.getBroadcast(context, 4, previousNoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent nextNotePendingIntent = PendingIntent.getBroadcast(context, 5, nextNoteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.item_notification_plus, pendingIntentOnPlusClick);
        remoteViews.setOnClickPendingIntent(R.id.item_notification_note, pendingNoteTimeClick);
        remoteViews.setOnClickPendingIntent(R.id.item_notification_time_date, pendingNoteTimeClick);
        remoteViews.setOnClickPendingIntent(R.id.item_notification_next_note, nextNotePendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.item_notification_previous_note, previousNotePendingIntent);

        Notification notification;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        {
            notification = new NotificationCompat.Builder(context)
                    .setAutoCancel(false)
                    .setSmallIcon(R.mipmap.main_icon_no_bg)
                    .setContentIntent(pendingIntentOnItemClick)
                    .setCustomBigContentView(remoteViews)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
        } else {
            if (MainActivity.isRTL)
            {
                remoteViews.setTextViewText(R.id.item_notification_next_note,  "<");
                remoteViews.setTextViewText(R.id.item_notification_previous_note, ">");
            }

            notification = new NotificationCompat.Builder(context)
                    .setAutoCancel(false)
                    .setSmallIcon(R.mipmap.main_icon_no_bg)
                    .setContentIntent(pendingIntentOnItemClick)
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
        remoteViews.setTextColor(R.id.item_notification_note, context.getResources().getColor(android.R.color.tertiary_text_dark));
        remoteViews.setTextColor(R.id.item_notification_time_date, context.getResources().getColor(android.R.color.tertiary_text_dark));
    }

    public void setGreenColor(Context context)
    {
        remoteViews.setTextColor(R.id.item_notification_note, context.getResources().getColor(R.color.colorGreen));
        remoteViews.setTextColor(R.id.item_notification_time_date, context.getResources().getColor(R.color.colorGreen));
    }

}
