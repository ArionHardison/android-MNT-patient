package com.dietmanager.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.DietitianListAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.models.dietitianlist.DietitianListItem;
import com.dietmanager.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DietitianListActivity extends AppCompatActivity implements DietitianListAdapter.IDietitianListener {

    @BindView(R.id.dietitian_list_rv)
    RecyclerView dietitianListRv;
    @BindView(R.id.llNoRecords)
    LinearLayout llNoRecords;
    @BindView(R.id.mNestedScrollView)
    NestedScrollView mNestedScrollView;
    CustomDialog customDialog;

    @BindView(R.id.search_et)
    EditText etSearch;
    private List<DietitianListItem> dietitianItems = new ArrayList<>();
    private DietitianListAdapter dietitianListAdapter;
    private Context context;
    private Activity activity;
    private ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private int orderId;
    private boolean first = true;

    private int visibleThreshold = 5;
    private int currentPage = 1;
    private int previousTotalItemCount = 0;
    private boolean loading = false;
    private int assetId = 0;
    String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietitian_list);
        customDialog = new CustomDialog(this);
        dietitianItems.clear();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderId = bundle.getInt("order_id");
        }
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.toolbar).findViewById(R.id.title)).setText(R.string.dietitians);
        findViewById(R.id.toolbar).findViewById(R.id.back).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar).findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setupAdapter();

        customDialog.show();
        getAssignChefList(false);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                currentPage = 1;
                /*new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dietitianItems.clear();*/
                        getAssignChefList(true);
                    /*}
                }, 1000);*/

                //dietitianListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        dietitianListRv.setLayoutManager(llm);

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY >= v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight() && scrollY > oldScrollY) {
                        int lastVisibleItemPosition = llm.findLastVisibleItemPosition();
                        int totalItemCount = llm.getItemCount();
                        if (loading && totalItemCount > previousTotalItemCount) {
                            loading = false;
                            previousTotalItemCount = totalItemCount;
                        }
                        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount && dietitianListRv.getAdapter().getItemCount() > visibleThreshold) { // This condition will useful when recyclerview has less than visibleThreshold items
                            currentPage++;
                            loading = true;
                            mNestedScrollView.fullScroll(View.FOCUS_DOWN);
                            customDialog.show();
                            getAssignChefList(false);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDietitianClicked(DietitianListItem dietitianListItem) {
        Intent intent=new Intent(DietitianListActivity.this,DietitianDetailedActivity.class);
        intent.putExtra("dietitianId",dietitianListItem.getId());
        startActivity(intent);
    }

    private void setupAdapter() {
        dietitianListAdapter = new DietitianListAdapter(dietitianItems, this, this);
        dietitianListRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        dietitianListRv.setHasFixedSize(true);
        dietitianListRv.setAdapter(dietitianListAdapter);
    }

    private void getAssignChefList(final boolean clear) {
        Call<List<DietitianListItem>> call = apiInterface.getDietitianList(currentPage, searchText);
        call.enqueue(new Callback<List<DietitianListItem>>() {
            @Override
            public void onResponse(Call<List<DietitianListItem>> call, Response<List<DietitianListItem>> response) {
                loading = false;
                if (customDialog.isShowing())
                    customDialog.dismiss();
                if (response.isSuccessful()) {
                    if (clear)
                        dietitianItems.clear();
                    List<DietitianListItem> subscribedModel = response.body();
                    if (subscribedModel != null) {
                        if (subscribedModel.size() > 0) {
                            llNoRecords.setVisibility(View.GONE);
                            dietitianListRv.setVisibility(View.VISIBLE);
                            dietitianItems.addAll(subscribedModel);
                            dietitianListAdapter.setList(dietitianItems);
                        }
                        dietitianListAdapter.notifyDataSetChanged();

                        /*else {
                            llNoRecords.setVisibility(View.VISIBLE);
                            dietitianListRv.setVisibility(View.GONE);
                        }*/
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DietitianListItem>> call, Throwable t) {
                loading = false;
                if (customDialog.isShowing())
                    customDialog.dismiss();
                Utils.displayMessage(DietitianListActivity.this, DietitianListActivity.this, getString(R.string.something_went_wrong));
            }
        });
    }

}