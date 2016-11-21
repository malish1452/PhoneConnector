package com.example.kirpichnikov.phoneconnector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.session.MediaSession;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice btController;
    private BluetoothSocket btControllerSocket;
    ArrayList list = new ArrayList();
    UUID mUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView devicelist = (ListView)findViewById(R.id.listView);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isEnabled()){
            mAdapter.enable();
        };
        btController = mAdapter.getRemoteDevice("98:D3:33:80:CD:68");

        try {
            btControllerSocket=btController.createRfcommSocketToServiceRecord(mUuid);
            mAdapter.cancelDiscovery();
            btControllerSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (btControllerSocket.isConnected())
        {   int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            try {
                InputStream iStream=btControllerSocket.getInputStream();
                int bytesRead;
                String rawMessage;
                String timeStampedMessage="";
                JSONObject prevCommand= null;
                while (true){
                    boolean gotCommand=false;
                    rawMessage="";
                    timeStampedMessage="";
                    if (iStream.available()>0){
                        while (!checkMessage(rawMessage)) {

                            while (iStream.available() > 0) {
                                bytesRead = iStream.read(buffer);
                                rawMessage = rawMessage + new String(buffer, 0, bytesRead);
                            }
                            btControllerSocket.getInputStream();
                        }
                        timeStampedMessage=timeStampedMessage+timeStampMessage(rawMessage.substring(0,rawMessage.lastIndexOf('}')+1));
                       // rawMessage=rawMessage.substring(rawMessage.lastIndexOf('}')+1);
                        gotCommand=true;
                     }
                    if (gotCommand) {
                        if (prevCommand!=null){ timeStampedMessage=prevCommand.toString()+",\n"+timeStampedMessage;}
                        timeStampedMessage = "[" + timeStampedMessage.substring(0, timeStampedMessage.lastIndexOf(',')) + "]";

                        JSONArray jsonArray = new JSONArray(timeStampedMessage);

                        for (int i = 1; i < jsonArray.length(); i++) {
                            long  deff = jsonArray.getJSONObject(i).optLong("timeReceive") - jsonArray.getJSONObject(i - 1).optLong("timeReceive");
                            jsonArray.getJSONObject(i).put("duration", deff);
                            Log.i("PC","Got command, duration "+ Long.toString(deff));
                        }
                        prevCommand=jsonArray.getJSONObject(jsonArray.length()-1);
                    }
                    //wait(100);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private boolean checkMessage(String input){
        int countOpen=0;
        int countClose=0;

        String tempString=input;
        while (tempString.indexOf('{')>=0){
            countOpen++;
            tempString=tempString.substring(tempString.indexOf('{')+1);
        }

        tempString=input;
        while (tempString.indexOf('}')>=0){
            countClose++;
            tempString=tempString.substring(tempString.indexOf('}')+1);
        }
        return (countOpen>0)&&(countOpen==countClose);
    }


    private String timeStampMessage(String input){

        String result="";
        try {
            while ((input!="")&&(input.indexOf('}')>0)) {
                Calendar c = Calendar.getInstance();
                JSONObject jsonObject = new JSONObject(input.substring(0, input.indexOf('}') + 1));
                jsonObject.put("timeReceive", c.getTimeInMillis());
                result = result  + jsonObject.toString()+ ",\n";
                input=input.substring(input.indexOf('}')+1);
                if (input.indexOf('{')>=0){
                    input=input.substring(input.indexOf('{'));}
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        return result;
    }

    public void onClickStart(View view) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBtIntent);

    }

    public void onClickStop(View view) {
        stopService(new Intent(this,MusicService.class));
    }
}
