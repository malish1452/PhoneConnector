package com.example.kirpichnikov.phoneconnector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothComandProcessor extends Service {
    public BluetoothComandProcessor() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
