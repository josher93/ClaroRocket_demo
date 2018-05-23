package com.globalpaysolutions.clarorocket;

import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalpaysolutions.clarorocket.R;
import com.globalpaysolutions.clarorocket.adapters.EstadoCuentaViewPagerAdapter;
import com.globalpaysolutions.clarorocket.adapters.ViewPagerHistAdapter;
import com.globalpaysolutions.clarorocket.customs.SessionManager;
import com.globalpaysolutions.clarorocket.customs.SlidingTabLayout;

public class EstadoCuenta extends AppCompatActivity implements ViewPager.OnPageChangeListener
{
    Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private EstadoCuentaViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_cuenta);
        toolbar = (Toolbar) findViewById(R.id.estadoCuentaToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mAdapter = new EstadoCuentaViewPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();
    }

    private void setupTabIcons()
    {
        ConstraintLayout estadoCuenta = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView title1 = (TextView) estadoCuenta.findViewById(R.id.tvTitleTab);
        ImageView icon1 = (ImageView) estadoCuenta.findViewById(R.id.icIconTab);
        title1.setText(R.string.label_account_statement);
        icon1.setImageResource(R.drawable.ic_referred);
        mTabLayout.getTabAt(0).setCustomView(estadoCuenta);

        ConstraintLayout historialPago = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        TextView title2 = (TextView) historialPago.findViewById(R.id.tvTitleTab);
        ImageView icon2 = (ImageView) historialPago.findViewById(R.id.icIconTab);
        title2.setText(R.string.label_billing_history);
        icon2.setImageResource(R.drawable.icono_historial);
        mTabLayout.getTabAt(1).setCustomView(historialPago);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        mAdapter.addFragment(new FragmentEstadoCuenta(), "Estado de Cuenta");
        mAdapter.addFragment(new FragmentHistorialCobros(), "Historial de Pagos");
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {
        android.support.v4.app.Fragment fragment = mAdapter.getFragment(position);
        if (fragment != null) {
            fragment.onResume();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }
}
