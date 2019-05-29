package com.xpg.util;

import android.app.Activity;
import android.widget.Toast;

public class MsgUtil {
    public static void showMsg(Activity activity, String msg, int toast_mode) {
        Toast.makeText(activity, msg, toast_mode).show();
    }
}
