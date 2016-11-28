package com.example.kirpichnikov.phoneconnector;

import android.app.Service;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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


    public void onCreate() {
       super.onCreate();
        es=Executors.newFixedThreadPool(1);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        Connector connector = new Connector(startId);
        connector.run();
        return START_STICKY;
    }




    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    class Connector implements Runnable{
        boolean state;
        int counter;
        private BluetoothAdapter mAdapter;
        private BluetoothDevice btController;
        private BluetoothSocket btControllerSocket;
        private int startId;
        UUID mUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        public final static String BROADCAST_ACTION="ru.phoneconnector.CONTROLLER_IS_UP";

        public Connector(int startId){
            //
            this.startId=startId;
        }

        public void run(){
            mAdapter = BluetoothAdapter.getDefaultAdapter();

            btController = mAdapter.getRemoteDevice("98:D3:33:80:CD:68");


            counter=0;
            while (!state && counter<10) {
                try {
                    btControllerSocket = btController.createRfcommSocketToServiceRecord(mUuid);
                    mAdapter.cancelDiscovery();
                    btControllerSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (btControllerSocket.isConnected()) {

                    try {
                        btControllerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    state = true;
                    Intent intent = new Intent(BROADCAST_ACTION);
                    sendBroadcast(intent);


                }
                else {
                    counter++;

                }
            }
            stopSelf(startId);
        }
        public void stop(){
            stopSelf(startId);
        }



    }

}

