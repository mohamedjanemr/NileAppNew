package com.swadallail.nileapp.loginauth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.swadallail.nileapp.AuthPhone;
import com.swadallail.nileapp.MainActivity;
import com.swadallail.nileapp.R;
import com.swadallail.nileapp.Services.ChatNewService;
import com.swadallail.nileapp.Services.ChatService;
import com.swadallail.nileapp.Services.ReceiverSensor;
import com.swadallail.nileapp.chatpage.ChatActivity;
import com.swadallail.nileapp.chatroomspage.ChatRooms;
import com.swadallail.nileapp.data.FacebookToken;
import com.swadallail.nileapp.data.MainResponse;
import com.swadallail.nileapp.data.UserDataResponse;
import com.swadallail.nileapp.data.UserLogin;
import com.swadallail.nileapp.data.UserResponse;
import com.swadallail.nileapp.databinding.ActivityLoginAuthBinding;
import com.swadallail.nileapp.delegete.DelegeteHome;
import com.swadallail.nileapp.helpers.SharedHelper;
import com.swadallail.nileapp.history.HistoryOreders;
import com.swadallail.nileapp.network.ApiInterface;
import com.swadallail.nileapp.offers.OffersPage;
import com.swadallail.nileapp.registerauth.RegisterAuthActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginAuthActivity extends AppCompatActivity {
    ActivityLoginAuthBinding binding;
    ProgressDialog dialog;
    MyClick handlers;
    boolean mBound = false;
    ChatNewService chatService;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    Profile profile;
    Intent intent, goo;
    Intent open;
    String email, pass;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    CheckBox check;
    Intent chatNewService;
    private ReceiverSensor sensor;
    String type = null;
    Intent typein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_auth);
        getKeyHash();
        if (SharedHelper.getKey(this, "isLoged").equals("yes")) {
            Log.e("AppHere?", SharedHelper.getKey(this, "isLoged"));
            typein = getIntent();
            type = typein.getStringExtra("type");
            if (type != null) {
                try {
                    Log.e("Null?", "NotNull");
                    if (type.equals("0")) {
                        startActivity(new Intent(LoginAuthActivity.this, ChatRooms.class));
                        finish();
                    } else if (type.equals("1")) {
                        startActivity(new Intent(LoginAuthActivity.this, DelegeteHome.class));
                        finish();
                    } else if (type.equals("2")) {
                        startActivity(new Intent(LoginAuthActivity.this, OffersPage.class));
                        finish();
                    } else if (type.equals("3")) {
                        startActivity(new Intent(LoginAuthActivity.this, HistoryOreders.class));
                        finish();
                    } else if (type.equals("4")) {
                        startActivity(new Intent(LoginAuthActivity.this, MainActivity.class));
                        finish();
                    } else if (type.equals("5")) {
                        startActivity(new Intent(LoginAuthActivity.this, HistoryOreders.class));
                        finish();
                    }
                } catch (Exception e) {
                    Log.e("EXC", e.toString());
                }

            } else {
                Log.e("Null?", "Null");
                goo = new Intent(LoginAuthActivity.this, MainActivity.class);
                startActivity(goo);
                finish();
            }
            /*sensor = new ReceiverSensor();
            chatNewService = new Intent(this , ChatNewService.class);
            chatNewService.putExtra("Token" , SharedHelper.getKey(this , "token"));
            if (!isMyServiceRunning(sensor.getClass())) {
                Log.e("FromLogin" , "Service Started");
                startService(chatNewService);
            }*/

        }

        //chatService = new ChatService();
        check = findViewById(R.id.ch_rememberme);
        //intent = new Intent(LoginAuthActivity.this, ChatService.class);
        checkLocationPermission();
        handlers = new MyClick(this);
        binding.setHandlers(handlers);
        /*if (SharedHelper.getKey(this, "isLoged").equals("yes")) {
            intent.putExtra("token",SharedHelper.getKey(LoginAuthActivity.this , "token"));
            Log.e("TOKENEE" ,SharedHelper.getKey(LoginAuthActivity.this , "token") );
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            chatService.getToken( SharedHelper.getKey(LoginAuthActivity.this, "token"), "");
            goo = new Intent(LoginAuthActivity.this, MainActivity.class);
            startActivity(goo);
            finish();
        }*/
        Log.e("Loged", SharedHelper.getKey(this, "isLoged"));


    }

    private void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.swadallail.nileapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
        }
        catch (NoSuchAlgorithmException e) {
        }
    }

    public void facebooklogin(View view) {
        callbackManager = CallbackManager.Factory.create();
        binding.btnFaceLogin.setPermissions("email");
        binding.btnFaceLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserData(loginResult.getAccessToken());
                loginWithToken(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    public class MyClick {
        Context context;

        public MyClick(Context con) {
            con = this.context;
        }

        public void phonelogin(View view) {
            startActivity(new Intent(LoginAuthActivity.this, AuthPhone.class));
            finish();
        }

        public void loginfun(View view) {
            Log.e("UserNameBefore", SharedHelper.getKey(LoginAuthActivity.this, "UserName"));
            open = new Intent(LoginAuthActivity.this, MainActivity.class);
            email = binding.edEmaillogin.getText().toString();
            pass = binding.edPasslogin.getText().toString();

            if (email.isEmpty()) {
                binding.edEmaillogin.setError("ادخل البريد الإلكترونى");
            } else if (pass.isEmpty()) {
                binding.edPasslogin.setError("ادخل كلمة المرور");
            } else {
                SharedHelper.clearSharedPreferences(LoginAuthActivity.this);
                dialog = new ProgressDialog(LoginAuthActivity.this);
                dialog.setMessage(getApplicationContext().getResources().getString(R.string.the_data_is_loaded));
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://www.nileappco.com/api/User/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiInterface userclient = retrofit.create(ApiInterface.class);
                UserLogin userLogin = new UserLogin(email, pass);
                Call<MainResponse<UserResponse<UserDataResponse>>> call = userclient.UserLoginFun(userLogin);
                call.enqueue(new Callback<MainResponse<UserResponse<UserDataResponse>>>() {
                    @Override
                    public void onResponse(Call<MainResponse<UserResponse<UserDataResponse>>> call, Response<MainResponse<UserResponse<UserDataResponse>>> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            Log.e("StatusCode", "" + response.body().statusCode);
                            if (response.body().success) {
                                Log.e("SAVED", "YES");
                                SharedHelper.putKey(LoginAuthActivity.this, "balance", "" + response.body().data.user.getBalance());
                                SharedHelper.putKey(LoginAuthActivity.this, "mailConfirm", "" + response.body().data.user.getMailConfirmed());
                                SharedHelper.putKey(LoginAuthActivity.this, "phoneConfirm", "" + response.body().data.user.getPhoneConfirmed());
                                Toast.makeText(LoginAuthActivity.this, "" + response.body().data.user.getUsername(), Toast.LENGTH_SHORT).show();
                                SharedHelper.putKey(LoginAuthActivity.this, "UserName", response.body().data.user.getUsername());
                                SharedHelper.putKey(LoginAuthActivity.this, "role", response.body().data.user.getRole());
                                SharedHelper.putKey(LoginAuthActivity.this, "token", response.body().data.token);
                                //chatService.getToken( response.body().data.token, response.body().data.user.getUsername());
                                Log.e("TokenFromLogin", response.body().data.token);
                                Log.e("UserNameAfter", SharedHelper.getKey(LoginAuthActivity.this, "UserName"));
                                SharedHelper.putKey(LoginAuthActivity.this, "name", response.body().data.user.getFullName());
                                SharedHelper.putKey(LoginAuthActivity.this, "picUrl", response.body().data.user.getPic());
                                if (check.isChecked()) {
                                    SharedHelper.putKey(LoginAuthActivity.this, "isLoged", "yes");
                                } else {
                                    SharedHelper.putKey(LoginAuthActivity.this, "isLoged", "no");
                                }
                                dialog.dismiss();
                                startActivity(open);
                                LoginAuthActivity.this.finish();
                            } else {
                                Log.e("SAVED", "NO");
                                Toast.makeText(LoginAuthActivity.this, "خطأ فى البريد او كلمة المرور", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }


                    @Override
                    public void onFailure(Call<MainResponse<UserResponse<UserDataResponse>>> call, Throwable t) {
                        Toast.makeText(LoginAuthActivity.this, "خطأ فى البريد او كلمة المرور", Toast.LENGTH_SHORT).show();
                        Log.e("SS", t.toString());
                        dialog.dismiss();
                    }
                });
            }
        }

        public void gotoregister(View view) {
            startActivity(new Intent(LoginAuthActivity.this, RegisterAuthActivity.class));
            finish();
        }


        public void googlelogin(View view) {

        }


        public void forgetpassword(View view) {

        }

    }

    private void binserviceing(String token) {
        Intent intent = null;
        intent = new Intent(this, ChatNewService.class);
        // Create a new Messenger for the communication back
        // From the Service to the Activity
        intent.putExtra("Token", token);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void loginWithToken(String token) {

        dialog = new ProgressDialog(LoginAuthActivity.this);
        dialog.setMessage(getApplicationContext().getResources().getString(R.string.the_data_is_loaded));
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.nileappco.com/api/User/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface userclient = retrofit.create(ApiInterface.class);
        Log.e("TokenFacebook", token);
        FacebookToken facebookToken = new FacebookToken(token);
        Call<MainResponse<UserResponse<UserDataResponse>>> call2 = userclient.UserLoginwithtoken(facebookToken);
        SharedHelper.putKey(LoginAuthActivity.this, "token", "");
        SharedHelper.putKey(LoginAuthActivity.this, "UserName", "");
        SharedHelper.putKey(LoginAuthActivity.this, "name", "");
        SharedHelper.putKey(LoginAuthActivity.this, "picUrl", "");
        SharedHelper.putKey(LoginAuthActivity.this, "isLoged", "");
        call2.enqueue(new Callback<MainResponse<UserResponse<UserDataResponse>>>() {
            @Override
            public void onResponse(Call<MainResponse<UserResponse<UserDataResponse>>> call, Response<MainResponse<UserResponse<UserDataResponse>>> response) {
                if (response.body() != null) {
                    if (response.body().success) {
                        SharedHelper.putKey(LoginAuthActivity.this, "balance", "" + response.body().data.user.getBalance());
                        SharedHelper.putKey(LoginAuthActivity.this, "mailConfirm", "" + response.body().data.user.getMailConfirmed());
                        SharedHelper.putKey(LoginAuthActivity.this, "phoneConfirm", "" + response.body().data.user.getPhoneConfirmed());
                        SharedHelper.putKey(LoginAuthActivity.this, "token", response.body().data.token);
                        //chatService.getToken(LoginAuthActivity.this, response.body().data.token, response.body().data.user.getUsername());
                        SharedHelper.putKey(LoginAuthActivity.this, "retoken", response.body().data.refreshToken);
                        //Toast.makeText(LoginAuthActivity.this, response.body().data.token, Toast.LENGTH_SHORT).show();
                        SharedHelper.putKey(LoginAuthActivity.this, "isLoged", "yes");
                        SharedHelper.putKey(LoginAuthActivity.this, "UserName", response.body().data.user.getUsername());
                        Log.e("Name", response.body().data.user.getUsername());
                        SharedHelper.putKey(LoginAuthActivity.this, "role", response.body().data.user.getRole());
                        //chatService.getToken(LoginAuthActivity.this, response.body().data.token, response.body().data.user.getUsername());
                        //SharedHelper.putKey(LoginAuthActivity.this , "token" , response.body().data.token);
                        SharedHelper.putKey(LoginAuthActivity.this, "name", response.body().data.user.getFullName());
                        SharedHelper.putKey(LoginAuthActivity.this, "picUrl", response.body().data.user.getPic());
//
//                        Intent intent = new Intent(LoginAuthActivity.this, ChatService.class);
                        //intent.putExtra("token",response.body().data.token);
                        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                        /*chatNewService = new Intent(LoginAuthActivity.this , ChatNewService.class);
                        chatNewService.putExtra("Token" ,  response.body().data.token);
                        if (!isMyServiceRunning(sensor.getClass())) {
                            Log.e("FromLogin" , "Service Started");
                            startService(chatNewService);
                        }*/
                        startActivity(new Intent(LoginAuthActivity.this, MainActivity.class));
                        LoginManager.getInstance().logOut();
                        LoginAuthActivity.this.finish();
                    }
                    dialog.dismiss();

                }

            }

            @Override
            public void onFailure(Call<MainResponse<UserResponse<UserDataResponse>>> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    private void getUserData(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                            //Toast.makeText(LoginAuthActivity.this, email, Toast.LENGTH_SHORT).show();
                            SharedHelper.putKey(LoginAuthActivity.this, "fname", first_name);
                            SharedHelper.putKey(LoginAuthActivity.this, "lname", last_name);
                            SharedHelper.putKey(LoginAuthActivity.this, "picUrl", image_url);
                            SharedHelper.putKey(LoginAuthActivity.this, "email", email);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public ServiceConnection mConnection = new ServiceConnection() {

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

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("الصلاحيات")
                        .setMessage("الرجاء قبول الوصول لصلاحية الموقع")
                        .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LoginAuthActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    /*private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.e ("isMyServiceRunning?", false+"");
        return false;
    }*/
}
