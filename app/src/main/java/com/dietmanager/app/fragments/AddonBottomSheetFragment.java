package com.dietmanager.app.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.CartAddOnsAdapter;
import com.dietmanager.app.adapter.ViewCartAdapter;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Addon;
import com.dietmanager.app.models.Cart;
import com.dietmanager.app.models.Product;
import com.dietmanager.app.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.dietmanager.app.adapter.CartAddOnsAdapter.list;


/**
 * Created by santhosh@appoets.com on 13-11-2017.
 */

public class AddonBottomSheetFragment extends BottomSheetDialogFragment {

    @BindView(R.id.add_ons_rv)
    RecyclerView addOnsRv;

    Context context;
    List<Addon> addonList;
    @BindView(R.id.food_type)
    ImageView foodType;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.product_price)
    TextView productPrice;

    @BindView(R.id.tvOriginPrice)
    TextView tvOriginPrice;

    @SuppressLint("RestrictedApi")
    public static TextView addons;
    public static TextView price;

    @BindView(R.id.update)
    TextView update;

    Unbinder unbinder;
    Product product;
    BottomSheetBehavior mBottomSheetBehavior;

    public static Cart selectedCart;
    private ViewCartAdapter viewCartAdapter;

    public void setViewCartAdapter(ViewCartAdapter viewCartAdapter) {
        this.viewCartAdapter = viewCartAdapter;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.addon_bottom_sheet_fragment, null);
        dialog.setContentView(contentView);
        ButterKnife.bind(this, contentView);
        context = getContext();
        addons = contentView.findViewById(R.id.addons);
        price = contentView.findViewById(R.id.price);
        addOnsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        addOnsRv.setItemAnimator(new DefaultItemAnimator());
        addOnsRv.setHasFixedSize(false);
        addOnsRv.setNestedScrollingEnabled(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            mBottomSheetBehavior = (BottomSheetBehavior) behavior;
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {

                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = contentView.getMeasuredHeight();
                    mBottomSheetBehavior.setPeekHeight(height);
                }
            });
        }
        addonList = new ArrayList<>();
        CartAddOnsAdapter addOnsAdapter = new CartAddOnsAdapter(addonList, context);
        addOnsRv.setAdapter(addOnsAdapter);
        if (selectedCart != null) {
            product = selectedCart.getProduct();
            GlobalData.cartAddons = selectedCart.getCartAddons();
            addonList.clear();
            addonList.addAll(product.getAddons());
            addOnsAdapter.notifyDataSetChanged();
        } else if (GlobalData.isSelectedProduct != null) {
            product = GlobalData.isSelectedProduct;
            addonList.clear();
            addonList.addAll(product.getAddons());
            addOnsAdapter.notifyDataSetChanged();
        }
        productName.setText(product.getName());

        if (product.getPrices() != null && product.getPrices().getCurrency() != null) {
            if (product.getPrices().getDiscount() > 0) {
                Spannable spannable = new SpannableString(product.getPrices().getCurrency() + product.getPrices().getPrice());
                spannable.setSpan(new StrikethroughSpan(), 1, String.valueOf(product.getPrices().getPrice()).length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvOriginPrice.setText(spannable);
            }
            productPrice.setText(String.format("%s %.2f", product.getPrices().getCurrency(), product.getPrices().getOrignalPrice()));
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                map.put("product_id", product.getId().toString());
                map.put("quantity", String.valueOf(GlobalData.isSelctedCart.getQuantity()));
                map.put("cart_id", String.valueOf(GlobalData.isSelctedCart.getId()));
                for (int i = 0; i < list.size(); i++) {
                    Addon addon = list.get(i);
                    if (addon.getAddon().getChecked()) {
                        String id = String.valueOf(addon.getId());
                        map.put("product_addons[" + "" + i + "]", id);
                        map.put("addons_qty[" + "" + id + "]", addon.getQuantity().toString());
                    }
                }
                Log.e("AddCart_add", map.toString());
                viewCartAdapter.addCart(map, Objects.requireNonNull(getActivity()));
                dismiss();
            }
        });

    }


    private void setAddOnsText() {
        double quantity;
        double priceAmount;
        boolean once = true;
        AddonBottomSheetFragment.addons.setText("");
        if (selectedCart != null) {
            Product product = AddonBottomSheetFragment.selectedCart.getProduct();
            quantity = AddonBottomSheetFragment.selectedCart.getQuantity();
            priceAmount = quantity * product.getPrices().getOrignalPrice();
            for (Addon addon : list) {
                if (addon.getAddon().getChecked()) {
                    if (once) {
                        addons.append(addon.getAddon().getName());
                        once = false;
                    } else {
                        AddonBottomSheetFragment.addons.append(", " + addon.getAddon().getName());
                    }

                    priceAmount = priceAmount + (quantity * (addon.getQuantity() * addon.getPrice()));
                }
            }
            if (quantity == 1)
                AddonBottomSheetFragment.price.setText(quantity + " " + R.string.item_count + " | " + GlobalData.currencySymbol + Utils.getNewNumberFormat(priceAmount));
            else
                AddonBottomSheetFragment.price.setText(quantity + " " + R.string.items_counts + " | " + GlobalData.currencySymbol + Utils.getNewNumberFormat(priceAmount));

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
