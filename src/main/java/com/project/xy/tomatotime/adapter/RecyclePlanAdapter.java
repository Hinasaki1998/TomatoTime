package com.project.xy.tomatotime.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.project.xy.tomatotime.Entity.Plan;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.activity.ReviseActivity;
import com.project.xy.tomatotime.activity.TomatoActivity;

import java.util.List;


public class RecyclePlanAdapter extends RecyclerView.Adapter<RecyclePlanAdapter.ViewHolder> {
    private List<Plan> mTxList;//用以将适配完的子项储存的链表，它的泛型是之前的实体类

    static class ViewHolder extends RecyclerView.ViewHolder {
        //内部静态类，用以定义TxApter.View的泛型
        TextView beLevel;
        TextView beName,beNum;//这两个是在子项布局里面具体的控件
        ImageButton goTomato;
        View beView;//这个是用于整个子项的控制的控件

        public ViewHolder(View view) {
            super(view);
            beView = view;//这个是整个子项的控件
            beLevel = view.findViewById(R.id.being_level);
            beName = view.findViewById(R.id.being_name);//通过R文件的id查找，找出子项的具体控件
            beNum = view.findViewById(R.id.being_num);
            goTomato = view.findViewById(R.id.go_tomato);
        }
    }

    public RecyclePlanAdapter(List<Plan> txList) {
        //链表的赋值
        mTxList = txList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //ViewHode方法，我的理解就是对某个具体子项的操作，包括对具体控件的设置，包括且不限于的点击动作两个参数
        //A:ViewGroup parent主要用于调用其整个RecyclerView的上下文
        //B:第二个参数因为在方法里面没有调用，所以我也没看懂，从字面上看，这个参数是一个整型的控件类型？？？
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_beingplan, parent, false);
        //将子项的布局通过LayoutInflater引入
        final ViewHolder holder = new ViewHolder(view);
        holder.beView.setOnClickListener(new View.OnClickListener() {
            //这里是子项的点击事件，RecyclerView的特点就是可以对子项里面单个控件注册监听，这也是为什么RecyclerView要摒弃ListView的setOnItemClickListener方法的原因
            @Override
            public void onClick(View v) {
                Plan plan = mTxList.get(holder.getAdapterPosition());
                Toast.makeText(v.getContext(), "已经点击！", Toast.LENGTH_LONG).show();
                TextView textView = v.findViewById(R.id.being_name);
                String title = textView.getText().toString();
                Intent intent = new Intent(v.getContext(),ReviseActivity.class);
                intent.putExtra("date",title);
                v.getContext().startActivity(intent);
                //mTxList.remove(plan);//所谓的删除就是将子项从链表中remove
            }
        });
        return holder;//返回一个holder对象，给下个方法使用
    }

    @Override

    public void onBindViewHolder(ViewHolder holder, int position) {
        //用以将滚入屏幕的子项加载图片等的方法，两个参数
        //A:前面方法ViewHolder的对象；
        //B:子项的id
        Plan plan = mTxList.get(position);//创建前面实体类的对象
        holder.beLevel.setText(plan.getPlanLevel());//将具体值赋与子项对应的控件
        holder.beName.setText(plan.getPlanTitle());
        final String title = plan.getPlanTitle();
        holder.beNum.setText(plan.getPlanTomatoNum());
        holder.goTomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),TomatoActivity.class);
                intent.putExtra("tomatodate",title);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //用以返回RecyclerView的总共长度，这里直接使用了链表的长度（size）
        return mTxList.size();
    }
}
