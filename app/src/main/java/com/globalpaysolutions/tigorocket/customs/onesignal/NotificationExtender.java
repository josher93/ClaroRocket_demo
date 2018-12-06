package com.globalpaysolutions.tigorocket.customs.onesignal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.globalpaysolutions.tigorocket.R;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Josue Chavez on 01/11/2017.
 */

public class NotificationExtender extends NotificationExtenderService
{
    final static String TAG = NotificationExtender.class.getSimpleName();

    final static String GROUP_KEY_NOTIF = "yovendorecarga_default_notification";

    private OSNotificationReceivedResult mNotification;


    @Override
    protected boolean onNotificationProcessing(final OSNotificationReceivedResult notification)
    {
        mNotification = notification;

        OverrideSettings overrideSettings = new OverrideSettings();

        overrideSettings.extender = new NotificationCompat.Extender()
        {
            @Override
            public NotificationCompat.Builder extend(final NotificationCompat.Builder builder)
            {
                builder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.AppGreen))
                        .setAutoCancel(true)
                        .setSmallIcon(getNotificationIcon())
                        .setGroupSummary(true)
                        .setGroup(GROUP_KEY_NOTIF)
                        .setContentText(mNotification.payload.body);

                if(!TextUtils.isEmpty(mNotification.payload.bigPicture))
                {
                    Bitmap image = getBitmapfromUrl(mNotification.payload.bigPicture);

                    if(image != null)
                    {
                        final NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                        bigPictureStyle.bigPicture(image);
                        bigPictureStyle.setSummaryText(mNotification.payload.body);
                        builder.setStyle(bigPictureStyle);
                    }
                    else
                    {
                        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(mNotification.payload.body));
                    }
                }
                else
                {
                    builder.setStyle(new NotificationCompat.BigTextStyle().bigText(mNotification.payload.body));
                }

                return builder;
            }
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        Log.d(TAG, "Notification displayed with id: " + displayedResult.androidNotificationId);

        return true;
    }

    private int getNotificationIcon()
    {
        return R.drawable.ic_notification;
    }

    /*
    * To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl)
    {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
