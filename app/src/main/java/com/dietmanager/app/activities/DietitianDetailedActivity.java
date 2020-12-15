package com.dietmanager.app.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.R;
import com.dietmanager.app.adapter.SubscriptionPercentageAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.build.configure.BuildConfigure;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Message;
import com.dietmanager.app.models.dietitiandetail.DietitianDetailedResponse;
import com.dietmanager.app.models.dietitiandetail.SubscriptionItem;
import com.google.android.gms.common.util.CollectionUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DietitianDetailedActivity extends AppCompatActivity implements SubscriptionPercentageAdapter.ISubscriptionPercentageListener {


    @BindView(R.id.user_img)
    ImageView userImg;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvPhone)
    TextView mobileNo;
    @BindView(R.id.llPlan)
    LinearLayout llPlan;
    @BindView(R.id.tvMail)
    TextView tvMail;
    @BindView(R.id.follow_unfollow_btn)
    Button followUnfollowBtn;
    @BindView(R.id.tvSubscription)
    TextView tvSubscription;
    @BindView(R.id.tvAge)
    TextView tvAge;
    @BindView(R.id.tvExperience)
    TextView tvExperience;
    CustomDialog customDialog;
    @BindView(R.id.back_img)
    ImageView back;
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.subscription_percentage_rv)
    RecyclerView subscriptionPercentageRv;
    private ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    private DietitianDetailedResponse dietitianDetailedResponse;

    private int dietitianId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietitian_detailed);
        ButterKnife.bind(this);
        customDialog = new CustomDialog(DietitianDetailedActivity.this);
        //initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dietitianId = bundle.getInt("dietitianId");
        }
        getDietitianProfile(dietitianId);
        followUnfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followOrUnFollow();
            }
        });
    }

    public void followOrUnFollow() {
        customDialog.show();
        Call<Message> call = apiInterface.followOrUnFollow(dietitianId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(DietitianDetailedActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    getDietitianProfile(dietitianId);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(DietitianDetailedActivity.this, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(DietitianDetailedActivity.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(DietitianDetailedActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @OnClick({R.id.back_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
        }
    }

    public int calculateAge(Date birthdate) {
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthdate);
        Calendar today = Calendar.getInstance();

        int yearDifference = today.get(Calendar.YEAR)
                - birth.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH)) {
            yearDifference--;
        } else {
            if (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < birth
                    .get(Calendar.DAY_OF_MONTH)) {
                yearDifference--;
            }

        }

        return yearDifference;
    }

    private void initView() {
        if (dietitianDetailedResponse != null) {
            tvName.setText(dietitianDetailedResponse.getDietitian().getName());
            tvMail.setText(dietitianDetailedResponse.getDietitian().getEmail());
            mobileNo.setText(dietitianDetailedResponse.getDietitian().getMobile());
            tvAge.setText(String.valueOf(dietitianDetailedResponse.getDietitian().getDob()));
            if (dietitianDetailedResponse.getDietitian().getDescription() != null && !dietitianDetailedResponse.getDietitian().getDescription().isEmpty()) {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(dietitianDetailedResponse.getDietitian().getDescription());
            } else {
                tvDescription.setVisibility(View.GONE);
            }
            if (CollectionUtils.isEmpty(dietitianDetailedResponse.getDietitian().getFollowers())) {
                llPlan.setVisibility(View.GONE);
                followUnfollowBtn.setText(getString(R.string.follow));
                Drawable img = getResources().getDrawable(R.drawable.ic_follow);
                followUnfollowBtn.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            } else {
                llPlan.setVisibility(View.VISIBLE);
                followUnfollowBtn.setText(getString(R.string.unfollow));
                Drawable img = getResources().getDrawable(R.drawable.ic_unfollow);
                followUnfollowBtn.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            }
            if (dietitianDetailedResponse.getDietitian().getDob() != null) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date birthdate = null;
                try {
                    birthdate = df.parse(dietitianDetailedResponse.getDietitian().getDob());
                    tvAge.setText(String.valueOf(calculateAge(birthdate)));
                } catch (ParseException e) {
                    tvAge.setText(R.string.not_found);
                    e.printStackTrace();
                }
            } else {
                tvAge.setText(R.string.not_found);
            }

            if (dietitianDetailedResponse.getSubscription().size() > 0) {
                tvSubscription.setVisibility(View.VISIBLE);
                subscriptionPercentageRv.setVisibility(View.VISIBLE);
                SubscriptionPercentageAdapter adapter = new SubscriptionPercentageAdapter(this, dietitianDetailedResponse.getTotalSubscribers(), this);
                subscriptionPercentageRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                subscriptionPercentageRv.setHasFixedSize(true);
                subscriptionPercentageRv.setAdapter(adapter);
                adapter.setList(dietitianDetailedResponse.getSubscription());
            } else {
                subscriptionPercentageRv.setVisibility(View.GONE);
                tvSubscription.setVisibility(View.GONE);
            }

/*
            selectedDob=sdf.format(myCalendar.getTime());

            dob.setText(sdf.format(myCalendar.getTime()));*/

            if (dietitianDetailedResponse.getDietitian().getExperience() >= 1)
                tvExperience.setText(getString(R.string.count_years_experience, dietitianDetailedResponse.getDietitian().getExperience()));
            else
                tvExperience.setText(getString(R.string.no_experience));

            Glide.with(this)
                    .load(BuildConfigure.BASE_URL + dietitianDetailedResponse.getDietitian().getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.shimmer_bg)
                            .error(R.drawable.shimmer_bg))
                    .into(userImg);
        }
    }

    @Override
    public void onPercentageClicked(SubscriptionItem item) {
        if(item.getId()==GlobalData.subscription.getPlanId()||item.getAccessMethod().equalsIgnoreCase("PUBLIC")){
            Intent intent=new Intent(DietitianDetailedActivity.this,DietitianMealPlanActivity.class);
            intent.putExtra("planId",item.getId());
            intent.putExtra("planNoOfDays",item.getNoOfDays());
            intent.putExtra("planName",item.getTitle());
            startActivity(intent);
        }
        else {
            Toast.makeText(DietitianDetailedActivity.this,"This is private plan. Please subscribe to view",Toast.LENGTH_LONG).show();
        }
    }

    private void getDietitianProfile(int dietitianId) {
        customDialog.show();
        Call<DietitianDetailedResponse> call = apiInterface.getDietitianDetailed(dietitianId);
        call.enqueue(new Callback<DietitianDetailedResponse>() {
            @Override
            public void onResponse(@NonNull Call<DietitianDetailedResponse> call,
                                   @NonNull Response<DietitianDetailedResponse> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    dietitianDetailedResponse = response.body();
                    initView();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DietitianDetailedResponse> call, @NonNull Throwable t) {
                customDialog.cancel();
                Toast.makeText(DietitianDetailedActivity.this, R.string.something_went_wrong,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
