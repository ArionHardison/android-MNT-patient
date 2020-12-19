package com.dietmanager.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.CurrentOrderDetailActivity;
import com.dietmanager.app.activities.PastOrderDetailActivity;
import com.dietmanager.app.activities.ViewCartActivity;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.AddCart;
import com.dietmanager.app.models.FoodOrder;
import com.dietmanager.app.models.FoodOrderModel;
import com.dietmanager.app.models.Item;
import com.dietmanager.app.models.Order;
import com.dietmanager.app.models.OrderModel;
import com.dietmanager.app.models.Orderingredient;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by santhosh@appoets.com on 28-08-2017.
 */

public class FoodsOrdersAdapter extends SectionedRecyclerViewAdapter<FoodsOrdersAdapter.ViewHolder> {

    private List<FoodOrderModel> list = new ArrayList<>();
    private LayoutInflater inflater;
    Context context1;
    Activity activity;
    int lastPosition = -1;
    List<Orderingredient> itemList;
    CustomDialog customDialog;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public FoodsOrdersAdapter(Context context, Activity activity, List<FoodOrderModel> list) {
        this.context1 = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.activity = activity;
        customDialog = new CustomDialog(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = inflater.inflate(R.layout.header_order, parent, false);
//                setScaleAnimation(v);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
                v = inflater.inflate(R.layout.orders_list_item, parent, false);
//                setScaleAnimation(v);
                return new ViewHolder(v, false);
            default:
                v = inflater.inflate(R.layout.orders_list_item, parent, false);
//                setScaleAnimation(v);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }


    @Override
    public int getItemCount(int section) {
        return list.get(section).getOrders().size();
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, final int section) {
        holder.headerTxt.setText(list.get(section).getHeader());
        holder.headerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(list.get(section).getHeader());
            }
        });
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {
        final FoodOrder object = list.get(section).getOrders().get(relativePosition);
        holder.restaurantNameTxt.setText(object.getFood().getName());
        holder.reorderBtn.setText(object.getStatus());

        //if (object.getFood().getTimeCategory()!=null)
        holder.restaurantAddressTxt.setText(object.getCustomerAddress().getMapAddress());
        int lastPostion = relativePosition + 1;
        if (list.get(section).getOrders().size() == 1) {
            holder.dividerLine.setVisibility(View.GONE);
        } else if (list.get(section).getOrders().size() == relativePosition + 1) {
            holder.dividerLine.setVisibility(View.GONE);
        } else {
            holder.dividerLine.setVisibility(View.VISIBLE);
        }

        itemList = new ArrayList<>();
        itemList.addAll(object.getOrderingredient());
        String dishNameValue = "";

        for (int i = 0; i < itemList.size(); i++) {
            if (object.getOrderingredient().get(i).getFoodingredient().getIngredient().getUnitType() != null) {
                if (i == 0)
                    dishNameValue = object.getOrderingredient().get(i).getFoodingredient().getIngredient().getName() + " (" + object.getOrderingredient().get(i).getFoodingredient().getQuantity() + " " + object.getOrderingredient().get(i).getFoodingredient().getIngredient().getUnitType().getName() + ")";
                else
                    dishNameValue = dishNameValue + ", " + object.getOrderingredient().get(i).getFoodingredient().getIngredient().getName() + " (" + object.getOrderingredient().get(i).getFoodingredient().getQuantity() + " " + object.getOrderingredient().get(i).getFoodingredient().getIngredient().getUnitType().getName() + ")";
            } else {
                if (i == 0)
                    dishNameValue = object.getOrderingredient().get(i).getFoodingredient().getIngredient().getName() + " (" + object.getOrderingredient().get(i).getFoodingredient().getQuantity() + ")";
                else
                    dishNameValue = dishNameValue + ", " + object.getOrderingredient().get(i).getFoodingredient().getIngredient().getName() + " (" + object.getOrderingredient().get(i).getFoodingredient().getQuantity() + ")";
            }
        }
        holder.dishNameTxt.setText(dishNameValue);
        if (object.getScheduleAt() != null)
            holder.dateTimeTxt.setText(getTimeFromString(object.getScheduleAt()));
        else
            holder.dateTimeTxt.setText(getTimeFromString(object.getCreatedAt()));

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(section).getHeader().equalsIgnoreCase("Current Orders")) {
                    GlobalData.isSelectedFoodOrder = list.get(section).getOrders().get(relativePosition);
                    context1.startActivity(new Intent(context1, CurrentOrderDetailActivity.class).putExtra("is_order_page", true));
                } else {
                    GlobalData.isSelectedFoodOrder = list.get(section).getOrders().get(relativePosition);
                    context1.startActivity(new Intent(context1, PastOrderDetailActivity.class));
                }
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView headerTxt;
        TextView restaurantNameTxt, disputeTxt, restaurantAddressTxt, totalAmount, dishNameTxt,
                dateTimeTxt, orderType;
        Button reorderBtn;
        ImageView disputeStatusImage;
        View dividerLine;
        LinearLayout itemLayout, disputeLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                headerTxt = itemView.findViewById(R.id.header);
            } else {
                disputeStatusImage = itemView.findViewById(R.id.dispute_status_image);
                disputeLayout = itemView.findViewById(R.id.dispute_layout);
                disputeTxt = itemView.findViewById(R.id.dispute_txt);

                itemLayout = itemView.findViewById(R.id.item_layout);
                restaurantNameTxt = itemView.findViewById(R.id.restaurant_name);
                restaurantAddressTxt = itemView.findViewById(R.id.restaurant_address);
                totalAmount = itemView.findViewById(R.id.total_amount);
                reorderBtn = itemView.findViewById(R.id.reorder);
                dishNameTxt = itemView.findViewById(R.id.dish_name);
                dateTimeTxt = itemView.findViewById(R.id.date_time);
                dividerLine = itemView.findViewById(R.id.divider_line);
                orderType = itemView.findViewById(R.id.order_type);
            }


        }

    }

    private String getTimeFromString(String time) {
        System.out.println("Time : " + time);
        String value = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, hh:mm aa");

            if (time != null) {
                Date date = df.parse(time);
                value = sdf.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return value;
    }

}
