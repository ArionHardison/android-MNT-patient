package com.dietmanager.app.fragments;


import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dietmanager.app.R;
import com.dietmanager.app.adapter.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderViewFragment extends Fragment {


    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.view_line4)
    View viewLine4;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;

    public OrderViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_view, container, false);
        unbinder = ButterKnife.bind(this, view);


        //ViewPager Adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new OrderDetailFragment(), getResources().getString(R.string.order_details));
        adapter.addFragment(new OrderHelpFragment(), getResources().getString(R.string.order_help));
        viewPager.setAdapter(adapter);
        //set ViewPager
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }





}
