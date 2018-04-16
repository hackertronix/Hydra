package io.execube.monotype.deimos.Utils;

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
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import io.execube.monotype.deimos.R;
import io.execube.monotype.deimos.common.HomeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
  private static final String TAG = "FirebaseMessageService";

  @Override public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);

    String notificationTitle = null, notificationBody = null;

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
      notificationTitle = remoteMessage.getNotification().getTitle();
      notificationBody = remoteMessage.getNotification().getBody();
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
    sendNotification(notificationTitle, notificationBody);
  }

  private void sendNotification(String title,String messageBody) {
    Intent intent = new Intent(this, HomeActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        PendingIntent.FLAG_ONE_SHOT);

    String channelId = getString(R.string.default_notification_channel_id);
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(this, channelId)
            .setColorized(true)
            .setSmallIcon(R.drawable.ic_notification_bell)
            .setColor(R.color.colorAccent)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setChannelId(channelId)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(channelId,
          "Channel human readable title",
          NotificationManager.IMPORTANCE_DEFAULT);
      notificationManager.createNotificationChannel(channel);
    }

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
  }


}
