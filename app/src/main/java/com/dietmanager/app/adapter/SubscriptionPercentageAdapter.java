package com.dietmanager.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.SubMenuDetailedActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.dietitiandetail.DietitianDetailedResponse;
import com.dietmanager.app.models.dietitiandetail.SubscriptionItem;
import com.dietmanager.app.models.food.FoodItem;

import java.util.ArrayList;
import java.util.List;

import static com.dietmanager.app.build.configure.BuildConfigure.BASE_URL;

public class SubscriptionPercentageAdapter extends RecyclerView.Adapter<SubscriptionPercentageAdapter.MyViewHolder> {
    private List<SubscriptionItem> list;
    private Context context;
    private int totalSubscribers;
    private ISubscriptionPercentageListener listener;

    public SubscriptionPercentageAdapter(Context context,int totalSubscribers,ISubscriptionPercentageListener listener) {
        this.totalSubscribers = totalSubscribers;
        this.listener = listener;
        list = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<SubscriptionItem> itemList) {
        if (itemList == null) {
            return;
        }
        list.clear();
        list.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubscriptionPercentageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subscription_percentage, parent, false);
        return new SubscriptionPercentageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionPercentageAdapter.MyViewHolder holder, final int position) {
        SubscriptionItem item = list.get(position);
        holder.tvSubscriptionTitle.setText(String.valueOf(item.getTitle()));
        int percentage=0;
        if(item.getTotalPlanCount()!=0) {
            double value=item.getPlanCount() / item.getTotalPlanCount()*100;
            percentage=(int)value;
        }
        holder.progressSubscribe.setProgress(percentage);
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPercentageClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemLayout;
        TextView tvSubscriptionTitle;
        ProgressBar progressSubscribe;

        MyViewHolder(View view) {
            super(view);
            tvSubscriptionTitle = view.findViewById(R.id.tvSubscriptionTitle);
            progressSubscribe = view.findViewById(R.id.progressSubscribe);
            itemLayout = view.findViewById(R.id.item_layout);
        }
    }

    public interface ISubscriptionPercentageListener{
        void onPercentageClicked(SubscriptionItem item);
    }
}