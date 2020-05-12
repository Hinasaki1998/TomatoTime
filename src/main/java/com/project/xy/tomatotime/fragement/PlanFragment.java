package com.project.xy.tomatotime.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.activity.AddPlanActivity;
import com.project.xy.tomatotime.adapter.FragmentAdapterTabLayout;
import com.project.xy.tomatotime.adapter.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class PlanFragment extends Fragment{
    private ImageButton mPlanButton;
    //fragment的集合，对应每个子页面
    private ArrayList<Fragment> fragments;
    private List<String> titles;
    NoScrollViewPager viewPager;
    private TabLayout tabLayout;
    View View;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View = inflater.inflate(R.layout.fragment_plan, container, false);
        initPager();
        return View;
    }
    public void initPager() {
        viewPager = (NoScrollViewPager) View.findViewById(R.id.vp);
        viewPager.setNoScroll(true);
        tabLayout = (TabLayout) View.findViewById(R.id.tab_layout);
        titles = new ArrayList<String>();
        titles.add("未完成");
        titles.add("已完成");
        fragments = new ArrayList<Fragment>();
        fragments.add(new BeingFragment());
        fragments.add(new FinishFragment());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        inin();
    }
    public void inin(){
        FragmentAdapterTabLayout adpter = new FragmentAdapterTabLayout(getChildFragmentManager(),fragments,titles);
        adpter.notifyDataSetChanged();
        viewPager.setAdapter(adpter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPlanButton = (ImageButton) getActivity().findViewById(R.id.title_edit);
        mPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AddPlanActivity.class);
                startActivity(intent);
            }
        });
    }

    public static PlanFragment newInstance(String param1) {
        PlanFragment fragment = new PlanFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        initPager();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewPager.setCurrentItem(0);
    }
}
