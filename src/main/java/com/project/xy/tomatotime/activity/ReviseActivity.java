package com.project.xy.tomatotime.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.project.xy.tomatotime.Entity.Plan;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.greendao.PlanDao;
import com.project.xy.tomatotime.tool.CustomDatePicker;
import com.project.xy.tomatotime.tool.DateFormatUtils;
import com.project.xy.tomatotime.tool.PickerViewNum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReviseActivity extends Activity {
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
        Intent intent = getIntent();
        mPlanName = intent.getStringExtra("date");
        initDate(mPlanName);
        initTool();

        mPlanDefine = (Button) findViewById(R.id.define);
        mPlanDefine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
                finish();
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

    public void initDate(String title){
        planList = mPlanDao.queryBuilder().where(PlanDao.Properties.PlanTitle.eq(title)).unique();
        mPlanName = planList.getPlanTitle();
        mPlanLevl = planList.getPlanLevel();
        mPlanTime = planList.getEndTime();
        mPlanNum = planList.getPlanTomatoNum();
        mPlanRemark = planList.getRemarks();

        EditText name =  (EditText) findViewById(R.id.InputPlanName);
        name.setText(mPlanName);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        setSpinnerDefaultValue(spinner,mPlanLevl);
        TextView time =  (TextView) findViewById(R.id.selected_date);
        time.setText(mPlanTime);
        TextView num =  (TextView) findViewById(R.id.selected_Num);
        num.setText(mPlanNum);
        EditText remark =  (EditText) findViewById(R.id.InputRemark);
        remark.setText(mPlanRemark);
    }
    public void insert() {
        EditText title = (EditText) findViewById(R.id.InputPlanName);
        mPlanName = title.getText().toString();
        Spinner level = (Spinner) findViewById(R.id.spinner);
        mPlanLevl = level.getSelectedItem().toString();
        mPlanTime = mTvSelectedDate.getText().toString();
        mPlanNum = mNumTextView.getText().toString();
        EditText remark = (EditText) findViewById(R.id.InputRemark);
        mPlanRemark = remark.getText().toString();
        Plan findUser = new Plan(planList.getId(), mPlanName, mPlanLevl, mPlanTime, mPlanNum, mPlanRemark);
        findUser.setId(findUser.getId());
        findUser.setPlanTitle(mPlanName);
        findUser.setPlanLevel(mPlanLevl);
        findUser.setEndTime(mPlanTime);
        findUser.setPlanTomatoNum(mPlanNum);
        findUser.setRemarks(mPlanRemark);
        mPlanDao.update(findUser);
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
    /**
     * spinner 接收默认值的Spinner
     * value 需要设置的默认值
     */
    private void setSpinnerDefaultValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter();
        int size = apsAdapter.getCount();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(value, apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i,true);
                break;
            }
        }
    }
}
