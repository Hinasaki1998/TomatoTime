package com.project.xy.tomatotime.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.project.xy.tomatotime.R;
import com.project.xy.tomatotime.tool.AudioService;
import com.project.xy.tomatotime.tool.CountDownTimerListener;
import com.project.xy.tomatotime.tool.CountDownTimerService;
import com.project.xy.tomatotime.tool.CountDownTimerUtil;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;


public class TomatoActivity extends Activity implements View.OnClickListener {
    //service countdown
    private Button btnServiceStart;
    private Button btnServiceStop;
    private Button btnServiceReStart;
    private TextView tvServiceTime;
    private TextView tvServiceTime1;
    private TextView toma_planname;

    private ImageButton btnMusic;
    private static int musicId;
    private static boolean isshow;
    private String mPlanName_to;//标题
    private static String mPlanName_to1;//标题

    private ImageButton btnTextShow;
    private boolean istextshow;

    //是否显示button
    LinearLayout btn_linear1;
    LinearLayout btn_linear2;
    LinearLayout text_linear1;
    LinearLayout text_linear2;
    LinearLayout text_linear3;

    private long timer_unit = 1000;
    private long distination_total = timer_unit*10;
    private long service_distination_total = timer_unit*1500;
    private long timer_couting;


    private int timerStatus = CountDownTimerUtil.PREPARE;

    private Timer timer;
    private TimerTask timerTask;

    private CountDownTimerService countDownTimerService;


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    tvServiceTime.setText(formateTimer(countDownTimerService.getCountingTime()));
                    tvServiceTime1.setText(formateTimer(countDownTimerService.getCountingTime()));
                    if(countDownTimerService.getTimerStatus()==CountDownTimerUtil.PREPARE){
                        btnServiceStart.setText("开始");
                    }
                    break;
            }
        }
    };

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class MyCountDownLisener implements CountDownTimerListener {

        @Override
        public void onChange() {
            mHandler.sendEmptyMessage(2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomato);

        initTimerStatus();
        btn_linear1 = (LinearLayout) findViewById(R.id.ButtonShow1);
        btn_linear2 = (LinearLayout) findViewById(R.id.ButtonShow2);
        text_linear1 = (LinearLayout) findViewById(R.id.textShow1);
        text_linear2 = (LinearLayout) findViewById(R.id.textShow2);
        text_linear3 = (LinearLayout) findViewById(R.id.plannameshow);
        btnServiceStart = (Button) findViewById(R.id.normal_start);
        btnServiceReStart = (Button) findViewById(R.id.normal_restart);
        btnServiceStop = (Button) findViewById(R.id.normal_finish);
        tvServiceTime = (TextView) findViewById(R.id.timeView);
        tvServiceTime1 = (TextView) findViewById(R.id.FocustimeView);
        btnTextShow = (ImageButton) findViewById(R.id.normal_model);

        btnMusic = (ImageButton) findViewById(R.id.normal_music);

        Intent intent = getIntent();
        mPlanName_to = intent.getStringExtra("tomatodate");
        toma_planname = (TextView) findViewById(R.id.toma_planname);
        if(mPlanName_to!=null){
            mPlanName_to1 = mPlanName_to;
        }
        if(mPlanName_to1!=null){
            toma_planname.setText(mPlanName_to1);
            text_linear3.setVisibility(View.VISIBLE);
            Intent i = new Intent(TomatoActivity.this,CountDownTimerService.class);
            i.putExtra("CountTime",mPlanName_to);
            startService(i);
        }

        btnServiceStart.setOnClickListener(this);
        btnServiceReStart.setOnClickListener(this);
        btnServiceStop.setOnClickListener(this);

        btnTextShow.setOnClickListener(this);
        btnMusic.setOnClickListener(this);

        countDownTimerService = CountDownTimerService.getInstance(new MyCountDownLisener()
                ,service_distination_total);
        initServiceCountDownTimerStatus();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.normal_start:
                switch (countDownTimerService.getTimerStatus()){
                    case CountDownTimerUtil.PREPARE:
                        countDownTimerService.startCountDown();
                        btnServiceStart.setText("暂停");
                        break;
                    case CountDownTimerUtil.START:
                        countDownTimerService.pauseCountDown();
                        btn_linear1.setVisibility(View.GONE);
                        btn_linear2.setVisibility(View.VISIBLE);
                        if(isshow == true)
                            stop();
                        break;
                }
                break;
            case R.id.normal_restart:
                countDownTimerService.startCountDown();
                btn_linear1.setVisibility(View.VISIBLE);
                btn_linear2.setVisibility(View.GONE);
                if(isshow == true)
                    start(musicId);
                break;
            case R.id.normal_finish:
                countDownTimerService.stopCountDown();
                btn_linear1.setVisibility(View.VISIBLE);
                btn_linear2.setVisibility(View.GONE);
                mPlanName_to1 = null;
                TomatoActivity.this.stopService(new
                        Intent(TomatoActivity.this,
                        CountDownTimerService.class));
                if(isshow == true)
                    stop();
                break;
            case R.id.normal_music:
                // 这里的view代表popupMenu需要依附的view
                backgroundAlpha(0.5f);
                PopupMenu popupMenu = new PopupMenu(TomatoActivity.this, v);
                // 获取布局文件
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.show();
                // 通过上面这几行代码，就可以把控件显示出来了
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override public boolean onMenuItemClick(MenuItem item) {
                        // 控件每一个item的点击事件
                        switch (item.getItemId()){
                            case R.id.music_stop:
                                isshow=false;
                                stop();
                                break;
                            case R.id.music_1:
                                isshow=true;
                                stop();
                                musicId = 1;
                                start(musicId);
                                Log.e("启动","音乐一");
                                break;
                            case R.id.music_2:
                                isshow=true;
                                stop();
                                musicId = 2;
                                start(musicId);
                                Log.e("启动","音乐二");
                                break;
                            case R.id.music_3:
                                isshow=true;
                                stop();
                                musicId = 3;
                                start(musicId);
                                Log.e("启动","音乐三");
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override public void onDismiss(PopupMenu menu) {
                        // 控件消失时的事件
                        backgroundAlpha(1f);
                    }
                });
                break;
            case R.id.normal_model:
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(option);
                text_linear1.setVisibility(View.GONE);
                text_linear2.setVisibility(View.VISIBLE);
                istextshow = true;
                break;
        }
    }
    //开始播放音乐
    public void start(int id){
        Intent intent = new Intent(TomatoActivity.this,AudioService.class);
        intent.putExtra("music_id",id);
        startService(intent);
    }
    //停止音乐
    public void stop(){
        Intent intent = new Intent(TomatoActivity.this,AudioService.class);
        stopService(intent);
    }

     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * init timer status
     */
    private void initTimerStatus(){
        timerStatus = CountDownTimerUtil.PREPARE;
        timer_couting = distination_total;
    }

    /**
     * start count down
     */
    private void startTimer(){
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, timer_unit);
    }

    /**
     * formate timer shown in textview
     * @param time
     * @return
     */
    private String formateTimer(long time){
        String str = "00:00";
        int hour = 0;
        if(time>=1000*3600){
            hour = (int)(time/(1000*3600));
            time -= hour*1000*3600;
        }
        int minute = 0;
        if(time>=1000*60){
            minute = (int)(time/(1000*60));
            time -= minute*1000*60;
        }
        int sec = (int)(time/1000);
        str = formateNumber(minute)+":"+formateNumber(sec);
        return str;
    }

    /**
     * formate time number with two numbers auto add 0
     * @param time
     * @return
     */
    private String formateNumber(int time){
        return String.format("%02d", time);
    }


    /**
     * count down task
     */
    private class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            timer_couting -=timer_unit;
            if(timer_couting==0){
                cancel();
                initTimerStatus();
            }
            mHandler.sendEmptyMessage(1);
        }
    }
    /**
     *  捕捉返回键事件
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 当点击返回键以及点击重复次数为0
        if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            // 执行事件
            if(istextshow == true){
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(option);
                text_linear1.setVisibility(View.VISIBLE);
                text_linear2.setVisibility(View.GONE);
                istextshow = false;
            }else
                TomatoActivity.this.finish();
        }
        return false;
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = TomatoActivity.this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        TomatoActivity.this.getWindow().setAttributes(lp);
    }

    /**
     * init countdowntimer buttons status for servce
     */
    private void initServiceCountDownTimerStatus(){
        switch (countDownTimerService.getTimerStatus()) {
            case CountDownTimerUtil.PREPARE:
                btnServiceStart.setText("开始");
                break;
            case CountDownTimerUtil.START:
                btnServiceStart.setText("暂停");
                break;
            case CountDownTimerUtil.PASUSE:
                btnServiceReStart.setText("继续");
                btn_linear1.setVisibility(View.GONE);
                btn_linear2.setVisibility(View.VISIBLE);
                break;
        }
        tvServiceTime.setText(formateTimer(countDownTimerService.getCountingTime()));
        tvServiceTime1.setText(formateTimer(countDownTimerService.getCountingTime()));
    }
}
