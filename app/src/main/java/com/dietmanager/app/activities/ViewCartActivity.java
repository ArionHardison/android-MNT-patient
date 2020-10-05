package com.dietmanager.app.activities;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.R;
import com.dietmanager.app.fragments.CartFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewCartActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        ButterKnife.bind(this);
        setTitle(getString(R.string.view_cart_txt));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        fragmentManager = getSupportFragmentManager();
        fragment = new CartFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean("show_toolbar",true);
        fragment.setArguments(bundle);
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.view_cart_container, fragment).commit();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
        finish();
    }
}
