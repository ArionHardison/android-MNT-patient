package com.oyola.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.oyola.app.HomeActivity;
import com.oyola.app.R;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tv_user_name)
    TextView userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (GlobalData.profileModel != null)
            userNameText.setText("Hi," + " " + GlobalData.profileModel.getName());
    }

    @OnClick({R.id.ll_use_current_location, R.id.tv_set_location_manually})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.ll_use_current_location:
                bundle.putBoolean(Constants.ENABLE_PLACES_AUTOCOMPLETE, false);
                break;

            case R.id.tv_set_location_manually:
                bundle.putBoolean(Constants.ENABLE_PLACES_AUTOCOMPLETE, true);
                break;

            default:
                break;
        }
        launchActivityForResult(SaveDeliveryLocationActivity.class, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("isFromSignUp", true);
            startActivity(intent);
            finish();
        }
    }
}
