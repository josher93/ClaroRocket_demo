package com.globalpaysolutions.yovendorecarga;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.StringsURL;
import com.globalpaysolutions.yovendorecarga.customs.Validation;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Referido extends AppCompatActivity
{
    Toolbar toolbar;
    EditText email;
    Validation validator;
    CustomFullScreenDialog FullScreenDialog;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    TextView lblMoreInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referido);

        toolbar = (Toolbar) findViewById(R.id.referToolbar);
        toolbar.setTitle(getString(R.string.title_activity_referido));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FullScreenDialog = new CustomFullScreenDialog(Referido.this, this);
        sessionManager = new SessionManager(this);

        email = (EditText) findViewById(R.id.etEmailReferred);
        lblMoreInfo = (TextView) findViewById(R.id.lblMoreInfo);

        lblMoreInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    String url = "https://www.yovendorecarga.com/" + sessionManager.getIso2Code() + "/home/promocion";
                    Uri webpage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null)
                    {
                        startActivity(intent);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        InitializeValidation();
    }

    public void enviarReferido(View view)
    {
        if(CheckValidation())
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getResources().getString(R.string.refer_sending_invitation));
            progressDialog.show();

            String inviteEmail = email.getText().toString();
            JSONObject jEmail = new JSONObject();

            try
            {
                jEmail.put("Email", inviteEmail);
                sendEmail(jEmail);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }
        }

    }

    public void sendEmail(JSONObject pEmail)
    {
        if (CheckConnection())
        {
            YVScomSingleton.getInstance(this).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            StringsURL.REFERRED,
                            pEmail,
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response)
                                {
                                    Log.d("Mensaje JSON ", response.toString());
                                    ProcessResponse(response);
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    HandleVolleyError(error);
                                }
                            }
                    )
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
                    }, 1); //Parametro, de maximo de re-intentos
        }
    }

    public void ProcessResponse(JSONObject response)
    {
        progressDialog.dismiss();

        try
        {
            String result = response.has("Message") ? response.getString("Message") : "";
            if(result.equals("Success"))
            {
                FullScreenDialog.CreateFullScreenDialog(
                        getResources().getString(R.string.invitation_success),
                        getResources().getString(R.string.invitation_sent_line1),
                        null, null, "Aceptar", "NAVIGATEHOME", false, false, null );
            }
            else if(result.equals("Email already referred"))
            {
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.invitation_email_already_invited_title),
                        getString(R.string.invitation_email_already_invited_message), null, null, "Aceptar", "NEWACTION", true, false, null );
                ClearFields();
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
    }

    public void HandleVolleyError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if(networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if(pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            progressDialog.dismiss();
            FullScreenDialog.CreateFullScreenDialog(getString(R.string.we_are_sorry_msg_title),
                    getString(R.string.something_went_wrong_try_again), null, null, "Aceptar", "NEWACTION", true, false, null );
        }
        else if(pError instanceof ServerError)
        {
            //StatusCode 502 significa Token Inválido
            //if(statusCode == 502)
            if(statusCode == 401)
            {
                Log.e("Error: ", networkResponse.toString());
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.expired_session),
                        getString(R.string.dialog_error_topup_content), null, null, "Aceptar", "LOGOUT", true, false, null );
            }
            else
            {
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.we_are_sorry_msg_title),
                        getString(R.string.something_went_wrong), getResources().getString(R.string.try_again_message), null, "Aceptar", "NEWACTION", true, false, null );
            }
        }
        else if (pError instanceof NetworkError)
        {
            progressDialog.dismiss();
            FullScreenDialog.CreateFullScreenDialog(getString(R.string.internet_connecttion_title),
                    getString(R.string.internet_connecttion_msg), null, null, "Aceptar", "NEWACTION", true, false, null );
        }
        else if(pError instanceof AuthFailureError)
        {
            if(statusCode == 401)
            {
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.expired_session),
                        getString(R.string.dialog_error_topup_content), null, null, "Aceptar", "LOGOUT", true, false, null );
            }
            else if(statusCode == 403)
            {
                Log.e("Error: ", networkResponse.toString());
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.invitation_email_already_invited_title),
                        getString(R.string.invitation_email_already_invited_message), null, null, "Aceptar", "NEWACTION", true, false, null );
                ClearFields();
            }
            else if (statusCode == 426)
            {
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.title_must_update_app),
                        getString(R.string.content_must_update_app_generic), null, null, "Aceptar", "LOGOUT", true, false, null );
            }
            else
            {
                progressDialog.dismiss();
                FullScreenDialog.CreateFullScreenDialog(getString(R.string.expired_session),
                        getString(R.string.dialog_error_topup_content), null, null, "Aceptar", "LOGOUT", true, false, null );
            }

        }
    }

    private boolean CheckValidation()
    {
        boolean ret = true;

        validator = new Validation(Referido.this);

        if (!validator.IsEmailAddress(email, true))
        {
            ret = false;
        }

        return ret;
    }

    public void InitializeValidation()
    {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    validator = new Validation(Referido.this);
                    validator.IsEmailAddress(email, true);
                    validator.HasText(email);
                }
            }
        });

    }
    public String RetrieveSavedToken()
    {
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        String Token = MapToken.get(SessionManager.KEY_TOKEN);
        return Token;
    }
    public void ClearFields()
    {
        email.setText("");
    }

    private boolean CheckConnection()
    {
        boolean connected;

        if(HasNetworkConnection() != true)
        {
            connected = false;
            String connectionMessage = getString(R.string.no_internet_connection);
            Toast.makeText(Referido.this, connectionMessage, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        else
        {
            connected = true;
        }

        return connected;
    }

    private boolean HasNetworkConnection()
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
