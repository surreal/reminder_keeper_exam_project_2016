package com.reminder_keeper.adapters.AdaptersRV;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.adapters.AdaptersRV.Models.DayModel;
import com.reminder_keeper.CalendarConverter;
import com.reminder_keeper.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AdapterDaysRV extends RecyclerView.Adapter<AdapterDaysRV.ViewHolder>
{
    private ArrayList<DayModel> daysModelsArrayList;
    private final int currentDayOfYear;
    private View lastSelectedView;
    private int lastSelectedPosition;
    private boolean isDaysAdapterCalled;
    private final Calendar currentCalendar;

    public AdapterDaysRV(ArrayList<DayModel> daysModelsArrayList)
    {
        this.daysModelsArrayList = daysModelsArrayList;
        isDaysAdapterCalled = true;
        currentCalendar = Calendar.getInstance();
        currentDayOfYear = currentCalendar.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public int getItemCount() {
        return daysModelsArrayList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_horizontal, parent, false);
        itemView.getLayoutParams().width = (screenWidth / 7);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPosition(position);
        holder.setCalendar(daysModelsArrayList.get(position).getCalendar());
        int dayOfYear = daysModelsArrayList.get(position).getCalendar().get(Calendar.DAY_OF_YEAR);
        holder.dayOfMonthTV.setText((daysModelsArrayList.get(position).getCalendar().get(Calendar.DAY_OF_MONTH)) + "");

        if (position == lastSelectedPosition) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
        if (daysModelsArrayList.get(position).isHaveNotification()) {
            holder.isHaveNotificationsHighLighterTV.setVisibility(View.VISIBLE);
        } else {
            holder.isHaveNotificationsHighLighterTV.setVisibility(View.INVISIBLE);
        }
        if (dayOfYear == currentDayOfYear) {
            if (isDaysAdapterCalled) {
                isDaysAdapterCalled = false;
                holder.itemView.setSelected(true);
                daysModelsArrayList.get(position).setSelected(true);
            }
            holder.dayOfMonthTV.setBackgroundResource(R.drawable.cycle_yellow_background);
        } else {
            holder.dayOfMonthTV.setBackgroundResource(R.drawable.day_item_selector);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView dayOfMonthTV, isHaveNotificationsHighLighterTV;
        private int position;
        private Calendar calendar;

        public ViewHolder(final View itemView)
        { super(itemView);
            dayOfMonthTV = (TextView) itemView.findViewById(R.id.item_view_horizontal_tv);
            isHaveNotificationsHighLighterTV = (TextView) itemView.findViewById(R.id.item_view_horizontal_have_notes_highlighter);

            itemView.setOnClickListener(new View.OnClickListener()
            { @Override
            public void onClick(View view)
            {
                final MainActivity mainActivity = new MainActivity();
                CalendarConverter calendarConverter = new CalendarConverter(MainActivity.activity);
                if (daysModelsArrayList.get(lastSelectedPosition).isSelected())
                {
                    lastSelectedView.setSelected(false);
                    daysModelsArrayList.get(lastSelectedPosition).setSelected(false);
                }
                mainActivity.dispatchSelected();
                Calendar gCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
                mainActivity.loadAndShowSelectedDayItems(gCalendar);
                itemView.setSelected(true);
                lastSelectedView = itemView;
                lastSelectedPosition = position;
                Toast.makeText(MainActivity.activity, calendarConverter.setDateString(calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)),
                        Toast.LENGTH_SHORT).show();
                daysModelsArrayList.get(position).setSelected(true);
            }
            });
        }
        public void setPosition(int position) { this.position = position; }
        public void setCalendar(Calendar calendar) { this.calendar = calendar; }
    }
}
