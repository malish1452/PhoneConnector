package com.example.kirpichnikov.phoneconnector;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CommandStarter extends BroadcastReceiver {

    private Context context;
    public CommandStarter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;

        if (!isMyServiceRunning(BluetoothComandProcessor.class)){
            context.startService(new Intent(context,BluetoothComandProcessor.class));
            }


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
