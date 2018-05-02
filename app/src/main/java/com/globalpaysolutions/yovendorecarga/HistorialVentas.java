package com.globalpaysolutions.yovendorecarga;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.adapters.ViewPagerHistAdapter;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.SlidingTabLayout;

import java.util.HashMap;

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
                return getResources().getColor(R.color.ApplicationGreenTheme);
            }
        });
        tabs.setViewPager(pager);
        sessionManager = new SessionManager(this);

    }

    /*public void CreateArrayUpdate()
    {
        if(!Data.PaymentItems.isEmpty())
        {
            JSONArray jsonPaymentArray = new JSONArray();

            for(PaymentItem item : Data.PaymentItems)
            {
                JSONObject jPaymentObject = new JSONObject();

                try
                {
                    jPaymentObject.put("id", item.getId());
                    jPaymentObject.put("transaction_id", item.getTransactionID());
                    jPaymentObject.put("paid", item.isPaid());
                    jsonPaymentArray.put(jPaymentObject);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            if (CheckConnection())
            {
                JSONObject PaymentItems = new JSONObject();

                try
                {
                    PaymentItems.put("PaymentItems", jsonPaymentArray);
                    System.out.println(PaymentItems);
                }
                catch (JSONException e1)
                {
                    e1.printStackTrace();
                }
                // Depurando objeto Json...
                Log.d("Payment", PaymentItems.toString());

                // Env�o de par�metros a servidor y obtenci�n de respuesta
                YVScomSingleton.getInstance(this).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                StringsURL.TOPUPPAYMENT,
                                //StringsURL.TEST_TIMEOUT,
                                PaymentItems,
                                new Response.Listener<JSONObject>()
                                {
                                    @Override
                                    public void onResponse(JSONObject response)
                                    {
                                        Log.d("Mensaje JSON ", response.toString());
                                        Data.PaymentItems.clear();
                                    }
                                },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        Log.d("Mensaje JSON ", error.toString());
                                        Data.PaymentItems.clear();
                                    }
                                }
                        )
                        {
                            //Se a�ade el header para enviar el Token
                            @Override
                            public Map<String, String> getHeaders()
                            {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Token-Autorization", RetrieveSavedToken());
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        }
                        ,1);//Parametro, de maximo de re-intentos
            }
        }

    }*/



    /*
    *
    *   OTROS METODOS
    *
    */

    private boolean CheckConnection()
    {
        boolean connected;

        if(HaveNetworkConnection() != true)
        {
            connected = false;
        }
        else
        {
            connected = true;
        }

        return connected;
    }

    private boolean HaveNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (ni.isConnected())
                {
                    haveConnectedWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if (ni.isConnected())
                {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public String RetrieveSavedToken()
    {
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }
}
