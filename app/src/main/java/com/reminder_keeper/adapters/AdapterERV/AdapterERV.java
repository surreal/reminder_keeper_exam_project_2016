
package com.reminder_keeper.adapters.AdapterERV;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reminder_keeper.activities.TheArrangeActivity;
import com.reminder_keeper.adapters.AdapterERV.models.GroupItemModel;
import com.reminder_keeper.listeners.OnListItemClickListener;
import com.reminder_keeper.R;
import com.reminder_keeper.adapters.AdapterERV.models.ChildItemModel;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.HashMap;
import java.util.List;

public class AdapterERV extends ExpandableRecyclerViewAdapter<AdapterERV.ViewHolderGroupItem, AdapterERV.ViewHolderChildItem>
{
    private Activity activity;
    private OnListItemClickListener listener;
    private String activityPassed;
    private TheArrangeActivity theArrangeActivity;
    private boolean isForDelete;
    public static HashMap<Integer, Integer> groupsFPosId;
    public static HashMap<Integer, Integer> childrenFPosId;
    private List<? extends ExpandableGroup> groups;
    private boolean isConstructorCalled;
    public static boolean isConstructorWithExpand;
    public static View selectedItemView;

    public AdapterERV(List<? extends ExpandableGroup> groups, Activity activity, String activityPassed, boolean isForDelete)
    {   super(groups);

        this.groups = groups;
        this.activity = activity;
        this.activityPassed = activityPassed;
        listener = (OnListItemClickListener) activity;
        this.isForDelete = isForDelete;
        theArrangeActivity = new TheArrangeActivity();

        isConstructorCalled = true;
        childrenFPosId = new HashMap<>();
        groupsFPosId = new HashMap<>();

        if (!activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)){ isConstructorWithExpand = false; }
    }

    @Override
    public int getItemCount()
    {
        return super.getItemCount();
    }
    @Override
    public ViewHolderGroupItem onCreateGroupViewHolder(ViewGroup parent, int viewType)
    {
        View mainItemView = LayoutInflater.from(activity).inflate(R.layout.item_view_group, parent, false);
        return new ViewHolderGroupItem(mainItemView);
    }
    @Override
    public ViewHolderChildItem onCreateChildViewHolder(ViewGroup parent, int viewType)
    {
        View childItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_child, parent, false);
        return new ViewHolderChildItem(childItemView);
    }

    @Override
    public void onBindChildViewHolder(ViewHolderChildItem holder, int flatPosition, ExpandableGroup group, int childIndex)
    {
        ChildItemModel childItemModel = (ChildItemModel) group.getItems().get(childIndex);
        holder.setChildTitle(childItemModel.getTitle());
        holder.setFlatPosition(flatPosition);
        holder.setId(childItemModel.getId());
        holder.setGroupTitle(group.getTitle());

        if (!TheArrangeActivity.isOnDrug)
        {
            if (childrenFPosId.containsKey(flatPosition)){ childrenFPosId.put(flatPosition, childItemModel.getId()); }
        }
    }

    @Override
    public void onBindGroupViewHolder(ViewHolderGroupItem holder, int flatPosition, ExpandableGroup group) {
        GroupItemModel groupItemModel = (GroupItemModel) group;
        holder.setTitle(group.getTitle());
        holder.setFlatPosition(flatPosition);
        holder.setId(groupItemModel.getId());
        holder.setType(groupItemModel.isGroup());
        holder.setGroup(group);
        if (activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)) {
            if (isConstructorCalled) { groupsFPosId.put(flatPosition, groupItemModel.getId()); }
        }
    }

    public int getGroupId(int flatPosition) {
        int id = -1;
        for (int i : groupsFPosId.keySet()) { if (i == flatPosition) { id = groupsFPosId.get(i); } }
        return id;
    }

    public int getChildId(int flatPosition) {
        int id = -1;
        for (int i : childrenFPosId.keySet()) { if (i == flatPosition) { id = childrenFPosId.get(i); } }
        return id;
    }

    //TODO: View Holder Child
    public class ViewHolderChildItem extends ChildViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        private TextView titleTV;
        private String childTitle;
        private int flatPosition;
        private int id;
        private String groupTitle;
        private ImageView xIV;
        private RelativeLayout rvOnX;

        public ViewHolderChildItem(View itemView)
        {   super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.item_view_child_tv);
            xIV = (ImageView) itemView.findViewById(R.id.item_view_child_x_iv);
            itemView.setOnClickListener(this);
            itemView.setId(id);
            if (activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY))
            {
                itemView.setOnLongClickListener(this);
                titleTV.setOnClickListener(this);
                titleTV.setOnLongClickListener(this);
                itemView.setBackgroundResource(R.drawable.item_main_view_selector);
                if (isForDelete)
                {
                    RelativeLayout.LayoutParams arrowXImageParams = new RelativeLayout.LayoutParams(30,30);
                    arrowXImageParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                    arrowXImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    arrowXImageParams.setMarginEnd(60);
                    xIV.setLayoutParams(arrowXImageParams);
                    rvOnX = (RelativeLayout) itemView.findViewById(R.id.item_view_child_on_x_rv);
                    rvOnX.setOnClickListener(this);
                    xIV.setVisibility(View.VISIBLE);
                } else {
                    xIV.setVisibility(View.GONE);
                }
            }
        }

        private void setFlatPosition(int flatPosition) { this.flatPosition = flatPosition; }
        private void setId(int id) { this.id = id; }
        private void setGroupTitle(String groupTitle) { this.groupTitle = groupTitle; }
        private void setChildTitle(String childTitle) {
            this.childTitle = childTitle;
            titleTV.setText(childTitle);
        }

        @Override
        public void onClick(View view)
        {
            if (view == itemView) {
                itemView.setSelected(true);
                if (activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)) {
                    listener.itemClicked(groupTitle, childTitle, null, null, false, id);
                } else {
                    selectUnselectItemView(itemView);
                    listener.itemClicked(groupTitle, childTitle, null, activityPassed, true, id);
                }
            } else if (view == titleTV) {
                TheArrangeActivity.clickedElementString = TheArrangeActivity.titleTVElement;
                listener.itemClicked(groupTitle, childTitle, null, activityPassed, true, id);
            } else if (view == rvOnX) {
                TheArrangeActivity.clickedElementString = TheArrangeActivity.xElement;
                listener.itemClicked(groupTitle, childTitle, null, null, true, id);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            theArrangeActivity.onLongClick(groupTitle, false);
            return false;
        }
    }

    public void selectUnselectItemView(View itemView) {
        if (selectedItemView != null){
            selectedItemView.setSelected(false);
        }
        if (itemView != null) {
            itemView.setSelected(true);
            selectedItemView = itemView;
        } else {
            selectedItemView = null;
        }
    }

    //TODO: View Holder Main
    public class ViewHolderGroupItem extends GroupViewHolder implements View.OnLongClickListener
    {
        private TextView titleTV;
        private String title;
        private ImageView arrowXImage, listGroupIconIV;
        private RelativeLayout.LayoutParams arrowXImageParams;
        private int flatPosition;
        private int id;
        private boolean isGroup;
        private RelativeLayout.LayoutParams listGroupIconParams;
        private ExpandableGroup group;
        private final RelativeLayout onXRV;

        public ViewHolderGroupItem(View itemView)
        {   super(itemView);
            titleTV = (TextView) itemView.findViewById(R.id.item_view_groups_title_tv);
            arrowXImage = (ImageView) itemView.findViewById(R.id.item_view_groups_image_arrow);
            listGroupIconIV = (ImageView) itemView.findViewById(R.id.item_view_groups_relevant_icon);
            onXRV = (RelativeLayout) itemView.findViewById(R.id.item_view_groups_on_x_rv);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            setParams();
            if (activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)) {
                titleTV.setOnClickListener(this);
                titleTV.setOnLongClickListener(this);

                if (isForDelete) { onXRV.setOnClickListener(this); }
            }
        }

        private void setTitle(String title) {
            this.title = title;
            titleTV.setText(title);
        }
        private void setFlatPosition(int position) { this.flatPosition = position; }
        private void setId(int id) { this.id = id; }
        private void setType(boolean isGroup) { this.isGroup = isGroup; }
        private void setGroup(ExpandableGroup group) { this.group = group; }

        @Override
        public void onClick(View view) {
            if (view == itemView) {
                if (isGroup)
                { super.onClick(view);
                    listener.itemClicked(title, null, null, null, false, id);
                } else {
                    if (activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)) {
                        listener.itemClicked(null, null, title, activityPassed, false, id);
                    } else {
                        itemView.setBackgroundResource(R.drawable.drawer_list_child_selector);
                        selectUnselectItemView(itemView);
                        listener.itemClicked(null, null, title, activityPassed, true, id);
                    }
                }
            } else if (view == titleTV) {
                TheArrangeActivity.clickedElementString = TheArrangeActivity.titleTVElement;
                if (isGroup) {
                    listener.itemClicked(title, null, null, null, true, id);
                } else {
                    listener.itemClicked(null, null, title, null, true, id);
                }
            } else if (isForDelete && view == onXRV) {
                TheArrangeActivity.clickedElementString = TheArrangeActivity.xElement;
                if (isGroup) {
                    listener.itemClicked(title, null, null, null, true, id);
                } else {
                    listener.itemClicked(null, null, title, null, true, id);
                }
            }
        }
        @Override
        public boolean onLongClick(View view) {
            if (activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)) {
                theArrangeActivity.onLongClick(title, true);
            }
            return false;
        }
        @Override
        public void collapse() {
            initRelevantViewsOnCollapse();
            if (activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)) {
                if (!TheArrangeActivity.isOnDrug) {
                    loadGroupMap(false);
                    loadChildrenMap(false);
                }

                if (isConstructorCalled) {
                    if (flatPosition -childrenFPosId.size() == groups.size() -1) {
                        isConstructorCalled = false;
                    }
                }
            }
        }
        @Override
        public void expand() {
            initRelevantViewsOnExpand();
            if (activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)) {
                if (!TheArrangeActivity.isOnDrug) {
                    loadGroupMap(true);
                    loadChildrenMap(true);
                }
            }
        }

        private void loadGroupMap(boolean isOnExpand) {
            if (!isConstructorCalled) {
                HashMap<Integer, Integer> tempGroupsMap = new HashMap<>();
                for (int i : groupsFPosId.keySet()) {
                    if (i > flatPosition) {
                        if (isOnExpand)
                        {
                            tempGroupsMap.put(i + group.getItemCount(), groupsFPosId.get(i));
                        } else {
                            tempGroupsMap.put(i - group.getItemCount(), groupsFPosId.get(i));
                        }
                    } else {
                        tempGroupsMap.put(i, groupsFPosId.get(i));
                    }
                }
                groupsFPosId = tempGroupsMap;
            }
        }

        private void loadChildrenMap(boolean isOnExpand)
        {
            if (!isConstructorCalled || isConstructorWithExpand)
            {
                HashMap<Integer, Integer> tempChildrenMap = new HashMap<>();
                int position = flatPosition;
                if (isOnExpand)
                {
                    if (childrenFPosId.size() > 0)
                    {
                        for (int i : childrenFPosId.keySet())
                        {
                            if (i > flatPosition)
                            {
                                tempChildrenMap.put(i + group.getItemCount(), childrenFPosId.get(i));
                            } else {
                                tempChildrenMap.put(i, childrenFPosId.get(i));
                            }
                        }
                        childrenFPosId = tempChildrenMap;
                    }

                    for (int i = 0; i < group.getItemCount(); i++) {
                        position++;
                        childrenFPosId.put(position, null);
                    }
                } else {
                    for (int i = 0; i < group.getItemCount(); i++)
                    {
                        position++;
                        childrenFPosId.remove(position);
                    }
                    for (int i : childrenFPosId.keySet())
                    {
                        if (i > flatPosition)
                        {
                            tempChildrenMap.put(i - group.getItemCount(), childrenFPosId.get(i));
                        } else {
                            tempChildrenMap.put(i, childrenFPosId.get(i));
                        }
                    }
                    childrenFPosId = tempChildrenMap;
                }
            }
        }

        private void setParams() {
            arrowXImageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            arrowXImageParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            arrowXImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
            arrowXImage.setLayoutParams(arrowXImageParams);
            listGroupIconParams = isGroup ? new RelativeLayout.LayoutParams(80,80) : new RelativeLayout.LayoutParams(70,70);
            listGroupIconParams.setMarginStart(35);
            listGroupIconParams.addRule(RelativeLayout.CENTER_VERTICAL);
            listGroupIconIV.setLayoutParams(listGroupIconParams);
        }

        private void initRelevantViewsOnExpand()
        {
            setParams();
            if (isGroup)
            {
                if (isForDelete)
                {
                    arrowXImage.setImageResource(R.mipmap.x);
                    arrowXImageParams.height = 30;
                    arrowXImageParams.width = 30;
                    arrowXImageParams.setMarginEnd(60);
                } else {
                    arrowXImage.setImageResource(android.R.drawable.arrow_up_float);
                    arrowXImageParams.setMarginEnd(50);
                }
                listGroupIconIV.setImageResource(R.mipmap.silver_folder);
                arrowXImage.setVisibility(View.VISIBLE);

                if (!activityPassed.equals(TheArrangeActivity.EDIT_FOLDERS_ACTIVITY)) {
                    listener.itemClicked(title, null, null, activityPassed, false, id);
                }
            } else {
                arrowXImage.setVisibility(View.INVISIBLE);
                listGroupIconIV.setImageResource(R.mipmap.list_modified);
            }
        }

        private void initRelevantViewsOnCollapse() {
            setParams();
            if (isGroup)
            {
                if (isForDelete){
                    arrowXImage.setImageResource(R.mipmap.x);
                    arrowXImageParams.height = 60;
                    arrowXImageParams.width = 60;
                    arrowXImageParams.setMarginEnd(45);
                } else {
                    arrowXImage.setImageResource(android.R.drawable.ic_menu_more);
                    arrowXImageParams.setMarginEnd(25);
                }
                arrowXImage.setVisibility(View.VISIBLE);
                listGroupIconIV.setImageResource(R.mipmap.silver_folder);
            } else {
                if (isForDelete)
                {
                    arrowXImage.setImageResource(R.mipmap.x);
                    arrowXImageParams.height = 60;
                    arrowXImageParams.width = 60;
                    arrowXImageParams.setMarginEnd(45);
                } else {
                    arrowXImage.setVisibility(View.GONE);
                }
                listGroupIconIV.setImageResource(R.mipmap.list_modified);
                arrowXImageParams.height = 60;
                arrowXImageParams.width = 60;
            }
        }
    }
}
