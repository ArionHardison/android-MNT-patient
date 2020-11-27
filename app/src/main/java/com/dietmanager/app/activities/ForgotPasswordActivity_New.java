package com.dietmanager.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dietmanager.app.CountryPicker.Country;
import com.dietmanager.app.CountryPicker.CountryPicker;
import com.dietmanager.app.CountryPicker.CountryPickerListener;
import com.dietmanager.app.R;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.build.configure.BuildConfigure;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.ForgotPassword;
import com.dietmanager.app.models.LoginModel;
import com.dietmanager.app.utils.Utils;

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

public class ForgotPasswordActivity_New extends AppCompatActivity {

    @BindView(R.id.et_mobile_number)
    EditText etMobileNumber;
    @BindView(R.id.next_btn)
    Button nextBtn;
    @BindView(R.id.countryImage)
    ImageView mCountryFlagImageView;
    @BindView(R.id.countryNumber)
    TextView mCountryDialCodeTextView;
    @BindView(R.id.back_img)
    ImageView backImg;

    Context context;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private CountryPicker mCountryPicker;
    String country_code = "+91";
    String mobileNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword_new);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        mCountryPicker = CountryPicker.newInstance(getResources().getString(R.string.select_contry));
        context = ForgotPasswordActivity_New.this;
        customDialog = new CustomDialog(context);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
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
        /*if (country != null) {
            mCountryFlagImageView.setImageResource(country.getFlag());
            mCountryDialCodeTextView.setText(country.getDialCode());
            country_code = country.getDialCode();
        } else {*/
        mCountryFlagImageView.setImageResource(R.drawable.flag_us);
        mCountryDialCodeTextView.setText("+1");
        country_code = "+1";
        //}
    }

    @OnClick({R.id.next_btn,R.id.back_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.next_btn:
                mobileNumber = country_code + etMobileNumber.getText().toString();
                if (!isValidMobile(mobileNumber)) {
                    /*HashMap<String, String> map = new HashMap<>();
                    map.put("phone", mobileNumber);
                    getOtpVerification(map);*/
                    Toast.makeText(this, getResources().getString(R.string.please_enter_valid_number), Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(this,OtpActivity.class));
                }else {
                    if (isInternet) {
                        apicall();
                    } else
                        Utils.displayMessage(ForgotPasswordActivity_New.this,this ,getString(R.string.oops_no_internet));

                }
                break;
            case R.id.back_img:
                onBackPressed();
                break;
        }
    }

    private void apicall() {
        Call<ForgotPassword> call;
        customDialog.show();
        call = apiInterface.forgotPassword(mobileNumber);
        call.enqueue(new Callback<ForgotPassword>() {
            @Override
            public void onResponse(@NonNull Call<ForgotPassword> call, @NonNull Response<ForgotPassword> response) {
                if (response.isSuccessful()) {
                    customDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity_New.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity_New.this, OtpActivity.class);
                    if (response.body().getUser()!=null)
                    {
                        GlobalData.profileModel = response.body().getUser();
                        intent.putExtra("otp", response.body().getUser().getOtp());
                        //SharedHelper.putKey(context, "otp", "" + response.body().getUser().getOtp());
                    }
                    intent.putExtra("isUserExist", false);
                    intent.putExtra("mIsFromSocial", false);
                    intent.putExtra("mobile", mobileNumber);
                    startActivity(intent);
                    finish();
                }  else {
                    customDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("phone"))
                            Toast.makeText(context, jObjError.optString("phone"), Toast.LENGTH_LONG).show();
                        else
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgotPassword> call, @NonNull Throwable t) {

            }
        });
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
