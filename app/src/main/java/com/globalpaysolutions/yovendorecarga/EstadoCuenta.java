package com.globalpaysolutions.yovendorecarga;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.adapters.EstadoCuentaViewPagerAdapter;
import com.globalpaysolutions.yovendorecarga.adapters.ViewPagerHistAdapter;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.SlidingTabLayout;

public class EstadoCuenta extends AppCompatActivity
{
    Toolbar toolbar;
    ViewPager mPager;
    EstadoCuentaViewPagerAdapter mViewPagerAdapter;
    SlidingTabLayout mTabs;
    CharSequence mTitles[] = {"Estado de Cuenta", "Historial de Pagos"};
    int mNumbofTabs = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_cuenta);

        mViewPagerAdapter = new EstadoCuentaViewPagerAdapter(EstadoCuenta.this.getSupportFragmentManager(), mTitles, mNumbofTabs);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mViewPagerAdapter);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);



        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
        {
            @Override
            public int getIndicatorColor(int position)
            {
                return getResources().getColor(R.color.color_claro_red);
            }
        });

        mTabs.setViewPager(mPager);
    }
}
