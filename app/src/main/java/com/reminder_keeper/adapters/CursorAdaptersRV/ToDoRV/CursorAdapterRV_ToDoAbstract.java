package com.reminder_keeper.adapters.CursorAdaptersRV.ToDoRV;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reminder_keeper.adapters.AdaptersRV.ViewHolderRV_Abstract;

public abstract class CursorAdapterRV_ToDoAbstract<VH extends ViewHolderRV_Abstract> extends RecyclerView.Adapter<VH>
{
    private VH viewHolder;
    protected Activity activity;
    protected static CursorAdapter cursorAdapterToDo;

    protected CursorAdapterRV_ToDoAbstract(Activity activity){this.activity = activity;}

    protected void setupCursorAdapter(Cursor cursor, final int resource)
    {
        cursorAdapterToDo = new CursorAdapter(activity, cursor, 0)
        {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent)
            {
                return LayoutInflater.from(context).inflate(resource, parent,false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor)
            {
                viewHolder.bindCursor(cursor);
            }
        };
    }

    protected void setViewHolder(VH viewHolder)
    { this.viewHolder = viewHolder; }

    public void reBindCursor(Cursor cursor)
    {
        this.cursorAdapterToDo.swapCursor(cursor);
        notifyDataSetChanged();
    }

}
