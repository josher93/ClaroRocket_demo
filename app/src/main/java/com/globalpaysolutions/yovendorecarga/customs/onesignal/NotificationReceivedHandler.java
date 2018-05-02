package com.globalpaysolutions.yovendorecarga.customs.onesignal;

import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Josué Chávez on 01/11/2017.
 */

public class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler
{
    private static final String TAG = NotificationReceivedHandler.class.getSimpleName();

    @Override
    public void notificationReceived(OSNotification notification)
    {
        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null)
        {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i(TAG, "customkey set with value: " + customKey);
        }
    }
}
