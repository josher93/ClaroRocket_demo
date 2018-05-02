package com.globalpaysolutions.yovendorecarga;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.adapters.TopupRequestsAdapter;
import com.globalpaysolutions.yovendorecarga.customs.Data;
import com.globalpaysolutions.yovendorecarga.customs.PinDialogBuilder;
import com.globalpaysolutions.yovendorecarga.customs.RequestTopupListener;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.StringsURL;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;
import com.globalpaysolutions.yovendorecarga.model.PendingRequestItem;
import com.globalpaysolutions.yovendorecarga.model.TopupRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class SolicitudesRecarga extends AppCompatActivity implements RequestTopupListener
{
    private static final String TAG = SolicitudesRecarga.class.getSimpleName();

    //Layout y vistas
    Toolbar toolbar;
    ListView RequestsListView;
    ProgressBar progressBar;
    SwipeRefreshLayout SwipeRefresh;
    ProgressDialog progressDialog;

    //Global Variables
    SessionManager sessionManager;
    public PinDialogBuilder.CustomOnClickListener ClickListener;
    PinDialogBuilder PinDialogBuilder;

    //Adapters
    TopupRequestsAdapter topupRequestsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes_recarga);

        toolbar = (Toolbar) findViewById(R.id.topupReqToolbar);
        toolbar.setTitle(getString(R.string.title_activity_topup_requests));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.pbLoadingRequests);
        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_topup_requests);

        RequestsListView = (ListView) findViewById(R.id.listviewTopupRequests);
        topupRequestsAdapter = new TopupRequestsAdapter(SolicitudesRecarga.this, R.layout.custom_topup_request_item, this);

        sessionManager = new SessionManager(this);

        RequestsListView.setAdapter(topupRequestsAdapter);

        /*
        *
        *   LISTVIEW
        *   Detecta si la primer fila del List est? en la posici?n primer mas alta,
        *   entonces habilita el SwipeRefreshLayout, de lo contrario lo deshabilita.
        */
        RequestsListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int topRowVerticalPosition = (RequestsListView == null || RequestsListView.getChildCount() == 0) ? 0 : RequestsListView.getChildAt(0).getTop();
                SwipeRefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        /*
        *
        *   SWIPEREFRESH
        *
        */
        SwipeRefresh.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if(CheckConnection())
                {
                    retrieveTopupRequests(true);
                }

            }
        });

       if(CheckConnection())
        {
            retrieveTopupRequests(false);
        }



    }

    private void retrieveTopupRequests(boolean isSwipe)
    {

        if (isSwipe)
        {
            SetProgressBarVisible(false);
            SwipeRefresh.setRefreshing(true);
            topupRequestsAdapter.clear();
            topupRequestsAdapter.notifyDataSetChanged();
        }
        else
        {
            SetProgressBarVisible(true);
        }


        YVScomSingleton.getInstance(SolicitudesRecarga.this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.TOPUP_REQUESTS,
                        null,
                        new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("Mensaje JSON ", response.toString());
                processRequests(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                handleVolleyError(error);
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
        }, 1); //Parametro de n?mero de re-intentos
    }

    private void processRequests(JSONObject response)
    {
        HideSwipe();
        SetProgressBarVisible(false);

        try
        {
            JSONObject pendingRequests = response.getJSONObject("pendingRequests");
            JSONArray requestsList = pendingRequests.getJSONArray("PendingRequestsList");

            for (int i = 0; i < requestsList.length(); i++)
            {
                PendingRequestItem requestItem = new PendingRequestItem();

                try
                {
                    JSONObject JsonRequestItem = requestsList.getJSONObject(i);

                    //Asignacion a objeto para Adapter
                    requestItem.setTopUpRequestID(!JsonRequestItem.isNull("TopUpRequestID") ? JsonRequestItem.getInt("TopUpRequestID") : 0);
                    requestItem.setPhoneNumber(!JsonRequestItem.isNull("PhoneNumber") ? JsonRequestItem.getString("PhoneNumber") : "");
                    requestItem.setOperatorName(!JsonRequestItem.isNull("OperatorName") ? JsonRequestItem.getString("OperatorName") : "");
                    requestItem.setAmount(!JsonRequestItem.isNull("Amount") ? JsonRequestItem.getString("Amount") : "");
                    requestItem.setNickname(!JsonRequestItem.isNull("Nickname") ? JsonRequestItem.getString("Nickname") : "");
                    //Date
                    String stringDate = !JsonRequestItem.isNull("Date") ? JsonRequestItem.getString("Date") : "";
                    SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date DateSale = Format.parse(stringDate);
                    requestItem.setDate(DateSale);

                    requestItem.setDateGMT(!JsonRequestItem.isNull("DateGMT") ? JsonRequestItem.getString("DateGMT") : "");
                    Log.i(TAG, JsonRequestItem.toString() );

                    topupRequestsAdapter.add(requestItem);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                    Log.e(TAG, "Error parsing date");
                }
            }


        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void handleVolleyError(VolleyError pError)
    {
        SetProgressBarVisible(false);
        HideSwipe();
        dismissProgressDialog();

        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if (pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            SetProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("ALGO HA SALIDO MAL...");
            alertDialog.setMessage(getString(R.string.something_went_wrong_try_again));
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            alertDialog.show();
        }
        else if (pError instanceof ServerError)
        {
            SetProgressBarVisible(false);
            //if (statusCode == 502)
            if (statusCode == 401)
            {
                Log.e("Error: ", networkResponse.toString());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getString(R.string.expired_session));
                alertDialog.setMessage(getString(R.string.dialog_error_topup_content));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser(false);
                    }
                });
                alertDialog.show();
            }
            else if(statusCode == 503)
            {
                SetProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getString(R.string.insufficent_balance_title));
                alertDialog.setMessage(getString(R.string.insufficent_balance_ln1));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                alertDialog.show();
            }
            else
            {
                SetProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("ALGO HA SALIDO MAL...");
                alertDialog.setMessage(getString(R.string.something_went_wrong_try_again));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                alertDialog.show();
            }
        }
        else if (pError instanceof NetworkError)
        {
            SetProgressBarVisible(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.internet_connecttion_title));
            alertDialog.setMessage(getString(R.string.internet_connecttion_msg));
            alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            alertDialog.show();
        }
        else if (pError instanceof AuthFailureError)
        {
            if(statusCode == 401)
            {
                SetProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getString(R.string.expired_session));
                alertDialog.setMessage(getString(R.string.dialog_error_topup_content));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser(false);
                    }
                });
                alertDialog.show();
            }
            else if (statusCode == 426)
            {
                SetProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getString(R.string.title_must_update_app));
                alertDialog.setMessage(getString(R.string.content_must_update_app_generic));
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser(false);
                    }
                });
                alertDialog.show();
            }
            else
            {
                SetProgressBarVisible(false);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("ERROR");
                alertDialog.setMessage("Las credenciales son incorrectas");
                alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.LogoutUser(false);
                    }
                });
                alertDialog.show();
            }
        }
    }

    public void sendTopupRequest(final boolean accepted, int requestID)
    {
        try
        {
            generateProgressDialog(getString(R.string.general_text_wait));
            JSONObject topupResponse = new JSONObject();
            topupResponse.put("PendingRequestID", requestID);
            topupResponse.put("ResponseToRequest", accepted);

            if (CheckConnection())
            {

                YVScomSingleton.getInstance(this).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                StringsURL.RESPONSE_PENDING_REQUEST,
                                topupResponse,
                                new Response.Listener<JSONObject>()
                                {
                                    @Override
                                    public void onResponse(JSONObject response)
                                    {
                                        Log.d("Mensaje JSON ", response.toString());
                                        processTopupResponse(accepted);
                                    }
                                },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        handleVolleyError(error);
                                    }
                                }
                        )
                        {
                            //Se a�ade el header para enviar el Token
                            @Override
                            public Map<String, String> getHeaders()
                            {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Token-Autorization", sessionManager.getSavedToken());
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        }, 1); //Parametro, de maximo de re-intentos
            }
        }
        catch (JSONException ex)
        {
            Log.i(TAG, "Error on JSON");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void processTopupResponse(boolean accepted)
    {
        try
        {
            String title;
            String content;
            if(accepted)
            {
                title = getString(R.string.dialog_succeed_topoup_title);
                content = getString(R.string.dialog_simple_succeed_topup_content);
            }
            else
            {
                title = getString(R.string.succeed_title);
                content = getString(R.string.topup_request_denied);
            }
            dismissProgressDialog();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(title);
            alertDialog.setMessage(content);
            alertDialog.setNeutralButton(getString(R.string.button_accept), new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    if(CheckConnection())
                        retrieveTopupRequests(true);
                }
            });
            alertDialog.show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*
    *
    *
    *   OTROS METODOS
    *
    *
    */

    private boolean CheckConnection()
    {
        boolean connected;

        if(!HaveNetworkConnection())
        {
            connected = false;
            String connectionMessage = getString(R.string.no_internet_connection);
            Toast.makeText(this, connectionMessage, Toast.LENGTH_LONG).show();
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

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void SetProgressBarVisible(boolean pVisible)
    {

        if (pVisible)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void HideSwipe()
    {
        if (SwipeRefresh.isShown() && SwipeRefresh != null)
        {
            SwipeRefresh.setRefreshing(false);
        }
    }

    public String RetrieveUserPin()
    {
        String securityPin = "";
        HashMap<String, String> MapToken = sessionManager.GetSecurityPin();
        securityPin = MapToken.get(SessionManager.KEY_PIN_CODE);

        if (!StringUtils.isNotBlank(securityPin))
        {
            securityPin = "";
        }

        return securityPin;
    }

    private void generateProgressDialog(String text)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(text);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void dismissProgressDialog()
    {
        try
        {
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private String formatPhoneNumber(String phone)
    {
        String PhoneNumber = phone;

        try
        {
            PhoneNumber = PhoneNumber.substring(3);
            if(!"".equals(PhoneNumber))
            {
                PhoneNumber = PhoneNumber.substring(0,4) + "-" + PhoneNumber.substring(4,PhoneNumber.length());
            }
        }
        catch (Exception ex) {  ex.printStackTrace();   }

        return PhoneNumber;
    }


    @Override
    public void onRequestResponse(final PendingRequestItem requestItem, final boolean accepted)
    {

        if (!RetrieveUserPin().isEmpty())
        {
            if (sessionManager.IsSecurityPinActive())
            {

                //Construye el dialogo, sobreescribe el metodo del click
                //y lo muestra
                ClickListener = new PinDialogBuilder.CustomOnClickListener()
                {
                    @Override
                    public void onAcceptClick()
                    {
                        final String strPIN = PinDialogBuilder.strPIN;
                        if (sessionManager.ValidEnteredPIN(strPIN))
                        {
                            PinDialogBuilder.dismiss();
                            if(accepted)
                            {
                                sendTopupRequest(true, requestItem.getTopUpRequestID());
                            }
                            else
                            {
                                sendTopupRequest(false, requestItem.getTopUpRequestID());
                            }
                            Data.IntentCounter = 0;
                        }
                        else
                        {
                            //Valida que los intentos no hayan sido m�s de 4
                            if (Data.IntentCounter < 3)
                            {
                                PinDialogBuilder.GenerateIncorrectPINText();
                                Data.IntentCounter = Data.IntentCounter + 1;
                            }
                            else
                            {
                                Data.IntentCounter = 0;
                                sessionManager.LogoutUser(false);
                            }

                        }
                    }
                };
                PinDialogBuilder = new PinDialogBuilder(this, ClickListener, formatPhoneNumber(requestItem.getPhoneNumber()), requestItem.getAmount(), accepted);

                //Muestra el teclado al aparecer el dialogo
                PinDialogBuilder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                PinDialogBuilder.show();

            }
            else
            {
                if(accepted)
                {
                    sendTopupRequest(true, requestItem.getTopUpRequestID());
                }
                else
                {
                    sendTopupRequest(false, requestItem.getTopUpRequestID());
                }
            }
        }
        else
        {
            if(accepted)
            {
                sendTopupRequest(true, requestItem.getTopUpRequestID());
            }
            else
            {
                sendTopupRequest(false, requestItem.getTopUpRequestID());
            }
        }


    }
}
