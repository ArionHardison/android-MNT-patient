package com.dietmanager.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dietmanager.app.CountryPicker.Country;
import com.dietmanager.app.CountryPicker.CountryPicker;
import com.dietmanager.app.CountryPicker.CountryPickerListener;
import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.OyolaApplication;
import com.dietmanager.app.R;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.build.configure.BuildConfigure;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.AddressList;
import com.dietmanager.app.models.LoginModel;
import com.dietmanager.app.models.Otp;
import com.dietmanager.app.models.SocialModel;
import com.dietmanager.app.models.User;
import com.dietmanager.app.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberActivity extends AppCompatActivity {

    @BindView(R.id.app_logo)
    ImageView appLogo;
    @BindView(R.id.et_mobile_number)
    EditText etMobileNumber;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.connect_with)
    TextView connectWith;
    @BindView(R.id.facebook_login)
    ImageButton facebookLogin;
    @BindView(R.id.google_login)
    ImageButton googleLogin;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.countryImage)
    ImageView mCountryFlagImageView;
    @BindView(R.id.countryNumber)
    TextView mCountryDialCodeTextView;
    @BindView(R.id.back_img)
    ImageView backImg;/*
    @BindView(R.id.already_have_aacount_txt)
    TextView alreadyHaveAacountTxt;*/
    private CountryPicker mCountryPicker;
    String country_code = "+61";
    Context context;
    CustomDialog customDialog;
    JSONObject json;
    ConnectionHelper helper;
    Boolean isInternet;
    String mobileNumber = "";
    Boolean mIsFromSocial = false;
    SocialModel mModel;
    /*----------Google Login---------------*/
    String TAG = "Login";
    String device_token, device_UDID;
    String GRANT_TYPE = "password";

    @BindView(R.id.et_current_password)
    EditText etCurrentPassword;
    @BindView(R.id.et_current_password_eye_img)
    ImageView etCurrentPasswordEyeImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);
        ButterKnife.bind(this);
        mCountryPicker = CountryPicker.newInstance(getResources().getString(R.string.select_contry));
        context = MobileNumberActivity.this;
        customDialog = new CustomDialog(context);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        OyolaApplication.getAppInstance().fetchDeviceToken();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mIsFromSocial = bundle.getBoolean("mIsFromSocial");
            mModel = (SocialModel) bundle.getSerializable("social_login_model");
        }

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        /*// OTP REtriver
        List<String> list = new AppSignatureHelper(this).getAppSignatures();
        Log.d(TAG, "HASH " + list.toString());
        GlobalData.hashcode = list.get(0);
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
*/
        // You can limit the displayed countries
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country s1, Country s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
        mCountryPicker.setCountriesList(countryList);
        setListener();
        etCurrentPasswordEyeImg.setTag(1);
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

    private void login(HashMap<String, String> map) {
        Call<LoginModel> call;
     /*   if (GlobalData.loginBy.equals("manual"))
            call = apiInterface.postLogin(map);
        else
            call = apiInterface.postSocialLogin(map);*/
        SharedHelper.putKey(context, "access_token", "");
        call = apiInterface.postLogin(map);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "access_token", "" + response.body().getAccessToken());
                    getProfile();
                }  else {
                customDialog.dismiss();
                try {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                }
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
                    SharedHelper.putKey(context, "referral_code", response.body().getReferralCode());
                    GlobalData.profileModel = response.body();
                    GlobalData.addCart = new AddCart();
                    GlobalData.addCart.setProductList(response.body().getCart());
                    GlobalData.addressList = new AddressList();
                    GlobalData.addressList.setAddresses(response.body().getAddresses());
//                    Toast.makeText(context, getResources().getString(R.string.regsiter_success), Toast.LENGTH_SHORT).show();
                    if (mIsFromSocial) {
                        Intent intent = new Intent(context, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("isFromSignUp", true);
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(context, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
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
        customDialog.show();
        Call<Otp> call = apiInterface.sendOtp(map);
        call.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(@NonNull Call<Otp> call, @NonNull Response<Otp> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    if (mIsFromSocial) {
                        if (response.body().getError().equalsIgnoreCase("no")) {
                            if (response.body().getUser().equalsIgnoreCase("yes")) {
                                Toast.makeText(MobileNumberActivity.this, "Mobile number already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MobileNumberActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MobileNumberActivity.this, OtpActivity.class);
                                intent.putExtra("otp", response.body().getOtp());
                                if (response.body().getUser().equalsIgnoreCase("yes")) {
                                    intent.putExtra("isUserExist", true);
                                } else {
                                    intent.putExtra("isUserExist", false);
                                }
                                intent.putExtra("mobile", mobileNumber);
                                intent.putExtra("mIsFromSocial", mIsFromSocial);
                                intent.putExtra("social_login_model", mModel);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(MobileNumberActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (response.body().getError().equalsIgnoreCase("no")) {
                            Toast.makeText(MobileNumberActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MobileNumberActivity.this, OtpActivity.class);
                            intent.putExtra("otp", response.body().getOtp());
                            if (response.body().getUser().equalsIgnoreCase("yes")) {
                                intent.putExtra("isUserExist", true);
                            } else {
                                intent.putExtra("isUserExist", false);
                            }

                            intent.putExtra("mobile", mobileNumber);
                            intent.putExtra("mIsFromSocial", mIsFromSocial);
                            intent.putExtra("social_login_model", mModel);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MobileNumberActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("phone")) {
                            if (jObjError.optJSONArray("phone") != null)
                                Toast.makeText(context, jObjError.optJSONArray("phone").get(0).toString(), Toast.LENGTH_LONG).show();
                        } else if (jObjError.has("email"))
                            Toast.makeText(context, jObjError.optString("email"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Otp> call, @NonNull Throwable t) {
                customDialog.dismiss();

            }
        });

    }

    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,
                                        int flagDrawableResID) {
                mCountryDialCodeTextView.setText(dialCode);
                country_code = dialCode;
                mCountryFlagImageView.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
            }
        });
        mCountryDialCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        mCountryFlagImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = Country.getCountryFromSIM(this);
        if (country != null) {
            mCountryFlagImageView.setImageResource(country.getFlag());
            mCountryDialCodeTextView.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            mCountryFlagImageView.setImageResource(R.drawable.flag_au);
            mCountryDialCodeTextView.setText("+61");
            country_code = "+61";
        }
    }

    @OnClick({R.id.back_img, R.id.next_btn/*, R.id.already_have_aacount_txt*/, R.id.et_current_password_eye_img, R.id.facebook_login, R.id.google_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
       /*     case R.id.already_have_aacount_txt:
                startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                finish();
                break;*/

            case R.id.et_current_password_eye_img:
                if (etCurrentPasswordEyeImg.getTag().equals(1)) {
                    etCurrentPassword.setTransformationMethod(null);
                    etCurrentPasswordEyeImg.setImageResource(R.drawable.ic_eye_close);
                    etCurrentPasswordEyeImg.setTag(0);
                } else {
                    etCurrentPassword.setTransformationMethod(new PasswordTransformationMethod());
                    etCurrentPasswordEyeImg.setImageResource(R.drawable.ic_eye_open);
                    etCurrentPasswordEyeImg.setTag(1);
                }
                break;
            case R.id.facebook_login:
                break;
            case R.id.google_login:
                break;

            case R.id.next_btn:
                mobileNumber = country_code + etMobileNumber.getText().toString();
                if (!isValidMobile(mobileNumber)) {
                    /*HashMap<String, String> map = new HashMap<>();
                    map.put("phone", mobileNumber);
                    getOtpVerification(map);*/
                    Toast.makeText(this, getResources().getString(R.string.please_enter_valid_number), Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(this,OtpActivity.class));
                } else if (etCurrentPassword.getText().toString().isEmpty()) {
                    Utils.displayMessage(MobileNumberActivity.this, MobileNumberActivity.this, getResources().getString(R.string.please_enter_password));
                } else if (etCurrentPassword.getText().toString().length() < 6) {
                    Utils.displayMessage(MobileNumberActivity.this, MobileNumberActivity.this, getResources().getString(R.string.please_enter_minimum_length_password));
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("username", mobileNumber);
                    map.put("password", etCurrentPassword.getText().toString());
                    map.put("grant_type", GRANT_TYPE);
                    map.put("client_id", BuildConfigure.CLIENT_ID);
                    map.put("client_secret", BuildConfigure.CLIENT_SECRET);
                    //map.put("device_type", "android");
                   // map.put("device_id", device_UDID);
                   // map.put("device_token", device_token);
                    login(map);
                }
                break;
        }

    }

    private boolean isValidMobile(String phone) {
        return !(phone == null || phone.length() < 6 || phone.length() > 13) && android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

}
