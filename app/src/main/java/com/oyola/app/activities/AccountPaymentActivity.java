package com.oyola.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oyola.app.R;
import com.oyola.app.adapter.AccountPaymentAdapter;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.fragments.CartFragment;
import com.oyola.app.helper.CustomDialog;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.models.Card;
import com.oyola.app.models.Message;
import com.oyola.app.models.Order;

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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.oyola.app.fragments.CartFragment.checkoutMap;
import static com.oyola.app.helper.GlobalData.addCart;
import static com.oyola.app.helper.GlobalData.cardArrayList;
import static com.oyola.app.helper.GlobalData.currencySymbol;
import static com.oyola.app.helper.GlobalData.isCardChecked;

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
    boolean mIsImmediate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = AccountPaymentActivity.this;
        customDialog = new CustomDialog(context);
        if (checkoutMap.get("wallet") != null && checkoutMap.get("wallet").equals("1")
                && addCart.getPayable() < Double.parseDouble(GlobalData.profileModel.getWalletBalance())) {
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
            mEstimatedDeliveryTime = getIntent().getIntExtra("est_delivery_time", 0);
            mRestaurantType = getIntent().getStringExtra("delivery_type");


            cardArrayList = new ArrayList<>();
            accountPaymentAdapter = new AccountPaymentAdapter(AccountPaymentActivity.this, cardArrayList, !isCashVisible);
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

            proceedToPayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCardChecked) {
                        for (int i = 0; i < cardArrayList.size(); i++) {
                            if (cardArrayList.get(i).isChecked()) {
                                Card card = cardArrayList.get(i);
                                CartFragment.checkoutMap.put("payment_mode", "stripe");
                                CartFragment.checkoutMap.put("card_id", String.valueOf(card.getId()));
                                checkOut(CartFragment.checkoutMap);
                                return;
                            }
                        }
                    } else if (cashCheckBox.isChecked()) {
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
                            startActivity(new Intent(context, CurrentOrderDetailActivity.class).putExtra("is_order_page",true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(context, CurrentOrderDetailActivity.class).putExtra("is_order_page",true).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
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
                        GlobalData.isCardChecked = true;
                        proceedToPayBtn.performClick();
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        if (checkoutMap.get("wallet") == null || !checkoutMap.get("wallet").equals("1")
                || !(addCart.getPayable() < Double.parseDouble(GlobalData.profileModel.getWalletBalance()))) {
            String walletMoney = GlobalData.profileModel.getWalletBalance();
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
                finish();
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
