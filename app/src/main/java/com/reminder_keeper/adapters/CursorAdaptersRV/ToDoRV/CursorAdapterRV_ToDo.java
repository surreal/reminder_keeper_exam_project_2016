
package com.reminder_keeper.adapters.CursorAdaptersRV.ToDoRV;

import android.app.Activity;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.reminder_keeper.AuthorityClass;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.R;
import com.reminder_keeper.adapters.AdaptersRV.ViewHolderRV_Abstract;
import com.reminder_keeper.CalendarConverter;

import java.util.ArrayList;


public class CursorAdapterRV_ToDo extends CursorAdapterRV_ToDoAbstract<CursorAdapterRV_ToDo.ViewHolder>
{
    private final CursorsDBMethods cursors;
    private final CalendarConverter calendarConverter;
    private MainActivity mainActivity;
    ArrayList<Integer> daysArray;

    public CursorAdapterRV_ToDo(Activity activity, Cursor cursorToDo)
    {   super(activity);
        this.activity = activity;
        setupCursorAdapter(cursorToDo, R.layout.item_view_reminders);
        cursors = new CursorsDBMethods(activity);
        calendarConverter = new CalendarConverter(activity);
        mainActivity = new MainActivity();
    }

    @Override
    public int getItemCount()
    {
        return cursorAdapterToDo.getCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    { return new ViewHolder(cursorAdapterToDo.newView(activity, cursorAdapterToDo.getCursor(), parent)); }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        cursorAdapterToDo.getCursor().moveToPosition(position);
        setViewHolder(holder);
        cursorAdapterToDo.bindView(null, activity, cursorAdapterToDo.getCursor());
    }

    public class ViewHolder extends ViewHolderRV_Abstract
            implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {
        private final TextView noteTextToDo, timeTextToDo, repeatIndicatorTV;
        private final CheckBox checkBox;
        private int idToDo;
        private String reminderValue, dateTimeDBValue, repeatOptionDBValue, repeatDaysOrDateDBValue, days = activity.getString(R.string.repeat_every) + " ";


        public ViewHolder(View itemView)
        {   super(itemView);
            noteTextToDo = (TextView) itemView.findViewById(R.id.item_view_reminder_reminder_tv);
            timeTextToDo = (TextView) itemView.findViewById(R.id.item_view_reminder_date_tv);
            repeatIndicatorTV = (TextView) itemView.findViewById(R.id.item_view_reminder_repeat_indicator_tv);
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            itemView.setOnLongClickListener(this);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_view_reminder_checkbox);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor)
        {
            idToDo = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
            reminderValue = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER));
            dateTimeDBValue = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME));
            repeatOptionDBValue = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REPEAT_OPTION));
            repeatDaysOrDateDBValue = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REPEAT_CUSTOM_DAYS_OR_DATE));

            repeatOptionDBValue = repeatOptionDBValue != null ? repeatOptionDBValue :  AuthorityClass.REPEAT_NO_REPEAT;
            setDateTimeAndColor();
            setRepeatIndicator();
            defineRepeatOption(cursor);
        }

        private void setDateTimeAndColor() {
            if (dateTimeDBValue != null)
            {
                calendarConverter.timeDateString = dateTimeDBValue;
                calendarConverter.convertDateTimeFromDB();
                if (CalendarConverter.calIsSetWithTD.compareTo(CalendarConverter.currentCalWithTD) < 0)
                {
                    timeTextToDo.setTextColor(activity.getResources().getColor(R.color.colorRed));
                } else {
                    timeTextToDo.setTextColor(activity.getResources().getColor(R.color.colorGreen));
                }
            }
            noteTextToDo.setText(reminderValue);
            timeTextToDo.setText(dateTimeDBValue);
        }

        private void setRepeatIndicator() {
            if (!repeatOptionDBValue.equals(AuthorityClass.REPEAT_NO_REPEAT)) {
                repeatIndicatorTV.setVisibility(View.VISIBLE);
            } else {
                repeatIndicatorTV.setVisibility(View.INVISIBLE);
            }
        }

        private void defineRepeatOption(Cursor cursor) {
            switch (repeatOptionDBValue)
            {
                case AuthorityClass.REPEAT_CUSTOM:
                    setDaysString(cursor);
                    break;
                case AuthorityClass.REPEAT_EVERY_WEEK:
                    setDaysString(cursor);
                    break;
                case AuthorityClass.REPEAT_EVERY_MONTH:
                    days += repeatDaysOrDateDBValue;
                    break;
            }
        }

        private void setDaysString(Cursor cursor) {
            daysArray = mainActivity.convertDaysToIntArray(cursor);
            for (int i = 0; i < daysArray.size(); i++)
            {
                days += calendarConverter.getDayString(daysArray.get(i));
                if (i < daysArray.size() -1)
                {
                    days += ", ";
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            checkBox.setChecked(true);
            //TODO: init Handler
            new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg)
            { super.handleMessage(msg);
                Log.d("handleMessage", msg + "");
            }
            }.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    try {
                        cursors.moveToDB(idToDo, -1, true);
                        mainActivity.initRelevantAdapter(CalendarConverter.calIsSetNoTD);
                        mainActivity.deleteAlarmNotification(idToDo);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            },1000);
        }

        @Override
        public void onClick(View view)
        {
            view.setSelected(true);
            mainActivity.itemClicked(idToDo, -1);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                mainActivity.onItemActionDown(idToDo, -1);
            }
            return false;
        }

        @Override
        public boolean onLongClick(View view) {
            String repeatOption = AuthorityClass.REPEAT_NO_REPEAT;
            switch (repeatOptionDBValue)
            {
                case AuthorityClass.REPEAT_EVERY_DAY:
                    repeatOption = activity.getString(R.string.repeat_everyday);
                    break;
                case AuthorityClass.REPEAT_EVERY_WORK_DAY:
                    repeatOption = activity.getString(R.string.repeat_every_workday);
                    break;
                case AuthorityClass.REPEAT_EVERY_WEEK:
                    repeatOption = days;
                    break;
                case AuthorityClass.REPEAT_EVERY_MONTH:
                    repeatOption = days;
                    break;
                case AuthorityClass.REPEAT_CUSTOM:
                    repeatOption = days;
                    break;
                case AuthorityClass.REPEAT_NO_REPEAT:
                    repeatOption = activity.getString(R.string.no_repeat);
                    break;
            }
            Toast.makeText(activity, repeatOption, Toast.LENGTH_LONG).show();
            return false;
        }

    }
}
