package com.reminder_keeper.adapters.AdaptersRV.Models;

import android.support.annotation.NonNull;

import java.util.Calendar;

public class DayModel implements Comparable
{
    private boolean isHaveNotification;
    private boolean isSelected;
    private Calendar calendar;
    private int idToDo;
    private int idChecked;
    private String note;
    private int position;

    public DayModel(Calendar calendar, int idToDo, int idChecked, boolean isHaveNotification, boolean isSelected, String note, int position)
    {
        this.position = position;
        this.calendar = calendar;
        this.isHaveNotification = isHaveNotification;
        this.isSelected = isSelected;
        this.idToDo = idToDo;
        this.idChecked = idChecked;
        this.note = note;
    }

    public Calendar getCalendar() { return calendar; }
    public int getIdToDo() { return idToDo; }
    public int getIdChecked() { return idChecked; }
    public boolean isHaveNotification() { return isHaveNotification; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public String getNote() { return note; }
    public int getPosition() { return position; }

    @Override
    public int compareTo(@NonNull Object o) {
        if (position == -1) {
            return getCalendar().compareTo(((DayModel) o).getCalendar());
        } else {
            return getPosition() - (((DayModel) o).getPosition());
        }
    }

}
