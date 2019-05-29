package com.xpg.manager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothDriver implements IDriver {
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int REQUEST_ENABLE_BT = 3;
    private static BluetoothAdapter adapter = null;
    private static BluetoothDriver instance = null;
    private static boolean isBluetoothOpen = false;
    private static final long serialVersionUID = 1;
    private Context context;
    private MobileCommunicationDelegate delegate;
    private Map<String, BluetoothDevice> devices = new HashMap();
    private InputStream inputStream;
    private final BroadcastReceiver mReceiver = new C00871();
    private OutputStream outputStream;
    private BluetoothSocket socket;

    /* renamed from: com.xpg.manager.BluetoothDriver$1 */
    class C00871 extends BroadcastReceiver {
        C00871() {
        }

        public void onReceive(Context context, Intent intent) {
            if (BluetoothDriver.isBluetoothOpen) {
                try {
                    String action = intent.getAction();
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    if (!BluetoothDriver.this.deviceIsExisted(device)) {
                        BluetoothDriver.this.devices.put(device.getAddress(), device);
                        if ("android.bluetooth.device.action.FOUND".equals(action)) {
                            BluetoothDriver.this.delegate.deviceFounded(device);
                        } else if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(action)) {
                            BluetoothDriver.this.delegate.stateChanged(device.getBondState(), device.getName(), device.getAddress());
                        } else if (!"android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                            "android.bluetooth.device.action.ACL_DISCONNECTED".equals(action);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void setMobileDelegate(MobileCommunicationDelegate delegate) {
        this.delegate = delegate;
    }

    public void request() {
        ((Activity) this.context).startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
    }

    public Map<String, Object> connect(String macAddress) {
        Map<String, Object> result = new HashMap();
        this.outputStream = null;
        this.inputStream = null;
        if (adapter != null) {
            adapter.cancelDiscovery();
            BluetoothDevice device = (BluetoothDevice) this.devices.get(macAddress);
            if (device != null) {
                try {
                    this.socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    this.socket.connect();
                    this.outputStream = this.socket.getOutputStream();
                    this.inputStream = this.socket.getInputStream();
                    result.put("OUTPUTSTREAM", this.outputStream);
                    result.put("INPUTSTREAM", this.inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void disConnect() {
        try {
            if (this.outputStream != null) {
                this.outputStream.flush();
                this.outputStream.close();
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(boolean isToRegister) {
        if (isToRegister) {
            IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
            filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
            filter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
            filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
            filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
            filter.addAction("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
            filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            if (((Activity) this.context) != null) {
                ((Activity) this.context).registerReceiver(this.mReceiver, filter);
            }
        } else if (((Activity) this.context) != null) {
            ((Activity) this.context).unregisterReceiver(this.mReceiver);
        }
    }

    public void finding() {
        adapter.startDiscovery();
        for (BluetoothDevice device : adapter.getBondedDevices()) {
            this.devices.put(device.getAddress(), device);
            this.delegate.deviceFounded(device);
        }
    }

    public void setContext(Context context) {
        this.context = (Activity) context;
    }

    public Set<?> getExistedDevice() {
        return adapter.getBondedDevices();
    }

    public boolean deviceIsExisted(BluetoothDevice device) {
        for (BluetoothDevice bondedDevice : adapter.getBondedDevices()) {
            if (bondedDevice.getAddress().equals(device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    public boolean isOpen() {
        return isBluetoothOpen;
    }

    public static BluetoothDriver getInstance() {
        if (instance == null) {
            instance = new BluetoothDriver();
        }
        adapter = BluetoothAdapter.getDefaultAdapter();
        isBluetoothOpen = adapter.isEnabled();
        return instance;
    }
}
