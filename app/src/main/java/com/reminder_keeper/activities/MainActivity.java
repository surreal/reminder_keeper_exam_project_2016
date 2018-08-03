package com.reminder_keeper.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reminder_keeper.broadcasts.NotifierNotificationReceiver;
import com.reminder_keeper.broadcasts.NotificationItemBroadcastReceiver;
import com.reminder_keeper.views.ToolbarView;
import com.reminder_keeper.adapters.AdaptersRV.Models.DayModel;
import com.reminder_keeper.AuthorityClass;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.adapters.SnapHelper;
import com.reminder_keeper.CalendarConverter;
import com.reminder_keeper.views.DrawerLayoutView;
import com.reminder_keeper.views.SelectListView;
import com.reminder_keeper.R;
import com.reminder_keeper.data_base.DBOpenHelper;
import com.reminder_keeper.data_base.DBProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class MainActivity extends AuthorityClass
{
    public static final String MAIN_ACTIVITY = "MainActivity";
    public static Activity activity;
    public static String toolbarTitleString;
    public static boolean isRTL;
    private Cursor cursor;
    private Button createAccountMV_Btn;
    private ActionBarDrawerToggle toggle;
    private DrawerLayoutView drawerLayoutView;
    private boolean isOnSearchMode;
    private AutoCompleteTextView search_ACTV;
    private RelativeLayout searchACTV_RL, lupeBtn_RL, lupeACTV_IV;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    { super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onCreateActions();
        setAllConfiguredAlarms();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toolbarTitleString = ToolbarView.titleTV.getText().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();

        new BackupManager(getApplicationContext()).dataChanged();
        Intent onStartIntent = new Intent(activity, NotificationItemBroadcastReceiver.class).putExtra("isForStartIntent", true);
        sendBroadcast(onStartIntent);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_custom));
        toolbarCustom = new ToolbarView(this, getSupportActionBar(), MAIN_ACTIVITY);
        if (toolbarTitleString != null) {
            ToolbarView.titleTV.setText(toolbarTitleString);
        }

        drawerLayoutView.setDrawerAdapterERV();
        setLoginButtonMainViewVisibility();
        initRelevantAdapter(setCalNoTD);
    }

    private void onCreateActions() {
        activity = this;
        toolbarTitleString = null;
        setCalNoTD = new GregorianCalendar();
        isOnCalendarMode = false;
        Configuration configuration = getResources().getConfiguration();
        if (configuration.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) { isRTL = true; }
        castings();
        drawerLayoutView = new DrawerLayoutView(activity);
        setNotificationsRVLayouts();
        initToggle();
        rebindCursorsSetRV();
        setListsTitlesVisible();
        initSwipe();
        calendarConverter = new CalendarConverter(this);
        daysArrayList = isRTL ? loadDaysArrayMapRTL() : loadDaysArrayMapDefault();
        initDaysRVAndSnap();
    }

    //TODO: castings
    private void castings() {
        createAccountMV_Btn = (Button) findViewById(R.id.activity_main_create_account_button);
        listsTitleTextChecked = (TextView) findViewById(R.id.listTitleTextChecked);
        listsTitleTextToDo = (TextView) findViewById(R.id.listTitleTextToDo);
        recyclerViewToDo = (RecyclerView) findViewById(R.id.activity_main_recycler_view_todo);
        recyclerViewChecked = (RecyclerView) findViewById(R.id.RecyclerViewChecked);
        calAndSeqBtn_RL = (RelativeLayout) findViewById(R.id.activity_main_calendar_vew_rv);
        calendar_ll = (LinearLayout) findViewById(R.id.activity_main_calendar_vew_ll);
        recyclerViewDays = (RecyclerView) findViewById(R.id.activity_main_days_recycler_view);
        calAndSeq_IV = (ImageView) findViewById(R.id.activity_main_change_view_iv);
        lupeACTV_IV = (RelativeLayout) findViewById(R.id.activity_main_actv_lupe);
        search_ACTV = (AutoCompleteTextView) findViewById(R.id.activity_main_search_actv);
        lupeBtn_RL = (RelativeLayout) findViewById(R.id.activity_main_search_btn_rv);
        searchACTV_RL = (RelativeLayout) findViewById(R.id.activity_main_search_actv_rv);
        calAndSeqBtn_RL.setOnClickListener(onChangeModeButtonsClickListener);
        lupeBtn_RL.setOnClickListener(onChangeModeButtonsClickListener);
        lupeACTV_IV.setOnClickListener(onChangeModeButtonsClickListener);
    }

    /*** on change view actions */
    View.OnClickListener onChangeModeButtonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.activity_main_calendar_vew_rv:
                    isOnCalendarMode = !isOnCalendarMode;
                    calendarModeBTNChangeState(isOnCalendarMode);
                    setCalModeViewsVisibility(isOnCalendarMode);
                    onChangeModeBtnClickedInitRelevantAdapter(true);
                    String relevantSearchHint = isOnCalendarMode ? getString(R.string.search_in_selected_date) : getString(R.string.search_in_selected_list);
                    search_ACTV.setHint(relevantSearchHint);
                    break;
                case R.id.activity_main_search_btn_rv:
                    searchACTV_RL.setSelected(true);
                    searchModeBtnClicked();
                    break;
                case R.id.activity_main_actv_lupe:
                    lupeACTV_IV.setSelected(true);
                    addSearchRequestTextToTable();
                    setACTVArrayAdapter();
                    searchInputText_ACTV = search_ACTV.getText().toString().trim().toLowerCase().trim();
                    if (!searchInputText_ACTV.equals("")) { runOnTableLookListsOrChildrenToShow(true); }
                    break;
            }
        }
    };

    private void onChangeModeBtnClickedInitRelevantAdapter(boolean isCalendarClicked) {
        if (isOnCalendarMode) {
            setDaysAdapterAndSnap();
            loadAndShowSelectedDayItems(CalendarConverter.currentCalNoTD);
        } else {
            if (isCalendarClicked) {
                toolbarCustom.setSequenceViewToolbar(getString(R.string.all_notes));
                rebindCursorsSetRV();
            } else {
                if (ToolbarView.titleTV.getText().toString().equals(getString(R.string.all_notes))) {
                    toolbarCustom.setSequenceViewToolbar(getString(R.string.all_notes));
                    rebindCursorsSetRV();
                } else {
                    runOnTableLookListsOrChildrenToShow(false);
                }
            }
        }
    }

    private void initDaysRVAndSnap() {
        linearLayoutDays = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, isRTL);
        recyclerViewDays.setLayoutManager(linearLayoutDays);
        new SnapHelper(firstPosition).attachToRecyclerView(recyclerViewDays);
    }

    private ArrayList<DayModel> loadDaysArrayMapRTL() {
        daysArrayList = loadDaysArrayMapDefault();
        Collections.reverse(daysArrayList);
        Calendar currentCal = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        for (int i = 1;  i < daysArrayList.size(); i++) {
            if (daysArrayList.get(i).getCalendar().compareTo(currentCal) == 0) {
                firstPosition = daysArrayList.size() - i;

                if (daysArrayList.get(i).getCalendar().get(Calendar.DAY_OF_WEEK) != 7) {
                    for (int j = daysArrayList.get(i).getCalendar().get(Calendar.DAY_OF_WEEK); j > 1; j--) {
                        firstPosition--;
                    }
                } else {
                    firstPosition -=6;
                }
            }
        }
        firstPosition = daysArrayList.size() - firstPosition -6;
        return daysArrayList;
    }

    /**
     * loads 3 years array for adapter
     * properties for items:
     * key - positions for snap;
     * dayOfYear, dayOfMonth, monthForDB, year, isHaveNotifications;
     * checks every position if isHaveNotifications for adapter to highlight;
     * sets first position for snap;
     */
    private ArrayList<DayModel> loadDaysArrayMapDefault() {
        ArrayList<DayModel> datesFromDBConvertedArray = calendarConverter.getItemsDatesContainFromDBConvertedArray(activity);
        ArrayList<DayModel> daysModelsArrayList = new ArrayList<>();
        Calendar calIsSet = new GregorianCalendar();
        int dayOfYearFirstSunday = 0;
        int dayOfYearLastSaturday = 0;
        int keyPosition = -1;
        boolean isHaveNotification = false;

        /* set first sunday */
        for (int i = 1; i <= 7; i++) {
            calIsSet = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) - 1, 0, 0, 0, 0, 0);
            calIsSet.set(Calendar.DAY_OF_YEAR, i);
            if (calIsSet.get(Calendar.DAY_OF_WEEK) == 1) {
                dayOfYearFirstSunday = i;
                break;
            }
        }

        /* add to array past year */
        for (int i = dayOfYearFirstSunday; i <= calIsSet.getActualMaximum(Calendar.DAY_OF_YEAR); i++) {
            keyPosition += 1;
            calIsSet = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) - 1, 0, 0, 0, 0, 0);
            calIsSet.set(Calendar.DAY_OF_YEAR, i);
            for (DayModel model : datesFromDBConvertedArray) {
                if (calIsSet.compareTo(model.getCalendar()) == 0) {
                    isHaveNotification = true;
                }
            }
            daysModelsArrayList.add(new DayModel(calIsSet, -1, -1, isHaveNotification, false, null, keyPosition));
            isHaveNotification = false;
        }

        /* add to array current year */
        calIsSet = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), 0, 0, 0, 0, 0);
        for (int i = 1; i <= calIsSet.getActualMaximum(Calendar.DAY_OF_YEAR); i++) {
            keyPosition += 1;
            calIsSet = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), 0, 0, 0, 0, 0);
            calIsSet.set(Calendar.DAY_OF_YEAR, i);
            for (DayModel model : datesFromDBConvertedArray) {
                Calendar modelCalendar = new GregorianCalendar(model.getCalendar().get(Calendar.YEAR), 0, 0, 0, 0, 0);
                modelCalendar.set(Calendar.DAY_OF_YEAR, model.getCalendar().get(Calendar.DAY_OF_YEAR));
                if (calIsSet.compareTo(modelCalendar) == 0) {
                    isHaveNotification = true;
                }
            }
            daysModelsArrayList.add(new DayModel(calIsSet, -1, -1, isHaveNotification, false, null, keyPosition));
            isHaveNotification = false;

            /* set first position for snap */
            if (i == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                firstPosition = keyPosition;
                if (calIsSet.get(Calendar.DAY_OF_WEEK) != 1) {
                    for (int j = 1; j < calIsSet.get(Calendar.DAY_OF_WEEK); j++) {
                        firstPosition -= 1;
                    }
                }
            }
        }

        /* set last saturday */
        calIsSet = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) + 1, 0, 0, 0, 0, 0);
        for (int i = calIsSet.getActualMaximum(Calendar.DAY_OF_YEAR); i >= calIsSet.getActualMaximum(Calendar.DAY_OF_YEAR) - 7; i--) {
            calIsSet = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) + 1, 0, 0, 0, 0, 0);
            calIsSet.set(Calendar.DAY_OF_YEAR, i);
            if (calIsSet.get(Calendar.DAY_OF_WEEK) == 7) {
                dayOfYearLastSaturday = i;
                break;
            }
        }

        /* add to array next year */
        for (int i = 1; i <= dayOfYearLastSaturday; i++) {
            keyPosition += 1;
            calIsSet = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR) + 1, 0, 0, 0, 0, 0);
            calIsSet.set(Calendar.DAY_OF_YEAR, i);
            for (DayModel model : datesFromDBConvertedArray) {
                Calendar modelCalendar = new GregorianCalendar(model.getCalendar().get(Calendar.YEAR) + 1, 0, 0, 0, 0, 0);
                modelCalendar.set(Calendar.DAY_OF_YEAR, model.getCalendar().get(Calendar.DAY_OF_YEAR));
                if (calIsSet.compareTo(model.getCalendar()) == 0) {
                    isHaveNotification = true;
                }
            }
            daysModelsArrayList.add(new DayModel(calIsSet, -1, -1, isHaveNotification, false, null, keyPosition));
            isHaveNotification = false;
        }
        return daysModelsArrayList;
    }


    //TODO: initToggle()
    public void initToggle() {
        toggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toggle);
    }

    //TODO: inflate actionBar elements
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //TODO: Set visible NEW button in Action Bar
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.icon).setVisible(true).setIcon(R.mipmap.plus).setTitle(getString(R.string.newNote));
        return super.onPrepareOptionsMenu(menu);
    }


    //TODO: onOpenDrawerLayout button click syncState icon
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    //TODO: on Action bar element click listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon:
                startActivity(new Intent(activity, ReminderActivity.class));
                finish();
                break;
        }

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: check if Account logged and set Login Buttons Visibility
    private void setLoginButtonMainViewVisibility() {
        SharedPreferences sharedPreferences = getSharedPreferences("userAuthentication", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", null);
        if ((userName == null) && ((cursors.getCursorToDo().getCount() > 0 || cursors.getCursorChecked().getCount() > 0))) {
            createAccountMV_Btn.setVisibility(View.GONE);
        } else if (userName == null) {
            createAccountMV_Btn.setVisibility(View.VISIBLE);
            createAccountMV_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { startActivityForResult(new Intent(activity, LoginActivity.class), 1); } });
        } else {
            createAccountMV_Btn.setVisibility(View.GONE);
        }
    }

    //TODO: called from Cursor_Adapters_RV item from ToDo or Checked clicked
    public void itemClicked(int itemIdToDo, int itemIdChecked) {
        Intent intent = new Intent(activity, ReminderActivity.class);
        if (itemIdToDo != -1) {
            intent.putExtra(ID_TO_DO, itemIdToDo);
        } else if (itemIdChecked != -1) {
            intent.putExtra(ID_CHECKED, itemIdChecked);
        }
        intent.putExtra("isActivityForResult", true);
        activity.startActivityForResult(intent, 1);
    }

    //TODO: called from CursorAdaptersRV(NOTES) on item action down
    public void onItemActionDown(int idDBToDo, int idDBChecked) {
        idToDoNoteItem = idDBToDo;
        idCheckedNoteItem = idDBChecked;
    }

    //TODO: init swipe
    private void initSwipe() {
        ItemTouchHelper.SimpleCallback itemTouchHelperSimpleCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    final AlertDialog deleteDialog = dialogBuilder.create();
                    View view = LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_delete, null, false);
                    view.findViewById(R.id.dialog_confirm_delete_delete_tv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            moveToRecyclerBin();
                            deleteAlarmNotification(idToDoNoteItem);
                            deleteDialog.dismiss();
                            //initRelevantAdapter(CalendarConverter.calIsSetNoTD);
                        }
                    });
                    deleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            initRelevantAdapter(CalendarConverter.calIsSetNoTD);
                        }
                    });
                    deleteDialog.setView(view);
                    deleteDialog.show();

                } else if (direction == ItemTouchHelper.RIGHT) {
                    selectListView = new SelectListView(activity, MAIN_ACTIVITY);
                    selectListView.initListViewDialog();
                }
            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                Bitmap icon;
                Paint paint = new Paint();
                RectF backgroundRF, iconDestRF;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View drawView = viewHolder.itemView;
                    int height = drawView.getBottom() - drawView.getTop();
                    int width = height / 3;

                    if (dX > 0) {
                        paint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
                        backgroundRF = new RectF(drawView.getLeft(), drawView.getTop(), dX, drawView.getBottom());
                        canvas.drawRect(backgroundRF, paint);
                        iconDestRF = new RectF
                                (
                                        drawView.getLeft() + width,
                                        drawView.getTop() + width,
                                        drawView.getLeft() + width * 2,
                                        drawView.getBottom() - width
                                );
                        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.silver_move_icon);
                        canvas.drawBitmap(icon, null, iconDestRF, paint);
                    } else if (dX < 0) {

                        paint.setColor(getResources().getColor(R.color.colorRed));
                        backgroundRF = new RectF(drawView.getRight() + dX, drawView.getTop(), drawView.getRight(), drawView.getBottom());
                        canvas.drawRect(backgroundRF, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.recycling_bin_empty);
                        iconDestRF = new RectF
                                (
                                        drawView.getRight() - width * 2,
                                        drawView.getTop() + width,
                                        drawView.getRight() - width,
                                        drawView.getBottom() - width
                                );
                        canvas.drawBitmap(icon, null, iconDestRF, paint);
                    }
                }
            }
        };
        ItemTouchHelper itemTouchHelperToDo = new ItemTouchHelper(itemTouchHelperSimpleCallBack);
        ItemTouchHelper itemTouchHelperChecked = new ItemTouchHelper(itemTouchHelperSimpleCallBack);
        itemTouchHelperToDo.attachToRecyclerView(recyclerViewToDo);
        itemTouchHelperChecked.attachToRecyclerView(recyclerViewChecked);
    }

    //TODO: move to recycling bin
    private void moveToRecyclerBin() {
        String where = idToDoNoteItem != -1 ? DBOpenHelper.COLUMN_ID + "=" + idToDoNoteItem : DBOpenHelper.COLUMN_ID + "=" + idCheckedNoteItem;
        Uri relevantUri = idToDoNoteItem != -1 ? DBProvider.TODO_TABLE_PATH_URI : DBProvider.CHECKED_TABLE_PATH_URI;
        cursor = CursorsDBMethods.cursor;
        cursor = getContentResolver().query(relevantUri, null, where, null, null);
        cursor.moveToFirst();
        String note = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REMINDER));
        String timeDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_DATE_TIME));
        String list = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_LIST));
        String group = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_GROUP));
        String child = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_CHILD));
        cursors.moveToRecyclingBin(group, child, list, note, timeDate);
        cursors.removeFromDB(idToDoNoteItem, idCheckedNoteItem);
        initRelevantAdapter(CalendarConverter.calIsSetNoTD);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else {
            finish();
        }
    }

    public void deleteAlarmNotification(int idToDo) {
        Intent notificationReceiverIntent = new Intent(activity, NotifierNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, idToDo, notificationReceiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    //TODO: method to run on save button clicked
    /*** generate the request code for notification */
    public int generateRequestCode(Context context)
    {
        int requestCode;
        SharedPreferences sharedPreferences = context.getSharedPreferences("requestCodeParam", MODE_PRIVATE);
        int requestCodeSP = sharedPreferences.getInt("requestCode", -1);
        if (requestCodeSP != -1) {
            requestCode = requestCodeSP +1;
        } else {
            requestCode = 1;
        }
        sharedPreferences.edit().putInt("requestCode", requestCode).apply();
        return requestCode;
    }

    public void setAllConfiguredAlarms()
    {
        int requestCode;
        ArrayList<DayModel> itemsWithTimeDateArray = calendarConverter.isHaveFutureTimeDateArray(this);
        ArrayList<Integer> customRepeatDaysArray;
        if (CalendarConverter.isHaveFutureTimeDate)
        {
            for (DayModel dayModel : itemsWithTimeDateArray)
            {
                requestCode = generateRequestCode(this);
                cursor = getContentResolver().query(DBProvider.TODO_TABLE_PATH_URI, null, DBOpenHelper.COLUMN_ID + "=" + dayModel.getIdToDo(), null, null);
                cursor.moveToFirst();
                String repeatOption = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REPEAT_OPTION));
                customRepeatDaysArray = repeatOption.equals(REPEAT_CUSTOM) ? convertDaysToIntArray(cursor) : null;
                setNotificationAlarm(this ,dayModel.getNote(), "", requestCode, dayModel.getIdToDo() , dayModel.getCalendar(), repeatOption, customRepeatDaysArray);
            }
        }
    }

    public ArrayList<Integer> convertDaysToIntArray(Cursor cursor) {
        ArrayList<Integer> selectedDaysForCustomRepeatArray = new ArrayList<>();
        String repeatCustomDays = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_REPEAT_CUSTOM_DAYS_OR_DATE));
        int currentDay;
        for (int i = 0; i < repeatCustomDays.length(); i++)
        {
            currentDay = Integer.parseInt(repeatCustomDays.substring(i, i +1));
            selectedDaysForCustomRepeatArray.add(currentDay);
        }
        return selectedDaysForCustomRepeatArray;
    }

    public void searchModeBtnClicked()
    {
        isOnSearchMode = !isOnSearchMode;
        if (isOnSearchMode) {
            toolbarTitleString = ToolbarView.titleTV.getText().toString();
            search_ACTV.setText("");
            if (isOnCalendarMode){
                search_ACTV.setHint(getString(R.string.search_in_selected_date));
            } else {
                search_ACTV.setHint(R.string.search_in_selected_list);
            }
            searchACTV_RL.setVisibility(View.VISIBLE);
        } else {
            searchACTV_RL.setVisibility(View.GONE);
            onChangeModeBtnClickedInitRelevantAdapter(false);
        }
    }

    private void addSearchRequestTextToTable()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.COLUMN_SEARCH_KEY, search_ACTV.getText().toString());
        getContentResolver().insert(DBProvider.SEARCH_TABLE_PATH_URI, contentValues);
    }

    private void setACTVArrayAdapter()
    {
        ArrayList<String> searchKeys = new ArrayList<>();
        cursors.getCursorSearchTable();
        while (CursorsDBMethods.cursorSearchKeys.moveToNext())
        {
            String searchKey = CursorsDBMethods.cursorSearchKeys.getString(CursorsDBMethods.cursorSearchKeys.getColumnIndex(DBOpenHelper.COLUMN_SEARCH_KEY));
            searchKeys.add(searchKey);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, searchKeys);
        search_ACTV.setAdapter(arrayAdapter);
    }
}