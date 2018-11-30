package com.globalpaysolutions.mrocket;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.globalpaysolutions.mrocket.R;
import com.globalpaysolutions.mrocket.customs.DatabaseHandler;
import com.globalpaysolutions.mrocket.customs.SessionManager;
import com.globalpaysolutions.mrocket.customs.StringsURL;
import com.globalpaysolutions.mrocket.customs.YVScomSingleton;
import com.globalpaysolutions.mrocket.model.LocalNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleNotificacion extends AppCompatActivity
{
    //Views and Layouts
    Toolbar toolbar;
    TextView tvTitle;
    TextView tvContent;

    //Global variables
    DatabaseHandler db;
    String mTitle;
    String mContent;
    int mTrackingID;
    boolean mSeen;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_notificacion);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHandler(this);
        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        mTitle = intent.getStringExtra("notifTitle");
        mContent = intent.getStringExtra("notifMessage");
        mTrackingID = intent.getIntExtra("trackingID", 0);
        mSeen = intent.getBooleanExtra("seen", false);


        tvTitle = (TextView) findViewById(R.id.notifTitle);
        tvContent = (TextView) findViewById(R.id.notifBody);

        tvTitle.setText(mTitle);
        tvContent.setText(mContent);

        if (!mSeen)
        {
            MarkNotificationRead(mTrackingID);
        }

    }

    public void MarkNotificationRead(int pTrackingID)
    {
        String TrackingID = String.valueOf(pTrackingID);

        JSONObject jObject = new JSONObject();
        try
        {
            jObject.put("trackingID", TrackingID);
            jObject.put("AmeDeviceID", "");
            System.out.println(jObject);
        } catch (JSONException e1)
        {
            e1.printStackTrace();
        }

        YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST, StringsURL.MARK_NOTIFICATION_READ, jObject, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("Mensaje JSON ", response.toString());
                Log.e("Not. Seen: ", "Success");
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                HandleSilentVolleyError(error);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Token-Autorization", sessionManager.getSavedToken());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        }, 0);//Parametro, de maximo de re-intentos
    }

    public void HandleSilentVolleyError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if (pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            Log.e("Montos: ", "Ocurri� 'TimeoutError' o 'NoConnectionError'");
        }
        else if (pError instanceof ServerError)
        {
            if (statusCode == 502)
            {
                Log.e("Montos: ", "Ocurri� 'ServerError', sesion expirada");
            }
            else
            {
                Log.e("Montos: ", "Ocurri� un 'ServerError'.");
            }
        }
        else if (pError instanceof NetworkError)
        {
            Log.e("Montos: ", "Ocurri� un 'NetworkError'.");
        }
        else if (pError instanceof AuthFailureError)
        {
            Log.e("Montos: ", "Ocurri� un 'AuthFailureError'.");

        }
    }


}
