package com.project.xy.tomatotime.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.xy.tomatotime.Entity.Occupied;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.adapter.FragmentAdapter;
import com.project.xy.tomatotime.greendao.OccupiedDao;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment{
    private List<Fragment> mFragmentArray;
    private TodayFragment mTodayFragment;
    private TomorrowFragment mTomorrowFragment;
    View View;
    ViewPager viewPager;
    private boolean TiaoZhuan = true;
    //数据库
    private int mTime;//时间
    private boolean misoccupied;//是否被占用
    private List<Occupied> list = new ArrayList<>();
    OccupiedDao mOccupiedDao = MyApplication.getInstances().getDaoSession().getOccupiedDao();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View = inflater.inflate(R.layout.fragment_schedule, container, false);
        initPager();
        InitDate();
        return View;
    }

    public void InitDate(){
        list = mOccupiedDao.loadAll();
        if(list.size()==0){
            for(int i=0;i<=24;i++){
                mTime = i;
                misoccupied = false;
                Occupied occupied = new Occupied(null,mTime,misoccupied);
                mOccupiedDao.insert(occupied);
            }
        }
    }

    public void initPager(){
        viewPager = (ViewPager) View.findViewById(R.id.vp);

        mTodayFragment = new TodayFragment();
        mTomorrowFragment = new TomorrowFragment();

        mFragmentArray = new ArrayList<>();
        mFragmentArray.add(mTodayFragment);
        mFragmentArray.add(mTomorrowFragment);
        inin();
    }
    public void inin(){
        //创建fragment适配器并把页面组传给它
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager(),mFragmentArray);
        fragmentAdapter.notifyDataSetChanged();
        //添加适配器
        viewPager.setAdapter(fragmentAdapter);
    }

    public void inin1(){
        //创建fragment适配器并把页面组传给它
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager(),mFragmentArray);
        fragmentAdapter.notifyDataSetChanged();
        //添加适配器
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        inin();
        if (TiaoZhuan == true)
            viewPager.setCurrentItem(0);
        else{
            viewPager.setCurrentItem(1);
        }
        TiaoZhuan = true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 10) {
            TiaoZhuan = false;
        }
        else if (requestCode == 2 && resultCode == 10)
            TiaoZhuan = true;
    }

    public static ScheduleFragment newInstance(String param1) {
        ScheduleFragment fragment = new ScheduleFragment();
        return fragment;
    }

}
