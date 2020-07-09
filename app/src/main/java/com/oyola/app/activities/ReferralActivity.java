package com.oyola.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oyola.app.R;
import com.oyola.app.helper.SharedHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Prasanth on 21-01-2020
 */
public class ReferralActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_referralcode)
    TextView txtReferralcode;
    String mReferralCode = "";
    String mReferralContent = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mReferralCode = SharedHelper.getKey(this, "referral_code");
        if (mReferralCode != null)
            txtReferralcode.setText(mReferralCode);
    }

    @OnClick({R.id.btn_invite})
    public void onCLick(View v) {
        switch (v.getId()) {
            case R.id.btn_invite:
                shareReferralCode(mReferralCode);
                break;
            default:
                break;
        }
    }

    private void shareReferralCode(String mCode) {
        try {
            String appName = getString(R.string.app_name);
            mReferralContent = getString(R.string.referrral_content, appName, mCode);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mReferralContent);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
        } catch (Exception e) {
            Toast.makeText(this, "applications not found!", Toast.LENGTH_SHORT).show();
        }
    }
}
