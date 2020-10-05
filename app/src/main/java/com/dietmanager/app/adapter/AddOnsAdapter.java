package com.dietmanager.app.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dietmanager.app.R;
import com.dietmanager.app.activities.ProductDetailActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Addon;
import com.dietmanager.app.models.Cart;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.List;

import static com.dietmanager.app.OyolaApplication.currency;
import static com.dietmanager.app.helper.GlobalData.isSelectedProduct;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class AddOnsAdapter extends RecyclerView.Adapter<AddOnsAdapter.MyViewHolder> {
    public static List<Addon> list;
    private Context context;
    Addon addon;

    //Animation number
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();

    public AddOnsAdapter(List<Addon> list, Context con) {
        AddOnsAdapter.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_ons_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public void add(Addon item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Cart item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.cardAddTextLayout.setVisibility(View.VISIBLE);
        holder.cardAddDetailLayout.setVisibility(View.GONE);
        addon = list.get(position);
        holder.cardTextValueTicker.setCharacterList(NUMBER_LIST);
        Spannable wordOne = new SpannableString(addon.getAddon().getName() + " " + currency + list.get(position).getPrice());
        wordOne.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorSecondaryText)), 0, wordOne.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.addonName.setText(wordOne);
        Spannable wordTwo;
        if (addon.getAddon().getCalories() != null) {
            wordTwo = new SpannableString(" " + addon.getAddon().getCalories() + " "+context.getString(R.string.calories_symbol));
        } else {
            wordTwo = new SpannableString(" 0 "+ context.getString(R.string.calories_symbol));
        }
        wordTwo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.checkbox_green)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.addonName.append(wordTwo);
//        holder.addonName.setText(addon.getAddon().getName() + " " + currency + list.get(position).getPrice());
        addon.setQuantity(1);
        holder.cardTextValue.setText("1");
        holder.cardTextValueTicker.setText("1");
        addon.getAddon().setChecked(false);
        holder.addonName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
                    holder.cardAddTextLayout.setVisibility(View.GONE);
                    addon = list.get(position);
                    addon.getAddon().setChecked(true);
                    setAddOnsText();
                } else {
                    holder.cardAddDetailLayout.setVisibility(View.GONE);
                    holder.cardAddTextLayout.setVisibility(View.VISIBLE);
                    addon.getAddon().setChecked(false);
                    setAddOnsText();

                }

            }
        });

        holder.cardAddTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addon = list.get(position);
                holder.cardAddDetailLayout.setVisibility(View.VISIBLE);
                holder.cardAddTextLayout.setVisibility(View.GONE);
                holder.cardTextValue.setText("1");
                holder.cardTextValueTicker.setText("1");
                addon.setQuantity(1);
                holder.addonName.setChecked(true);
                addon.getAddon().setChecked(true);
                setAddOnsText();

            }
        });

        holder.cardAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Press Add Card Add button */
                addon = list.get(position);
                addon.getAddon().setChecked(true);
                int countValue = Integer.parseInt(holder.cardTextValue.getText().toString()) + 1;
                holder.cardTextValue.setText("" + countValue);
                holder.cardTextValueTicker.setText("" + countValue);
                addon.setQuantity(countValue);
                setAddOnsText();
            }
        });

        holder.cardMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countMinusValue;
                /** Press Add Card Minus button */
                addon = list.get(position);
                if (holder.cardTextValue.getText().toString().equalsIgnoreCase("1")) {
                    holder.cardAddDetailLayout.setVisibility(View.GONE);
                    holder.cardAddTextLayout.setVisibility(View.VISIBLE);
                    holder.addonName.setChecked(false);
                    addon.getAddon().setChecked(false);
                } else {
                    countMinusValue = Integer.parseInt(holder.cardTextValue.getText().toString()) - 1;
                    holder.cardTextValue.setText("" + countMinusValue);
                    holder.cardTextValueTicker.setText("" + countMinusValue);
                    addon.setQuantity(countMinusValue);
                }
                setAddOnsText();
            }
        });
    }

    private void setAddOnsText() {
        double totalAmount = isSelectedProduct.getPrices().getOrignalPrice();
        for (int i = 0; i < list.size(); i++) {
            Addon addon = list.get(i);
            if (addon.getAddon().getChecked()) {
                totalAmount = totalAmount + (addon.getQuantity() * addon.getPrice());
            }
        }
        ProductDetailActivity.itemText.setText("1 Item | " + GlobalData.currencySymbol + totalAmount);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView dishImg, foodImageType, cardAddBtn, cardMinusBtn, animationLineCartAdd;
        private TextView cardTextValue, cardAddInfoText, cardAddOutOfStock;
        TickerView cardTextValueTicker;
        CheckBox addonName;
        RelativeLayout cardAddDetailLayout, cardAddTextLayout, cardInfoLayout, addButtonRootLayout;

        private MyViewHolder(View view) {
            super(view);
            foodImageType = itemView.findViewById(R.id.food_type_image);
            animationLineCartAdd = itemView.findViewById(R.id.animation_line_cart_add);
            addonName = itemView.findViewById(R.id.dish_name_text);
            /*    Add card Button Layout*/
            cardAddDetailLayout = itemView.findViewById(R.id.add_card_layout);
            addButtonRootLayout = itemView.findViewById(R.id.add_button_root_layout);
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
