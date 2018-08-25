package com.reminder_keeper.views;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reminder_keeper.activities.AboutActivity;
import com.reminder_keeper.activities.AccountActivity;
import com.reminder_keeper.activities.TheArrangeActivity;
import com.reminder_keeper.activities.LoginActivity;
import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.activities.ReminderActivity;
import com.reminder_keeper.activities.RecyclingBinActivity;
import com.reminder_keeper.activities.SettingsActivity;
import com.reminder_keeper.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.reminder_keeper.AuthorityClass.calendarConverter;
import static com.reminder_keeper.CalendarConverter.currentCalNoTD;

public class ToolbarView
{
    public static TextView titleTV;
    private TextView currentDateTV;

    private Calendar calendar;
    private RelativeLayout currentDateRL;
    private RelativeLayout.LayoutParams titleTVParams;

    public ToolbarView(final Activity activity, ActionBar actionBar, final String requestFrom)
    {
        currentDateRL = (RelativeLayout) activity.findViewById(R.id.toolbar_custom_current_date_RL);
        currentDateTV = (TextView) activity.findViewById(R.id.toolbar_custom_current_date_tv);
        titleTV = (TextView) activity.findViewById(R.id.toolbar_custom_title_tv);
        calendar = new GregorianCalendar();

        switch (requestFrom)
        {
            case MainActivity.MAIN_ACTIVITY:
                titleTV.setText(R.string.all_reminders);
                break;
            case ReminderActivity.REMINDER_ACTIVITY:
                titleTV.setText(R.string.select_list);
                titleTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        titleTV.setTextColor(activity.getResources().getColor(android.R.color.holo_blue_bright));
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {titleTV.setTextColor(activity.getResources().getColor(R.color.colorYellow));}
                                catch (Exception e){e.printStackTrace();}}}, 200);
                        new SelectListView(activity, requestFrom).initListViewDialog();
                    }
                });
                break;
            case RecyclingBinActivity.RECYCLING_BIN_ACTIVITY:
                titleTV.setText(R.string.recycling_bin);
                break;
            case SettingsActivity.SETTINGS_ACTIVITY:
                titleTV.setText(R.string.options);
                titleTV.setPaddingRelative(0,0,150,0);
                break;
            case TheArrangeActivity.THE_ARRANGE_ACTIVITY:
                titleTV.setText(R.string.rearrange_and_rename);
                break;
            case AccountActivity.ACCOUNT_ACTIVITY:
                titleTV.setText(R.string.profile);
                titleTV.setPaddingRelative(0,0,150,0);
                break;
            case LoginActivity.LOGIN_ACTIVITY:
                titleTV.setText(R.string.login);
                titleTV.setPaddingRelative(0,0,150,0);
                break;
            case AboutActivity.ABOUT_ACTIVITY:
                titleTV.setText(R.string.app_name);
                titleTV.setPaddingRelative(0,0,150,0);
                break;
        }
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    public void setCalendarViewToolbar(String dayMonth)
    {
        titleTVParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleTVParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        titleTVParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titleTV.setLayoutParams(titleTVParams);
        currentDateRL.setVisibility(View.VISIBLE);
        titleTV.setText(dayMonth);
        currentDateTV.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
        currentDateRL.setOnClickListener(new View.OnClickListener()
        { @Override
            public void onClick(View view)
            {
                currentDateTV.setSelected(true);
                MainActivity mainActivity = new MainActivity();
                mainActivity.loadArraysWithSelectedDayItemsIds(currentCalNoTD);
                mainActivity.rebindRemindersCursors(mainActivity.setStringIdsForDB(MainActivity.selectedListIdsToDo), mainActivity.setStringIdsForDB(MainActivity.selectedListIdsChecked));
                mainActivity.setDaysAdapterAndSnap();
                Toast.makeText(MainActivity.activity, calendarConverter.setDateString(calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setSequenceViewToolbar(String title)
    {
        titleTVParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleTVParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleTV.setLayoutParams(titleTVParams);
        currentDateRL.setVisibility(View.GONE);
        titleTV.setText(title);
    }
}
