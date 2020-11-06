package com.dietmanager.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.R;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.AddressList;
import com.dietmanager.app.models.LoginModel;
import com.dietmanager.app.models.SocialModel;
import com.dietmanager.app.models.User;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.helper.GlobalData.profileModel;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_GOOGLE_SIGN_IN = 100;
    private static final int REQ_SIGN_IN_REQUIRED = 101;
    Context context;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    JSONObject json;
    SocialModel mSocialModel;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    String device_token, device_UDID;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;


    private void addLink(TextView textView, String patternToMatch,
                         final String link) {
        Linkify.TransformFilter filter = (match, url) -> link;
        Linkify.addLinks(textView, Pattern.compile(patternToMatch), null, null,
                filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView tvTermsAndPolicy = findViewById(R.id.tv_terms_policy);
        tvTermsAndPolicy.setText(getString(R.string.login_terms_privacy_policy));
        addLink(tvTermsAndPolicy, getString(R.string.login_terms_and_conditions_label), getString(R.string.login_terms_and_conditions_url));
        addLink(tvTermsAndPolicy, getString(R.string.login_privacy_policy_label), getString(R.string.login_privacy_policy_url));

        context = LoginActivity.this;
        customDialog = new CustomDialog(context);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        callbackManager = CallbackManager.Factory.create();
        configureGoogleSignInObject();
        initializeViewReferences();
        getDeviceToken();
    }

    public void getDeviceToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
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

    @OnClick({R.id.cl_facebook, R.id.cl_google, R.id.cl_mobile, R.id.tv_login_later})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_facebook:
                registerFacebookLoginCallback();
                break;

            case R.id.cl_google:
                launchGoogleSignInActivity();
                break;

            case R.id.cl_mobile:
                Intent intent = new Intent(getApplicationContext(), MobileNumberActivity.class);
                intent.putExtra("mIsFromSo cial", false);
                startActivity(intent);
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
                        .requestIdToken(getString(R.string.google_client_id))
                        .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void registerFacebookLoginCallback() {
        if (callbackManager != null) {
            callbackManager = CallbackManager.Factory.create();
        }
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (accessToken != null) {
                            SharedHelper.putKey(getApplicationContext(), "access_token",
                                    loginResult.getAccessToken().getToken());
                            mSocialModel = new SocialModel();
                            mSocialModel.setmAccessToken(loginResult.getAccessToken().getToken());
                            mSocialModel.setmLoginBy("facebook");
                            requestFbData();
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
            if (completedTask.isSuccessful()) {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                mSocialModel = new SocialModel();
                mSocialModel.setmLoginBy("google");
                mSocialModel.setmName(account.getDisplayName());
                mSocialModel.setmEmail(account.getEmail());
                mSocialModel.setmImageUrl(account.getPhotoUrl().toString());
                new RetrieveTokenTask().execute(account.getEmail());
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "GoogleSignInResult:Failed Code=" + e.getStatusCode());
        }
    }

    public void requestFbData() {
        if (isInternet) {
            customDialog.show();
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.e("response", "" + response);
                    json = response.getJSONObject();
                    Log.e("FB JSON", "" + json);
                    try {
                        if (json != null) {
                            mSocialModel.setmName(json.optString("name"));
                            mSocialModel.setmEmail(json.optString("email"));
                            com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
                            String FBUserID = profile.getId();
                            Log.e("FBUserID", "" + FBUserID);
                            URL image_value = new URL("https://graph.facebook.com/" + FBUserID + "/picture?type=large");
                            mSocialModel.setmImageUrl(image_value.toString());
                            mSocialModel.setmLoginBy("facebook");
                            socialLogin(mSocialModel);

                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,email,picture");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.check_your_internet)).setCancelable(false);
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(NetworkAction);

                }
            });
            builder.show();
        }
    }

    private void socialLogin(SocialModel model) {
        HashMap<String, String> map = new HashMap<>();
        map.put("login_by", model.getmLoginBy());
        map.put("accessToken", model.getmAccessToken());
        Call<LoginModel> call;
        call = apiInterface.postSocialLogin(map);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "access_token", response.body().getTokenType() + " " + response.body().getAccessToken());
                    SharedHelper.putKey(context, "login_by", mSocialModel.getmLoginBy());
                    getProfile();
                } else {
                    customDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("status")) {
                            if (!jObjError.getBoolean("status")) {
                                Intent intent = new Intent(LoginActivity.this, MobileNumberActivity.class);
                                intent.putExtra("social_login_model", mSocialModel);
                                intent.putExtra("mIsFromSocial", true);
                                startActivity(intent);
                            }
                        } else if (jObjError.has("message")) {
                            Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
                customDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });
    }

    private void getProfile() {
        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", device_UDID);
        map.put("device_token", device_token);
        Call<User> getprofile = apiInterface.getProfile(map);
        getprofile.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "logged", "true");
                    GlobalData.profileModel = response.body();
                    GlobalData.addCart = new AddCart();
                    GlobalData.addCart.setProductList(response.body().getCart());
                    GlobalData.addressList = new AddressList();
                    GlobalData.addressList.setAddresses(response.body().getAddresses());
                    GlobalData.subscription = profileModel.getSubscription();
//                    Toast.makeText(context, getResources().getString(R.string.regsiter_success), Toast.LENGTH_SHORT).show();
                    if (   GlobalData.subscription !=null&& GlobalData.subscription!="") {
                        Intent intent = new Intent(context, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("isFromSignUp", true);
                        startActivity(intent);
                        finish();
                    }else {
                        startActivity(new Intent(context, SubscribePlanActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(context, "UnAuthenticated", Toast.LENGTH_LONG).show();
                        SharedHelper.putKey(context, "logged", "false");
                        startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String GoogleaccessToken) {
            super.onPostExecute(GoogleaccessToken);
            Log.e("Token", GoogleaccessToken);
            mSocialModel.setmAccessToken(GoogleaccessToken);
            mSocialModel.setmLoginBy("google");
            socialLogin(mSocialModel);
        }
    }

}
