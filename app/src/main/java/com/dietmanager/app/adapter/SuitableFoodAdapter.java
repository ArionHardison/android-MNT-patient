package com.dietmanager.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.SubMenuDetailedActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.food.FoodItem;
import com.dietmanager.app.utils.IOnClickListener;

import java.util.ArrayList;
import java.util.List;

import static com.dietmanager.app.build.configure.BuildConfigure.BASE_URL;

public class SuitableFoodAdapter extends RecyclerView.Adapter<SuitableFoodAdapter.MyViewHolder> {
    private List<FoodItem> foodItems;
    private Context context;
    private IOnClickListener listener;

    public SuitableFoodAdapter(Context context, IOnClickListener listener) {
        this.listener = listener;
        foodItems = new ArrayList<>();
        this.context = context;
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
    public SuitableFoodAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.changefood_list_item, parent, false);
        return new SuitableFoodAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SuitableFoodAdapter.MyViewHolder holder, final int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.tvFoodTitle.setText(String.valueOf(foodItem.getName()));
        holder.tv_proteins.setText(String.valueOf(foodItem.getProtein()));
        holder.tv_fat.setText(String.valueOf(foodItem.getFat()));
        holder.tv_carb.setText(String.valueOf(foodItem.getCarbohydrates()));
        if (foodItem.getAvatar() != null)
            Glide.with(context).load(BASE_URL +foodItem.getAvatar())
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.shimmer_bg).error(R.drawable.shimmer_bg).dontAnimate()).into(holder.imgFood);
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.selectedfood = foodItem;
                listener.onItemClicked();
            }
        });
        holder.check_food.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {

                } else {


                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvFoodTitle, tv_proteins, tv_fat, tv_carb;
        CardView cardItem;
        ImageView imgFood;
        CheckBox check_food;

        MyViewHolder(View view) {
            super(view);
            imgFood = view.findViewById(R.id.food_avatar);
            tvFoodTitle = view.findViewById(R.id.food_name);
            tv_proteins = view.findViewById(R.id.tv_proteins);
            tv_fat = view.findViewById(R.id.tv_fat);
            tv_carb = view.findViewById(R.id.tv_carb);
            cardItem = view.findViewById(R.id.item_layout);
            check_food = itemView.findViewById(R.id.check_food);
        }
    }
}