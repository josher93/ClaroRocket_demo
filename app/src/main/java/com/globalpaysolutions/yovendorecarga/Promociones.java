package com.globalpaysolutions.yovendorecarga;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;

public class Promociones extends AppCompatActivity
{
    //Layouts and Views
    private Toolbar toolbar;
    WebView webView;

    //Global Objects
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones);
        toolbar = (Toolbar) findViewById(R.id.promosToolbar);
        toolbar.setTitle(getString(R.string.title_activity_promotions));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);

        sessionManager = new SessionManager(this);

        String promoURL = sessionManager.getPromotionURL();

        try
        {

            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(promoURL);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_promociones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_refresh_promo)
        {
            try
            {
                Toast.makeText(this, getString(R.string.label_refreshing_wait), Toast.LENGTH_LONG).show();
                String promoURL = sessionManager.getPromotionURL();
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(promoURL);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
