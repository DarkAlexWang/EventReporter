package com.laioffer.eventreporter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From:" + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload:" + remoteMessage.getData());

            if (/* Check if data need to be processed by long running job */ true) {
                // FOr long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                // ScheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }
        sendNotification(remoteMessage);
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body:" +
                remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
       Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, "sendNotification(remoteMessage)");
        Intent intent = new Intent(this, EventActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Define pending intent to trigger activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create Notification accourding to builder pattern

        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, "EventReporter");
            String imgUri = remoteMessage.getData().get("imgUri");

            if (imgUri != null && !imgUri.isEmpty()) {
                notificationBuilder.setLargeIcon(Utils.getBitmapFromURL(imgUri));
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
                // Cannot receive notification when no image url
                Log.d(TAG, "no image bitmap: " + bitmap.toString());
                notificationBuilder.setLargeIcon(bitmap);
            }
                notificationBuilder.setSmallIcon(R.drawable.icon)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("description"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

        // Get Notification Manager
        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Send Notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}
