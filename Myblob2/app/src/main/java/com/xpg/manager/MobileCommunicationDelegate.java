package com.xpg.manager;

import android.bluetooth.BluetoothDevice;

public interface MobileCommunicationDelegate {
    void deviceFounded(BluetoothDevice bluetoothDevice);

    void stateChanged(int i, String str, String str2);
}
