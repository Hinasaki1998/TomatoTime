package com.project.xy.tomatotime.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.project.xy.tomatotime.Entity.Plan;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.fragement.BeingFragment;
import com.project.xy.tomatotime.greendao.PlanDao;
import com.project.xy.tomatotime.tool.CustomDatePicker;
import com.project.xy.tomatotime.tool.DateFormatUtils;
import com.project.xy.tomatotime.tool.PickerViewNum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddPlanActivity extends Activity{
    private ImageButton mPlanButton;
    //数据库
    PlanDao mPlanDao = MyApplication.getInstances().getDaoSession().getPlanDao();
    private List<Plan> mPlanList;

    //输入数据
    private String mPlanName;//标题
    private String mPlanLevl;//优先级
    private String mPlanTime;//截止时间
    private String mPlanNum;//番茄钟数目
    private String mPlanRemark;//备注

    private TextView mTvSelectedDate;
    private CustomDatePicker mDatePicker;
    private Button mPlanDefine, mPlanCancel;

    PickerViewNum pv;
    private LinearLayout layout_1 ,layout_2;
    private boolean isVisible = true;
    private TextView mNumTextView;
    Plan planList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplan);
        initTool();
        mPlanDefine = (Button) findViewById(R.id.define);
        mPlanDefine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
            }
        });
        mPlanCancel = (Button) findViewById(R.id.cancel);
        mPlanCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void insert(){
        EditText title =(EditText)findViewById(R.id.InputPlanName);
        mPlanName=title.getText().toString();
        Spinner level =(Spinner) findViewById(R.id.spinner);
        mPlanLevl=level.getSelectedItem().toString();
        mPlanTime=mTvSelectedDate.getText().toString();
        mPlanNum=mNumTextView.getText().toString();
        EditText remark =(EditText)findViewById(R.id.InputRemark);
        mPlanRemark=remark.getText().toString();
        if(TextUtils.isEmpty(title.getText())){
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("计划名称没有填写")
                    .setPositiveButton("确定", null)
                    .show();
        } else {
            try{
                planList = mPlanDao.queryBuilder().where(PlanDao.Properties.PlanTitle.eq(mPlanName)).unique();
                if(planList.getPlanTitle() != null){
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("有相同的计划存在")
                            .setPositiveButton("确定", null)
                            .show();
                }
            }catch (Exception e){
                Plan plan = new Plan(null,mPlanName,mPlanLevl,mPlanTime,mPlanNum,mPlanRemark);
                mPlanDao.insert(plan);
                finish();
            }
        }
    }
    public void initTool(){
        //标题栏返回键监听
        mPlanButton = (ImageButton) findViewById(R.id.title_back);
        mPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //番茄钟数目选择
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            data.add(i+"");
        }
        layout_1 = (LinearLayout) findViewById(R.id.RelativeLayout_id);
        layout_1.setVisibility(View.GONE);//这一句即隐藏布局LinearLayout区域
        layout_2 = (LinearLayout) findViewById(R.id.RelativeLayout);
        layout_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible) {
                    isVisible = false;
                    layout_1.setVisibility(View.VISIBLE);//这一句显示布局LinearLayout区域
                } else {
                    layout_1.setVisibility(View.GONE);//这一句即隐藏布局LinearLayout区域
                    isVisible = true;
                }
            }
        });
        mNumTextView = (TextView) findViewById(R.id.selected_Num);
        mNumTextView.setText("1");
        pv=findViewById(R.id.minute_pv);
        pv.setData(data);
        pv.setOnSelectListener(new PickerViewNum.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mNumTextView.setText(text);
            }
        });


        //日期选择
        findViewById(R.id.ll_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_date:
                        // 日期格式为yyyy-MM-dd
                        mDatePicker.show(mTvSelectedDate.getText().toString());
                        break;
                }
            }
        });
        mTvSelectedDate = findViewById(R.id.selected_date);
        initDatePicker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatePicker.onDestroy();
    }
    private void initDatePicker() {
        long beginTimestamp = System.currentTimeMillis();
        //在现在时间基础上加一年
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)+1);
        Date date=curr.getTime();
        long endTimestamp = DateFormatUtils.dateToLong(date);
        //long endTimestamp = DateFormatUtils.str2Long("date", false);

        mTvSelectedDate.setText(DateFormatUtils.long2Str(beginTimestamp, false));

        // 通过时间戳初始化日期，毫秒级别
        mDatePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mTvSelectedDate.setText(DateFormatUtils.long2Str(timestamp, false));
            }
        }, beginTimestamp, endTimestamp);
        // 不允许点击屏幕或物理返回键关闭
        mDatePicker.setCancelable(true);
        // 不显示时和分
        mDatePicker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        mDatePicker.setScrollLoop(false);
        // 不允许滚动动画
        mDatePicker.setCanShowAnim(true);
    }

}
