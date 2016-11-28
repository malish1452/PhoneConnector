package com.example.kirpichnikov.phoneconnector;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothComandProcessor extends Service {
    private BluetoothAdapter mAdapter;
    private BluetoothDevice btController;
    private BluetoothSocket btControllerSocket;
    UUID mUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ExecutorService es;


    public final static String BROADCAST_ACTION="ru.phoneconnector.CONTROLLER_IS_UP";

    public IBinder onBind(Intent intent) {
        return null;
    }


    public void onCreate() {
        super.onCreate();
        es=Executors.newFixedThreadPool(1);


    }

    public int onStartCommand(Intent intent, int flags, int startId) {

       RunablePart runablePart=new RunablePart();
        runablePart.run();

        return START_STICKY;
    }
    public void onDestroy(){

    }



    class RunablePart implements Runnable{

        private BluetoothAdapter mAdapter;
        private BluetoothDevice btController;
        private BluetoothSocket btControllerSocket;
        int startId;
        UUID mUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        public final static String BROADCAST_ACTION="ru.phoneconnector.CONTROLLER_BUTTON_PRESSED";

        public RunablePart (){

        }

        @Override

        public void run(){

            mAdapter = BluetoothAdapter.getDefaultAdapter();

            btController = mAdapter.getRemoteDevice("98:D3:33:80:CD:68");

            try {
                btControllerSocket=btController.createRfcommSocketToServiceRecord(mUuid);
                mAdapter.cancelDiscovery();
                btControllerSocket.connect();
            } catch (IOException e) {
                Log.i("PhoneC","ConnectError ");
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

                            gotCommand=true;
                        }
                        if (gotCommand) {
                            if (prevCommand!=null){ timeStampedMessage=prevCommand.toString()+",\n"+timeStampedMessage;}
                            timeStampedMessage = "[" + timeStampedMessage.substring(0, timeStampedMessage.lastIndexOf(',')) + "]";

                            JSONArray jsonArray = new JSONArray(timeStampedMessage);

                            for (int i = 0; i < jsonArray.length()-1; i++) {
                                long  deff = jsonArray.getJSONObject(i+1).optLong("timeReceive") - jsonArray.getJSONObject(i).optLong("timeReceive");
                                jsonArray.getJSONObject(i).put("duration", deff);

                                SendCommand(jsonArray.getJSONObject(i));
                            }



                            prevCommand=jsonArray.getJSONObject(jsonArray.length()-1);
                        }
                        Thread.sleep(100);
                    }

                } catch (IOException e) {
                    e.printStackTrace();


                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (InterruptedException e) {
                    e.printStackTrace();

                }


            }


        }

        private void SendCommand(JSONObject input) {


            //1 - отпущено
            //2 - Mute
            //3 - Mode
            //4 - down
            //5 - up
            //6 - volume +
            //7 - volume -

            try {
                String type = input.getString("type");
                if (type.equals("button")) {
                    int buttonId = input.getInt("id");
                    long duration = input.getLong("duration");
                    Intent buttonIntent = new Intent(BROADCAST_ACTION);
                    if (buttonId > 1) {
                        if (buttonId == 4) {
                            if (duration < 2000) {
                                buttonIntent.putExtra("COMMAND", "NEXT");
                            } else {
                                buttonIntent.putExtra("COMMAND", "NEXT");
                            }
                        }
                        if (buttonId == 5) {
                            if (duration < 2000) {
                                buttonIntent.putExtra("COMMAND", "BEGIN");
                            } else {
                                buttonIntent.putExtra("COMMAND", "PREVIOUS");
                            }
                        }
                        if (buttonId == 6) {
                            buttonIntent.putExtra("COMMAND", "VOLUME_UP");
                        }
                        if (buttonId == 7) {
                            buttonIntent.putExtra("COMMAND", "VOLUME_DOWN");
                        }


                        sendBroadcast(buttonIntent);

                    }
                }

            }
            catch(JSONException e){
                e.printStackTrace();
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
    }
}
