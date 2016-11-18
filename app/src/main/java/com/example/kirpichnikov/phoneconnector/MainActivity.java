package com.example.kirpichnikov.phoneconnector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.media.session.MediaSession;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mAdapter;
    private Set<BluetoothDevice> pairedDevices;
    ArrayList list = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView devicelist = (ListView)findViewById(R.id.listView);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isEnabled()){
            mAdapter.enable();
        };

        pairedDevices= mAdapter.getBondedDevices();


        if (pairedDevices.size()>0)
        {


            for(int i=0;i<=pairedDevices.size();i++){
                BluetoothDevice bt = (BluetoothDevice) pairedDevices.toArray()[i];
               if (bt.getName()=="HC-05") {

                    list.add(bt.getName() + "\n" + bt.getAddress() + "\n" + bt.getBondState()+"\n"+bt.getUuids().toString());
               }//Get the device's name and the address
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
      //  devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }


    public void onClickStart(View view) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBtIntent);

    }

    public void onClickStop(View view) {
        stopService(new Intent(this,MusicService.class));
    }
}
