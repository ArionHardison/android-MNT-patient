package com.oyola.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.oyola.app.HomeActivity;
import com.oyola.app.R;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.helper.CustomDialog;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.helper.SharedHelper;
import com.oyola.app.models.AddCart;
import com.oyola.app.models.AddressList;
import com.oyola.app.models.ResetPassword;
import com.oyola.app.models.User;
import com.oyola.app.utils.TextUtils;
import com.oyola.app.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ResetPasswordActivity extends AppCompatActivity {

    Context context;
    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.confirm_password)
    EditText confirmPassword;
    @BindView(R.id.change_btn)
    Button changeBtn;
    @BindView(R.id.sigin_layout)
    LinearLayout siginLayout;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.sign_in_here)
    TextView signInHere;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    String strConfirmPassword, strNewPassword;

    CustomDialog customDialog;
    @BindView(R.id.password_eye_img)
    ImageView passwordEyeImg;
    @BindView(R.id.confirm_password_eye_img)
    ImageView confirmPasswordEyeImg;

    String device_token, device_UDID;
    Utils utils = new Utils();
    String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        context = ResetPasswordActivity.this;
        customDialog = new CustomDialog(context);
        getDeviceToken();
        passwordEyeImg.setTag(1);
        confirmPasswordEyeImg.setTag(1);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @OnClick({R.id.change_btn, R.id.sign_in_here, R.id.password_eye_img, R.id.confirm_password_eye_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.change_btn:
                initValues();
                break;
            case R.id.sign_in_here:
                break;
            case R.id.password_eye_img:
                if (passwordEyeImg.getTag().equals(1)) {
                    newPassword.setTransformationMethod(null);
                    passwordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_close));
                    passwordEyeImg.setTag(0);
                } else {
                    passwordEyeImg.setTag(1);
                    newPassword.setTransformationMethod(new PasswordTransformationMethod());
                    passwordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_open));
                }
                break;
            case R.id.confirm_password_eye_img:
                if (confirmPasswordEyeImg.getTag().equals(1)) {
                    confirmPassword.setTransformationMethod(null);
                    confirmPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_close));
                    confirmPasswordEyeImg.setTag(0);
                } else {
                    confirmPasswordEyeImg.setTag(1);
                    confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_open));
                }
                break;
        }
    }


    private void initValues() {

        strConfirmPassword = confirmPassword.getText().toString();
        strNewPassword = newPassword.getText().toString();
        if (TextUtils.isEmpty(strNewPassword)) {
            Toast.makeText(this, R.string.please_enter_password, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strConfirmPassword)) {
            Toast.makeText(this, R.string.please_enter_your_confirm_password, Toast.LENGTH_SHORT).show();

        } else if (!strConfirmPassword.equalsIgnoreCase(strNewPassword)) {
            Toast.makeText(this, R.string.password_and_confirm_password_doesnot_match, Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(GlobalData.profileModel.getId()));
            map.put("password", strNewPassword);
            map.put("password_confirmation", strConfirmPassword);
            resetPassword(map);
        }

    }

    private void resetPassword(HashMap<String, String> map) {
        customDialog.show();
        Call<ResetPassword> call = apiInterface.resetPassword(map);
        call.enqueue(new Callback<ResetPassword>() {
            @Override
            public void onResponse(@NonNull Call<ResetPassword> call, @NonNull Response<ResetPassword> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, R.string.password_updates, Toast.LENGTH_SHORT).show();
                /*    SharedHelper.putKey(context, "access_token", "Bearer" + " " + response.body().getAccessToken());
                    GlobalData.accessToken = "Bearer" + " " + response.body().getAccessToken();
                    getProfile();*/

                    startActivity(new Intent(context, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
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
            public void onFailure(@NonNull Call<ResetPassword> call, @NonNull Throwable t) {

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
                customDialog.dismiss();
                SharedHelper.putKey(context, "logged", "true");
                GlobalData.profileModel = response.body();
                GlobalData.addCart = new AddCart();
                GlobalData.addCart.setProductList(response.body().getCart());
                GlobalData.addressList = new AddressList();
                GlobalData.addressList.setAddresses(response.body().getAddresses());
                startActivity(new Intent(ResetPasswordActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });
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
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

}