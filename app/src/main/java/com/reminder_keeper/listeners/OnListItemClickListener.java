package com.reminder_keeper.listeners;

/**
 * Created by Andrey on 17/09/2017.
 */

public interface OnListItemClickListener
{
    void itemClicked(String expandedGroupTitle, String selectedChildTitle, String listTitle, String passedFrom, boolean isForAction, int id);
}
