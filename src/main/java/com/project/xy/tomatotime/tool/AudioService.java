package com.project.xy.tomatotime.tool;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.project.xy.tomatotime.R;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener {
    // 实例化MediaPlayer对象
    MediaPlayer player;
    private final IBinder binder = new AudioBinder();
    int id;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        id = intent.getIntExtra("music_id",1);
        if(id == 1){
            player = MediaPlayer.create(this, R.raw.awake);
            player.setOnCompletionListener(this);
            player.setLooping(true);
        }else if(id == 2){
            player = MediaPlayer.create(this, R.raw.il_vento_d_oro);
            player.setOnCompletionListener(this);
            player.setLooping(true);
        }else if(id == 3){
            player = MediaPlayer.create(this, R.raw.lotta_feroce);
            player.setOnCompletionListener(this);
            player.setLooping(true);
        }
        if (!player.isPlaying()) {
            new MusicPlayThread().start();
        }
        else player.isPlaying();
        return START_STICKY;
    }


    /**
     * 当Audio播放完的时候触发该动作
     */
    public void onCompletion(MediaPlayer mp) {
        stopSelf();// 结束了，则结束Service

    }

    public void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
    }

    // 为了和Activity交互，我们需要定义一个Binder对象
    public class AudioBinder extends Binder {
        // 返回Service对象
        public AudioService getService() {
            return AudioService.this;
        }
    }

    private class MusicPlayThread extends Thread {
        public void run() {
            if (!player.isPlaying()) {
                player.start();
            }
        }
    }
}

