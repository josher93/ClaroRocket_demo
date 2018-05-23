package com.globalpaysolutions.clarorocket;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.style.QuoteSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.globalpaysolutions.clarorocket.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.globalpaysolutions.clarorocket.customs.CustomFullScreenDialog;
import com.globalpaysolutions.clarorocket.customs.SessionManager;
import com.globalpaysolutions.clarorocket.customs.StringsURL;
import com.globalpaysolutions.clarorocket.customs.Validation;
import com.globalpaysolutions.clarorocket.customs.YVScomSingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


public class Perfil extends AppCompatActivity
{
    private static final String TAG = Perfil.class.getSimpleName();
    //Layouts and Views
    Toolbar toolbar;
    TextView tvName;
    TextView tvNick;
    TextView tvVendorCode;
    TextView tvFullname;
    TextView tvEmail;
    TextView tvPhone;
    TextView tvLastSale;
    TextView tvPhoneLabel;
    CircleImageView ivProfilePicture;

    //Global Fragment variables
    Validation Validator;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        toolbar = (Toolbar) findViewById(R.id.toolbarProf);
        toolbar.setTitle(getString(R.string.title_activity_profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvName = (TextView) findViewById(R.id.tvName);
        tvNick = (TextView) findViewById(R.id.tvNick);
        tvVendorCode = (TextView) findViewById(R.id.tvVendorCode);
        tvFullname = (TextView) findViewById(R.id.tvFullname);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvLastSale = (TextView) findViewById(R.id.tvLastSale);
        tvPhoneLabel = (TextView) findViewById(R.id.tvPhoneLabel);
        ivProfilePicture = (CircleImageView) findViewById(R.id.ivProfilePicture);

        sessionManager = new SessionManager(this);

        setUpProfile();


    }


    public void setUpProfile()
    {
        if(sessionManager.isVendorM())
        {
            tvPhoneLabel.setVisibility(View.GONE);
            tvPhone.setVisibility(View.GONE);
        }

        String name = sessionManager.getUserFirstname();
        String lastName = sessionManager.getUserLastname();
        String shortName = name + " " + lastName;
        String code = String.format(getString(R.string.label_profile_vendorcode), sessionManager.GetVendorCode());
        String nickname = sessionManager.getUserNickame();
        String lastSale = sessionManager.getUserLastsale();
        String phone = sessionManager.getUserPhoneNumber();

        Picasso.with(this).load(R.drawable.img_profile_picture).into(ivProfilePicture);
        tvName.setText(shortName);
        tvNick.setText(nickname);
        tvVendorCode.setText(code);
        tvFullname.setText(sessionManager.getUserFullName());
        tvEmail.setText(sessionManager.getEmail());
        tvLastSale.setText(formatDate(lastSale));
        tvPhone.setText(formatPhone(phone));


    }


    /*
     *
     *
     *   OTROS METODOS
     *
     * */
    public String formatDate(String pDate)
    {
        String newDate = getString(R.string.label_no_sales_registered);

        try
        {
            if(!TextUtils.isEmpty(pDate))
            {
                pDate = pDate.substring(0, pDate.length() - 12);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date date = format.parse(pDate);
                Log.i(TAG, "Date: " + date);

                format = new SimpleDateFormat("EEE, d MMM yyyy - h:mm a");
                newDate = format.format(date);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return newDate;
    }

    public String formatPhone(String pPhone)
    {
        try
        {

            if(!"".equals(pPhone))
            {
                if (pPhone.length() > 8)
                {
                    pPhone = pPhone.substring(3);
                }

                pPhone = pPhone.substring(0,4) + "-" + pPhone.substring(4,pPhone.length());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return pPhone;
    }

    private boolean CheckConnection()
    {
       return hasNetworkConnection();
    }

    private boolean hasNetworkConnection()
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
