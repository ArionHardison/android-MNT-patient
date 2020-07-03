package com.oyola.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.oyola.app.HeaderView;
import com.oyola.app.R;
import com.oyola.app.adapter.CategoryAdapter;
import com.oyola.app.adapter.HotelCatagoeryAdapter;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.helper.ConnectionHelper;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.models.AddCart;
import com.oyola.app.models.Category;
import com.oyola.app.models.CategoryModel;
import com.oyola.app.models.Favorite;
import com.oyola.app.models.Product;
import com.oyola.app.models.Shop;
import com.oyola.app.models.ShopDetail;
import com.oyola.app.utils.CommonUtils;
import com.oyola.app.utils.Utils;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.oyola.app.adapter.HotelCatagoeryAdapter.bottomSheetDialogFragment;

public class HotelViewActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        CategoryAdapter.OnSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.accompaniment_dishes_rv)
    RecyclerView accompanimentDishesRv;
    @BindView(R.id.rv_category)
    RecyclerView mRvCategory;
    @BindView(R.id.heart_btn)
    ShineButton heartBtn;
    @BindView(R.id.halal)
    TextView mTxtHalal;
    @BindView(R.id.resturantname_tv)
    TextView resturantname_tv;
    @BindView(R.id.resturant_descb_tv)
    TextView resturant_descb_tv;
    //    @BindView(R.id.scroll_view)
//    NestedScrollView scrollView;
    public static TextView itemText;
    public static TextView viewCartShopName;
    public static TextView viewCart;
    public static RelativeLayout viewCartLayout;
    @BindView(R.id.restaurant_image)
    ImageView restaurantImage;
    @BindView(R.id.header_view_title)
    TextView headerViewTitle;
    @BindView(R.id.header_view_sub_title)
    TextView headerViewSubTitle;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.offer)
    TextView offer;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.delivery_time)
    TextView deliveryTime;
    @BindView(R.id.root_layout)
    CoordinatorLayout rootLayout;
    private boolean isHideToolbarView = false;
    @BindView(R.id.toolbar_header_view)
    HeaderView toolbarHeaderView;
    @BindView(R.id.float_header_view)
    HeaderView floatHeaderView;
    int restaurantPosition = 0;
    boolean isShopIsChanged = true;
    double priceAmount = 0;
    int itemCount = 0;
    int itemQuantity = 0;
    Animation slide_down, slide_up;


    Context context;
    ConnectionHelper connectionHelper;
    Activity activity;
    public static Shop shops;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public static List<Category> categoryList;
    public static List<Product> featureProductList;
    public static HotelCatagoeryAdapter catagoeryAdapter;
    public static List<CategoryModel> mCategoryDetails;
    public static CategoryAdapter mAdapter;
    ViewSkeletonScreen skeleton;
    boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_view);
        ButterKnife.bind(this);
        context = HotelViewActivity.this;
        activity = HotelViewActivity.this;
        connectionHelper = new ConnectionHelper(context);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        appBarLayout.addOnOffsetChangedListener(this);
        categoryList = new ArrayList<>();
        mCategoryDetails = new ArrayList<>();
        shops = GlobalData.selectedShop;

        if (shops != null) {
            //Load animation
            slide_down = AnimationUtils.loadAnimation(context,
                    R.anim.slide_down);
            slide_up = AnimationUtils.loadAnimation(context,
                    R.anim.slide_up);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                restaurantPosition = bundle.getInt("position");

            }
            isFavourite = getIntent().getBooleanExtra("is_fav", false);

            if (shops.getOfferPercent() == null || shops.getOfferPercent() == 0) {
                offer.setVisibility(View.GONE);
            } else {
                offer.setVisibility(View.VISIBLE);
//                offer.setText("Flat " + shops.getOfferPercent().toString() + "% offer on all Orders");
                offer.setText("Get " + shops.getOfferPercent().toString() + "% off on Minimum Amount " + GlobalData.currencySymbol + shops.getOfferMinAmount());
            }

            rating.setText(CommonUtils.getRating(shops.getRating()));

            deliveryTime.setText(shops.getEstimatedDeliveryTime().toString() + "Mins");

            itemText = findViewById(R.id.item_text);
            viewCartShopName = findViewById(R.id.view_cart_shop_name);
            viewCart = findViewById(R.id.view_cart);
            viewCartLayout = findViewById(R.id.view_cart_layout);

            viewCartLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HotelViewActivity.this, ViewCartActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
            });

            Glide.with(context)
                    .load(shops.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.ic_restaurant_place_holder)
                            .error(R.drawable.ic_restaurant_place_holder))
                    .into(restaurantImage);

            Picasso.get()
                    .load(shops.getAvatar())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            assert restaurantImage != null;
                            restaurantImage.setImageBitmap(bitmap);
                            Palette.from(bitmap)
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch textSwatch = palette.getDarkMutedSwatch();
                                            if (textSwatch == null) {
                                                textSwatch = palette.getMutedSwatch();
                                                if (textSwatch != null) {
                                                    collapsingToolbar.setContentScrimColor(textSwatch.getRgb());
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        Window window = getWindow();
                                                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                                        window.setStatusBarColor(textSwatch.getRgb());
                                                    }
                                                    headerViewTitle.setTextColor(textSwatch.getTitleTextColor());
                                                    headerViewSubTitle.setTextColor(textSwatch.getBodyTextColor());
                                                }
                                            } else {
                                                collapsingToolbar.setContentScrimColor(textSwatch.getRgb());
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    Window window = getWindow();
                                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                                    window.setStatusBarColor(textSwatch.getRgb());
                                                }
                                                headerViewTitle.setTextColor(textSwatch.getTitleTextColor());
                                                headerViewSubTitle.setTextColor(textSwatch.getBodyTextColor());
                                            }

                                        }
                                    });

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

            //Set title
            collapsingToolbar.setTitle(" ");
//            toolbarHeaderView.bindTo(shops.getName(), shops.getDescription());
            toolbarHeaderView.bindTo(shops.getName());
            resturantname_tv.setText(shops.getName());
            resturant_descb_tv.setText(shops.getDescription());
            if (shops.getHalal() != null) {
                mTxtHalal.setVisibility(shops.getHalal() == 1 ? View.VISIBLE : View.GONE);
            }
//            floatHeaderView.bindTo(shops.getName(), shops.getDescription());

            //Set Categoery shopList adapter
            catagoeryAdapter = new HotelCatagoeryAdapter(this, activity, categoryList);
            accompanimentDishesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            accompanimentDishesRv.setItemAnimator(new DefaultItemAnimator());
            accompanimentDishesRv.setAdapter(catagoeryAdapter);

            if (heartBtn != null)
                heartBtn.init(this);
            if (shops.getFavorite() != null || isFavourite) {
                heartBtn.setChecked(true);
                heartBtn.setTag(1);
            } else
                heartBtn.setTag(0);
            heartBtn.setShineDistanceMultiple(1.8f);
            heartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (heartBtn.getTag().equals(0)) {
                        heartBtn.setTag(1);
                        heartBtn.setShapeResource(R.raw.heart);
                    } else {
                        heartBtn.setTag(0);
                        heartBtn.setShapeResource(R.raw.icc_heart);
                    }

                }
            });
            heartBtn.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View view, boolean checked) {
                    Log.e("HeartButton", "click " + checked);
                    if (connectionHelper.isConnectingToInternet()) {
                        if (checked) {
                            if (GlobalData.profileModel != null)
                                doFavorite(shops.getId());
                            else {
                                startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.anim_nothing);
                                finish();
                            }
                        } else {
                            deleteFavorite(shops.getId());
                        }
                    } else {
                        Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
                    }
                }
            });


        } else {
            startActivity(new Intent(context, SplashActivity.class));
            finish();
        }

        skeleton = Skeleton.bind(rootLayout)
                .load(R.layout.skeleton_hotel_view)
                .show();

        getViewCart();
    }


    private void getViewCart() {
        skeleton.show();
        Call<AddCart> call = apiInterface.getViewCart();
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(Call<AddCart> call, Response<AddCart> response) {
                skeleton.hide();
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    GlobalData.addCart = null;
                } else if (response.isSuccessful()) {
                    //get Item Count
                    itemCount = response.body().getProductList().size();
                    GlobalData.notificationCount = response.body().getProductList().size();
                    if (itemCount == 0) {
                        GlobalData.addCart = response.body();
                    } else {
                        AddCart addCart = response.body();
                        GlobalData.addCart = response.body();
                        for (int i = 0; i < itemCount; i++) {
                            //Get Total item Quantity
                            itemQuantity = itemQuantity + response.body().getProductList().get(i).getQuantity();
                            //Get product price
                            if (response.body().getProductList().get(i).getProduct().getPrices().getOrignalPrice() != 0)
                                priceAmount = priceAmount + (response.body().getProductList().get(i).getQuantity() * response.body().getProductList().get(i).getProduct().getPrices().getOrignalPrice());

                            if (addCart.getProductList().get(i).getCartAddons() != null && !addCart.getProductList().get(i).getCartAddons().isEmpty()) {
                                for (int j = 0; j < addCart.getProductList().get(i).getCartAddons().size(); j++) {
                                    priceAmount = priceAmount + (addCart.getProductList().get(i).getQuantity() * (addCart.getProductList().get(i).getCartAddons().get(j).getQuantity() *
                                            addCart.getProductList().get(i).getCartAddons().get(j).getAddonProduct().getPrice()));
                                }
                            }
                        }
                        GlobalData.notificationCount = itemQuantity;
                        GlobalData.addCartShopId = response.body().getProductList().get(0).getProduct().getShopId();
                        //Set Payment details
                        String currency = response.body().getProductList().get(0).getProduct().getPrices().getCurrency();
                        if (response.body().getProductList().get(0).getProduct().getShop().getOfferMinAmount() != 0) {
                            if (response.body().getProductList().get(0).getProduct().getShop().getOfferMinAmount() < priceAmount) {
                                int offerPercentage = response.body().getProductList().get(0).getProduct().getShop().getOfferPercent();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AddCart> call, Throwable t) {

            }
        });
    }

    private void deleteFavorite(Integer id) {
        Call<Favorite> call = apiInterface.deleteFavorite(id);
        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(@NonNull Call<Favorite> call, @NonNull Response<Favorite> response) {
                Favorite favorite = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<Favorite> call, @NonNull Throwable t) {

            }
        });

    }

    private void doFavorite(Integer id) {
        Call<Favorite> call = apiInterface.doFavorite(id);
        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(@NonNull Call<Favorite> call, @NonNull Response<Favorite> response) {
                Favorite favorite = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<Favorite> call, @NonNull Throwable t) {

            }
        });

    }

    private void setViewcartBottomLayout(AddCart addCart) {
        priceAmount = 0;
        itemQuantity = 0;
        itemCount = 0;
        //get Item Count
        itemCount = addCart.getProductList().size();
        for (int i = 0; i < itemCount; i++) {
            //Get Total item Quantity
            itemQuantity = itemQuantity + addCart.getProductList().get(i).getQuantity();
            //Get product price
            if (addCart.getProductList().get(i).getProduct().getPrices().getPrice() > 0)
                priceAmount = priceAmount + (addCart.getProductList().get(i).getQuantity() * addCart.getProductList().get(i).getProduct().getPrices().getOrignalPrice());
            if (addCart.getProductList().get(i).getCartAddons() != null && !addCart.getProductList().get(i).getCartAddons().isEmpty()) {
                for (int j = 0; j < addCart.getProductList().get(i).getCartAddons().size(); j++) {
                    priceAmount = priceAmount + (addCart.getProductList().get(i).getQuantity() * (addCart.getProductList().get(i).getCartAddons().get(j).getQuantity() *
                            addCart.getProductList().get(i).getCartAddons().get(j).getAddonProduct().getPrice()));
                }
            }
        }
        GlobalData.notificationCount = itemQuantity;
        if (itemQuantity == 0) {
            HotelViewActivity.viewCartLayout.setVisibility(View.GONE);
            // Start animation
            viewCartLayout.startAnimation(slide_down);
        } else if (itemQuantity == 1) {
            if (!shops.getId().equals(GlobalData.addCart.getProductList().get(0).getProduct().getShopId())) {
                HotelViewActivity.viewCartShopName.setVisibility(View.VISIBLE);
                HotelViewActivity.viewCartShopName.setText("From : " + GlobalData.addCart.getProductList().get(0).getProduct().getShop().getName());
            } else
                HotelViewActivity.viewCartShopName.setVisibility(View.GONE);
            String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
            HotelViewActivity.itemText.setText("" + itemQuantity + " " + getResources().getString(R.string.item_count) + " | " + currency + "" + priceAmount);
            if (HotelViewActivity.viewCartLayout.getVisibility() == View.GONE) {
                // Start animation
                HotelViewActivity.viewCartLayout.setVisibility(View.VISIBLE);
                viewCartLayout.startAnimation(slide_up);
            }
        } else {
            if (!Objects.equals(shops.getId(), GlobalData.addCart.getProductList().get(0).getProduct().getShopId())) {
                HotelViewActivity.viewCartShopName.setVisibility(View.VISIBLE);
                HotelViewActivity.viewCartShopName.setText("From : " + GlobalData.addCart.getProductList().get(0).getProduct().getShop().getName());
            } else
                HotelViewActivity.viewCartShopName.setVisibility(View.GONE);

            String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
            HotelViewActivity.itemText.setText("" + itemQuantity + " " + getResources().getString(R.string.item_count) + " | " + currency + "" + Utils.getNewNumberFormat(priceAmount));
            if (HotelViewActivity.viewCartLayout.getVisibility() == View.GONE) {
                // Start animation
                HotelViewActivity.viewCartLayout.setVisibility(View.VISIBLE);
                viewCartLayout.startAnimation(slide_up);
            }

        }
    }

    private void getCategories(HashMap<String, String> map) {
        skeleton.show();
        Call<ShopDetail> call = apiInterface.getCategories(map);
        call.enqueue(new Callback<ShopDetail>() {
            @Override
            public void onResponse(@NonNull Call<ShopDetail> call, @NonNull Response<ShopDetail> response) {
                skeleton.hide();
                if (response.isSuccessful()) {
                    categoryList = new ArrayList<>();
                    featureProductList = new ArrayList<>();
                    categoryList.clear();
                    featureProductList.clear();

                    if (response.body().getFeaturedProducts() != null && response.body().getFeaturedProducts().size() > 0) {
                        Category category = new Category();
                        featureProductList.addAll(response.body().getFeaturedProducts());
                        category.setName(getResources().getString(R.string.featured_products));
                        category.setProducts(featureProductList);
                        categoryList.add(category);
                    }
                    if (response.body().getCategories() != null && response.body().getCategories().size() > 0) {
                        categoryList.addAll(response.body().getCategories());
                    }
                    catagoeryAdapter = new HotelCatagoeryAdapter(context, activity, categoryList);
                    accompanimentDishesRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    accompanimentDishesRv.setItemAnimator(new DefaultItemAnimator());
                    accompanimentDishesRv.setAdapter(catagoeryAdapter);

                    if (categoryList != null && categoryList.size() > 1)
                        setCategoryAdapter(categoryList);
                    if (GlobalData.addCart != null && GlobalData.addCart.getProductList().size() != 0) {
                        setViewcartBottomLayout(GlobalData.addCart);
                    } else {
                        viewCartLayout.setVisibility(View.GONE);
                    }
                    catagoeryAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ShopDetail> call, @NonNull Throwable t) {

            }
        });


    }

    private void setCategoryAdapter(List<Category> mList) {
        mCategoryDetails = new ArrayList<>();
        mCategoryDetails.clear();
        mCategoryDetails.add(new CategoryModel("All", true));
        if (mList != null && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                if (!mList.get(i).getName().equalsIgnoreCase("Featured Items")) {
                    mCategoryDetails.add(new CategoryModel(mList.get(i).getName(), false));
                }
            }
        }
        mAdapter = new CategoryAdapter(context, activity, mCategoryDetails, this);
        mRvCategory.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mRvCategory.setItemAnimator(new DefaultItemAnimator());
        mRvCategory.setAdapter(mAdapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomSheetDialogFragment != null)
            bottomSheetDialogFragment.dismiss();

        if (connectionHelper.isConnectingToInternet()) {
            //get User Profile Data
            if (GlobalData.profileModel != null) {
                HashMap<String, String> map = new HashMap<>();
                map.put("shop", String.valueOf(shops.getId()));
                map.put("user_id", String.valueOf(GlobalData.profileModel.getId()));
                getCategories(map);
            } else {
                HashMap<String, String> map = new HashMap<>();
                map.put("shop", String.valueOf(shops.getId()));
                getCategories(map);
            }
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public void onSelected(String data) {
        if (catagoeryAdapter != null)
            catagoeryAdapter.setCategoryList(data);
    }
}


