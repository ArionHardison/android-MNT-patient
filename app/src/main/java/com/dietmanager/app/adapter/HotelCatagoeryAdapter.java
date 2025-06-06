package com.dietmanager.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.HotelViewActivity;
import com.dietmanager.app.activities.LoginActivity;
import com.dietmanager.app.activities.ProductDetailActivity;
import com.dietmanager.app.activities.ViewCartActivity;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.fragments.CartChoiceModeFragment;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.Addon;
import com.dietmanager.app.models.Cart;
import com.dietmanager.app.models.CartAddon;
import com.dietmanager.app.models.Category;
import com.dietmanager.app.models.ClearCart;
import com.dietmanager.app.models.Prices;
import com.dietmanager.app.models.Product;
import com.dietmanager.app.models.ShopDetail;
import com.dietmanager.app.utils.JavaUtils;
import com.dietmanager.app.utils.Utils;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dietmanager.app.helper.GlobalData.selectedShop;

/**
 * Created by Tamil on 28-08-2017.
 */

public class HotelCatagoeryAdapter extends SectionedRecyclerViewAdapter<HotelCatagoeryAdapter.ViewHolder> {

    private List<Category> mList;
    private List<Category> mFilteredList;
    private List<Category> mGlobalList;
    private LayoutInflater inflater;
    public static Context context;
    Activity activity;
    int lastPosition = -1;
    public static double priceAmount = 0;
    public static int itemQuantity = 0;
    public static Product product;
    List<Product> productList;
    public static ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static AddCart addCart;
    public static Animation slide_down, slide_up;
    public static CartChoiceModeFragment bottomSheetDialogFragment;
    public static AnimatedVectorDrawableCompat avdProgress;
    public static Dialog dialog;
    public static Runnable action;
    public static boolean dataResponse = false;

    //Animation number
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();

    public HotelCatagoeryAdapter(Context context, Activity activity, List<Category> list) {
        HotelCatagoeryAdapter.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mGlobalList = list;
        this.mList = list;
        this.activity = activity;
        if (GlobalData.addCart != null && GlobalData.addCart.getProductList().size() != 0) {
            addCart = GlobalData.addCart;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = inflater.inflate(R.layout.category_header, parent, false);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
                v = inflater.inflate(R.layout.accompainment_list_item, parent, false);
                return new ViewHolder(v, false);
            default:
                v = inflater.inflate(R.layout.accompainment_list_item, parent, false);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return mList.size();
    }


    @Override
    public int getItemCount(int section) {
        return mList.get(section).getProducts().size();
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, final int section) {

        if (mList.get(section).getName().equalsIgnoreCase(context.getResources().getString(R.string.featured_items))) {
            holder.featureProductsTitle.setVisibility(View.VISIBLE);
            holder.categoryHeaderLayout.setVisibility(View.GONE);
        } else {
            holder.featureProductsTitle.setVisibility(View.GONE);
            holder.categoryHeaderLayout.setVisibility(View.VISIBLE);
            holder.headerTxt.setText(mList.get(section).getName());
        }

        holder.headerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(mList.get(section).getName());
            }
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {
        Category category = mList.get(section);
        product = mList.get(section).getProducts().get(relativePosition);
        productList = mList.get(section).getProducts();
        holder.cardTextValueTicker.setCharacterList(NUMBER_LIST);
        if (product.getName() != null) {
            Spannable wordOne = new SpannableString(product.getName());
            wordOne.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorTextBlack)), 0, wordOne.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.dishNameTxt.setText(wordOne);
        }

        Spannable wordTwo;
        if (product.getCalories() != null) {
            wordTwo = new SpannableString(" " + product.getCalories() + " " + context.getString(R.string.calories_symbol));
        } else {
            wordTwo = new SpannableString(" 0 " + context.getString(R.string.calories_symbol));
        }
        wordTwo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.checkbox_green)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.dishNameTxt.append(wordTwo);

//        holder.dishNameTxt.setText(product.getName());

        holder.cardTextValueTicker.setVisibility(View.GONE);
        holder.cardTextValue.setVisibility(View.VISIBLE);
        if (category.getName().equalsIgnoreCase(context.getResources().getString(R.string.featured_items))) {
            if (product.getFeaturedImages() != null && product.getFeaturedImages().size() > 0) {
                holder.featuredImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(product.getFeaturedImages().get(0).getUrl())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_banner)
                                .error(R.drawable.ic_banner))
                        .into(holder.featuredImage);

            } else {
                holder.featuredImage.setVisibility(View.GONE);

            }
        } else holder.featuredImage.setVisibility(View.GONE);
        //Check if product is already added
        if (product.getCart() != null && product.getCart().size() != 0) {
            selectedShop = HotelViewActivity.shops;
            holder.cardAddTextLayout.setVisibility(View.GONE);
            holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
            Integer quantity = 0;
            for (Cart cart : product.getCart()) quantity += cart.getQuantity();
            if (!holder.cardTextValue.getText().toString().equalsIgnoreCase(String.valueOf(quantity))) {
                holder.cardTextValueTicker.setText(String.valueOf(quantity));
                holder.cardTextValue.setText(String.valueOf(quantity));
            }
        } else {
            holder.cardAddTextLayout.setVisibility(View.VISIBLE);
            holder.cardAddDetailLayout.setVisibility(View.GONE);
            holder.cardTextValueTicker.setText(String.valueOf(1));
            holder.cardTextValue.setText(String.valueOf(1));
        }

        //Check if add-ons is available
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = mList.get(section).getProducts().get(relativePosition);
                if (product.getOut_of_stock() != null && product.getOut_of_stock().equalsIgnoreCase("NO")) {
                    GlobalData.isSelectedProduct = mList.get(section).getProducts().get(relativePosition);
                    context.startActivity(new Intent(context, ProductDetailActivity.class));
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
            }
        });

        if (product.getPrices() != null && product.getPrices().getCurrency() != null) {
            if (product.getPrices().getDiscount() > 0) {
                Spannable spannable = new SpannableString(product.getPrices().getCurrency() + product.getPrices().getPrice());
                spannable.setSpan(new StrikethroughSpan(), 1, String.valueOf(product.getPrices().getPrice()).length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.realPrice.setText(spannable);
            }
            holder.priceTxt.setText("" + product.getPrices().getCurrency() + product.getPrices().getOrignalPrice());
        }

        if (!product.getFoodType().equalsIgnoreCase("veg")) {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nonveg));
            holder.foodImageType.setVisibility(View.GONE);
        } else {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg));
            holder.foodImageType.setVisibility(View.VISIBLE);
        }

        if (product.getOut_of_stock() != null)
            if (product.getOut_of_stock().equalsIgnoreCase("YES")) {
                holder.cardInfoLayout.setVisibility(View.VISIBLE);
                holder.cardAddOutOfStock.setVisibility(View.VISIBLE);
                holder.cardAddTextLayout.setVisibility(View.GONE);
                holder.cardAddDetailLayout.setVisibility(View.GONE);
                holder.customizableTxt.setVisibility(View.GONE);
                holder.addOnsIconImg.setVisibility(View.GONE);
            } else {
                holder.cardAddOutOfStock.setVisibility(View.GONE);
                holder.cardInfoLayout.setVisibility(View.GONE);
                if (product.getAddons() != null && product.getAddons().size() != 0) {
                    holder.customizableTxt.setVisibility(View.VISIBLE);
                    holder.addOnsIconImg.setVisibility(View.VISIBLE);
                } else {
                    holder.customizableTxt.setVisibility(View.GONE);
                    holder.addOnsIconImg.setVisibility(View.GONE);
                }
            }

        holder.cardAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = mList.get(section).getProducts().get(relativePosition);
                if (!Utils.isShopChanged(HotelViewActivity.shops.getId())) {
                    /** Intilaize Animation View Image */
                    avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.add_cart_avd_line);
                    holder.animationLineCartAdd.setBackground(avdProgress);
                    avdProgress.start();
                    action = new Runnable() {
                        @Override
                        public void run() {
                            if (!dataResponse) {
                                avdProgress.start();
                                holder.animationLineCartAdd.postDelayed(action, 3000);
                            }

                        }
                    };
                    holder.animationLineCartAdd.postDelayed(action, 3000);
                    /** Press Add Card Add button */
                    if (product.getAddons() != null && !product.getAddons().isEmpty()) {
                        GlobalData.isSelectedProduct = product;
                        bottomSheetDialogFragment = new CartChoiceModeFragment();
                        bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        CartChoiceModeFragment.isViewcart = false;
                        CartChoiceModeFragment.isSearch = false;
                    } else {
                        holder.cardTextValueTicker.setVisibility(View.VISIBLE);
                        holder.cardTextValue.setVisibility(View.GONE);
                        holder.animationLineCartAdd.setVisibility(View.VISIBLE);
                        int cartId = 0;
                        if (addCart.getProductList() != null && addCart.getProductList().size() > 0) {
                            for (int i = 0; i < addCart.getProductList().size(); i++) {
                                if (addCart.getProductList().get(i).getProductId().equals(product.getId())) {
                                    cartId = addCart.getProductList().get(i).getId();
                                }
                            }
                        }
                        int countValue = Integer.parseInt(holder.cardTextValue.getText().toString()) + 1;
                        holder.cardTextValue.setText("" + countValue);
                        holder.cardTextValueTicker.setText("" + countValue);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_id", product.getId().toString());
                        map.put("quantity", holder.cardTextValue.getText().toString());
                        map.put("cart_id", String.valueOf(cartId));
                        Log.e("AddCart_add", map.toString());
                        addCart(map);
                    }
                }
            }
        });
        holder.cardAddDetailLayout.setClickable(false);
        holder.cardAddDetailLayout.setEnabled(false);

        holder.cardMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Intilaize Animation View Image */
                holder.animationLineCartAdd.setVisibility(View.VISIBLE);
                holder.cardTextValueTicker.setVisibility(View.VISIBLE);
                holder.cardTextValue.setVisibility(View.GONE);
                //Intialize
                avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.add_cart_avd_line);
                holder.animationLineCartAdd.setBackground(avdProgress);
                avdProgress.start();
                action = new Runnable() {
                    @Override
                    public void run() {
                        if (!dataResponse) {
                            avdProgress.start();
                            holder.animationLineCartAdd.postDelayed(action, 3000);
                        }

                    }
                };
                holder.animationLineCartAdd.postDelayed(action, 3000);

                /** Press Add Card Minus button */
                product = mList.get(section).getProducts().get(relativePosition);
                int cartId = 0;
                for (int i = 0; i < addCart.getProductList().size(); i++) {
                    if (addCart.getProductList().get(i).getProductId().equals(product.getId())) {
                        cartId = addCart.getProductList().get(i).getId();
                    }
                }
                if (holder.cardTextValue.getText().toString().equalsIgnoreCase("1")) {
                    int countMinusValue = Integer.parseInt(holder.cardTextValue.getText().toString()) - 1;
                    holder.cardTextValue.setText("" + countMinusValue);
                    holder.cardTextValueTicker.setText("" + countMinusValue);
                    holder.cardAddDetailLayout.setVisibility(View.GONE);
                    if (addCart.getProductList().size() == 0 && addCart != null)
                        HotelViewActivity.viewCartLayout.setVisibility(View.GONE);
                    holder.cardAddTextLayout.setVisibility(View.VISIBLE);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("product_id", product.getId().toString());
                    map.put("quantity", "0");
                    map.put("cart_id", String.valueOf(cartId));
                    Log.e("AddCart_Minus", map.toString());
                    addCart(map);
                } else {
                    if (product.getCart().size() == 1) {
                        int countMinusValue = Integer.parseInt(holder.cardTextValue.getText().toString()) - 1;
                        holder.cardTextValue.setText("" + countMinusValue);
                        holder.cardTextValueTicker.setText("" + countMinusValue);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_id", product.getId().toString());
                        map.put("quantity", holder.cardTextValue.getText().toString());
                        map.put("cart_id", String.valueOf(cartId));
                        List<CartAddon> cartAddonList = product.getCart().get(0).getCartAddons();
                        for (int i = 0; i < cartAddonList.size(); i++) {
                            CartAddon cartAddon = cartAddonList.get(i);
                            Addon addonProduct = cartAddon.getAddonProduct();
                            if (addonProduct != null) {
                                String id = String.valueOf(addonProduct.getId());
                                map.put("product_addons[" + "" + i + "]", id);
                                map.put("addons_qty[" + "" + id + "]", cartAddon.getQuantity().toString());
                            }
                        }
                        Log.e("AddCart_Minus", map.toString());
                        addCart(map);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getResources().getString(R.string.remove_item_from_cart))
                                .setMessage(context.getResources().getString(R.string.remove_item_from_cart_description))
                                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        context.startActivity(new Intent(context, ViewCartActivity.class));
                                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);

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

        holder.cardAddTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = mList.get(section).getProducts().get(relativePosition);
                if (GlobalData.profileModel == null) {
                    GlobalData.notificationCount = 0;
                    activity.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.anim_nothing);
                    activity.finish();
                    Toast.makeText(context, context.getResources().getString(R.string.please_login_and_order_dishes), Toast.LENGTH_SHORT).show();
                } else if (!Utils.isShopChanged(HotelViewActivity.shops.getId())) {
                    /** Intilaize Animation View Image */
                    holder.animationLineCartAdd.setVisibility(View.VISIBLE);
                    //Intialize
                    avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.add_cart_avd_line);
                    holder.animationLineCartAdd.setBackground(avdProgress);
                    avdProgress.start();
                    action = new Runnable() {
                        @Override
                        public void run() {
                            if (!dataResponse) {
                                avdProgress.start();
                                holder.animationLineCartAdd.postDelayed(action, 3000);
                            }

                        }
                    };
                    holder.animationLineCartAdd.postDelayed(action, 3000);

                    if (product.getAddons() != null && product.getAddons().size() != 0) {
                        GlobalData.isSelectedProduct = product;
                        context.startActivity(new Intent(context, ProductDetailActivity.class));
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                    } else {
                        holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
                        holder.cardAddTextLayout.setVisibility(View.GONE);
                        holder.cardTextValue.setText("1");
                        holder.cardTextValueTicker.setText("1");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("product_id", product.getId().toString());
                        map.put("quantity", holder.cardTextValue.getText().toString());
                        Log.e("AddCart_Text", map.toString());
                        addCart(map);
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.replace_cart_item))
                            .setMessage(context.getResources().getString(R.string.do_you_want_to_discart_the_selection_and_add_dishes_from_the_restaurant))
                            .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    clearCart();
                                    if (product.getAddons() != null && product.getAddons().size() != 0) {
                                        GlobalData.isSelectedProduct = product;
                                        context.startActivity(new Intent(context, ProductDetailActivity.class));
                                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                                    } else {
                                        selectedShop = HotelViewActivity.shops;
                                        product = mList.get(section).getProducts().get(relativePosition);
                                        holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
                                        holder.cardAddTextLayout.setVisibility(View.GONE);
                                        holder.cardTextValue.setText("1");
                                        holder.cardTextValueTicker.setText("1");
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("product_id", product.getId().toString());
                                        map.put("quantity", holder.cardTextValue.getText().toString());
                                        Log.e("AddCart_Text", map.toString());
                                        addCart(map);
                                    }

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
        });

    }

    private void clearCart() {
        Call<ClearCart> call = apiInterface.clearCart();
        call.enqueue(new Callback<ClearCart>() {
            @Override
            public void onResponse(Call<ClearCart> call, Response<ClearCart> response) {

                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setCategoryList(String mValue) {
        if (mValue.equalsIgnoreCase("All")) {
            mList = mGlobalList;
            notifyDataSetChanged();
        } else {
            for (int i = 0; i < mGlobalList.size(); i++) {
                if (mGlobalList.get(i).getName().equalsIgnoreCase(mValue)) {
                    mFilteredList = new ArrayList<>();
                    mFilteredList.add(mGlobalList.get(i));
                }
            }
            if (mFilteredList != null && mFilteredList.size() > 0) {
                mList = mFilteredList;
                notifyDataSetChanged();
            }
        }
    }

    public static void addCart(HashMap<String, String> map) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.empty_dialog);
        dialog.setCancelable(false);
        dataResponse = false;
        dialog.show();
        Call<AddCart> call = apiInterface.postAddCart(map);
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(Call<AddCart> call, Response<AddCart> response) {
                selectedShop = HotelViewActivity.shops;
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    dialog.dismiss();
                    dataResponse = true;
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    dialog.dismiss();
                    if (selectedShop != null)
                        GlobalData.addCartShopId = selectedShop.getId();
                    addCart = response.body();
                    GlobalData.addCart = response.body();
                    setViewcartBottomLayout(addCart);
                    //get User Profile Data
                    if (GlobalData.profileModel != null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("shop", String.valueOf(HotelViewActivity.shops.getId()));
                        map.put("user_id", String.valueOf(GlobalData.profileModel.getId()));
                        getCategories(map);
                    } else {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("shop", String.valueOf(HotelViewActivity.shops.getId()));
                        getCategories(map);
                    }
                }
            }

            @Override
            public void onFailure(Call<AddCart> call, Throwable t) {
                dialog.dismiss();
                dataResponse = true;
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void getCategories(HashMap<String, String> map) {
        Call<ShopDetail> call = apiInterface.getCategories(map);
        call.enqueue(new Callback<ShopDetail>() {
            @Override
            public void onResponse(@NonNull Call<ShopDetail> call, @NonNull Response<ShopDetail> response) {
                dataResponse = true;
                dialog.dismiss();
                HotelViewActivity.categoryList.clear();
                Category category = new Category();
                category.setName(context.getResources().getString(R.string.featured_items));
                category.setProducts(response.body().getFeaturedProducts());
                HotelViewActivity.categoryList.add(category);
                HotelViewActivity.categoryList.addAll(response.body().getCategories());
                GlobalData.selectedShop.setCategories(HotelViewActivity.categoryList);
                HotelViewActivity.catagoeryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ShopDetail> call, @NonNull Throwable t) {
                Toast.makeText(context, "Some thing went wrong", Toast.LENGTH_SHORT).show();
                dataResponse = true;
                dialog.dismiss();

            }
        });


    }

    private static void setViewcartBottomLayout(AddCart addCart) {
        priceAmount = 0;
        itemQuantity = 0;
        List<Cart> cartList = addCart != null && !JavaUtils.isNullOrEmpty(addCart.getProductList()) ? addCart.getProductList() : null;
        for (int i = 0, size = cartList.size(); i < size; i++) {
            int quantity = cartList.get(i).getQuantity() != null ? cartList.get(i).getQuantity() : 0;
            Product product = cartList.get(i).getProduct();
            Prices prices = product != null ? product.getPrices() : null;
            double price = prices != null ? prices.getPrice() : 0;
            double origionalPrice = prices != null ? prices.getOrignalPrice() : 0;
            List<CartAddon> cartAddonList = cartList.get(i).getCartAddons();

            //Get Total item Quantity
            itemQuantity = itemQuantity + quantity;
            //Get addon price
            if (origionalPrice > 0)
                priceAmount = priceAmount + (quantity * origionalPrice);
            if (!JavaUtils.isNullOrEmpty(cartAddonList)) {
                for (int j = 0, cartAddonSize = cartAddonList.size(); j < cartAddonSize; j++) {
                    int cartAddonQuantity = cartAddonList.get(j).getQuantity() != null ? cartAddonList.get(j).getQuantity() : 0;
                    Addon addon = cartAddonList.get(j).getAddonProduct();
                    double addonPrice = addon != null ? addon.getPrice() : 0;
                    priceAmount = priceAmount + (quantity * (cartAddonQuantity * addonPrice));
                }
            }
        }
        GlobalData.notificationCount = itemQuantity;
        if (addCart.getProductList().isEmpty()) {
            HotelViewActivity.viewCartLayout.setVisibility(View.GONE);
            // Start animation
            HotelViewActivity.viewCartLayout.startAnimation(slide_down);
        } else {
            if (!Objects.equals(HotelViewActivity.shops.getId(), GlobalData.addCart.getProductList().get(0).getProduct().getShopId())) {
                HotelViewActivity.viewCartShopName.setVisibility(View.VISIBLE);
                HotelViewActivity.viewCartShopName.setText("From : " + GlobalData.addCart.getProductList().get(0).getProduct().getShop().getName());
            } else {
                HotelViewActivity.viewCartShopName.setVisibility(View.GONE);
            }
            String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
            String itemMessage = context.getResources().getQuantityString(R.plurals.item, itemQuantity, itemQuantity);
            itemMessage = itemMessage + " | " + currency + priceAmount;
            Log.d("itemMessage", itemMessage);
            HotelViewActivity.itemText.setText(itemMessage);
            if (HotelViewActivity.viewCartLayout.getVisibility() == View.GONE) {
                // Start animation
                HotelViewActivity.viewCartLayout.setVisibility(View.VISIBLE);
                HotelViewActivity.viewCartLayout.startAnimation(slide_up);
            }

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView headerTxt, featureProductsTitle;
        private ImageView dishImg, foodImageType, cardAddBtn, cardMinusBtn, animationLineCartAdd, addOnsIconImg, featuredImage;
        private TextView dishNameTxt, priceTxt, realPrice, cardTextValue, cardAddInfoText, cardAddOutOfStock, customizableTxt;
        TickerView cardTextValueTicker;
        RelativeLayout cardAddDetailLayout, cardAddTextLayout, cardInfoLayout;
        LinearLayout rootLayout, categoryHeaderLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                headerTxt = itemView.findViewById(R.id.category_header);
                featureProductsTitle = itemView.findViewById(R.id.featured_product_title);
                categoryHeaderLayout = itemView.findViewById(R.id.category_header_layout);

            } else {
                dishImg = itemView.findViewById(R.id.dishImg);
                foodImageType = itemView.findViewById(R.id.food_type_image);
                featuredImage = itemView.findViewById(R.id.featured_image);
                addOnsIconImg = itemView.findViewById(R.id.add_ons_icon);
                animationLineCartAdd = itemView.findViewById(R.id.animation_line_cart_add);
                dishNameTxt = itemView.findViewById(R.id.dish_name_text);
                customizableTxt = itemView.findViewById(R.id.customizable_txt);
                priceTxt = itemView.findViewById(R.id.price_text);
                realPrice = itemView.findViewById(R.id.realPrice);

                /*    Add card Button Layout*/
                cardAddDetailLayout = itemView.findViewById(R.id.add_card_layout);
                rootLayout = itemView.findViewById(R.id.root_layout);
                cardAddTextLayout = itemView.findViewById(R.id.add_card_text_layout);
                cardInfoLayout = itemView.findViewById(R.id.add_card_info_layout);
                cardAddInfoText = itemView.findViewById(R.id.avialablity_time);
                cardAddOutOfStock = itemView.findViewById(R.id.out_of_stock);
                cardAddBtn = itemView.findViewById(R.id.card_add_btn);
                cardMinusBtn = itemView.findViewById(R.id.card_minus_btn);
                cardTextValue = itemView.findViewById(R.id.card_value);
                cardTextValueTicker = itemView.findViewById(R.id.card_value_ticker);
                //itemView.setOnClickListener( this);

                //Load animation
                slide_down = AnimationUtils.loadAnimation(context,
                        R.anim.slide_down);
                slide_up = AnimationUtils.loadAnimation(context,
                        R.anim.slide_up);
            }

        }
    }
}
