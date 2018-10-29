package mta.com.final_project.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import mta.com.final_project.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class PushNotification extends FirebaseMessagingService {
    private FirebaseAuth mAuth;
    private static final String TAG ="PushNotificationService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(currentUser);
                ref.child("notificationTokens").child(s).setValue(true);

        }



    public PushNotification() {
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.e(TAG, "onMessageReceived() >>");
        String title = "title";
        String body = "body";
        int icon = R.drawable.common_google_signin_btn_text_dark_normal_background;
        Uri soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Map<String,String> data;
        RemoteMessage.Notification notification;


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());


        if (remoteMessage.getNotification() == null) {
            Log.e(TAG, "onMessageReceived() >> Notification is empty");
        } else {
            notification = remoteMessage.getNotification();
            title = notification.getTitle();
            body = notification.getBody();
            Log.e(TAG, "onMessageReceived() >> title: " + title + " , body="+body);
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() == 0) {
            Log.e(TAG, "onMessageReceived() << No data doing nothing");
            return;
        }


        //parse the data
        data = remoteMessage.getData();
        Log.e(TAG, "Message data : " + data);

        String value = data.get("title");
        if (value != null) {
            title = value;
            title = title + mAuth.getCurrentUser().getDisplayName();
        }

        value = data.get("body");
        if (value != null) {
            body = value;
        }

        value = data.get("small_icon");
        if (value != null  && value.equals("alarm")) {
            icon = R.drawable.ic_account_box_black_24dp;
        }
        value = data.get("sound");
        if (value != null) {
            if (value.equals("alert")) {
                soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            } else if (value.equals("ringtone")) {
                soundRri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        Intent intent = new Intent(this, HomeActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, null)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(icon)
                        .setSound(soundRri);


        value = data.get("Action");
        if (value != null) {
             if (value.contains("Message")) {
                intent = new Intent(this, HomeActivity.class);
                 intent.putExtra("openPage", value);
                PendingIntent pendingShareIntent = PendingIntent.getActivity(this, 0 , intent,
                        PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.common_google_signin_btn_text_dark,"Go to Market!",pendingShareIntent));
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());

        Log.e(TAG, "onMessageReceived() <<");

    }
}
