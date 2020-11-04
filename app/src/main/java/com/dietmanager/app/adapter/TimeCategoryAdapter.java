package com.dietmanager.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.dietmanager.app.R;
import com.dietmanager.app.models.timecategory.TimeCategoryItem;

import java.util.ArrayList;
import java.util.List;

public class TimeCategoryAdapter extends RecyclerView.Adapter<TimeCategoryAdapter.MyViewHolder> {
    private List<TimeCategoryItem> timeCategoryList;
    private int selectedIndex=0;
    private Context context;
    private ITimeCategoryListener listener;

    public TimeCategoryAdapter(Context context, int selectedIndex, ITimeCategoryListener listener) {
        this.listener = listener;
        timeCategoryList = new ArrayList<>();
        this.context=context;
        this.selectedIndex=selectedIndex;
    }

    public void setList(List<TimeCategoryItem> itemList) {
        if (itemList == null) {
            return;
        }
        timeCategoryList.clear();
        timeCategoryList.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.time_category_list_item, parent, false);

        return new TimeCategoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeCategoryAdapter.MyViewHolder holder, final int position) {
        TimeCategoryItem timeCategoryItem = timeCategoryList.get(position);
        holder.tvTimeCategory.setText(String.valueOf(timeCategoryItem.getName()));

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCategoryClicked(timeCategoryItem.getId(),timeCategoryItem.getName());
                selectedIndex=position;
                notifyDataSetChanged();
            }
        });
        if(position==selectedIndex){
            holder.itemLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.button_corner_bg_theme));
            holder.tvTimeCategory.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        }
        else{
            holder.itemLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.white_bg_curved));
            holder.tvTimeCategory.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return timeCategoryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTimeCategory;
        LinearLayout itemLayout;

        MyViewHolder(View view) {
            super(view);
            tvTimeCategory = view.findViewById(R.id.tvTimeCategory);
            itemLayout = view.findViewById(R.id.item_view);
        }
    }

    public interface ITimeCategoryListener {
        public void onCategoryClicked(int category, String categoryName);
    }
}
