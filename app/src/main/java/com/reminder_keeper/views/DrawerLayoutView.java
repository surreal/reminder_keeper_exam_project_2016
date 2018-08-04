package com.reminder_keeper.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reminder_keeper.activities.AccountActivity;
import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.activities.SettingsActivity;
import com.reminder_keeper.adapters.AdapterERV.AdapterERV;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.listeners.NewListBtnClickListener;
import com.reminder_keeper.R;
import com.reminder_keeper.SignIn;

public class DrawerLayoutView extends MainActivity
{
    public static final String DRAWER_LAYOUT_VIEW = "drawerLayoutView";
    private SignIn signIn;
    public static AdapterERV adapterERV;
    public static int countOfNotifications;
    private Activity activity;
    public Button newListButton;

    public DrawerLayoutView(Activity activity)
    {
        this.activity = activity;
        castings();
        cursors = new CursorsDBMethods(activity);
        signIn = new SignIn(activity, null);
        signIn.checkIfAccountLogged(profileNameTV, profileEmailTV, profileImageIV);
        countOfNotifications();
        initListeners();
    }

    //TODO: castings
    private void castings()
    {
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.activity_main_drawer_Layout);
        newListButton = (Button) activity.findViewById(R.id.drawer_layout_button_new_list);
        numOfNotificationsTV = (TextView) activity.findViewById(R.id.drawer_layout_num_of_notifications);
        linearLayoutUnclassifiedList = (LinearLayout) activity.findViewById(R.id.drawer_layout_linear_layout_unclassified);
        recyclerViewGAndL = (RecyclerView) activity.findViewById(R.id.drawer_layout_recycler_view_folders);
        rLayoutAllNotes = (RelativeLayout) activity.findViewById(R.id.drawer_layout_all_notes_linear_layout_all_notes);
        settingsIV = (ImageView) activity.findViewById(R.id.drawer_layout_profile_settings);
        profileNameTV = (TextView) activity.findViewById(R.id.drawer_layout_profile_name);
        profileEmailTV = (TextView) activity.findViewById(R.id.drawer_layout_profile_email);
        profileImageIV = (ImageView) activity.findViewById(R.id.drawer_layout_profile_image);
    }

    //TODO: init listeners
    public void initListeners()
    {
        profileImageIV.setOnClickListener(onElementsClickListener);
        settingsIV.setOnClickListener(onElementsClickListener);
        linearLayoutUnclassifiedList.setOnClickListener(onElementsClickListener);
        rLayoutAllNotes.setOnClickListener(onElementsClickListener);
        newListButton.setOnClickListener(new NewListBtnClickListener(activity, DRAWER_LAYOUT_VIEW));
        numOfNotificationsTV.setText(countOfNotifications + "");
    }

    //TODO: set adapters
    public void setDrawerAdapterERV()
    {
        loadGroupsChildrenAndListsForERVAdapter();
        adapterERV = new AdapterERV(groups, activity, ACTION_FROM_DRAWER_LAYOUT,false);
        recyclerViewGAndL.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewGAndL.setAdapter(adapterERV);
        signIn.checkIfAccountLogged(profileNameTV, profileEmailTV, profileImageIV);
    }

    //TODO count of Notes
    public void countOfNotifications()
    {
        countOfNotifications = cursors.getCursorToDo().getCount() + cursors.getCursorChecked().getCount();
    }

    //TODO: on views click listener
    View.OnClickListener onElementsClickListener = new View.OnClickListener()
    {   @Override
        public void onClick(View view)
        {
            if (view == linearLayoutUnclassifiedList) {
                selectedListTitleDL = UNCLASSIFIED;
                expandedGroupNameDL = null;
                selectedChildTitleDL = null;
                toolbarCustom.setSequenceViewToolbar(activity.getString(R.string.unclassified));
                calendarModeBTNChangeState(false);
                runOnTableLookListsOrChildrenToShow(false);
                setCalModeViewsVisibility(false);
                adapterERV.selectUnselectItemView(linearLayoutUnclassifiedList);
                drawerLayout.closeDrawers();
            } else if (view == rLayoutAllNotes) {
                selectedListTitleDL = activity.getString(R.string.all_notes);
                expandedGroupNameDL = null;
                selectedChildTitleDL = null;
                rebindCursorsSetMainRVs();
                setCalModeViewsVisibility(false);
                toolbarCustom.setSequenceViewToolbar(activity.getString(R.string.all_notes));
                calendarModeBTNChangeState(false);
                adapterERV.selectUnselectItemView(null);
                drawerLayout.closeDrawers();
            } else if (view == profileImageIV) {
                activity.startActivityForResult(new Intent(activity, AccountActivity.class), 1);
            } else if (view == settingsIV) {
                activity.startActivityForResult(new Intent(activity, SettingsActivity.class), 1);
                finish();
            }
        }
    };
}
