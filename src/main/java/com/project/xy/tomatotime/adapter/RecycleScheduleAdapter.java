package com.project.xy.tomatotime.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.project.xy.tomatotime.Entity.Schedule;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;

import com.project.xy.tomatotime.greendao.ScheduleDao;

import java.util.List;

public class RecycleScheduleAdapter extends RecyclerView.Adapter<RecycleScheduleAdapter.ViewHolder> {
    private List<Schedule> mTxList;//用以将适配完的子项储存的链表，它的泛型是之前的实体类
    //数据库
    ScheduleDao mScheduleDao = MyApplication.getInstances().getDaoSession().getScheduleDao();
    private RecycleScheduleAdapter adapter;
    private OnItemClickListener mOnItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        //内部静态类，用以定义TxApter.View的泛型
        TextView scheduletile;
        TextView scheduletime;//这两个是在子项布局里面具体的控件
        ImageButton goDel;
        View beView;//这个是用于整个子项的控制的控件
        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View view) {
            super(view);
            beView = view;//这个是整个子项的控件
            scheduletile = view.findViewById(R.id.schedule_title);
            scheduletime = view.findViewById(R.id.schedule_time);//通过R文件的id查找，找出子项的具体控件
            goDel = view.findViewById(R.id.go_del);
        }
    }

    public RecycleScheduleAdapter(List<Schedule> txList) {
        //链表的赋值
        mTxList = txList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //ViewHode方法，我的理解就是对某个具体子项的操作，包括对具体控件的设置，包括且不限于的点击动作两个参数
        //A:ViewGroup parent主要用于调用其整个RecyclerView的上下文
        //B:第二个参数因为在方法里面没有调用，所以我也没看懂，从字面上看，这个参数是一个整型的控件类型？？？
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_schedule, parent, false);
        //将子项的布局通过LayoutInflater引入
        final RecycleScheduleAdapter.ViewHolder holder = new RecycleScheduleAdapter.ViewHolder(view);
        holder.beView.setOnClickListener(new View.OnClickListener() {
            //这里是子项的点击事件，RecyclerView的特点就是可以对子项里面单个控件注册监听，这也是为什么RecyclerView要摒弃ListView的setOnItemClickListener方法的原因
            @Override
            public void onClick(View v) {
                Schedule schedule = mTxList.get(holder.getAdapterPosition());
                Toast.makeText(v.getContext(), "已经点击！", Toast.LENGTH_LONG).show();
                //mTxList.remove(plan);//所谓的删除就是将子项从链表中remove
            }
        });
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return holder;//返回一个holder对象，给下个方法使用
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //用以将滚入屏幕的子项加载图片等的方法，两个参数
        //A:前面方法ViewHolder的对象；
        //B:子项的id
        Schedule schedule = mTxList.get(position);//创建前面实体类的对象
        holder.scheduletile.setText(schedule.getMemorandum());//将具体值赋与子项对应的控件
        holder.scheduletime.setText(schedule.getStartTime());

        holder.goDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(holder, position);
            }
        });
    }
    //自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener  {
        void onItemClick(ViewHolder holder, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }
    @Override
    public int getItemCount() {
        //用以返回RecyclerView的总共长度，这里直接使用了链表的长度（size）
        return mTxList.size();
    }
}
