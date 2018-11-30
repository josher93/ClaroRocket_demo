package com.globalpaysolutions.mrocket;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.globalpaysolutions.mrocket.R;
import com.globalpaysolutions.mrocket.adapters.ViewPagerHistAdapter;
import com.globalpaysolutions.mrocket.customs.SessionManager;
import com.globalpaysolutions.mrocket.customs.SlidingTabLayout;
import com.globalpaysolutions.mrocket.model.rest.RocketPaymentHistoryResponse;
import com.globalpaysolutions.mrocket.views.EstadoCuentaView;

import java.util.List;

public class HistorialVentas extends AppCompatActivity
{
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerHistAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Hoy", "Ayer", "Semana"};
    int Numboftabs = 3;

    //Global Variables
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ventas);

        toolbar = (Toolbar) findViewById(R.id.historyToolbar);
        toolbar.setTitle(getString(R.string.title_activity_sales_history));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new ViewPagerHistAdapter(HistorialVentas.this.getSupportFragmentManager(), Titles, Numboftabs);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
        {
            @Override
            public int getIndicatorColor(int position)
            {
                return getResources().getColor(R.color.color_movistar_green);
            }
        });
        tabs.setViewPager(pager);
        sessionManager = new SessionManager(this);

    }

}
