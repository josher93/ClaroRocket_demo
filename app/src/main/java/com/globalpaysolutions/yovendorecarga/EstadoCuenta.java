package com.globalpaysolutions.yovendorecarga;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.adapters.EstadoCuentaViewPagerAdapter;
import com.globalpaysolutions.yovendorecarga.adapters.ViewPagerHistAdapter;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.SlidingTabLayout;

public class EstadoCuenta extends AppCompatActivity
{
    Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_cuenta);
        toolbar = (Toolbar) findViewById(R.id.estadoCuentaToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


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
        EstadoCuentaViewPagerAdapter adapter = new EstadoCuentaViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentEstadoCuenta(), "Estado de Cuenta");
        adapter.addFragment(new FragmentHistorialCobros(), "Historial de Pagos");
        viewPager.setAdapter(adapter);
    }
}
