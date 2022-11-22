package android.example.uptoskills.services

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.example.uptoskills.MainActivity
import android.example.uptoskills.R
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyNotifications : FirebaseMessagingService() {
    private val TAG = "FCMListenerService"
    private val MESSAGE_CHANNEL_ID = "com.example.uptoskills" + "Message"
    private val MSG_NOTIFICATION_DESCRIPTION = "New Message Notification"
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: NotificationCompat.Builder

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("newToken", p0)
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", p0).apply()
    }

    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // First case when notifications are received via
        // data event
        // Here, 'title' and 'message' are the assumed names
        // of JSON
        // attributes. Since here we do not have any data
        // payload, This section is commented out. It is
        // here only for reference purposes.
        if(remoteMessage.data.isNotEmpty()){
            showNotification(remoteMessage.getData().get("title"),
                remoteMessage.getData().get("message"));
        }

        // Second case when notification payload is
        // received.
        Log.e("recieved", "yes")
        if (remoteMessage.notification != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            showNotification(
                remoteMessage.notification!!.title,
                remoteMessage.notification!!.body)
        }
    }


    // Method to display the notifications
    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(
        title: String?,
        message: String?,
    ) {
        // Pass the intent to switch to the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        // Assign channel ID
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Pass the intent to PendingIntent to start the
        // next Activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        builder = NotificationCompat.Builder(applicationContext,
            MESSAGE_CHANNEL_ID)
            .setSmallIcon(R.drawable.uptoskills_logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000,
                1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(message)

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            notificationChannel = NotificationChannel(MESSAGE_CHANNEL_ID,
                MSG_NOTIFICATION_DESCRIPTION,
                NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            notificationChannel.setSound(uri, attributes)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
                .setChannelId(MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.uptoskills_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(uri)
                .setContentIntent(pendingIntent)
        } else {
            builder = NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.uptoskills_logo)
                .setContentTitle(title)
                .setContentText(message) //.setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(uri)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(0, builder.build())
    }

    companion object {
        fun getToken(context: Context): String? {
            return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty")
        }
    }

}