package com.globalpaysolutions.mrocket.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.globalpaysolutions.mrocket.R;

import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter
{

    private ArrayList<Integer> mImages;
    private LayoutInflater mInflater;
    private Context mContext;

    public SliderAdapter(Context context, ArrayList<Integer> images)
    {
        this.mContext = context;
        this.mImages = images;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public int getCount()
    {
        return mImages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position)
    {
        View imageLayout = mInflater.inflate(R.layout.banner_slide, view, false);
        ImageView image = (ImageView) imageLayout.findViewById(R.id.bannerImage);
        image.setImageResource(mImages.get(position));
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }
}