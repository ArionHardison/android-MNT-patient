package com.dietmanager.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dietmanager.app.HomeActivity;
import com.dietmanager.app.R;
import com.dietmanager.app.adapter.IngredientAdapter;
import com.dietmanager.app.build.api.ApiClient;
import com.dietmanager.app.build.api.ApiInterface;
import com.dietmanager.app.helper.ConnectionHelper;
import com.dietmanager.app.helper.CustomDialog;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Address;
import com.dietmanager.app.models.AddressList;
import com.dietmanager.app.models.CAddress;
import com.dietmanager.app.models.ChangePassword;
import com.dietmanager.app.models.CustomerAddress;
import com.dietmanager.app.models.PlaceOrderResponse;
import com.dietmanager.app.models.food.FoodIngredient;
import com.dietmanager.app.models.ingredients.Ingredient;
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

import static com.dietmanager.app.helper.GlobalData.selectedAddress;

public class IngredientsActivity  extends AppCompatActivity implements IngredientAdapter.ICheckclickListener {


    ConnectionHelper connectionHelper;
    Activity activity;
    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.tv_itemnamme)
    TextView tv_itemnamme;
    @BindView(R.id.tv_total)
    TextView tv_total;
    @BindView(R.id.next_btn)
    Button next_btn;
    @BindView(R.id.food_rv)
    RecyclerView ingredient_rv;
    IngredientAdapter ingredientAdapter;
    private List<FoodIngredient> foodIngredientList = new ArrayList<>();
    CustomDialog customDialog;

    @BindView(R.id.add_address_txt)
    TextView addAddressTxt;
    @BindView(R.id.address_header)
    TextView addressHeader;
    @BindView(R.id.address_detail)
    TextView addressDetail;
    @BindView(R.id.address_delivery_time)
    TextView addressDeliveryTime;
    @BindView(R.id.add_address_btn)
    Button addAddressBtn;
    @BindView(R.id.location_error_sub_title)
    TextView locationErrorSubTitle;
    @BindView(R.id.selected_address_btn)
    Button selectedAddressBtn;
    @BindView(R.id.location_error_layout)
    RelativeLayout locationErrorLayout;
    @BindView(R.id.location_info_layout)
    LinearLayout locationInfoLayout;
    @BindView(R.id.layout_order_type)
    LinearLayout layoutOrderType;

    boolean isActivityResultCalled = false;

    int ADDRESS_SELECTION = 1;

    List<AddressList> modelList = new ArrayList<>();

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

        //getCustomerAddresses();
        initializeAddressDetails();
        if (ingredientAdapter.getSelected().size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, String> map = new HashMap<>();
            double ingredienttotal= Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();
            double ingredienttotalWithTax= Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();

            stringBuilder.append(GlobalData.selectedfood.getName());
            for (int i = 0; i < ingredientAdapter.getSelected().size(); i++) {
                stringBuilder.append("+");
                stringBuilder.append(ingredientAdapter.getSelected().get(i).getIngredient().getName());
                //stringBuilder.append("\n");
                map.put("ingredient["+i+"]", ingredientAdapter.getSelected().get(i).getId().toString());
                ingredienttotal= ingredienttotal + Double.valueOf(ingredientAdapter.getSelected().get(i).getIngredient().getPrice()).doubleValue();
            }
            ingredienttotalWithTax=ingredienttotal+ingredienttotal*Double.parseDouble(GlobalData.profileModel.getDietTax())/100;

            tv_itemnamme.setText(stringBuilder.toString().trim());
            tv_total.setText(GlobalData.currency +" "+ingredienttotalWithTax);
        } else {
            double totalWithTax= Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();
            totalWithTax=totalWithTax+totalWithTax*Double.valueOf(GlobalData.profileModel.getDietTax()).doubleValue()/100;
            tv_itemnamme.setText(GlobalData.selectedfood.getName());
            tv_total.setText(GlobalData.currency + " " + totalWithTax);
        }

        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (ingredientAdapter.getSelected().size() > 0) {
                    if (GlobalData.selectedAddress != null) {
                        GlobalData.orderMap.clear();

                        StringBuilder stringBuilder = new StringBuilder();
                        HashMap<String, String> map = new HashMap<>();
                        double ingredienttotal = Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();
                        double ingredienttotalWithTax= Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();

                        for (int i = 0; i < ingredientAdapter.getSelected().size(); i++) {
                            stringBuilder.append(ingredientAdapter.getSelected().get(i).getIngredient().getName());
                            stringBuilder.append("\n");
                            GlobalData.orderMap.put("ingredient[" + i + "]", String.valueOf(ingredientAdapter.getSelected().get(i).getIngredient().getId()));
                            //map.put("ingredient[" + i + "]", String.valueOf(ingredientAdapter.getSelected().get(i).getIngredient().getId()));
                            ingredienttotal = ingredienttotal + Double.valueOf(ingredientAdapter.getSelected().get(i).getIngredient().getPrice()).doubleValue();

                        }
                        ingredienttotalWithTax=ingredienttotal+ingredienttotal*Double.parseDouble(GlobalData.profileModel.getDietTax())/100;

                        //Toast.makeText(context, stringBuilder.toString().trim(), Toast.LENGTH_LONG).show();


                        GlobalData.orderMap.put("food_id", String.valueOf(GlobalData.selectedfood.getId()));
                        GlobalData.orderMap.put("date", GlobalData.schedule_date + " " + GlobalData.schedule_time);
                        //map.put("schedule_date", GlobalData.schedule_date);
                        //map.put("schedule_time", GlobalData.schedule_time);
                        GlobalData.orderMap.put("dietitian_id", String.valueOf(GlobalData.selectedfood.getDietitian().getId()));
                        GlobalData.orderMap.put("delivery_address_id", "" + GlobalData.selectedAddress.getId());
                        GlobalData.orderMap.put("payable", String.valueOf(ingredienttotalWithTax));
                        //placeorder(map);


                        startActivity(new Intent(IngredientsActivity.this, AccountPaymentActivity.class)
                                .putExtra("is_show_wallet", true)
                                .putExtra("is_show_cash", false));
                    }else {
                        Toast.makeText(context, getString(R.string.add_address_error), Toast.LENGTH_LONG).show();
                    }
                /*} else {
                    Toast.makeText(context, "No Selection", Toast.LENGTH_LONG).show();
                }*/
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
        findViewById(R.id.add_address_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addAddressTxt.getText().toString().equalsIgnoreCase(getResources().getString(R.string.change_address))) {
                    startActivityForResult(new Intent(context, SetDeliveryLocationActivity.class).putExtra("get_address", true).putExtra("isCustomer_address", true), ADDRESS_SELECTION);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
                /**  If address is filled */
                else if (addAddressTxt.getText().toString().equalsIgnoreCase(getResources().getString(R.string.add_address))) {
                    startActivityForResult(new Intent(context, SaveDeliveryLocationActivity.class).putExtra("get_address", true).putExtra("isCustomer_address", true), ADDRESS_SELECTION);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
            }
        });
        findViewById(R.id.add_address_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, SaveDeliveryLocationActivity.class).putExtra("get_address", true).putExtra("isCustomer_address", true), ADDRESS_SELECTION);
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });
        findViewById(R.id.selected_address_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, SetDeliveryLocationActivity.class).putExtra("get_address", true).putExtra("isCustomer_address", true), ADDRESS_SELECTION);
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
            }
        });
    }

    private void placeorder(HashMap<String, String> map) {
        customDialog = new CustomDialog(context);
        customDialog.show();
        Call<PlaceOrderResponse> call = apiInterface.placeorder(map);
        call.enqueue(new Callback<PlaceOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlaceOrderResponse> call, @NonNull Response<PlaceOrderResponse> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("isFromSignUp", false);
                    startActivity(intent);
                    finishAffinity();*/

                    startActivity(new Intent(context, OrdersActivity.class));

//                    startActivity(new Intent(context, CurrentOrderDetailActivity.class).putExtra("orderId",response.body().getOrderId()));

                    //finish();
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
            public void onFailure(@NonNull Call<PlaceOrderResponse> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(context, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initializeAddressDetails() {
//        if (GlobalData.selectedAddress != null && GlobalData.selectedAddress.getLandmark() != null) {
        if (GlobalData.selectedAddress != null) {
            /*if (GlobalData.addressList.getAddresses().size() == 0)
                addAddressTxt.setText(getString(R.string.add_address));
            else*/
                addAddressTxt.setText(getString(R.string.change_address));
//            addAddressBtn.setBackgroundResource(R.drawable.button_corner_bg_green);
//            addAddressBtn.setText(getResources().getString(R.string.proceed_to_pay));
            locationErrorLayout.setVisibility(View.GONE);
            locationInfoLayout.setVisibility(View.VISIBLE);
            layoutOrderType.setVisibility(View.GONE);
            addressHeader.setText(GlobalData.selectedAddress.getType());
            addressDetail.setText((selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") +
                    GlobalData.selectedAddress.getMapAddress());
            /*if (viewCartItemList != null && viewCartItemList.size() != 0)
                addressDeliveryTime.setText(viewCartItemList.get(0).getProduct().getShop().getEstimatedDeliveryTime().toString() + " Mins");*/
        } else if (GlobalData.addressList != null) {
            addAddressBtn.setBackgroundResource(R.drawable.button_curved);
            addAddressBtn.setText(getResources().getString(R.string.add_address));
            locationErrorSubTitle.setText(GlobalData.addressHeader);
            selectedAddressBtn.setVisibility(View.VISIBLE);
            locationErrorLayout.setVisibility(View.VISIBLE);
            locationInfoLayout.setVisibility(View.GONE);
        } else {
            if (GlobalData.selectedAddress != null)
                locationErrorSubTitle.setText((selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") +
                        GlobalData.selectedAddress.getMapAddress());
            else
                locationErrorSubTitle.setText(GlobalData.addressHeader);
            locationErrorLayout.setVisibility(View.VISIBLE);
            selectedAddressBtn.setVisibility(View.GONE);
            locationInfoLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClicked(int day) {
        if (ingredientAdapter.getSelected().size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, String> map = new HashMap<>();
            double ingredienttotal= Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();
            double ingredienttotalWithTax= Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();
            stringBuilder.append(GlobalData.selectedfood.getName());
            for (int i = 0; i < ingredientAdapter.getSelected().size(); i++) {
                stringBuilder.append("+");
                stringBuilder.append(ingredientAdapter.getSelected().get(i).getIngredient().getName());
                //stringBuilder.append("\n");
                map.put("ingredient["+i+"]", ingredientAdapter.getSelected().get(i).getId().toString());
                ingredienttotal= ingredienttotal + Double.valueOf(ingredientAdapter.getSelected().get(i).getQuantity()*ingredientAdapter.getSelected().get(i).getIngredient().getPrice()).doubleValue();
            }
            ingredienttotalWithTax=ingredienttotal+ingredienttotal*Double.parseDouble(GlobalData.profileModel.getDietTax())/100;

            tv_itemnamme.setText(stringBuilder.toString().trim());
            tv_total.setText(GlobalData.currency +" "+ingredienttotalWithTax);
        } else {
            double totalWithTax= Double.valueOf(GlobalData.selectedfood.getPrice()).doubleValue();
            totalWithTax=totalWithTax+totalWithTax*Double.parseDouble(GlobalData.profileModel.getDietTax())/100;

            tv_total.setText(GlobalData.currency +" "+totalWithTax);
            tv_itemnamme.setText(GlobalData.selectedfood.getName());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.print("CartFragment");
        if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_OK) {
            System.out.print("CartFragment : Success");
            isActivityResultCalled = true;
            layoutOrderType.setVisibility(View.GONE);
            if (GlobalData.selectedAddress != null) {
                locationErrorLayout.setVisibility(View.GONE);
                locationInfoLayout.setVisibility(View.VISIBLE);
                layoutOrderType.setVisibility(View.GONE);
                //Intialize address Value
                if (GlobalData.selectedAddress != null) {
                    /*if (GlobalData.addressList.getAddresses().size() == 0)
                        addAddressTxt.setText(getString(R.string.add_address));
                    else*/
                        addAddressTxt.setText(getString(R.string.change_address));
                }
                addressHeader.setText(GlobalData.selectedAddress.getType());
                addressDetail.setText((selectedAddress.getBuilding() != null ? selectedAddress.getBuilding() + ", " : "") +
                        GlobalData.selectedAddress.getMapAddress());
                //addressDeliveryTime.setText(viewCartItemList.get(0).getProduct().getShop().getEstimatedDeliveryTime().toString() + " Mins");
            } else {
                locationErrorLayout.setVisibility(View.VISIBLE);
                locationInfoLayout.setVisibility(View.GONE);
            }
        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("CartFragment : Failure");
            isActivityResultCalled = true;

        }
    }

    /*private void getCustomerAddresses() {
        Call<List<Address>> call = apiInterface.getCustomerAddresses();
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(@NonNull Call<List<Address>> call, @NonNull Response<List<Address>> response) {
                if (response.isSuccessful()) {
                    modelList.clear();
                    animationLineCartAdd.setVisibility(View.GONE);
                    avdProgress.stop();
                    CustomerAddress model = new CustomerAddress();
                    model.setHeader(getResources().getString(R.string.saved_addresses));
                    model.setAddresses(response.body());
                    GlobalData.profileModel.setAddresses(response.body());
                    modelList.add(model);
                    modelListReference.clear();
                    modelListReference.addAll(modelList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Address>> call, @NonNull Throwable t) {

            }
        });
    }*/

}
