package com.dietmanager.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.IngredientAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.ChangePassword;
import com.dietmanager.app.models.food.FoodIngredient;
import com.dietmanager.app.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngredientsActivity  extends AppCompatActivity implements IngredientAdapter.ICheckclickListener {


    ConnectionHelper connectionHelper;
    Activity activity;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.tv_itemnamme)
    TextView tv_itemnamme;
    @BindView(R.id.next_btn)
    Button next_btn;
    @BindView(R.id.food_rv)
    RecyclerView ingredient_rv;
    IngredientAdapter ingredientAdapter;
    private List<FoodIngredient> foodIngredientList = new ArrayList<>();
    CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        ButterKnife.bind(this);
        ((TextView)findViewById(R.id.toolbar).findViewById(R.id.title)).setText(R.string.choose_your_ingredients);

        context = IngredientsActivity.this;
        activity = IngredientsActivity.this;
        connectionHelper = new ConnectionHelper(context);

        ingredientAdapter = new IngredientAdapter(foodIngredientList,this,this);
        ingredient_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ingredient_rv.setAdapter(ingredientAdapter);
        List<FoodIngredient> dataList = GlobalData.selectedfood.getFoodIngredients();
        if (dataList != null && !Utils.isNullOrEmpty(dataList)) {
            foodIngredientList.clear();
            foodIngredientList.addAll(dataList);
            ingredientAdapter.notifyDataSetChanged();

        }
        tv_itemnamme.setText( GlobalData.selectedfood.getName());

        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredientAdapter.getSelected().size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    HashMap<String, String> map = new HashMap<>();
                    double ingredienttotal= 0;
                    for (int i = 0; i < ingredientAdapter.getSelected().size(); i++) {
                        stringBuilder.append(ingredientAdapter.getSelected().get(i).getIngredient().getName());
                        stringBuilder.append("\n");
                        map.put("ingredient["+i+"]", ingredientAdapter.getSelected().get(i).getId().toString());
                        ingredienttotal= ingredienttotal + Double.valueOf(ingredientAdapter.getSelected().get(i).getPrice()).doubleValue();
                    }

                    //Toast.makeText(context, stringBuilder.toString().trim(), Toast.LENGTH_LONG).show();


                    map.put("food_id",  String.valueOf(GlobalData.selectedfood.getId()));
                    map.put("schedule_date", GlobalData.schedule_date);
                    map.put("schedule_time", GlobalData.schedule_time);
                    map.put("dietitian_id",  String.valueOf(GlobalData.selectedfood.getDietitian().getId()));
                    map.put("payable", String.valueOf(ingredienttotal));
                    placeorder(map);
                } else {
                    Toast.makeText(context, "No Selection", Toast.LENGTH_LONG).show();
                }
                //startActivity(new Intent(IngredientsActivity.this, HowYouLikeToOrderActivity.class));
            }
        });
        findViewById(R.id.toolbar).findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void placeorder(HashMap<String, String> map) {
        customDialog = new CustomDialog(context);
        customDialog.show();
        Call<ChangePassword> call = apiInterface.placeorder(map);
        call.enqueue(new Callback<ChangePassword>() {
            @Override
            public void onResponse(@NonNull Call<ChangePassword> call, @NonNull Response<ChangePassword> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("password"))
                            Toast.makeText(context, jObjError.optJSONArray("password").get(0).toString(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChangePassword> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(context, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onClicked(int day) {
        if (ingredientAdapter.getSelected().size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, String> map = new HashMap<>();
            double ingredienttotal= Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();
            stringBuilder.append(GlobalData.selectedfood.getName());
            for (int i = 0; i < ingredientAdapter.getSelected().size(); i++) {
                stringBuilder.append("+");
                stringBuilder.append(ingredientAdapter.getSelected().get(i).getIngredient().getName());
                //stringBuilder.append("\n");
                map.put("ingredient["+i+"]", ingredientAdapter.getSelected().get(i).getId().toString());
                ingredienttotal= ingredienttotal + Double.valueOf(ingredientAdapter.getSelected().get(i).getPrice()).doubleValue();
            }
            tv_itemnamme.setText(stringBuilder.toString().trim());
            next_btn.setText(getString(R.string.total)+"-"+ingredienttotal);
        } else {
            next_btn.setText(getString(R.string.next));
            tv_itemnamme.setText(GlobalData.selectedfood.getName());
        }
    }
}
