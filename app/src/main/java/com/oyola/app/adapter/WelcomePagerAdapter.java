package com.oyola.app.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oyola.app.R;

public class WelcomePagerAdapter extends PagerAdapter {

    public static final int PAGE_COUNT = 4;
    private TypedArray pageBanners;
    private String[] pageTitles;
    private String[] pageDescriptions;

    public WelcomePagerAdapter(Context context) {
        pageBanners = context.getResources().obtainTypedArray(R.array.walk_through_banner);
        pageTitles = context.getResources().getStringArray(R.array.walk_through_title);
        pageDescriptions = context.getResources().getStringArray(R.array.walk_through_description);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(container.getContext())
                .inflate(R.layout.welcome_slide, container, false);
        initializeView(view, position);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void initializeView(ViewGroup view, int position) {
        ImageView bannerImage = view.findViewById(R.id.iv_banner);
        bannerImage.setImageResource(pageBanners.getResourceId(position, -1));
        TextView titleText = view.findViewById(R.id.tv_title);
        titleText.setText(pageTitles[position]);
        TextView descriptionText = view.findViewById(R.id.tv_description);
        descriptionText.setText(pageDescriptions[position]);
    }
}
