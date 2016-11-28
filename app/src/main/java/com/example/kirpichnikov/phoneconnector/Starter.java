package com.example.kirpichnikov.phoneconnector;

import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



/**
 * Created by kirpichnikov on 18.11.2016.
 */
public class Starter extends BroadcastReceiver {

    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context=context;
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if  (device.getName().equals("POR 1007BT")) {

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                if (!isMyServiceRunning(BluetoothService.class)) {
                    context.startService(new Intent(context, BluetoothService.class));
                }

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if (isMyServiceRunning(BluetoothService.class)) {
                    context.stopService(new Intent(context, BluetoothService.class));
                }

            }
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
