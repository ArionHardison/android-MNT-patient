package com.dietmanager.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.dietmanager.app.R;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Addon;
import com.dietmanager.app.models.Addon_;
import com.dietmanager.app.models.CartAddon;
import com.dietmanager.app.models.Item;
import com.dietmanager.app.models.Orderingredient;
import com.dietmanager.app.models.Prices;

import java.util.List;

public class OrderDetailAdapter extends SectionedRecyclerViewAdapter<OrderDetailAdapter.ViewHolder> {

    private List<Orderingredient> list;
    private Context context;

    public OrderDetailAdapter(List<Orderingredient> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_list_item, parent, false);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_addons_list_item, parent, false);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }

    @Override
    public int getItemCount(int section) {
        return list.size();
        /*if (list.get(section).getCartAddons().isEmpty())
            return 1;
        else
            return list.get(section).getCartAddons().size();*/
    }

    public void add(Orderingredient item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Orderingredient item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, final int section) {
        Orderingredient item = list.get(section);
        /*String value = context.getString(R.string.product_, item.getProduct().getName(), item.getQuantity(),
                item.getProduct().getPrices().getCurrency() +
                        item.getProduct().getPrices().getOrignalPrice());
        holder.productDetail.setText(value);
        double totalAmount = item.getQuantity() * item.getProduct().getPrices().getOrignalPrice();
        holder.productPrice.setText(item.getProduct().getPrices().getCurrency() + totalAmount);
        if (item.getProduct().getNote() != null) {
            holder.tvNotes.setVisibility(View.VISIBLE);
            holder.tvNotes.setText(item.getProduct().getNote());
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int section, int relativePosition, int absolutePosition) {
        /*if (!list.get(section).getCartAddons().isEmpty()) {
            CartAddon cartAddon = list.get(section).getCartAddons().get(relativePosition);
            Addon addon = cartAddon.getAddonProduct();
            Addon_ addons = (cartAddon.getAddonProduct() != null && cartAddon.getAddonProduct().getAddon() != null) ?
                    cartAddon.getAddonProduct().getAddon() : null;
            Prices prices = (list.get(section).getProduct() != null && list.get(section).getProduct().getPrices() != null) ?
                    list.get(section).getProduct().getPrices() : null;
            holder.itemLayout.setVisibility(View.VISIBLE);

            double price = (addon != null && addon.getPrice() != null) ? addon.getPrice() : 0;
            String value = context.getString(R.string.addon_,
                    addons != null ? addons.getName() : "", list.get(section).getQuantity() * cartAddon.getQuantity(),
                    prices != null ? prices.getCurrency() : "" + price);
            holder.addonDetail.setText(value);
            Double totalAmount = price * list.get(section).getQuantity() * cartAddon.getQuantity();
            holder.addonPrice.setText(prices != null ? prices.getCurrency() : "" + totalAmount);
        } else {
            holder.itemLayout.setVisibility(View.GONE);
        }*/
        Orderingredient item = list.get(section);
        /*Prices prices = (list.get(section).getProduct() != null && list.get(section).getProduct().getPrices() != null) ?
                list.get(section).getProduct().getPrices() : null;*/
        holder.itemLayout.setVisibility(View.VISIBLE);

        double price = item != null ? item.getFoodingredient().getIngredient().getPrice() : 0;
        @SuppressLint("StringFormatMatches") String value = context.getString(R.string.addon_,
                item.getFoodingredient().getIngredient() != null ? item.getFoodingredient().getIngredient().getName() : "", item.getFoodingredient().getQuantity(),
                item.getFoodingredient().getIngredient() != null ? GlobalData.currency : "" + price);
        holder.addonDetail.setText(value);
        //Double totalAmount = price * list.get(section).getQuantity() * cartAddon.getQuantity();
        Double totalAmount = item.getFoodingredient().getIngredient().getPrice();
        holder.addonPrice.setText(/*prices != null ? prices.getCurrency() : ""*/GlobalData.currency + totalAmount);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productDetail;
        TextView productPrice;
        TextView tvNotes;
        TextView addonDetail;
        TextView addonPrice;
        LinearLayout itemLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                productDetail = itemView.findViewById(R.id.product_detail);
                productPrice = itemView.findViewById(R.id.product_price);
                tvNotes = itemView.findViewById(R.id.tvNotes);
            } else {
                addonPrice = itemView.findViewById(R.id.addon_price);
                addonDetail = itemView.findViewById(R.id.addon_detail);
                itemLayout = itemView.findViewById(R.id.item_layout);
            }
        }
    }
}