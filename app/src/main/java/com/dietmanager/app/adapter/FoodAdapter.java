package com.dietmanager.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.ChangeFoodActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.food.FoodItem;

import java.util.ArrayList;
import java.util.List;

import static com.dietmanager.app.build.configure.BuildConfigure.BASE_URL;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {
    private List<FoodItem> foodItems;
    private Context context;
    private IClickListener listener;

    public FoodAdapter(Context context, IClickListener listener) {
        foodItems = new ArrayList<>();
        this.context = context;
        this.listener = listener;
    }

    public void setList(List<FoodItem> itemList) {
        if (itemList == null) {
            return;
        }
        foodItems.clear();
        foodItems.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_food, parent, false);
        return new FoodAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.MyViewHolder holder, final int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.tvFoodTitle.setText(String.valueOf(foodItem.getName()));
        holder.tv_proteins.setText(String.valueOf(foodItem.getProtein()));
        holder.tv_fat.setText(String.valueOf(foodItem.getFat()));
        holder.tv_carb.setText(String.valueOf(foodItem.getCarbohydrates()));
       /* holder.tvFoodDescription.setText(String.valueOf(foodItem.getDescription()));
        holder.tvFoodPrice.setText(String.valueOf(foodItem.getPrice()));*/
            Glide.with(context).load(BASE_URL + foodItem.getAvatar())
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.shimmer_bg).error(R.drawable.shimmer_bg).dontAnimate()).into(holder.imgFood);
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.selectedfood = foodItem;
                //context.startActivity(new Intent(context, SubMenuDetailedActivity.class));
                context.startActivity(new Intent(context, ChangeFoodActivity.class));
            }
        });
        holder.img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRefreshClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public interface IClickListener {
        public void onRefreshClicked(int day);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvFoodTitle, tv_proteins, tv_fat, tv_carb;
        CardView cardItem;
        ImageView imgFood, img_refresh;

        MyViewHolder(View view) {
            super(view);
            imgFood = view.findViewById(R.id.img_food);
            tvFoodTitle = view.findViewById(R.id.tv_food_title);
            tv_proteins = view.findViewById(R.id.tv_proteins);
            tv_fat = view.findViewById(R.id.tv_fat);
            tv_carb = view.findViewById(R.id.tv_carb);
            img_refresh = view.findViewById(R.id.img_refresh);
            cardItem = view.findViewById(R.id.card_item);
        }
    }
}