package com.reminder_keeper.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import com.reminder_keeper.views.ToolbarView;
import com.reminder_keeper.AuthorityClass;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;
import com.reminder_keeper.listeners.OnListItemClickListener;
import com.reminder_keeper.R;
import com.reminder_keeper.CalendarConverter;
import com.reminder_keeper.views.SelectListView;

import static com.reminder_keeper.CalendarConverter.dateString;
import static com.reminder_keeper.CalendarConverter.timeDateString;
import static com.reminder_keeper.CalendarConverter.timeString;

public class ReminderActivity extends AppCompatActivity
        implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        OnListItemClickListener
{
    public static final String REMINDER_ACTIVITY = "ReminderActivity";
    private static Cursor cursor;
    public static String childTitle, listTitle, groupTitle, reminderTextFromDB, selectedChildTitle;

    private Uri uri;
    private String where;
    private int passedItemIdToDo, passedItemIdChecked;
    private TextView timeInput, dateInput, repeatTV;
    private EditText inputText;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private ContentValues contentValues;

    private CursorsDBMethods cursors;
    private CalendarConverter calendarConverter;
    private MainActivity mainActivity;
    private RadioButton repeatEverydayRB, repeatEveryWorkdayRB, repeatEveryWeekRB, repeatEveryMonthRB, repeatCustomRB, checkedRB, repeatNoRepeatRB;
    private String selectedRepeatOption = AuthorityClass.REPEAT_NO_REPEAT;
    private LinearLayout repeatEverydayLL, repeatCustomLL, repeatEveryMonthLL, repeatEveryWeekLL, repeatEveryWorkDayLL;

    private AlertDialog repeatViewDialog;
    private LinearLayout repeatNoRepeatLL;
    private ArrayList<Integer> selectedDaysForCustomRepeatArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    { super.onCreate(savedInstanceState); setContentView(R.layout.activity_reminder);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_custom));
        new ToolbarView(this, getSupportActionBar(), REMINDER_ACTIVITY);
        mainActivity = new MainActivity();
        cursors = new CursorsDBMethods(this);
        calendarConverter = new CalendarConverter(this);
        selectedDaysForCustomRepeatArray = new ArrayList<>();
        castings();
        setRepeatDialog();
        passedId();
    }

    //TODO: castings
    private void castings()
    {
        repeatTV = (TextView) findViewById(R.id.activity_reminder_repeat_tv);
        inputText = (EditText) findViewById(R.id.activity_reminder_EditText);
        timeInput = (TextView) findViewById(R.id.activity_reminder_time_tv);
        dateInput = (TextView) findViewById(R.id.activity_reminder_date_tv);
        dateInput.setOnClickListener(this);
        timeInput.setOnClickListener(this);
        repeatTV.setOnClickListener(this);
    }

    //TODO: actions if id is sent
    private void passedId()
    {
        passedItemIdToDo = getIntent().getIntExtra(AuthorityClass.ID_TO_DO, -1);
        passedItemIdChecked = getIntent().getIntExtra(AuthorityClass.ID_CHECKED, -1);
        if (passedItemIdToDo != -1 || passedItemIdChecked != -1) {
            getDBData();
            setDBDataOnRelevantViews();
        } else {
            calendarConverter.convertDateTimeForDB();
        }
    }

    //TODO: get data from DB if item from mainActivity is selected
    private void setDBDataOnRelevantViews()
    {
        String title = listTitle != null ? listTitle : childTitle;
        if (title == null) { title = AuthorityClass.UNCLASSIFIED; }
        if (title.equals(AuthorityClass.UNCLASSIFIED)) {
            ToolbarView.titleTV.setText(R.string.unclassified);
        } else {
            ToolbarView.titleTV.setText(title);
        }
        inputText.setText(reminderTextFromDB);
        if (timeDateString != null)
        {
            calendarConverter.convertDateTimeFromDB();
            timeInput.setText(timeString);
            dateInput.setText(dateString);
            if (passedItemIdChecked != -1)
            {
                timeInput.setTextColor(getResources().getColor(R.color.colorGray));
                dateInput.setTextColor(getResources().getColor(R.color.colorGray));
                repeatTV.setTextColor(getResources().getColor(R.color.colorGray));
            }
        } else {
            calendarConverter.convertDateTimeForDB();
        }
    }

    //TODO: get data from DB
    private void getDBData()
    {
        where = cursors.setWhere(passedItemIdToDo, passedItemIdChecked);
        uri = cursors.setUri(passedItemIdToDo);
        cursor = getContentResolver().query(uri, null, where, null, null);
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            reminderTextFromDB = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER));
            groupTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_GROUP));
            childTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CHILD));
            listTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_LIST));
            timeDateString = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME));
            if (passedItemIdToDo != -1) {
                String repeatActionDB = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REPEAT_OPTION));
                selectedRepeatOption = repeatActionDB != null ? repeatActionDB : AuthorityClass.REPEAT_NO_REPEAT;
                if (selectedRepeatOption.equals(AuthorityClass.REPEAT_CUSTOM))
                {
                    selectedDaysForCustomRepeatArray = mainActivity.convertDaysToIntArray(cursor);
                }
            }
        }

        checkRelevantDialogOptions();
    }

    //TODO: click listener for Time and Date views
    @Override
    public void onClick(View view)
    {
        if (view == dateInput)
        {
            datePickerDialog = new DatePickerDialog
                    (
                            this,this,
                            CalendarConverter.year,
                            CalendarConverter.monthForCalendar,
                            CalendarConverter.dayOfMonth
                    );
            datePickerDialog.show();
        } else if (view == timeInput) {
            timePickerDialog = new TimePickerDialog
                    (
                            this,this,
                            CalendarConverter.hour,
                            CalendarConverter.minutes,
                            true
                    );
            timePickerDialog.show();
        } else if (view == repeatTV)
        {
            repeatViewDialog.show();
        }
    }

    private void setRepeatDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        repeatViewDialog = dialogBuilder.create();
        View repeatView = LayoutInflater.from(this).inflate(R.layout.dialog_repeat_options, null, false);

        repeatNoRepeatLL = (LinearLayout) repeatView.findViewById(R.id.repeat_options_dialog_no_repeat_ll);
        repeatEverydayLL = (LinearLayout) repeatView.findViewById(R.id.repeat_options_dialog_repeat_everyday_ll);
        repeatEveryWorkDayLL = (LinearLayout) repeatView.findViewById(R.id.repeat_options_dialog_repeat_every_workday_ll);
        repeatEveryWeekLL = (LinearLayout) repeatView.findViewById(R.id.repeat_options_dialog_repeat_every_week_ll);
        repeatEveryMonthLL = (LinearLayout) repeatView.findViewById(R.id.repeat_options_dialog_repeat_every_month_ll);
        repeatCustomLL = (LinearLayout) repeatView.findViewById(R.id.repeat_options_dialog_repeat_custom_ll);

        repeatNoRepeatRB = (RadioButton) repeatView.findViewById(R.id.repeat_options_dialog_no_repeat_rb);
        repeatEverydayRB = (RadioButton) repeatView.findViewById(R.id.repeat_options_dialog_repeat_everyday_rb);
        repeatEveryWorkdayRB = (RadioButton) repeatView.findViewById(R.id.repeat_options_dialog_repeat_every_workday_rb);
        repeatEveryWeekRB = (RadioButton) repeatView.findViewById(R.id.repeat_options_dialog_repeat_every_week_rb);
        repeatEveryMonthRB = (RadioButton) repeatView.findViewById(R.id.repeat_options_dialog_repeat_every_month_rb);
        repeatCustomRB = (RadioButton) repeatView.findViewById(R.id.repeat_options_dialog_repeat_custom_rb);

        repeatNoRepeatLL.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatEverydayLL.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatEveryWorkDayLL.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatEveryWeekLL.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatEveryMonthLL.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatCustomLL.setOnClickListener(repeatOptionsDialogOptionsClickListener);

        repeatNoRepeatRB.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatEverydayRB.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatEveryWorkdayRB.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatEveryWeekRB.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatEveryMonthRB.setOnClickListener(repeatOptionsDialogOptionsClickListener);
        repeatCustomRB.setOnClickListener(repeatOptionsDialogOptionsClickListener);

        repeatViewDialog.setView(repeatView);
    }

    private void checkRelevantDialogOptions()
    {
        switch (selectedRepeatOption)
        {
            case AuthorityClass.REPEAT_NO_REPEAT:
                repeatNoRepeatRB.setChecked(true);
                checkedRB = repeatNoRepeatRB;
                repeatTV.setText(getString(R.string.no_repeat));
                break;
            case AuthorityClass.REPEAT_EVERY_DAY:
                repeatEverydayRB.setChecked(true);
                checkedRB = repeatEverydayRB;
                repeatTV.setText(R.string.repeat_everyday_btn);
                break;
            case AuthorityClass.REPEAT_EVERY_WORK_DAY:
                repeatEveryWorkdayRB.setChecked(true);
                checkedRB = repeatEveryWorkdayRB;
                repeatTV.setText(R.string.repeat_every_workday_btn);
                break;
            case AuthorityClass.REPEAT_EVERY_WEEK:
                repeatEveryWeekRB.setChecked(true);
                checkedRB = repeatEveryWeekRB;
                repeatTV.setText(R.string.repeat_every_week_btn);
                break;
            case AuthorityClass.REPEAT_EVERY_MONTH:
                repeatEveryMonthRB.setChecked(true);
                checkedRB = repeatEveryMonthRB;
                repeatTV.setText(R.string.repeat_every_month_btn);
                break;
            case AuthorityClass.REPEAT_CUSTOM:
                repeatCustomRB.setChecked(true);
                checkedRB = repeatCustomRB;
                repeatTV.setText(R.string.repeat_custom);
                break;
        }

    }

    View.OnClickListener repeatOptionsDialogOptionsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == repeatNoRepeatLL || view == repeatNoRepeatRB) {
                selectedRepeatOption = AuthorityClass.REPEAT_NO_REPEAT;
                setCheckUncheck(repeatNoRepeatRB);
                repeatTV.setText(getString(R.string.no_repeat));
            } else if (view == repeatEverydayLL || view == repeatEverydayRB) {
                selectedRepeatOption = AuthorityClass.REPEAT_EVERY_DAY;
                setCheckUncheck(repeatEverydayRB);
                repeatTV.setText(R.string.repeat_everyday_btn);
            } else if (view == repeatEveryWorkDayLL || view == repeatEveryWorkdayRB) {
                selectedRepeatOption = AuthorityClass.REPEAT_EVERY_WORK_DAY;
                setCheckUncheck(repeatEveryWorkdayRB);
                repeatTV.setText(R.string.repeat_every_workday_btn);
            } else if (view == repeatEveryWeekLL || view == repeatEveryWeekRB){
                selectedRepeatOption = AuthorityClass.REPEAT_EVERY_WEEK;
                setCheckUncheck(repeatEveryWeekRB);
                repeatTV.setText(R.string.repeat_every_week_btn);
            } else if (view == repeatEveryMonthLL || view == repeatEveryMonthRB){
                selectedRepeatOption = AuthorityClass.REPEAT_EVERY_MONTH;
                setCheckUncheck(repeatEveryMonthRB);
                repeatTV.setText(R.string.repeat_every_month_btn);
            } else if (view == repeatCustomLL || view == repeatCustomRB){
                selectedRepeatOption = AuthorityClass.REPEAT_CUSTOM;
                setCheckUncheck(repeatCustomRB);
                repeatTV.setText(R.string.repeat_custom);
                initRepeatCustomDialog();
            }
        }

        private void initRepeatCustomDialog()
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ReminderActivity.this);
            AlertDialog dialog = dialogBuilder.create();
            View repeatCustomDialogView = LayoutInflater.from(ReminderActivity.this).inflate(R.layout.dialog_repeat_custom, null, false);

            repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_sunday_tv).setOnClickListener(selectedDayFromCustomDialogClickListener);
            repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_monday_tv).setOnClickListener(selectedDayFromCustomDialogClickListener);
            repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_tuesday_tv).setOnClickListener(selectedDayFromCustomDialogClickListener);
            repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_wednesday_tv).setOnClickListener(selectedDayFromCustomDialogClickListener);
            repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_thursday_tv).setOnClickListener(selectedDayFromCustomDialogClickListener);
            repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_friday_tv).setOnClickListener(selectedDayFromCustomDialogClickListener);
            repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_saturday_tv).setOnClickListener(selectedDayFromCustomDialogClickListener);

            setRelevantDaysSelected(repeatCustomDialogView);
            dialog.setView(repeatCustomDialogView);
            dialog.show();
        }

        private void setRelevantDaysSelected(View repeatCustomDialogView) {
            if (selectedDaysForCustomRepeatArray.size() > 0)
            {
                for (int i = 1; i <= 7; i++)
                {
                    if (selectedDaysForCustomRepeatArray.contains(i))
                    {
                        switch (i)
                        {
                            case 1:
                                repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_sunday_tv).setSelected(true);
                                break;
                            case 2:
                                repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_monday_tv).setSelected(true);
                                break;
                            case 3:
                                repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_tuesday_tv).setSelected(true);
                                break;
                            case 4:
                                repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_wednesday_tv).setSelected(true);
                                break;
                            case 5:
                                repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_thursday_tv).setSelected(true);
                                break;
                            case 6:
                                repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_friday_tv).setSelected(true);
                                break;
                            case 7:
                                repeatCustomDialogView.findViewById(R.id.repeat_custom_dialog_saturday_tv).setSelected(true);
                                break;

                        }
                    }
                }
            }
        }

        private void setCheckUncheck(RadioButton relevantRB) {
            if (checkedRB != null) { checkedRB.setChecked(false); }
            checkedRB = relevantRB;
            relevantRB.setChecked(true);
        }
    };

    View.OnClickListener selectedDayFromCustomDialogClickListener = new View.OnClickListener()
    { @Override
        public void onClick(View view)
        {
            int selectedDayInt = -1;
            switch (view.getId())
            {
                case R.id.repeat_custom_dialog_sunday_tv:
                    if (!view.isSelected()){ Toast.makeText(ReminderActivity.this, getString(R.string.sunday), Toast.LENGTH_SHORT).show(); }
                    selectedDayInt = 1;
                    break;
                case R.id.repeat_custom_dialog_monday_tv:
                    if (!view.isSelected()){ Toast.makeText(ReminderActivity.this, getString(R.string.monday), Toast.LENGTH_SHORT).show(); }
                    selectedDayInt = 2;
                    break;
                case R.id.repeat_custom_dialog_tuesday_tv:
                    if (!view.isSelected()){ Toast.makeText(ReminderActivity.this, getString(R.string.tuesday), Toast.LENGTH_SHORT).show(); }
                    selectedDayInt = 3;
                    break;
                case R.id.repeat_custom_dialog_wednesday_tv:
                    if (!view.isSelected()){ Toast.makeText(ReminderActivity.this, getString(R.string.wednesday), Toast.LENGTH_SHORT).show(); }
                    selectedDayInt = 4;
                    break;
                case R.id.repeat_custom_dialog_thursday_tv:
                    if (!view.isSelected()){ Toast.makeText(ReminderActivity.this, getString(R.string.thursday), Toast.LENGTH_SHORT).show(); }
                    selectedDayInt = 5;
                    break;
                case R.id.repeat_custom_dialog_friday_tv:
                    if (!view.isSelected()){ Toast.makeText(ReminderActivity.this, getString(R.string.friday), Toast.LENGTH_SHORT).show(); }
                    selectedDayInt = 6;
                    break;
                case R.id.repeat_custom_dialog_saturday_tv:
                    if (!view.isSelected()){ Toast.makeText(ReminderActivity.this, getString(R.string.saturday), Toast.LENGTH_SHORT).show(); }
                    selectedDayInt = 7;
                    break;
            }

            if (!view.isSelected()) {
                view.setSelected(true);
                selectedDaysForCustomRepeatArray.add(selectedDayInt);
            } else {
                view.setSelected(false);
                selectedDaysForCustomRepeatArray.remove(selectedDaysForCustomRepeatArray.indexOf(selectedDayInt));
            }
        }
    };

    //TODO: listener for Time is set
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute)
    {
        CalendarConverter.hour = hour;
        CalendarConverter.minutes = minute;
        calendarConverter.setTimeString(hour, minute);
        calendarConverter.setTimeDateForDB();
        CalendarConverter.calIsSetWithTD.set(CalendarConverter.year, CalendarConverter.monthForCalendar, CalendarConverter.dayOfMonth, hour, minute, 00);
        timeInput.setText(timeString);
        Toast.makeText(this, timeDateString, Toast.LENGTH_SHORT).show();
    }

    //TODO: listener for Date is set
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day)
    {
        CalendarConverter.year = year;
        CalendarConverter.monthForCalendar = month;
        CalendarConverter.dayOfMonth = day;
        CalendarConverter.calIsSetWithTD.set(year, month, day, CalendarConverter.hour, CalendarConverter.minutes, 00);
        CalendarConverter.calIsSetNoTD.set(year, month, day, 0, 0, 0);
        calendarConverter.setDateString(day, month, year);
        calendarConverter.setTimeDateForDB();
        dateInput.setText(dateString);
        if (CalendarConverter.calIsSetNoTD.compareTo(CalendarConverter.currentCalNoTD) < 0) {
            Toast.makeText(this, R.string.date_is_passed_msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, timeDateString, Toast.LENGTH_SHORT).show();
        }
    }

    private void setContentValues(boolean isDateSet)
    {
        contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.COLUMN_REMINDER, inputText.getText().toString());
        if (isDateSet) { contentValues.put(DBOpenHelper.COLUMN_DATE_TIME, timeDateString); }
        if (selectedRepeatOption.equals(AuthorityClass.REPEAT_CUSTOM) && selectedDaysForCustomRepeatArray.size() > 0)
        {
            String daysInNums = "";
            for (int day : selectedDaysForCustomRepeatArray) { daysInNums += day + ""; }
            contentValues.put(DBOpenHelper.COLUMN_REPEAT_OPTION, selectedRepeatOption);
            contentValues.put(DBOpenHelper.COLUMN_REPEAT_CUSTOM_DAYS_OR_DATE, daysInNums);
        } else {
            contentValues.put(DBOpenHelper.COLUMN_REPEAT_OPTION, selectedRepeatOption);
            if (selectedRepeatOption.equals(AuthorityClass.REPEAT_EVERY_WEEK))
            {
                contentValues.put(DBOpenHelper.COLUMN_REPEAT_CUSTOM_DAYS_OR_DATE, CalendarConverter.calIsSetNoTD.get(Calendar.DAY_OF_WEEK));
            } else if (selectedRepeatOption.equals(AuthorityClass.REPEAT_EVERY_MONTH))
            {
                int dayDateInt = CalendarConverter.calIsSetNoTD.get(Calendar.DAY_OF_MONTH);
                String dayDateString;
                dayDateString = (dayDateInt < 10 ? "0" + dayDateInt : dayDateInt) + "";
                contentValues.put(DBOpenHelper.COLUMN_REPEAT_CUSTOM_DAYS_OR_DATE, dayDateString);
            }
        }

        if (ToolbarView.titleTV.getText().toString().equals(getString(R.string.unclassified)) ||
                ToolbarView.titleTV.getText().toString().equals(getString(R.string.select_list)))
        {
            contentValues.put(DBOpenHelper.COLUMN_LIST, AuthorityClass.UNCLASSIFIED);
        } else {
            if (groupTitle != null && childTitle != null) {
                contentValues.put(DBOpenHelper.COLUMN_GROUP, groupTitle);
                contentValues.put(DBOpenHelper.COLUMN_CHILD, childTitle);
                contentValues.putNull(DBOpenHelper.COLUMN_LIST);
            } else if (listTitle != null) {
                contentValues.put(DBOpenHelper.COLUMN_LIST, listTitle);
                contentValues.putNull(DBOpenHelper.COLUMN_GROUP);
                contentValues.putNull(DBOpenHelper.COLUMN_CHILD);
            }
        }
    }

    //TODO: update DB data
    private void insertUpdateDeleteDBData()
    {
        uri = passedItemIdToDo != -1 ? DBProvider.TODO_TABLE_PATH_URI : DBProvider.CHECKED_TABLE_PATH_URI;
        if (passedItemIdToDo != -1) {
            getContentResolver().update(uri, contentValues, where, null);
        } else {
            getContentResolver().insert(DBProvider.TODO_TABLE_PATH_URI, contentValues);
            if (passedItemIdChecked != -1) {
                getContentResolver().delete(DBProvider.CHECKED_TABLE_PATH_URI, where, null);
            }
        }
    }


    //TODO: set visible actionBar save button
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.icon).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_layer,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //TODO: onOptionsItemSelected()
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // SD Icon
            case R.id.icon:
                sdClickedAction();
                break;
            case android.R.id.home:
                finishAndNullTheStatics();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sdClickedAction() {
        if (!inputText.getText().toString().isEmpty())
        {
            CalendarConverter.currentCalWithTD = new GregorianCalendar();
            if (CalendarConverter.calIsSetWithTD.compareTo(CalendarConverter.currentCalWithTD) > 0)
            {
                Toast.makeText(this, timeDateString, Toast.LENGTH_SHORT).show();
                if (selectedRepeatOption.equals(AuthorityClass.REPEAT_CUSTOM))
                {
                    selectedRepeatOption = selectedDaysForCustomRepeatArray.size() > 0 ? selectedRepeatOption : AuthorityClass.REPEAT_NO_REPEAT;
                    if (selectedDaysForCustomRepeatArray.size() > 1){ Collections.sort(selectedDaysForCustomRepeatArray); }
                }
                setContentValues(true);
                insertUpdateDeleteDBData();

                int id = passedItemIdToDo;
                if (passedItemIdToDo == -1)
                {
                    cursors.getCursorToDo();
                    cursor = CursorsDBMethods.cursor;
                    cursor.moveToFirst();
                    id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
                }

                CalendarConverter.calIsSetWithTD.set(Calendar.SECOND, 0);
                mainActivity.setNotificationAlarm
                        (
                                this,
                                inputText.getText().toString(),
                                "",
                                mainActivity.generateRequestCode(this),
                                id,
                                CalendarConverter.calIsSetWithTD,
                                selectedRepeatOption,
                                selectedDaysForCustomRepeatArray
                        );
            } else {
                setContentValues(false);
                insertUpdateDeleteDBData();
                timeDateString = null;
                Toast.makeText(this, R.string.time_not_set_msg, Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK);
            finishAndNullTheStatics();
        } else {
            Toast.makeText(this, R.string.input_empty_msg, Toast.LENGTH_LONG).show();
        }
    }

    private void finishAndNullTheStatics(){
        groupTitle = null;
        childTitle = null;
        selectedChildTitle = null;
        listTitle = null;
        finish();
        if (MainActivity.activity == null){
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    //TODO: on list item clicked
    @Override
    public void itemClicked(String expandedGroupTitle, String selectedChildTitle, String listTitle, String passedFrom, boolean isForAction, int id)
    {
        groupTitle = expandedGroupTitle;
        childTitle = selectedChildTitle;
        this.listTitle = listTitle;
        //TODO: child selected
        if (expandedGroupTitle != null && selectedChildTitle != null) {
            ToolbarView.titleTV.setText(selectedChildTitle);
            SelectListView.selectListViewDialog.dismiss();
            //TODO: list selected
        } else if (listTitle != null) {
            this.selectedChildTitle = null;
            ToolbarView.titleTV.setText(listTitle);
            SelectListView.selectListViewDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed()
    {   super.onBackPressed();
        finishAndNullTheStatics();
    }
}
