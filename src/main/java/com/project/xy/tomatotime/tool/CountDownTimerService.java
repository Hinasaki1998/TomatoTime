package com.project.xy.tomatotime.tool;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.project.xy.tomatotime.Entity.Plan;
import com.project.xy.tomatotime.MyApplication;
import com.project.xy.tomatotime.activity.TomatoActivity;
import com.project.xy.tomatotime.greendao.PlanDao;

import java.util.Timer;
import java.util.TimerTask;

public class CountDownTimerService extends Service {

    private static final long timer_unit =1000;
    private static long mDistination_total;
    private Timer timer;
    private MyTimerTask timerTask;

    //数据库
    PlanDao mPlanDao = MyApplication.getInstances().getDaoSession().getPlanDao();
    private static String CountPlanName;
    Plan TomaplanList;
    private String CountPlanNum;//番茄钟数目

    private static long timer_couting = 0;
    private static long timer_rustcouting = 0;

    private int timerStatus = CountDownTimerUtil.PREPARE;

    public static CountDownTimerService countDownTimerService;

    private static CountDownTimerListener mCountDownTimerListener;

    public static CountDownTimerService getInstance(CountDownTimerListener countDownTimerListener
            ,long distination_total){
        if(countDownTimerService==null){
            countDownTimerService = new CountDownTimerService();
        }
        setCountDownTimerListener(countDownTimerListener);
        mDistination_total = distination_total;
        if(timer_couting==0) {
            timer_couting = mDistination_total;
            timer_rustcouting = 1800*timer_unit;
        }
        return  countDownTimerService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(CountPlanName==null)
            CountPlanName = intent.getStringExtra("CountTime");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * get countdowan time
     * @return
     */
    public long getCountingTime(){
        if(timer_couting == 0)
            return timer_rustcouting;
        else
            return timer_couting;
    }

    /**
     * get current timer status
     * @return
     */
    public int getTimerStatus(){
        return  timerStatus;
    }

    /**
     * start
     */
    public void startCountDown(){
        startTimer();
        timerStatus = CountDownTimerUtil.START;
    }

    /**
     * paust
     */
    public void pauseCountDown(){
        timer.cancel();
        timerStatus = CountDownTimerUtil.PASUSE;
    }

    /**
     * stop
     */
    public void stopCountDown(){
        if(timer!=null){
            timer.cancel();
            CountPlanName = null;
            initTimerStatus();
            mCountDownTimerListener.onChange();
        }
    }

    public static void  setCountDownTimerListener(CountDownTimerListener countDownTimerListener){
        mCountDownTimerListener = countDownTimerListener;
    }

    /**
     * count down task
     */
    private class MyTimerTask extends TimerTask {


        @Override
        public void run() {
            if(timer_couting != 0)
                timer_couting -=timer_unit;
            timer_rustcouting -= timer_unit;
            mCountDownTimerListener.onChange();

            if(CountPlanName!= null && timer_couting==0) {
                TomaplanList = mPlanDao.queryBuilder().where(PlanDao.Properties.PlanTitle.eq(CountPlanName)).unique();
                CountPlanNum = TomaplanList.getPlanTomatoNum();
                int mTomatoNum = Integer.valueOf(CountPlanNum).intValue();
                if(mTomatoNum != 0){
                    mTomatoNum = mTomatoNum - 1;
                    Plan findUser = new Plan(TomaplanList.getId(), TomaplanList.getPlanTitle(), TomaplanList.getPlanLevel(), TomaplanList.getEndTime(), mTomatoNum + "", TomaplanList.getRemarks());
                    mPlanDao.update(findUser);
                    CountPlanName = null;
                }
            }
            if(timer_couting==0 && timer_rustcouting==0){
                cancel();
                initTimerStatus();
            }
        }
    }

    /**
     * init timer status
     */
    private void initTimerStatus(){
        timer_couting = mDistination_total;
        timer_rustcouting = 20*timer_unit;
        timerStatus = CountDownTimerUtil.PREPARE;
    }

    /**
     * start count down
     */
    private void startTimer(){
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 0, timer_unit);
    }
}
