package com.example.manuelj.myblob2;

import android.bluetooth.BluetoothSocket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ManageConnectThread extends Thread {

    public ManageConnectThread() { }

    public void sendData(BluetoothSocket socket, byte[] data) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(data);
    }

    public int receiveData(BluetoothSocket socket) throws IOException{
        byte[] buffer = new byte[4];
        ByteArrayInputStream input = new ByteArrayInputStream(buffer);
        InputStream inputStream = socket.getInputStream();
        inputStream.read(buffer);
        return input.read();
    }
}
