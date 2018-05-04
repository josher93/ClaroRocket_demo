package com.globalpaysolutions.yovendorecarga;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.android.yovendosaldo.BuildConfig;
import com.android.yovendosaldo.R;
import com.globalpaysolutions.yovendorecarga.adapters.AmountSpinnerAdapter;
import com.globalpaysolutions.yovendorecarga.adapters.OperatorsAdapter;
import com.globalpaysolutions.yovendorecarga.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendorecarga.customs.Data;
import com.globalpaysolutions.yovendorecarga.customs.DatabaseHandler;
import com.globalpaysolutions.yovendorecarga.customs.NicknameDialogBuilder;
import com.globalpaysolutions.yovendorecarga.customs.PinDialogBuilder;
import com.globalpaysolutions.yovendorecarga.customs.PromotionsHandler;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.StringsURL;
import com.globalpaysolutions.yovendorecarga.customs.Validation;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;
import com.globalpaysolutions.yovendorecarga.customs.YvsPhoneStateListener;
import com.globalpaysolutions.yovendorecarga.data.RealmDatabase;
import com.globalpaysolutions.yovendorecarga.model.Amount;
import com.globalpaysolutions.yovendorecarga.model.Operator;
import com.globalpaysolutions.yovendorecarga.model.OperatorsBalance;
import com.globalpaysolutions.yovendorecarga.model.OperatorsBalanceModel;
import com.globalpaysolutions.yovendorecarga.model.TopupModel;
import com.globalpaysolutions.yovendorecarga.model.TopupResult;
import com.globalpaysolutions.yovendorecarga.rest.ApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.vision.text.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;



public class Home extends AppCompatActivity implements FragmentFavoritos.FavoritesListener
{
    //Adapters y Layouts
    OperatorsAdapter OpeAdapter;
    AmountSpinnerAdapter AmountAdapter;
    GridView GridViewOperators;
    Spinner SpinnerAmount;
    RelativeLayout rlMainHomeContent;
    Toolbar toolbar;
    EditText txtPhoneNumber;
    LinearLayout lnrSelectAmount;

    ProgressDialog ProgressDialog;
    Button btnTopup;
    SwipeRefreshLayout SwipeRefresh;
    ScrollView scrollView;
    //LinearLayout lnrBalance;
    //TextView tvCurrency;
    TextView tvVendorName;
    TextView tvVendorCode;
    MenuItem drawerMenuItem;
    ImageButton ibContacts;
    ImageButton ibFavorites;
    TextView lblSelectedAmount;

    //Objetos para el Drawer
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    //Objetos globales para activity
    List<Operator> ListaOperadores = new ArrayList<Operator>();
    List<Amount> selectedOperatorAmounts = new ArrayList<>();
    private static final String TAG = Home.class.getSimpleName();
    public static String Token;
    boolean OperatorSelected = false;
    boolean RetrievingAmounts = false;
    String SelectedOperatorName;
    //int AmountTopup;
    double AmountTopup;
    int PackageCode;
    SessionManager sessionManager;
    Validation validator;
    private boolean IsExecuting = false;
    private boolean isFirstTime;
    CustomFullScreenDialog CustomDialogCreator;
    PinDialogBuilder PinDialogBuilder;
    public PinDialogBuilder.CustomOnClickListener ClickListener;
    DatabaseHandler db;
    RealmDatabase realmBD;
    PromotionsHandler promotionsHandler;
    String SelectedAmuntItemDisplay;

    //Signal
    TelephonyManager mTelephonyManager;
    YvsPhoneStateListener psListener;


    //Push Notifications
    public static Home homeActivity;
    public static Boolean isVisible = false;


    //Lock screen
    PowerManager.WakeLock wakeLock;

    //Contacts
    static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Inicialización de Activity
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Creación de objetos globales
        sessionManager = new SessionManager(Home.this);
        CustomDialogCreator = new CustomFullScreenDialog(Home.this, Home.this);
        db = new DatabaseHandler(Home.this);
        promotionsHandler = new PromotionsHandler(this, this);

        //Seteo de vistas y layouts globales
        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        txtPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        btnTopup = (Button) findViewById(R.id.btnEnvar);
        scrollView = (ScrollView) findViewById(R.id.homeScrollView);
        ibContacts = (ImageButton) findViewById(R.id.ibContacts);
        ibFavorites = (ImageButton) findViewById(R.id.ibFavorites);
        lblSelectedAmount = (TextView) findViewById(R.id.lblSelectedAmount);
        lnrSelectAmount = (LinearLayout) findViewById(R.id.lnrSelectAmount);
        lnrSelectAmount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                displayAmounts();
            }
        });

        isFirstTime = sessionManager.IsFirstTime();
        homeActivity = this;

        //Validacion de version y hace las demas peticiones
        //checkAppVersion();

        CheckLogin();

        if(sessionManager.IsUserLoggedIn())
        {
            //Validacion de version y hace las demas peticiones
            checkAppVersion();
        }



        //Signal
        psListener = new YvsPhoneStateListener();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(psListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);



        /*
        ******************************
        *
        *   Navigation Drawer
        *
        ******************************
        */

        InitializeNavigationDrawer();


        if (isFirstTime)
        {
            //ExecuteShowcase();
            sessionManager.SetUserRateResponse(false);
            sessionManager.SetShowcaseViewShown(false);
        }


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
                ListaOperadores.clear();
                RetrieveAmounts();
            }
        });


        if (sessionManager.IsUserLoggedIn())
        {
            if(!isFirstTime)
            {
                SwipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
                SwipeRefresh.setRefreshing(true);

            }

            renderSavedAmounts();
        }



        /*
        *
        *   SCROLL VIEW
        *   Detecta si el scroll view está a 0 en Y
        */

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener()
        {

            @Override
            public void onScrollChanged()
            {
                int scrollY = scrollView.getScrollY();
                if (scrollY == 0)
                    SwipeRefresh.setEnabled(true);
                else
                    SwipeRefresh.setEnabled(false);

            }
        });


    }

    private HashMap<String, Amount> mAmountsMap = new HashMap<>();
    private List<String> mAmountNames = new ArrayList<>();
    private Amount mSelectedAmunt;
    List<Amount> mAmountList = new ArrayList<>();

    private void renderSavedAmounts()
    {
        try
        {
            JSONObject savedData = sessionManager.getSavedAmounts();
            JSONArray savedAmounts = savedData.getJSONArray("denomination");

            for (int a = 0; a < savedAmounts.length(); a++)
            {
                JSONObject currentAmount = savedAmounts.getJSONObject(a);

                Amount amount = new Amount();
                //Obtiene los valores del Item
                String amountCode = currentAmount.has("Code") ? currentAmount.getString("Code") : "";
                String amountDisplay = currentAmount.has("Description") ? currentAmount.getString("Description") : "";
                String amountAmount = currentAmount.has("Amount") ? currentAmount.getString("Amount") : "";
                int amountPackageCode = currentAmount.has("PackageCode") ? currentAmount.getInt("PackageCode") : 0;
                int relevance = currentAmount.has("Relevance") ? currentAmount.getInt("Relevance") : 0;

                if(amountDisplay.isEmpty())
                {
                    amountDisplay = amountAmount;
                }

                //Setea el objeto Amount con las respectivas propiedades
                amount.setMNO(Data.MNO_NAME);
                amount.setCode(amountCode);
                amount.setDisplay(amountDisplay);
                amount.setPackageCode(amountPackageCode);
                amount.setRelevance(relevance);

                //  MONTOS CON DECIMALES
                double amountWithDecimals = Double.parseDouble(amountAmount);

                //Lo añade al objeto amount
                amount.setAmount(amountWithDecimals);

                mAmountList.add(amount);
            }

            for(Amount amount : mAmountList)
            {
                mAmountNames.add(amount.getDisplay());
                mAmountsMap.put(amount.getDisplay(), amount);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    private void displayAmounts()
    {
        if(mAmountList.size() > 0)
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.label_title_select_amount_dialog));
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, mAmountNames);
            builder.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    String amountName = mAmountNames.get(i);
                    mSelectedAmunt = mAmountsMap.get(amountName);
                }
            });

            builder.setPositiveButton(getString(R.string.button_accept), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    if(mSelectedAmunt != null)
                    {
                        //presenter.createRequestTopupObject().setAmount(selectedAmount.getAmount());
                        setSelectedAmount(mSelectedAmunt);
                    }
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void setSelectedAmount(Amount pSelected)
    {
        AmountTopup = pSelected.getAmount();
        lblSelectedAmount.setText(pSelected.getDisplay());
        lblSelectedAmount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        lblSelectedAmount.setTypeface(null, Typeface.BOLD);
    }



    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   NAVIGATION DRAWER
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void InitializeNavigationDrawer()
    {
        //Navigation Drawer
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        rlMainHomeContent = (RelativeLayout) findViewById(R.id.rlMainHomeContent);

        View headerView = navigationView.inflateHeaderView(R.layout.header);
        tvVendorName = (TextView) headerView.findViewById(R.id.tvVendorName);
        tvVendorCode = (TextView) headerView.findViewById(R.id.tvVendorCode);

        HashMap<String, String> UserNames = sessionManager.GetUserProfile();
        String FirstName = UserNames.get(SessionManager.KEY_FIRST_NAME).trim();
        String LastName = UserNames.get(SessionManager.KEY_LAST_NAME).trim();
        tvVendorName.setText(FirstName + " " + LastName);
        tvVendorCode.setText(String.format(getString(R.string.label_vendor_code), sessionManager.GetVendorCode()));

        //Setea el menu dependiendo del tipo de vendedor
        navigationView.inflateMenu(R.menu.m_vendor_drawer);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.Home:
                        drawerLayout.closeDrawers();
                        finish();
                        Intent i = new Intent(getApplication().getApplicationContext(), Home.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        return true;
                    case R.id.Historial:
                        drawerLayout.closeDrawers();
                        Intent history = new Intent(getApplication().getApplicationContext(), HistorialVentas.class);
                        startActivity(history);
                        return true;


                    case R.id.Perfil:
                        drawerLayout.closeDrawers();
                        Intent perfil = new Intent(getApplication().getApplicationContext(), Perfil.class);
                        startActivity(perfil);
                        return true;



                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Inicializando Drawer Layout y ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        {

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();



    }


    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   TOPUP
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void RequestTopUp(View view)
    {

        EnableTopupButton(false);
        Log.i("Print click", "Para saber cuantas veces se ha hecho click en el botón.");

        final String PhoneNumber = txtPhoneNumber.getText().toString();

        if (CheckValidation())
        {
            if (!RetrieveUserPin().isEmpty())
            {
                if (sessionManager.IsSecurityPinActive())
                {

                    //Construye el dialogo, sobreescribe el método del click
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
                                BeginTopup(PhoneNumber);
                                Data.IntentCounter = 0;
                            }
                            else
                            {
                                //Valida que los intentos no hayan sido más de 4
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
                    PinDialogBuilder = new PinDialogBuilder(Home.this, ClickListener, PhoneNumber, SelectedAmuntItemDisplay, true);

                    //Muestra el teclado al aparecer el dialogo
                    PinDialogBuilder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    PinDialogBuilder.show();
                    EnableTopupButton(true);

                }
                else
                {
                    BeginTopup(PhoneNumber);
                }
            }
            else
            {
                BeginTopup(PhoneNumber);
            }
        }
        else
        {
            EnableTopupButton(true);
        }

    }

    public void BeginTopup(String PhoneNumber)
    {
        if (CheckConnection())
        {
            PhoneNumber = PhoneNumber.replace("-", "");

            String Amount = "";

            if ((AmountTopup == Math.floor(AmountTopup)) && !Double.isInfinite(AmountTopup))
            {
                int amountInt = (int) AmountTopup;
                Amount = String.valueOf(amountInt);
            }
            else
            {
                Amount = String.valueOf(AmountTopup);
            }

            ProgressDialog = new ProgressDialog(Home.this);
            ProgressDialog.setMessage("Enviando Recarga...");
            ProgressDialog.show();
            ProgressDialog.setCancelable(false);
            ProgressDialog.setCanceledOnTouchOutside(false);

            //***   ALERTA!! CON ESTE METODO SE ENVIA               ***
            //***   LA RECARGA, TENER CUIDADO CON IMPLEMENTACION    ***
            TopUp(PhoneNumber, Amount);

        }
    }

    public void TopUp(String pPhoneNumber, String pAmount)
    {
        synchronized (this)
        {
            if (IsExecuting)
                return;
        }
        IsExecuting = true;

        //Locks the screen while sending request
        activateLockScreenFromSleep(true);

        //Setting up TopupData
        String countryCode = RetrieveCountryPhoneCode();
        String CountryID = RetrieveCountryID();
        String TopupData = countryCode + pPhoneNumber + "/" + pAmount + "/" + PackageCode;

        //Making request to server
        TopupModel model = new TopupModel(SelectedOperatorName, CountryID);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<TopupResult> call = apiService.getTopupResult(TopupData, Token, model);
        Log.i(TAG, "Topup: " + TopupData);

        //Handle response
        call.enqueue(new Callback<TopupResult>() {
            @Override
            public void onResponse(Call<TopupResult> call, retrofit2.Response<TopupResult> response)
            {
                if(response.isSuccessful())
                {
                    TopupResult topupResult = response.body();
                    activateLockScreenFromSleep(false);
                    ProgressDialog.dismiss();
                    ProcessTopupResponse(topupResult);
                }
                else
                {
                    int codeResponse = response.code();
                    ProcessRetrofitError(codeResponse);
                }
            }
            @Override
            public void onFailure(Call<TopupResult> call, Throwable t)
            {
                ProgressDialog.dismiss();
                if(t instanceof SocketTimeoutException)
                {
                    //Toast.makeText(Home.this, "Tiempo agotado", Toast.LENGTH_LONG).show();
                    String Titulo = getString(R.string.check_history_title);
                    String Linea1 = getString(R.string.check_history_advice_ln1);
                    String Linea2 = getString(R.string.check_history_advice_ln2);
                    String Button = "OK";
                    //Se añadio: 14-ENE-16
                    txtPhoneNumber.setText("");
                    IsExecuting = false;
                    //ResetControls();
                    ResetDefaults(ListaOperadores);

                    EnableTopupButton(true);
                    //CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1 + " " + Linea2, null, null, Button, "NEWACTION", true, true);
                    CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1 + " " + Linea2, null, null, Button, "CHECHKHISTORY", true, true, null);
                }
                else
                {
                    String Titulo = "ALGO HA SALIDO MAL...";
                    String Linea1 = getString(R.string.something_went_wrong_try_again);
                    String Button = "OK";
                    //Se añadio: 14-ENE-16
                    //ResetControls();
                    ResetDefaults(ListaOperadores);

                    IsExecuting = false;
                    EnableTopupButton(true);
                    CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "NEWACTION", true, true, null);
                }

                //Unlocks the screen on response
                activateLockScreenFromSleep(false);
            }

        });


    }

    //Procesa Respuesta de TopUp
    public void ProcessTopupResponse(TopupResult pResponse)
    {
        //Restablece el scroll al tope despues de enviar recarga.
        scrollView.fullScroll(View.FOCUS_UP);

        String PhoneUsed = txtPhoneNumber.getText().toString();
        String Operator = "";

        //Añade cantidad de recargas realizadas
        int actual = sessionManager.GetNumberOfRecharges();
        actual = actual + 1;
        sessionManager.SetNumberOfRecharges(actual);

        try
        {
            String Message = pResponse.getMessage();
            String Balance = pResponse.getAvailableAmount();

            List<OperatorsBalance> balances = new ArrayList<>();
            for(OperatorsBalanceModel item : pResponse.getOperatorsBalance())
            {
                OperatorsBalance operatorsBalance = new OperatorsBalance();
                operatorsBalance.setOperatorId(item.getOperatorID());
                operatorsBalance.setMobileOperator(operatorsBalance.getMobileOperator());
                operatorsBalance.setBalance(operatorsBalance.getBalance());
                balances.add(operatorsBalance);
            }

            realmBD = RealmDatabase.getInstance(this);
            realmBD.InsertMultipleOperatorsBalance(balances);

            sessionManager.SaveAvailableBalance(Balance);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String buttonText = "";
        Bundle xtraValues = new Bundle();
        xtraValues.putBoolean("shareOnFacebook", sessionManager.isFacebookShareAllowed());
        xtraValues.putString("lastSaleOperator", SelectedOperatorName);
        buttonText = (sessionManager.isFacebookShareAllowed()) ? getString(R.string.button_content_share_on_facebook) : getString(R.string.label_sale_another_topup);
        CustomDialogCreator.CreateFullScreenDialog(getString(R.string.dialog_succeed_topoup_title), getString(R.string.dialog_succeed_topup_content), PhoneUsed, Operator, buttonText, "NEWACTION", false, true, xtraValues);

        //Resetea todos los controles
        IsExecuting = false;
        EnableTopupButton(true);
        //ResetControls();
        ResetDefaults(ListaOperadores);
        //shareOnFacebook();

    }

    public void ProcessRetrofitError(int error)
    {
        scrollView.fullScroll(View.FOCUS_UP);

        String Titulo;
        String Linea1;
        String Linea2;
        String Button;
        ProgressDialog.dismiss();

        switch (error)
        {
            //case 502:
            case 401: //API V2 - Token expired
                Titulo = getString(R.string.expired_session);
                Linea1 = getString(R.string.dialog_error_topup_content);
                Button = "OK";
                txtPhoneNumber.setText("");
                IsExecuting = false;
                EnableTopupButton(true);
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "LOGOUT", true, true, null);
                break;
            case 503:
                Titulo = getString(R.string.insufficent_balance_title);
                Linea1 = getString(R.string.insufficent_balance_ln1);
                Linea2 = getString(R.string.insufficent_balance_ln2);
                Button = "OK";
                //Se añadio: 14-ENE-16
                //ResetControls();
                ResetDefaults(ListaOperadores);
                IsExecuting = false;
                EnableTopupButton(true);

                //TODO: Quitar comentario
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1 + " " + Linea2, null, null, Button, "NAVIGATEHOME", true, true, null);
                //Bundle xtraValues = new Bundle();
                //xtraValues.putBoolean("shareOnFacebook", true);
                //xtraValues.putString("lastSaleOperator", SelectedOperatorName);
                //CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1 + " " + Linea2, null, null, buttonText, "NEWACTION", true, true, xtraValues);

                break;
            default:
                Titulo = "ALGO HA SALIDO MAL...";
                Linea1 = getString(R.string.something_went_wrong_try_again);
                Button = "OK";
                //Se añadio: 14-ENE-16
                //ResetControls();
                ResetDefaults(ListaOperadores);
                IsExecuting = false;
                EnableTopupButton(true);
                CustomDialogCreator.CreateFullScreenDialog(Titulo, Linea1, null, null, Button, "NEWACTION", true, true, null);
                break;
        }
    }






    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   OPERADORES Y MONTOS
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */




    public void RetrieveAmounts()
    {

        RetrievingAmounts = true;
        Data.GetAmounts(Home.this, new Data.VolleyCallback()
        {
            @Override
            public void onResult(boolean result, JSONObject response)
            {
                if (result)
                {
                    RetrievingAmounts = false;
                    HideSwipe();
                    sessionManager.saveAmounts(response);

                }
                else
                {
                    RetrievingAmounts = false;
                    HideSwipe();
                }
            }
        });
    }




    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   BOLZÓN DE USUARIO
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public void HideSwipe()
    {
        if (SwipeRefresh.isShown() && SwipeRefresh != null && !RetrievingAmounts)
        {
            SwipeRefresh.setRefreshing(false);
        }
    }


    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   OTROS MÉTODOS
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

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

    private boolean CheckConnection()
    {
        boolean connected;

        if (HaveNetworkConnection() != true)
        {
            connected = false;
            String connectionMessage = "No esta conectado a internet.";
            Toast.makeText(getApplicationContext(), connectionMessage, Toast.LENGTH_LONG).show();
            EnableTopupButton(true);
        }
        else
        {
            connected = true;
        }

        return connected;
    }

    public void RetrieveSavedToken()
    {
        HashMap<String, String> MapToken = sessionManager.GetSavedToken();
        Token = MapToken.get(SessionManager.KEY_TOKEN);
    }

    public void CheckLogin()
    {
        sessionManager = new SessionManager(Home.this);
        sessionManager.CheckLogin();
    }

    public void InitializeValidation()
    {

        txtPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus)
                {
                    validator = new Validation(Home.this);
                    validator.IsPhoneNumber(txtPhoneNumber, true);
                    validator.HasText(txtPhoneNumber);
                }
            }
        });
        txtPhoneNumber.addTextChangedListener(new TextWatcher()
        {

            int TextLength = 0;
            private static final char dash = '-';

            @Override
            public void afterTextChanged(Editable text)
            {

                String NumberText = txtPhoneNumber.getText().toString();


                //Esconde el teclado después que el EditText alcanzó los 9 dígitos
                if (NumberText.length() == 9 && TextLength < NumberText.length())
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }

                // Remove spacing char
                if (text.length() > 0 && (text.length() % 5) == 0)
                {
                    final char c = text.charAt(text.length() - 1);
                    if (dash == c)
                    {
                        text.delete(text.length() - 1, text.length());
                    }
                }
                // Insert char where needed.
                if (text.length() > 0 && (text.length() % 5) == 0)
                {
                    char c = text.charAt(text.length() - 1);
                    // Only if its a digit where there should be a dash we insert a dash
                    if (Character.isDigit(c) && TextUtils.split(text.toString(), String.valueOf(dash)).length <= 3)
                    {
                        text.insert(text.length() - 1, String.valueOf(dash));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                String str = txtPhoneNumber.getText().toString();
                TextLength = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
        });
    }

    private boolean CheckValidation()
    {
        boolean ret = true;

        validator = new Validation(Home.this);

        if (!validator.IsPhoneNumber(txtPhoneNumber, true))
        {
            ret = false;
        }

        if (!OperatorSelected)
        {
            Toast.makeText(Home.this, getResources().getString(R.string.validation_required_operator), Toast.LENGTH_LONG).show();
            ret = false;
        }

        if (!ret)
        {
            EnableTopupButton(true);
        }

        if (AmountTopup == 0)
        {
            Toast.makeText(Home.this, getString(R.string.spinner_amount_validation), Toast.LENGTH_LONG).show();
            ret = false;
        }

        return ret;
    }


    public void EnableTopupButton(boolean pEnabled)
    {
        btnTopup.setClickable(pEnabled);
        btnTopup.setEnabled(pEnabled);
    }

    public String RetrieveUserPin()
    {
        String securityPin = "";
        HashMap<String, String> MapToken = sessionManager.GetSecurityPin();
        securityPin = MapToken.get(SessionManager.KEY_PIN_CODE);


        return securityPin;
    }


    public void ResetControls()
    {
        txtPhoneNumber.setText("");
        OperatorSelected = false;

        //Remueve el operador seleccionado
        for (int i = 0; i < GridViewOperators.getAdapter().getCount(); i++)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                GridViewOperators.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.custom_rounded_corner_operator));
            }
            else
            {
                GridViewOperators.getChildAt(i).setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_rounded_corner_operator));
            }
        }

        //Resetea los montos
        selectedOperatorAmounts.clear();
        for (int i = 0; i < 2; i++)
        {
            selectedOperatorAmounts.add(Data.AmountHint(Home.this));
        }
        SpinnerAmount.setSelection(AmountAdapter.getCount());
    }

    public void activateLockScreenFromSleep(boolean pActivate)
    {
        if(pActivate)
        {
            wakeLock.acquire();
        }
        else
        {
            wakeLock.release();
        }
    }

    public String RetrieveCountryPhoneCode()
    {
        String phoneCode = "";
        HashMap<String, String> countryPhoneCode = sessionManager.GetCountryPhoneCode();
        phoneCode = countryPhoneCode.get(SessionManager.KEY_PHONE_CODE);

        return phoneCode;
    }

    public String RetrieveCountryID()
    {
        String countryID = "0";
        HashMap<String, String> country_ID = sessionManager.GetCountryID();
        countryID = country_ID.get(SessionManager.KEY_COUNTRY_ID);

        if(countryID.equals(""))
        {
            countryID = "0";
        }

        return countryID;
    }

    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   ACTIVITY'S LIFECYCLE
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    @Override
    protected void onStart()
    {
        super.onStart();
        isVisible = true;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (ProgressDialog != null && ProgressDialog.isShowing())
        {
            ProgressDialog.dismiss();
        }
        isVisible = false;
    }

    @Override
    protected void onResume()
    {
        super.onResume();


        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "YVR Lock");

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isVisible = false;
    }



    /*
    * ***************************
    *   METODOS DE MENU
    * ***************************
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
            alertDialog.setTitle(getString(R.string.title_logout));
            alertDialog.setMessage(getString(R.string.content_confirm_logout));
            alertDialog.setCancelable(false);
            alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.setNeutralButton("ACEPTAR", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    ProgressDialog = new ProgressDialog(Home.this);
                    ProgressDialog.setMessage(getString(R.string.dialog_signing_out));
                    ProgressDialog.show();
                    ProgressDialog.setCancelable(false);
                    ProgressDialog.setCanceledOnTouchOutside(false);
                    sessionManager.LogoutUser(true);
                }
            });
            alertDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }


    public void ResetDefaults(List<Operator> pListaOperadores)
    {
        try
        {
            txtPhoneNumber.setText("");
            lblSelectedAmount.setText(R.string.label_select);
            mSelectedAmunt = null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }



    private void createGenericDialog(String pTitle, String pMessage, String pButton)
    {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle(pTitle);
        alertDialog.setMessage(pMessage);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, pButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }


    private void checkAppVersion()
    {
        JSONObject validation = new JSONObject();
        try
        {
            validation.put("appVersionName", BuildConfig.VERSION_NAME);
            validation.put("platformName", Data.PLATFORM);
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(
                Request.Method.POST,
                StringsURL.VALIDATE_VERSION,
                validation,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        InitializeValidation();

                        RetrieveSavedToken();

                        RetrieveAmounts();

                        promotionsHandler.retrievePromotions();
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                HideSwipe();
                HandleValidationError(error);
            }
        })
        {
            //Se añade el header para enviar el Token
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Token-Autorization", sessionManager.getSavedToken());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        }, 1); //Parametro de número de re-intentos
    }

    public void HandleValidationError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;
        String body = "";
        String AppVersion = "";



        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;

            if(pError.networkResponse.data!=null)
            {
                try
                {
                    body = new String(pError.networkResponse.data,"UTF-8");
                    JSONObject response = new JSONObject(body);
                    AppVersion = response.has("RequiredVersion") ? response.getString("RequiredVersion") : "";
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                catch(JSONException ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        if (pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            createGenericDialog(getString(R.string.we_are_sorry_msg_title), getString(R.string.something_went_wrong_try_again), getString(R.string.button_accept));

        }
        else if (pError instanceof ServerError)
        {

            //if (statusCode == 502)
            if(statusCode == 401)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
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
                sessionManager.silentLogout();
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Home.this);
                alertDialog.setTitle(getString(R.string.title_must_update_app));
                alertDialog.setMessage(String.format(getString(R.string.content_must_update_app), AppVersion));
                alertDialog.setPositiveButton(getString(R.string.button_accept), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            sessionManager.LogoutUser(false);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
            }
            else
            {
                createGenericDialog(getString(R.string.we_are_sorry_msg_title), getString(R.string.something_went_wrong_try_again), getString(R.string.button_accept));
            }
        }
        else if (pError instanceof NetworkError)
        {
            createGenericDialog(getString(R.string.internet_connecttion_title), getString(R.string.internet_connecttion_msg),  getString(R.string.button_accept));
        }
        else if (pError instanceof AuthFailureError)
        {
            if(statusCode == 401)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
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
                sessionManager.silentLogout();
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Home.this);
                alertDialog.setTitle(getString(R.string.title_must_update_app));
                alertDialog.setMessage(String.format(getString(R.string.content_must_update_app), AppVersion));
                alertDialog.setPositiveButton(getString(R.string.button_accept), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            sessionManager.LogoutUser(false);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
            }
            else
            {
                createGenericDialog(getString(R.string.we_are_sorry_msg_title), getString(R.string.something_went_wrong_try_again), getString(R.string.button_accept));
            }
        }
    }



    /*
    *
    *
    *
    *   OPEN CONTACTS
    *
    *
    * */

    public void openContacts(View view)
    {
        // Start an activity for the user to pick a phone number from contacts
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK)
        {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst())
            {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);
                // Do something with the phone number

                setPhoneOnTextbox(number);
            }
        }
    }

    public void setPhoneOnTextbox(String pPhone)
    {
        try
        {
            String phone = pPhone.trim();
            phone = phone.replace(" ", "");

            if (phone.length() >= 8)
            {
                phone = phone.substring(phone.length() - 8);
                phone = phone.substring(0, 4) + "-" + phone.substring(4, phone.length());

                txtPhoneNumber.setText(phone);
            }
            else
            {
                Toast.makeText(this, getString(R.string.toast_not_valid_phone), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public void navigateFavorites(View view)
    {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
        {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = FragmentFavoritos.newInstance();
        newFragment.show(fragmentTransaction, "favorites");
    }

    @Override
    public void onFavoriteSelected(String phone)
    {
        if(!TextUtils.isEmpty(phone))
            setPhoneOnTextbox(phone);

        //Dismises fragment
        android.support.v4.app.Fragment favorites = getSupportFragmentManager().findFragmentByTag("favorites");
        if (favorites != null)
        {
            DialogFragment df = (DialogFragment) favorites;
            df.dismiss();
        }

    }

    public interface ApiInterface
    {
        @POST
        Call<TopupResult> getTopupResult(@Url String topupUrl, @Header("Token-autorization") String tokenAuthorization, @Body TopupModel topupModel);

    }

}