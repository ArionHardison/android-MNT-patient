package com.oyola.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.bumptech.glide.Glide;
import com.oyola.app.R;
import com.oyola.app.activities.HotelViewActivity;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.models.Available;
import com.oyola.app.models.FavListModel;
import com.oyola.app.models.Shop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by santhosh@appoets.com on 24-08-2017.
 */

public class FavouritesAdapter extends SectionedRecyclerViewAdapter<FavouritesAdapter.ViewHolder> {

    Context context;
    private List<FavListModel> list = new ArrayList<>();
    private LayoutInflater inflater;

    public FavouritesAdapter(Context context, List<FavListModel> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_HEADER) {
            v = inflater.inflate(R.layout.header, parent, false);
            return new ViewHolder(v, true);
        } else {
            v = inflater.inflate(R.layout.favorite_list_item, parent, false);
            return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }

    @Override
    public int getItemCount(int section) {
        return list.get(section).getFav().size();
    }

    @Override
    public void onBindHeaderViewHolder(FavouritesAdapter.ViewHolder holder, final int section) {
//        if (list.get(section).getHeader().equals("available"))
//            holder.header.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen));
//        else
//            holder.header.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        holder.header.setText(list.get(section).getHeader());
    }

    @Override
    public void onBindViewHolder(FavouritesAdapter.ViewHolder holder, final int section, int relativePosition, int absolutePosition) {
        final Available availableItem = list.get(section).getFav().get(relativePosition);
        Shop shop = availableItem.getShop();
        holder.shopName.setText(shop.getName());
        holder.shopAddress.setText(shop.getAddress());
        System.out.println(shop.getAvatar());
        Glide.with(context).load(shop.getAvatar()).into(holder.shopAvatar);
        holder.shopStatus.setText(shop.getShopstatus() != null ? shop.getShopstatus() : "NA");

        holder.itemLayout.setOnClickListener(v -> {
            if (list.get(section).getHeader().equals("available")) {
                GlobalData.selectedShop = shop;
                if (shop.getShopstatus() != null) {
                    if (!shop.getShopstatus().equalsIgnoreCase("CLOSED"))
                        context.startActivity(new Intent(context, HotelViewActivity.class).putExtra("is_fav", true));
                    else
                        Toast.makeText(context, context.getResources().getString(R.string.the_shop_is_closed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.the_shop_is_closed_not_available_now), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.un_available), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView header;
        TextView shopAddress;
        TextView shopStatus;
        TextView shopName;
        ImageView shopAvatar;
        RelativeLayout itemLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                header = itemView.findViewById(R.id.header);
            } else {
                itemLayout = itemView.findViewById(R.id.item_layout);
                shopName = itemView.findViewById(R.id.shop_name);
                shopAddress = itemView.findViewById(R.id.shop_address);
                shopStatus = itemView.findViewById(R.id.tv_status);
                shopAvatar = itemView.findViewById(R.id.shop_avatar);
            }

        }


    }
}
