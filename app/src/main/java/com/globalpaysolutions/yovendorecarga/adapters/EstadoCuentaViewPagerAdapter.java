package com.globalpaysolutions.yovendorecarga.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.globalpaysolutions.yovendorecarga.FragmentEstadoCuenta;
import com.globalpaysolutions.yovendorecarga.FragmentHistorialCobros;

public class EstadoCuentaViewPagerAdapter extends FragmentStatePagerAdapter
{
    private CharSequence mTitles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    private int mNumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

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

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position)
    {
        return mTitles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount()
    {
        return mNumbOfTabs;
    }


}
