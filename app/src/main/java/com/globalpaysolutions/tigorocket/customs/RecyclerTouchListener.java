package com.globalpaysolutions.tigorocket.customs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Josué Chávez on 22/3/2018.
 */

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
{
    private GestureDetector mGestureDetector;
    private RecyclerClickListener mClickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final RecyclerClickListener clickListener)
    {
        mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null && clickListener != null)
                {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
    {
        View child = rv.findChildViewUnder(e.getX(), e.getY());

        if (child != null && mClickListener != null && mGestureDetector.onTouchEvent(e))
        {
            mClickListener.onClick(child, rv.getChildPosition(child));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e)
    {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
    {

    }
}