package com.example.kirpichnikov.phoneconnector;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by kirpichnikov on 17.11.2016.
 */

public class MusicService extends Service {

    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    final String LOG_TAG = "myLogs";
    public final static String BROADCAST_ACTION="com.example.kirpichnikov.phoneconnector.CONTROLLER_BUTTON_PRESSED";
    BroadcastReceiver br;
    MediaPlayer mpMusic;
    AFListener afListenerMusic;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    public void onCreate() {
        super.onCreate();


        br = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String command = intent.getStringExtra("COMMAND");

                mpMusic = new MediaPlayer();
                try {
                    String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
                   // dir=dir+"/Sounds/Alan Walker - Faded.mp3";
                   // dir = "storage/3037-3562/Sounds/Alan Walker - Faded.mp3";
                    dir = "sdcard/Download/Alan Walker - Faded.mp3";
                    mpMusic.setDataSource(dir);
                    mpMusic.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mpMusic.setOnCompletionListener(this);

                mpMusic.start();
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void button (){

    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    void someTask(){
        new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i<=5; i++) {
                    Log.d(LOG_TAG, "i = " + i);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
            }
        }).start();
    }

    class AFListener implements AudioManager.OnAudioFocusChangeListener {

        String label = "";
        MediaPlayer mp;

        public AFListener(MediaPlayer mp, String label) {
            this.label = label;
            this.mp = mp;
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            String event = "";
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    event = "AUDIOFOCUS_LOSS";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    event = "AUDIOFOCUS_LOSS_TRANSIENT";
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    event = "AUDIOFOCUS_GAIN";
                    break;
            }
            Log.d(LOG_TAG, label + " onAudioFocusChange: " + event);
        }
    }
}
