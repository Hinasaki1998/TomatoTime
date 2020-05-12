package com.project.xy.tomatotime.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.xy.tomatotime.Entity.Plan;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.adapter.RecyclePlanAdapter;
import com.project.xy.tomatotime.greendao.PlanDao;
import com.project.xy.tomatotime.tool.RecyclerViewSpacesItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class FinishFragment extends Fragment {
    private List<Plan> txList = new ArrayList<>();//一个全局的链表
    private List<Plan> list = new ArrayList<>();
    //数据库
    PlanDao mPlanDao = MyApplication.getInstances().getDaoSession().getPlanDao();
    //输入数据
    private String mPlanName;//标题
    private String mPlanLevl;//优先级
    private String mPlanTime;//截止时间
    private String mPlanNum;//番茄钟数目
    private String mPlanRemark;//备注
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View View = inflater.inflate(R.layout.fragment_finishplan, container, false);
        initTxs();//下面的初始化方法
        RecyclerView recyclerView = View.findViewById(R.id.RecycleFinishPlan);//找到RecyclerView控件
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//布局管理器
        recyclerView.setLayoutManager(layoutManager);
        RecyclePlanAdapter adapter = new RecyclePlanAdapter(txList);//适配器对象
        //间隔
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 6);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 6);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 0);//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 0);//右间距
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        recyclerView.setAdapter(adapter);//设置适配器为上面的对象
        return View;
    }

    private void initTxs(){
        //加载数据
        txList.clear();
        list = mPlanDao.loadAll();
        Collections.sort(list, new MapComparator());
        for (int i = 0; i < list.size(); i++) {
            Plan model = list.get(i);
            mPlanName=model.getPlanTitle();
            mPlanLevl=model.getPlanLevel();
            mPlanTime=model.getEndTime();
            mPlanNum=model.getPlanTomatoNum();
            mPlanRemark=model.getRemarks();
            int mTomatoNum = Integer.valueOf(mPlanNum).intValue();
            if(mTomatoNum == 0 ) {
                Plan a = new Plan(null, mPlanName, mPlanLevl, mPlanTime, mPlanNum, mPlanRemark);
                txList.add(a);//加入到链表
            }
        }
    }

    class MapComparator implements Comparator<Plan> {
        public int compare(Plan lhs, Plan rhs) {
            return lhs.getPlanLevel().compareTo(rhs.getPlanLevel());
        }
    }

}