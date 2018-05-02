package com.globalpaysolutions.yovendorecarga;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
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
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.globalpaysolutions.yovendorecarga.adapters.AmountSpinnerAdapter;
import com.globalpaysolutions.yovendorecarga.adapters.OperatorsAdapter;
import com.globalpaysolutions.yovendorecarga.customs.CustomFullScreenDialog;
import com.globalpaysolutions.yovendorecarga.customs.Data;
import com.globalpaysolutions.yovendorecarga.customs.DatabaseHandler;
import com.globalpaysolutions.yovendorecarga.customs.DeviceName;
import com.globalpaysolutions.yovendorecarga.customs.LocationTracker;
import com.globalpaysolutions.yovendorecarga.customs.NicknameDialogBuilder;
import com.globalpaysolutions.yovendorecarga.customs.PinDialogBuilder;
import com.globalpaysolutions.yovendorecarga.customs.PromotionsHandler;
import com.globalpaysolutions.yovendorecarga.customs.SessionManager;
import com.globalpaysolutions.yovendorecarga.customs.StringsURL;
import com.globalpaysolutions.yovendorecarga.customs.Validation;
import com.globalpaysolutions.yovendorecarga.customs.YVScomSingleton;
import com.globalpaysolutions.yovendorecarga.customs.YvsPhoneStateListener;
import com.globalpaysolutions.yovendorecarga.data.RealmDatabase;
import com.globalpaysolutions.yovendorecarga.firebase.FirebaseInteractor;
import com.globalpaysolutions.yovendorecarga.model.Amount;
import com.globalpaysolutions.yovendorecarga.model.LocationData;
import com.globalpaysolutions.yovendorecarga.model.Operator;
import com.globalpaysolutions.yovendorecarga.model.OperatorsBalance;
import com.globalpaysolutions.yovendorecarga.model.OperatorsBalanceModel;
import com.globalpaysolutions.yovendorecarga.model.TopupModel;
import com.globalpaysolutions.yovendorecarga.model.TopupResult;
import com.globalpaysolutions.yovendorecarga.rest.ApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;



public class Home extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, FragmentFavoritos.FavoritesListener
{
    //Adapters y Layouts
    OperatorsAdapter OpeAdapter;
    AmountSpinnerAdapter AmountAdapter;
    GridView GridViewOperators;
    Spinner SpinnerAmount;
    RelativeLayout rlMainHomeContent;
    Toolbar toolbar;
    EditText txtPhoneNumber;
    TextView tvBalance;
    TextView tvAvailabeBalanceLabel;
    ProgressDialog ProgressDialog;
    Button btnTopup;
    SwipeRefreshLayout SwipeRefresh;
    ScrollView scrollView;
    LinearLayout lnrBalance;
    TextView tvCurrency;
    TextView tvVendorName;
    TextView tvVendorCode;
    MenuItem drawerMenuItem;
    ImageButton ibContacts;
    ImageButton ibFavorites;

    //Objetos para el Drawer
    private NavigationView navigationView;
    private NavigationView navigationFooter;
    private DrawerLayout drawerLayout;

    //Objetos globales para activity
    List<Operator> ListaOperadores = new ArrayList<Operator>();
    List<Amount> ListaMontos = new ArrayList<Amount>();
    List<Amount> selectedOperatorAmounts = new ArrayList<>();
    List<Operator> arrivingOperadores = new ArrayList<>();
    private static final String TAG = Home.class.getSimpleName();
    private static final String VALID = "VALID";
    private static final String NOTVALID = "NOTVALID";
    private static final String ERROR = "ERROR";
    private static final String LIVECHAT_LICENSE_KEY = "8556888";
    public static String Token;
    boolean OperatorSelected = false;
    boolean RetrievingAmounts = false;
    String SelectedOperatorName;
    //int AmountTopup;
    double AmountTopup;
    int PackageCode;
    SessionManager sessionManager;
    Validation validator;
    private ShowcaseView showcaseView;
    private int ShowCaseCounter = 0;
    private boolean IsExecuting = false;
    private boolean isFirstTime;
    CustomFullScreenDialog CustomDialogCreator;
    PinDialogBuilder PinDialogBuilder;
    public PinDialogBuilder.CustomOnClickListener ClickListener;
    String mNMO;
    boolean mFineLocationGranted;
    boolean mCoarseLocationGranted;
    DatabaseHandler db;
    RealmDatabase realmBD;
    PromotionsHandler promotionsHandler;
    String SelectedAmuntItemDisplay;

    //Location
    LocationTracker locationTracker;
    LocationData mLocationData;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 3000; // 10 sec
    private static int FATEST_INTERVAL = 100; // 5 sec
    private static int DISPLACEMENT = 2; // 10 meters


    //Signal
    TelephonyManager mTelephonyManager;
    YvsPhoneStateListener psListener;


    //Push Notifications
    public static Home homeActivity;
    public static Boolean isVisible = false;


    //Lock screen
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    //Nickname
    private NicknameDialogBuilder nicknameDialogBuilder;
    private NicknameDialogBuilder.CustomOnClickListener nicknameClickListener;

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
        tvBalance = (TextView) findViewById(R.id.tvAvailableBalance);
        txtPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        btnTopup = (Button) findViewById(R.id.btnEnvar);
        scrollView = (ScrollView) findViewById(R.id.homeScrollView);
        lnrBalance = (LinearLayout) findViewById(R.id.rectangle);
        tvCurrency = (TextView) findViewById(R.id.tvCurrency);
        tvAvailabeBalanceLabel = (TextView) findViewById(R.id.tvAvailableBalanceLabel);
        ibContacts = (ImageButton) findViewById(R.id.ibContacts);
        ibFavorites = (ImageButton) findViewById(R.id.ibFavorites);

        isFirstTime = sessionManager.IsFirstTime();
        homeActivity = this;

        //Validacion de version y hace las demas peticiones
        //checkAppVersion();

        CheckLogin();
        setUserCountryCurrency();

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

        /*
        ******************************
        *
        *   End Navigation Drawer
        *
        ******************************
        */


        /*
        *
        *   OPERADORES Y BAG
        *
        */

        //SetBalanceTextView();


        //retrieveOperators();

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
                GetUserBag(false);

                retrieveOperators();
                //RetrieveAmounts();

                setOperators();
            }
        });


        if (sessionManager.IsUserLoggedIn())
        {
            if(!isFirstTime)
            {
                SwipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
                SwipeRefresh.setRefreshing(true);

                checkUserNickname();
            }


            //Se movio el metodo a checkAppVersion - Success
            // el 15-FEB-2017
            //retrieveOperators();

            //GetUserBag(false);

            //RetrieveAmounts();


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


        /*
        *
        *   BALANCE LAYOUT
        *
        */
        lnrBalance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NavigateHistoryActivity();
            }
        });






        /*
        *
        *   LOCATION
        *
        *
        */

        if (checkPlayServices())
        {
            buildGoogleApiClient();
            createLocationRequest();
        }
        mLocationData = new LocationData();

        AskForRating();

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
        if (isVendorM())
        {
            navigationView.inflateMenu(R.menu.m_vendor_drawer);
        }
        else
        {
            navigationView.inflateMenu(R.menu.drawer);
            //TODO: Uncomment for Realtime location tracking
            drawerMenuItem = navigationView.getMenu().findItem(R.id.Ubicacion);
            SwitchCompat locationSwith = (SwitchCompat) drawerMenuItem.getActionView().findViewById(R.id.swLocation);
            locationSwith.setClickable(false);
            if(sessionManager.isUserLocationVisible())
            {
                changeLocationMenuStatus(true);
            }
            else
            {
                changeLocationMenuStatus(false);
            }
        }


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

                    case R.id.ValidarDeposito:
                        drawerLayout.closeDrawers();
                        Intent deposito = new Intent(getApplication().getApplicationContext(), DepositoBancario.class);
                        startActivity(deposito);
                        return true;

                    case R.id.Historial:
                        drawerLayout.closeDrawers();
                        Intent history = new Intent(getApplication().getApplicationContext(), HistorialVentas.class);
                        startActivity(history);
                        return true;

                    case R.id.SolicitarSaldo:
                        drawerLayout.closeDrawers();
                        Intent solicitarSaldo = new Intent(getApplication().getApplicationContext(), SolicitarSaldo.class);
                        startActivity(solicitarSaldo);
                        return true;

                    case R.id.Alertas:
                        drawerLayout.closeDrawers();
                        Intent notif = new Intent(getApplication().getApplicationContext(), Notificaciones.class);
                        startActivity(notif);
                        return true;

                    case R.id.Promociones:
                        drawerLayout.closeDrawers();
                        Intent promo = new Intent(getApplication().getApplicationContext(), Promociones.class);
                        startActivity(promo);
                        return true;

                    //TODO: Uncomment for YCR Integration
                    case R.id.SolicitudesRecarga:
                        drawerLayout.closeDrawers();
                        Intent solicitudes = new Intent(getApplication().getApplicationContext(), SolicitudesRecarga.class);
                        startActivity(solicitudes);
                        return true;

                    case R.id.Chat:
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(Home.this, com.livechatinc.inappchat.ChatWindowActivity.class);
                        intent.putExtra(com.livechatinc.inappchat.ChatWindowActivity.KEY_LICENCE_NUMBER, LIVECHAT_LICENSE_KEY);
                        intent.putExtra(com.livechatinc.inappchat.ChatWindowActivity.KEY_VISITOR_NAME, sessionManager.getUserFullName());
                        intent.putExtra(com.livechatinc.inappchat.ChatWindowActivity.KEY_VISITOR_EMAIL, sessionManager.getEmail());
                        startActivity(intent);
                        return true;

                    case R.id.Perfil:
                        drawerLayout.closeDrawers();
                        Intent perfil = new Intent(getApplication().getApplicationContext(), Perfil.class);
                        startActivity(perfil);
                        return true;

                    case R.id.Conf:
                        drawerLayout.closeDrawers();
                        Intent conf = new Intent(getApplication().getApplicationContext(), Configuracion.class);
                        startActivity(conf);
                        return true;

                    //TODO: Uncomment for YCR Integration
                    case R.id.Ubicacion:
                        final SwitchCompat actionView = (SwitchCompat) menuItem.getActionView().findViewById(R.id.swLocation);

                        if(isLocationServiceEnabled())
                        {
                            if(!sessionManager.isUserLocationVisible())
                            {
                                FirebaseInteractor.getFirebaseInstance(Home.this).writeVendorData(mLastLocation);
                                sessionManager.setUserLocationVisible(true);
                                actionView.setChecked(true);
                                menuItem.setTitle(String.format(getString(R.string.drawer_my_location), getString(R.string.label_location_visible)));
                            }
                            else
                            {
                                FirebaseInteractor.getFirebaseInstance(Home.this).deleteVendorLocation();
                                sessionManager.setUserLocationVisible(false);
                                actionView.setChecked(false);
                                menuItem.setTitle(String.format(getString(R.string.drawer_my_location), getString(R.string.label_location_invisible)));
                            }
                        }
                        else
                        {
                            FirebaseInteractor.getFirebaseInstance(Home.this).deleteVendorLocation();
                            sessionManager.setUserLocationVisible(false);
                            actionView.setChecked(false);
                            menuItem.setTitle(String.format(getString(R.string.drawer_my_location), getString(R.string.label_location_invisible)));
                            askForLocationActivation();
                        }
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

    private void changeLocationMenuStatus(boolean pActive)
    {
        try
        {
            SwitchCompat locationSwith = (SwitchCompat) drawerMenuItem.getActionView().findViewById(R.id.swLocation);
            locationSwith.setClickable(false);
            if(pActive)
            {
                locationSwith.setChecked(true);
                drawerMenuItem.setTitle(String.format(getString(R.string.drawer_my_location), getString(R.string.label_location_visible)));
            }
            else
            {
                locationSwith.setChecked(false);
                drawerMenuItem.setTitle(String.format(getString(R.string.drawer_my_location), getString(R.string.label_location_invisible)));
                FirebaseInteractor.getFirebaseInstance(Home.this).deleteVendorLocation();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
        getLocation();

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

            sendDeviceData(collectDeviceData());


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
                    GetUserBag(false);
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
                    GetUserBag(false);
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
                    GetUserBag(false);
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
            //SetBalanceTextView();

            //Pre-selecciona el operador default
            //setDefaultOperator(ListaOperadores);

            //Fecha: 03-01-2017 -------------------------
            //refreshAllData();
            GetUserBag(false);
            //setDefaultOperator(ListaOperadores);




            Log.d("Resultado: ", Message);
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
                GetUserBag(false);
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
                GetUserBag(false);
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

    public void retrieveOperators()
    {
        YVScomSingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        StringsURL.OPERATORS,
                        null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response)
                            {
                                Log.d("Mensaje JSON ", response.toString());
                                ProcessOperatorsResponse(response);
                                RetrieveAmounts();
                                GetUserBag(false);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                HandleOperatorsVolleyError(error);
                            }
                        }
                )
                {

                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Token-Autorization", Token);
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }
                , 1); //Parametro de número de re-intentos
    }

    public void ProcessOperatorsResponse(JSONObject pResponse)
    {
        try
        {
            JSONObject operators = pResponse.getJSONObject("operators");
            JSONArray countryOperators = operators.getJSONArray("countryOperators");

            for (int i = 0; i < countryOperators.length(); i++)
            {
                Operator operator = new Operator();

                JSONObject jsonOperator = countryOperators.getJSONObject(i);
                operator.setID(jsonOperator.has("operatorID") ? jsonOperator.getInt("operatorID") : 0);
                operator.setOperatorName(jsonOperator.has("name") ? jsonOperator.getString("name") : "");
                operator.setLogo(jsonOperator.has("operatorLogo") ? jsonOperator.getString("operatorLogo") : "");
                operator.setLogoURL(jsonOperator.has("logoUrl") ? jsonOperator.getString("logoUrl") : "");
                operator.setLogoVersion(jsonOperator.has("logoVersion") ? jsonOperator.getInt("logoVersion") : 0);
                operator.setBrand(jsonOperator.has("brand") ? jsonOperator.getString("brand") : "");
                operator.setHexColor(jsonOperator.has("HexColor") ? jsonOperator.getString("HexColor") : "");
                operator.setRelevance(jsonOperator.has("Relevance") ? jsonOperator.getInt("Relevance") : 0);
                ListaOperadores.add(operator);
            }

            realmBD = RealmDatabase.getInstance(this);
            realmBD.DeleteInvalidOperators(ListaOperadores);

            setOperators();

            //Setea el showcase view despues que se lleno el adapter
            if (isFirstTime )
            {
                ExecuteShowcase();
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    public void HandleOperatorsVolleyError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if (pError instanceof TimeoutError)
        {
            Toast.makeText(Home.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
        }
        if (pError instanceof NoConnectionError)
        {
            Toast.makeText(Home.this, getString(R.string.internet_connecttion_msg), Toast.LENGTH_LONG).show();
        }
        else if (pError instanceof ServerError)
        {
            //if (statusCode == 502)
            if (statusCode == 401)
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
            else
            {
                Toast.makeText(Home.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            }
        }
        else if (pError instanceof NetworkError)
        {
            Toast.makeText(Home.this, getString(R.string.internet_connecttion_msg), Toast.LENGTH_LONG).show();
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
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


    public void setOperators()
    {
        //Setea el hint cuando no se ha seleccionado un operador
        //Se añade 2 veces porque pone el último como seleccionado
        //for (int i = 0; i < 2; i++)
        //{
        //    selectedOperatorAmounts.add(Data.AmountHint(Home.this));
        //}
        AmountAdapter = new AmountSpinnerAdapter(this, R.layout.custom_amount_spinner_item, R.id.tvAmount, selectedOperatorAmounts);
        AmountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerAmount = (Spinner) this.findViewById(R.id.spMontoRecarga);
        SpinnerAmount.setAdapter(AmountAdapter);
        SpinnerAmount.setSelection(AmountAdapter.getCount());


        OperatorSelected = false;
        //Seteando el adapter de Operadores
        GridViewOperators = (GridView) findViewById(R.id.gvOperadores);

        GridViewOperators.setNumColumns(ListaOperadores.size());

        OpeAdapter = new OperatorsAdapter(this, R.layout.custom_operator_gridview_item);
        //Refresh items in GridView
        OpeAdapter.notifyDataSetChanged();
        GridViewOperators.invalidateViews();

        GridViewOperators.setAdapter(OpeAdapter);

        GridViewOperators.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                OperatorSelected = true;
                SelectedOperatorName = ((Operator) parent.getItemAtPosition(position)).getOperatorName();
                mNMO = ((Operator) parent.getItemAtPosition(position)).getBrand();
                String HexColor = ((Operator) parent.getItemAtPosition(position)).getHexColor();

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    GridViewOperators.getChildAt(position).setBackground(getResources().getDrawable(R.drawable.custom_rounded_corner_selected));
                }
                else
                {
                    GridViewOperators.getChildAt(position).setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_rounded_corner_selected));
                }


                String Validation = ValidateOperatorAmounts(mNMO);
                if(Validation.equals(VALID))
                {
                    setServerAmounts(mNMO);
                    SetBalanceTextView(SelectedOperatorName);
                    ChangeBalanceColor(HexColor);
                }
                else if (Validation.equals(ERROR))
                {
                    String title = String.format(getString(R.string.title_operator_error), SelectedOperatorName);
                    createGenericDialog(title, getString(R.string.content_operator_error), getString(R.string.button_accept));
                }
                else
                {
                    //Crear dialogo aqui
                    CreateExpectationOperatorsDialog(getString(R.string.title_dialog_coming_soon), getString(R.string.content_dialog_coming_soon), getString(R.string.button_accept));
                }






            }
        });

        for (Operator item : ListaOperadores)
        {
            OpeAdapter.add(item);
        }

        //Fecha: 03-01-2016
        //Setea el default
        //setDefaultOperator(ListaOperadores);

    }

    public void setServerAmounts(final String pOperatorName)
    {
        selectedOperatorAmounts.clear();
        AmountAdapter.clear();

        //Escenario: si no hay conexion a internet
        //y el usuario clickeo un operador, entonces
        //como la lista 'Data.Amounts' está vacía, no pone nada en el spinner
        //por eso cuando esté vacía la llenará con el Hint.
        /*if (!Data.Amounts.isEmpty())
        {
            for (Amount item : Data.Amounts)
            {
                //Valida que MNO no venga Null, para evitar NullPointerException
                if (item.getMNO() != null && !item.getMNO().isEmpty())
                {
                    if (item.getMNO().equals(pOperatorName))
                    {
                        selectedOperatorAmounts.add(item);
                    }
                }
            }
        }
        else
        {
            selectedOperatorAmounts.add(Data.AmountHint(Home.this));
        }*/

        try
        {
            if (!Data.Amounts.isEmpty())
            {
                for (Amount item : Data.Amounts)
                {
                    //Valida que MNO no venga Null, para evitar NullPointerException
                    if (item.getMNO() != null && !item.getMNO().isEmpty())
                    {
                        if (item.getMNO().equals(pOperatorName))
                        {
                            if(!selectedOperatorAmounts.contains(item))
                            {
                                selectedOperatorAmounts.add(item);
                            }
                        }
                    }
                }
            }


            AmountAdapter.notifyDataSetChanged();

            //Si no hay montos del operador seleccionado, añade el hint 2 veces
             if(selectedOperatorAmounts.size() == 0)
            {
                for (int i = 0; i < 2; i++)
                {
                    selectedOperatorAmounts.add(Data.AmountHint(Home.this));
                }
            }
            else
            {
                selectedOperatorAmounts.add(Data.AmountHint(Home.this));
                /*String title = String.format(getString(R.string.title_operator_error), pOperatorName);
                createGenericDialog(title, getString(R.string.content_operator_error), getString(R.string.button_accept));*/
            }


            AmountAdapter = new AmountSpinnerAdapter(this, R.layout.custom_amount_spinner_item, R.id.tvAmount, selectedOperatorAmounts);
            AmountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerAmount = (Spinner) this.findViewById(R.id.spMontoRecarga);
            SpinnerAmount.setAdapter(AmountAdapter);
            SpinnerAmount.setSelection(AmountAdapter.getCount());
            SpinnerAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
                {
                    AmountTopup = ((Amount) parentView.getItemAtPosition(position)).getAmount();
                    PackageCode = ((Amount) parentView.getItemAtPosition(position)).getPackageCode();

                    String description = ((Amount) parentView.getItemAtPosition(position)).getDisplay();
                    SelectedAmuntItemDisplay = description;

                    /*if(selectedOperatorAmounts.size() <= 1)
                    {
                        if(description.equals(getResources().getString(R.string.spinner_hint)))
                        {
                            String title = String.format(getString(R.string.title_operator_error), pOperatorName);
                            createGenericDialog(title, getString(R.string.content_operator_error), getString(R.string.button_accept));
                        }
                    }*/

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView)
                {
                    // your code here
                }

            });


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void RetrieveAmounts()
    {
        final GridView gridOperators = (GridView) findViewById(R.id.gvOperadores);
        if (Data.Amounts.isEmpty())
        {

            Data.Amounts.clear();
            RetrievingAmounts = true;
            gridOperators.setEnabled(false);
            Log.i("Amounts", "Request para traer montos");

            /*if (!isFirstTime)
            {
                SwipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
                SwipeRefresh.setRefreshing(true);
            }*/


            Data.GetAmounts(Home.this, new Data.VolleyCallback()
            {
                @Override
                public void onResult(boolean result, JSONObject response)
                {
                    if (result)
                    {
                        RetrievingAmounts = false;
                        //SwipeRefresh.setRefreshing(false);
                        HideSwipe();
                        gridOperators.setEnabled(true);

                        getLocation();

                        setServerAmounts(mNMO);

                        /* Se obtiene la ubicacion despues de que
                        los montos se hayan obtenido para darle suficiente
                        tiempo a LocationRequest de conectar */
                        sendDeviceData(collectDeviceData());
                    }
                    else
                    {
                        getLocation();
                        RetrievingAmounts = false;
                        //SwipeRefresh.setRefreshing(false);
                        HideSwipe();
                        gridOperators.setEnabled(true);
                    }
                }
            });
        }
        else
        {
            setServerAmounts(mNMO);
        }
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

    public void GetUserBag(boolean isSwipe)
    {
        /*if (isSwipe)
        {
            SwipeRefresh.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            SwipeRefresh.setRefreshing(true);
        }*/

        YVScomSingleton.getInstance(Home.this).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, StringsURL.USERBAG, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                HideSwipe();
                Log.d("Mensaje JSON ", response.toString());
                ProcessBagResponse(response);
                //Fecha: 03-01-2016
                //Prueba---
                setDefaultOperator(ListaOperadores);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                HideSwipe();
                ProcessBagErrorResponse(error);
            }
        })
        {
            //Se añade el header para enviar el Token
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Token-Autorization", Token);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        }, 1); //Parametro de número de re-intentos
    }

    public void ProcessBagResponse(JSONObject pResponse)
    {
        String NewBalance = "0.00";
        try
        {
            JSONObject Bag = pResponse.getJSONObject("userBag");
            NewBalance = Bag.has("AvailableAmount") ? Bag.getString("AvailableAmount") : "";
            String newToken  = !pResponse.isNull("token") ? pResponse.getString("token") : sessionManager.getSavedToken();

            //Operators balance
            JSONArray operatorsBalance = Bag.getJSONArray("OperatorsBalance");
            updateOperatorsBalance(operatorsBalance);
            sessionManager.saveToken(newToken);

        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        sessionManager.SaveAvailableBalance(NewBalance);
        //SetBalanceTextView();
    }

    public void ProcessBagErrorResponse(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;

        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if (pError instanceof TimeoutError)
        {
            Toast.makeText(Home.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
        }
        if (pError instanceof NoConnectionError)
        {
            Toast.makeText(Home.this, getString(R.string.internet_connecttion_msg), Toast.LENGTH_LONG).show();
        }
        else if (pError instanceof ServerError)
        {
            //if (statusCode == 502)
            if (statusCode == 401)
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
            else
            {
                Toast.makeText(Home.this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            }
        }
        else if (pError instanceof NetworkError)
        {
            Toast.makeText(Home.this, getString(R.string.internet_connecttion_msg), Toast.LENGTH_LONG).show();
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
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

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
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

    public void ChangeBalanceColor(String pOperatorName)
    {
        tvCurrency.setTextColor(Color.parseColor(pOperatorName));
        tvBalance.setTextColor(Color.parseColor(pOperatorName));
    }

    public void SetBalanceTextView(String pSelectedOperator)
    {
        realmBD = RealmDatabase.getInstance(this);
        String balance = realmBD.OperatorBalanceByName(pSelectedOperator);
        String name = realmBD.OperatorName(pSelectedOperator);

        if(balance.isEmpty())
        {
            HashMap<String, String> Balance = sessionManager.GetAvailableBalance();
            String strBalance = Balance.get(SessionManager.KEY_BALANCE);

            double amount = Double.parseDouble(strBalance);
            DecimalFormat formatter = new DecimalFormat("#,###.##");
            formatter.setDecimalSeparatorAlwaysShown(true);
            formatter.setMinimumFractionDigits(2);

            tvAvailabeBalanceLabel.setText(getResources().getString(R.string.avialable_balance_label_generic));
            tvBalance.setText(formatter.format(amount));
        }
        else
        {
            String label = String.format(getResources().getString(R.string.avialable_balance_label), name);
            tvAvailabeBalanceLabel.setText(label);
            tvBalance.setText(balance);
        }

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

    int showcaseOperatorRuns;
    public void ExecuteShowcase()
    {
        if(!sessionManager.ShowcaseViewHasBeenShown())
        {
            final Target tgBalance = new ViewTarget(findViewById(R.id.rectangle));
            final Target tgPhone = new ViewTarget(findViewById(R.id.etPhoneNumber));
            final Target tgAmount = new ViewTarget(findViewById(R.id.spMontoRecarga));
            final Target tgButton = new ViewTarget(findViewById(R.id.btnEnvar));
            final Target tgContacts = new ViewTarget(findViewById(R.id.ibContacts));

            showcaseView = new ShowcaseView.Builder(Home.this).setTarget(tgBalance).setContentTitle(getString(R.string.sv_title_1)).setContentText(getString(R.string.sv_content_1)).setStyle(R.style.CustomShowcaseTheme2).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    switch (ShowCaseCounter)
                    {
                        case 0:

                            scrollView.fullScroll(View.FOCUS_DOWN);

                            Handler handler = new Handler();
                            if(ListaOperadores.size() != 0)
                            {
                                showcaseView.hideButton();
                            }
                            //showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(0).findViewById(R.id.ivOperador)), true)

                            int time = 0;
                            showcaseOperatorRuns = GridViewOperators.getChildCount();

                            for(int i = 0; i < GridViewOperators.getChildCount(); i++)
                            {
                                final int position = i;
                                time = time + 1000;
                                handler.postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        showcaseOperatorRuns = showcaseOperatorRuns -1;
                                        showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(position).findViewById(R.id.networkViewOperador)), true);

                                        if(showcaseOperatorRuns == 0)
                                        {
                                            showcaseView.showButton();
                                        }
                                    }
                                }, time);
                            }

                        /*handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(2).findViewById(R.id.ivOperador)), true);

                            }
                        }, 2000);
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showcaseView.setShowcase(new ViewTarget(GridViewOperators.getChildAt(3).findViewById(R.id.ivOperador)), true);

                            }
                        }, 3000);
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showcaseView.showButton();

                            }
                        }, 3500);*/

                            showcaseView.setContentTitle(getString(R.string.sv_title_2));
                            showcaseView.setContentText(getString(R.string.sv_content_2));
                            showcaseView.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);


                            break;
                        case 1:
                            showcaseView.setShowcase(tgPhone, true);
                            showcaseView.setContentTitle(getString(R.string.sv_title_3));
                            showcaseView.setContentText(getString(R.string.sv_content_3));
                            showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                            break;
                        case 2:
                            showcaseView.setShowcase(tgContacts, true);
                            showcaseView.setContentTitle(getString(R.string.sv_title_6));
                            showcaseView.setContentText(getString(R.string.sv_content_6));
                            showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                            break;
                        case 3:
                            showcaseView.setShowcase(tgAmount, true);
                            showcaseView.setContentTitle(getString(R.string.sv_title_4));
                            showcaseView.setContentText(getString(R.string.sv_content_4));
                            break;
                        case 4:
                            showcaseView.setShowcase(tgButton, true);
                            showcaseView.setContentTitle(getString(R.string.sv_title_5));
                            showcaseView.setContentText(getString(R.string.sv_content_5));
                            showcaseView.setButtonText(getString(R.string.sv_close_boton));
                            break;
                        case 5:
                            scrollView.fullScroll(View.FOCUS_UP);
                            showcaseView.hide();
                            askForLocationActivation();
                            sessionManager.SetShowcaseViewShown(true);
                            checkUserNickname();
                    }
                    ShowCaseCounter++;
                }
            }).build();

        }

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

        /*if (!TextUtils.isEmpty(securityPin))
        {
            securityPin = "";
        }*/

        return securityPin;
    }

    public void NavigateHistoryActivity()
    {
        Intent history = new Intent(getApplication().getApplicationContext(), HistorialVentas.class);
        startActivity(history);
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

    public boolean isVendorM()
    {
        boolean vendorM;
        HashMap<String, Boolean> MapVendor = sessionManager.GetVendorInfo();
        vendorM = MapVendor.get(SessionManager.KEY_VENDOR_M);

        return vendorM;
    }

    public static boolean containsID(Collection<Operator> pOpe, int pID)
    {
        for(Operator o : pOpe)
        {
            if(o != null && o.getID() == pID)
            {
                return true;
            }
        }
        return false;
    }


    public void updateOperatorsBalance(JSONArray pArray)
    {
        //Operators balance
        List<OperatorsBalance> operatorsBalanceList = new ArrayList<>();
        try
        {
            for (int i = 0; i < pArray.length(); i++ )
            {
                OperatorsBalance balance = new OperatorsBalance();
                JSONObject operator = pArray.getJSONObject(i);
                balance.setBalance(operator.has("balance") ? operator.getString("balance") : "");
                balance.setMobileOperator(operator.has("mobileOperator") ? operator.getString("mobileOperator") : "");
                balance.setOperatorId(operator.has("operatorId") ? operator.getInt("operatorId") : 0);

                operatorsBalanceList.add(balance);
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        realmBD = RealmDatabase.getInstance(this);
        realmBD.InsertMultipleOperatorsBalance(operatorsBalanceList);

    }


    /*
    * ****************************************************************************
    *       PUSH NOTIFICATIONS LOGIC
    * ****************************************************************************
    */

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean CheckGooglePlayServices()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                //apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "Dispositivo si tiene soporte para Google Play Services.");
            }
            else
            {
                Log.i(TAG, "Este dispositivo no tiene soporte para Google Play Services.");
                Toast.makeText(this, getString(R.string.google_play_not_supported), Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
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
        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
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

        //Screen lock
        //wakeLock.release();

        stopLocationUpdates();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isVisible = true;

        if (mGoogleApiClient != null)
        {
            // Resuming the periodic location updates
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates)
            {
                startLocationUpdates();
            }
        }

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "YVR Lock");

        //Registering location Receiver
        registerReceiver(mLocationReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isVisible = false;

        if (mGoogleApiClient != null)
        {
            if (mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver);
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


            /*ProgressDialog = new ProgressDialog(Home.this);
            ProgressDialog.setMessage(getString(R.string.dialog_signing_out));
            ProgressDialog.show();
            ProgressDialog.setCancelable(false);
            ProgressDialog.setCanceledOnTouchOutside(false);

            sessionManager.LogoutUser();
            return true;*/
        }
        return super.onOptionsItemSelected(item);
    }




    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   DATOS DEL DISPOSITIVO
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    public JSONObject collectDeviceData()
    {
        //Carrier Name
        String carrierName = mTelephonyManager.getNetworkOperatorName();

        //Network Type
        String networkType = getNetworkType();

        //dBm Signal Strength
        //int signalStrength = psListener.signalStrengthValue;
        int signalStrength = YvsPhoneStateListener.signalStrengthPercent;

        //Device manufacturer
        String Manufacturer = Build.MANUFACTURER;
        Manufacturer = Manufacturer.substring(0, 1).toUpperCase() + Manufacturer.substring(1).toLowerCase();

        //Device Model
        String Model = DeviceName.getDeviceName();

        //Location data, duh!!..
        //mLocationData = locationTracker.getLocation();


        JSONObject deviceData = new JSONObject();
        try
        {
            deviceData.put("carrierName", carrierName);
            deviceData.put("networkType", networkType);
            deviceData.put("dBm", signalStrength);
            deviceData.put("phoneModel", Model);
            deviceData.put("phoneManufacturer", Manufacturer);
            deviceData.put("latitude", mLocationData.getLatitude());
            deviceData.put("longitude", mLocationData.getLongitude());

        } catch (JSONException ex)
        {
            ex.printStackTrace();
        }

        return deviceData;

    }

    private String getNetworkType()
    {
        int networkType = mTelephonyManager.getNetworkType();

        switch (networkType)
        {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
        }
        throw new RuntimeException("New type of network");
    }

    public void sendDeviceData(JSONObject pDeviceData)
    {
        if(isLocationServiceEnabled())
        {
            if(mLocationData.getLatitude() != 0 && mLocationData.getLongitude() != 0)
            {
                YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(Request.Method.POST, StringsURL.CEOA_DEVICE_DATA, pDeviceData, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DeviceData ", response.toString());
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        String errorDetails = error.getMessage();

                    }
                })
                {
                    //Se añade el header para enviar el Token
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("apikey", StringsURL.CEO_ANALYTICS_APIKEY);
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                }, 1);
            }
        }
    }



    /*
    * ***************************************************************************************************
    * ***************************************************************************************************
    *
    *   GEO-LOCATION
    *
    * ***************************************************************************************************
    * ***************************************************************************************************
    */

    /**
     * Method to display the location on UI
     * */
    private void getLocation()
    {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else
        {
            if(mGoogleApiClient != null)
            {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null)
                {
                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();

                    mLocationData.setLongitude(longitude);
                    mLocationData.setLatitude(latitude);
                }
                else
                {
                    Log.i(TAG, "Couldn't get the location. Make sure location is enabled on the device");
                }
            }
        }
    }

    /**
     * Method to toggle periodic location updates
     * */
    private void startPeriodicLocationUpdates()
    {
        if (mRequestingLocationUpdates)
        {
            // Starting the location updates
            startLocationUpdates();
            Log.d(TAG, "Periodic location updates started!");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                /*Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();*/
                Log.i(TAG, "This device does not support Google Play Services");
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates()
    {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                //Log.i(TAG, "Location has been denied on device by user.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            }




            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, 0);

    /* Only if you want to keep features using external storage */
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }
        else
        {
            if(mGoogleApiClient != null)
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates()
    {
        try
        {
            if(mGoogleApiClient != null)
            {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0)
    {
        // Once connected with google api, get the location
        //displayLocation();

        if (mRequestingLocationUpdates)
        {
            //startLocationUpdates();
            startPeriodicLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0)
    {
        if(mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        // Assign the new location
        mLastLocation = location;
        if(sessionManager.isUserLocationVisible())
            FirebaseInteractor.getFirebaseInstance(Home.this).writeVendorData(mLastLocation);
        else
            FirebaseInteractor.getFirebaseInstance(Home.this).deleteVendorLocation();

        Log.i(TAG, "Location DID changed");

    }

    public boolean isLocationServiceEnabled()
    {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;

        if (locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        }

        try
        {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex)
        {
            //do nothing...
        }

        try
        {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex)
        {
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }

    public void askForLocationActivation()
    {
        if(!isLocationServiceEnabled())
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
            alertDialog.setTitle(getString(R.string.title_activate_location));
            alertDialog.setMessage(getString(R.string.content_activate_location));
            alertDialog.setCancelable(false);
            alertDialog.setNeutralButton("ACTIVAR", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    public void setDefaultOperator(List<Operator> pListaOperadores)
    {
        try
        {
            if(pListaOperadores.size() > 0)
            {
                OperatorSelected = true;

                Operator defaultOperator = pListaOperadores.get(0);

                SelectedOperatorName = defaultOperator.getOperatorName();
                mNMO = defaultOperator.getBrand();
                String HexColor = defaultOperator.getHexColor();

                //Se seterán los montos una vez que ya se obtuvieron los operadores
                setServerAmounts(mNMO);
                SetBalanceTextView(SelectedOperatorName);
                ChangeBalanceColor(HexColor);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void ResetDefaults(List<Operator> pListaOperadores)
    {
        try
        {
            txtPhoneNumber.setText("");
            //GridView
            setOperators();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void enableControls(boolean setEnabled)
    {
        btnTopup.setEnabled(setEnabled);
        txtPhoneNumber.setEnabled(setEnabled);
    }

    public void CreateExpectationOperatorsDialog(String pTitle, String pMessage, String pButton)
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
                setDefaultOperator(ListaOperadores);
                ResetDefaults(ListaOperadores);
            }
        });
        alertDialog.show();
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

    public void printOperatorsBalances()
    {
        realmBD = RealmDatabase.getInstance(this);
        List<OperatorsBalance> operators = realmBD.GetOperatorsBalance();

        for(OperatorsBalance item : operators)
        {
            Log.i("Realm: ", "opeID=" + String.valueOf(item.getOperatorId()) + ", name=" + item.getMobileOperator() + ", balance=" + item.getBalance());
        }
    }


    public String ValidateOperatorAmounts(String pSelectedOperator)
    {
        String result = VALID;

        selectedOperatorAmounts.clear();
        AmountAdapter.clear();

        try
        {
            if (!Data.Amounts.isEmpty())
            {
                for (Amount item : Data.Amounts)
                {
                    //Valida que MNO no venga Null, para evitar NullPointerException
                    if (item.getMNO() != null && !item.getMNO().isEmpty())
                    {
                        if (item.getMNO().equals(pSelectedOperator))
                        {
                            if (!selectedOperatorAmounts.contains(item))
                            {
                                selectedOperatorAmounts.add(item);
                            }
                        }
                    }
                }
            }

            if (selectedOperatorAmounts.size() == 1)
            {
                Amount amount = selectedOperatorAmounts.get(0);

                if (amount.getAmount() <= 0 && amount.getMNO().equals(pSelectedOperator) && !amount.getDisplay().equals(getString(R.string.spinner_hint)))
                {
                    result = NOTVALID; //No es válido
                }
                else
                {
                    result = VALID;
                }
            }
            else if (selectedOperatorAmounts.size() <= 0)
            {
                result = ERROR;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return result;
    }

    public void AskForRating()
    {
        int recharges = sessionManager.GetNumberOfRecharges();
        if(recharges == 5)
        {
            if(!sessionManager.userHasAnsweredForRating())
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
                alertDialog.setTitle(getString(R.string.dialog_title_rate_us));
                alertDialog.setMessage(getString(R.string.dialog_content_rate_us));
                alertDialog.setPositiveButton(getString(R.string.dialog_button_positive_rate_us), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.SetUserRateResponse(true);
                        final String appPackageName = getApplicationContext().getPackageName();
                        try
                        {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        }
                        catch (android.content.ActivityNotFoundException anfe)
                        {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.dialog_button_negative_rate_us), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sessionManager.SetUserRateResponse(true);
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }

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

                        retrieveOperators();

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


    private void setUserCountryCurrency()
    {
        try
        {
            tvCurrency.setText(sessionManager.getMoneySymbol());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /*
    *
    *
    *   NICKNAME
    *
    *
    */

    public void checkUserNickname()
    {
        String nickname = sessionManager.getUserNickame();
        if(TextUtils.isEmpty(nickname))
        {
            nicknameClickListener = new NicknameDialogBuilder.CustomOnClickListener()
            {
                @Override
                public void onAcceptClick()
                {
                    toggleLabelVisible(false);
                    insertNickname(nicknameDialogBuilder.getInputText());
                }

                @Override
                public void onCancelClick()
                {
                    //CERRAR SESION
                    sessionManager.LogoutUser(false);
                }
            };

            nicknameDialogBuilder = new NicknameDialogBuilder(this, nicknameClickListener);
            nicknameDialogBuilder.setCancelable(false);
            nicknameDialogBuilder.show();
        }
    }

    public void toggleLabelVisible(boolean pVisible)
    {
        nicknameDialogBuilder.nicknameAlreadyExists(pVisible);
    }

    public void insertNickname(final String pNickname)
    {
        if(nicknameDialogBuilder.isValidUsername())
        {
            nicknameDialogBuilder.lockViews();
            JSONObject requestBody = new JSONObject();
            try
            {
                requestBody.put("NickName", pNickname);
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

            YVScomSingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(
                    Request.Method.POST,
                    StringsURL.INSERT_GAMER_PROFILE,
                    requestBody,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            sessionManager.saveUserNickname(pNickname);
                            processNicknameResponse();
                        }
                    }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    processNicknameError(error);
                }
            })
            {
                //Se añade el header para enviar el Token
                @Override
                public Map<String, String> getHeaders()
                {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Token-Autorization", Token);
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            }, 1); //Parametro de número de re-intentos
        }
        else
        {
            nicknameDialogBuilder.resetViews();
        }
    }

    public void processNicknameResponse()
    {
        nicknameDialogBuilder.dismiss();
        String titulo = getString(R.string.label_nickname_success_title);
        String content = getString(R.string.label_nickname_success_content);
        String button = getString(R.string.btn_accept);
        CustomDialogCreator.CreateFullScreenDialog(titulo, content, null, null, button, "NEWACTION", false, false, null);
    }

    public void processNicknameError(VolleyError pError)
    {
        int statusCode = 0;
        NetworkResponse networkResponse = pError.networkResponse;
        nicknameDialogBuilder.resetViews();

        if (networkResponse != null)
        {
            statusCode = networkResponse.statusCode;
        }

        if (pError instanceof TimeoutError || pError instanceof NoConnectionError)
        {
            nicknameDialogBuilder.setFeedbackLabelText(getString(R.string.something_went_wrong_try_again));
        }
        else if (pError instanceof ServerError)
        {
            //if (statusCode == 502)
            if (statusCode == 401)
            {
                sessionManager.LogoutUser(false);
            }
            else if(statusCode == 406)
            {
                toggleLabelVisible(true);
            }
        }
        else if (pError instanceof NetworkError)
        {
            nicknameDialogBuilder.setFeedbackLabelText(getString(R.string.internet_connecttion_msg));
        }
        else if (pError instanceof AuthFailureError)
        {
            if (statusCode == 401)
            {
                sessionManager.LogoutUser(false);
            }
            else if(statusCode == 406)
            {
                toggleLabelVisible(true);
            }
            else
            {
                nicknameDialogBuilder.setFeedbackLabelText(getString(R.string.internet_connecttion_msg));
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

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
            {
                // Make an action or refresh an already managed state.
                if(!isLocationServiceEnabled())
                {
                    changeLocationMenuStatus(false);
                }
            }
        }
    };

}