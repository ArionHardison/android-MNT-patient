package com.oyola.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.gson.Gson;
import com.oyola.app.R;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.fragments.AddonBottomSheetFragment;
import com.oyola.app.fragments.CartChoiceModeFragment;
import com.oyola.app.fragments.CartFragment;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.models.AddCart;
import com.oyola.app.models.Addon;
import com.oyola.app.models.Addon_;
import com.oyola.app.models.Cart;
import com.oyola.app.models.CartAddon;
import com.oyola.app.models.Product;
import com.oyola.app.models.Shop;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oyola.app.MyApplication.commonAccess;

public class ViewCartAdapter extends RecyclerView.Adapter<ViewCartAdapter.MyViewHolder> {

    private static final String TAG = "ViewCartAdapter";
    private List<Cart> list;
    public static Context context;
    public static int priceAmount = 0;
    public static int discount = 0;
    public static int itemCount = 0;
    public static int itemQuantity = 0;
    public static Product product;
    public static boolean dataResponse = false;
    public static Cart productList;
    public static ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    public static AddCart addCart;
    public static AnimatedVectorDrawableCompat avdProgress;
    public static Dialog dialog;
    public static Runnable action;
    public static Shop selectedShop = GlobalData.selectedShop;
    public static CartChoiceModeFragment bottomSheetDialogFragment;
    //Animation number
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();

    public ViewCartAdapter(List<Cart> list, Context con) {
        this.list = list;
        context = con;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_product_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public void add(Cart item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Cart item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public static void addCart(HashMap<String, String> map, final Context context) {
        Log.d(TAG, context.toString());
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.empty_dialog);
        dialog.setCancelable(false);
        dataResponse = false;
        dialog.show();
        Call<AddCart> call = apiInterface.postAddCart(map);
        Log.e(" Call<AddCart>==>", "" + map);
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(@NonNull Call<AddCart> call, @NonNull Response<AddCart> response) {
                avdProgress.stop();
                dialog.dismiss();
                dataResponse = true;
                if (!response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    Log.d(TAG, response.body().toString());
                    addCart = response.body();
                    GlobalData.addCart = new AddCart();
                    GlobalData.addCart = response.body();
                    CartFragment.viewCartItemList.clear();
                    CartFragment.viewCartItemList.addAll(response.body().getProductList());
                    CartFragment.viewCartAdapter.notifyDataSetChanged();
                    priceAmount = 0;
                    discount = 0;
                    itemQuantity = 0;
                    itemCount = 0;
                    //get Item Count
                    itemCount = addCart.getProductList().size();
                    if (itemCount != 0) {
                        GlobalData.notificationCount = itemQuantity;
                        //Set Payment details
                        String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
                        CartFragment.itemTotalAmount.setText(currency + "" + response.body().getTotalPrice());
                        CartFragment.discountAmount.setText("- " + currency + "" + response.body().getShopDiscount());
                        CartFragment.serviceTax.setText(currency + "" + response.body().getTax());
                        CartFragment.payAmount.setText(currency + "" + response.body().getPayable());
                    } else {
                        GlobalData.notificationCount = itemQuantity;
                        CartFragment.errorLayout.setVisibility(View.VISIBLE);
                        CartFragment.dataLayout.setVisibility(View.GONE);
                        Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddCart> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Cart cart = list.get(position);
        holder.cardAddTextLayout.setVisibility(View.GONE);
        holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
        product = cart.getProduct();
        holder.cardTextValueTicker.setCharacterList(NUMBER_LIST);
        holder.dishNameTxt.setText(product.getName());
        holder.cardTextValue.setText(cart.getQuantity().toString());
        holder.cardTextValueTicker.setText(cart.getQuantity().toString());
        //  priceAmount = product.getCalculated_price();
        List<CartAddon> cartAddonList = cart.getCartAddons();
        double totalAmount = cart.getQuantity() * product.getPrices().getOrignalPrice();
        holder.priceTxt.setText(product.getPrices().getCurrency() + totalAmount);
        if (cartAddonList.isEmpty()) {
            holder.addons.setText(context.getString(R.string.no_addons));
            holder.tvAddonPrice.setVisibility(View.GONE);
        } else {
            holder.tvAddonPrice.setVisibility(View.VISIBLE);
            for (int i = 0; i < cartAddonList.size(); i++) {
                Addon addonProduct = cartAddonList.get(i).getAddonProduct();
                Addon_ addon = addonProduct != null ? addonProduct.getAddon() : null;
                double price = addonProduct != null ? addonProduct.getPrice() : 0;
                String name = addon != null ? addon.getName() : "";

                String value = context.getString(R.string.addon_,
                        name,
                        cart.getQuantity() * cartAddonList.get(i).getQuantity(), product.getPrices().getCurrency() + price);
                double addOnAmount = price * cart.getQuantity() * cartAddonList.get(i).getQuantity();
                if (i == 0) {
                    holder.addons.setText(value);
                    holder.tvAddonPrice.setText(product.getPrices().getCurrency() + addOnAmount);
                } else {
                    holder.addons.append("\n" + value);
                    holder.tvAddonPrice.append("\n" + product.getPrices().getCurrency() + addOnAmount);
                }
            }
        }
        if (!product.getFoodType().equalsIgnoreCase("veg")) {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_nonveg));
            holder.foodImageType.setVisibility(View.GONE);
        } else {
            holder.foodImageType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_veg));
            holder.foodImageType.setVisibility(View.VISIBLE);
        }
        selectedShop = product.getShop();
        if (cart.getNote() != null) {
            holder.tvNotes.setVisibility(View.VISIBLE);
            holder.tvNotes.setText(cart.getNote());
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }
        if (product.getAddons().size() > 0) {
            holder.customize.setVisibility(View.VISIBLE);
            holder.addons.setVisibility(View.VISIBLE);
        } else {
            holder.customize.setVisibility(View.GONE);
            holder.addons.setVisibility(View.GONE);
        }
        holder.cardAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Intilaize Animation View Image */
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
                /* Press Add Card Add button */
                product = cart.getProduct();
                if (product.getAddons() != null && !product.getAddons().isEmpty()) {
                    GlobalData.isSelectedProduct = product;
                    Log.d(TAG, cart.toString());
                    CartChoiceModeFragment.lastCart = cart;
                    Log.d(TAG, new Gson().toJson(cart));
                    commonAccess = "Chooice";
                    bottomSheetDialogFragment = new CartChoiceModeFragment();
                    bottomSheetDialogFragment.show(((AppCompatActivity) context)
                            .getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                    CartChoiceModeFragment.isViewcart = true;
                    CartChoiceModeFragment.isSearch = false;
                } else {
                    int countValue = Integer.parseInt(holder.cardTextValue.getText().toString()) + 1;
                    holder.cardTextValue.setText("" + countValue);
                    holder.cardTextValueTicker.setText("" + countValue);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("product_id", product.getId().toString());
                    map.put("quantity", holder.cardTextValue.getText().toString());
                    map.put("cart_id", String.valueOf(cart.getId()));
                    Log.e("AddCart_add", map.toString());
                    addCart(map, holder.itemView.getContext());
                }
            }
        });
        holder.cardMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Initialize Animation View Image */
                holder.animationLineCartAdd.setVisibility(View.VISIBLE);
                //Initialize
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
                int countMinusValue;
                /* Press Add Card Minus button */
                product = cart.getProduct();
                if (holder.cardTextValue.getText().toString().equalsIgnoreCase("1")) {
                    countMinusValue = Integer.parseInt(holder.cardTextValue.getText().toString()) - 1;
                    holder.cardTextValue.setText("" + countMinusValue);
                    holder.cardTextValueTicker.setText("" + countMinusValue);
                    productList = cart;
                    HashMap<String, String> map = new HashMap<>();
                    map.put("product_id", product.getId().toString());
                    map.put("quantity", String.valueOf(countMinusValue));
                    map.put("cart_id", String.valueOf(cart.getId()));
                    map.put("adddon", "repeat");
                    List<CartAddon> cartAddonList = cart.getCartAddons();
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
                    addCart(map, holder.itemView.getContext());
                    remove(productList);
                } else {
                    countMinusValue = Integer.parseInt(holder.cardTextValue.getText().toString()) - 1;
                    holder.cardTextValue.setText("" + countMinusValue);
                    holder.cardTextValueTicker.setText("" + countMinusValue);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("product_id", product.getId().toString());
                    map.put("quantity", String.valueOf(countMinusValue));
                    map.put("cart_id", String.valueOf(cart.getId()));
                    map.put("adddon", "repeat");
                    List<CartAddon> cartAddonList = cart.getCartAddons();
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
                    addCart(map, holder.itemView.getContext());
                }
            }
        });
        holder.customize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Initialize Animation View Image */
                holder.animationLineCartAdd.setVisibility(View.VISIBLE);
                //Initialize
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
                productList = cart;
                GlobalData.isSelectedProduct = product;
                GlobalData.isSelctedCart = productList;
                GlobalData.cartAddons = productList.getCartAddons();
                AddonBottomSheetFragment bottomSheetDialogFragment = new AddonBottomSheetFragment();
                bottomSheetDialogFragment.show(((AppCompatActivity) context)
                        .getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                AddonBottomSheetFragment.selectedCart = cart;
                // Right here!
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TickerView cardTextValueTicker;
        RelativeLayout cardAddDetailLayout, cardAddTextLayout, cardInfoLayout;
        private ImageView dishImg, foodImageType, cardAddBtn, cardMinusBtn, animationLineCartAdd;
        private TextView dishNameTxt, priceTxt, tvAddonPrice, cardTextValue, cardAddInfoText, cardAddOutOfStock,
                customizableTxt, addons, customize, tvNotes;

        private MyViewHolder(View view) {
            super(view);
            foodImageType = itemView.findViewById(R.id.food_type_image);
            animationLineCartAdd = itemView.findViewById(R.id.animation_line_cart_add);
            dishNameTxt = itemView.findViewById(R.id.dish_name_text);
            priceTxt = itemView.findViewById(R.id.price_text);
            tvAddonPrice = itemView.findViewById(R.id.tvAddonPrice);
            customizableTxt = itemView.findViewById(R.id.customizable_txt);
            addons = itemView.findViewById(R.id.addons);
            customize = itemView.findViewById(R.id.customize);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            /*    Add card Button Layout*/
            cardAddDetailLayout = itemView.findViewById(R.id.add_card_layout);
            cardAddTextLayout = itemView.findViewById(R.id.add_card_text_layout);
            cardAddInfoText = itemView.findViewById(R.id.avialablity_time);
            cardAddOutOfStock = itemView.findViewById(R.id.out_of_stock);
            cardAddBtn = itemView.findViewById(R.id.card_add_btn);
            cardMinusBtn = itemView.findViewById(R.id.card_minus_btn);
            cardTextValue = itemView.findViewById(R.id.card_value);
            cardTextValueTicker = itemView.findViewById(R.id.card_value_ticker);
        }
    }
}