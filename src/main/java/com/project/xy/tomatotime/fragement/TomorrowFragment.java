package com.project.xy.tomatotime.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.project.xy.tomatotime.Entity.Schedule;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.activity.AddScheduleActivity;
import com.project.xy.tomatotime.adapter.RecycleScheduleAdapter;
import com.project.xy.tomatotime.greendao.ScheduleDao;
import com.project.xy.tomatotime.tool.RecyclerViewSpacesItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TomorrowFragment extends Fragment{
    private List<Schedule> txList = new ArrayList<>();//一个全局的链表
    private List<Schedule> list = new ArrayList<>();
    private List<Schedule> delalllist = new ArrayList<>();
    //数据库
    ScheduleDao mScheduleDao = MyApplication.getInstances().getDaoSession().getScheduleDao();
    private String mScheduleName;//标题
    private String mScheduleTime;//开始时间
    private String mScheduleTimeNum;//预计时间数目
    private String mScheduleRemark;//备注
    private String mScheduleType;//类型
    private RecycleScheduleAdapter adapter;
    private Button addTomorrow;
    private Button delAllTomorrow;
    View View;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View = inflater.inflate(R.layout.fragment_tomorrow, container, false);
        addTomorrow = (Button) View.findViewById(R.id.AddTomorrow);
        addTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "明天";
                Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
                intent.putExtra("date",type);
                //startActivity(intent);
                getParentFragment().startActivityForResult(intent,1);
            }
        });
        initTxs();//下面的初始化方法
        Recycle();
        delAllTomorrow = (Button) View.findViewById(R.id.deleteall_tomorrow);
        delAllTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delalllist = mScheduleDao.loadAll();
                for (int i = 0; i < list.size(); i++) {
                    Schedule model = list.get(i);
                    mScheduleType=model.getType();
                    if(mScheduleType.equals("明天")){
                        long id = model.getId();
                        mScheduleDao.deleteByKey(id);
                    }
                }
                adapter.notifyDataSetChanged();
                ((ScheduleFragment) (TomorrowFragment.this.getParentFragment())).inin1();
            }
        });
        return View;
    }

    private void Recycle(){
        RecyclerView recyclerView = View.findViewById(R.id.RecycleTomorrow);//找到RecyclerView控件
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//布局管理器
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleScheduleAdapter(txList);//适配器对象
        //间隔
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 6);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 6);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 0);//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 0);//右间距
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        recyclerView.setAdapter(adapter);//设置适配器为上面的对象
        adapter.setOnItemClickListener(new RecycleScheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecycleScheduleAdapter.ViewHolder v, int position) {
                Schedule s = txList.get(position);
                String title = s.getMemorandum();
                Schedule schedule1 = mScheduleDao.queryBuilder().where(ScheduleDao.Properties.Memorandum.eq(title),ScheduleDao.Properties.Type.eq("明天")).unique();
                long id = schedule1.getId();
                mScheduleDao.deleteByKey(id);
                if (position != txList.size())
                    adapter.notifyItemRangeChanged(position, txList.size() - position);
                ((ScheduleFragment) (TomorrowFragment.this.getParentFragment())).inin1();
            }
        });
    }

    private void initTxs(){
        //加载数据
        txList.clear();
        list = mScheduleDao.loadAll();
        Collections.sort(list, new MapComparator());
        for (int i = 0; i < list.size(); i++) {
            Schedule model = list.get(i);
            mScheduleName=model.getMemorandum();
            mScheduleTime=model.getStartTime();
            mScheduleTimeNum=model.getEstimatedTime();
            mScheduleRemark=model.getRemarks();
            mScheduleType=model.getType();
            if(mScheduleType.equals("明天")){
                Schedule a = new Schedule(null, mScheduleName, mScheduleTime, mScheduleTimeNum, mScheduleRemark, mScheduleType);
                txList.add(a);//加入到链表
            }
        }
    }

    class MapComparator implements Comparator<Schedule> {

        public int compare(Schedule lhs, Schedule rhs) {
            return lhs.getStartTime().compareTo(rhs.getStartTime());
        }
    }
}
