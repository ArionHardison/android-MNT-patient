package com.dietmanager.app.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dietmanager.app.R;
import com.dietmanager.app.models.Promotions;
import com.dietmanager.app.models.Restaurant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.MyViewHolder> {
    private List<Promotions> list;
    PromotionListener promotionListener;

    public PromotionsAdapter(List<Promotions> list,PromotionListener promotionListener) {
        this.list = list;
        this.promotionListener=promotionListener;
    }

    public interface PromotionListener{
        void onApplyBtnClick(Promotions promotions);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promotions_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void add(Promotions item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Restaurant item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Promotions promotionsModel = list.get(position);
        holder.promoNameTxt.setText(promotionsModel.getPromoCode());
        holder.expireTxt.setText(convertDateFormat(promotionsModel.getExpiration()));
        holder.statusBtnTxt.setTag(promotionsModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView promoNameTxt;
        TextView expireTxt;
        Button statusBtnTxt;

        private MyViewHolder(View view) {
            super(view);
            promoNameTxt= (TextView) view.findViewById(R.id.promo_name_txt);
            expireTxt= (TextView) view.findViewById(R.id.expire_date_txt);
            statusBtnTxt= (Button) view.findViewById(R.id.status_btn);

            statusBtnTxt.setOnClickListener(this);
        }

        public void onClick(View v) {
            Promotions promotions= (Promotions) v.getTag();
            promotionListener.onApplyBtnClick(promotions);
        }
    }

    private String convertDateFormat(String date) {
        if(date != null) {
            String newDateString = null;
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            try {
                Date newDate = spf.parse(date);
                newDateString =
                        new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return newDateString;
        }else
            return "-";
    }
}
