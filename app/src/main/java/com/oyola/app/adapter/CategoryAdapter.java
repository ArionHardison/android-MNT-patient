package com.oyola.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oyola.app.R;
import com.oyola.app.models.CategoryModel;

import java.util.List;

/**
 * Created by Prasanth on 16-12-2019
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private List<CategoryModel> list;
    private Context context;
    private Activity activity;
    private OnSelectedListener mListener;
    int selected_pos = -1;
    boolean singleSelection;

    public CategoryAdapter(Context context, Activity activity, List<CategoryModel> list, OnSelectedListener listener) {
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
                .inflate(R.layout.item_category_select, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CategoryModel model=list.get(position);
        holder.tvCategoryName.setText(model.getName());
        if (singleSelection) {
            if (selected_pos == position) {
                model.setSelected(true);
            } else {
                model.setSelected(false);
            }
        }
        holder.tvCategoryName.setTextColor(model.isSelected() ? Color.WHITE : Color.BLACK);
        holder.tvCategoryName.setBackground(model.isSelected() ? ContextCompat.getDrawable(context,
                R.drawable.bg_round_corner_pink) : ContextCompat.getDrawable(context, R.drawable.bg_round_corner_white1));
        holder.tvCategoryName.setTag(position);
        holder.tvCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_pos =(int) v.getTag();;
                model.setSelected(!model.isSelected());
//                    holder.view.setBackgroundColor(model.isSelected() ? getResources().getColor(R.color.colorPrimary) : Color.WHITE);
                holder.tvCategoryName.setTextColor(model.isSelected() ? Color.WHITE : Color.BLACK);
                holder.tvCategoryName.setBackground(model.isSelected() ? ContextCompat.getDrawable(context,
                        R.drawable.bg_round_corner_pink) : ContextCompat.getDrawable(context, R.drawable.bg_round_corner_white1));
                if (singleSelection) {
                    notifyDataSetChanged();
                }
                mListener.onSelected(model.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;


        private MyViewHolder(View view) {
            super(view);
            tvCategoryName = view.findViewById(R.id.tv_category_name);
        }

    }

    public interface OnSelectedListener {
        void onSelected(String data);
    }
}

