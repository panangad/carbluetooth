package com.xpg.view;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.xpg.manager.BluetoothBonder;
import com.xpg.util.MsgUtil;

public class ConnectDialog {
    private BluetoothDevice device;

    /* renamed from: com.xpg.view.ConnectDialog$2 */
    class C00922 implements OnClickListener {
        C00922() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public ConnectDialog(BluetoothDevice device) {
        this.device = device;
    }

    public void show(final Activity activity) {
        new Builder(activity).setMessage("设备:" + this.device.getName() + "\n地址:" + this.device.getAddress()).setNeutralButton("配对", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    BluetoothBonder.createBond(ConnectDialog.this.device.getClass(), ConnectDialog.this.device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MsgUtil.showMsg(activity, "与" + ConnectDialog.this.device.getName() + "配对中...", 0);
            }
        }).setNegativeButton("取消", new C00922()).show();
    }
}
