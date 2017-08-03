package com.fiek.ushtrime.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.camera2.params.Face;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonObject;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    static final String APP_ID = "59760";
    static final String AUTH_KEY = "fpwn4kU3Puayx7S";
    static final String AUTH_SECRET = "8tSQDYQqRqx5WfA";
    static final String ACCOUNT_KEY = "3RtJqaggQV4CTZScza6s";

    String user,password;

    Button btnLogin, btnSignup;
    EditText edtUser, edtPassword;
    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    String first_name,last_name,full_name;

    SessionManager sessionManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        InitializeFramework();
        sessionManager=new SessionManager(this);
        sessionManager.checkLogin();

        btnLogin = (Button) findViewById(R.id.main_btnLogin);
        btnSignup = (Button) findViewById(R.id.main_btnSignUp);

        edtUser = (EditText) findViewById(R.id.main_editLogin);
        edtPassword = (EditText) findViewById(R.id.main_editPassword);

        loginButton = (LoginButton) findViewById(R.id.fb_loginbutton);
        callbackManager = CallbackManager.Factory.create();



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Toast.makeText(getBaseContext(),""+loginResult.getAccessToken().getUserId(),Toast.LENGTH_SHORT);
                Profile profile=Profile.getCurrentProfile();
                // edtUser.setText(loginResult.getAccessToken().toString());
                loginFacebook( loginResult.getAccessToken().toString(),loginResult.getAccessToken().getUserId(), profile.getFirstName()+""+profile.getLastName());
                //edtUser.setText(""+ first_name + last_name);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, SignUpAcitivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = edtUser.getText().toString();
                password = edtPassword.getText().toString();

                QBUser qbUser = new QBUser(user, password);

                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        sessionManager.createLoginSession(user,password);

                        Toast.makeText(getBaseContext(), "Login successfully", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(MainActivity.this, ChatDialogsActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        finish();// Close login action after logged
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void InitializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void loginFacebook(final String  _accessToken, final String facebookId, final String _fullName){

        user=_fullName.toLowerCase();
        full_name=_fullName;
        password=_accessToken.replaceAll("[^A-Za-z0-9]", "").substring(0,39);

        QBUser qbUser = new QBUser(user, password);

        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                sessionManager.createLoginSession(user,password);
                Toast.makeText(getBaseContext(), "Login successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ChatDialogsActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("password", password);
                startActivity(intent);
                finish();// Close login action after logged
            }

            @Override
            public void onError(QBResponseException e) {
                QBUser qbUser=new QBUser(user,password);

                qbUser.setFullName(full_name);
                qbUser.setFacebookId(facebookId);

                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        loginFacebook(_accessToken,user,full_name);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });



    }


}