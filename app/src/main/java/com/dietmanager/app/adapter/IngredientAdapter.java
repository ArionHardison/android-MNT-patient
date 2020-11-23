package com.dietmanager.app.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.ProductDetailActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Addon;
import com.dietmanager.app.models.Cart;
import com.dietmanager.app.models.food.FoodIngredient;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.ArrayList;
import java.util.List;

import static com.dietmanager.app.OyolaApplication.currency;
import static com.dietmanager.app.helper.GlobalData.isSelectedProduct;

/**
 * Created by arun on 05-11-2020.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {
    public static List<FoodIngredient> list;
    private Context context;
    //FoodIngredient foodIngredient;
    private ICheckclickListener listener;


    public IngredientAdapter(List<FoodIngredient> list, Context con, ICheckclickListener listener) {
        this.list = list;
        this.context = con;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredients_item_list, parent, false);
        return new MyViewHolder(itemView);
    }

    public void add(FoodIngredient item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(FoodIngredient item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        FoodIngredient foodIngredient = list.get(position);
        holder.tv_name.setText(foodIngredient.getIngredient().getName());
        if (foodIngredient.getIngredient().getUnitType()!=null){
            holder.tv_unit.setText(foodIngredient.getQuantity()+" "+foodIngredient.getIngredient().getUnitType().getName());
        }
        if (foodIngredient.getIngredient().getAvatar()!=null)
            Glide.with(context).load(foodIngredient.getIngredient().getAvatar())
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.shimmer_bg)
                            .error(R.drawable.shimmer_bg).dontAnimate()).into(holder.img_item);
        holder.check_food.setChecked(foodIngredient.isChecked());
        holder.check_food.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {

                } else {

                }
                foodIngredient.setChecked(!foodIngredient.isChecked());
                if (listener!=null)
                    listener.onClicked(position);

            }
        });
    }
    public ArrayList<FoodIngredient> getSelected() {
        ArrayList<FoodIngredient> selected = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) {
                selected.add(list.get(i));
            }
        }
        return selected;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface ICheckclickListener{
        public void onClicked(int day);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_item;
        private TextView tv_name,tv_unit;
        CheckBox check_food;


        private MyViewHolder(View view) {
            super(view);

            img_item = itemView.findViewById(R.id.img_item);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_unit = itemView.findViewById(R.id.tv_unit);
            check_food = itemView.findViewById(R.id.check_food);

        }
    }

}
