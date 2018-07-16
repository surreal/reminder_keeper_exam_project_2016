package com.reminder_keeper.adapters.CursorAdaptersRV.CheckedRV;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reminder_keeper.adapters.AdaptersRV.ViewHolderRV_Abstract;

public abstract class CursorAdapterRV_CheckedAbstract<VHC extends ViewHolderRV_Abstract> extends RecyclerView.Adapter<VHC>
{
    protected Activity activity;
    protected static CursorAdapter cursorAdapterChecked;
    private VHC viewHolderChecked;

    public void setupCursorAdapterChecked(Cursor cursor, final int resource)
    {
        cursorAdapterChecked = new CursorAdapter(activity, cursor, 0)
        {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent)
            {
                return LayoutInflater.from(context).inflate(resource, parent,false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor)
            {
                viewHolderChecked.bindCursor(cursor);
            }
        };
    }

    protected void setViewHolderChecked(VHC viewHolderChecked)
    {this.viewHolderChecked = viewHolderChecked;}

    public void reBindCursorChecked(Cursor cursor)
    {
        cursorAdapterChecked.swapCursor(cursor);
        notifyDataSetChanged();
    }

}
