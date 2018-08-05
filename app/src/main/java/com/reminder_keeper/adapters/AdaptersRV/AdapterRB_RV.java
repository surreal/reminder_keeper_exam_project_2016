package com.reminder_keeper.adapters.AdaptersRV;

import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reminder_keeper.activities.RecyclingBinActivity;
import com.reminder_keeper.adapters.CursorAdaptersRV.ToDoRV.CursorAdapterRV_ToDoAbstract;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.R;

public class AdapterRB_RV extends CursorAdapterRV_ToDoAbstract<AdapterRB_RV.ViewHolderRB>
{
    private RecyclingBinActivity recyclingBinActivity;
    private String currentActivity;
    private Cursor cursorToDO, cursorChecked;
    private Activity activity;

    public AdapterRB_RV(Activity activity, Cursor cursorToDO, Cursor cursorChecked, String currentActivity)
    {   super(activity);
        this.activity = activity;
        this.currentActivity = currentActivity;
        this.cursorToDO = cursorToDO;
        this.cursorChecked = cursorChecked;
        if (cursorToDO != null) {
            setupCursorAdapter(cursorToDO, R.layout.item_view_reminders_without_checkbox);
        } else if (cursorChecked != null) {
            setupCursorAdapter(cursorChecked, R.layout.item_view_reminders_without_checkbox);
        }
        if (currentActivity.equals(RecyclingBinActivity.RECYCLING_BIN_ACTIVITY))
        {
            recyclingBinActivity = new RecyclingBinActivity();
        }
    }

    @Override
    public int getItemCount() { return cursorAdapterToDo.getCursor().getCount(); }

    @Override
    public ViewHolderRB onCreateViewHolder(ViewGroup parent, int viewType)
    { return new ViewHolderRB(cursorAdapterToDo.newView(activity, cursorAdapterToDo.getCursor(), parent)); }

    @Override
    public void onBindViewHolder(ViewHolderRB holder, int position)
    {
        cursorAdapterToDo.getCursor().moveToPosition(position);
        setViewHolder(holder);
        cursorAdapterToDo.bindView(null, activity, cursorAdapterToDo.getCursor());
    }

    public class ViewHolderRB extends ViewHolderRV_Abstract implements View.OnClickListener{
        private TextView reminderTV;
        private TextView timeTV;
        private boolean isSelected;
        private int itemId;

        public ViewHolderRB(View itemView)
        {   super(itemView);
            reminderTV = (TextView) itemView.findViewById(R.id.item_view_recycling_bin_reminder_tv);
            timeTV = (TextView) itemView.findViewById(R.id.item_view_recycling_bin_time_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindCursor(Cursor cursor)
        {
            itemId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
            reminderTV.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER)));
            timeTV.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME)));
        }

        @Override
        public void onClick(View view)
        {
            if (!isSelected) {
                view.setSelected(true);
                recyclingBinActivity.onItemClicked(itemId);
                recyclingBinActivity.isListItemSelected = true;
            } else {
                view.setSelected(false);
                if (currentActivity.equals(RecyclingBinActivity.RECYCLING_BIN_ACTIVITY)) {
                    recyclingBinActivity.onItemClicked(itemId);
                    recyclingBinActivity.isListItemSelected = false;
                }
            }
            isSelected = !isSelected;
        }

    }
}
