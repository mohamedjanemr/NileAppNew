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
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.swadallail.nileapp.MainActivity;
import com.swadallail.nileapp.R;
import com.swadallail.nileapp.chatroomspage.ChatRooms;
import com.swadallail.nileapp.data.CustomMessage;
import com.swadallail.nileapp.data.NotificationView;
import com.swadallail.nileapp.delegete.DelegeteHome;
import com.swadallail.nileapp.helpers.Globals;
import com.swadallail.nileapp.helpers.SharedHelper;
import com.swadallail.nileapp.history.HistoryOreders;
import com.swadallail.nileapp.modelviews.MessageViewModel;
import com.swadallail.nileapp.offers.OffersPage;

import io.reactivex.Single;

public class ChatService extends Service implements OnLogout {

    private final IBinder mBinder = new LocalBinder();
    static HubConnection hubConnection;
    Handler handler;
    String token;
    static boolean hubStarted = false;
    MainActivity activity;
    Intent restartServiceIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        token = intent.getExtras().getString("token");
        Log.e("TokenFromStartHub", token);
        hubStarted = StartHubConnection(token);
        if (!hubStarted) {
            ExitWithMessage("Chat Service failed to start!");
        } else if (hubStarted) {
            ExitWithMessage("Done");
        }
        onTaskRemoved(intent);
        return mBinder;
    }

    public void getToken(String tokens, String usernames) {
        StartHubConnection(tokens);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        try {
            if (!token.equals("None")) {
                Log.e("restart Service", ": restarted");
                StartHubConnection(token);
                startService(restartServiceIntent);
            }
        } catch (Exception e) {
            Log.e("Service", ": Stoped");
        }

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        onTaskRemoved(intent);


        return START_STICKY;
    }


    private void biludNotification(String body, String title, int type, String refId) {
        Intent intent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


// Creating a pending intent and wrapping our intent

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Intent resultIntent;

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
        switch (type) {
            case 0:
                resultIntent = new Intent(this, ChatRooms.class);
                break;
            case 1:
                resultIntent = new Intent(this, DelegeteHome.class);
                break;
            case 2:
                resultIntent = new Intent(this, OffersPage.class);
                break;
            case 3:
                resultIntent = new Intent(this, HistoryOreders.class);
                break;

            default:
                resultIntent = new Intent(this, MainActivity.class);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void biludNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


// Creating a pending intent and wrapping our intent

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

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
                .setContentTitle("Nile App")
                .setContentText(message)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setVibrate(pattern);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        super.onCreate();
    }

    @Override
    public void OnAppLogout(String token) {

    }

    public class LocalBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }

    }

    private boolean StartHubConnection(String token) {

        if (hubStarted) {
            return true;
        }

        hubConnection =
                HubConnectionBuilder
                        .create("https://test.nileappco.com/chatHub")
                        .withAccessTokenProvider(Single.defer(() -> {
                            return Single.just(token);

                            //SharedHelper.getKey(getApplicationContext(),"token");
                        })).build();
        new HubTask().execute(hubConnection);
//        String sh = SharedHelper.getKey(getApplicationContext(), "tokens");
        Log.e("totoken", token);
        OnNotificationRecevied();
        OnRecevied();
        return true;
    }

    public static class HubTask extends AsyncTask<HubConnection, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(HubConnection... hubConnections) {
            HubConnection hubConnection = hubConnections[0];
            hubConnection.start().blockingAwait();//.andThen(()-> Log.e("",""));
            String conn = hubConnection.getConnectionId();
            Log.e("Conn", "" + conn);
            return null;
        }
    }

    private void ExitWithMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 3000);
    }

    public void Send(CustomMessage object, String token) {
        StartHubConnection(token);
        try {
            hubConnection.send("Send", object);
            Log.e("message", "" + object.Message);
            Log.e("ChatID", "" + object.ChatId);
            Log.e("Image", "" + object.Img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void OnRecevied() {
        hubConnection.on("RecieveChatMessage", (message) -> {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("Qu", "" + message.images);
//                    message.isMine = message.from.equals(SharedHelper.getKey(getApplicationContext(), "UserName")) ? 1 : 0;
//                    int mine = message.isMine;
                    String op = SharedHelper.getKey(getApplicationContext(), "opened");
                    if (message.images == null) {
                        message.images = "";
                        Globals.Messages.add(message);
                    } else {
                        Globals.Messages.add(message);
                    }


                    if (message.isMine == 0 && op.equals("no")) {
                        biludNotification(message.content);
                    }
                    sendBroadcast(new Intent().setAction("notifyAdapter"));
                }
            });
        }, MessageViewModel.class);
    }

    private void OnNotificationRecevied() {
        hubConnection.on("RecieveNotification", (message) -> {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    biludNotification(message.body, message.title, message.type, message.refId);
                }
            });
        }, NotificationView.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        SharedHelper.putKey(getApplicationContext(), "tokens", "");
    }

}
