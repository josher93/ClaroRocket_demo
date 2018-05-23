package com.globalpaysolutions.clarorocket.customs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.IntentCompat;
import android.util.Log;

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
import com.globalpaysolutions.clarorocket.R;
import com.globalpaysolutions.clarorocket.Login;
import com.globalpaysolutions.clarorocket.model.Amount;
import com.globalpaysolutions.clarorocket.model.PaymentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Josué Chávez on 30/05/2016.
 *
 * Data: Se almacena data temporal que se utilizarña únicamente
 * en tiempo de ejecución.
 */
public class Data
{
    public static final String PLATFORM  = "ANDROID";
    public static List<PaymentItem> PaymentItems = new ArrayList<>();
    public static List<Amount> Amounts = new ArrayList<>();
    public static List<Amount> resultAmountList = new ArrayList<>();
    public static boolean isEditMode = false;

    public static final String MNO_NAME = "Claro El Salvador";

    //PIN intent counter
    public static int IntentCounter = 0;

    static SessionManager sessionManager;

    public static void ManagePaymentItems(PaymentItem pItem)
    {
        if (PaymentItems.contains(pItem))
        {
            // do not add
            //PaymentItem oldItem = PaymentItems.get(PaymentItems.indexOf(pItem));
            PaymentItems.set(PaymentItems.indexOf(pItem), pItem);
        }
        else
        {
            PaymentItems.add(pItem);
        }
    }

    public static PaymentItem GetItemStatus(String pItemID)
    {
        PaymentItem item;
        item = PaymentItems.get(PaymentItems.indexOf(pItemID));

        return item;
    }


    public static void SignOut(final Context pContext, final boolean pNavigateStart)
    {

        JSONObject jSignout = new JSONObject();
        try
        {
            jSignout.put("SessionID", RetrieveSessionID(pContext));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        YVScomSingleton.getInstance(pContext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        StringsURL.SIGNOUT,
                        jSignout,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                               if(pNavigateStart)
                               {
                                   Amounts.clear();
                                   NavigateStart(pContext);

                               }
                                else
                               {
                                   NavigateLogin(pContext);
                               }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Log.d("Error Logout ", "Sucedió un error al completar el cierre de sesión.");
                                NavigateLogin(pContext);
                            }
                        }
                )
                {
                    //Se añade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        String pToken = RetrieveSavedToken(pContext);
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-autorization", pToken );
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 1); //Parametro, de maximo de re-intentos
    }

    public static void SilentSignOut(final Context pContext)
    {

        JSONObject jSignout = new JSONObject();
        try
        {
            jSignout.put("SessionID", RetrieveSessionID(pContext));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        YVScomSingleton.getInstance(pContext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        StringsURL.SIGNOUT,
                        jSignout,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Amounts.clear();
                                Log.d("Mensaje JSON ", response.toString());
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Log.d("Error Logout ", "Sucedió un error al completar el cierre de sesión.");
                            }
                        }
                )
                {
                    //Se añade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        String pToken = RetrieveSavedToken(pContext);
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-autorization", pToken );
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 1); //Parametro, de maximo de re-intentos
    }



    public static void AirtimeRequest(final Context pContext, final VolleyCallback callback)
    {

        JSONObject jsBalanceReq = new JSONObject();
        try
        {
            jsBalanceReq.put("VendorEmail", RetrieveUserEmail(pContext));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        YVScomSingleton.getInstance(pContext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        StringsURL.BALANCREQUEST,
                        jsBalanceReq,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                callback.onResult(true, response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Log.d("Error Logout ", "Sucedió un error al solicitar saldo.");
                                callback.onResult(false, null);
                            }
                        }
                )
                {
                    //Se añade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        String pToken = RetrieveSavedToken(pContext);
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-autorization", pToken );
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 1); //Parametro, de maximo de re-intentos

    }

    public interface VolleyCallback
    {
        void onResult(boolean result, JSONObject response);
    }




    /* *********************************************************************************
    *
    *   RETRIEVE AMOUNTS
    *
    * **********************************************************************************/
    public static void GetAmounts(final Context pContext, final VolleyCallback callback)
    {
        YVScomSingleton.getInstance(pContext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.PRODUCTS + RetrieveIso3Code(pContext),
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                ProcessAmountResponse(response, pContext);
                                callback.onResult(true, response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                ProcessAmountErrorResponse(error, pContext);
                                callback.onResult(true, null);
                            }
                        }
                )
                {
                    //Se añade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-Autorization", RetrieveSavedToken(pContext));
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }
                , 1); //Parametro de número de re-intentos
    }

    private static void ProcessAmountResponse(JSONObject pResonse, Context pContext)
    {
        try
        {
            JSONArray jProducts = pResonse.getJSONArray("products");

            for(int i = 0; i < jProducts.length(); i++)
            {
                JSONObject JsonProductItem = jProducts.getJSONObject(i);
                JSONArray jDenomination = JsonProductItem.getJSONArray("denomination");

                for (int a = 0; a < jDenomination.length(); a++)
                {
                    JSONObject jDenominationItem = jDenomination.getJSONObject(a);
                    Amount amount = new Amount();

                    //Obtiene los valores del Item
                    String amountCode = jDenominationItem.has("Code") ? jDenominationItem.getString("Code") : "";
                    String amountDisplay = jDenominationItem.has("Description") ? jDenominationItem.getString("Description") : "";
                    String amountAmount = jDenominationItem.has("Amount") ? jDenominationItem.getString("Amount") : "";
                    int amountPackageCode = jDenominationItem.has("PackageCode") ? jDenominationItem.getInt("PackageCode") : 0;
                    int relevance = jDenominationItem.has("Relevance") ? jDenominationItem.getInt("Relevance") : 0;

                    if(amountDisplay.isEmpty())
                    {
                        amountDisplay = amountAmount;
                    }

                    //Setea el objeto Amount con las respectivas propiedades
                    amount.setMNO(JsonProductItem.has("mno") ? JsonProductItem.getString("mno") : "");
                    amount.setAditionalText("");
                    amount.setDecimal("");
                    amount.setCode(amountCode);
                    amount.setDisplay(amountDisplay);
                    amount.setPackageCode(amountPackageCode);
                    amount.setRelevance(relevance);

                    //  MONTOS CON DECIMALES
                    double amountWithDecimals = Double.parseDouble(amountAmount);

                    //Lo añade al objeto amount
                    amount.setAmount(amountWithDecimals);

                    Data.Amounts.add(amount);
                    Log.i("Monto", amount.getDisplay() + " " + amount.getMNO() );

                }
            }

            Amount Hint = new Amount();
            Hint.setDisplay(pContext.getString(R.string.spinner_hint));
            Hint.setAmount(0);
            Hint.setDecimal("");
            Hint.setAditionalText("");
            Hint.setPackageCode(0);
            Amounts.add(Hint);

        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

    }

    public static void ProcessAmountErrorResponse(VolleyError pError, Context pContex)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if(networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if(pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            //Toast.makeText(pContex, pContex.getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            Log.e("Montos: ","Ocurrió 'TimeoutError' o 'NoConnectionError'");
        }
        else if(pError instanceof ServerError)
        {
            if(statusCode == 502)
            {
                Log.e("Montos: ","Ocurrió 'ServerError', sesion expirada");
            }
            else
            {
                //Toast.makeText(Home.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
                Log.e("Montos: ","Ocurrió un 'ServerError'.");
            }
        }
        else if (pError instanceof NetworkError)
        {
            //Toast.makeText(Home.this, getString(R.string.internet_connecttion_msg), Toast.LENGTH_LONG).show();
            Log.e("Montos: ","Ocurrió un 'NetworkError'.");
        }
        else if(pError instanceof AuthFailureError)
        {
            Log.e("Montos: ","Ocurrió un 'AuthFailureError'.");

        }
    }

    public static void sendSinglePaymentItem(PaymentItem pItem, final Context pContext)
    {
        JSONObject paymentItem = new JSONObject();
        try
        {
            paymentItem.put("id", pItem.getId());
            paymentItem.put("paid", pItem.isPaid());
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }


        YVScomSingleton.getInstance(pContext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        StringsURL.TOPUPPAYMENT_SINGLE,
                        paymentItem,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Log.d("Mensaje JSON ", error.toString());
                            }
                        }
                )
                {
                    //Se a�ade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-Autorization", RetrieveSavedToken(pContext));
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }
                ,1);//Parametro, de maximo de re-intentos
    }

    public static Amount AmountHint(Context pContex)
    {
        Amount Hint = new Amount();
        Hint.setDisplay(pContex.getString(R.string.spinner_hint));
        Hint.setAmount(0);
        Hint.setDecimal("");
        Hint.setAditionalText("");

        return Hint;
    }



    /*
    *
    *   OTHER METHODS
    *
    */

    public static int RetrieveSessionID(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        int sessionId;
        HashMap<String, Integer> SessionID = sessionManager.RetrieveSessionID();
        sessionId = SessionID.get(SessionManager.KEY_SESSION_ID);

        return sessionId;
    }

    public static String RetrieveUserEmail(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        String  userEmail;
        HashMap<String, String> UserEmail = sessionManager.GetUserEmail();
        userEmail = UserEmail.get(SessionManager.KEY_USER_EMAIL);

        return userEmail;
    }

    public static String RetrieveSavedToken(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
    }

    public static String RetrieveIso3Code(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        String Iso3Code;
        HashMap<String, String> MapIso3Code = sessionManager.GetUserISO3Code();
        Iso3Code = MapIso3Code.get(SessionManager.KEY_ISO3_CODE);

        return Iso3Code;

    }

    private static void NavigateLogin(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        sessionManager.DeleteSavedToken();

        Intent i = new Intent(pContext, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Borra el stack completo de navegación:
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        pContext.startActivity(i);
        ((Activity)pContext).finish();
    }

    private static void NavigateStart(Context pContext)
    {
        sessionManager = new SessionManager(pContext);
        sessionManager.DeleteSavedToken();

        Intent i = new Intent(pContext, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Borra el stack completo de navegación:
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        pContext.startActivity(i);
        ((Activity)pContext).finish();
    }


}
