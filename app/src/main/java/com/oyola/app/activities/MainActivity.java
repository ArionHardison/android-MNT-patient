package com.oyola.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.oyola.app.R;
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

        initializeViewReferences();
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
        launchActivity(LocationPickActivity.class, bundle);
    }

    private void initializeViewReferences() {
        ButterKnife.bind(this);
    }
}
