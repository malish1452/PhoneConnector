package com.example.kirpichnikov.phoneconnector;

import android.app.Service;
import android.bluetooth.*;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Set;


/**
 * Created by kirpichnikov on 16.11.2016.
 */

public class BluetoothService extends Service {

    private BluetoothAdapter mAdapter;
    private static final int REQUEST_ENABLE_BT=3;
    private Set pairedDevices;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    public void onCreate() {
        super.onCreate();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mAdapter.isEnabled()){
            mAdapter.enable();
        };

        pairedDevices= mAdapter.getBondedDevices();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startTask() {

       //

    }

}

