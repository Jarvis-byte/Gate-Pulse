package com.example.fmoapplication.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.example.fmoapplication.R;
import com.example.fmoapplication.ViewVisitor;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;

import java.util.Map;

public class CustomMessagingService extends FirebaseMessagingService {

    NotificationManager notificationManager;
    Notification notification;

    Uri defaultSoundUri;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences sh = this.getSharedPreferences("application", Context.MODE_PRIVATE);
        int fromWhere = sh.getInt("AddedFrom", -1);
        String nameOfVisitor = sh.getString("Visitor_name", "");

        //for seen
        SharedPreferences sh_seen = PreferenceManager.getDefaultSharedPreferences(CustomMessagingService.this);
        int fromWhere_seen = sh_seen.getInt("AddedFrom_seen", -1);
        String nameOfVisitor_seen = sh_seen.getString("Visitor_name_seen", "");
        String nameOfSubmitter_seen = sh_seen.getString("Submitor_name_seen", "");

        //For Delete
        SharedPreferences sh_delete = PreferenceManager.getDefaultSharedPreferences(CustomMessagingService.this);
        int fromWhere_delete = sh_delete.getInt("AddedFrom_delete", -1);
        String nameOfVisitor_delete = sh_delete.getString("Visitor_name_delete", "");
        String nameOfSubmitter_delete = sh_delete.getString("Submitor_name_delete", "");


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (remoteMessage != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();

            if (fromWhere == 0) {
                SharedPreferences.Editor editor = sh.edit().remove("Visitor_name");
                editor.remove("AddedFrom");
                editor.apply();
                notifyUser(title, nameOfVisitor + " is visiting your office.");
            } else if (fromWhere_seen == 1) {
                SharedPreferences.Editor editor = sh_delete.edit().remove("Visitor_name_seen");
                editor.remove("AddedFrom_seen");
                editor.remove("Submitor_name_seen");
                editor.apply();
                notifyUser(title, "Visitor " + nameOfVisitor_seen + " has been seen by " + nameOfSubmitter_seen);
            } else if (fromWhere_delete == 2) {
                SharedPreferences.Editor editor = sh_seen.edit().remove("Visitor_name_delete");
                editor.remove("AddedFrom_delete");
                editor.remove("Submitor_name_delete");
                editor.apply();
                notifyUser(title, "Visitor " + nameOfVisitor_delete + " has been deleted by " + nameOfSubmitter_delete);

            } else {
                notifyUser(title, "Visitor's List has been Updated");
            }


            if (remoteMessage.getData().size() > 0) {
                if (fromWhere == 0) {
                    SharedPreferences.Editor editor = sh.edit().remove("Visitor_name");
                    editor.remove("AddedFrom");
                    editor.apply();
                    notifyUser(title, nameOfVisitor + " is visiting your office.");
                } else if (fromWhere_seen == 1) {
                    SharedPreferences.Editor editor = sh_delete.edit().remove("Visitor_name_seen");
                    editor.remove("AddedFrom_seen");
                    editor.remove("Submitor_name_seen");
                    editor.apply();
                    notifyUser(title, "Visitor " + nameOfVisitor_seen + " has been seen by you ");
                } else if (fromWhere_delete == 2) {
                    SharedPreferences.Editor editor = sh_seen.edit().remove("Visitor_name_delete");
                    editor.remove("AddedFrom_delete");
                    editor.remove("Submitor_name_delete");
                    editor.apply();
                    notifyUser(title, "Visitor " + nameOfVisitor_delete + " has been deleted by you " );

                } else {
                    notifyUser(title, "Visitor's List Updated");
                }
            }
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        }

    }

    private void notifyUser(String title, String messageBody) {
        Intent intent = new Intent(this, ViewVisitor.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }





        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.storm)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For android Oreo and above  notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Fcm notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());


    }


    @SuppressLint("NotificationId0")
    public void bigTextNotification(Map<String, String> dataMap) {
        String title = dataMap.get("title");
        String message = dataMap.get("message");
        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = "FCMPush";
        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this, channelId);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(chan);

        }

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(message);
        style.setSummaryText(title);

        builder1.setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.storm)
                .setColor(Color.BLUE)
                .setStyle(style);
        builder1.build();
        notification = builder1.getNotification();
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(0, notification);
        } else {
            notificationManager.notify(0, notification);
        }

    }

    @SuppressLint("NotificationId0")
    public void bigPicNotification(Map<String, String> dataMap) {
        String title = dataMap.get("title");
        String message = dataMap.get("message");
        String imageUrl = dataMap.get("imageUrl");
        try {
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = "FCMPush";
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, channelId);
            if (Build.VERSION.SDK_INT >= 26) {

                NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(chan);
            }
            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            style.setBigContentTitle(title);
            style.setSummaryText(message);
            style.bigPicture(Glide.with(CustomMessagingService.this).asBitmap().load(imageUrl).submit().get());

            builder2.setContentTitle(title)
                    .setContentText(message)
                    .setSound(defaultSoundUri)
                    .setColor(Color.GREEN)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.storm)
                    .setStyle(style);
            builder2.build();
            notification = builder2.getNotification();
            if (Build.VERSION.SDK_INT >= 26) {
                startForeground(0, notification);
            } else {
                notificationManager.notify(0, notification);
            }

        } catch (Exception e) {

        }


    }

    @SuppressLint("NotificationId0")
    public void notificationActions(Map<String, String> dataMap) {
        String title = dataMap.get("title");
        String message = dataMap.get("message");

        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = "FCMPush";
        NotificationCompat.Builder builder3 = new NotificationCompat.Builder(this, channelId);

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(chan);

        }

        Intent intent1 = new Intent(CustomMessagingService.this, ViewVisitor.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(CustomMessagingService.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent = new Intent(getBaseContext(), NotificationReceiver.class);
        cancelIntent.putExtra("ID", 0);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, cancelIntent, 0);

        builder3.setSmallIcon(R.drawable.storm)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(Color.BLUE)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent)
                .addAction(android.R.drawable.ic_delete, "DISMISS", cancelPendingIntent)
                .build();
        notification = builder3.getNotification();
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(0, notification);
        } else {
            notificationManager.notify(0, notification);
        }


    }

//    @SuppressLint("NotificationId0")
//    public void directReply(Map<String, String> dataMap) {
//        String title = dataMap.get("title");
//        String message = dataMap.get("message");
//        String channelId = getString(R.string.default_notification_channel_id);
//        String channelName = "FCMPush";
//        NotificationCompat.Builder builder4 = new NotificationCompat.Builder(this, channelId);
//
//        if (Build.VERSION.SDK_INT >= 26) {
//
//            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(chan);
//        }
//
//        Intent cancelIntent = new Intent(getBaseContext(), NotificationReceiver.class);
//        cancelIntent.putExtra("ID", 0);
//        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, cancelIntent, 0);
//
//
//        Intent feedbackIntent = new Intent(CustomMessagingService.this, NotificationReceiver.class);
//        PendingIntent feedbackPendingIntent = PendingIntent.getBroadcast(CustomMessagingService.this,
//                100, feedbackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        RemoteInput remoteInput = new RemoteInput.Builder("DirectReplyNotification")
//                .setLabel(message)
//                .build();
//
//        NotificationCompat.Action action = new NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
//                "Write here...", feedbackPendingIntent)
//                .addRemoteInput(remoteInput)
//                .build();
//
//
//        builder4.setSmallIcon(R.drawable.storm)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(feedbackPendingIntent)
//                .addAction(action)
//                .setColor(Color.RED)
//                .addAction(android.R.drawable.ic_menu_compass, "Cancel", cancelPendingIntent);
//        builder4.build();
//        notification = builder4.getNotification();
//        if (Build.VERSION.SDK_INT >= 26) {
//            startForeground(0, notification);
//        } else {
//            notificationManager.notify(0, notification);
//        }
//
//
//    }

    @SuppressLint("NotificationId0")
    public void inboxTypeNotification(Map<String, String> dataMap) {
        try {
            String title = dataMap.get("title");
            String message = dataMap.get("message");
            JSONArray jsonArray = new JSONArray(dataMap.get("contentList"));

            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = "FCMPush";
            NotificationCompat.Builder builder5 = new NotificationCompat.Builder(this, channelId);

            if (Build.VERSION.SDK_INT >= 26) {

                NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(chan);
            }

            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            style.setSummaryText(message);
            style.setBigContentTitle(title);
            for (int i = 0; i < jsonArray.length(); i++) {
                String emailName = jsonArray.getString(i);
                style.addLine(emailName);
            }

            builder5.setContentTitle(title)
                    .setContentText(message)
                    .setColor(Color.BLUE)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.storm)
                    .setStyle(style);
            builder5.build();
            notification = builder5.getNotification();
            if (Build.VERSION.SDK_INT >= 26) {
                startForeground(0, notification);
            } else {
                notificationManager.notify(0, notification);
            }

        } catch (Exception e) {

        }


    }

    @SuppressLint("NotificationId0")
    public void messageTypeNotification(Map<String, String> dataMap) {
        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = "FCMPush";
        NotificationCompat.Builder builder6 = new NotificationCompat.Builder(this, channelId);

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(chan);
        }

        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle("Janhavi");
        style.addMessage("Is there any online tutorial for FCM?", 0, "member1");
        style.addMessage("Yes", 0, "");
        style.addMessage("How to use constraint layout?", 0, "member2");


        builder6.setSmallIcon(R.drawable.storm)
                .setColor(Color.RED)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.storm))
                .setSound(defaultSoundUri)
                .setStyle(style)
                .setAutoCancel(true);
        builder6.build();
        notification = builder6.getNotification();

        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(0, notification);
        } else {
            notificationManager.notify(0, notification);
        }


    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }


}