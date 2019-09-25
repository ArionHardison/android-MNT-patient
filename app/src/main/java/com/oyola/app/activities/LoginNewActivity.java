package com.oyola.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.oyola.app.HomeActivity;
import com.oyola.app.R;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.helper.SharedHelper;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginNewActivity extends BaseActivity {

    private static final String TAG = LoginNewActivity.class.getSimpleName();
    private static final int RC_GOOGLE_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        configureGoogleSignInObject();
        registerFacebookLoginCallback();
        initializeViewReferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    @OnClick({R.id.cl_facebook, R.id.cl_google, R.id.cl_email, R.id.tv_login_later})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_facebook:
                break;

            case R.id.cl_google:
                launchGoogleSignInActivity();
                break;

            case R.id.cl_email:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;

            case R.id.tv_login_later:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                break;

            default:
                break;
        }
    }

    private void configureGoogleSignInObject() {
        // Configure sign-in to request the user's ID, email address, and basic profile.
        // ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void registerFacebookLoginCallback() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken != null) {
                            SharedHelper.putKey(getApplicationContext(), "access_token",
                                    loginResult.getAccessToken().getToken());
                            GlobalData.access_token = loginResult.getAccessToken().getToken();
//                                RequestData();
                            GlobalData.loginBy = "facebook";
                            HashMap<String, String> map = new HashMap<>();
                            map.put("login_by", GlobalData.loginBy);
                            map.put("accessToken", GlobalData.access_token);
//                                map.put("client_id", BuildConfigure.CLIENT_ID);
//                                map.put("client_secret", BuildConfigure.CLIENT_SECRET);
//                            login(map);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.w(TAG, "FacebookLoginResult:" + exception.getMessage());
                    }
                });
    }

    private void initializeViewReferences() {
        ButterKnife.bind(this);
    }

    private void launchGoogleSignInActivity() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            GlobalData.name = acct.getDisplayName();
//            GlobalData.email = acct.getEmail();
//            GlobalData.imageUrl = "" + acct.getPhotoUrl();
//            Log.d("Google", "display_name:" + acct.getDisplayName());
//            Log.d("Google", "mail:" + acct.getEmail());
//            Log.d("Google", "photo:" + acct.getPhotoUrl());
//            new LoginActivity.RetrieveTokenTask().execute(acct.getEmail());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "GoogleSignInResult:Failed Code=" + e.getStatusCode());
        }
    }
}
