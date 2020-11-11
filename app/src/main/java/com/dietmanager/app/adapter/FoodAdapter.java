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
import com.dietmanager.app.activities.ProductDetailActivity;
import com.dietmanager.app.activities.SubMenuDetailedActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.food.FoodItem;

import java.util.ArrayList;
import java.util.List;

import static com.dietmanager.app.build.configure.BuildConfigure.BASE_URL;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {
    private List<FoodItem> foodItems;
    private Context context;

    public FoodAdapter(Context context) {
        foodItems = new ArrayList<>();
        this.context=context;
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
       /* holder.tvFoodDescription.setText(String.valueOf(foodItem.getDescription()));
        holder.tvFoodPrice.setText(String.valueOf(foodItem.getPrice()));*/
        if (foodItem.getAvatar()!=null)
            Glide.with(context).load(BASE_URL +foodItem.getAvatar())
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.shimmer_bg).error(R.drawable.shimmer_bg).dontAnimate()).into(holder.imgFood);
holder.cardItem.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        GlobalData.selectedfood = foodItem;
        //context.startActivity(new Intent(context, SubMenuDetailedActivity.class));
        context.startActivity(new Intent(context, ChangeFoodActivity.class));
    }
});
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvFoodTitle,tv_proteins,tv_fat,tv_carb;
        CardView cardItem;
        ImageView imgFood;

        MyViewHolder(View view) {
            super(view);
            imgFood = view.findViewById(R.id.img_food);
            tvFoodTitle = view.findViewById(R.id.tv_food_title);
            tv_proteins = view.findViewById(R.id.tv_proteins);
            tv_fat = view.findViewById(R.id.tv_fat);
            tv_carb = view.findViewById(R.id.tv_carb);
            cardItem = view.findViewById(R.id.card_item);
        }
    }
}