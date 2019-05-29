package com.xpg.manager;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xpg.C0085R;
import com.xpg.constant.Constants;
import com.xpg.util.BluetoothDeviceNameFilter;

import java.util.List;

public class ConnectionAdapter extends ArrayAdapter<BluetoothDevice> {
    Activity activity;
    BluetoothDeviceNameFilter bluetoothDeviceNameFilter;
    List<BluetoothDevice> devices;

    public ConnectionAdapter(Context context, int textViewResourceId, List<BluetoothDevice> devices) {
        super(context, textViewResourceId, devices);
        this.activity = (Activity) context;
        this.devices = devices;
    }

    public void clear() {
        this.devices.clear();
        notifyDataSetChanged();
    }

    public void append(BluetoothDevice device) {
        boolean isExist = false;
        for (int i = 0; i < this.devices.size(); i++) {
            if (((BluetoothDevice) this.devices.get(i)).getAddress().equals(device.getAddress())) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            this.devices.add(device);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(C0085R.layout.device_list, null);
        }
        BluetoothDevice device = getItem(position);
        BluetoothDevice m = device;
        convertView.setTag(device);
        TextView deviceName = (TextView) convertView.findViewById(C0085R.id.device_name);
        TextView deviceAddress = (TextView) convertView.findViewById(C0085R.id.mac_address);
        TextView deviceState = (TextView) convertView.findViewById(C0085R.id.connect_status);
        deviceAddress.setVisibility(8);
        deviceState.setVisibility(8);
        if (m.getBondState() == 12) {
            deviceState.setText(activity.getResources().getString(C0085R.string.bondedString));
        } else if (m.getBondState() == 10) {
            deviceState.setText(activity.getResources().getString(C0085R.string.unbondedString));
        } else if (m.getBondState() == 11) {
            deviceState.setText(activity.getResources().getString(C0085R.string.bondingString));
        }
        String name = m.getName();
        if (name == null) {
            name = "Unknown Device";
        } else if (name.equals(Constants.CURRENT_MODEL)) {
            name = "Unknown Device";
        }
        if (this.bluetoothDeviceNameFilter != null) {
            name = this.bluetoothDeviceNameFilter.doNameFilter(name, position);
        }
        deviceName.setText(new StringBuilder(String.valueOf(name)).append("\n").toString());
        deviceAddress.setText(m.getAddress());
        return convertView;
    }

    public BluetoothDeviceNameFilter getBluetoothDeviceNameFilter() {
        return this.bluetoothDeviceNameFilter;
    }

    public void setBluetoothDeviceNameFilter(BluetoothDeviceNameFilter bluetoothDeviceNameFilter) {
        this.bluetoothDeviceNameFilter = bluetoothDeviceNameFilter;
    }
}
