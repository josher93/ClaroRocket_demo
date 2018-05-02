package com.globalpaysolutions.yovendorecarga.core;

import android.app.Application;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import com.globalpaysolutions.yovendorecarga.customs.onesignal.NotificationOpenedHandler;
import com.globalpaysolutions.yovendorecarga.customs.onesignal.NotificationReceivedHandler;
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
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .setNotificationReceivedHandler(new NotificationReceivedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(false)
                .init();

        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        // OneSignal.syncHashedEmail(userEmail);
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
