package com.dietmanager.app.adapter;

import android.content.Context;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dietmanager.app.R;
import com.dietmanager.app.activities.CurrentOrderDetailActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.OrderFlow;

import java.util.List;

import static com.dietmanager.app.helper.GlobalData.isSelectedFoodOrder;
import static com.dietmanager.app.helper.GlobalData.isSelectedOrder;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class OrderFlowAdapter extends RecyclerView.Adapter<OrderFlowAdapter.MyViewHolder> {
    private List<OrderFlow> list;
    private Context context;
    public String orderStatus = "";
    boolean isRateOpened = true;
    boolean isImageOpened = true;


    public OrderFlowAdapter(List<OrderFlow> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_flow_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void add(OrderFlow item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(OrderFlow item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        OrderFlow orderFlow = list.get(position);
        holder.statusTitle.setText(orderFlow.statusTitle);
        holder.statusDescription.setText(orderFlow.statusDescription);
//        holder.statusImage.setImageResource(orderFlow.statusImage);
        if (orderFlow.status.contains(isSelectedFoodOrder.getStatus())) {
            holder.statusImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_dot_green));
            holder.statusTitle.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            /*if (isSelectedFoodOrder.getStatus().equals(GlobalData.ORDER_STATUS.get(GlobalData.ORDER_STATUS.size() - 3))) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        if (isRateOpened){
                            ((CurrentOrderDetailActivity) context).rate();
                            isRateOpened = false;
                        }

                    }
                }, 5000);

            }
            if (isSelectedFoodOrder.getStatus().equalsIgnoreCase("PREPARED")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        if (isImageOpened){
                            ((CurrentOrderDetailActivity) context).showImage();
                            isImageOpened = false;
                        }

                    }
                }, 5000);

            }*/
            if (isSelectedFoodOrder.getStatus().equals(GlobalData.ORDER_STATUS.get(0))) {
                CurrentOrderDetailActivity.orderCancelTxt.setVisibility(View.GONE);
            } else {
                CurrentOrderDetailActivity.orderCancelTxt.setVisibility(View.GONE);
            }
        } else {
            holder.statusImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_dot_grey));
            holder.statusTitle.setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryText));
        }

        if (list.size() == position + 1)
            holder.viewLine.setVisibility(View.GONE);
        else
            holder.viewLine.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView statusImage;
        private View viewLine;
        private TextView statusTitle, statusDescription;


        private MyViewHolder(View view) {
            super(view);
            statusImage = (ImageView) view.findViewById(R.id.order_status_img);
            statusTitle = (TextView) view.findViewById(R.id.order_status_title);
            statusDescription = (TextView) view.findViewById(R.id.order_status_description);
            viewLine = (View) view.findViewById(R.id.view_line);
//            notificatioLayout.setOnClickListener(this);
        }

        public void onClick(View v) {
//            if (v.getId() == notificatioLayout.getId()) {
////                context.startActivity(new Intent(context, HotelViewActivity.class));
//                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
//            }
        }

    }


}
