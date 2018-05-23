package com.globalpaysolutions.clarorocket.customs;

import java.net.URL;

/**
 * Created by Geovanni on 02/04/2016.
 */
public final class StringsURL
{
    //Servidor para PRODUCCION
    //public final static String URL_BASE = "http://csncusgats.cloudapp.net:82/v1/";

    //PRE-PRODUCCIÓN (Enviando recargas de prueba)
    //private final static String URL_BASE = "http://csncusgats.cloudapp.net:8074/v1/";

    //API V2 - PRE-PRODUCCION
    //private final static String URL_BASE = "http://csncusgats1.northcentralus.cloudapp.azure.com:8073/v1/";

    //PRODUCCIÓN PRUEBAS
    //private final static String URL_BASE = "http://csncusgats.cloudapp.net:8074/PreTem/";

    //Servidor para DESARROLLOm
    //public final static String URL_BASE = "http://csncusgats.cloudapp.net:8073/v1/";

    //Direccion con cifrado
    //TODO: Cambiar a esta URL antes de publicar
    public final static String URL_BASE = "http://api.clarorocket.com/V1/Rocket/";

    public static final String YVR_API = "https://api.yovendorecarga.com/";

    //BEBUG::::
    //private final static String URL_BASE = "192.168.1.32:8082/";

    //  :::::   LOCALHOST API   :::::
    //public final static String URL_BASE = "http://10.0.2.2:49435/";

    //:::::   LOCALHOST API   :::::
    //public final static String URL_BASE = "http://192.168.1.32:8084/";

    //public final static String URL_BASE = "http://192.168.1.30:61123/";



    public final static String SIGNIN = URL_BASE + "signin/";

    public final static String AUTH_SIGNIN = URL_BASE + "auth/signin/";

    public final static String TOPUP = YVR_API + "topup/";

    public final static String PASSWORD = URL_BASE + "password";

    public final static String HISTORY_GMT0 = URL_BASE + "history/gmt0";

    public final static String HISTORY = URL_BASE + "history";

    public final static String PROFILE = URL_BASE + "profile";

    public final static String DEPOSIT = URL_BASE + "deposito";

    public final static String TOPUPPAYMENT = URL_BASE + "topuppayment";

    public final static String TOPUPPAYMENT_SINGLE = URL_BASE + "topuppayment/single";

    public final static String SIGNOUT = URL_BASE + "signout";

    public final static String USERBAG = URL_BASE + "userbag";

    public final static String PRODUCTS = URL_BASE + "products/";

    public final static String DEVICEREGISTRATION = URL_BASE + "deviceregistration/";

    public final static String NOTIFICATIONSHISTORY = URL_BASE + "getnotifications/";

    public final static String BALANCREQUEST = URL_BASE + "balancerequest";

    public final static String BANKS = URL_BASE + "banks/";

    public final static String OPERATORS = URL_BASE + "operators/";

    public final static String USER_NOTIFICATIONS = URL_BASE + "getUserNotifications/";

    public final static String REFERRED = URL_BASE + "referred/";

    public final static String VALIDATE_VERSION = URL_BASE + "validateversion";

    public final static String PROMOTIONS = URL_BASE + "promotions";

    public final static String INSERT_GAMER_PROFILE = URL_BASE + "InsertGameProfile";

    public final static String INSERT_FACEBOOK_PROFILE = URL_BASE + "InsertUser";

    public final static String TOPUP_REQUESTS = URL_BASE + "GetPendingTopUps";

    public final static String RESPONSE_PENDING_REQUEST = URL_BASE + "ResponseToPendingRequest";

    public final static String MARK_NOTIFICATION_READ = URL_BASE + "setNotificationRead/";

    public static final String FAVORITES_LIST = URL_BASE + "FavoriteNumber/Get";

    public static final String FAVORITES_CREATE = URL_BASE + "FavoriteNumber/Create";

    public static final String FAVORITES_DELETE = URL_BASE + "FavoriteNumber/Delete";

    public static final String ROCKET_BALANCE = "GetBalanceRocket";

    public static final String ROCKET_PAYMENT = "GetPaymentHistory";

    /**
     *
     *
     *  CEOAnalytics API
     *
     **/

    //Desarrollo
    //public final static String CEO_ANALYTICS_URL_BASE = "http://csncusgats.cloudapp.net:8076/v1/";

    //Pre-Produccion

    public static final String ROCKET_BALANCE_PAYMENT =  "rocketpayment";
    public static final String ROCKET_SALE_DETAIL = "getSaleDetail";


    public static String CEO_ANALYTICS_APIKEY = "a1bXuav8tqtXQWPALOhdQJvAQ1IK8esE4d6YYGBk+qc=";


    /*
    *
    *
    *
    *   OTRAS URLS
    *
    *
    */

    public static final String YVR_WEB = "https://yovendorecarga.com/?country=null";
    public static final String YVR_FACEBOOK = "https://www.facebook.com/yovendorecarga/";
    public static final String YVR_HASHTAG1 = "#PidemeUnaRecarga";
    public static final String YVR_HASHTAG2 = "#YoVendoRecarga";
    public static final String YVR_HASHTAG3 = "#OtraRecargaVendida";

    /**
     *
     *  URL PARA PROBAR HTTP STATUS CODES
     *
     *  GET /status
     *  http://apps.testinsane.com/rte/status/{code}/{delay}
     *
     *  POST /status
     *  http://apps.testinsane.com/rte/status/{code}/{delay}
     *
     * */

    public final static String TEST_TIMEOUT = "http://apps.testinsane.com/rte/status/200/25";
    public final static String TEST_INVALID_TOKEN = "http://apps.testinsane.com/rte/status/505/3";
    public final static String TEST_SERVER_ERROR = "http://apps.testinsane.com/rte/status/404/3";
    public final static String TEST_INVALID_CREDENTIALS = "http://apps.testinsane.com/rte/status/403/3";
    public final static String TEST_INSUFFICENT_BALANCE = "http://apps.testinsane.com/rte/status/503/3";

    public final static String TEST_TOPUP_INVALID_TOKEN = "http://apps.testinsane.com/rte/status/502/3";
    public final static String TEST_TOPUP_INSUFFICENT_BALANCE = "http://apps.testinsane.com/rte/status/503/3";
    public final static String TEST_TOPUP_GENERAL_ERROR = "http://apps.testinsane.com/rte/status/505/3";
    public final static String TEST_TOPUP_GATS_ERROR = "http://apps.testinsane.com/rte/status/403/3";
}
