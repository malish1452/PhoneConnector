package com.example.kirpichnikov.phoneconnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by kirpichnikov on 18.11.2016.
 */
public class Starter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,BluetoothService.class));
    }
}
