package com.globalpaysolutions.yovendorecarga;

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
import com.android.yovendosaldo.R;
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
import com.globalpaysolutions.yovendorecarga.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.StringsURL;
import com.globalpaysolutions.yovendorecarga.customs.Validation;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;
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
    TextView tvAssociateFacebook;
    CheckBox chckTopupShare;
    CircleImageView ivProfilePicture;
    LinearLayout fbpanelLoggedIn;

    //Global Fragment variables
    Validation Validator;
    SessionManager sessionManager;

    //Facebook
    CallbackManager callbackManager;
    LoginButton btnLoginFacebook;
    LikeView btnLikeFacebook;
    ShareButton btnShareFacebook;
    ProfileTracker profileTracker;
    AccessTokenTracker accessTokenTracker;
    ShareDialog shareDialog;
    String facebookEmail;

    //AccessTokenTracker accessTokenTracker;
    //AccessToken accessToken;

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
        chckTopupShare = (CheckBox) findViewById(R.id.chckTopupShare);
        tvAssociateFacebook = (TextView) findViewById(R.id.tvAssociateFacebook);
        btnLoginFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);
        btnLikeFacebook = (LikeView) findViewById(R.id.btnLikeFacebook);
        btnShareFacebook = (ShareButton) findViewById(R.id.btnShareFacebook);
        ivProfilePicture = (CircleImageView) findViewById(R.id.ivProfilePicture);
        fbpanelLoggedIn = (LinearLayout) findViewById(R.id.fbpanelLoggedIn);

        sessionManager = new SessionManager(this);
        callbackManager = CallbackManager.Factory.create();

        btnLoginFacebook.setReadPermissions(Arrays.asList("email","public_profile"));

        chckTopupShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(chckTopupShare.isChecked())
                    sessionManager.saveFacebookSettingShareAllowed(true);
                else
                    sessionManager.saveFacebookSettingShareAllowed(false);
            }
        });

        profileTracker = new ProfileTracker()
        {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile)
            {
                updateUI();
            }
        };

        accessTokenTracker = new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
            {
                if (currentAccessToken == null)
                {
                    sessionManager.saveFacebookLoggedin(false);
                    Log.i(TAG, "User logged out from Facebook");
                }
            }
        };

        setUpProfile();
        setUpFacebook();

        updateUI();

        if (ShareDialog.canShow(ShareLinkContent.class))
        {
            ShareLinkContent shareContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(StringsURL.YVR_WEB))
                    .setQuote(getString(R.string.facebook_quote_im_seller))
                    .setShareHashtag(new ShareHashtag.Builder().setHashtag(StringsURL.YVR_HASHTAG1).build())
                    .build();
            btnShareFacebook.setShareContent(shareContent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

        tvName.setText(shortName);
        tvNick.setText(nickname);
        tvVendorCode.setText(code);
        tvFullname.setText(sessionManager.getUserFullName());
        tvEmail.setText(sessionManager.getEmail());
        tvLastSale.setText(formatDate(lastSale));
        tvPhone.setText(formatPhone(phone));

        if(sessionManager.isFacebookShareAllowed())
            chckTopupShare.setChecked(true);
        else
            chckTopupShare.setChecked(false);
    }

    public void setUpFacebook()
    {
        // Callback registration
        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                // App code

                GraphRequest mGraphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response)
                            {
                                if (response.getError() == null)
                                {
                                    try
                                    {
                                        facebookEmail = me.getString("email");
                                        Log.i(TAG, "FacebookEmail: " + facebookEmail);
                                    }
                                    catch (JSONException ex) {  ex.printStackTrace();   }
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email, name");
                mGraphRequest.setParameters(parameters);
                mGraphRequest.executeAsync();


                updateUI();
                sessionManager.saveFacebookLoggedin(true);
            }

            @Override
            public void onCancel()
            {
                // App code
                updateUI();
            }

            @Override
            public void onError(FacebookException exception)
            {
                // App code
                updateUI();
            }
        });

        //Share
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>()
        {
            @Override
            public void onSuccess(Sharer.Result result)
            {

            }

            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException error)
            {

            }
        });

        btnLikeFacebook.setObjectIdAndType(StringsURL.YVR_FACEBOOK, LikeView.ObjectType.PAGE);
    }

    public void updateUI()
    {
        try
        {
            Profile profile = Profile.getCurrentProfile();
            if (profile != null)
            {
                String picture = profile.getProfilePictureUri(500, 500).toString();
                Picasso.with(this).load(picture).into(ivProfilePicture);

                sessionManager.saveFacebookFirstname(profile.getFirstName());
                sessionManager.saveFacebookLastname(profile.getLastName());
                sessionManager.saveFacebookProfileID(profile.getId());
                sessionManager.saveUserFacebookURL(profile.getLinkUri().toString());

                String facebookName = sessionManager.getFacebookFirstname() + " " + sessionManager.getFacebookLastname();
                tvName.setText(facebookName);
                tvAssociateFacebook.setVisibility(View.GONE);
                fbpanelLoggedIn.setVisibility(View.VISIBLE);

                insertUserFacebookInfo(facebookEmail);

            }
            else
            {
                ivProfilePicture.setImageDrawable(getResources().getDrawable(R.drawable.img_profile_picture));
                tvAssociateFacebook.setVisibility(View.VISIBLE);
                fbpanelLoggedIn.setVisibility(View.GONE);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void insertUserFacebookInfo(String pEmail)
    {
        try
        {
            JSONObject body = new JSONObject();
            body.put("ProfileID", sessionManager.getFacebookProfileID());
            body.put("userID", sessionManager.getFacebookUserID());
            body.put("firstname", sessionManager.getFacebookFirstname());
            body.put("middlename", "Not found");
            body.put("lastname", sessionManager.getFacebookLastname());
            body.put("email", pEmail);
            body.put("phoneNumber", "Not found");
            body.put("facebookURL", sessionManager.getFacebookUrl());

            if (CheckConnection())
            {
                YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(
                                Request.Method.POST,
                                StringsURL.INSERT_FACEBOOK_PROFILE,
                                body,
                                new Response.Listener<JSONObject>()
                                {
                                    @Override
                                    public void onResponse(JSONObject response)
                                    {
                                        Log.d(TAG, "Success: " + response.toString());
                                    }
                                },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        Log.d(TAG, "Error: " + error.toString());
                                    }
                                }
                        )
                        {
                            @Override
                            public Map<String, String> getHeaders()
                            {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Token-Autorization", sessionManager.getSavedToken());
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        }, 1);
            }
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
