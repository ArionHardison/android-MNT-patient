package com.dietmanager.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.CurrentFoodAdapter;
import com.dietmanager.app.adapter.FoodAdapter;
import com.dietmanager.app.adapter.SuitableFoodAdapter;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.food.FoodItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChangeFoodActivity extends AppCompatActivity {

    private SuitableFoodAdapter  suitableAdapter;
    private CurrentFoodAdapter currentAdapter;
    @BindView(R.id.rv_curfoods)
    RecyclerView rv_curfoods;
    @BindView(R.id.rv_suifoods)
    RecyclerView rv_suifoods;

    private List<FoodItem>  suitableItems = new ArrayList<>();
    private List<FoodItem>  currentItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_food);
        ButterKnife.bind(this);
        ((TextView)findViewById(R.id.toolbar).findViewById(R.id.title)).setText(R.string.change_food);
        findViewById(R.id.toolbar).findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (GlobalData.foodItemList!=null&& GlobalData.foodItemList.size()>0){
            //currentfood Adapter
            currentAdapter = new CurrentFoodAdapter(this);
            rv_curfoods.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rv_curfoods.setHasFixedSize(true);
            rv_curfoods.setAdapter(currentAdapter);
            currentAdapter.setList(GlobalData.foodItemList.get(0));

            //suitablefood Adapter
            suitableAdapter = new SuitableFoodAdapter(this);
            rv_suifoods.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rv_suifoods.setHasFixedSize(true);
            rv_suifoods.setAdapter(suitableAdapter);
            rv_suifoods.setItemAnimator(new DefaultItemAnimator());
            currentItems = GlobalData.foodItemList;
            currentItems.remove(0);
            suitableAdapter.setList(currentItems);


        }
    }
}