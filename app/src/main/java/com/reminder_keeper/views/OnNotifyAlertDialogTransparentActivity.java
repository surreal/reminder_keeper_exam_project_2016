package com.reminder_keeper.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.reminder_keeper.AuthorityClass;
import com.reminder_keeper.CalendarConverter;
import com.reminder_keeper.R;
import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.activities.ReminderActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

/** This Activity is transparent, visible only AlertDialog */

public class OnNotifyAlertDialogTransparentActivity extends AppCompatActivity {

    private int requestCode, idToDo;
    private AlertDialog alertDialogRepeat;
    private AlertDialog.Builder dialogBuilder;
    private MainActivity mainActivity;
    private String reminderText, nextDateTime;

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
        if (getIntent().getBooleanExtra("isForFinish", false)){ finish(); }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    { super.onCreate(savedInstanceState);
        idToDo = getIntent().getIntExtra(AuthorityClass.ID_TO_DO, -1);
        requestCode = getIntent().getIntExtra(AuthorityClass.REQUEST_CODE, -1);
        reminderText = getIntent().getStringExtra(AuthorityClass.REMINDER_TEXT);
        nextDateTime = getIntent().getStringExtra(AuthorityClass.DATE_TIME);
        mainActivity = new MainActivity();
        initRepeatDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startActivity(new Intent(this, OnNotifyAlertDialogTransparentActivity.class).putExtra("isForFinish", true));
        alertDialogRepeat.dismiss();
        finish();
    }

    private void initRepeatDialog()
    {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_repeat, null, false);
        view.findViewById(R.id.notify_dialog_15m_iv).setOnClickListener(onRepeatADialogElementsClickListener);
        view.findViewById(R.id.notify_dialog_1h_iv).setOnClickListener(onRepeatADialogElementsClickListener);
        view.findViewById(R.id.notify_dialog_3h_iv).setOnClickListener(onRepeatADialogElementsClickListener);
        TextView reminderTextTV = (TextView) view.findViewById(R.id.notify_dialog_text_tv);
        reminderTextTV.setText(reminderText);
        reminderTextTV.setOnClickListener(onRepeatADialogElementsClickListener);
        alertDialogRepeat = dialogBuilder.create();
        alertDialogRepeat.setView(view);
        setGravity(alertDialogRepeat);
        alertDialogRepeat.show();
        alertDialogRepeat.setOnDismissListener(new DialogInterface.OnDismissListener()
        { @Override public void onDismiss(DialogInterface dialogInterface) { finish();} });
    }

    private void setGravity(AlertDialog alertDialog)
    {
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams windowManagerParams = window.getAttributes();
        windowManagerParams.gravity = Gravity.BOTTOM;
        window.setAttributes(windowManagerParams);
    }

    View.OnClickListener onRepeatADialogElementsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.notify_dialog_text_tv:
                    startActivity(new Intent(OnNotifyAlertDialogTransparentActivity.this, ReminderActivity.class).putExtra(AuthorityClass.ID_TO_DO, idToDo));
                    finish();
                    break;
                case R.id.notify_dialog_15m_iv:
                    setNotifierRepeatNotification(15, -1);
                    finish();
                    break;
                case R.id.notify_dialog_1h_iv:
                    setNotifierRepeatNotification(-1, 1);
                    finish();
                    break;
                case R.id.notify_dialog_3h_iv:
                    setNotifierRepeatNotification(-1, 3);
                    finish();
                    break;
            }
        }
    };

    private void setNotifierRepeatNotification(int plusMinutes, int plusHour)
    {
        Calendar calendar = new GregorianCalendar();
        if (plusMinutes != -1) {
            calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE) +plusMinutes);
        } else if(plusHour != -1){
            calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY) +plusHour);
        }
        calendar.set(Calendar.SECOND, 0);
        Toast.makeText(this, getString(R.string.will_remind_at) + " " + new CalendarConverter(this).setTimeString(calendar), Toast.LENGTH_SHORT).show();
        mainActivity.setNotificationAlarm(this ,reminderText, nextDateTime, requestCode, idToDo, calendar, AuthorityClass.REPEAT_NO_REPEAT, null);
    }
}
