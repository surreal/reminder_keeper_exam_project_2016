package com.reminder_keeper.adapters.AdapterERV.models;

/**
 * Created by Andrey on 30/07/2017.
 */

public class ChildItemModel
{
    private String title;
    private int id;

    public ChildItemModel(String title, int id)
    {
        this.title = title;
        this.id = id;
    }

    public String getTitle() { return title; }
    public int getId() { return id; }
}
