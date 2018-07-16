package com.reminder_keeper.adapters.AdapterERV.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GroupItemModel extends ExpandableGroup
{
    private int id;
    private int flatPosition;
    private boolean isGroup;

    public GroupItemModel(String title, List items, int id, boolean isGroup)
    { super(title, items);
        this.isGroup = isGroup;
        this.id = id;
    }

    public int getId() { return id; }
    public boolean isGroup() { return isGroup; }

    public void setFlatPosition(int flatPosition) {
        this.flatPosition = flatPosition;
    }

}
