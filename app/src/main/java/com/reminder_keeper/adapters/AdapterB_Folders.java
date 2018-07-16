package com.reminder_keeper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.reminder_keeper.R;

import java.util.ArrayList;

/**
 * Created by Andrey on 03/08/2017.
 */

public class AdapterB_Folders extends BaseAdapter
{
    ArrayList<String> foldersList;
    Context context;

    public AdapterB_Folders(Context context, ArrayList<String> foldersList)
    {
        this.context = context;
        this.foldersList = foldersList;
    }

    @Override
    public int getCount() {
        return foldersList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = LayoutInflater.from(context).inflate(R.layout.item_view_select_folder, null, false);
        TextView textView = (TextView) view.findViewById(R.id.select_folder_item_view_TV);
        textView.setText(foldersList.get(i));
        return view;
    }
}
