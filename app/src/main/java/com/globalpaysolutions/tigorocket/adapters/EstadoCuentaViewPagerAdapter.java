package com.globalpaysolutions.tigorocket.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.globalpaysolutions.tigorocket.FragmentEstadoCuenta;
import com.globalpaysolutions.tigorocket.FragmentHistorialCobros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadoCuentaViewPagerAdapter extends FragmentStatePagerAdapter
{
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    public EstadoCuentaViewPagerAdapter(FragmentManager manager)
    {
        super(manager);
        mFragmentManager = manager;
        mFragmentTags = new HashMap<Integer, String>();
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position) {
            case 0:
                return FragmentEstadoCuenta.newInstance();
            case 1:
                return FragmentHistorialCobros.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title)
    {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return mFragmentTitleList.get(position);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }

    public Fragment getFragment(int position)
    {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null)
        {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }
}
