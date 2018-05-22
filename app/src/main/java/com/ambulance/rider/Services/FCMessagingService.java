package com.ambulance.rider.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ambulance.rider.Common.Common;
import com.ambulance.rider.MainActivity;
import com.ambulance.rider.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sumit on 18-Mar-18.
 */

public class FCMessagingService extends FirebaseMessagingService {

    private String title, body;
    private final int NOTIFICATION_ID = 1556;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {
            if (remoteMessage.getData() != null) {
                title = remoteMessage.getData().get("title");
                body = remoteMessage.getData().get("body");
            }

            Log.d("onMessageReceived: ", title + " " + body);

            if (!body.isEmpty() && (body.equals("Request cancelled") || body.equals("You reached your destination"))) {
                Common.isBooked = false;
                Common.emergencyRequestBookingID = "";
                showNotification(title, body);
                MainActivity.fragmentManager.popBackStack();
            } else if (!body.isEmpty()) {
                showNotification(title, body);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showNotification(String title, String msg) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(msg);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                .setStyle(inboxStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentText(msg)
                .build();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("NOTIFICATION", "Ride Notification", importance);
            mChannel.setDescription("Ride Notification");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
