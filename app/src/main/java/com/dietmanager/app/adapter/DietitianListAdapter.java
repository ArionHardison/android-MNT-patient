package com.dietmanager.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.app.R;
import com.dietmanager.app.models.dietitianlist.DietitianListItem;

import java.util.ArrayList;
import java.util.List;

public class DietitianListAdapter extends RecyclerView.Adapter<DietitianListAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private IDietitianListener listener;
    private List<DietitianListItem> list;
    private List<DietitianListItem> filterItems =new ArrayList<>();
    public DietitianListAdapter(List<DietitianListItem> list, Context con, IDietitianListener listener) {
        this.list = list;
        filterItems.clear();
        filterItems.addAll(list);
        this.context = con;
        this.listener = listener;
    }

    @Override
    public DietitianListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_dietitian_list, parent, false);
        return new DietitianListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DietitianListAdapter.MyViewHolder holder, final int position) {
        DietitianListItem item = list.get(position);
        holder.userName.setText(item.getName());
            Glide.with(context)
                    .load(item.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.man)
                            .error(R.drawable.man))
                    .into(holder.userImg);

        if(item.getTitle()!=null&&!item.getTitle().isEmpty()) {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(item.getTitle());
        }
        else
            holder.title.setVisibility(View.GONE);

        if (item.getExperience()==1)
            holder.experience.setText(context.getString(R.string.years_experience,item.getExperience()));
        else if (item.getExperience()>1)
            holder.experience.setText(context.getString(R.string.years_experience_plural,item.getExperience()));
        else
            holder.experience.setText(context.getString(R.string.no_experience));


        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDietitianClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String charString = constraint.toString();
            if (charString.isEmpty()) {
                list=filterItems;
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            } else {
                List<DietitianListItem> filteredList = new ArrayList<DietitianListItem>();
                for (DietitianListItem row : filterItems) {
                    if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                        filteredList.add(row);
                    }
                }
                list = filteredList;
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = list;
            return filterResults;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList<DietitianListItem>)results.values;
            notifyDataSetChanged();
        }
    };

    public void setList(List<DietitianListItem> list) {
        this.list = list;
        filterItems.clear();
        filterItems.addAll(list);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userName, title,experience;
        LinearLayout itemLayout;
        ImageView userImg;

        public MyViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.user_name);
            title = view.findViewById(R.id.title);
            experience = view.findViewById(R.id.experience);
            itemLayout = view.findViewById(R.id.item_layout);
            userImg = view.findViewById(R.id.user_img);
        }
    }

    public interface IDietitianListener{
        void onDietitianClicked(DietitianListItem dietitianListItem);
    }
}