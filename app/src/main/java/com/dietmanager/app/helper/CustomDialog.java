package com.dietmanager.app.helper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.dietmanager.app.R;


public class CustomDialog extends /*ProgressDialog*/Dialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setIndeterminate(true);
        //setMessage("Please wait...");
        setCancelable(false);
        setContentView(R.layout.custom_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
}
