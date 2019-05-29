package com.example.manuelj.myblob2;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.xpg.convertor.ProtocolConstant;
import com.xpg.convertor.ProtocolConvertor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ConnectThread extends Thread{
    private BluetoothSocket bTSocket;
    private ProtocolConvertor convertor;
    private byte[] bdata;
    private Socket socket;
    private DataInputStream input;
    public DataOutputStream out;
    public DataInputStream in;
    private ManageConnectThread mt;

    ConnectThread(Context context){
        convertor = new ProtocolConvertor().initConfig(context.getResources().openRawResource(R.raw.config), "Silverlit BTFerrari");

    }


    public boolean connect(BluetoothDevice bTDevice, UUID mUUID) throws IOException {




        BluetoothSocket temp = null;
        try {
            temp = bTDevice.createInsecureRfcommSocketToServiceRecord(mUUID);
            bTSocket = temp;
        } catch (IOException e) {
            Log.d("CONNECTTHREAD","Could not create RFCOMM socket:" + e.toString());
            return false;
        }
        try {


            bTSocket.connect();
            mt = new ManageConnectThread();




            ManageConnectThread mt = new ManageConnectThread();

            for(int i=0; i<100;i++){
                bdata = getData(80,0.0f);
                mt.sendData(bTSocket,bdata);
                Log.d("CONNECTTHREAD",String.valueOf(i));
                Thread.sleep(50);
            }

//            for(int i=0; i<55;i++){
//                bdata = getData(0.0f,127.5f);
//                mt.sendData(bTSocket,bdata);
//                Log.d("CONNECTTHREAD",String.valueOf(i));
//                Thread.sleep(50);
//            }
//
//            for(int i=0; i<55;i++){
//                bdata = getData(127.5f+i,127.5f);
//                mt.sendData(bTSocket,bdata);
//                Log.d("CONNECTTHREAD",String.valueOf(i));
//                Thread.sleep(50);
//            }
//
//            for(int i=0; i<50;i++){
//                bdata = getData(127.5f-i,127.5f);
//                mt.sendData(bTSocket,bdata);
//                Log.d("CONNECTTHREAD",String.valueOf(i));
//                Thread.sleep(50);
//            }





            bdata = getData(157.5f,127.5f);
            mt.sendData(bTSocket,bdata);
            Log.d("CONNECTTHREAD","Sent ON");
            Thread.sleep(1000);

        } catch(IOException e) {
            Log.d("CONNECTTHREAD","Could not connect: " + e.toString());
            try {
                bTSocket.close();
            } catch(IOException close) {
                Log.d("CONNECTTHREAD", "Could not close connection:" + e.toString());
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean cancel() {
        try {
            bTSocket.close();
        } catch(IOException e) {
            Log.d("CONNECTTHREAD","Could not close connection:" + e.toString());
            return false;
        }
        return true;
    }

    public byte[] getData(float pitch, float yaw){
        Map<String, Float> remoteDataMap = new HashMap();
        remoteDataMap.put(ProtocolConstant.PITCH_KEY, pitch);
        remoteDataMap.put(ProtocolConstant.YAW_KEY, yaw);
        remoteDataMap.put(ProtocolConstant.TRIMER_KEY, 7f);
        remoteDataMap.put(ProtocolConstant.LIGHT_KEY, 0f);
        remoteDataMap.put(ProtocolConstant.MATCH_KEY, 1f);
        byte[] tmp = convertFromDataToProtocol(remoteDataMap);
        return tmp;
    }

    public byte[] convertFromDataToProtocol(Map<String, Float> remoteDataMap) {
        int[] sendInts = null;
        Iterator<?> it = remoteDataMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            sendInts = convertor.convertToInts(key, ((Float) remoteDataMap.get(key)).floatValue(), sendInts, false);
        }
        int[] ints = new int[this.convertor.byteTotal];
        int i;
        if (this.convertor.reverse == 1) {
            for (i = 0; i < sendInts.length; i++) {
                ints[i] = (byte) sendInts[sendInts.length - (i + 1)];
            }
        } else {
            for (i = 0; i < sendInts.length; i++) {
                ints[i] = (byte) sendInts[i];
            }
        }
        return this.convertor.stringToBytes(new StringBuilder(String.valueOf(this.convertor.header)).append(this.convertor.intsToString(ints)).toString());
    }
}



