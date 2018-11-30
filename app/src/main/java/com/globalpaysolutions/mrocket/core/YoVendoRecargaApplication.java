package com.globalpaysolutions.mrocket.core;

import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import com.globalpaysolutions.mrocket.customs.onesignal.NotificationOpenedHandler;
import com.onesignal.OneSignal;

/**
 * Created by Josué Chávez on 01/11/2017.
 */

public class YoVendoRecargaApplication extends MultiDexApplication
{
    private static YoVendoRecargaApplication appSingleton;

    private static YoVendoRecargaApplication getInstane()
    {
        return appSingleton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        appSingleton = this;
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
    }
}
