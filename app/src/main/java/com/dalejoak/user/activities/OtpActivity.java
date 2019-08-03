package com.dalejoak.user.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dalejoak.user.models.ForgotPassword;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.dalejoak.user.HomeActivity;
import com.dalejoak.user.R;
import com.dalejoak.user.build.api.ApiClient;
import com.dalejoak.user.build.api.ApiInterface;
import com.dalejoak.user.helper.CustomDialog;
import com.dalejoak.user.helper.GlobalData;
import com.dalejoak.user.helper.SharedHelper;
import com.dalejoak.user.models.AddCart;
import com.dalejoak.user.models.AddressList;
import com.dalejoak.user.models.LoginModel;
import com.dalejoak.user.models.Otp;
import com.dalejoak.user.models.RegisterModel;
import com.dalejoak.user.models.User;
import com.dalejoak.user.utils.Utils;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.philio.pinentry.PinEntryView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.dalejoak.user.SmsRetrive.MySMSBroadcastReceiver.INTENT_OTP;


public class OtpActivity extends AppCompatActivity {


    @BindView(R.id.otp_image)
    ImageView otpImage;
    @BindView(R.id.verification_code_txt)
    TextView verificationCodeTxt;
    @BindView(R.id.veri_txt1)
    TextView veriTxt1;
    @BindView(R.id.veri_txt2)
    TextView veriTxt2;
    @BindView(R.id.rel_verificatin_code)
    RelativeLayout relVerificatinCode;
    @BindView(R.id.otp_value1)
    PinEntryView otpValue1;
    @BindView(R.id.otp_continue)
    Button otpContinue;
    Context context;
    boolean isSignUp = true;
    @BindView(R.id.mobile_number_txt)
    TextView mobileNumberTxt;
    CustomDialog customDialog;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
//    private BroadcastReceiver receiver;

    String device_token, device_UDID;
    Utils utils = new Utils();
    String TAG = "OTPACTIVITY";
    SmsVerifyCatcher smsVerifyCatcher;
    private String hashcode = "";
    //    private BroadcastReceiver receiver;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(INTENT_OTP)) {
                final String message = intent.getStringExtra("otp");
                Log.d("MySMS", "OTPCheck" + message);
                otpValue1.setText(message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        context = OtpActivity.this;
        customDialog = new CustomDialog(context);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            isSignUp = bundle.getBoolean("signup", true);
        }
        mobileNumberTxt.setText(GlobalData.mobile);
        //if(!valOTP.equalsIgnoreCase(""))
        //otpValue1.setText(valOTP);
        /*receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(INTENT_OTP)) {
                    final String message = intent.getStringExtra("otp");
                    Log.d(TAG, "OTPCheck" + message);
                    otpValue1.setText(message);
                }
            }
        };*/

       /* List<String> list = new AppSignatureHelper(this).getAppSignatures();
        Log.d(TAG, "HASH " + list.toString());
        hashcode = list.get(0);*/

        SmsRetriver();
        if(GlobalData.otpValue != 0){
            otpValue1.setText(""+GlobalData.otpValue);
        }

//        otpValue1.setText(String.valueOf(GlobalData.otpValue));
        getDeviceToken();

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                otpValue1.setText(code);//set code in edit text
//                Toast.makeText(context,code,Toast.LENGTH_LONG).show();
                //then you can send verification code to server
            }
        });


    }

    private void SmsRetriver() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Successfully started retriever");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                Log.d(TAG, " started retriever failed");
            }
        });

//        otpValue1.setText(Integer.toString(getIntent().getExtras().getInt("OTP")));


    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("(\\d{6})");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void signup(HashMap<String, String> map) {
        customDialog.show();
        Call<RegisterModel> call = apiInterface.postRegister(map);
        call.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(@NonNull Call<RegisterModel> call, @NonNull Response<RegisterModel> response) {
                if (response.isSuccessful()) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("login_by", GlobalData.loginBy);
                    map.put("accessToken", GlobalData.access_token);
                    login(map);
                } else {
                    customDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("phone"))
                            Toast.makeText(context, jObjError.optString("phone"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("email"))
                            Toast.makeText(context, jObjError.optString("email"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(context, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterModel> call, @NonNull Throwable t) {
                Toast.makeText(OtpActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });

    }

    private void login(HashMap<String, String> map) {
        Call<LoginModel> call;
        if (GlobalData.loginBy.equals("manual"))
            call = apiInterface.postLogin(map);
        else
            call = apiInterface.postSocialLogin(map);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "access_token", response.body().getTokenType() + " " + response.body().getAccessToken());
                    GlobalData.accessToken = response.body().getTokenType() + " " + response.body().getAccessToken();
                    getProfile();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {

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
                    Toast.makeText(context, getResources().getString(R.string.regsiter_success), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
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

            }
        });
    }

    public void getOtpVerification(HashMap<String, String> map) {
        SmsRetriver();
        customDialog.show();
        Call<Otp> call = apiInterface.postOtp(map);
        call.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(@NonNull Call<Otp> call, @NonNull Response<Otp> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    GlobalData.otpValue = response.body().getOtp();
                    otpValue1.setText("");
                    otpValue1.setText(String.valueOf(GlobalData.otpValue));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Otp> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });

    }

    private void getForgotResendOtp() {
        SmsRetriver();
        customDialog.show();
        Call<ForgotPassword> call = apiInterface.forgotPassword(GlobalData.mobile, GlobalData.hashcode);
        call.enqueue(new Callback<ForgotPassword>() {
            @Override
            public void onResponse(@NonNull Call<ForgotPassword> call, @NonNull Response<ForgotPassword> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    GlobalData.otpValue = Integer.parseInt(response.body().getUser().getOtp());
                    otpValue1.setText("");
                    otpValue1.setText(String.valueOf(GlobalData.otpValue));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgotPassword> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });

    }

    @OnClick({R.id.otp_continue, R.id.resend_otp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.otp_continue:
                Log.d("OtpData", otpValue1.getText().toString() + " = " + GlobalData.otpValue);
                if (otpValue1.getText().toString().equals("" + GlobalData.otpValue)) {
                    if (!GlobalData.loginBy.equals("manual")) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("name", GlobalData.name);
                        map.put("email", GlobalData.email);
                        map.put("phone", GlobalData.mobile);
                        map.put("login_by", GlobalData.loginBy);
                        map.put("accessToken", GlobalData.access_token);
                        signup(map);
                    } else if (isSignUp) {
                        startActivity(new Intent(OtpActivity.this, SignUpActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                        finish();
                    } else {
                        startActivity(new Intent(OtpActivity.this, ResetPasswordActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                        finish();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.entor_otp_incorrect), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.resend_otp:
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("phone", GlobalData.mobile);
                map1.put("hashcode", GlobalData.hashcode);
                if (isSignUp)
                    getOtpVerification(map1);
                else
                    getForgotResendOtp();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(INTENT_OTP));

        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}