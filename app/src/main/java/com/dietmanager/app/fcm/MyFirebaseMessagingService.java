package com.dietmanager.app.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.SplashActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.NotificationData;

import java.util.Arrays;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    NotificationData customData;
    String pushMessage;
    int orderId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getData());
            if (remoteMessage.getData().containsKey("custom")) {
                Log.d(TAG, "CustomData" + remoteMessage.getData().get("custom"));
                customData = new Gson().fromJson(remoteMessage.getData().get("custom"), NotificationData.class);
            }
//            Log.d(TAG, "onMessageReceived: " + customData.getCustomData().get(0).getOrderId());
            /*orderId = Integer.parseInt(remoteMessage.getData().get("message")
                    .substring(0, (remoteMessage.getData().get("message").indexOf(getString(R.string.order_id)) +
                            getString(R.string.order_id).length())));*/
            pushMessage = remoteMessage.getData().get("message")
                    .substring(0, (remoteMessage.getData().get("message").indexOf(getString(R.string.order_id)) +
                            getString(R.string.order_id).length())).replace(getString(R.string.order_id), "");
            orderId = Integer.parseInt(remoteMessage.getData().get("message").replace(pushMessage, "")
                    .replace(getString(R.string.order_id), ""));
            Log.d(TAG, "FCM Notification " + orderId);
            //Calling method to generate notification
            sendNotification(pushMessage);
        } else {
            Log.d(TAG, "FCM Notification failed");
        }
    }

    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedHelper.putKey(getApplicationContext(), "device_token", "" + s);
        Log.e(TAG, "" + s);
        Log.e("New_Token", s);
    }

    private void sendNotification(String messageBody) {
        Log.d(TAG, "messageBody " + messageBody);
        GlobalData.access_token = SharedHelper.getKey(getApplicationContext(), "access_token");
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        if (customData != null && customData.getCustomData().get(0).getOrderId() != null) {
            GlobalData.ORDER_STATUS = Arrays.asList("ORDERED", "RECEIVED", "ASSIGNED", "PROCESSING", "REACHED", "PICKEDUP", "ARRIVED", "COMPLETED");
            intent.putExtra("customData", customData);
            intent.putExtra("order_status", "ongoing");
        } else {
            intent.putExtra("order_status", "dispute");
        }
        intent.putExtra("orderId", orderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Notification", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "PUSH");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(messageBody);
        long when = System.currentTimeMillis();         // notification time
        String CHANNEL_ID = "my_channel_01";    // The id of the channel.
        CharSequence name = "Channel human readable title";// The user-visible name of the channel.
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            importance = NotificationManager.IMPORTANCE_HIGH;
        Notification notification;
        notification = mBuilder
                .setWhen(when)
                //                .setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(inboxStyle)
                .setWhen(when)
                .setAutoCancel(true)
                .setSmallIcon(getNotificationIcon(mBuilder))
                .setContentText(messageBody)
                .setChannelId(CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .build();
        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationChannel mChannel = new android.app.NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(0, notification);
    }

    /*private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            return R.drawable.ic_stat_push;
        } else return R.drawable.ic_stat_push;
    }*/

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            return R.drawable.ic_stat_push;
        } else {
//            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            return R.drawable.ic_stat_push;
        }
    }
}