package com.globalpaysolutions.mrocket.customs;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josué Chávez on 18/04/2017.
 */

public class PromotionsHandler
{
    private static final String TAG = PromotionsHandler.class.getSimpleName();
    private Context mContex;
    private SessionManager sessionManager;

    public PromotionsHandler(Context pContext, AppCompatActivity pActivity)
    {
        this.mContex = pContext;
        this.sessionManager = new SessionManager(mContex);
    }

    public void retrievePromotions()
    {
        YVScomSingleton.getInstance(mContex).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, StringsURL.PROMOTIONS, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                processSuccess(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                HandleVolleyError(error);
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
        }, 0); //Parametro de número de re-intentos
    }

    private void processSuccess(JSONObject pResponseObject)
    {
        try
        {
            String operatorName = pResponseObject.has("Operator") ? pResponseObject.getString("Operator") : "";
            String promotionTitle = pResponseObject.has("Tittle") ? pResponseObject.getString("Tittle") : "";
            String promotionDescripction = pResponseObject.has("description") ? pResponseObject.getString("description") : "";
            String promotionURL = pResponseObject.has("URL") ? pResponseObject.getString("URL") : "";
            String endpointMethod = pResponseObject.has("Method") ? pResponseObject.getString("Method") : "";

            sessionManager.savePromotionsData(operatorName, promotionTitle, promotionDescripction, promotionURL, endpointMethod);

        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
    }

    private void HandleVolleyError(VolleyError pError)
    {
        if (pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            Log.e(TAG, "TimeoutError || NoConnectionError: Error retrieving promos.");
        }
        else if (pError instanceof ServerError)
        {
            Log.e(TAG, "ServerError: Error retrieving promos.");
        }
        else if (pError instanceof NetworkError)
        {
            Log.e(TAG, "NetworkError: Error retrieving promos.");
        }
        else if (pError instanceof AuthFailureError)
        {
            Log.e(TAG, "AuthFailureError: Error retrieving promos.");
        }
    }


}
