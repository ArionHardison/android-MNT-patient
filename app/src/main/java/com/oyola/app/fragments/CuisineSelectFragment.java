package com.oyola.app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.oyola.app.HomeActivity;
import com.oyola.app.R;
import com.oyola.app.build.api.ApiClient;
import com.oyola.app.build.api.ApiInterface;
import com.oyola.app.helper.ConnectionHelper;
import com.oyola.app.models.Cuisine;
import com.oyola.app.models.CuisinesModel;
import com.oyola.app.utils.Utils;

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

public class CuisineSelectFragment extends DialogFragment {

    public List<Cuisine> CUISINES = new ArrayList<>();
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.cuisine_rv)
    RecyclerView cuisineRv;
    @BindView(R.id.done)
    Button done;
    Unbinder unbinder;
    RecyclerViewAdapter mAdapter;
    List<Cuisine> list = new ArrayList<>();
    ConnectionHelper connectionHelper;
    boolean singleSelection;
    int selected_pos = -1;
    Context context;
    OnSuccessListener listener;

    public CuisineSelectFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public CuisineSelectFragment(boolean singleSelection) {
        // Required empty public constructor
        this.singleSelection = singleSelection;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuisine_select, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        unbinder = ButterKnife.bind(this, view);
        mAdapter = new RecyclerViewAdapter(list, singleSelection);
        cuisineRv.setAdapter(mAdapter);
        connectionHelper = new ConnectionHelper(getActivity());
        if (connectionHelper.isConnectingToInternet())
            getCuisines();
        else
            Utils.displayMessage(getActivity(), context, getString(R.string.oops_no_internet));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.done)
    public void onViewClicked() {
        CUISINES = mAdapter.getSelectedValues();
        HashMap<String, Integer> map = new HashMap<>();
        if (CUISINES.size() > 0) {
            CuisinesModel cuisinesModel = new CuisinesModel();
            List<Integer> value = new ArrayList<>();
            value.clear();
            for (int i = 0; i < CUISINES.size(); i++) {
//                map.put("cuisines["+i+"]",CUISINES.get(i).getId());
                value.add(CUISINES.get(i).getId());
            }
            cuisinesModel.setIntegerList(value);
            updateFavouriteCuisines(cuisinesModel);
        } else {
            CuisinesModel cuisinesModel = new CuisinesModel();
            List<Integer> value = new ArrayList<>();
            value.clear();
            cuisinesModel.setIntegerList(value);
            updateFavouriteCuisines(cuisinesModel);
        }
        dismiss();
    }

    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
    }

    private void getCuisines() {
        Call<List<Cuisine>> call = apiInterface.getCuisines();
        call.enqueue(new Callback<List<Cuisine>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cuisine>> call, @NonNull Response<List<Cuisine>> response) {
                if (response.isSuccessful()) {
                    list.clear();
                    list.addAll(response.body());
                    mAdapter.notifyDataSetChanged();
                    if (response.code() == 401) {
                        startActivity(new Intent(getActivity(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Cuisine>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Utils.displayMessage(getActivity(), context, getString(R.string.something_went_wrong));
                }
            }
        });
    }

    //    private void updateFavouriteCuisines(Map<String, Integer> mMap) {
    private void updateFavouriteCuisines(CuisinesModel mMap) {
        Call<Object> call = apiInterface.updateCuisines(mMap);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    if (listener != null) {
                        listener.refreshHome();
                    }
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Utils.displayMessage(getActivity(), context, getString(R.string.something_went_wrong));
                }
            }
        });
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        boolean isSingleSelection;
        private List<Cuisine> mModelList;

        RecyclerViewAdapter(List<Cuisine> modelList, boolean isSingleSelection) {
            mModelList = modelList;
            this.isSingleSelection = isSingleSelection;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_cuisine, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final Cuisine model = mModelList.get(position);
            holder.textView.setText(model.getName());
            if (singleSelection) {
                if (selected_pos == position) {
                    model.setSelected(true);
                } else {
                    model.setSelected(false);
                }
            }
            holder.textView.setTextColor(model.isSelected() ? Color.WHITE : Color.BLACK);
            holder.textView.setBackground(model.isSelected() ?
                    ContextCompat.getDrawable(getContext(), R.drawable.bg_round_corner_red) :
                    ContextCompat.getDrawable(getContext(), R.drawable.bg_round_corner_white));
            holder.textView.setTag(position);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_pos = (int) view.getTag();
                    model.setSelected(!model.isSelected());
                    holder.textView.setTextColor(model.isSelected() ? Color.WHITE : Color.BLACK);
                    holder.textView.setBackground(model.isSelected() ?
                            ContextCompat.getDrawable(getContext(), R.drawable.bg_round_corner_red) :
                            ContextCompat.getDrawable(getContext(), R.drawable.bg_round_corner_white));
                    if (singleSelection) {
                        notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mModelList == null ? 0 : mModelList.size();
        }

        List<Cuisine> getSelectedValues() {
            List<Cuisine> mm = new ArrayList<>();
            for (Cuisine obj : mModelList) if (obj.isSelected()) mm.add(obj);
            return mm;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            private MyViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.name);
            }
        }
    }

    public void setListener(OnSuccessListener listener) {
        this.listener = listener;
    }

    public interface OnSuccessListener {
        void refreshHome();
    }
}