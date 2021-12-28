package com.example.neighbormaterebuild.network;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.neighbormaterebuild.config.Config;
import com.example.neighbormaterebuild.model.BaseResponse;
import com.example.neighbormaterebuild.service.PushTokenService;
import com.example.neighbormaterebuild.ui.MainActivity;
import com.example.neighbormaterebuild.R;
import com.example.neighbormaterebuild.config.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";

    private int notificationID = 0;

    public void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        HttpHeaders.getInstance().setPushToken(token);

//                        Log.d(TAG, "Push Token::: " + token);
//                        pushToken();
                    }
                });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        HttpHeaders.getInstance().setPushToken(token);
        //Log.d(TAG, "New Push Token::: " + token);
        pushToken();
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

//        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (remoteMessage.getData().size() > 0) {
                //Log.e(TAG, "Message data payload: " + remoteMessage.getData());

                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                if (body == null || body.isEmpty()) body = remoteMessage.getData().get("message");
                int badge = Integer.parseInt(remoteMessage.getData().get("badge"));
                showNotification(title, body, badge);

                LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
                Intent intent = new Intent(Constants.REQUEST_ACCEPT_BADGE);
                intent.putExtra("badge", badge);
                broadcaster.sendBroadcast(intent);

                Log.d(TAG, "badge: " + badge);
                ShortcutBadger.applyCount(getApplicationContext(), badge);
            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }

        }

        super.onMessageReceived(remoteMessage);

    }

    private void showNotification(String messageTitle, String messageBody, int badge) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (notificationID < 9) {
            notificationID = notificationID + 1;
        } else {
            notificationID = 0;
        }

        if (badge > 0) notificationID = badge;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = getString(R.string.notification_channel_id);
        String channelName = getString(R.string.notification_channel_name);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    public void pushToken() {
        Client.getClient(Config.API_URL).create(PushTokenService.class)
                .pushToken().enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                //Log.e(TAG, "Header:" + HttpHeaders.getInstance().headers());
                //Log.e(TAG, "Push token: " + response.code() + ": " + response.message());

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //Log.e(TAG, "Push token to server fail" + t);
                //Log.e(TAG, "Push token to server fail" + call.request());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

    }
}
