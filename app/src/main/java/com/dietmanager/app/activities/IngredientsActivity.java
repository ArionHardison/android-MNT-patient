package com.dietmanager.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.IngredientAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.food.FoodIngredient;
import com.dietmanager.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsActivity  extends AppCompatActivity {


    ConnectionHelper connectionHelper;
    Activity activity;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.food_rv)
    RecyclerView ingredient_rv;
    IngredientAdapter ingredientAdapter;
    private List<FoodIngredient> foodIngredientList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        ButterKnife.bind(this);
        ((TextView)findViewById(R.id.toolbar).findViewById(R.id.title)).setText(R.string.choose_your_ingredients);

        context = IngredientsActivity.this;
        activity = IngredientsActivity.this;
        connectionHelper = new ConnectionHelper(context);

        ingredientAdapter = new IngredientAdapter(foodIngredientList,this);
        ingredient_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ingredient_rv.setAdapter(ingredientAdapter);
        List<FoodIngredient> dataList = GlobalData.selectedfood.getFoodIngredients();
        if (dataList != null && !Utils.isNullOrEmpty(dataList)) {
            foodIngredientList.clear();
            foodIngredientList.addAll(dataList);
            ingredientAdapter.notifyDataSetChanged();
        }

        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredientAdapter.getSelected().size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < ingredientAdapter.getSelected().size(); i++) {
                        stringBuilder.append(ingredientAdapter.getSelected().get(i).getIngredient().getName());
                        stringBuilder.append("\n");
                    }

                    Toast.makeText(context, stringBuilder.toString().trim(), Toast.LENGTH_LONG).show();
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
    }


}
