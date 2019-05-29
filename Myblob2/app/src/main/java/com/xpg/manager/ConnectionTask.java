package com.xpg.manager;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class ConnectionTask extends AsyncTask<String, Void, Map<String, Object>> {
    private static final String INPUT_STREAM = "INPUTSTREAM";
    private static final String OUTPUT_STREAM = "OUTPUTSTREAM";
    private ConnectionDelegate delegate;
    private String deviceName;

    public ConnectionTask setDelegate(ConnectionDelegate delegate) {
        this.delegate = delegate;
        return this;
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(Map<String, Object> result) {
        if (result == null || result.size() == 0) {
            this.delegate.connectionWasFailed(this.deviceName);
            return;
        }
        Log.d("连接", "result:" + result.size());
        this.delegate.connectionWasSetup(this.deviceName, result);
        MobileController.defaultController().setOutputStream((OutputStream) result.get(OUTPUT_STREAM));
        MobileController.defaultController().setInputStream((InputStream) result.get(INPUT_STREAM));
    }

    protected Map<String, Object> doInBackground(String... params) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IDriver driver = BluetoothDriver.getInstance();
        Map<String, Object> streams = null;
        try {
            this.deviceName = params[0];
            streams = driver.connect(params[1]);
            Log.e("connecton success", "--" + params[1] + "----");
            return streams;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("connecton fail", "------");
            return streams;
        }
    }
}
