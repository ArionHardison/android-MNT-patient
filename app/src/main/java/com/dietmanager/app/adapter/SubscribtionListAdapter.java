package com.dietmanager.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.app.R;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.SubscriptionList;

import java.util.List;

/**
 * Created by arun on 05-11-2020.
 */

public class SubscribtionListAdapter extends RecyclerView.Adapter<SubscribtionListAdapter.MyViewHolder> {
    public  List<SubscriptionList> list;
    private Context context;
    SubscriptionList foodIngredient;
    private SubscribtionListAdapter.OnSelectedListener mListener;
    public static int checkedPosition = 0;
    public static int selectedposition = 0;


    public SubscribtionListAdapter(List<SubscriptionList> list, Context con) {
        this.list = list;
        this.context = con;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_subscribe, parent, false);
        return new MyViewHolder(itemView);
    }




    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        foodIngredient = list.get(position);
        holder.rd_text.setText(foodIngredient.getTitle());

        double price=0.0;
        price=Double.parseDouble(foodIngredient.getPrice())+(Double.parseDouble(foodIngredient.getPrice())*Double.parseDouble(GlobalData.profileModel.getDietTax())/100);
        holder.tv_price.setText(GlobalData.currency+price);
        if (checkedPosition == -1) {
            holder.rd_text.setChecked(false);
        } else {
            if (checkedPosition == position) {
                holder.rd_text.setChecked(true);
                selectedposition =foodIngredient.getId();
            } else {
                holder.rd_text.setChecked(false);
            }
        }
        holder.rd_text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    if (checkedPosition != position) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = position;
                        selectedposition = foodIngredient.getId();
                    }

                }


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
        void onSelected(SubscriptionList data);
    }
}
