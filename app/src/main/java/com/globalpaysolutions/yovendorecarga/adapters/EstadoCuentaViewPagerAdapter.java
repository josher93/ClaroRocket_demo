package com.globalpaysolutions.yovendorecarga.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.globalpaysolutions.yovendorecarga.FragmentEstadoCuenta;
import com.globalpaysolutions.yovendorecarga.FragmentHistorialCobros;

import java.util.ArrayList;
import java.util.List;

public class EstadoCuentaViewPagerAdapter extends FragmentStatePagerAdapter
{
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public EstadoCuentaViewPagerAdapter(FragmentManager manager)
    {
        super(manager);
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
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


    /*private CharSequence mTitles[];
    private int mNumbOfTabs;

    public EstadoCuentaViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb)
    {
        super(fm);
        this.mTitles = mTitles;
        this.mNumbOfTabs = mNumbOfTabsumb;
    }

    @Override
    public Fragment getItem(int position)
    {

        if(position == 0)
        {
            FragmentEstadoCuenta estadoCuenta = new FragmentEstadoCuenta();
            return estadoCuenta;
        }
        else
        {
            FragmentHistorialCobros historialCobros = new FragmentHistorialCobros();
            return historialCobros;
        }


    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return mTitles[position];
    }


    @Override
    public int getCount()
    {
        return mNumbOfTabs;
    }*/

}
