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

import java.util.ArrayList;
import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.MyViewHolder> {
    private List<Integer> dayList;
    private int selectedIndex=0;
    private Context context;
    private IDayListener listener;

    public DaysAdapter(Context context, int selectedIndex, IDayListener listener) {
        this.listener = listener;
        dayList = new ArrayList<>();
        this.context=context;
        this.selectedIndex=selectedIndex;
    }

    public void setList(List<Integer> itemList) {
        if (itemList == null) {
            return;
        }
        dayList.clear();
        dayList.addAll(itemList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DaysAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_list_item, parent, false);

        return new DaysAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DaysAdapter.MyViewHolder holder, final int position) {
        Integer day = dayList.get(position);
        holder.tvDay.setText(String.valueOf(day));

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDayClicked(day);
                selectedIndex=position;
                notifyDataSetChanged();
            }
        });
        if(position==selectedIndex){
            holder.itemLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_color_primary_border));
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.tvDayDummy.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
        else{
            holder.itemLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_color_grey_border));
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            holder.tvDayDummy.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        }
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDay,tvDayDummy;
        LinearLayout itemLayout;

        MyViewHolder(View view) {
            super(view);
            tvDay = view.findViewById(R.id.tvDay);
            tvDayDummy = view.findViewById(R.id.tvDayDummy);
            itemLayout = view.findViewById(R.id.item_view);
        }
    }

    public interface IDayListener{
        public void onDayClicked(int day);
    }
}
