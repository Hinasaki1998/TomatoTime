package com.project.xy.tomatotime.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.xy.tomatotime.Entity.Occupied;
import com.project.xy.tomatotime.Entity.Schedule;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.greendao.OccupiedDao;
import com.project.xy.tomatotime.greendao.ScheduleDao;
import com.project.xy.tomatotime.tool.CustomDatePicker;
import com.project.xy.tomatotime.tool.DateFormatUtils;
import com.project.xy.tomatotime.tool.PickerViewNum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddScheduleActivity extends Activity {
    private ImageButton mScheduleButton;
    private Button mScheduleDefine, mScheduleCancel;
    //数据库
    ScheduleDao mScheduleDao = MyApplication.getInstances().getDaoSession().getScheduleDao();
    OccupiedDao mOccupiedDao = MyApplication.getInstances().getDaoSession().getOccupiedDao();
    Occupied occupiedList;
    //输入数据
    private String mScheduleName;//标题
    private String mScheduleTime;//开始时间
    private String mScheduleTimeNum;//预计时间数目
    private String mScheduleRemark;//备注
    private String mScheduleType;//类型

    private CustomDatePicker mDatePicker;
    private TextView mTvSelectedTime;

    PickerViewNum pv2;
    private LinearLayout layout_3 ,layout_4;
    private boolean isVisible2 = true;
    private TextView mNumTextView;
    Schedule scheduleList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addschedule);
        Intent intent = getIntent();
        mScheduleType = intent.getStringExtra("date");
        initTool();
        mScheduleDefine = (Button) findViewById(R.id.define);
        mScheduleDefine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(10);
                insert();
            }
        });
        mScheduleCancel = (Button) findViewById(R.id.cancel);
        mScheduleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(10);
                finish();
            }
        });
    }

    public void insert(){
        EditText title =(EditText)findViewById(R.id.InputScheduleName);
        mScheduleName=title.getText().toString();
        mScheduleTime = mTvSelectedTime.getText().toString();
        mScheduleTimeNum=mNumTextView.getText().toString();
        EditText remark =(EditText)findViewById(R.id.InputRemark);
        mScheduleRemark=remark.getText().toString();
        if(TextUtils.isEmpty(title.getText())){
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("日程名称没有填写")
                    .setPositiveButton("确定", null)
                    .show();
        }else {
            try{
                scheduleList = mScheduleDao.queryBuilder().where(ScheduleDao.Properties.Memorandum.eq(mScheduleName)).unique();
                if(scheduleList.getMemorandum() != null){
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("有相同的日程存在")
                            .setPositiveButton("确定", null)
                            .show();
                }
            }catch (Exception e){
                if(mScheduleTime!=""){
                    int x = delString(mScheduleTime);
                    int j = Integer.valueOf(mScheduleTimeNum).intValue();
                    for(int i=0;i<=j;i++){
                        int y = x+i;
                        occupiedList = mOccupiedDao.queryBuilder().where(OccupiedDao.Properties.Time.eq(y)).unique();
                        occupiedList.setIsoccupied(true);
                    }
                    mOccupiedDao.update(occupiedList);
                }
                Schedule schedule = new Schedule(null,mScheduleName,mScheduleTime,mScheduleTimeNum,mScheduleRemark,mScheduleType);
                mScheduleDao.insert(schedule);
                finish();
            }
        }
    }
    public void initTool(){
        //标题栏返回键监听
        mScheduleButton = (ImageButton) findViewById(R.id.title_back);
        mScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initDate();
        //预计时间数目选择
        List<String> data2 = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            data2.add(i+"");
        }
        layout_3 = (LinearLayout) findViewById(R.id.RelativeLayout_id);
        layout_3.setVisibility(View.GONE);//这一句即隐藏布局LinearLayout区域
        layout_4 = (LinearLayout) findViewById(R.id.RelativeLayout);
        layout_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible2) {
                    isVisible2 = false;
                    layout_3.setVisibility(View.VISIBLE);//这一句显示布局LinearLayout区域
                } else {
                    layout_3.setVisibility(View.GONE);//这一句即隐藏布局LinearLayout区域
                    isVisible2 = true;
                }
            }
        });
        mNumTextView = (TextView) findViewById(R.id.selected_Num);
        mNumTextView.setText("0");
        pv2=findViewById(R.id.minute_pv);
        pv2.setData(data2);
        pv2.setOnSelectListener(new PickerViewNum.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mNumTextView.setText(text);
            }
        });
    }

    public int delString(String time){
         return Integer.valueOf(time.substring(0,2)).intValue();
    }

    /**
     *  捕捉返回键事件
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 当点击返回键以及点击重复次数为0
        if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){

            // 执行事件
            AddScheduleActivity.this.setResult(10);
            AddScheduleActivity.this.finish();
        }
        return false;
    }

    private void initDate(){
        findViewById(R.id.l2_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.l2_date:
                        // 日期格式为yyyy-MM-dd
                        if(mScheduleType.equals("今天"))
                            mDatePicker.show(mTvSelectedTime.getText().toString());
                        else if(mScheduleType.equals("明天"))
                            mDatePicker.show1(mTvSelectedTime.getText().toString());
                        break;
                }
            }
        });
        mTvSelectedTime = findViewById(R.id.selected_date2);
        initDatePicker();
    }
    private void initDatePicker() {
        String beginTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        //在现在时间基础上加一天
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.DAY_OF_WEEK,curr.get(Calendar.DAY_OF_WEEK)+1);
        Date date=curr.getTime();
        long endTimestamp = DateFormatUtils.dateToLong(date);
        String endTime = DateFormatUtils.long2Str(endTimestamp,true);
        String selectTime = beginTime.substring(11,16);
        mTvSelectedTime.setText(selectTime);

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mDatePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                String x = DateFormatUtils.long2Str(timestamp,true);
                String y = x.substring(11,16);
                mTvSelectedTime.setText(y);
            }
        }, beginTime, endTime);
        // 不允许点击屏幕或物理返回键关闭
        mDatePicker.setCancelable(true);
        // 不显示时和分
        mDatePicker.setCanShowPreciseTime(true);
        // 不允许循环滚动
        mDatePicker.setScrollLoop(false);
        // 不允许滚动动画
        mDatePicker.setCanShowAnim(true);
    }
}
