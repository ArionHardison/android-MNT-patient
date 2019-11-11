package com.oyola.app.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.oyola.app.R;
import com.oyola.app.activities.AccountPaymentActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.oyola.app.fragments.CartFragment.checkoutMap;

/**
 * Created by Prasanth on 23-10-2019.
 */
public class OrderDeliveryTypeFragment extends BottomSheetDialogFragment {

    @BindView(R.id.layout_order_time)
    LinearLayout mLayOrderTime;
    @BindView(R.id.layout_schedule)
    LinearLayout mLaySchedule;
    @BindView(R.id.imgBack)
    ImageView mImgBack;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.txt_delivery_content)
    TextView mTxtDeliveryContent;
    private String mRestaurantType = "";
    private Integer mEstimatedDeliveryTime = 0;
    Context mContext;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat mSimpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SimpleDateFormat mServerTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    Calendar myCalendar;

    public static OrderDeliveryTypeFragment newInstance(String mType) {
        OrderDeliveryTypeFragment typeFragment = new OrderDeliveryTypeFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("deliveryType", mType);
        typeFragment.setArguments(args);

        return typeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRestaurantType = getArguments().getString("deliveryType");
        mEstimatedDeliveryTime = getArguments().getInt("estDeliveryTime");
        mContext = getContext();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_order_delivery_type, null);
        dialog.setContentView(contentView);
        ButterKnife.bind(this, contentView);
        myCalendar = Calendar.getInstance();
        myCalendar.setTimeInMillis(System.currentTimeMillis() + 30 * 60 * 1000);
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                date.setText(mSimpleDateFormat.format(myCalendar.getTime()));
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                myCalendar.set(Calendar.MINUTE, selectedMinute);
                if (myCalendar.getTimeInMillis() > System.currentTimeMillis()) {
                    time.setText(mSimpleTimeFormat.format(myCalendar.getTime()));
                } else {
                    Toast.makeText(mContext, "Please select future time", Toast.LENGTH_SHORT).show();
                }
            }
        };


        date.setText(mSimpleDateFormat.format(myCalendar.getTime()));
        time.setText(mSimpleTimeFormat.format(myCalendar.getTime()));

        if (mRestaurantType.equalsIgnoreCase("PICKUP")) {
            mTxtDeliveryContent.setVisibility(View.VISIBLE);
        } else {
            mTxtDeliveryContent.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.schedule_btn, R.id.asap_btn, R.id.tvClose, R.id.imgBack, R.id.date, R.id.time, R.id.btn_done})
    public void onCLick(View v) {
        switch (v.getId()) {
            case R.id.schedule_btn:
                mImgBack.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_back));
                mLayOrderTime.setVisibility(View.GONE);
                mLaySchedule.setVisibility(View.VISIBLE);
                break;
            case R.id.asap_btn:
                dismiss();
                if (mRestaurantType.equalsIgnoreCase("PICKUP")) {
                    checkoutMap.put("pickup_from_restaurants", "1");
                    callPaymentActivity(true);
                } else if (mRestaurantType.equalsIgnoreCase("DELIVERY")) {
                    callPaymentActivity(true);
                }

                break;
            case R.id.tvClose:
            case R.id.imgBack:
                if (mLaySchedule.getVisibility() == View.VISIBLE) {
                    mLayOrderTime.setVisibility(View.VISIBLE);
                    mLaySchedule.setVisibility(View.GONE);
//                    mImgBack.setImageResource(R.drawable.ic_close_black);
                    mImgBack.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_close_black));
                } else if (mLayOrderTime.getVisibility() == View.VISIBLE) {
                    dismiss();
                }
                break;
            case R.id.date:
//                datePicker(dateSetListener);
                break;
            case R.id.time:
                timePicker(timeSetListener);
                break;
            case R.id.btn_done:
                dismiss();
                String mSelectedTime = mServerTimeFormat.format(myCalendar.getTime());
                if (mRestaurantType.equalsIgnoreCase("PICKUP")) {
                    checkoutMap.put("pickup_from_restaurants", "1");
                    checkoutMap.put("delivery_date", mSelectedTime);
                    callPaymentActivity(false);
                } else if (mRestaurantType.equalsIgnoreCase("DELIVERY")) {
                    checkoutMap.put("delivery_date", mSelectedTime);
                    callPaymentActivity(false);
                }
                break;
        }
    }

    private void callPaymentActivity(boolean mIsImmediate) {
        startActivity(new Intent(mContext, AccountPaymentActivity.class)
                .putExtra("is_show_wallet", false)
                .putExtra("delivery_type", mRestaurantType)
                .putExtra("est_delivery_time", mEstimatedDeliveryTime)
                .putExtra("is_immediate", mIsImmediate)
                .putExtra("is_show_cash", true));
    }

    public void datePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    public void timePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }
}
