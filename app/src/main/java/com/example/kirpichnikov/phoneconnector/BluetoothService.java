package com.example.kirpichnikov.phoneconnector;

import android.app.Service;
import android.bluetooth.*;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by kirpichnikov on 16.11.2016.
 */

public class BluetoothService extends Service {

    ExecutorService es;

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
        es = Executors.newFixedThreadPool(1);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        RunablePart runablePart = new RunablePart(startId);
        es.execute(runablePart);
        return Service.START_STICKY;
    }


    class RunablePart implements Runnable{
        private BluetoothAdapter mAdapter;
        private BluetoothDevice btController;
        private BluetoothSocket btControllerSocket;
        UUID mUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        int startId;

        public RunablePart (int startId){
            this.startId=startId;
        }
        public void run(){
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            boolean connected=false;
            int count=0;
            while (!connected)
            {
                connected = true;
                if (!mAdapter.isEnabled())
                {
                    mAdapter.enable();
                }

                btController = mAdapter.getRemoteDevice("98:D3:33:80:CD:68");

                try
                {
                    btControllerSocket = btController.createRfcommSocketToServiceRecord(mUuid);
                    mAdapter.cancelDiscovery();
                    btControllerSocket.connect();
                }
                catch (IOException e)
                {
                    connected = false;
                }

                count++;

                try
                {
                    if (count < 10) {
                        Thread.sleep(500);
                        Log.i("PhoneC","Not Connected!!! Sleep 500");
                    } else {
                        Thread.sleep(30000);
                        Log.i("PhoneC","Not Connected!!! Sleep 30000");
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            if (btControllerSocket.isConnected()){
                Log.i("PhoneC","Connected!!!");
                startService(new Intent(this,com.example.kirpichnikov.phoneconnector.BluetoothComandProcessor.class));
                stop();
            }
        }

        void stop(){
            stopSelf(startId);
        }
    }

}

