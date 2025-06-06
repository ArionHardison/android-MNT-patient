package com.dietmanager.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.AddOnsAdapter;
import com.dietmanager.app.adapter.SliderPagerAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.Addon;
import com.dietmanager.app.models.ClearCart;
import com.dietmanager.app.models.Image;
import com.dietmanager.app.models.Product;
import com.dietmanager.app.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.OyolaApplication.currency;
import static com.dietmanager.app.adapter.AddOnsAdapter.list;
import static com.dietmanager.app.helper.GlobalData.selectedShop;


public class ProductDetailActivity extends AppCompatActivity {

    @BindView(R.id.product_slider)
    ViewPager productSlider;
    @BindView(R.id.product_slider_dots)
    LinearLayout productSliderDots;
    @BindView(R.id.custom_notes)
    EditText custom_notes;

    SliderPagerAdapter sliderPagerAdapter;
    List<Image> slider_image_list;
    int page_position = 0;
    @BindView(R.id.add_ons_rv)
    RecyclerView addOnsRv;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.food_type_image)
    ImageView foodImageType;
    @BindView(R.id.product_description)
    TextView productDescription;


    @BindView(R.id.realPrice)
    TextView realPrice;
    @BindView(R.id.showPrice)
    TextView showPrice;
    @BindView(R.id.txt_ingredients)
    TextView txtIngredients;
    @BindView(R.id.txt_header_ingredients)
    TextView txtHeaderIngredients;

    Product product;
    List<Addon> addonList;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static TextView addOnsTxt;
    int cartId = 0;
    int quantity = 0;

    private static final String TAG = "ProductDetailActivity";

    CustomDialog customDialog;

    public static TextView itemText;
    public static TextView viewCart;
    public static RelativeLayout addItemLayout;
    private boolean isNewSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        isNewSelection = getIntent().getBooleanExtra("isNewSelection", false);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform whatever you want on back arrow click
                onBackPressed();
            }
        });
        try {
            context = ProductDetailActivity.this;
            customDialog = new CustomDialog(context);

            //Intialize
            addOnsTxt = findViewById(R.id.add_ons_txt);
            itemText = findViewById(R.id.item_text);
            viewCart = findViewById(R.id.view_cart);
            addItemLayout = findViewById(R.id.view_cart_layout);
            product = GlobalData.isSelectedProduct;
            if (GlobalData.addCart != null) {
                if (GlobalData.addCart.getProductList().size() != 0) {
                    for (int i = 0; i < GlobalData.addCart.getProductList().size(); i++) {
                        if (GlobalData.addCart.getProductList().get(i).getProductId().equals(product.getId())) {
                            cartId = GlobalData.addCart.getProductList().get(i).getId();
                            quantity = GlobalData.addCart.getProductList().get(i).getQuantity();
                        }
                    }
                }
            }

            if (product.getName() != null) {
                Spannable wordOne = new SpannableString(product.getName());
                wordOne.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorTextBlack)), 0, wordOne.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                productName.setText(wordOne);
            }
            if (!product.getFoodType().equalsIgnoreCase("veg")) {
                foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nonveg));
                foodImageType.setVisibility(View.INVISIBLE);
            } else {
                foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg));
                foodImageType.setVisibility(View.VISIBLE);
            }
            Spannable wordTwo;
            if (product.getCalories() != null) {
                wordTwo = new SpannableString(" " + product.getCalories() + " " + getString(R.string.calories_symbol));
            } else {
                wordTwo = new SpannableString(" 0 " + getString(R.string.calories_symbol));
            }
            wordTwo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.checkbox_green)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            productName.append(wordTwo);

            if (product.getIngredients() != null) {
                txtIngredients.setText(product.getIngredients());
            } else {
                txtHeaderIngredients.setVisibility(View.GONE);
                txtIngredients.setVisibility(View.GONE);
            }
            if (product.getPrices() != null && product.getPrices().getCurrency() != null) {
                if (product.getPrices().getDiscount() > 0) {
                    Spannable spannable = new SpannableString(product.getPrices().getCurrency() + product.getPrices().getPrice());
                    spannable.setSpan(new StrikethroughSpan(), 1, String.valueOf(product.getPrices().getPrice()).length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    realPrice.setText(spannable);
                }
                showPrice.setText(String.format("%s %.2f", product.getPrices().getCurrency(), product.getPrices().getOrignalPrice()));
            }


            currency = product.getPrices().getCurrency();
            itemText.setText("1 " + getResources().getString(R.string.item_count) + " | " + product.getPrices().getCurrency() + product.getPrices().getOrignalPrice());
            productDescription.setText(product.getDescription());
            slider_image_list = new ArrayList<>();
            addonList = new ArrayList<>();
            addonList.addAll(product.getAddons());
            if (addonList.size() == 0)
                addOnsTxt.setVisibility(View.GONE);
            else
                addOnsTxt.setVisibility(View.VISIBLE);

            //Add ons Adapter
            addOnsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//        addOnsRv.setItemAnimator(new DefaultItemAnimator());
            addOnsRv.setHasFixedSize(false);
            addOnsRv.setNestedScrollingEnabled(false);

            AddOnsAdapter addOnsAdapter = new AddOnsAdapter(addonList, context);
            addOnsRv.setAdapter(addOnsAdapter);

            slider_image_list.addAll(product.getImages());
            sliderPagerAdapter = new SliderPagerAdapter(this, slider_image_list, true, false);
            productSlider.setAdapter(sliderPagerAdapter);
            addBottomDots(0);

            addItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GlobalData.profileModel == null) {
                        startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        overridePendingTransition(R.anim.slide_in_left, R.anim.anim_nothing);
                        finish();
                        Toast.makeText(context, context.getResources().getString(R.string.please_login_and_order_dishes), Toast.LENGTH_SHORT).show();
                    } else {
                        final HashMap<String, String> map = new HashMap<>();
                        map.put("product_id", product.getId().toString());
                        map.put("note", custom_notes.getText().toString());
                        if (product.getCart() != null && product.getCart().size() == 1) {
//                            map.put("quantity", String.valueOf(product.getCart().get(0).getQuantity() + 1));
//                            map.put("cart_id", String.valueOf(product.getCart().get(0).getId()));

                            if (!isNewSelection) {
                                map.put("quantity", String.valueOf(product.getCart().get(0).getQuantity() + 1));
                                map.put("cart_id", String.valueOf(product.getCart().get(0).getId()));
                            } else {
                                map.put("quantity", String.valueOf(product.getCart().get(0).getQuantity()));
                            }
                            if (!list.isEmpty()) {
                                for (int i = 0; i < list.size(); i++) {
                                    Addon addon = list.get(i);
                                    if (addon.getAddon().getChecked()) {
                                        String id = String.valueOf(addon.getId());
                                        map.put("product_addons[" + "" + i + "]", id);
                                        map.put("addons_qty[" + "" + id + "]", addon.getQuantity().toString());
                                    }
                                }
                            }

                        } else if (product.getAddons().isEmpty() && cartId != 0) {
                            map.put("quantity", String.valueOf(quantity + 1));
                            map.put("cart_id", String.valueOf(cartId));
                        } else {
                            map.put("quantity", "1");
                            map.put("cart_id", "0");
                            if (!list.isEmpty()) {
                                for (int i = 0; i < list.size(); i++) {
                                    Addon addon = list.get(i);
                                    if (addon.getAddon().getChecked()) {
                                        String id = String.valueOf(addon.getId());
                                        map.put("product_addons[" + "" + i + "]", id);
                                        map.put("addons_qty[" + "" + id + "]", addon.getQuantity().toString());
                                    }
                                }
                            }
                        }
                        Log.e("AddCart_add", map.toString());

                        if (!Utils.isShopChanged(product.getShopId())) {
                            addItem(map);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getResources().getString(R.string.replace_cart_item))
                                    .setMessage(context.getResources().getString(R.string.do_you_want_to_discart_the_selection_and_add_dishes_from_the_restaurant))
                                    .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                            clearCart();
                                            addItem(map);

                                        }
                                    })
                                    .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                            dialog.dismiss();

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
                            nbutton.setTypeface(nbutton.getTypeface(), Typeface.BOLD);
                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
                            pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);
                        }
                    }
                }
            });

            productSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    addBottomDots(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void clearCart() {

        Call<ClearCart> call = apiInterface.clearCart();
        call.enqueue(new Callback<ClearCart>() {
            @Override
            public void onResponse(Call<ClearCart> call, Response<ClearCart> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    selectedShop = HotelViewActivity.shops;
                    GlobalData.addCart.getProductList().clear();
                    GlobalData.notificationCount = 0;

                }

            }

            @Override
            public void onFailure(Call<ClearCart> call, Throwable t) {
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addItem(HashMap<String, String> map) {
//        if (commonAccess.equalsIgnoreCase("")) {
            customDialog.show();
            Call<AddCart> call = apiInterface.postAddCart(map);
            call.enqueue(new Callback<AddCart>() {
                @Override
                public void onResponse(@NonNull Call<AddCart> call, @NonNull Response<AddCart> response) {
                    customDialog.dismiss();
                    if (response.isSuccessful()) {
                        GlobalData.addCart = response.body();
                        finish();
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AddCart> call, @NonNull Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "ProductDetail : Something went wrong", Toast.LENGTH_SHORT).show();
                    customDialog.dismiss();

                }
            });
//        } else {
//            commonAccess = "";
//            ViewCartAdapter.addCart(map, this);
             /* finish();*/
//        }

    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[slider_image_list.size()];

        productSliderDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#000000"));
            productSliderDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
        finish();
    }
}
