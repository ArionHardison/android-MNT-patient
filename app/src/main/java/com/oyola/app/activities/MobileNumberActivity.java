package com.oyola.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.oyola.app.CountryPicker.Country;
import com.oyola.app.CountryPicker.CountryPicker;
import com.oyola.app.CountryPicker.CountryPickerListener;
import com.oyola.app.R;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.helper.ConnectionHelper;
import com.oyola.app.helper.CustomDialog;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.helper.SharedHelper;
import com.oyola.app.models.ForgotPassword;
import com.oyola.app.models.Otp;
import com.oyola.app.models.SocialModel;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    ImageView backImg;
    @BindView(R.id.already_have_aacount_txt)
    TextView alreadyHaveAacountTxt;
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
    String TAG = "ActivitySocialLogin";

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

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        Country country = Country.getCountrydetails("IN");
        if (country != null) {
            mCountryFlagImageView.setImageResource(country.getFlag());
            mCountryDialCodeTextView.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {
            mCountryFlagImageView.setImageResource(R.drawable.flag_in);
            mCountryDialCodeTextView.setText("IN");
            country_code = "+91";
        }
    }

    @OnClick({R.id.back_img, R.id.next_btn, R.id.already_have_aacount_txt, R.id.facebook_login, R.id.google_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
            case R.id.already_have_aacount_txt:
                startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                finish();
                break;
            case R.id.facebook_login:
                break;
            case R.id.google_login:
                break;

            case R.id.next_btn:
                mobileNumber = country_code + etMobileNumber.getText().toString();
                if (isValidMobile(mobileNumber)) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("phone", mobileNumber);
                    getOtpVerification(map);
//                    startActivity(new Intent(this,OtpActivity.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.please_enter_valid_number), Toast.LENGTH_SHORT).show();
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
