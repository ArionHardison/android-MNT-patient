package com.dietmanager.app.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.HotelViewActivity;
import com.dietmanager.app.activities.ProductDetailActivity;
import com.dietmanager.app.adapter.HotelCatagoeryAdapter;
import com.dietmanager.app.adapter.ProductsAdapter;
import com.dietmanager.app.adapter.ViewCartAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.Addon;
import com.dietmanager.app.models.Cart;
import com.dietmanager.app.models.CartAddon;
import com.dietmanager.app.models.Product;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.fragments.CartFragment.locationInfoLayout;

/**
 * Created by santhosh@appoets.com on 13-11-2017.
 */

public class CartChoiceModeFragment extends BottomSheetDialogFragment {

    Context context;
    Activity activity;
    Product product;
    @BindView(R.id.add_ons_items_txt)
    TextView addOnsItemsTxt;
    @BindView(R.id.add_ons_qty)
    TextView addOnsQty;
    @BindView(R.id.i_will_choose_btn)
    Button iWillChooseBtn;
    @BindView(R.id.repeat_btn)
    Button repeatBtn;
    Unbinder unbinder;
    @BindView(R.id.food_type)
    ImageView foodType;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.product_price)
    TextView productPrice;
    String addOnsValue = "";
    List<CartAddon> cartAddonList;
    public static Cart lastCart;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    CustomDialog customDialog;
    HashMap<String, String> repeatCartMap;
    public static boolean isViewcart = false;
    public static boolean isSearch = false;
    private ViewCartAdapter viewCartAdapter;

    public void setViewCartAdapter(ViewCartAdapter viewCartAdapter) {
        this.viewCartAdapter = viewCartAdapter;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.cart_choice_mode_fragment, null);
        dialog.setContentView(contentView);
        ButterKnife.bind(this, contentView);
        context = getContext();
        activity = getActivity();
        customDialog = new CustomDialog(context);
        if (GlobalData.isSelectedProduct != null) {
            product = GlobalData.isSelectedProduct;
            productName.setText(product.getName());
            productPrice.setText(product.getPrices().getCurrency() + " " + product.getPrices().getOrignalPrice());
            cartAddonList = new ArrayList<>();

            if (isViewcart) {
                cartAddonList = lastCart.getCartAddons();
            } else if (product.getCart() != null) {
                cartAddonList = product.getCart().get(product.getCart().size() - 1).getCartAddons();
                lastCart = product.getCart().get(product.getCart().size() - 1);
            } else if (GlobalData.addCart != null) {
                for (int i = 0; i < GlobalData.addCart.getProductList().size(); i++) {
                    if (GlobalData.addCart.getProductList().get(i).getProductId().equals(product.getId())) {
                        lastCart = GlobalData.addCart.getProductList().get(i);
                        cartAddonList = lastCart.getCartAddons();
                    }
                }
            }
//            if (GlobalData.addCart != null) {
//                if (isViewcart) {
//                    cartAddonList = lastCart.getCartAddons();
//                } else {
//                    for (int i = 0; i < GlobalData.addCart.getProductList().size(); i++) {
//                        if (GlobalData.addCart.getProductList().get(i).getProductId().equals(product.getId())) {
//                            lastCart = GlobalData.addCart.getProductList().get(i);
//                            cartAddonList = lastCart.getCartAddons();
//                        }
//                    }
//                }
//            } else if (product.getCart() != null) {
//                cartAddonList = product.getCart().get(product.getCart().size() - 1).getCartAddons();
//                lastCart = product.getCart().get(product.getCart().size() - 1);
//            }
//            addOnsQty.setText("" + cartAddonList.size() + " Add on");
            addOnsQty.setText(context.getResources().getQuantityString(R.plurals.add_ons, cartAddonList.size(), cartAddonList.size()));
            for (int i = 0; i < cartAddonList.size(); i++) {
                Addon addonProduct = cartAddonList.get(i).getAddonProduct();
                String name = (addonProduct != null && addonProduct.getAddon() != null) ? addonProduct.getAddon().getName() : "";
                if (i == 0)
                    addOnsItemsTxt.setText(name);
                else
                    addOnsItemsTxt.append(", " + name);
            }


        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @OnClick({R.id.i_will_choose_btn, R.id.repeat_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.i_will_choose_btn:
                if (locationInfoLayout != null) {
                    locationInfoLayout.setVisibility(View.GONE);
                }
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("isNewSelection", true);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                break;
            case R.id.repeat_btn:
                repeatCartMap = new HashMap<>();
                repeatCartMap.put("product_id", product.getId().toString());
                repeatCartMap.put("quantity", String.valueOf(lastCart.getQuantity() + 1));
                repeatCartMap.put("cart_id", String.valueOf(lastCart.getId()));
                repeatCartMap.put("adddon", "repeat");
                for (int i = 0; i < cartAddonList.size(); i++) {
                    CartAddon cartAddon = cartAddonList.get(i);
                    String id = String.valueOf(cartAddon.getAddonProduct().getId());
                    repeatCartMap.put("product_addons[" + "" + i + "]", id);
                    repeatCartMap.put("addons_qty[" + "" + id + "]", String.valueOf(cartAddon.getQuantity()));
                }
                Log.e("Repeat_cart", repeatCartMap.toString());
                if (isViewcart) {
                    viewCartAdapter.addCart(repeatCartMap, Objects.requireNonNull(getActivity()));
                } else if (isSearch) {
                    ProductsAdapter.addCart(repeatCartMap);
                    if (GlobalData.searchProductList != null) {
                        for (int i = 0; i < GlobalData.searchProductList.size(); i++) {
                            Product oldProduct = GlobalData.searchProductList.get(i);
                            if (oldProduct.getId().equals(product.getId())) {
                                GlobalData.searchProductList.get(i).getCart().get(GlobalData.searchProductList.get(i).getCart().size() - 1).setQuantity(lastCart.getQuantity() + 1);
                                ProductSearchFragment.productsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    HotelCatagoeryAdapter.addCart(repeatCartMap);
                    if (HotelViewActivity.categoryList != null) {
                        for (int i = 0; i < HotelViewActivity.categoryList.size(); i++) {
                            for (int j = 0; j < HotelViewActivity.categoryList.get(i).getProducts().size(); j++) {
                                Product oldProduct = HotelViewActivity.categoryList.get(i).getProducts().get(j);
                                if (oldProduct.getId().equals(product.getId())) {
                                    HotelViewActivity.categoryList.get(i).getProducts().get(j).getCart().get(HotelViewActivity.categoryList.get(i).getProducts().get(j).getCart().size() - 1).setQuantity(lastCart.getQuantity() + 1);
                                    HotelViewActivity.catagoeryAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
                dismiss();
                break;
        }
    }

    private void addItem(HashMap<String, String> map) {
        customDialog.show();
        Call<AddCart> call = apiInterface.postAddCart(map);
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(@NonNull Call<AddCart> call, @NonNull Response<AddCart> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    GlobalData.addCart = response.body();
                    dismiss();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddCart> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

    }
}
