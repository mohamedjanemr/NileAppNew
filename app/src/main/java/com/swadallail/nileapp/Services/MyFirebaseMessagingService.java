package com.swadallail.nileapp.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.swadallail.nileapp.MainActivity;
import com.swadallail.nileapp.R;
import com.swadallail.nileapp.accept.AcceptOffers;
import com.swadallail.nileapp.chatroomspage.ChatRooms;
import com.swadallail.nileapp.delegete.DelegeteHome;
import com.swadallail.nileapp.history.HistoryOreders;
import com.swadallail.nileapp.offers.OffersPage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FIREBASE";
    private static int notificationIdentifier = 101;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //  Log.e(TAG, "From: " + remoteMessage.getFrom());
        //Log.e(TAG, "message: " + remoteMessage.getNotification().getBody());
        try {
            Log.e(TAG, "title " + remoteMessage.getData().get("title").toString());
            Log.e(TAG, "body " + remoteMessage.getData().get("body").toString());
            Log.e(TAG, "type " + remoteMessage.getData().get("type").toString());
            Log.e(TAG, "Ref " + remoteMessage.getData().get("refId").toString());

            showNotification(remoteMessage.getData().get("title").toString(), remoteMessage.getData().get("body").toString(), remoteMessage.getData().get("type").toString(), remoteMessage.getData().get("refId").toString());
        } catch (Exception e) {
        }

    }


    public void showNotification(String title, String body, String Type, String ref) {
        //Intent intent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        int requestID = (int) System.currentTimeMillis();
        Intent notificationIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Uri alarmSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long pattern[] = {0, 800, 200, 300, 400};

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.nileapp)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setVibrate(pattern);

        if (Type.equals("0")) {
            Log.e("TYPE0" , Type);
            notificationIntent = null;
            notificationIntent = new Intent(getApplicationContext(), ChatRooms.class);
            notificationIntent.putExtra("type" ,"0");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            /*Intent resultIntent = new Intent(this, HistoryOreders.class);
            TaskStackBuilder stackBuildermess = TaskStackBuilder.create(this);
            stackBuildermess.addNextIntentWithParentStack(resultIntent);
            PendingIntent resultPendingIntent0 =
                    stackBuildermess.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent0);*/
        } else if (Type.equals("1")) {
            Log.e("TYPE1" , Type);
            notificationIntent = null;
            notificationIntent = new Intent(getApplicationContext(), DelegeteHome.class);
            notificationIntent.putExtra("type" ,"1");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            /*Intent resultIntent1 = new Intent(this, DelegeteHome.class);
            TaskStackBuilder stackBuildernewordr = TaskStackBuilder.create(this);
            stackBuildernewordr.addNextIntentWithParentStack(resultIntent1);
            PendingIntent resultPendingIntent1 =
                    stackBuildernewordr.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent1);*/
        } else if (Type.equals("2")) {
            Log.e("TYPE2" , Type);
            notificationIntent = null;
            notificationIntent = new Intent(getApplicationContext(), OffersPage.class);
            notificationIntent.putExtra("type" ,"2");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            /*Intent resultIntent2 = new Intent(this, OffersPage.class);
            TaskStackBuilder stackBuilderoffer = TaskStackBuilder.create(this);
            stackBuilderoffer.addNextIntentWithParentStack(resultIntent2);
            PendingIntent resultPendingIntent2 =
                    stackBuilderoffer.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent2);*/
        } else if (Type.equals("5")) {
            Log.e("TYPE5" , Type);
            notificationIntent = null;
            notificationIntent = new Intent(getApplicationContext(), HistoryOreders.class);
            notificationIntent.putExtra("type" ,"5");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            /*Intent resultIntent5 = new Intent(this, ChatRooms.class);
            TaskStackBuilder stackBuilderhistory = TaskStackBuilder.create(this);
            stackBuilderhistory.addNextIntentWithParentStack(resultIntent5);
            PendingIntent resultPendingIntent5 =
                    stackBuilderhistory.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent5);*/
        } else if (Type.equals("4")) {
            Log.e("TYPE4" , Type);
            notificationIntent = null;
            notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.putExtra("type" ,"4");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            /*Intent resultIntent4 = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuildermain = TaskStackBuilder.create(this);
            stackBuildermain.addNextIntentWithParentStack(resultIntent4);
            PendingIntent resultPendingIntent4 =
                    stackBuildermain.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent4);*/
        } else if (Type.equals("3")) {
            Log.e("TYPE3" , Type);
            notificationIntent = null;
            notificationIntent = new Intent(getApplicationContext(), HistoryOreders.class);
            notificationIntent.putExtra("type" ,"3");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            /*Intent resultIntent3 = new Intent(this, HistoryOreders.class);
            TaskStackBuilder stackBuilderhistory2 = TaskStackBuilder.create(this);
            stackBuilderhistory2.addNextIntentWithParentStack(resultIntent3);
            PendingIntent resultPendingIntent3 =
                    stackBuilderhistory2.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent3);*/

        }

        notificationManager.notify(notificationId, mBuilder.build());
    }

    /*private void sendNotification(String messageTitle, String messageBody ,String Type ,String points,String ChildCount) {
        /// o
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);

        mBuilder.setContentTitle(messageTitle);
        mBuilder.setContentText(messageBody);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(messageBody));
        mBuilder.setTicker("");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setAutoCancel(true);
        long pattern[] = {0, 800, 200, 300, 400};
        mBuilder.setVibrate(pattern);
        Uri alarmSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        try {
            //  Log.e("pushnotification","1  "+type);
            *//*Intent resultIntent = new Intent(this, MainpageActivity.class);
            resultIntent.putExtra(AppConstant.OpenNotification, "notification");
            if (Type.equalsIgnoreCase("2")){
                Intent intnt = new Intent(AppConstant.ActionString);
                intnt.putExtra(AppConstant.UPDATE_POINTS,points);
                sendBroadcast(intnt);
            }
            else if(Type.equalsIgnoreCase("3")){
                Intent intnt = new Intent(AppConstant.ActionString);
                intnt.putExtra(AppConstant.UPDATE_POINTS,points);
                intnt.putExtra(AppConstant.UPDATE_CHILD,ChildCount);
                sendBroadcast(intnt);
            }
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainpageActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationIdentifier + 1,
                    mBuilder.build());*//*

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
}

