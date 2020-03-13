package com.oyola.app.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.oyola.app.R;
import com.oyola.app.activities.OtherHelpActivity;
import com.oyola.app.activities.SplashActivity;
import com.oyola.app.adapter.DisputeMessageAdapter;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.helper.CustomDialog;
import com.oyola.app.helper.GlobalData;
import com.oyola.app.models.DisputeMessage;
import com.oyola.app.models.Order;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.oyola.app.helper.GlobalData.disputeMessageList;
import static com.oyola.app.helper.GlobalData.isSelectedOrder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHelpFragment extends Fragment {


    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    Unbinder unbinder;
    Context context;
    DisputeMessageAdapter disputeMessageAdapter;
    @BindView(R.id.help_rv)
    RecyclerView helpRv;
    @BindView(R.id.other_help_layout)
    LinearLayout otherHelpLayout;
    @BindView(R.id.dispute)
    Button dispute;
    @BindView(R.id.chat_us)
    Button chatUs;
    @BindView(R.id.email_us)
    Button emailUs;


    Double priceAmount = 0.0;
    int DISPUTE_ID = 0;
    int itemQuantity = 0;
    String currency = "";
    String reason = "OTHERS";
    CustomDialog customDialog;
    String disputeType;
    Integer DISPUTE_HELP_ID = 0;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public OrderHelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_help, container, false);
        unbinder = ButterKnife.bind(this, view);
        customDialog = new CustomDialog(context);

        getDisputeMessage();


        return view;
    }

    private void updateDiputeLayout() {
        if (disputeMessageList != null) {
            helpRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            helpRv.setItemAnimator(new DefaultItemAnimator());
            helpRv.setHasFixedSize(true);
            disputeMessageAdapter = new DisputeMessageAdapter(disputeMessageList, context, getActivity());
            helpRv.setAdapter(disputeMessageAdapter);
            if (disputeMessageList.size() > 0) {
                otherHelpLayout.setVisibility(View.GONE);
            } else {
                otherHelpLayout.setVisibility(View.VISIBLE);
            }
        } else {
            startActivity(new Intent(context, SplashActivity.class));
            getActivity().finish();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showDialog() {
        final String[] disputeArrayList = {"COMPLAINED", "CANCELLED", "REFUND"};
        disputeType = "COMPLAINED";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dispute_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.reason_edit);
        final Spinner disputeTypeSpinner = (Spinner) dialogView.findViewById(R.id.dispute_type);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, disputeArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disputeTypeSpinner.setAdapter(arrayAdapter);
        disputeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disputeType = disputeArrayList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialogBuilder.setTitle(R.string.order_details_page + "  #000" + isSelectedOrder.getId().toString());
        dialogBuilder.setMessage(reason);
        dialogBuilder.setPositiveButton(R.string.submit, null);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edt.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(context, R.string.pleasse_enter_reason, Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            HashMap<String, String> map = new HashMap<>();
                            map.put("order_id", GlobalData.isSelectedOrder.getId().toString());
                            map.put("status", "CREATED");
                            map.put("description", edt.getText().toString());
                            map.put("dispute_type", disputeType);
                            map.put("created_by", "user");
                            map.put("created_to", "user");
                            postDispute(map);
                        }
                    }
                });
            }
        });
        alertDialog.show();

    }

    private void postDispute(HashMap<String, String> map) {
        customDialog.show();
        Call<Order> call = apiInterface.postDispute(map);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, R.string.dispate_create_success, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getDisputeMessage() {
        Call<List<DisputeMessage>> call = apiInterface.getDisputeList();
        call.enqueue(new Callback<List<DisputeMessage>>() {
            @Override
            public void onResponse(Call<List<DisputeMessage>> call, Response<List<DisputeMessage>> response) {
                if (response.isSuccessful()) {
                    Log.e("Dispute List : ", response.toString());
                    disputeMessageList = new ArrayList<>();
                    disputeMessageList.addAll(response.body());
                    updateDiputeLayout();

                } else {
                    updateDiputeLayout();

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<List<DisputeMessage>> call, Throwable t) {
                updateDiputeLayout();
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();

            }
        });


    }


    @OnClick({R.id.chat_us, R.id.dispute, R.id.email_us})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chat_us:
                startActivity(new Intent(getActivity(), OtherHelpActivity.class).putExtra("is_chat", true));
                break;
            case R.id.dispute:
                showDialog();
                break;
            case R.id.email_us:
//                goGmail();
                goToCall();
                break;
        }
    }

    private void goToCall() {
         /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    if (isSelectedOrder.getUser().getCustomer_support() != null && !isSelectedOrder.getUser().getCustomer_support().isEmpty()) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + isSelectedOrder.getUser().getCustomer_support()));
                        startActivity(intent);
                    }else {
                        Toast.makeText(context,R.string.no_number_available,Toast.LENGTH_LONG).show();
                    }
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
            } else {
                if (isSelectedOrder.getUser().getCustomer_support() != null && !isSelectedOrder.getUser().getCustomer_support().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + isSelectedOrder.getUser().getCustomer_support()));
                    startActivity(intent);
                }else {
                    Toast.makeText(context,R.string.no_number_available,Toast.LENGTH_LONG).show();
                }
            }*/
        if (isSelectedOrder.getUser().getCustomer_support() != null && !isSelectedOrder.getUser().getCustomer_support().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + isSelectedOrder.getUser().getCustomer_support()));
            startActivity(intent);
        } else {
            Toast.makeText(context, R.string.no_number_available, Toast.LENGTH_LONG).show();
        }

    }

    private void goGmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, "");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + "-" + getString(R.string.help));
        intent.putExtra(Intent.EXTRA_TEXT, "Hello team");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToCall();
                } else
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
