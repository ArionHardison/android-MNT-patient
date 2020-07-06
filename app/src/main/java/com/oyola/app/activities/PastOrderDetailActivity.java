package com.oyola.app.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oyola.app.R;
import com.oyola.app.fragments.OrderViewFragment;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.models.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.oyola.app.helper.GlobalData.isSelectedOrder;

public class PastOrderDetailActivity extends AppCompatActivity {

    Fragment orderFullViewFragment;
    FragmentManager fragmentManager;

    double priceAmount = 0;
    int discount = 0;
    int itemCount = 0;
    int itemQuantity = 0;
    String currency = "";
    @BindView(R.id.order_id_txt)
    TextView orderIdTxt;
    @BindView(R.id.order_item_txt)
    TextView orderItemTxt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.source_image)
    ImageView sourceImage;
    @BindView(R.id.restaurant_name)
    TextView restaurantName;
    @BindView(R.id.restaurant_address)
    TextView restaurantAddress;
    @BindView(R.id.source_layout)
    RelativeLayout sourceLayout;
    @BindView(R.id.destination_image)
    ImageView destinationImage;
    @BindView(R.id.user_address_title)
    TextView userAddressTitle;
    @BindView(R.id.user_address)
    TextView userAddress;
    @BindView(R.id.destination_layout)
    RelativeLayout destinationLayout;
    @BindView(R.id.view_line2)
    View viewLine2;
    @BindView(R.id.order_succeess_image)
    ImageView orderSucceessImage;
    @BindView(R.id.order_status_txt)
    TextView orderStatusTxt;
    @BindView(R.id.order_status_layout)
    RelativeLayout orderStatusLayout;
    @BindView(R.id.order_detail_fargment)
    FrameLayout orderDetailFargment;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.dot_line_img)
    ImageView dotLineImg;
    @BindView(R.id.doted_line_layout)
    RelativeLayout layoutDotLine;
    @BindView(R.id.order_type)
    TextView orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        //Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (isSelectedOrder != null) {
            Order order = GlobalData.isSelectedOrder;
            orderIdTxt.setText(R.string.order_details_page + " #000" + order.getId().toString());
            itemQuantity = order.getInvoice().getQuantity();
            priceAmount = order.getInvoice().getPayable();
            if (order.getStatus().equalsIgnoreCase("CANCELLED")) {
                orderStatusTxt.setText(getResources().getString(R.string.order_cancelled));
                orderSucceessImage.setImageResource(R.drawable.order_cancelled_img);
                dotLineImg.setBackgroundResource(R.drawable.order_cancelled_line);
                orderStatusTxt.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
            } else {
                if (order.getOrdertiming() != null && order.getOrdertiming().size() > 0) {
                    for (int i = 0; i < order.getOrdertiming().size(); i++) {
                        if (order.getOrdertiming().get(i).getStatus().equalsIgnoreCase("COMPLETED")) {
                            orderStatusTxt.setText(getResources().getString(R.string.order_delivered_successfully_on) + " " + getFormatTime(order.getOrdertiming().get(i).getCreatedAt()));
                            break;
                        }
                    }
                }
                orderStatusTxt.setTextColor(ContextCompat.getColor(this, R.color.colorGreen));
                orderSucceessImage.setImageResource(R.drawable.ic_circle_tick);
                dotLineImg.setBackgroundResource(R.drawable.ic_line);
            }
            if (order.getItems() != null && order.getItems().size() > 0) {
                currency = order.getItems().get(0).getProduct().getPrices().getCurrency();
            }
            if (itemQuantity == 1)
                orderItemTxt.setText(itemQuantity + " " + getResources().getString(R.string.item_count) + " , " + currency + priceAmount);
            else
                orderItemTxt.setText(itemQuantity + " " + getResources().getString(R.string.items_counts) + " , " + currency + priceAmount);

            restaurantName.setText(order.getShop().getName());
            restaurantAddress.setText(order.getShop().getAddress());
            if (order.getPickUpRestaurant() != null) {
                if (order.getPickUpRestaurant() == 0) {
                    orderType.setText(getString(R.string.order_type_delivery));
                    if (order.getAddress() != null) {
                        userAddressTitle.setText(order.getAddress().getType());
                        userAddress.setText((order.getAddress().getBuilding() != null ? order.getAddress().getBuilding() + ", " : "") + order.getAddress().getMapAddress());
                    }
                } else if (order.getPickUpRestaurant() == 1) {
                    orderType.setText(getString(R.string.order_type_takeaway));
                    destinationLayout.setVisibility(View.GONE);
                    layoutDotLine.setVisibility(View.GONE);
                } else {
                    if (order.getAddress() != null) {
                        userAddressTitle.setText(order.getAddress().getType());
                        userAddress.setText((order.getAddress().getBuilding() != null ? order.getAddress().getBuilding() + ", " : "") + order.getAddress().getMapAddress());
                    }
                }
            }

            //set Fragment
            orderFullViewFragment = new OrderViewFragment();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.order_detail_fargment, orderFullViewFragment).commit();
        }

    }

    private String getFormatTime(String time) {
        System.out.println("Time : " + time);
        String value = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.getDefault());

            if (time != null) {
                Date date = df.parse(time);
                value = sdf.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();

        }
        return value;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }
}