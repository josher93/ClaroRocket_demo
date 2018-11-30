package com.globalpaysolutions.mrocket;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.globalpaysolutions.mrocket.R;
import com.globalpaysolutions.mrocket.adapters.NotificationsAdapter;
import com.globalpaysolutions.mrocket.customs.SessionManager;
import com.globalpaysolutions.mrocket.customs.StringsURL;
import com.globalpaysolutions.mrocket.customs.YVScomSingleton;
import com.globalpaysolutions.mrocket.model.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notificaciones extends AppCompatActivity
{
    //Layout y vistas
    Toolbar toolbar;
    ListView NotifListView;
    ProgressBar progressBar;
    SwipeRefreshLayout SwipeRefresh;
    LinearLayout lnrEmptyText;
    TextView tvResultText;

    //Adapters
    NotificationsAdapter NotifAdapter;

    //Variables globales
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notificaciones);
        toolbar = (Toolbar) findViewById(R.id.notifToolbar);
        toolbar.setTitle(getString(R.string.title_activity_notificaciones));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Borra el listado de notificaciones que se han almacenado
        //YvsNotificationsHandler.notifications.clear();
        //YvsNotificationsHandler.Counter = 0;

        progressBar = (ProgressBar) findViewById(R.id.pbLoadingNotif);
        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_notifications);
        lnrEmptyText = (LinearLayout) findViewById(R.id.lnrEmptyText);
        tvResultText = (TextView) findViewById(R.id.tvResultText);

        NotifListView = (ListView) findViewById(R.id.lvNotificaciones);
        NotifAdapter = new NotificationsAdapter(Notificaciones.this, R.layout.custom_notification_listview_item);
        NotifListView.setAdapter(NotifAdapter);

        /*
        *
        *   LISTVIEW
        *   Detecta si la primer fila del List está en la posición primer mas alta,
        *   entonces habilita el SwipeRefreshLayout, de lo contrario lo deshabilita.
        */
        NotifListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int topRowVerticalPosition = (NotifListView == null || NotifListView.getChildCount() == 0) ? 0 : NotifListView.getChildAt(0).getTop();
                SwipeRefresh.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        NotifListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                NotifAdapter.mSelectedItem = position;
                NotifAdapter.notifyDataSetChanged();

                Notification notification = ((Notification) parent.getItemAtPosition(position));
                Intent notifDetail = new Intent(getApplicationContext(), DetalleNotificacion.class);
                notifDetail.putExtra("trackingID", notification.getTrackingID());
                notifDetail.putExtra("notifTitle", notification.getTitle());
                notifDetail.putExtra("notifMessage", notification.getContent());
                notifDetail.putExtra("seen", notification.getSeen());
                startActivity(notifDetail);
            }
        });

        sessionManager = new SessionManager(this);



        /*
        *
        *   SWIPEREFRESH
        *
        */
        SwipeRefresh.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2, R.color.refresh_progress_3);
        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if(CheckConnection())
                {
                    //RequestNotificationsHistory(true);
                    RequestUserNotifications(true);
                }

            }
        });

        if(CheckConnection())
        {
            //RequestNotificationsHistory(false);
            RequestUserNotifications(false);
        }




    }

    //region ANTERIOR
    /*public void RequestNotificationsHistory(boolean isSwipe)
    {
        if (isSwipe)
        {
            SetProgressBarVisible(false);
            SwipeRefresh.setRefreshing(true);
            NotifAdapter.clear();
            NotifAdapter.notifyDataSetChanged();
        }
        else
        {
            SetProgressBarVisible(true);
        }

        YVScomSingleton.getInstance(Notificaciones.this)
                .addToRequestQueue(new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.NOTIFICATIONSHISTORY,
                        null,
            new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("Mensaje JSON ", response.toString());
                ProcessResponse(response);
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
            //Se añade el header para enviar el Token
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Token-Autorization", RetrieveSavedToken());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        }, 1); //Parametro de número de re-intentos
    }*/

    /*public void ProcessResponse(JSONObject pResponse)
    {
        HideSwipe();
        SetProgressBarVisible(false);
        try
        {
            JSONObject NotificationsHistory = pResponse.getJSONObject("NotificationsHistory");
            JSONArray Notifications = NotificationsHistory.getJSONArray("notifications");

            for (int i = 0; i < Notifications.length(); i++)
            {
                Notification notification = new Notification();

                try
                {
                    JSONObject JsonNotification = Notifications.getJSONObject(i);

                    //Obtención de fecha
                    String StrNotificationDate = JsonNotification.has("Date") ? JsonNotification.getString("Date") : "";
                    SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date DateNotification = Format.parse(StrNotificationDate);

                    //Asignacion a objeto para Adapter
                    notification.setTitle(JsonNotification.has("Title") ? JsonNotification.getString("Title") : "");
                    notification.setContent(JsonNotification.has("Message") ? JsonNotification.getString("Message") : "");
                    notification.setDate(DateNotification);

                    NotifAdapter.add(notification);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }


        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }*/
    //endregion


    /*
    *
    * *********************************************
    *           PETICIONES
    * *********************************************
    */
    public void RequestUserNotifications(boolean isSwipe)
    {
        if (isSwipe)
        {
            SetProgressBarVisible(false);
            SwipeRefresh.setRefreshing(true);
            NotifAdapter.clear();
            NotifAdapter.notifyDataSetChanged();
        }
        else
        {
            SetProgressBarVisible(true);
        }

        YVScomSingleton.getInstance(Notificaciones.this)
                .addToRequestQueue(new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.USER_NOTIFICATIONS,
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                ProcessUserNotifications(response);
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
                        headers.put("Token-Autorization", RetrieveSavedToken());
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 1); //Parametro de número de re-intentos
    }


    public void RequestNotificationsHistory(boolean isSwipe)
    {
        if (isSwipe)
        {
            SetProgressBarVisible(false);
            SwipeRefresh.setRefreshing(true);
            NotifAdapter.clear();
            NotifAdapter.notifyDataSetChanged();
        }
        else
        {
            SetProgressBarVisible(true);
        }


    }


    /*
    *
    * *********************************************
    *           RESPUESTAS
    * *********************************************
    */

    public void ProcessUserNotifications(JSONObject pResponse)
    {
        HideSwipe();
        SetProgressBarVisible(false);
        try
        {
            JSONObject NotificationsHistory = pResponse.getJSONObject("notifications");
            JSONArray Notifications = NotificationsHistory.getJSONArray("userNotifications");

            if(Notifications.length() <= 0)
            {
                //Empty layout text
                lnrEmptyText.setVisibility(View.VISIBLE);
                tvResultText.setText(getString(R.string.tv_no_notifications_empty_text));
            }
            else
            {
                for (int i = 0; i < Notifications.length(); i++)
                {
                    Notification notification = new Notification();

                    try
                    {
                        JSONObject JsonNotification = Notifications.getJSONObject(i);

                        //Asignacion a objeto para Adapter
                        notification.setTrackingID(JsonNotification.has("trackingID") ? JsonNotification.getInt("trackingID") : 0);
                        notification.setID(JsonNotification.has("notificationID") ? JsonNotification.getInt("notificationID") : 0);
                        notification.setTitle(JsonNotification.has("title") ? JsonNotification.getString("title") : "");
                        notification.setContent(JsonNotification.has("message") ? JsonNotification.getString("message") : "");
                        notification.setSeen(JsonNotification.has("seen") ? JsonNotification.getBoolean("seen") : false);

                        NotifAdapter.add(notification);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }


        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void ProcessCEOANotificationsResponse(JSONObject pResponse)
    {
        HideSwipe();
        SetProgressBarVisible(false);
        try
        {
            JSONObject NotificationsHistory = pResponse.getJSONObject("NotificationsHistory");
            JSONArray Notifications = NotificationsHistory.getJSONArray("notifications");

            if(Notifications.length() <= 0)
            {
                //Empty layout text
                lnrEmptyText.setVisibility(View.VISIBLE);
                tvResultText.setText(getString(R.string.tv_no_notifications_empty_text));
            }
            else
            {
                for (int i = 0; i < Notifications.length(); i++)
                {
                    Notification notification = new Notification();

                    try
                    {
                        JSONObject JsonNotification = Notifications.getJSONObject(i);

                        //Obtención de fecha
                        String StrNotificationDate = JsonNotification.has("Date") ? JsonNotification.getString("Date") : "";
                        SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date DateNotification = Format.parse(StrNotificationDate);

                        //Asignacion a objeto para Adapter
                        notification.setTitle(JsonNotification.has("Title") ? JsonNotification.getString("Title") : "");
                        notification.setContent(JsonNotification.has("Message") ? JsonNotification.getString("Message") : "");
                        notification.setDate(DateNotification);

                        NotifAdapter.add(notification);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
            }


        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void HandleVolleyError(VolleyError pError)
    {
        HideSwipe();

        //Empty layout text
        lnrEmptyText.setVisibility(View.VISIBLE);
        tvResultText.setText(getString(R.string.tv_empty_layout_error));

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
            if(statusCode == 401)
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


    /*
    *
    *   OTROS MÉTODOS
    *
    */

    public String RetrieveSavedToken()
    {
        String Token;
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);

        return Token;
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

}
