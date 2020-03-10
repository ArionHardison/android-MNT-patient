package com.oyola.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.oyola.app.HomeActivity;
import com.oyola.app.R;
import com.oyola.app.activities.AccountPaymentActivity;
import com.oyola.app.activities.PromotionActivity;
import com.oyola.app.activities.SaveDeliveryLocationActivity;
import com.oyola.app.activities.SetDeliveryLocationActivity;
import com.oyola.app.adapter.ViewCartAdapter;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.helper.ConnectionHelper;
import com.oyola.app.helper.CustomDialog;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.models.AddCart;
import com.oyola.app.models.Cart;
import com.oyola.app.models.DeliveryOption;
import com.oyola.app.utils.Utils;
import com.robinhood.ticker.TickerUtils;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oyola.app.adapter.ViewCartAdapter.bottomSheetDialogFragment;


/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */
public class CartFragment extends Fragment implements OrderDeliveryTypeFragment.BottomListener {

    private static final String TAG = "CartFragment";
    private static final int PROMOCODE_APPLY = 201;
    @BindView(R.id.re)
    RelativeLayout re;
    @BindView(R.id.order_item_rv)
    RecyclerView orderItemRv;

    @BindView(R.id.map_marker_image)
    ImageView mapMarkerImage;
    @BindView(R.id.location_error_title)
    TextView locationErrorTitle;
    @BindView(R.id.location_error_sub_title)
    TextView locationErrorSubTitle;
    @BindView(R.id.add_address_btn)
    Button addAddressBtn;
    @BindView(R.id.dummy_image_view)
    ImageView dummyImageView;
    @BindView(R.id.total_amount)
    TextView totalAmount;
    @BindView(R.id.buttonLayout)
    LinearLayout buttonLayout;
    @BindView(R.id.address_header)
    TextView addressHeader;
    @BindView(R.id.address_detail)
    TextView addressDetail;
    @BindView(R.id.address_delivery_time)
    TextView addressDeliveryTime;
    @BindView(R.id.add_address_txt)
    TextView addAddressTxt;
    @BindView(R.id.promo_code_apply)
    TextView promoCodeApply;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.layout_order_type)
    LinearLayout layoutOrderType;
    public static RelativeLayout dataLayout;
    public static RelativeLayout errorLayout;
    public static LinearLayout locationInfoLayout;
    @BindView(R.id.location_error_layout)
    RelativeLayout locationErrorLayout;
    @BindView(R.id.restaurant_image)
    ImageView restaurantImage;
    @BindView(R.id.restaurant_name)
    TextView restaurantName;
    @BindView(R.id.restaurant_description)
    TextView restaurantDescription;
    @BindView(R.id.proceed_to_pay_btn)
    Button proceedToPayBtn;
    @BindView(R.id.selected_address_btn)
    Button selectedAddressBtn;
    @BindView(R.id.error_layout_description)
    TextView errorLayoutDescription;
    @BindView(R.id.use_wallet_chk_box)
    CheckBox useWalletChkBox;
    @BindView(R.id.amount_txt)
    TextView amountTxt;
    @BindView(R.id.custom_notes)
    TextView customNotes;
    @BindView(R.id.wallet_layout)
    LinearLayout walletLayout;
    @BindView(R.id.pickup_btn)
    Button pickupBtn;
    @BindView(R.id.delivery_btn)
    Button deliveryBtn;
    @BindView(R.id.txt_delivery_content)
    TextView mTxtDeliveryContent;
    @BindView(R.id.lay_delivery_fees)
    LinearLayout layoutDeliveryFees;
    private Context context;
    private ViewGroup toolbar;
    private View toolbarLayout;
    AnimatedVectorDrawableCompat avdProgress;
    //Animation number
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();

    public static TextView itemTotalAmount, deliveryCharges, discountAmount, serviceTax, payAmount, promocode_amount;
    LinearLayout lnrPromocodeAmount;
    String promo_code = "";
    Fragment orderFullViewFragment;
    FragmentManager fragmentManager;
    //Orderitem List
    public static List<Cart> viewCartItemList;

    int priceAmount = 0;
    int discount = 0;
    public static int deliveryChargeValue = 0;
    public static int tax = 0;
    int itemCount = 0;
    int itemQuantity = 0;
    int ADDRESS_SELECTION = 1;

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static ViewCartAdapter viewCartAdapter;
    CustomDialog customDialog;
    ViewSkeletonScreen skeleton;
    ConnectionHelper connectionHelper;
    Activity activity;
    boolean mIsPickUpAvailable = false;
    boolean mIsOnlyPickUpAvailable = false;
    boolean mIsDeliveryAvailable = false;
    boolean mIsPickUpSelected = false;
    boolean mIsDeliverySelected = false;
    boolean isActivityResultCalled = false;
    AddCart addCart;

    public static HashMap<String, String> checkoutMap;
    OrderDeliveryTypeFragment bottomSheetTypeDialogFragment;
    Integer mEstimatedDeliveryTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        this.activity = getActivity();
        bottomSheetTypeDialogFragment = new OrderDeliveryTypeFragment();
        bottomSheetTypeDialogFragment.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        connectionHelper = new ConnectionHelper(context);

        /*  Intialize Global Values*/
        itemTotalAmount = view.findViewById(R.id.item_total_amount);
        deliveryCharges = view.findViewById(R.id.delivery_charges);
        discountAmount = view.findViewById(R.id.discount_amount);
        promocode_amount = view.findViewById(R.id.promocode_amount);
        serviceTax = view.findViewById(R.id.service_tax);
        payAmount = view.findViewById(R.id.total_amount);
        dataLayout = view.findViewById(R.id.data_layout);
        lnrPromocodeAmount = view.findViewById(R.id.lnrPromocodeAmount);
        errorLayout = view.findViewById(R.id.error_layout);
        locationInfoLayout = view.findViewById(R.id.location_info_layout);

        GlobalData.addCart = null;

        HomeActivity.updateNotificationCount(context, 0);
        customDialog = new CustomDialog(context);

        skeleton = Skeleton.bind(dataLayout)
                .load(R.layout.skeleton_fragment_cart)
                .show();
        viewCartItemList = new ArrayList<>();
        //Offer Restaurant Adapter
        orderItemRv.setNestedScrollingEnabled(false);
        orderItemRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        orderItemRv.setItemAnimator(new DefaultItemAnimator());
        orderItemRv.setHasFixedSize(false);


        //Intialize address Value
       /* if (GlobalData.selectedAddress != null && GlobalData.selectedAddress.getLandmark() != null) {
            if (GlobalData.addressList.getAddresses().size() == 1)
                addAddressTxt.setText(getString(R.string.add_address));
            else
                addAddressTxt.setText(getString(R.string.change_address));
            addAddressBtn.setBackgroundResource(R.drawable.button_corner_bg_green);
            addAddressBtn.setText(getResources().getString(R.string.proceed_to_pay));
            addressHeader.setText(GlobalData.selectedAddress.getType());
            addressDetail.setText(GlobalData.selectedAddress.getMapAddress());
            if (viewCartItemList != null && viewCartItemList.size() != 0)
                addressDeliveryTime.setText(viewCartItemList.get(0).getProduct().getShop().getEstimatedDeliveryTime().toString() + " Mins");
        } else if (GlobalData.addressList != null) {
            addAddressBtn.setBackgroundResource(R.drawable.button_curved);
            addAddressBtn.setText(getResources().getString(R.string.add_address));
            locationErrorSubTitle.setText(GlobalData.addressHeader);
            selectedAddressBtn.setVisibility(View.VISIBLE);
            locationErrorLayout.setVisibility(View.VISIBLE);
            locationInfoLayout.setVisibility(View.GONE);
        } else {
            if (GlobalData.selectedAddress != null)
                locationErrorSubTitle.setText(GlobalData.selectedAddress.getMapAddress());
            else
                locationErrorSubTitle.setText(GlobalData.addressHeader);
            locationErrorLayout.setVisibility(View.VISIBLE);
            selectedAddressBtn.setVisibility(View.GONE);
            locationInfoLayout.setVisibility(View.GONE);
        }*/
        return view;
    }

    private void initializeAddressDetails() {
//        if (GlobalData.selectedAddress != null && GlobalData.selectedAddress.getLandmark() != null) {
        if (GlobalData.selectedAddress != null) {
            if (GlobalData.addressList.getAddresses().size() == 1)
                addAddressTxt.setText(getString(R.string.add_address));
            else
                addAddressTxt.setText(getString(R.string.change_address));
//            addAddressBtn.setBackgroundResource(R.drawable.button_corner_bg_green);
//            addAddressBtn.setText(getResources().getString(R.string.proceed_to_pay));
            addressHeader.setText(GlobalData.selectedAddress.getType());
            addressDetail.setText(GlobalData.selectedAddress.getMapAddress());
            if (viewCartItemList != null && viewCartItemList.size() != 0)
                addressDeliveryTime.setText(viewCartItemList.get(0).getProduct().getShop().getEstimatedDeliveryTime().toString() + " Mins");
        } else if (GlobalData.addressList != null) {
            addAddressBtn.setBackgroundResource(R.drawable.button_curved);
            addAddressBtn.setText(getResources().getString(R.string.add_address));
            locationErrorSubTitle.setText(GlobalData.addressHeader);
            selectedAddressBtn.setVisibility(View.VISIBLE);
            locationErrorLayout.setVisibility(View.VISIBLE);
            locationInfoLayout.setVisibility(View.GONE);
        } else {
            if (GlobalData.selectedAddress != null)
                locationErrorSubTitle.setText(GlobalData.selectedAddress.getMapAddress());
            else
                locationErrorSubTitle.setText(GlobalData.addressHeader);
            locationErrorLayout.setVisibility(View.VISIBLE);
            selectedAddressBtn.setVisibility(View.GONE);
            locationInfoLayout.setVisibility(View.GONE);
        }
    }

    private void getViewCart() {
        Call<AddCart> call = apiInterface.getViewCart();
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(Call<AddCart> call, Response<AddCart> response) {
                Log.d(TAG, response.toString());
                skeleton.hide();
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                    dataLayout.setVisibility(View.GONE);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    customDialog.dismiss();
                    //get Item Count
                    itemCount = response.body().getProductList().size();
                    GlobalData.notificationCount = response.body().getProductList().size();
                    if (itemCount == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                        dataLayout.setVisibility(View.GONE);
                        GlobalData.addCart = null;
                    } else {
                        addCart = response.body();
                        viewCartItemList.clear();
                        viewCartItemList = addCart.getProductList();
                        viewCartAdapter = new ViewCartAdapter(viewCartItemList, context);
                        orderItemRv.setAdapter(viewCartAdapter);
                        viewCartAdapter.notifyDataSetChanged();
                        errorLayout.setVisibility(View.GONE);
                        dataLayout.setVisibility(View.VISIBLE);
                        for (int i = 0; i < itemCount; i++) {
                            //Get Total item Quantity
                            itemQuantity = itemQuantity + response.body().getProductList().get(i).getQuantity();
                        }
                        GlobalData.notificationCount = itemQuantity;
                        String currency = response.body().getProductList().get(0).getProduct().getPrices().getCurrency();
                        itemTotalAmount.setText(currency + "" + response.body().getTotalPrice());
                        discountAmount.setText("- " + currency + "" + response.body().getShopDiscount());
                        promocode_amount.setText("- " + currency + "" + response.body().getPromocodeAmount());
                        serviceTax.setText(currency + response.body().getTax());
                        payAmount.setText(currency + "" + response.body().getPayable());
                        deliveryCharges.setText(response.body().getProductList().get(0).getProduct().getPrices().getCurrency() + "" + response.body().getDeliveryCharges());
                        //Set Restaurant Details
                        restaurantName.setText(response.body().getProductList().get(0).getProduct().getShop().getName());
                        restaurantDescription.setText(response.body().getProductList().get(0).getProduct().getShop().getDescription());
                        String image_url = response.body().getProductList().get(0).getProduct().getShop().getAvatar();
                        Glide.with(context)
                                .load(image_url)
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.drawable.ic_restaurant_place_holder)
                                        .error(R.drawable.ic_restaurant_place_holder))
                                .into(restaurantImage);
                        if (response.body().getProductList().get(0).getProduct().getShop().getEstimatedDeliveryTime() != null) {
                            mEstimatedDeliveryTime = response.body().getProductList().get(0).getProduct().getShop().getEstimatedDeliveryTime();
                        }

                        if (response.body().getProductList() != null && response.body().getProductList().size() > 0) {
                            List<DeliveryOption> mList = new ArrayList<>();
                            mList = response.body().getProductList().get(0).getProduct().getShop().getDeliveryOptionList();
                            if (mList != null && mList.size() > 0) {
                                for (int i = 0; i < mList.size(); i++) {
                                    if (mList.get(i).getName().equalsIgnoreCase("Takeaway")) {
                                        mIsPickUpAvailable = true;
                                    }
                                    if (mList.get(i).getName().equalsIgnoreCase("Delivery")) {
                                        mIsDeliveryAvailable = true;
                                    }
                                }
                            }
                        }
                        if (mIsPickUpAvailable && mIsDeliveryAvailable) {
                            if (!isActivityResultCalled) {
                                layoutOrderType.setVisibility(View.VISIBLE);
                                pickupBtn.setVisibility(View.VISIBLE);
                                mTxtDeliveryContent.setVisibility(View.VISIBLE);
                                deliveryBtn.setVisibility(View.VISIBLE);
                                layoutDeliveryFees.setVisibility(View.VISIBLE);
                                if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                                    if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                                        deliveryFareCalculation();
                                    }
                                }
                            } else {
                                if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                                    if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                                        deliveryFareCalculation();
                                    }
                                }
                            }
                        } else if (mIsPickUpAvailable) {
                            mIsOnlyPickUpAvailable = true;
                            layoutOrderType.setVisibility(View.VISIBLE);
                            pickupBtn.setVisibility(View.VISIBLE);
                            deliveryBtn.setVisibility(View.GONE);
                            mTxtDeliveryContent.setVisibility(View.VISIBLE);
                            layoutDeliveryFees.setVisibility(View.GONE);
                            deliveryFareCalculation();

                        } else if (mIsDeliveryAvailable) {
                            layoutOrderType.setVisibility(View.GONE);
                            locationErrorLayout.setVisibility(View.VISIBLE);
                            layoutDeliveryFees.setVisibility(View.VISIBLE);
                            initializeAddressDetails();
                            if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                                if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                                    deliveryFareCalculation();
                                }
                            }
                        } else {
                            layoutOrderType.setVisibility(View.GONE);
                            layoutDeliveryFees.setVisibility(View.VISIBLE);
                            if (!isActivityResultCalled) {
                                locationErrorLayout.setVisibility(View.VISIBLE);
                                initializeAddressDetails();
                            }
                            if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                                if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                                    deliveryFareCalculation();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AddCart> call, Throwable t) {
                errorLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getViewCartWithPromocode(String promotion) {
        Call<AddCart> call = apiInterface.getViewCartPromocode(promotion);
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(Call<AddCart> call, Response<AddCart> response) {
                skeleton.hide();
                if (!response.isSuccessful() && response.errorBody() != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                    dataLayout.setVisibility(View.GONE);
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    customDialog.dismiss();
                    //get Item Count
                    itemCount = response.body().getProductList().size();
                    GlobalData.notificationCount = response.body().getProductList().size();
                    if (itemCount == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                        dataLayout.setVisibility(View.GONE);
                        GlobalData.addCart = response.body();
                        GlobalData.addCart = null;
                    } else {
                        addCart = response.body();

                        viewCartItemList.clear();
                        viewCartItemList = addCart.getProductList();
                        viewCartAdapter = new ViewCartAdapter(viewCartItemList, context);
                        orderItemRv.setAdapter(viewCartAdapter);
                        viewCartAdapter.notifyDataSetChanged();

                        errorLayout.setVisibility(View.GONE);
                        dataLayout.setVisibility(View.VISIBLE);
                        GlobalData.notificationCount = itemQuantity;
                        GlobalData.addCartShopId = response.body().getProductList().get(0).getProduct().getShopId();
                        //Set Payment details
                        String currency = response.body().getProductList().get(0).getProduct().getPrices().getCurrency();

                        if (response.body().getPromocodeAmount() > 0) {
                            lnrPromocodeAmount.setVisibility(View.VISIBLE);
                            promoCodeApply.setText("Promocode Applied.");
                            promoCodeApply.setEnabled(false);
                        } else {
                            promoCodeApply.setText("Apply");
                            promoCodeApply.setEnabled(true);
                            lnrPromocodeAmount.setVisibility(View.GONE);
                        }

                        itemTotalAmount.setText(currency + "" + response.body().getTotalPrice());
                        discountAmount.setText("- " + currency + "" + response.body().getShopDiscount());
                        promocode_amount.setText("- " + currency + "" + response.body().getPromocodeAmount());
                        serviceTax.setText(currency + Double.parseDouble(response.body().getTax()));
                        payAmount.setText(currency + "" + response.body().getPayable());
                        deliveryCharges.setText(response.body().getProductList().get(0).getProduct().getPrices().getCurrency()
                                + "" + response.body().getDeliveryCharges());
                        //Set Restaurant Details
                        restaurantName.setText(response.body().getProductList().get(0).getProduct().getShop().getName());
                        restaurantDescription.setText(response.body().getProductList().get(0).getProduct().getShop().getDescription());
                        String image_url = response.body().getProductList().get(0).getProduct().getShop().getAvatar();
                        Glide.with(context)
                                .load(image_url)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_restaurant_place_holder).error(R.drawable.ic_restaurant_place_holder))
                                .into(restaurantImage);

                        if (response.body().getProductList() != null && response.body().getProductList().size() > 0) {
                            List<DeliveryOption> mList = new ArrayList<>();
                            mList = response.body().getProductList().get(0).getProduct().getShop().getDeliveryOptionList();
                            if (mList != null && mList.size() > 0) {
                                for (int i = 0; i < mList.size(); i++) {
                                    if (mList.get(i).getName().equalsIgnoreCase("Takeaway")) {
                                        mIsPickUpAvailable = true;
                                    }
                                    if (mList.get(i).getName().equalsIgnoreCase("Delivery")) {
                                        mIsDeliveryAvailable = true;
                                    }
                                }
                            }
                        }
                        if (mIsPickUpAvailable && mIsDeliveryAvailable) {
                            if (!isActivityResultCalled) {
                                layoutOrderType.setVisibility(View.VISIBLE);
                                pickupBtn.setVisibility(View.VISIBLE);
                                deliveryBtn.setVisibility(View.VISIBLE);
                                mTxtDeliveryContent.setVisibility(View.VISIBLE);
                                layoutDeliveryFees.setVisibility(View.VISIBLE);
                                if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                                    if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                                        deliveryFareCalculation();
                                    }
                                }
                            }
                        } else if (mIsPickUpAvailable) {
                            mIsOnlyPickUpAvailable = true;
                            layoutOrderType.setVisibility(View.VISIBLE);
                            pickupBtn.setVisibility(View.VISIBLE);
                            deliveryBtn.setVisibility(View.GONE);
                            mTxtDeliveryContent.setVisibility(View.VISIBLE);
                            layoutDeliveryFees.setVisibility(View.GONE);
                            deliveryFareCalculation();
                        } else if (mIsDeliveryAvailable) {
                            layoutOrderType.setVisibility(View.GONE);
                            locationErrorLayout.setVisibility(View.VISIBLE);
                            layoutDeliveryFees.setVisibility(View.VISIBLE);
                            initializeAddressDetails();
                            if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                                if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                                    deliveryFareCalculation();
                                }
                            }
                        } else {
                            layoutOrderType.setVisibility(View.GONE);
                            layoutDeliveryFees.setVisibility(View.VISIBLE);
                            if (!isActivityResultCalled) {
                                locationErrorLayout.setVisibility(View.VISIBLE);
                                initializeAddressDetails();
                            }
                            if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                                if (response.body().getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                                    deliveryFareCalculation();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AddCart> call, Throwable t) {
                errorLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        priceAmount = 0;
        discount = 0;
        itemCount = 0;
        itemQuantity = 0;
        if (GlobalData.profileModel != null) {
            String money = GlobalData.profileModel.getWalletBalance();
            dataLayout.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            skeleton.show();
            errorLayoutDescription.setText(getResources().getString(R.string.cart_error_description));
            if (connectionHelper.isConnectingToInternet() && GlobalData.addCart == null) {
                if (promo_code.equalsIgnoreCase("")) {
                    getViewCart();
                } else {
                    getViewCartWithPromocode(promo_code);
                }
            } else if (GlobalData.addCart != null) {
               /* if (GlobalData.addCart.getProductList().size() > 0) {

                    String currency = GlobalData.addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
                    itemTotalAmount.setText(currency + " " + *//*String.format("%.2f", *//*GlobalData.addCart.getTotalPrice());
                    deliveryCharges.setText(currency + " " + GlobalData.addCart.getDeliveryCharges().toString());
                    discountAmount.setText("- " + currency + "" + GlobalData.addCart.getShopDiscount());
                    serviceTax.setText(currency + " " + GlobalData.addCart.getTax() + "");
                    payAmount.setText(currency + " " + *//*String.format("%.2f", *//*GlobalData.addCart.getPayable());
                    dataLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    skeleton.hide();
                }*/
                if (promo_code.equalsIgnoreCase("")) {
                    getViewCart();
                } else {
                    getViewCartWithPromocode(promo_code);
                }

            } else {
                Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
            }

            //TODO IF PROJECT HAVE PAYMENT GATEWAY ENABLE BELOW CODE OTHERWISE HIDDEN WALLET LAYOUT
            walletLayout.setVisibility(View.INVISIBLE);

            if (GlobalData.profileModel.getWalletBalance() != null) {
                float fd = Float.parseFloat(GlobalData.profileModel.getWalletBalance());
                if (fd > 0) {
                    amountTxt.setText(GlobalData.currencySymbol + "" + money);
                    walletLayout.setVisibility(View.VISIBLE);
                } else {
                    walletLayout.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            dataLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutDescription.setText(getResources().getString(R.string.please_login_and_order_dishes));
        }
        if (bottomSheetDialogFragment != null)
            bottomSheetDialogFragment.dismiss();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toolbar != null) {
            toolbar.removeView(toolbarLayout);
        }
    }


    public void FeedbackDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feedback);
        EditText commentEdit = dialog.findViewById(R.id.comment);

        Button submitBtn = dialog.findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("CartFragment");
        toolbar = getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (getArguments() != null) {
                if (getArguments().getBoolean("show_toolbar")) {
                    toolbar.setVisibility(View.VISIBLE);
                }
            } else {
                toolbar.setVisibility(View.GONE);
                dummyImageView.setVisibility(View.VISIBLE);
            }
        } else {
            dummyImageView.setVisibility(View.GONE);
        }
    }


    @OnClick({R.id.add_address_txt, R.id.add_address_btn, R.id.selected_address_btn, R.id.proceed_to_pay_btn,
            R.id.promo_code_apply, R.id.pickup_btn, R.id.delivery_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_address_txt:
                /**  If address is empty */
                if (addAddressTxt.getText().toString().equalsIgnoreCase(getResources().getString(R.string.change_address))) {
                    startActivityForResult(new Intent(getActivity(), SetDeliveryLocationActivity.class).putExtra("get_address", true), ADDRESS_SELECTION);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
                /**  If address is filled */
                else if (addAddressTxt.getText().toString().equalsIgnoreCase(getResources().getString(R.string.add_address))) {
                    startActivityForResult(new Intent(getActivity(), SaveDeliveryLocationActivity.class).putExtra("get_address", true), ADDRESS_SELECTION);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
                break;
            case R.id.add_address_btn:
                /**  If address is empty */
                startActivityForResult(new Intent(getActivity(), SaveDeliveryLocationActivity.class).putExtra("get_address", true), ADDRESS_SELECTION);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                break;
            case R.id.selected_address_btn:
                /**  If address is filled */
                startActivityForResult(new Intent(getActivity(), SetDeliveryLocationActivity.class).putExtra("get_address", true), ADDRESS_SELECTION);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

                break;


            case R.id.proceed_to_pay_btn:
                mIsPickUpSelected = false;
                mIsDeliverySelected = true;
                Bundle bundle = new Bundle();
                bundle.putString("deliveryType", "DELIVERY");
                bundle.putInt("estDeliveryTime", 0);
                bottomSheetTypeDialogFragment.setArguments(bundle);
                bottomSheetTypeDialogFragment.setCancelable(false);
                bottomSheetTypeDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetTypeDialogFragment.getTag());

                if (connectionHelper.isConnectingToInternet()) {
//                    checkOut(GlobalData.getInstance().selectedAddress.getId());
                    checkoutMap = new HashMap<>();
                    checkoutMap.put("user_address_id", "" + GlobalData.selectedAddress.getId());
                    checkoutMap.put("note", "" + customNotes.getText());

                    if (!promo_code.equalsIgnoreCase("")) {
                        checkoutMap.put("promocode_id", promo_code);
                    }

                    if (useWalletChkBox.isChecked())
                        checkoutMap.put("wallet", "1");
                    else
                        checkoutMap.put("wallet", "0");
                } else {
                    Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
                }
                break;

            case R.id.promo_code_apply:
                startActivityForResult(new Intent(activity, PromotionActivity.class).putExtra("tag", "CartFragment"), 201);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
//                activity.finish();
                break;
            case R.id.pickup_btn:
                /*mIsPickUpSelected = true;
                mIsDeliverySelected = false;
                locationErrorLayout.setVisibility(View.GONE);
                locationInfoLayout.setVisibility(View.GONE);
                layoutOrderType.setVisibility(View.GONE);
                layoutOrderTime.setVisibility(View.VISIBLE);*/
                deliveryFareCalculation();
                Bundle mBundle = new Bundle();
                mBundle.putString("deliveryType", "PICKUP");
                mBundle.putInt("estDeliveryTime", mEstimatedDeliveryTime);

                if (connectionHelper.isConnectingToInternet()) {
//                    checkOut(GlobalData.getInstance().selectedAddress.getId());
                    checkoutMap = new HashMap<>();
                    checkoutMap.put("note", "" + customNotes.getText());

                    if (!promo_code.equalsIgnoreCase("")) {
                        checkoutMap.put("promocode_id", promo_code);
                    }

                    if (useWalletChkBox.isChecked())
                        checkoutMap.put("wallet", "1");
                    else
                        checkoutMap.put("wallet", "0");
                } else {
                    Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
                }

                bottomSheetTypeDialogFragment.setArguments(mBundle);
                bottomSheetTypeDialogFragment.setCancelable(false);
                bottomSheetTypeDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetTypeDialogFragment.getTag());
                break;
            case R.id.delivery_btn:
                mIsPickUpSelected = false;
                mIsDeliverySelected = true;
                locationErrorLayout.setVisibility(View.VISIBLE);
                locationInfoLayout.setVisibility(View.GONE);
                layoutOrderType.setVisibility(View.GONE);
                if (GlobalData.selectedAddress != null) {
                    locationErrorLayout.setVisibility(View.GONE);
                    locationInfoLayout.setVisibility(View.VISIBLE);
                    layoutOrderType.setVisibility(View.GONE);
                }
                initializeAddressDetails();
                if (addCart != null)
                    if (addCart.getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                        if (addCart.getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                            deliveryFareCalculation();
                        }
                    }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.print("CartFragment");
        if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_OK) {
            System.out.print("CartFragment : Success");
            isActivityResultCalled = true;
            layoutOrderType.setVisibility(View.GONE);
            if (GlobalData.selectedAddress != null) {
                locationErrorLayout.setVisibility(View.GONE);
                locationInfoLayout.setVisibility(View.VISIBLE);
                layoutOrderType.setVisibility(View.GONE);
                //Intialize address Value
                if (GlobalData.selectedAddress != null) {
                    if (GlobalData.addressList.getAddresses().size() == 1)
                        addAddressTxt.setText(getString(R.string.add_address));
                    else
                        addAddressTxt.setText(getString(R.string.change_address));
                }
                addressHeader.setText(GlobalData.selectedAddress.getType());
                addressDetail.setText(GlobalData.selectedAddress.getMapAddress());
                addressDeliveryTime.setText(viewCartItemList.get(0).getProduct().getShop().getEstimatedDeliveryTime().toString() + " Mins");
            } else {
                locationErrorLayout.setVisibility(View.VISIBLE);
                locationInfoLayout.setVisibility(View.GONE);
            }
        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("CartFragment : Failure");
            isActivityResultCalled = true;

        } else if (requestCode == PROMOCODE_APPLY) {
            if (data != null) {
                promoCodeApply.setText(R.string.promo_applied);
                promoCodeApply.setEnabled(false);
                promo_code = data.getExtras().getString("promotion");
                getViewCartWithPromocode(promo_code);
            }
        }
    }

    @OnClick(R.id.wallet_layout)
    public void onViewClicked() {
    }

    @OnClick(R.id.custom_notes)
    public void onAddCustomNotesClicked() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final FrameLayout frameView = new FrameLayout(getActivity());
            builder.setView(frameView);

            final AlertDialog alertDialog = builder.create();
            LayoutInflater inflater = alertDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_note_popup, frameView);

            final EditText notes = dialogView.findViewById(R.id.notes);
            notes.setText(customNotes.getText());
            Button submit = dialogView.findViewById(R.id.custom_note_submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customNotes.setText(notes.getText());
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelClick() {
        if (!mIsOnlyPickUpAvailable) {
            layoutDeliveryFees.setVisibility(View.VISIBLE);

            if (addCart != null) {
                if (addCart.getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                    if (addCart.getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                        deliveryFareCalculation();
                    } else {
                        String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
                        itemTotalAmount.setText(currency + "" + addCart.getTotalPrice());
                        discountAmount.setText("- " + currency + "" + addCart.getShopDiscount());
                        promocode_amount.setText("- " + currency + "" + addCart.getPromocodeAmount());
                        serviceTax.setText(currency + Double.parseDouble(addCart.getTax()));
                        payAmount.setText(currency + "" + addCart.getPayable());
                        deliveryCharges.setText(addCart.getProductList().get(0).getProduct().getPrices().getCurrency()
                                + "" + addCart.getDeliveryCharges());
                    }
                } else {
                    String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
                    itemTotalAmount.setText(currency + "" + addCart.getTotalPrice());
                    discountAmount.setText("- " + currency + "" + addCart.getShopDiscount());
                    promocode_amount.setText("- " + currency + "" + addCart.getPromocodeAmount());
                    serviceTax.setText(currency + Double.parseDouble(addCart.getTax()));
                    payAmount.setText(currency + "" + addCart.getPayable());
                    deliveryCharges.setText(addCart.getProductList().get(0).getProduct().getPrices().getCurrency()
                            + "" + addCart.getDeliveryCharges());
                }
            }
        } else {
            if (addCart != null)
                if (addCart.getProductList().get(0).getProduct().getShop().getFreeDelivery() != null) {
                    if (addCart.getProductList().get(0).getProduct().getShop().getFreeDelivery() == 1) {
                        deliveryFareCalculation();
                    }
                }
        }
    }

    private void deliveryFareCalculation() {
        if (addCart != null) {
            layoutDeliveryFees.setVisibility(View.GONE);
            String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
            itemTotalAmount.setText(currency + "" + addCart.getTotalPrice());
            discountAmount.setText("- " + currency + "" + addCart.getShopDiscount());
            promocode_amount.setText("- " + currency + "" + addCart.getPromocodeAmount());
            serviceTax.setText(currency + Double.parseDouble(addCart.getTax()));
            Double mPayAmount = addCart.getPayable() - addCart.getDeliveryCharges();
            payAmount.setText(currency + "" + new DecimalFormat("##.##").format(mPayAmount));
            deliveryCharges.setText(addCart.getProductList().get(0).getProduct().getPrices().getCurrency()
                    + "" + addCart.getDeliveryCharges());
        }

    }

}