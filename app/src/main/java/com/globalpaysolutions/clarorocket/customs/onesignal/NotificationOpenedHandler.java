package com.globalpaysolutions.clarorocket.customs.onesignal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.globalpaysolutions.clarorocket.DetalleNotificacion;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Josué Chávez on 01/11/2017.
 */

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler
{
    private static final String TAG = NotificationOpenedHandler.class.getSimpleName();
    private Context mContext;

    public NotificationOpenedHandler(Context context)
    {
        this.mContext = context;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result)
    {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String customKey;

        if (data != null)
        {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i(TAG, "customkey set with value: " + customKey);
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i(TAG, "Button pressed with id: " + result.action.actionID);

        try
        {
            Intent intent = new Intent(mContext, DetalleNotificacion.class);
            intent.putExtra("notifTitle", result.notification.payload.title);
            intent.putExtra("notifMessage", result.notification.payload.body);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
