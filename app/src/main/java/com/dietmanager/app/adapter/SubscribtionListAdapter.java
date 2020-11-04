package com.dietmanager.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.app.R;
import com.dietmanager.app.models.CategoryModel;
import com.dietmanager.app.models.SubscriptionList;

import java.util.List;

/**
 * Created by arun on 04-11-2020
 */
public class SubscribtionListAdapter extends RecyclerView.Adapter<SubscribtionListAdapter.MyViewHolder> {
    private List<SubscriptionList> list;
    private Context context;
    private Activity activity;
    private OnSelectedListener mListener;
    int selected_pos = -1;
    boolean singleSelection;

    public SubscribtionListAdapter(Context context, Activity activity, List<SubscriptionList> list, OnSelectedListener listener) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.mListener = listener;
        singleSelection=true;
        selected_pos=0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_subscribe, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SubscriptionList model=list.get(position);
        holder.rd_text.setText(model.getDescription());
        holder.tv_price.setText(model.getPrice());
        holder.tv_price.setTag(position);
        holder.rd_text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_price;
        private RadioButton rd_text;


        private MyViewHolder(View view) {
            super(view);
            tv_price = view.findViewById(R.id.tv_price);
            rd_text = view.findViewById(R.id.rd_text);
        }

    }

    public interface OnSelectedListener {
        void onSelected(String data);
    }
}

