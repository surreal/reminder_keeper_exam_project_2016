package com.reminder_keeper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reminder_keeper.adapters.AdaptersRV.AdapterDaysRV;
import com.reminder_keeper.adapters.AdaptersRV.Models.DayModel;
import com.reminder_keeper.adapters.CursorAdaptersRV.CheckedRV.CursorAdapterRV_Checked;
import com.reminder_keeper.adapters.CursorAdaptersRV.ToDoRV.CursorAdapterRV_ToDo;
import com.reminder_keeper.adapters.AdapterERV.models.ChildItemModel;
import com.reminder_keeper.adapters.AdapterERV.models.GroupItemModel;
import com.reminder_keeper.adapters.SnapHelper;
import com.reminder_keeper.broadcasts.NotifierNotificationReceiver;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;
import com.reminder_keeper.listeners.OnListItemClickListener;
import com.reminder_keeper.views.ToolbarView;
import com.reminder_keeper.views.SelectListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.reminder_keeper.activities.MainActivity.MAIN_ACTIVITY;
import static com.reminder_keeper.activities.MainActivity.activity;

public class AuthorityClass extends AppCompatActivity implements OnListItemClickListener
{
    public static final String ACTION_FROM_DRAWER_LAYOUT = "actionFromDrawerLayout";
    public static final String ID_TO_DO = "idToDo";
    public static final String ID_CHECKED = "idChecked";
    public static final String UNCLASSIFIED = "Unclassified";
    public static final String REMINDER_TEXT = "reminderText";
    public static final String REQUEST_CODE = "requestCode";
    public static final String DATE_TIME = "dateTime";
    public static final String REPEAT_EVERY_DAY = "repeatEveryDay";
    public static final String REPEAT_EVERY_WORK_DAY = "repeatEveryWorkDay";
    public static final String REPEAT_EVERY_WEEK = "repeatEveryWeek";
    public static final String REPEAT_EVERY_MONTH = "repeatEveryMonth";
    public static final String REPEAT_CUSTOM = "repeatCustom";
    public static final String REPEAT_NO_REPEAT = "repeatNoRepeat";
    public static final String REPEAT_ACTION = "repeatAction";
    public static final String REPEAT_CUSTOM_DAYS_ARRAY = "repeatCustomDaysArray";

    public static final int RESULT_INIT_ADAPTERS = 1, RESULT_LOAD_ARRAYS = 2;

    public static String selectedGroupTitleSLV, selectedChildTitleSLV, selectedListTitleSLV;
    public static String expandedGroupNameDL, selectedChildTitleDL, selectedListTitleDL;

    public static int firstPosition;
    public static int idToDoReminderItem, idCheckedReminderItem = -1;

    public static boolean isOnCalendarMode, isOnSearchMode;

    public static CursorAdapterRV_ToDo adapterToDo;
    public static CursorAdapterRV_Checked adapterChecked;
    public static CursorsDBMethods cursors;
    private Cursor cursor;

    public static DrawerLayout drawerLayout;
    public static ToolbarView toolbarCustom;

    public static RecyclerView recyclerViewToDo, recyclerViewChecked, recyclerViewGAndL, recyclerViewDays;

    public RelativeLayout rLayoutAllReminders;

    public static LinearLayout calendar_ll;
    public static LinearLayoutManager linearLayoutDays;
    private static SnapHelper snapHelper;

    public static TextView listsTitleTextChecked, listsTitleTextToDo;
    public TextView numOfRemindersTV, profileEmailTV, profileNameTV;
    public ImageView settingsIV, profileImageIV;
    public static ImageView calAndSeq_IV;
    public static RelativeLayout calAndSeqBtn_RL;
    public static SelectListView selectListView;

    public static ArrayList<DayModel> daysArrayList;
    public static ArrayList<Integer> selectedListIdsToDo, selectedListIdsChecked;
    public static ArrayList<GroupItemModel> groups;

    public static CalendarConverter calendarConverter;
    public static Calendar setCalNoTD;
    public static String searchInputText_ET, toolbarTitle;
    public static String selectionForDBQuery;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        idCheckedReminderItem = -1;
        idToDoReminderItem = -1;
        cursors = new CursorsDBMethods(activity);
    }

    //TODO: set String ids for parameter 'selection' in method getContentResolver().query()
    public static String setStringIdsForDB(ArrayList<Integer> selectedListIds)
    {
        String rowsId;
        if (selectedListIds.size() > 1) {
            rowsId = "_id = " +  selectedListIds.get(0);
            for (int index = 1; index < selectedListIds.size(); index++) {
                rowsId += ( " OR " + "_id" + "=" +  + selectedListIds.get(index));
            }
        } else if (selectedListIds.size() == 1){
            rowsId = "_id = " + selectedListIds.get(0);
        } else {
            rowsId = "_id = 0";
        }
        return rowsId;
    }

    //TODO: init/reload Groups, Children and Lists
    public ArrayList<GroupItemModel> loadGroupsChildrenAndListsForERVAdapter(Activity activity)
    {
        Cursor cursorGroups = activity.getContentResolver().query(DBProvider.GROUPS_TABLE_PATH_URI, null, null, null, null);
        Cursor cursorChildren = activity.getContentResolver().query(DBProvider.CHILDREN_TABLE_PATH_URI, null, null, null, null);;
        groups = new ArrayList();
        int id;
        while (cursorGroups.moveToNext())
        {
            String groupValueGroupTable = cursorGroups.getString(cursorGroups.getColumnIndex(DBOpenHelper.COLUMN_GROUP));
            String listValueGroupTable = cursorGroups.getString(cursorGroups.getColumnIndex(DBOpenHelper.COLUMN_LIST));
            ArrayList<ChildItemModel> childrenList = new ArrayList();
            while (cursorChildren.moveToNext())
            {
                String groupValueChildrenTable = cursorChildren.getString(cursorChildren.getColumnIndex(DBOpenHelper.COLUMN_GROUP));
                String childValueChildrenTable = cursorChildren.getString(cursorChildren.getColumnIndex(DBOpenHelper.COLUMN_CHILD));
                id = cursorChildren.getInt(cursorChildren.getColumnIndex(DBOpenHelper.COLUMN_ID));
                if (groupValueGroupTable != null && groupValueGroupTable.equals(groupValueChildrenTable))
                {
                    childrenList.add(new ChildItemModel(childValueChildrenTable, id));
                }
            } cursorChildren.moveToPosition(-1);

            id = cursorGroups.getInt(cursorGroups.getColumnIndex(DBOpenHelper.COLUMN_ID));
            if (groupValueGroupTable != null)
            {
                groups.add(new GroupItemModel(groupValueGroupTable, childrenList, id, true));
            } else {
                groups.add(new GroupItemModel(listValueGroupTable, childrenList, id, false));
            }
        }
        return groups;
    }

    @Override //TODO: calling from AdapterERV -> item From List Clicked (Drawer or Dialog)
    public void itemClicked(String expandedGroupTitle, String selectedChildTitle, String listTitle, String passedFrom, boolean isForAction, int id)
    {
        if (isForAction)
        {
            //TODO: show selected listDB action
            if (passedFrom.equals(ACTION_FROM_DRAWER_LAYOUT))
            {
                expandedGroupNameDL = expandedGroupTitle;
                selectedChildTitleDL = selectedChildTitle;
                selectedListTitleDL = listTitle;
                toolbarTitle = listTitle != null ? listTitle : selectedChildTitle;
                String columnIndex = listTitle != null ? DBOpenHelper.COLUMN_LIST : DBOpenHelper.COLUMN_CHILD;
                selectionForDBQuery = columnIndex + " LIKE " + "'%" + toolbarTitle + "%'";
                calendarModeBTNChangeState(false);
                initRelevantModeAdapter();
                drawerLayout.closeDrawers();
            //TODO: move to action
            } else if (passedFrom.equals(MAIN_ACTIVITY))
            {
                selectedGroupTitleSLV = expandedGroupTitle;
                selectedChildTitleSLV = selectedChildTitle;
                selectedListTitleSLV = listTitle;
                selectListView.itemClickedInSLV();
            }
        }
    }

    public void loadArraysWithRelevantIds(String selection) {
        selectedListIdsToDo = new ArrayList<>();
        cursor = activity.getContentResolver().query(DBProvider.TODO_TABLE_PATH_URI, null, selection, null, null);
        while (cursor.moveToNext()){
            selectedListIdsToDo.add(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID)));
        }
        selectedListIdsChecked = new ArrayList<>();
        cursor = activity.getContentResolver().query(DBProvider.CHECKED_TABLE_PATH_URI, null, selection, null, null);
        while (cursor.moveToNext()){
            selectedListIdsChecked.add(cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID)));
        }
    }

    public void setCalModeViewsVisibility(boolean isOnCalendarViewClicked)
    {
        if (isOnCalendarViewClicked)
        {
            calendar_ll.setVisibility(View.VISIBLE);
            recyclerViewDays.setVisibility(View.VISIBLE);
        } else {
            recyclerViewDays.setVisibility(View.GONE);
            calendar_ll.setVisibility(View.GONE);
        }
    }

    public void setDaysAdapterAndSnap()
    {
        recyclerViewDays.setAdapter(new AdapterDaysRV(daysArrayList));
        snapHelper = new SnapHelper(firstPosition);
        snapHelper.findSnapView(linearLayoutDays);
    }

    //TODO: checking if MAIN GROUPS RV (ToDo || Checked) lists not empty set relevant TITLE
    public void setListsTitlesVisible()
    {
        if (adapterChecked.getItemCount() > 0)
        {
            listsTitleTextChecked.setVisibility(View.VISIBLE);
        } else {
            listsTitleTextChecked.setVisibility(View.GONE);
        }
        if (adapterToDo.getItemCount() > 0)
        {
            listsTitleTextToDo.setVisibility(View.VISIBLE);
        } else {
            listsTitleTextToDo.setVisibility(View.GONE);
        }
    }

    public void dispatchSelected()
    {
        recyclerViewDays.dispatchSetSelected(false);
    }

    public void setNotificationAlarm(Context context, String notificationTitle, String dateTime, int requestCode, int idToDo, Calendar calendar, String repeatAction, ArrayList<Integer> selectedDays)
    {
        Intent notificationReceiverIntent = new Intent(context, NotifierNotificationReceiver.class);
        notificationReceiverIntent.putExtra(REMINDER_TEXT, notificationTitle);
        notificationReceiverIntent.putExtra(REQUEST_CODE, requestCode);
        notificationReceiverIntent.putExtra(ID_TO_DO, idToDo);
        notificationReceiverIntent.putExtra(REPEAT_ACTION, repeatAction);
        notificationReceiverIntent.putExtra(DATE_TIME, dateTime);
        notificationReceiverIntent.putExtra(REPEAT_CUSTOM_DAYS_ARRAY, selectedDays);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, idToDo, notificationReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    public void calendarModeBTNChangeState(boolean isOnCalendarViewClicked)
    {
        calAndSeqBtn_RL.setSelected(true);
        int id = isOnCalendarViewClicked ? R.mipmap.main_icon : R.mipmap.calendar;
        calAndSeq_IV.setImageResource(id);
        setCalModeViewsVisibility(isOnCalendarViewClicked);
        this.isOnCalendarMode = isOnCalendarViewClicked;
    }

    public void loadArraysWithSelectedDayItemsIds(Calendar calendar)
    {
        setCalNoTD = calendar;
        Calendar dayModelCalendar;
        selectedListIdsToDo = new ArrayList<>();
        selectedListIdsChecked = new ArrayList<>();
        ArrayList<DayModel> allItemsWithDateInDB = calendarConverter.getItemsDatesContainFromDBConvertedArray(activity);

        for (DayModel dayModel : allItemsWithDateInDB)
        {
            dayModelCalendar = new GregorianCalendar(dayModel.getCalendar().get(Calendar.YEAR), dayModel.getCalendar().get(Calendar.MONTH), dayModel.getCalendar().get(Calendar.DAY_OF_MONTH), 0, 0,0);
            if (dayModelCalendar.compareTo(calendar) == 0)
            {
                int idToDo = dayModel.getIdToDo();
                int idChecked = dayModel.getIdChecked();
                if (idToDo != -1) {
                    selectedListIdsToDo.add(idToDo);
                } else {
                    selectedListIdsChecked.add(idChecked);
                }
            }
        }
    }

    private void loadArraysOnInput() {
        cursor = activity.getContentResolver().query(DBProvider.TODO_TABLE_PATH_URI, null, setStringIdsForDB(selectedListIdsToDo), null, null);
        selectedListIdsToDo = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String reminderText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER)).toLowerCase();
            int id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
            if (reminderText.contains(searchInputText_ET)) { selectedListIdsToDo.add(id); }
        }

        cursor = activity.getContentResolver().query(DBProvider.CHECKED_TABLE_PATH_URI, null, setStringIdsForDB(selectedListIdsChecked), null, null);
        selectedListIdsChecked = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String reminderText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER)).toLowerCase();
            int id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COLUMN_ID));
            if (reminderText.contains(searchInputText_ET)) { selectedListIdsChecked.add(id); }
        }
    }

    public void dayItemClicked(Calendar gCalendar) {
        setCalNoTD = gCalendar;
        initRelevantModeAdapter();
    }

    //TODO: show selected List
    public void rebindRemindersCursors(String cursorSelectionToDo, String cursorSelectionChecked)
    {
        cursor = activity.getContentResolver().query(DBProvider.TODO_TABLE_PATH_URI ,null, cursorSelectionToDo, null,null,null);
        adapterToDo.reBindCursor(cursor);
        cursor = activity.getContentResolver().query(DBProvider.CHECKED_TABLE_PATH_URI ,null, cursorSelectionChecked, null,null,null);
        adapterChecked.reBindCursorChecked(cursor);
        recyclerViewToDo.setAdapter(adapterToDo);
        recyclerViewChecked.setAdapter(adapterChecked);
        setListsTitlesVisible();
    }

    /** init relevant mode adapter like: Search Mode, Calendar Mode, Sequence Mode; Setting relevant Toolbar Title*/
    public void initRelevantModeAdapter(){
        if (isOnSearchMode) {
            if (isOnCalendarMode)
            {
                toolbarCustom.setCalendarViewToolbar(calendarConverter.getSelectedDateString(setCalNoTD.get(Calendar.DAY_OF_WEEK), setCalNoTD.get(Calendar.MONTH)));
                if (searchInputText_ET.equals("")) {
                    loadArraysWithSelectedDayItemsIds(setCalNoTD);
                    rebindRemindersCursors(setStringIdsForDB(selectedListIdsToDo), setStringIdsForDB(selectedListIdsChecked));
                } else {
                    loadArraysWithSelectedDayItemsIds(setCalNoTD);
                    loadArraysOnInput();
                    rebindRemindersCursors(setStringIdsForDB(selectedListIdsToDo), setStringIdsForDB(selectedListIdsChecked));
                }
            } else {
                toolbarCustom.setSequenceViewToolbar(toolbarTitle);
                if (searchInputText_ET.equals("") && ToolbarView.titleTV.getText().toString().equals(activity.getString(R.string.all_reminders))) {
                    rebindRemindersCursors(null, null);

                } else if(!searchInputText_ET.equals("") && ToolbarView.titleTV.getText().toString().equals(activity.getString(R.string.all_reminders)))
                {
                    String selection = DBOpenHelper.COLUMN_REMINDER + " LIKE " + "'%" + searchInputText_ET + "%'";
                    rebindRemindersCursors(selection, selection);
                } else if (searchInputText_ET.equals("") && !ToolbarView.titleTV.getText().toString().equals(activity.getString(R.string.all_reminders))){
                    rebindRemindersCursors(selectionForDBQuery, selectionForDBQuery);
                } else {
                    String selection = selectionForDBQuery + " AND " + DBOpenHelper.COLUMN_REMINDER + " LIKE " + "'%" + searchInputText_ET + "%'";
                    rebindRemindersCursors(selection, selection);
                }
            }
        } else if (isOnCalendarMode){
            toolbarCustom.setCalendarViewToolbar(calendarConverter.getSelectedDateString(setCalNoTD.get(Calendar.DAY_OF_WEEK), setCalNoTD.get(Calendar.MONTH)));
            loadArraysWithSelectedDayItemsIds(setCalNoTD);
            rebindRemindersCursors(setStringIdsForDB(selectedListIdsToDo), setStringIdsForDB(selectedListIdsChecked));
        } else {
            toolbarCustom.setSequenceViewToolbar(toolbarTitle);
            if (ToolbarView.titleTV.getText().toString().equals(activity.getString(R.string.all_reminders))){
                rebindRemindersCursors(null, null);
            } else {
                loadArraysWithRelevantIds(selectionForDBQuery);
                rebindRemindersCursors(selectionForDBQuery, selectionForDBQuery);
            }
        }
    }
}
