package com.globalpaysolutions.yovendorecarga;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.yovendosaldo.BuildConfig;
import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;

public class Configuracion extends AppCompatActivity
{
    private static final String TAG = Configuracion.class.getSimpleName();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        //Fragment = getIntent().getStringExtra("ConfFragment");

        toolbar = (Toolbar) findViewById(R.id.toolbarConf);
        toolbar.setTitle(getString(R.string.title_activity_config));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        SetConfigurationFragment();

        /*switch (Fragment)
        {
            case "PIN":
                MenuManejoPINFragment manejoPin = new MenuManejoPINFragment();
                fragmentTransaction.add(R.id.frameConfig, manejoPin);
                fragmentTransaction.commit();
                break;
            case "CONF":
                ConfiguracionFragment fragment = new ConfiguracionFragment();
                fragmentTransaction.add(R.id.frameConfig, fragment);
                fragmentTransaction.commit();
                break;
            default:
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_LONG).show();
                break;
        }*/

    }

    public static class ConfiguracionFragment extends PreferenceFragment
    {
        SessionManager sessionManager;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_configuracion);

            sessionManager = new SessionManager(getActivity());
            final String iso2Code = sessionManager.getIso2Code();

            Preference prefCalifica = findPreference("yvs_califica");
            prefCalifica.setOnPreferenceClickListener (new Preference.OnPreferenceClickListener(){
                public boolean onPreferenceClick(Preference preference)
                {
                    final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity  object
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    }
                    catch (android.content.ActivityNotFoundException anfe)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    }

                    return false;
                }
            });

            Preference prefPass = findPreference("con_pass");
            prefPass.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    try
                    {
                        String url = "https://yovendorecarga.com/" + iso2Code + "/Account/Forgotpassword";
                        Uri webpage = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                        {
                            startActivity(intent);
                            Log.i(TAG, "Opening URL: " + webpage);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    return false;
                }
            });

            Preference prefPoliticas = findPreference("yvs_politicas");
            prefPoliticas.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    try
                    {
                        String url = "https://yovendorecarga.com/" + iso2Code + "/home/terminos";
                        Uri webpage = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                        {
                            startActivity(intent);
                            Log.i(TAG, "Opening URL: " + webpage);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    return false;
                }
            });

            String versName = BuildConfig.VERSION_NAME;
            Preference versionName = findPreference("app_version");
            versionName.setSummary(getResources().getString(R.string.version) + " " + versName);
        }
    }

    /*@Override
    protected void onResume()
    {
        super.onResume();
        SetConfigurationFragment();
    }*/

    public void SetConfigurationFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ConfiguracionFragment fragment = new ConfiguracionFragment();
        fragmentTransaction.add(R.id.frameConfig, fragment);
        fragmentTransaction.commit();
    }

    /*@Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        finish();
        Intent i = new Intent(this, Home.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }*/
}
