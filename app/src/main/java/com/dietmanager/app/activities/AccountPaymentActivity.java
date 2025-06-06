package com.dietmanager.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.AccountPaymentAdapter;
import com.dietmanager.app.adapter.SubscribtionListAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.fragments.CartFragment;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.helper.SharedHelper;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.AddressList;
import com.dietmanager.app.models.Card;
import com.dietmanager.app.models.Message;
import com.dietmanager.app.models.Order;
import com.dietmanager.app.models.Otp;
import com.dietmanager.app.models.PlaceOrderResponse;
import com.dietmanager.app.models.User;
import com.dietmanager.app.utils.CommonUtils;
import com.dietmanager.app.utils.TextUtils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dietmanager.app.fragments.CartFragment.checkoutMap;
import static com.dietmanager.app.helper.GlobalData.addCart;
import static com.dietmanager.app.helper.GlobalData.cardArrayList;
import static com.dietmanager.app.helper.GlobalData.currencySymbol;
import static com.dietmanager.app.helper.GlobalData.isCardChecked;

//import com.braintreepayments.api.AndroidPay;
//import com.braintreepayments.api.BraintreeFragment;
//import com.braintreepayments.api.PayPal;
//import com.braintreepayments.api.dropin.DropInActivity;
//import com.braintreepayments.api.dropin.DropInRequest;
//import com.braintreepayments.api.dropin.DropInResult;
//import com.braintreepayments.api.dropin.utils.PaymentMethodType;
//import com.braintreepayments.api.exceptions.InvalidArgumentException;
//import com.braintreepayments.api.interfaces.BraintreeCancelListener;
//import com.braintreepayments.api.interfaces.BraintreeErrorListener;
//import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
//import com.braintreepayments.api.models.AndroidPayCardNonce;
//import com.braintreepayments.api.models.CardNonce;
//import com.braintreepayments.api.models.ClientToken;
//import com.braintreepayments.api.models.PayPalAccountNonce;
//import com.braintreepayments.api.models.PaymentMethodNonce;
//import com.braintreepayments.api.models.PostalAddress;
//import com.braintreepayments.api.models.VenmoAccountNonce;
//import com.foodie.user.braintree.CreateTransactionActivity;
//import com.foodie.user.braintree.Settings;
//import com.google.android.gms.identity.intents.model.CountrySpecification;
//import com.google.android.gms.identity.intents.model.UserAddress;
//import com.google.android.gms.wallet.Cart;
//import com.google.android.gms.wallet.LineItem;

public class AccountPaymentActivity extends AppCompatActivity {
//    public class AccountPaymentActivity extends AppCompatActivity implements PaymentMethodNonceCreatedListener,
//            BraintreeCancelListener, BraintreeErrorListener, DropInResult.DropInResultListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.payment_method_lv)
    ListView paymentMethodLv;
    @BindView(R.id.wallet_amount_txt)
    TextView walletAmtTxt;
    @BindView(R.id.wallet_layout)
    RelativeLayout walletLayout;
    @BindView(R.id.use_wallet_layout)
    LinearLayout useWalletLayout;
    @BindView(R.id.use_wallet_chk_box)
    CheckBox useWalletChkBox;
    NumberFormat numberFormat = GlobalData.getNumberFormat();
    @BindView(R.id.add_new_cart)
    TextView addNewCart;

//    //Braintree integration
//    private static final String KEY_AUTHORIZATION = "com.braintreepayments.demo.KEY_AUTHORIZATION";
//    protected String mAuthorization = "sandbox_8hsjt2ms_p4rvqdnmvxcqdm76";
//    protected String mCustomerId;
//    protected BraintreeFragment mBraintreeFragment;
//    private static final int DROP_IN_REQUEST = 100;
//    private static final String KEY_NONCE = "nonce";
//
//    private PaymentMethodType mPaymentMethodType;
//    private PaymentMethodNonce mNonce;
//
//    private CardView mPaymentMethod;
//    private ImageView mPaymentMethodIcon;
//    private TextView mPaymentMethodTitle;
//    private TextView mPaymentMethodDescription;
//    private TextView mNonceString;
//    private TextView mNonceDetails;
//    private TextView mDeviceData;
//
//    private Button mAddPaymentMethodButton;
//    private Button mPurchaseButton;
//    private ProgressDialog mLoading;

    private boolean mShouldMakePurchase = false;
    private boolean mPurchased = false;
    public static ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static CustomDialog customDialog;
    public static Context context;
    public static AccountPaymentAdapter accountPaymentAdapter;
    public static LinearLayout cashPaymentLayout;
    public static LinearLayout walletPaymentLayout;
    public static RadioButton cashCheckBox;
    public static Button proceedToPayBtn;
    boolean isWalletVisible = false;
    boolean isCashVisible = false;
    Integer mEstimatedDeliveryTime = 0;
    String mRestaurantType = "";
    String planId = "";
    String dietitianId = "";
    boolean mIsImmediate = false;
    private boolean isWithoutCache = false;
    boolean isFromProfile = false;
    boolean isFromSubscribe = false;
    double amountToBePaid = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = AccountPaymentActivity.this;
        getDeviceToken();
        customDialog = new CustomDialog(context);

        if (checkoutMap != null && checkoutMap.containsKey("wallet") && checkoutMap.get("wallet").equals("1")
                && addCart.getPayable() < GlobalData.profileModel.getWalletBalance()) {
            checkOut(checkoutMap);
        } else {
            setContentView(R.layout.activity_account_payment);
            ButterKnife.bind(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            cashPaymentLayout = findViewById(R.id.cash_payment_layout);
            walletPaymentLayout = findViewById(R.id.wallet_payment_layout);
            proceedToPayBtn = findViewById(R.id.proceed_to_pay_btn);
            cashCheckBox = findViewById(R.id.cash_check_box);

            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            isWalletVisible = getIntent().getBooleanExtra("is_show_wallet", false);
            isCashVisible = getIntent().getBooleanExtra("is_show_cash", false);
            mIsImmediate = getIntent().getBooleanExtra("is_immediate", false);
            isFromProfile = getIntent().getBooleanExtra("isFromProfile", false);
            isFromSubscribe = getIntent().getBooleanExtra("isFromSubscribe", false);
            if (isFromSubscribe) {
                planId = getIntent().getStringExtra("plan_id");
                dietitianId = getIntent().getStringExtra("dietitian_id");
            }

            amountToBePaid = getIntent().getDoubleExtra("amountToBePaid", 0.0);
            mEstimatedDeliveryTime = getIntent().getIntExtra("est_delivery_time", 0);
            mRestaurantType = getIntent().getStringExtra("delivery_type");

            if (GlobalData.profileModel.getWalletBalance() != null && GlobalData.profileModel.getWalletBalance() > 0)
                useWalletLayout.setVisibility(VISIBLE);
            else
                useWalletLayout.setVisibility(GONE);

            if (isFromProfile) {
                useWalletLayout.setVisibility(GONE);
                proceedToPayBtn.setVisibility(GONE);
            }

            cardArrayList = new ArrayList<>();
            accountPaymentAdapter = new AccountPaymentAdapter(AccountPaymentActivity.this, cardArrayList, isFromProfile);
            paymentMethodLv.setAdapter(accountPaymentAdapter);

            if (isWalletVisible)
                walletPaymentLayout.setVisibility(VISIBLE);
            else
                walletPaymentLayout.setVisibility(GONE);
            if (isCashVisible)
                cashPaymentLayout.setVisibility(GONE);
            else
                cashPaymentLayout.setVisibility(GONE);

            cashPaymentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cashCheckBox.setChecked(true);
                    isCardChecked = false;
                    accountPaymentAdapter.notifyDataSetChanged();
                    proceedToPayBtn.setVisibility(VISIBLE);
                    for (int i = 0; i < cardArrayList.size(); i++) {
                        cardArrayList.get(i).setChecked(false);
                    }
                    accountPaymentAdapter.notifyDataSetChanged();
                }
            });
            useWalletChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        if(GlobalData.profileModel.getWalletBalance()>=amountToBePaid)
                            proceedToPayBtn.setVisibility(VISIBLE);
                    }
                }
            });


            proceedToPayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isCardChecked) {
                        for (int i = 0; i < cardArrayList.size(); i++) {
                            if (cardArrayList.get(i).isChecked()) {
                                Card card = cardArrayList.get(i);
                                if (isFromSubscribe) {
                                    postsubscribe(String.valueOf(card.getId()));
                                } else {
                                /*if (CartFragment.checkoutMap != null) {
                                    CartFragment.checkoutMap.put("payment_mode", "stripe");
                                    CartFragment.checkoutMap.put("card_id", String.valueOf(card.getId()));
                                    checkOut(CartFragment.checkoutMap);
                                }*/
                                    GlobalData.orderMap.put("card_id", String.valueOf(card.getId()));

                                    if (useWalletChkBox.isChecked())
                                        GlobalData.orderMap.put("is_wallet", "1");
                                    else
                                        GlobalData.orderMap.put("is_wallet", "0");

                                    if (cashCheckBox.isChecked())
                                        GlobalData.orderMap.put("payment_mode", "cash");
                                    else
                                        GlobalData.orderMap.put("payment_mode", "card");

                                    placeorder(GlobalData.orderMap);
                                }
                                return;
                            }
                        }
                    }
                    else if(useWalletChkBox.isChecked()&&GlobalData.profileModel.getWalletBalance()>=amountToBePaid){
                        if (isFromSubscribe) {
                            postsubscribe(null);
                        } else {
                            GlobalData.orderMap.put("is_wallet", "1");

                            if (cashCheckBox.isChecked())
                                GlobalData.orderMap.put("payment_mode", "cash");
                            else
                                GlobalData.orderMap.put("payment_mode", "card");

                            placeorder(GlobalData.orderMap);
                        }
                    }else if (cashCheckBox.isChecked()) {
                        CartFragment.checkoutMap.put("payment_mode", "cash");
                        checkOut(CartFragment.checkoutMap);
                    } else {
                        Toast.makeText(context, R.string.please_select_payment_mode, Toast.LENGTH_SHORT).show();
                    }

                }
            });

//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey(KEY_NONCE)) {
//                mNonce = savedInstanceState.getParcelable(KEY_NONCE);
//            }
//        }
        }
    }


    private void postsubscribe(String cardId) {
        HashMap<String, String> map = new HashMap<>();

        if(cardId!=null)
            map.put("card_id", cardId);

        if (useWalletChkBox.isChecked())
            map.put("is_wallet", "1");
        else
            map.put("is_wallet", "0");

        map.put("plan_id", planId);
        map.put("dietitian_id", dietitianId);
        Call<Otp> postsubscribe = apiInterface.postsubscribe(map);
        postsubscribe.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(@NonNull Call<Otp> call, @NonNull Response<Otp> response) {
                if (response.isSuccessful()) {
                    Otp data = response.body();
                    Toast.makeText(context, data.getMessagenew(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(AccountPaymentActivity.this, SplashActivity.class));
                    finishAffinity();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(context, "UnAuthenticated", Toast.LENGTH_LONG).show();
                        SharedHelper.putKey(context, "logged", "false");
                        startActivity(new Intent(context, MobileNumberActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("Message"))
                            Toast.makeText(context, jObjError.optString("Message"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<Otp> call, @NonNull Throwable t) {

            }
        });
    }

    String TAG = "AccountPaymentActivity";
    String device_token, device_UDID;

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

    private void getProfile() {
//        retryCount++;

        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", device_UDID);
        map.put("device_token", device_token);
        Call<User> getprofile = apiInterface.getProfile(map);
        getprofile.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    GlobalData.profileModel = response.body();
                    if (GlobalData.profileModel.getWalletBalance() != null) {
                        Double walletMoney = GlobalData.profileModel.getWalletBalance();
                        walletAmtTxt.setText(currencySymbol + " " + walletMoney);
                        if (!isFromProfile&&GlobalData.profileModel.getWalletBalance() != null && GlobalData.profileModel.getWalletBalance() > 0)
                            useWalletLayout.setVisibility(VISIBLE);
                        else
                            useWalletLayout.setVisibility(GONE);
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(context, "UnAuthenticated", Toast.LENGTH_LONG).show();
                        SharedHelper.putKey(context, "logged", "false");
                        startActivity(new Intent(context, MobileNumberActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    private void placeorder(HashMap<String, String> map) {
        customDialog = new CustomDialog(context);
        customDialog.show();
        Call<PlaceOrderResponse> call = apiInterface.placeorder(map);
        call.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlaceOrderResponse> call, @NonNull Response<PlaceOrderResponse> response) {
                customDialog.dismiss();
                GlobalData.orderMap.clear();
                if (response.isSuccessful()) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("isFromSignUp", false);
                    startActivity(intent);
                    finishAffinity();*/

                    startActivity(new Intent(context, OrdersActivity.class));

//                    startActivity(new Intent(context, CurrentOrderDetailActivity.class).putExtra("orderId",response.body().getOrderId()));

                    //finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("password"))
                            Toast.makeText(context, jObjError.optJSONArray("password").get(0).toString(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceOrderResponse> call, @NonNull Throwable t) {
                GlobalData.orderMap.clear();
                customDialog.dismiss();
                Toast.makeText(context, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkOut(HashMap<String, String> map) {
        customDialog.show();
        Call<Order> call = apiInterface.postCheckout(map);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    GlobalData.addCart = null;
                    GlobalData.notificationCount = 0;
                    GlobalData.selectedShop = null;
                    GlobalData.profileModel.setWalletBalance(response.body().getUser().getWalletBalance());
                    GlobalData.isSelectedOrder = new Order();
                    GlobalData.isSelectedOrder = response.body();
                    if (mRestaurantType.equalsIgnoreCase("PICKUP")) {
                        if (mIsImmediate) {
                            showPickUpSuccessDialog();
                        } else {
                            startActivity(new Intent(context, CurrentOrderDetailActivity.class).putExtra("is_order_page", true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(context, CurrentOrderDetailActivity.class).putExtra("is_order_page", true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String message = jObjError.optString("message");
                        if (TextUtils.isEmpty(message)) {
                            JSONArray jsonDateErrorArray = jObjError.optJSONArray("delivery_date");
                            JSONArray jsonTimeErrorArray = jObjError.optJSONArray("delivery_time");
                            if (jsonDateErrorArray != null && jsonDateErrorArray.length() > 0) {
                                String jsonErrorMessage = jsonDateErrorArray.get(0).toString();
                                if (!TextUtils.isEmpty(jsonErrorMessage))
                                    CommonUtils.showToast(AccountPaymentActivity.this, jsonErrorMessage);
                            } else if (jsonTimeErrorArray != null && jsonTimeErrorArray.length() > 0) {
                                String jsonErrorMessage = jsonTimeErrorArray.get(0).toString();
                                if (!TextUtils.isEmpty(jsonErrorMessage))
                                    CommonUtils.showToast(AccountPaymentActivity.this, jsonErrorMessage);
                            }
                        } else {
                            CommonUtils.showToast(AccountPaymentActivity.this, message);
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Toast.makeText(AccountPaymentActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCardList() {
        customDialog.show();
        Call<List<Card>> call = apiInterface.getCardList();
        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(@NonNull Call<List<Card>> call, @NonNull Response<List<Card>> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    cardArrayList.clear();
                    cardArrayList.addAll(response.body());
                    accountPaymentAdapter.notifyDataSetChanged();
                    if (cardArrayList.size() == 1) {
                        cardArrayList.get(0).setChecked(true);
                        if (!isFromProfile)
                            proceedToPayBtn.setVisibility(VISIBLE);
                        GlobalData.isCardChecked = true;/*
                        if (!isWithoutCache)
                            proceedToPayBtn.performClick();*/
                    }
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
            public void onFailure(@NonNull Call<List<Card>> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });

    }

    public static void deleteCard(final int id) {
        customDialog.show();
        Call<Message> call = apiInterface.deleteCard(id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < GlobalData.cardArrayList.size(); i++) {
                        if (GlobalData.cardArrayList.get(i).getId().equals(id)) {
                            GlobalData.cardArrayList.remove(i);
                            accountPaymentAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
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
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

//    public void launchDropIn(View v) {
//        DropInRequest dropInRequest = new DropInRequest()
//                .clientToken(mAuthorization)
//                .amount("1.00")
//                .requestThreeDSecureVerification(Settings.isThreeDSecureEnabled(this))
//                .collectDeviceData(Settings.shouldCollectDeviceData(this))
//                .androidPayCart(getAndroidPayCart())
//                .androidPayShippingAddressRequired(Settings.isAndroidPayShippingAddressRequired(this))
//                .androidPayPhoneNumberRequired(Settings.isAndroidPayPhoneNumberRequired(this))
//                .androidPayAllowedCountriesForShipping(Settings.getAndroidPayAllowedCountriesForShipping(this));
//
//        if (Settings.isPayPalAddressScopeRequested(this)) {
//            dropInRequest.paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
//        }
//
//        startActivityForResult(dropInRequest.getIntent(this), DROP_IN_REQUEST);
//    }

//    private Cart getAndroidPayCart() {
//        return Cart.newBuilder()
//                .setCurrencyCode(Settings.getAndroidPayCurrency(this))
//                .setTotalPrice("1.00")
//                .addLineItem(LineItem.newBuilder()
//                        .setCurrencyCode("USD")
//                        .setDescription("Description")
//                        .setQuantity("1")
//                        .setUnitPrice("1.00")
//                        .setTotalPrice("1.00")
//                        .build())
//                .build();
//    }


    protected void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        safelyCloseLoadingView();
//
//        if (resultCode == RESULT_OK) {
//            DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//            displayResult(result.getPaymentMethodNonce(), result.getDeviceData());
//            mPurchaseButton.setEnabled(true);
//        } else if (resultCode != RESULT_CANCELED) {
//            safelyCloseLoadingView();
//            showDialog(((Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR))
//                    .getMessage());
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
//        if (mNonce != null) {
//            outState.putParcelable(KEY_NONCE, mNonce);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
        isWithoutCache = getIntent().getBooleanExtra("without_cache", false);
        if (checkoutMap == null || !checkoutMap.containsKey("wallet") ||
                !checkoutMap.get("wallet").equals("1")
                || !(addCart.getPayable() < GlobalData.profileModel.getWalletBalance())) {
            Double walletMoney = GlobalData.profileModel.getWalletBalance();
            walletAmtTxt.setText(currencySymbol + " " + walletMoney);
            getCardList();
//        if (mPurchased) {
//            mPurchased = false;
//            clearNonce();
//
//            try {
//                if (ClientToken.fromString(mAuthorization) instanceof ClientToken) {
//                    DropInResult.fetchDropInResult(this, mAuthorization, this);
//                } else {
//                    mAddPaymentMethodButton.setVisibility(VISIBLE);
//                }
//            } catch (InvalidArgumentException e) {
//                mAddPaymentMethodButton.setVisibility(VISIBLE);
//            }
//        }
        }
    }

    @OnClick({R.id.wallet_layout, R.id.add_new_cart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.wallet_layout:
                startActivity(new Intent(this, WalletActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                //finish();
                break;
            case R.id.add_new_cart:
//              launchDropIn(view);
                startActivity(new Intent(AccountPaymentActivity.this, AddCardActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                break;
        }
    }

    private void showPickUpSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountPaymentActivity.this);
        final FrameLayout frameView = new FrameLayout(AccountPaymentActivity.this);
        builder.setView(frameView);
        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_order_pickup, frameView);
        alertDialog.setCancelable(false);
        Button mBtnOk = dialogView.findViewById(R.id.btn_ok);
        TextView mTxtTime =
                dialogView.findViewById(R.id.txt_time);
        mTxtTime.setText(mEstimatedDeliveryTime + " mins");
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivity(new Intent(context, CurrentOrderDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }
}
