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

import com.project.xy.tomatotime.Entity.Occupied;
import com.project.xy.tomatotime.Entity.Plan;
import com.project.xy.tomatotime.Entity.Schedule;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.activity.AddScheduleActivity;
import com.project.xy.tomatotime.adapter.RecycleScheduleAdapter;
import com.project.xy.tomatotime.greendao.OccupiedDao;
import com.project.xy.tomatotime.greendao.PlanDao;
import com.project.xy.tomatotime.greendao.ScheduleDao;
import com.project.xy.tomatotime.tool.RecyclerViewSpacesItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TodayFragment extends Fragment{
    private List<Schedule> txList = new ArrayList<>();//一个全局的链表
    private List<Schedule> list = new ArrayList<>();
    private List<Plan> planList1 = new ArrayList<>();
    private List<Plan> planList2 = new ArrayList<>();
    private List<Plan> planList3 = new ArrayList<>();
    private List<Schedule> scheduleList = new ArrayList<>();
    private List<Schedule> delalllist = new ArrayList<>();

    //数据库
    ScheduleDao mScheduleDao = MyApplication.getInstances().getDaoSession().getScheduleDao();
    private String mScheduleName;//标题
    private String mScheduleTime;//开始时间
    private String mScheduleTimeNum;//预计时间数目
    private String mScheduleRemark;//备注
    private String mScheduleType;//类型

    //数据库
    PlanDao mPlanDao = MyApplication.getInstances().getDaoSession().getPlanDao();

    //数据库
    private boolean misoccupied;//是否被占用
    private List<Occupied> occupiedlist = new ArrayList<>();
    OccupiedDao mOccupiedDao = MyApplication.getInstances().getDaoSession().getOccupiedDao();

    private RecycleScheduleAdapter adapter;
    private Button addToday;
    private Button delAllToday,changeTomottow,addtomatotime;
    View View;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View = inflater.inflate(R.layout.fragment_today, container, false);
        addToday = (Button) View.findViewById(R.id.AddToday);
        addToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "今天";
                Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
                intent.putExtra("date",type);
                //startActivity(intent);
                getParentFragment().startActivityForResult(intent,2);
            }
        });
        initTxs();//下面的初始化方法
        Recycle();
        delAllToday = (Button) View.findViewById(R.id.deleteall_today);
        delAllToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delalllist = mScheduleDao.loadAll();
                for (int i = 0; i < list.size(); i++) {
                    Schedule model = list.get(i);
                    mScheduleType=model.getType();
                    if(mScheduleType.equals("今天")){
                        long id = model.getId();
                        mScheduleDao.deleteByKey(id);
                    }
                }
                InitDate();
                adapter.notifyDataSetChanged();
                ((ScheduleFragment) (TodayFragment.this.getParentFragment())).inin();
            }
        });
        changeTomottow = (Button) View.findViewById(R.id.changetomorrow);
        changeTomottow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delalllist = mScheduleDao.loadAll();
                for (int i = 0; i < delalllist.size(); i++) {
                    Schedule model = delalllist.get(i);
                    mScheduleType=model.getType();
                    if(mScheduleType.equals("今天")){
                        long id = model.getId();
                        mScheduleDao.deleteByKey(id);
                    }
                    InitDate();
                }
                for (int i = 0; i < delalllist.size(); i++) {
                    Schedule model = delalllist.get(i);
                    mScheduleName=model.getMemorandum();
                    mScheduleTime=model.getStartTime();
                    mScheduleTimeNum=model.getEstimatedTime();
                    mScheduleRemark=model.getRemarks();
                    mScheduleType=model.getType();
                    if(mScheduleType.equals("明天")){
                        mScheduleType="今天";
                        long id = model.getId();
                        Schedule sch = new Schedule(id, mScheduleName, mScheduleTime, mScheduleTimeNum, mScheduleRemark, mScheduleType);
                        mScheduleDao.update(sch);
                    }
                }
                adapter.notifyDataSetChanged();
                ((ScheduleFragment) (TodayFragment.this.getParentFragment())).inin();
            }
        });
        addtomatotime = (Button) View.findViewById(R.id.addtomato);
        addtomatotime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertplan();
                adapter.notifyDataSetChanged();
                ((ScheduleFragment) (TodayFragment.this.getParentFragment())).inin();
            }
        });
        return View;
    }

    private void Recycle(){
        final RecyclerView recyclerView = View.findViewById(R.id.RecycleToday);//找到RecyclerView控件
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//布局管理器
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
                Schedule schedule1 = mScheduleDao.queryBuilder().where(ScheduleDao.Properties.Memorandum.eq(title),ScheduleDao.Properties.Type.eq("今天")).unique();
                long id = schedule1.getId();
                mScheduleDao.deleteByKey(id);
                if(schedule1.getStartTime()!=""){
                    Occupied mOccupied = mOccupiedDao.queryBuilder().where(OccupiedDao.Properties.Time.eq(delString(schedule1.getStartTime()))).unique();
                    mOccupied.setIsoccupied(false);
                    mOccupiedDao.update(mOccupied);
                }
                if (position != txList.size())
                    adapter.notifyItemRangeChanged(position, txList.size() - position);
                ((ScheduleFragment) (TodayFragment.this.getParentFragment())).inin();
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
            if(mScheduleType.equals("今天")){
                Schedule a = new Schedule(null, mScheduleName, mScheduleTime, mScheduleTimeNum, mScheduleRemark, mScheduleType);
                txList.add(a);//加入到链表
            }
        }
    }

    private void insertplan(){
        occupiedlist = mOccupiedDao.loadAll();
        planList1 = mPlanDao.queryBuilder().where(PlanDao.Properties.PlanLevel.eq("Lv1")).list();
        planList2 = mPlanDao.queryBuilder().where(PlanDao.Properties.PlanLevel.eq("Lv2")).list();
        planList3 = mPlanDao.queryBuilder().where(PlanDao.Properties.PlanLevel.eq("Lv3")).list();
        misoccupied = false;
        for(int i=8,j=1;i<21;i++,j++){
            Occupied occ1 = occupiedlist.get(i);
            boolean x = occ1.getIsoccupied();
            if(misoccupied == false){
                if(x==false){
                    Occupied occup1 = new Occupied(occ1.getId(),occ1.getTime(),true);
                    mOccupiedDao.update(occup1);
                    String a;
                    if(occup1.getTime()<10){
                        a = "0"+occup1.getTime()+":00";
                    }else
                        a = occup1.getTime()+":00";

                    if(planList3.size()>0){
                        for(int e=0;e<planList3.size();e++){
                            Plan p = planList3.get(e);
                            scheduleList = mScheduleDao.queryBuilder().where(ScheduleDao.Properties.Memorandum.eq(p.getPlanTitle())).list();
                            if(scheduleList.size()==0 && p.getPlanTomatoNum()!="0"){
                                Schedule schedule = new Schedule(null, p.getPlanTitle(), a, "1", "", "今天");
                                mScheduleDao.insert(schedule);
                                misoccupied = true;
                                return;
                            }
                        }
                    }else if(planList2.size()>0){
                        for(int e=0;e<planList2.size();e++){
                            Plan p = planList2.get(e);
                            scheduleList = mScheduleDao.queryBuilder().where(ScheduleDao.Properties.Memorandum.eq(p.getPlanTitle())).list();
                            if(scheduleList.size()==0 && p.getPlanTomatoNum()!="0"){
                                Schedule schedule = new Schedule(null, p.getPlanTitle(), a, "1", "", "今天");
                                mScheduleDao.insert(schedule);
                                misoccupied = true;
                                return;
                            }
                        }
                    }else if(planList1.size()>0){
                        for(int e=0;e<planList1.size();e++){
                            Plan p = planList1.get(e);
                            scheduleList = mScheduleDao.queryBuilder().where(ScheduleDao.Properties.Memorandum.eq(p.getPlanTitle())).list();
                            if(scheduleList.size()==0 && p.getPlanTomatoNum()!="0"){
                                Schedule schedule = new Schedule(null, p.getPlanTitle(), a, "1", "", "今天");
                                mScheduleDao.insert(schedule);
                                misoccupied = true;
                                return;
                            }
                        }
                    }else {
                        scheduleList = mScheduleDao.queryBuilder().where(ScheduleDao.Properties.Memorandum.eq("番茄钟任务")).list();
                        if(scheduleList.size()==0){
                            Schedule schedule = new Schedule(null, "番茄钟任务", a, "1", "", "今天");
                            mScheduleDao.insert(schedule);
                            misoccupied = true;
                            return;
                        }
                    }
                }
            }
        }
    }

    public void InitDate(){
        occupiedlist = mOccupiedDao.loadAll();
        for(int i=0;i<occupiedlist.size();i++){
            long id = occupiedlist.get(i).getId();
            Occupied mOccupied = mOccupiedDao.queryBuilder().where(OccupiedDao.Properties.Id.eq(id)).unique();
            Occupied occupied = new Occupied(mOccupied.getId(),mOccupied.getTime(),false);
            mOccupiedDao.update(occupied);
        }
    }

    class MapComparator implements Comparator<Schedule> {

        public int compare(Schedule lhs, Schedule rhs) {
            return lhs.getStartTime().compareTo(rhs.getStartTime());
        }
    }

    public int delString(String time){
        return Integer.valueOf(time.substring(0,2)).intValue();
    }
}
