package com.dietmanager.app.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.AccountPaymentActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.dietmanager.app.fragments.CartFragment.checkoutMap;
import static com.dietmanager.app.fragments.CartFragment.locationInfoLayout;

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
    TextView tvDate;
    @BindView(R.id.time)
    TextView tvTime;
    @BindView(R.id.txt_delivery_content)
    TextView mTxtDeliveryContent;

    private String mRestaurantType = "";
    private Integer mEstimatedDeliveryTime = 0;
    private Context mContext;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat mSimpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Calendar myCalendar;
    private BottomListener listener;
    private CountDownTimer countDownTimer;

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
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tvDate.setText(mSimpleDateFormat.format(myCalendar.getTime()));
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 2);
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);

                if (calendar.getTimeInMillis() > myCalendar.getTimeInMillis()) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    myCalendar.setTimeInMillis(calendar.getTimeInMillis());
                    tvTime.setText(mSimpleTimeFormat.format(calendar.getTime()));
                } else {
                    Toast.makeText(mContext, "Please select future time", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (mRestaurantType.equalsIgnoreCase("PICKUP")) {
            mTxtDeliveryContent.setVisibility(View.VISIBLE);
        } else {
            mTxtDeliveryContent.setVisibility(View.GONE);
        }
    }

    //this method is used to refresh Time every Second
    private void refreshTime() //Call this method to refresh time
    {
        countDownTimer = new CountDownTimer(1000000000, 1000) {

            public void onTick(long millisUntilFinished) {
                myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.HOUR_OF_DAY, myCalendar.get(Calendar.HOUR_OF_DAY) + 2);
                String date = mSimpleDateFormat.format(myCalendar.getTime());
                String time = mSimpleTimeFormat.format(myCalendar.getTime());
                tvDate.setText(date);
                tvTime.setText(time);
            }

            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    @OnClick({R.id.schedule_btn, R.id.asap_btn, R.id.tvClose, R.id.imgBack, R.id.date, R.id.time, R.id.btn_done})
    public void onCLick(View v) {
        switch (v.getId()) {
            case R.id.schedule_btn:
                mImgBack.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_back));
                mLayOrderTime.setVisibility(View.GONE);
                mLaySchedule.setVisibility(View.VISIBLE);
                refreshTime();
                break;
            case R.id.asap_btn:
                dismiss();
                if (locationInfoLayout != null) {
                    locationInfoLayout.setVisibility(View.GONE);
                }
                if (mRestaurantType.equalsIgnoreCase("PICKUP")) {
                    checkoutMap.put("pickup_from_restaurants", "1");
                    callPaymentActivity(true);
                } else if (mRestaurantType.equalsIgnoreCase("DELIVERY")) {
                    checkoutMap.put("pickup_from_restaurants", "0");
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
                    if (listener != null) {
                        listener.onCancelClick();
                    }
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
                if (locationInfoLayout != null) {
                    locationInfoLayout.setVisibility(View.GONE);
                }
                String mSelectedDate = mSimpleDateFormat.format(myCalendar.getTime());
                String mSelectedTime = mSimpleTimeFormat.format(myCalendar.getTime());
                if (mRestaurantType.equalsIgnoreCase("PICKUP")) {
                    checkoutMap.put("pickup_from_restaurants", "1");
                    checkoutMap.put("delivery_date", mSelectedDate);
                    checkoutMap.put("delivery_time", mSelectedTime);
                    callPaymentActivity(false);
                    Log.e("PICKUP Params", checkoutMap.toString());
                } else if (mRestaurantType.equalsIgnoreCase("DELIVERY")) {
                    checkoutMap.put("pickup_from_restaurants", "0");
                    checkoutMap.put("delivery_date", mSelectedDate);
                    checkoutMap.put("delivery_time", mSelectedTime);
                    Log.e("DELIVERY Params", checkoutMap.toString());
                    callPaymentActivity(false);
                }
                break;
        }
    }

    private void callPaymentActivity(boolean mIsImmediate) {
        //{"delivery_date":"2020-07-23","wallet":0,"pickup_from_restaurants":1,"note":"",
        // "user_address_id":35,"card_id":35,"delivery_time":"15:25","payment_mode":"STRIPE"}
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
        myCalendar.set(Calendar.HOUR_OF_DAY, myCalendar.get(Calendar.HOUR_OF_DAY) + 2);
        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }

    public void setListener(BottomListener listener) {
        this.listener = listener;
    }

    public interface BottomListener {
        void onCancelClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
