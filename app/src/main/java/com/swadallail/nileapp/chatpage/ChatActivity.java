package com.swadallail.nileapp.chatpage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.swadallail.nileapp.R;
import com.swadallail.nileapp.Services.ChatNewService;
import com.swadallail.nileapp.Services.ChatService;
import com.swadallail.nileapp.adapters.MessageAdapter;
import com.swadallail.nileapp.data.ChatResponse;
import com.swadallail.nileapp.data.CustomMessage;
import com.swadallail.nileapp.data.MainResponse;
import com.swadallail.nileapp.data.MessageBody;
import com.swadallail.nileapp.data.MessageResponse;
import com.swadallail.nileapp.helpers.Globals;
import com.swadallail.nileapp.helpers.SharedHelper;
import com.swadallail.nileapp.loginauth.LoginAuthActivity;
import com.swadallail.nileapp.modelviews.MessageViewModel;
import com.swadallail.nileapp.modelviews.SendData;
import com.swadallail.nileapp.network.ApiInterface;
import com.swadallail.nileapp.uploaddoc.UploadData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    ChatNewService chatService;
    boolean mBound = false;
    MyReceiver myReceiver;
    MessageAdapter adapter;
    ProgressDialog dialog;
    int finalchatid;
    String chatname;
    String user_id;
    RecyclerView gridView;
    IntentFilter intentFilter;
    int PERMISSION_REQUEST_CODE = 200;
    String encodedImage = "";
    ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //chatService = new ChatNewService();
        binserviceing(SharedHelper.getKey(ChatActivity.this, "token"));
        //chatService.getToken(ChatActivity.this, SharedHelper.getKey(ChatActivity.this, "token"), "");
        Intent inGet = getIntent();
        user_id = inGet.getStringExtra("shopId");
        finalchatid = inGet.getIntExtra("chatId", 0);
        SharedHelper.putKey(ChatActivity.this, "opened", "true");
        myReceiver = new MyReceiver();
        intentFilter = new IntentFilter();
        //intentFilter.addAction("Send");
        intentFilter.addAction("notifyAdapter");
        registerReceiver(myReceiver, intentFilter);
        Log.e("userid", user_id + "");
        Log.e("chatid", finalchatid + "");
        getChatMessages(user_id, finalchatid);
        gridView = findViewById(R.id.gvMessages);

        //gridView.setTranscriptMode(GridView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        sendButton = findViewById(R.id.bSend);
        ImageButton sendImage = findViewById(R.id.bimgSend);
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int GALLERY = 1;
                final int CAM = 2;
                showPictureDialognatface(GALLERY, CAM);
            }
        });
        final EditText editText = (EditText) findViewById(R.id.etMessageText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        sendButton.setImageDrawable(getDrawable(R.drawable.ic_send_blue));
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        sendButton.setImageDrawable(getDrawable(R.drawable.ic_send_blue));
                    }
                });
                ArrayList<MessageViewModel> datasend = new ArrayList<>();
                if (message.isEmpty() && encodedImage == null) {
                    Toast.makeText(ChatActivity.this, "رجاء قم بكتابة الرسالة او قم بأدراج صورة", Toast.LENGTH_SHORT).show();
                } else {
                    editText.setText("");
                    CustomMessage object = new CustomMessage();
                    object.Message = message;
                    object.ChatId = finalchatid;
                    //SendData data = new SendData();
                    MessageViewModel model = new MessageViewModel();
                    model.isMine = 1;
                    model.content = message;
                    model.from = SharedHelper.getKey(ChatActivity.this, "UserName");


                    if (!encodedImage.equals("")) {
                        object.Img = encodedImage;
                        model.images = encodedImage;
                    } else {
                        object.Img = "";
                        model.images = "";
                    }
                    Globals.Messages.add(model);
                    adapter = new MessageAdapter(ChatActivity.this, Globals.Messages, 1);
                    gridView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                    gridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    gridView.scrollToPosition(adapter.getItemCount() - 1);
                    //scrollToBottom();
                    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();
                            gridView.scrollToPosition(adapter.getItemCount() - 1);
                            //scrollToBottom();
                        }
                    });
                    chatService.Send(object, SharedHelper.getKey(ChatActivity.this, "token"));
                    sendButton.setImageDrawable(getDrawable(R.drawable.ic_send_disable));

                    encodedImage = "";
                }


                /*arrayAdapter.add(message);
                arrayAdapter.notifyDataSetChanged();*/

            }
        });

//

    }
    private void binserviceing(String token) {
        Intent intent = null;
        intent = new Intent(this, ChatNewService.class);
        // Create a new Messenger for the communication back
        // From the Service to the Activity
        intent.putExtra("Token", token);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    private void getChatMessages(String user_id, int chatId) {
        dialog = new ProgressDialog(ChatActivity.this);
        dialog.setMessage(getApplicationContext().getResources().getString(R.string.the_data_is_loaded));
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://test.nileappco.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface userclient = retrofit.create(ApiInterface.class);
        MessageBody body = new MessageBody(chatId, user_id);
        Log.e("Body", "" + chatId);
        //String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoiYUBhLmNvbSIsInN1YiI6ImFAYS5jb20iLCJqdGkiOiJhZjFkMTNkMC00NzViLTQ2OWMtOWEwYi01YmU5NzE5YWJjMTciLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6IjAyN2Y2MWJiLWFlNzUtNGE2Yy04YWY4LWE5MDcyNmFlM2IyOCIsIlVzZXJJZCI6IjAyN2Y2MWJiLWFlNzUtNGE2Yy04YWY4LWE5MDcyNmFlM2IyOCIsImV4cCI6MTU5MjMyOTcxMywiaXNzIjoiUmF6TmV0LmNvbSIsImF1ZCI6IlJhek5ldC5jb20ifQ.ZoImLshfPppUM7qrURMeIBV-61YNeWAqF9Ge1lQ4N0w";
        String token = "Bearer " + SharedHelper.getKey(ChatActivity.this, "token");
        Call<MainResponse<ChatResponse<ArrayList<MessageViewModel>>>> call = userclient.GetChatMessages(token, body);
        call.enqueue(new Callback<MainResponse<ChatResponse<ArrayList<MessageViewModel>>>>() {
            @Override
            public void onResponse(Call<MainResponse<ChatResponse<ArrayList<MessageViewModel>>>> call, Response<MainResponse<ChatResponse<ArrayList<MessageViewModel>>>> response) {
                if (response.body() != null) {

                    finalchatid = response.body().data.chatId;
                    //chatname = response.body().data.messages.get(0).from;
                    if (response.body().data.messages.size() > 0) {
                        Globals.Messages = response.body().data.messages;
                    } else {
                        Toast.makeText(ChatActivity.this, "لا يوجد محادثة من قبل", Toast.LENGTH_SHORT).show();
                        gridView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                        adapter = new MessageAdapter(ChatActivity.this, Globals.Messages);
                        gridView.setAdapter(adapter);
                        gridView.scrollToPosition(adapter.getItemCount() - 1);
                        //scrollToBottom();
                        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                gridView.scrollToPosition(adapter.getItemCount() - 1);
                                //scrollToBottom();
                            }
                        });
                    }
                }


                for (int i = 0; i < response.body().data.messages.size(); i++) {
                    if (response.body().data.messages.get(i).isMine == 1) {
                        gridView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                        adapter = new MessageAdapter(ChatActivity.this, Globals.Messages, 1);
                        gridView.setAdapter(adapter);
                        gridView.scrollToPosition(adapter.getItemCount() - 1);
                        //gridView.setAdapter(adapter);

                        //scrollToBottom();
                        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                gridView.scrollToPosition(adapter.getItemCount() - 1);
                                //  scrollToBottom();
                            }
                        });
                    } else {
                        gridView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                        adapter = new MessageAdapter(ChatActivity.this, Globals.Messages, 0);
                        gridView.setAdapter(adapter);
                        gridView.scrollToPosition(adapter.getItemCount() - 1);
                        //gridView.setAdapter(adapter);
                        //scrollToBottom();
                        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                gridView.scrollToPosition(adapter.getItemCount() - 1);
                                //scrollToBottom();
                            }
                        });
                    }
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<MainResponse<ChatResponse<ArrayList<MessageViewModel>>>> call, Throwable t) {
                Log.e("BodyE", "" + t);
            }
        });
    }

    /*@Override
    protected void onStop() {
        if(mBound){
            unbindService(mConnection);
            unregisterReceiver(myReceiver);
            mBound = false;
        }

        Log.e("OnStop::" ,"chat Stoped");
        //Globals.Messages.clear();
        super.onStop();

    }*/

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ChatNewService.LocalBinder binder = (ChatNewService.LocalBinder) service;
            chatService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "notifyAdapter":
                    adapter.notifyDataSetChanged();
                    break;
            }

        }

    }

    private void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            gridView.getLayoutManager().smoothScrollToPosition(gridView, null, adapter.getItemCount() - 1);
    }

    @Override
    protected void onDestroy() {
        SharedHelper.putKey(ChatActivity.this, "opened", "no");
        //Globals.Messages.clear();

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        SharedHelper.putKey(ChatActivity.this, "opened", "no");
        super.onPause();
    }

    private void showPictureDialognatface(int gallery, int Cam) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("اختر الصورة من");
        String[] pictureDialogItems = {
                "معرض الصور",
                "الكاميرا"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (checkPermissiongallary()) {
                                    choosePhotoFromGallary(gallery);
                                } else {
                                    requestPermissiongallary();
                                }

                                break;
                            case 1:
                                if (checkPermissioncamera()) {
                                    takePhotoFromCamera(Cam);
                                } else {
                                    requestPermissioncamera();
                                }

                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private boolean checkPermissiongallary() {
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result2 == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkPermissioncamera() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void choosePhotoFromGallary(int GALLERY) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera(int cam) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, cam);
    }

    private void requestPermissiongallary() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    private void requestPermissioncamera() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case 1:
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        ByteArrayOutputStream natface = new ByteArrayOutputStream();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, natface);
                        encodedImage = Base64.encodeToString(natface.toByteArray(), Base64.DEFAULT);
                        Toast.makeText(ChatActivity.this, "تم اختيار الصورة", Toast.LENGTH_SHORT).show();
                        sendButton.setImageDrawable(getDrawable(R.drawable.ic_send_blue));

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ChatActivity.this, "خطأ فى رفع الصورة!", Toast.LENGTH_SHORT).show();
                        sendButton.setImageDrawable(getDrawable(R.drawable.ic_send_disable));
                    }
                }
                break;
            case 2:
                if (data != null) {
                    ByteArrayOutputStream natface = new ByteArrayOutputStream();
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, natface);
                    //encodedImage = Base64.encodeToString(natface.toByteArray(), Base64.DEFAULT);
                    Toast.makeText(ChatActivity.this, "تم اختيار الصورة", Toast.LENGTH_SHORT).show();
                    sendButton.setImageDrawable(getDrawable(R.drawable.ic_send_blue));
                } else {
                    sendButton.setImageDrawable(getDrawable(R.drawable.ic_send_disable));
                }
                break;
        }


    }
}
