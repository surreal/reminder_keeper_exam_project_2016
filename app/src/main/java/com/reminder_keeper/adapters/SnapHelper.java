package com.reminder_keeper.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class SnapHelper extends LinearSnapHelper
{
    private int position;
    private int counter = 0;

    public SnapHelper(int position) { this.position = position; }
    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException { super.attachToRecyclerView(recyclerView); }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager)
    {
        return getStartView(layoutManager, getHorizontalHelper(layoutManager));
    }

    public View getStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper)
    {
        int firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        View firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition);
        boolean isLastItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1;

        if (firstVisiblePosition == RecyclerView.NO_POSITION)
        {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
        }

        if (firstVisiblePosition != -1)
        {
            if (helper.getDecoratedStart(firstVisibleView) == 0)
            {
                counter = 0;
                if (firstVisiblePosition != position)
                {
                    ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);

                    if (firstVisiblePosition != position)
                    {
                        ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
                    }
                }
            } else {
                counter++;
                if (counter == 1)
                {
                    if (firstVisiblePosition > position) {
                        position +=  7;
                    } else if (firstVisiblePosition < position -1){
                        position -=  7;
                    }
                    ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position +1, 0);
                }
            }
        }
        return firstVisibleView;
    }

    @Override
    public int[] calculateScrollDistance(int velocityX, int velocityY)
    {
        return super.calculateScrollDistance(velocityX, velocityY);
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView)
    {
        int[] out = new int[2];
        out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
        return out;
    }
    private int distanceToStart(View targetView, OrientationHelper helper)
    {
        return helper.getDecoratedStart(targetView);
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager)
    {
        OrientationHelper mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        return mHorizontalHelper;
    }
}
