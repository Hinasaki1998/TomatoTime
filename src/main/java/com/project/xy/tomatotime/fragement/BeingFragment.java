package com.project.xy.tomatotime.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.project.xy.tomatotime.Entity.Plan;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.adapter.RecyclePlanAdapter;
import com.project.xy.tomatotime.greendao.PlanDao;
import com.project.xy.tomatotime.tool.RecyclerViewSpacesItemDecoration;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class BeingFragment extends Fragment {
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
    private RecyclePlanAdapter adapter;
    private SwipeRecyclerView recyclerView;
    View View;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View = inflater.inflate(R.layout.fragment_beingplan, container, false);
        initTxs();//下面的初始化方法
        Recycle();
        return View;
    }
    private void Recycle(){
        recyclerView = View.findViewById(R.id.RecycleBeingPlan);//找到RecyclerView控件
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//布局管理器
        recyclerView.setLayoutManager(layoutManager);
        // 设置菜单创建器。
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        // 设置菜单Item点击监听。
        recyclerView.setOnItemMenuClickListener(mItemMenuClickListener);
        adapter = new RecyclePlanAdapter(txList);//适配器对象
        //间隔
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 6);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 6);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 0);//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 0);//右间距
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        recyclerView.setAdapter(adapter);//设置适配器为上面的对象
    }
    // 创建菜单：
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
            int width = 240;
            int height = 240;

            SwipeMenuItem closeItem = new SwipeMenuItem(getContext())
                    .setBackground(R.drawable.selector_purple)
                    .setImage(R.mipmap.close)
                    .setBackground(R.drawable.plan_del)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(closeItem); // 添加菜单到右侧。
            SwipeMenuItem addItem = new SwipeMenuItem(getContext()).setBackground(
                    R.drawable.selector_green)
                    .setImage(R.mipmap.ok)
                    .setBackground(R.drawable.plan_ok)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            // 注意：哪边不想要菜单，那么不要添加即可。
        }
    };

    OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();
            // 左侧还是右侧菜单：
            int direction = menuBridge.getDirection();
            // 菜单在Item中的Position：
            int menuPosition = menuBridge.getPosition();
            //RecycleView的Item的position
            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 0) {
                    View view = recyclerView.getChildAt(position);
                    TextView textView = view.findViewById(R.id.being_name);
                    String title = textView.getText().toString();
                    Plan mPlan = mPlanDao.queryBuilder().where(PlanDao.Properties.PlanTitle.eq(title)).unique();
                    long id = mPlan.getId();
                    mPlanDao.deleteByKey(id);
                    if (position != txList.size())
                        adapter.notifyItemRangeChanged(position, txList.size() - position);
                    ((PlanFragment) (BeingFragment.this.getParentFragment())).inin();
                    Toast.makeText(getContext(), "删除" + position, Toast.LENGTH_SHORT).show();
                    Log.e("删除", title);
                } else if (menuPosition == 1) {
                    View view = recyclerView.getChildAt(position);
                    TextView textView = view.findViewById(R.id.being_name);
                    String title = textView.getText().toString();
                    Plan mPlan = mPlanDao.queryBuilder().where(PlanDao.Properties.PlanTitle.eq(title)).unique();
                    mPlanName = mPlan.getPlanTitle();
                    mPlanLevl = mPlan.getPlanLevel();
                    mPlanTime = mPlan.getEndTime();
                    mPlanNum = "0";
                    mPlanRemark = mPlan.getRemarks();
                    Plan plan = new Plan(mPlan.getId(), mPlanName, mPlanLevl, mPlanTime, mPlanNum, mPlanRemark);
                    mPlanDao.update(plan);
                    if (position != txList.size())
                        adapter.notifyItemRangeChanged(position, txList.size() - position);
                    ((PlanFragment) (BeingFragment.this.getParentFragment())).inin();
                    Toast.makeText(getContext(), "完成" + position, Toast.LENGTH_SHORT).show();
                    Log.e("完成", title);
                }
            }
        }
    };
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
            if(mTomatoNum != 0) {
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