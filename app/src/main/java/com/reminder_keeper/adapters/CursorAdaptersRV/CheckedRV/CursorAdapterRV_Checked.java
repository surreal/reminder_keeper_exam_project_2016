package com.reminder_keeper.adapters.CursorAdaptersRV.CheckedRV;

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

import com.reminder_keeper.adapters.AdaptersRV.ViewHolderRV_Abstract;
import com.reminder_keeper.CalendarConverter;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.R;

public class CursorAdapterRV_Checked extends CursorAdapterRV_CheckedAbstract<CursorAdapterRV_Checked.ViewHolder> {

    private CursorsDBMethods cursors;
    private final CalendarConverter calendarConverter;
    private MainActivity mainActivity;

    public CursorAdapterRV_Checked(Activity activity, Cursor cursorChecked)
    {
        this.activity = activity;
        setupCursorAdapterChecked(cursorChecked, R.layout.item_view_reminders);
        cursors = new CursorsDBMethods(activity);
        calendarConverter = new CalendarConverter(activity);
        mainActivity = new MainActivity();
    }

    @Override
    public int getItemCount() {return cursorAdapterChecked.getCount();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    { return new ViewHolder(cursorAdapterChecked.newView(activity, cursorAdapterChecked.getCursor(), parent)); }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        cursorAdapterChecked.getCursor().moveToPosition(position);
        setViewHolderChecked(holder);
        cursorAdapterChecked.bindView(null, activity, cursorAdapterChecked.getCursor());
    }

    public class ViewHolder extends ViewHolderRV_Abstract
                            implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnTouchListener
    {
        private final TextView reminderTextCheckedTV, timeTextCheckedTV;
        private final CheckBox checkBoxChecked;
        private String reminderTextChecked, valueTimeChecked;
        private int valueIdChecked;

        public ViewHolder(View itemView)
        {   super(itemView);
            reminderTextCheckedTV = (TextView) itemView.findViewById(R.id.item_view_reminder_reminder_tv);
            timeTextCheckedTV = (TextView) itemView.findViewById(R.id.item_view_reminder_date_tv);
            checkBoxChecked = (CheckBox) itemView.findViewById(R.id.item_view_reminder_checkbox);
            checkBoxChecked.setChecked(true);
            checkBoxChecked.setOnCheckedChangeListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);

            reminderTextCheckedTV.setTextColor(activity.getResources().getColor(android.R.color.tertiary_text_dark));
            timeTextCheckedTV.setTextColor(activity.getResources().getColor(android.R.color.tertiary_text_dark));
        }

        @Override
        public void bindCursor(Cursor cursor)
        {
            valueIdChecked = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
            reminderTextChecked = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER));
            valueTimeChecked = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME));
            reminderTextCheckedTV.setText(reminderTextChecked);
            timeTextCheckedTV.setText(valueTimeChecked);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            checkBoxChecked.setChecked(false);
            //TODO: init Handler
            new Handler(Looper.getMainLooper())
            {   @Override
                public void handleMessage(Message msg)
                {super.handleMessage(msg);
                    Log.d("handleMessage", msg + "");
                }
            }.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    try
                    {
                        cursors.moveToDB(-1, valueIdChecked, true);
                        mainActivity.initRelevantAdapter(CalendarConverter.calIsSetNoTD);
                    } catch (Exception  e)
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
            mainActivity.itemClicked(-1, valueIdChecked);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                mainActivity.onItemActionDown(-1, valueIdChecked);
            }
            return false;
        }
    }
}
