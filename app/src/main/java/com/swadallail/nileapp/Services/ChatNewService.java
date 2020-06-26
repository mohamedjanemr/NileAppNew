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

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Single;

public class ChatNewService extends Service {
    public int counter = 0;
    Handler handler;
    private IBinder mBinder = new ChatNewService.LocalBinder();
    public boolean aBoolean = false;
    static HubConnection hubConnection;

    @Override
    public void onCreate() {
        handler = new Handler();
        super.onCreate();
    }

    public ChatNewService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public ChatNewService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        Log.e("ChatNewService" , "Restarted");
        return START_STICKY;
    }

    private boolean StartHubConnection(String token) {
        if(aBoolean){
            return true;
        }else {
            hubConnection =
                    HubConnectionBuilder
                            .create("https://test.nileappco.com/chatHub")
                            .withAccessTokenProvider(Single.defer(() -> {
                                return Single.just(token);

                                //SharedHelper.getKey(getApplicationContext(),"token");
                            })).build();
            new HubTaskNew().execute(hubConnection);

            OnNotificationRecevied();
            OnRecevied();
            return true;
        }



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

    public void OnRecevied() {
        hubConnection.on("RecieveChatMessage", (message) -> {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (message.isMine == 0) {
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
                }
            });
        }, MessageViewModel.class);
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

    public static class HubTaskNew extends AsyncTask<HubConnection, Void, Void> {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, ReceiverSensor.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Started", "OnStartCommandsss" + intent.getStringExtra("Token"));
        if (!StartHubConnection(intent.getStringExtra("Token"))) {
            ExitWithMessage("Chat Service failed to start!");
        } else if (StartHubConnection(intent.getStringExtra("Token"))) {
            ExitWithMessage("Done");
            aBoolean = true;
        }
        startTimer();
        return mBinder;
    }
    public class LocalBinder extends Binder {
        public ChatNewService getService() {
            return ChatNewService.this;
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

    private void biludNotification(String body, String title, int type, String refId) {
        Intent intent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        if (type == 0) {
            resultIntent = new Intent(this, ChatRooms.class);
        } else if (type == 1) {
            resultIntent = new Intent(this, DelegeteHome.class);
        } else if (type == 2) {
            resultIntent = new Intent(this, OffersPage.class);
        } else if (type == 3) {
            resultIntent = new Intent(this, HistoryOreders.class);
        } else {
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
}
