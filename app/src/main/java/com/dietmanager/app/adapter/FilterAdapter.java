package com.dietmanager.app.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.dietmanager.app.R;
import com.dietmanager.app.activities.FilterActivity;
import com.dietmanager.app.helper.GlobalData;
import com.dietmanager.app.models.Cuisine;
import com.dietmanager.app.models.FilterModel;

import java.util.ArrayList;
import java.util.List;

import static com.dietmanager.app.activities.FilterActivity.isReset;
import static com.dietmanager.app.fragments.HomeDietFragment.isFilterApplied;

public class FilterAdapter extends SectionedRecyclerViewAdapter<FilterAdapter.ViewHolder> {

    private List<FilterModel> list;
    private LayoutInflater inflater;
    public static ArrayList<Integer> cuisineIdList = new ArrayList<>();
    public static boolean isOfferApplied = false;
    public static boolean isPureVegApplied = false;

    public FilterAdapter(Context context, List<FilterModel> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = inflater.inflate(R.layout.header_filter, parent, false);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
            default:
                v = inflater.inflate(R.layout.filter_list_item, parent, false);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }

    @Override
    public int getItemCount(int section) {
        return list.get(section).getCuisines().size();
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, final int section) {
        holder.headerTxt.setText(list.get(section).getHeader());
        if (section == 0)
            holder.viewBox.setVisibility(View.GONE);
        else
            holder.viewBox.setVisibility(View.VISIBLE);
        holder.headerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(list.get(section).getHeader());
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int section, final int relativePosition, int absolutePosition) {
        final String item = list.get(section).getCuisines().get(relativePosition).getName();
        holder.chkSelected.setText(item);
        if (list.get(section).getCuisines().size() == relativePosition + 1)
            holder.viewLine.setVisibility(View.GONE);
        else
            holder.viewLine.setVisibility(View.VISIBLE);
        cuisineIdList = new ArrayList<>();
        if (isReset) {
            holder.chkSelected.setChecked(false);
            isOfferApplied = false;
            isPureVegApplied = false;
            cuisineIdList.clear();
        } else {
            //Check if applied or not
            if (GlobalData.cuisineIdArrayList != null)
                cuisineIdList.addAll(GlobalData.cuisineIdArrayList);
            if (list.get(section).getHeader().equalsIgnoreCase("Cuisines")) {
                if (cuisineIdList.size() != 0) {
                    Cuisine cuisine = list.get(section).getCuisines().get(relativePosition);
                    for (int i = 0; i < cuisineIdList.size(); i++) {
                        if (cuisineIdList.get(i).equals(cuisine.getId())) {
                            holder.chkSelected.setChecked(true);
                        }
                    }
                } else {
                    holder.chkSelected.setChecked(false);
                }
            } else {
                if (list.get(section).getCuisines().get(relativePosition).getName().equalsIgnoreCase("Offers"))
                    holder.chkSelected.setChecked(isOfferApplied);
                else if (list.get(section).getCuisines().get(relativePosition).getName().equalsIgnoreCase("Vegetarian"))
                    holder.chkSelected.setChecked(isPureVegApplied);
            }
        }
        if (isFilterApplied) {
            FilterActivity.applyFilterBtn.setAlpha(1);
            FilterActivity.resetTxt.setAlpha(1);
            FilterActivity.applyFilterBtn.setClickable(true);
            FilterActivity.resetTxt.setClickable(true);
            FilterActivity.applyFilterBtn.setEnabled(true);
            FilterActivity.resetTxt.setEnabled(true);
        }
        holder.chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (list.get(section).getHeader().equalsIgnoreCase("Cuisines")) {
                        Cuisine cuisine = list.get(section).getCuisines().get(relativePosition);
                        cuisineIdList.add(cuisine.getId());
                    } else {
                        if (list.get(section).getCuisines().get(relativePosition).getName().equalsIgnoreCase("Offers"))
                            isOfferApplied = true;
                        else if (list.get(section).getCuisines().get(relativePosition).getName().equalsIgnoreCase("Vegetarian"))
                            isPureVegApplied = true;
                    }
                    FilterActivity.applyFilterBtn.setAlpha(1);
                    FilterActivity.resetTxt.setAlpha(1);
                    FilterActivity.applyFilterBtn.setClickable(true);
                    FilterActivity.applyFilterBtn.setEnabled(true);
                    FilterActivity.resetTxt.setEnabled(true);
                    FilterActivity.resetTxt.setClickable(true);
                } else {
                    if (list.get(section).getHeader().equalsIgnoreCase("Cuisines")) {
                        Cuisine cuisine = list.get(section).getCuisines().get(relativePosition);
                        for (int i = 0; i < cuisineIdList.size(); i++) {
                            if (cuisineIdList.get(i).equals(cuisine.getId())) {
                                cuisineIdList.remove(i);
                                break;
                            }
                        }
                    } else {
                        if (list.get(section).getCuisines().get(relativePosition).getName().equalsIgnoreCase("Offers"))
                            isOfferApplied = false;
                        else if (list.get(section).getCuisines().get(relativePosition).getName().equalsIgnoreCase("Vegetarian"))
                            isPureVegApplied = false;
                    }
                    if (cuisineIdList.size() == 0 && !isPureVegApplied && !isOfferApplied) {
                        if (!isFilterApplied) {
                            FilterActivity.applyFilterBtn.setAlpha((float) 0.5);
                            FilterActivity.applyFilterBtn.setClickable(false);
                            FilterActivity.applyFilterBtn.setEnabled(false);
                            FilterActivity.resetTxt.setAlpha((float) 0.5);
                            FilterActivity.resetTxt.setClickable(false);
                            FilterActivity.resetTxt.setEnabled(false);
                        }
                    }
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView headerTxt;
        CheckBox chkSelected;
        View viewLine, viewBox;
        LinearLayout itemLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                headerTxt = itemView.findViewById(R.id.header);
                viewBox = itemView.findViewById(R.id.view_box);
            } else {
                itemLayout = itemView.findViewById(R.id.item_layout);
                chkSelected = itemView.findViewById(R.id.chk_selected);
                viewLine = itemView.findViewById(R.id.view_line);
            }
        }
    }
}