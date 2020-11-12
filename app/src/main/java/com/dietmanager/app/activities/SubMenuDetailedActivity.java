package com.dietmanager.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.R;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.food.FoodItem;
import com.dietmanager.app.utils.Utils;

import java.lang.reflect.Field;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dietmanager.app.build.configure.BuildConfigure.BASE_URL;

public class SubMenuDetailedActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.img_food)
    ImageView img_food;
    public String schedule_time = "";
    public String schedule_date = "";
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu_detailed);
        ButterKnife.bind(this);
        if (GlobalData.selectedfood!=null){
            FoodItem foodItem = GlobalData.selectedfood;
            ((TextView)findViewById(R.id.toolbar).findViewById(R.id.title)).setText(GlobalData.selectedTimeCategoryName);
            tv_name.setText(foodItem.getName());
            tv_description.setText(foodItem.getDescription());
            if (foodItem.getAvatar()!=null)
                Glide.with(this)
                        .load(BASE_URL +foodItem.getAvatar())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_banner_1)
                                .error(R.drawable.ic_banner_1))
                        .into(img_food);
        }

        findViewById(R.id.order_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (schedule_date.equalsIgnoreCase("")){
                    Utils.displayMessage(SubMenuDetailedActivity.this, SubMenuDetailedActivity.this, getString(R.string.please_select_date_));
                }else   if (schedule_time.equalsIgnoreCase("")){
                    Utils.displayMessage(SubMenuDetailedActivity.this, SubMenuDetailedActivity.this, getString(R.string.please_select_time));
                }else*/ {
                    GlobalData.schedule_date = schedule_date;
                    GlobalData.schedule_time = schedule_time;
                    startActivity(new Intent(SubMenuDetailedActivity.this, IngredientsActivity.class));
                }
            }
        });findViewById(R.id.schedule_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        findViewById(R.id.toolbar).findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void showDialog() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_schedule_date, null);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        TextView tv_date = dialogView.findViewById(R.id.tv_date);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, datePicker.getYear());
                myCalendar.set(Calendar.MONTH, datePicker.getMonth());
                myCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                myCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                myCalendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                String date = Utils.getDayAndTimeFormat(myCalendar.getTime());
                tv_date.setText(date);
            }
        });

      /*  try {
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f) {
                if (field.getName().equals("mYearPicker")) {
                    field.setAccessible(true);
                    Object yearPicker = new Object();
                    yearPicker = field.get(datePicker);
                    ((View) yearPicker).setVisibility(View.GONE);
                }
            }
        }
        catch (SecurityException e) {
            Log.d("ERROR", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            Log.d("ERROR", e.getMessage());
        }
        catch (IllegalAccessException e) {
            Log.d("ERROR", e.getMessage());
        }*/
        dialogView.findViewById(R.id.close_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.cancel();
            }
        });


        myCalendar.set(Calendar.YEAR, datePicker.getYear());
        myCalendar.set(Calendar.MONTH, datePicker.getMonth());
        myCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        myCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        myCalendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        String date = Utils.getDayAndTimeFormat(myCalendar.getTime());
        tv_date.setText(date);

        datePicker.init(datePicker.getYear(),  datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, datePicker.getYear());
                myCalendar.set(Calendar.MONTH, datePicker.getMonth());
                myCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                myCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                myCalendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                String date = Utils.getDayAndTimeFormat(myCalendar.getTime());
                tv_date.setText(date);
            }

        });
        dialogView.findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCalendar.set(Calendar.YEAR, datePicker.getYear());
                myCalendar.set(Calendar.MONTH, datePicker.getMonth());
                myCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                myCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                myCalendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                Calendar currentTimeTwoHours = Calendar.getInstance();
                currentTimeTwoHours.add(Calendar.MINUTE, -1);
                if (myCalendar.getTimeInMillis() < currentTimeTwoHours.getTimeInMillis()) {
                    Utils.displayMessage(SubMenuDetailedActivity.this, SubMenuDetailedActivity.this, getString(R.string.invalid_date_time));
                    return;
                }
                Format f = new SimpleDateFormat("HH:mm:ss");
                schedule_time = f.format(myCalendar.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                schedule_date = sdf.format(myCalendar.getTime());
                dialogBuilder.cancel();
                GlobalData.schedule_date = schedule_date;
                GlobalData.schedule_time = schedule_time;
                startActivity(new Intent(SubMenuDetailedActivity.this, IngredientsActivity.class));

            }
        });
        dialogBuilder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
        /*final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();*/

    }

}