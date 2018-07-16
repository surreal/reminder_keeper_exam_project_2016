package com.reminder_keeper.adapters.AdaptersRV;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Andrey on 07/07/2017.
 */

public abstract class ViewHolderRV_Abstract extends RecyclerView.ViewHolder
{

    public ViewHolderRV_Abstract(View itemView)
    {super(itemView);}

    public abstract void bindCursor(Cursor cursor);
}
