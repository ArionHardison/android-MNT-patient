package com.dietmanager.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dietmanager.app.R;
import com.dietmanager.app.utils.Utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;

public class SubMenuDetailedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu_detailed);
        ButterKnife.bind(this);
        ((TextView)findViewById(R.id.toolbar).findViewById(R.id.title)).setText("Lunch");
        findViewById(R.id.order_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubMenuDetailedActivity.this, IngredientsActivity.class));
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

    public String schedule_time = "";
    public String schedule_date = "";
    Calendar myCalendar = Calendar.getInstance();

    private void showDialog() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_schedule_date, null);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        dialogView.findViewById(R.id.close_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.cancel();
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
                //setText(Utils.getDayAndTimeFormat(myCalendar.getTime()));
                dialogBuilder.cancel();
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