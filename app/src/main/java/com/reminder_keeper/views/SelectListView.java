package com.reminder_keeper.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.reminder_keeper.activities.MainActivity;
import com.reminder_keeper.activities.ReminderActivity;
import com.reminder_keeper.adapters.AdapterERV.AdapterERV;
import com.reminder_keeper.CursorsDBMethods;
import com.reminder_keeper.listeners.NewListBtnClickListener;
import com.reminder_keeper.R;

public class SelectListView implements DialogInterface.OnDismissListener
{
    public static final String SELECT_LIST_VIEW = "selectListView";
    private static RecyclerView recyclerViewSelectListView;
    public static AlertDialog selectListViewDialog;
    private CursorsDBMethods cursors;
    private Activity activity;
    private String requestFrom;
    private final MainActivity mainActivity;

    public SelectListView(Activity activity, String requestFrom)
    {
        this.activity = activity;
        this.requestFrom = requestFrom;
        cursors = new CursorsDBMethods(activity);
        cursors.getCursorGroups();
        cursors.getCursorChildren();
        mainActivity = new MainActivity();
    }

    public void setAdapter()
    {
        mainActivity.loadGroupsChildrenAndListsForERVAdapter();
        recyclerViewSelectListView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewSelectListView.setAdapter(new AdapterERV(MainActivity.groups, activity, requestFrom, false));
    }

    //TODO: init selectList View
    public void initListViewDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        selectListViewDialog = alertDialogBuilder.create();
        final View selectListView = LayoutInflater.from(activity).inflate(R.layout.dialog_select_list, null, false);
        selectListViewDialog.setView(selectListView);
        recyclerViewSelectListView = (RecyclerView) selectListView.findViewById(R.id.select_list_view_recycler_view);
        Button newListButton = (Button) selectListView.findViewById(R.id.select_list_view_create_new_list_button);
        newListButton.setOnClickListener(new NewListBtnClickListener(activity, SELECT_LIST_VIEW));

        final LinearLayout unclassifiedLLayout = (LinearLayout) selectListView.findViewById(R.id.select_list_view_layout_unclassified);
        unclassifiedLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unclassifiedLLayout.setSelected(true);
                if (requestFrom.equals(MainActivity.MAIN_ACTIVITY))
                {
                    MainActivity.expandedGroupNameSLV = null;
                    MainActivity.selectedChildNameSLV = null;
                    MainActivity.selectedListSLV = activity.getString(R.string.unclassified);
                    itemClickedInSLV();
                } else if (requestFrom.equals(ReminderActivity.REMINDER_ACTIVITY))
                {
                    ToolbarView.titleTV.setText(R.string.unclassified);
                    selectListViewDialog.dismiss();
                }
            }

        });
        selectListViewDialog.setOnDismissListener(this);
        setAdapter();
        selectListViewDialog.show();
    }

    public void itemClickedInSLV()
    {
        cursors.moveToDB(MainActivity.idToDoNoteItem, MainActivity.idCheckedNoteItem, false);
        mainActivity.initRelevantAdapter(MainActivity.setCalNoTD);
        selectListViewDialog.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface)
    {
        if (requestFrom.equals(MainActivity.MAIN_ACTIVITY))
        {
            mainActivity.initRelevantAdapter(MainActivity.setCalNoTD);
        }
    }
}
