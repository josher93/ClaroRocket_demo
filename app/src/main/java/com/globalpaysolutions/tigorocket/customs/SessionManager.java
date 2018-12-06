package com.globalpaysolutions.tigorocket.customs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.globalpaysolutions.tigorocket.Login;
import com.globalpaysolutions.tigorocket.PIN;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Geovanni on 05/04/2016.
 */
public class SessionManager
{
    private static final String TAG = SessionManager.class.getSimpleName();

    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    /*  PREFERENCIAS DEL PIN    */
    SharedPreferences pinPreferences;
    Editor pinEditor;

    /*  PREFERENCIAS DE NOTIFICACIONES  */
    SharedPreferences NotificationsSettings;
    Editor NotificationsEditor;

    /*  ENCRIPTACION/DECRIPTACION  */
    String KEY = "8080808080808089"; //llave
    String IV = "8080808080808090"; // vector de inicialización


    public static final String PREF_NAME = "yvsPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_BALANCE = "availableBalance";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_TOKEN = "userToken";
    private static final String KEY_USER_ID = "personID";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_REMEMBER_EMAIL = "rememberEmail";
    public static final String KEY_PPW = "userOtherWayConffirmation";
    public static final String KEY_SESSION_ID = "sessionID";
    public static final String KEY_VENDOR_M = "vendorM";
    public static final String KEY_COUNTRY_ID = "countryId";
    public static final String KEY_ISO3_CODE = "iso3Code";
    private static final String KEY_ISO2_CODE = "iso2Code";
    public static final String KEY_PHONE_CODE = "PhoneCode";
    private static final String KEY_USER_PHONENUMBER = "usr_phone_number";
    private static final String KEY_USER_LAST_SALE = "usr_last_sale";
    public static final String KEY_VENDOR_CODE = "VendorCode";
    public static final String KEY_SHOWCASE_SHOWN = "ShowcaseViewShown";
    public static final String KEY_NUMBER_RECHARGES_DONE = "numberOfRecharges";
    public static final String KEY_ASKED_USER_FOR_RATING = "askedUserForRating";
    private static final String KEY_MONEY_SYMBOL = "usr_country_money_symbol";

    private static final String KEY_USER_LOCATION_VISIBLE = "usr_location_visible";

    public static final String KEY_ACTIVATE_PIN = "securityPin";
    public static final String KEY_PIN_CODE = "pinCode";

    private static final String KEY_PROMO_OPERATOR = "promo_operator_name";
    private static final String KEY_PROMO_TITLE = "promo_title";
    private static final String KEY_PROMO_DESCRIPTION = "promo_description";
    private static final String KEY_PROMO_URL = "promo_url";
    private static final String KEY_PROMO_METHOD = "promo_method";


    private static final String KEY_USER_NICKNAME = "usr_nickname";

    private static final String KEY_FACEBOOK_PROFILE_ID = "usr_facebook_profile_id";
    private static final String KEY_FACEBOOK_USER_ID = "usr_facebook_user_id";
    private static final String KEY_FACEBOOK_FIRSTNAME = "usr_facebook_firstname";
    private static final String KEY_FACEBOOK_LASTNAME = "usr_facebook_lastname";
    private static final String KEY_FACEBOOK_URL = "usr_facebook_url";
    private static final String KEY_FACEBOOK_USER_LOGGED_IN = "usr_facebook_logged_in";
    private static final String KEY_FACEBOOK_SETTING_SHARE_ALLOWED = "usr_facebook_setting_share_allowed";
    private static final String KEY_FACEBOOK_AUTH_TOKEN = "usr_facebook_auth_token";

    private static final String KEY_MNO_PRODUCTS = "key_mno_products";



   /* private static final String KEY_ROCKET_BALANCE_AMOUNT = "key_rocket_balance_amount";
    private static final String KEY_ROCKET_BALANCE_FROMDATE  = "key_rocket_balance_fromdate";
    private static final String KEY_ROCKET_BALANCE_TODATE  = "key_rocket_balance_todate";
    private static final String KEY_ROCKET_BALANCE_STATUS  = "key_rocket_balance_status";
    private static final String KEY_ROCKET_BALANCE_CONCILIATION_DATE  = "key_rocket_balance_conciliation_date";
    private static final String KEY_ROCKET_BALANCE_PROFIT = "key_rocket_balance_profit";*/

    private static final String KEY_ROCKET_PAYMENTS_RESPONSE = "key_rocket_payments_response";

    private static final String KEY_ROCKET_BALANCE_ID  = "key_rocket_balance_id";
    private static final String KEY_ROCKET_POS_MASTER_ID = "key_rocket_pos_master_id";
    private static final String KEY_ROCKET_RECIVABLE = "key_rocket_recivable";
    private static final String KEY_ROCKET_RECONCILE_COUNT = "key_rocket_reconcile_count";
    private static final String KEY_ROCKET_SALE = "key_rocket_sale";
    private static final String KEY_ROCKET_DISTRIBUTOR = "key_rocket_distributor";
    private static final String KEY_ROCKET_MIN_DATE = "key_rocket_min_date";
    private static final String KEY_ROCKET_MAX_DATE = "key_rocket_max_date";


    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        //Obtiene las preferencias guardadas en las Preferences
        pinPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        pinEditor = pinPreferences.edit();

        NotificationsSettings = _context.getSharedPreferences("NotificationsHubSettings", PRIVATE_MODE);
        NotificationsEditor = NotificationsSettings.edit();
    }

    /*
    *
    *   SALDO DISPONIBLE
    *
    */
    public void SaveAvailableBalance(String pBalance)
    {
        try
        {
            if(!pBalance.isEmpty())
            {
                editor.putString(KEY_BALANCE, pBalance);
                editor.commit();
            }
        }
        catch (Exception ex)
        {
            Log.e("AMN", ex.getMessage());
        }
    }

    public HashMap<String, String> GetAvailableBalance()
    {
        HashMap<String, String> Balance = new HashMap<String, String>();
        Balance.put(KEY_BALANCE, pref.getString(KEY_BALANCE, "0.00"));
        return Balance;
    }

    /*
    *
    *   EMAIL DE USUARIO
    *
    */
    public void RememberEmail(boolean pRemember)
    {
        editor.putBoolean(KEY_REMEMBER_EMAIL, pRemember);
        editor.commit();
    }

    public HashMap<String, String> GetUserEmail()
    {
        HashMap<String, String> Email = new HashMap<String, String>();
        Email.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, ""));
        return Email;
    }

    public String GetUserEmailString()
    {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    //Guarda los datos de la sesión
    public void CreateLoginSession(String pEmail, String pToken, String pBalance, String pPww, int pSessionID, boolean pVendorM,
                                   String pCountryID, String pISO3code, String pPhoneCode, int pVendorCode)
    {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_EMAIL, pEmail);
        editor.putString(KEY_TOKEN, pToken);
        editor.putString(KEY_BALANCE, pBalance);
        editor.putString(KEY_PPW, pPww);
        editor.putInt(KEY_SESSION_ID, pSessionID);
        editor.putBoolean(KEY_VENDOR_M, pVendorM);
        editor.putString(KEY_COUNTRY_ID, pCountryID);
        editor.putString(KEY_ISO3_CODE, pISO3code);
        editor.putString(KEY_PHONE_CODE, pPhoneCode);
        editor.putInt(KEY_VENDOR_CODE, pVendorCode);

        editor.commit();
    }

    //Guarda el nombre y apellido del usuario
    public void SaveUserProfile(int pID, String pFirstname, String pLastname)
    {
        editor.putString(KEY_FIRST_NAME, pFirstname);
        editor.putString(KEY_LAST_NAME, pLastname);
        editor.putInt(KEY_USER_ID, pID);
        editor.commit();
    }

    public void UpdateUserSessionInfo(String pToken, String pBalance)
    {
        editor.putString(KEY_TOKEN, pToken);
        editor.putString(KEY_BALANCE, pBalance);
        editor.apply();
    }

    public void SetShowcaseViewShown(boolean pShown)
    {
        editor.putBoolean(KEY_SHOWCASE_SHOWN, pShown);
        editor.commit();
    }

    /*public HashMap<String, Boolean> GetShowcaseViewStatus()
    {
        HashMap<String, Boolean> Showcase = new HashMap<>();
        Showcase.put(KEY_SHOWCASE_SHOWN, pref.getBoolean(KEY_SHOWCASE_SHOWN, false));
        return Showcase;
    }*/

    public boolean ShowcaseViewHasBeenShown()
    {
        return pref.getBoolean(KEY_SHOWCASE_SHOWN, true);
    }

    public HashMap<String, String> GetUserSessionInfo()
    {
        HashMap<String, String> User = new HashMap<String, String>();
        User.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, ""));
        User.put(KEY_BALANCE, pref.getString(KEY_BALANCE, "0.00"));
        User.put(KEY_TOKEN, pref.getString(KEY_TOKEN, ""));

        return User;
    }

    //Obtiene la información del perfil del usuario
    public HashMap<String, String> GetUserProfile()
    {
        HashMap<String, String> Profile = new HashMap<String, String>();
        Profile.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, ""));
        Profile.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, ""));
        return Profile;
    }

    public HashMap<String, String> GetSavedToken()
    {
        HashMap<String, String> UserToken = new HashMap<String, String>();
        UserToken.put(KEY_TOKEN, pref.getString(KEY_TOKEN, ""));
        return UserToken;
    }

    public HashMap<String, Integer> RetrieveSessionID()
    {
        HashMap<String, Integer> SessionID = new HashMap<String, Integer>();
        SessionID.put(KEY_SESSION_ID, pref.getInt(KEY_SESSION_ID, 0));
        return SessionID;
    }

    public HashMap<String, Boolean> GetVendorInfo()
    {
        HashMap<String, Boolean> VendorInfo = new HashMap<String, Boolean>();
        VendorInfo.put(KEY_VENDOR_M, pref.getBoolean(KEY_VENDOR_M, false));
        return VendorInfo;
    }

    public HashMap<String, String> GetUserISO3Code()
    {
        HashMap<String, String> userIso3Code = new HashMap<String, String>();
        userIso3Code.put(KEY_ISO3_CODE, pref.getString(KEY_ISO3_CODE, ""));
        return userIso3Code;
    }

    public HashMap<String, String> GetCountryPhoneCode()
    {
        HashMap<String, String> countryPhoneCode = new HashMap<String, String>();
        countryPhoneCode.put(KEY_PHONE_CODE, pref.getString(KEY_PHONE_CODE, ""));
        return countryPhoneCode;
    }

    public HashMap<String, String> GetCountryID()
    {
        HashMap<String, String> countryID = new HashMap<String, String>();
        countryID.put(KEY_COUNTRY_ID, pref.getString(KEY_COUNTRY_ID, ""));
        return countryID;
    }

    public int GetVendorCode()
    {
        HashMap<String, Integer> VendorCode = new HashMap<String, Integer>();
        VendorCode.put(KEY_VENDOR_CODE, pref.getInt(KEY_VENDOR_CODE, 0));
        int vendorCode = VendorCode.get(SessionManager.KEY_VENDOR_CODE);
        return vendorCode;
    }

    public int GetPersonID()
    {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public void ClearUserSession()
    {
        editor.clear();
        editor.commit();
        /*pref.edit().remove("KEY_USER_EMAIL").apply();
        pref.edit().remove("KEY_TOKEN").apply();
        pref.edit().remove("KEY_BALANCE").apply();*/
    }

    public void CheckLogin()
    {
        if (!this.IsUserLoggedIn())
        {
            Intent i = new Intent(_context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            ((Activity)_context).finish();
        }
    }

    public boolean IsUserLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void LogoutUser(boolean pNavigateStart)
    {
       try
       {
                /*
            BORRA TODOS LOS DATOS DE SHARED PREFERENCES
            editor.clear();
            editor.apply();*/

           Data.SignOut(_context, pNavigateStart);

           editor.remove(KEY_BALANCE);
           //editor.remove(KEY_TOKEN);
           editor.remove(KEY_USER_ID);
           editor.remove(KEY_FIRST_NAME);
           editor.remove(KEY_LAST_NAME);
           editor.remove(IS_LOGIN);
           editor.remove(KEY_SESSION_ID);
           editor.remove(KEY_VENDOR_M);
           editor.remove(KEY_COUNTRY_ID);
           editor.remove(KEY_ISO3_CODE);
           editor.remove(KEY_PHONE_CODE);
           editor.remove(KEY_VENDOR_CODE);
           editor.remove("RanBefore");

           editor.remove(KEY_USER_NICKNAME);

           pinEditor.remove("KEY_ACTIVATE_PIN");
           editor.apply();

           NotificationsEditor.remove("registrationID");
           NotificationsEditor.apply();

           editor = pref.edit();
           editor.putBoolean("RanBefore", true);
           editor.apply();

           removePromotionsData();

           facebookLogout();


       }
       catch (Exception ex)
       {
           ex.printStackTrace();
       }
    }

    public void silentLogout()
    {
        try
        {

            Data.SilentSignOut(_context);

            editor.remove(KEY_BALANCE);
            editor.remove(KEY_USER_ID);
            editor.remove(KEY_FIRST_NAME);
            editor.remove(KEY_LAST_NAME);
            editor.remove(IS_LOGIN);
            editor.remove(KEY_SESSION_ID);
            editor.remove(KEY_VENDOR_M);
            editor.remove(KEY_COUNTRY_ID);
            editor.remove(KEY_ISO3_CODE);
            editor.remove(KEY_PHONE_CODE);
            editor.remove(KEY_VENDOR_CODE);
            editor.remove(KEY_USER_NICKNAME);
            editor.remove("RanBefore");
            pinEditor.remove("KEY_ACTIVATE_PIN");
            editor.apply();

            NotificationsEditor.remove("registrationID");
            NotificationsEditor.apply();

            Bundle appInfo = new Bundle();
            appInfo.putString("userid", "-");
            appInfo.putString("userFirstName", "-");
            appInfo.putString("userLastName", "-");


            editor = pref.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();

            removePromotionsData();

            facebookLogout();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean IsFirstTime()
    {

        boolean RanBefore = pref.getBoolean("RanBefore", false);
        if (!RanBefore)
        {
            if(IsUserLoggedIn())
            {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("RanBefore", true);
                editor.apply();
            }
        }
        return !RanBefore;
    }

    public boolean MustRememeberEmail()
    {
        boolean MustRemember = pref.getBoolean(KEY_REMEMBER_EMAIL, false);
        return  MustRemember;
    }

    /*
    *
    *   PIN DE SEGURIDAD
    *
    */


    public boolean IsSecurityPinActive()
    {
        SharedPreferences pinPreferences;
        pinPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        boolean pinActive = pinPreferences.getBoolean("KEY_ACTIVATE_PIN", false);

        return  pinActive;
    }

    public void setPinActive(boolean pValue)
    {
        pinEditor.putBoolean("KEY_ACTIVATE_PIN", pValue);
        pinEditor.apply();
    }

    public void AskForPIN()
    {
        if (this.IsSecurityPinActive())
        {
            /*Intent i = new Intent(_context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            ((Activity)_context).finish();*/

            Intent askPin = new Intent(_context, PIN.class);
            _context.startActivity(askPin);
        }
    }

    public void SetSecurityPin(String pCode)
    {
        pinEditor.putString(KEY_PIN_CODE, pCode);
        pinEditor.commit();
    }

    public HashMap<String, String> GetSecurityPin()
    {
        HashMap<String, String> UserPin = new HashMap<String, String>();
        UserPin.put(KEY_PIN_CODE, pinPreferences.getString(KEY_PIN_CODE, ""));
        return UserPin;
    }

    public void SetNumberOfRecharges(int pQuantity)
    {
        editor.putInt(KEY_NUMBER_RECHARGES_DONE, pQuantity);
        editor.apply();
    }

    public int GetNumberOfRecharges()
    {
        int rechargesDone = pref.getInt(KEY_NUMBER_RECHARGES_DONE, 0);
        return rechargesDone;
    }

    public void SetUserRateResponse(boolean pHasAnswered)
    {
        editor.putBoolean(KEY_ASKED_USER_FOR_RATING, pHasAnswered);
        editor.apply();
    }

    public  boolean userHasAnsweredForRating()
    {
        return pref.getBoolean(KEY_ASKED_USER_FOR_RATING, true);
    }

    public void DeteleSecurityPIN()
    {
        pinEditor.remove(KEY_PIN_CODE);
        pinEditor.apply();
    }

    public boolean ValidEnteredPIN(String pPIN)
    {
        boolean Valid = false;
        String SavedPIN = pinPreferences.getString(KEY_PIN_CODE, "");
        if(SavedPIN.equals(pPIN))
        {
            Valid = true;
        }

        return Valid;
    }

    public boolean ValidPww(String pPww)
    {
        boolean Valid = false;
        String SavedPww = pref.getString(KEY_PPW, "");
        SavedPww  = DecryptPass(KEY, IV, SavedPww);

        if(SavedPww.equals(pPww))
        {
            Valid = true;
        }

        return Valid;
    }

    public void DeleteSavedToken()
    {
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public String DecryptPass(String pKey, String pIV, String pEncryptedPass)
    {
        String _decrypted = "";
        try
        {
            _decrypted = Encrypt.decrypt(pKey, pIV, pEncryptedPass);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return _decrypted;
    }

    public String getSavedToken()
    {
        return pref.getString(KEY_TOKEN, "");
    }

    public void saveIso2code(String pIso2Code)
    {
        editor.putString(KEY_ISO2_CODE, pIso2Code);
        editor.commit();
    }

    public String getIso2Code()
    {
        return pref.getString(KEY_ISO2_CODE, "SV");
    }


    /*
    *
    *
    *   USER LOCATION
    *
    */
    public void setUserLocationVisible(boolean pVisible)
    {
        editor.putBoolean(KEY_USER_LOCATION_VISIBLE, pVisible);
        editor.commit();
    }

    public boolean isUserLocationVisible()
    {
        return pref.getBoolean(KEY_USER_LOCATION_VISIBLE, false);
    }

    /*
    *
    *
    *   PROMOTIONS
    *
    *
    */

    public void savePromotionsData(String pOperator, String pTitle, String pDescription, String pURL, String pMethod)
    {
        editor.putString(KEY_PROMO_OPERATOR, pOperator);
        editor.putString(KEY_PROMO_TITLE, pTitle);
        editor.putString(KEY_PROMO_DESCRIPTION, pDescription);
        editor.putString(KEY_PROMO_URL, pURL);
        editor.putString(KEY_PROMO_METHOD, pMethod);
        editor.commit();
    }

    private void removePromotionsData()
    {
        editor.remove(KEY_PROMO_OPERATOR);
        editor.remove(KEY_PROMO_TITLE);
        editor.remove(KEY_PROMO_DESCRIPTION);
        editor.remove(KEY_PROMO_URL);
        editor.remove(KEY_PROMO_METHOD);
        editor.commit();
    }

    public String getPromotionOperator()
    {
        return pref.getString(KEY_PROMO_OPERATOR, "");
    }

    public String getPromotionTitle()
    {
        return pref.getString(KEY_PROMO_TITLE, "");
    }

    public String getPromotionDescription()
    {
        return pref.getString(KEY_PROMO_DESCRIPTION, "");
    }

    public String getPromotionURL()
    {
        return pref.getString(KEY_PROMO_URL, "");
    }

    public String getPromotionMethod()
    {
        return pref.getString(KEY_PROMO_METHOD, "");
    }

    public String getUserFullName()
    {
        String firstname = pref.getString(KEY_FIRST_NAME, "");
        String lastname = pref.getString(KEY_LAST_NAME, "");
        return firstname + " " + lastname;
    }

    public String getUserFirstname()
    {
        return pref.getString(KEY_FIRST_NAME, "");
    }


    public String getUserLastname()
    {
        return pref.getString(KEY_LAST_NAME, "");
    }

    public String getUserNickame()
    {
        return pref.getString(KEY_USER_NICKNAME, "");
    }

    public void saveUserNickname(String pNick)
    {
        editor.putString(KEY_USER_NICKNAME, pNick);
        editor.commit();
    }

    public void saveUserPhone(String pPhone)
    {
        editor.putString(KEY_USER_PHONENUMBER, pPhone);
        editor.commit();
    }

    public void saveUserLastSale(String pLastSale)
    {
        editor.putString(KEY_USER_LAST_SALE, pLastSale);
        editor.commit();
    }

    public String getUserLastsale()
    {
        return pref.getString(KEY_USER_LAST_SALE, "");
    }

    public String getUserPhoneNumber()
    {
        return pref.getString(KEY_USER_PHONENUMBER, "");
    }


    public String getEmail()
    {
        return pref.getString(KEY_USER_EMAIL, "");
    }


    /*
    *
    *
    *       MONEY SYMBOL
    *
    * */

    public void saveMoneySymbol(String pSymbol)
    {
        editor.putString(KEY_MONEY_SYMBOL, pSymbol);
        editor.commit();
    }

    public String getMoneySymbol()
    {
        return pref.getString(KEY_MONEY_SYMBOL, "$");
    }

    /*
    *
    *
    *   VENDOR m
    *
    *
    * */

    public boolean isVendorM()
    {
        return pref.getBoolean(KEY_VENDOR_M, false);
    }

    /*
    *
    *
    *
    *   FACEBOOK
    *
    *
    *
    * */

    public void saveFacebookProfileID(String pFacebookID)
    {
        editor.putString(KEY_FACEBOOK_PROFILE_ID, pFacebookID);
        editor.commit();
    }

    public String getFacebookProfileID()
    {
        return pref.getString(KEY_FACEBOOK_PROFILE_ID, "");
    }

    public void saveFacebookUserID(String pFacebookUserID)
    {
        editor.putString(KEY_FACEBOOK_USER_ID, pFacebookUserID);
        editor.commit();
    }

    public String getFacebookUserID()
    {
        return pref.getString(KEY_FACEBOOK_PROFILE_ID, "");
    }

    public void saveFacebookFirstname(String pFirstname)
    {
        editor.putString(KEY_FACEBOOK_FIRSTNAME, pFirstname);
        editor.commit();
    }

    public String getFacebookFirstname()
    {
        return pref.getString(KEY_FACEBOOK_FIRSTNAME, "");
    }

    public void saveFacebookLastname(String pLastname)
    {
        editor.putString(KEY_FACEBOOK_LASTNAME, pLastname);
        editor.commit();
    }

    public String getFacebookLastname()
    {
        return pref.getString(KEY_FACEBOOK_LASTNAME, "");
    }

    public void saveUserFacebookURL(String pUrl)
    {
        editor.putString(KEY_FACEBOOK_URL, pUrl);
        editor.commit();
    }

    public String getFacebookUrl()
    {
        return pref.getString(KEY_FACEBOOK_URL, "");
    }

    public void saveFacebookLoggedin(boolean pLoggedIn)
    {
        editor.putBoolean(KEY_FACEBOOK_USER_LOGGED_IN, pLoggedIn);
        editor.commit();
    }

    public boolean isFacebookUserLoggedIn()
    {
        return pref.getBoolean(KEY_FACEBOOK_USER_LOGGED_IN, false);
    }

    public void saveFacebookSettingShareAllowed(boolean pAllowed)
    {
        editor.putBoolean(KEY_FACEBOOK_SETTING_SHARE_ALLOWED, pAllowed);
        editor.commit();
    }

    public boolean isFacebookShareAllowed()
    {
        return pref.getBoolean(KEY_FACEBOOK_SETTING_SHARE_ALLOWED, false);
    }

    public void saveFacebookAuthToken(String pToken)
    {
        editor.putString(KEY_FACEBOOK_AUTH_TOKEN, pToken);
        editor.commit();
    }

    private void facebookLogout()
    {
        try
        {
            //Facebook Logout
            LoginManager.getInstance().logOut();
            this.saveFacebookLoggedin(false);
            this.saveFacebookSettingShareAllowed(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void saveToken(String pToken)
    {
        editor.putString(KEY_TOKEN, pToken);
        editor.commit();
    }

    public void saveAmounts(JSONObject response)
    {
        try
        {
            JSONArray products = response.getJSONArray("products");

            for (int i = 0; i < products.length(); i++)
            {
                // Gets current operator
                JSONObject operator = products.getJSONObject(i);

                // If current operator is Claro
                if(TextUtils.equals(Data.MNO_NAME, operator.getString("mno")))
                {
                    // Gets saved operator and products if any
                    String savedProdicts = pref.getString(KEY_MNO_PRODUCTS, "");

                    // Compares cuerrent with saved operator
                    if(!TextUtils.equals(operator.toString(), savedProdicts))
                    {
                        // Saves changed products
                        editor.putString(KEY_MNO_PRODUCTS, operator.toString());
                        editor.commit();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    public JSONObject getSavedAmounts()
    {
        JSONObject savedProducts = new JSONObject();

        try
        {
            String savedJson = pref.getString(KEY_MNO_PRODUCTS, "");

            //Gson gson = new Gson();
            JsonObject object = new JsonParser().parse(savedJson).getAsJsonObject();
            savedProducts = new JSONObject(object.toString());

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }

        return savedProducts;
    }

    public void savePaymentsHistoryData(String response)
    {
        editor.putString(KEY_ROCKET_PAYMENTS_RESPONSE, response);
        editor.commit();
    }

    public String getPaymentsHistoryData()
    {
        return pref.getString(KEY_ROCKET_PAYMENTS_RESPONSE, "");
    }

    public void saveSaleDetail(int balanceID, int personMasterID, double receivable, double reconcileCount, double sale, String distributor, String name, String minDate, String maxDate)
    {
        editor.putInt(KEY_ROCKET_BALANCE_ID, balanceID);
        editor.putInt(KEY_ROCKET_POS_MASTER_ID, personMasterID);
        editor.putFloat(KEY_ROCKET_RECIVABLE, Parsers.doubleToFloat(receivable));
        editor.putFloat(KEY_ROCKET_RECONCILE_COUNT, Parsers.doubleToFloat(reconcileCount));
        editor.putFloat(KEY_ROCKET_SALE, Parsers.doubleToFloat(sale));
        editor.putString(KEY_ROCKET_DISTRIBUTOR, distributor);
        editor.putString(KEY_ROCKET_MIN_DATE, minDate);
        editor.putString(KEY_ROCKET_MAX_DATE, maxDate);
        editor.commit();
    }

    public int getBalanceID()
    {
        return pref.getInt(KEY_ROCKET_BALANCE_ID, 0);
    }

    public float getRocketSale()
    {
        return pref.getFloat(KEY_ROCKET_SALE, 0);
    }

    public float getReceivableAccount()
    {
        return pref.getFloat(KEY_ROCKET_RECIVABLE, 0);
    }

    public float getReconcileAccount()
    {
        return pref.getFloat(KEY_ROCKET_RECONCILE_COUNT, 0);
    }

    public String getMinDate()
    {
        return pref.getString(KEY_ROCKET_MIN_DATE, "");
    }

    public String getMaxDate()
    {
        return pref.getString(KEY_ROCKET_MAX_DATE, "");
    }
}
