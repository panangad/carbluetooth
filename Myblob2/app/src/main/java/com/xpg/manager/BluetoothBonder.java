package com.xpg.manager;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BluetoothBonder {
    public static void printAllInform(Class clsShow) {
        try {
            Method[] hideMethod = clsShow.getMethods();
            for (Method name : hideMethod) {
                Log.e("method name", name.getName());
            }
            Field[] allFields = clsShow.getFields();
            for (Field name2 : allFields) {
                Log.e("Field name", name2.getName());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    public static boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        return ((Boolean) btClass.getMethod("createBond", new Class[0]).invoke(btDevice, new Object[0])).booleanValue();
    }

    public static boolean removeBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        return ((Boolean) btClass.getMethod("removeBond", new Class[0]).invoke(btDevice, new Object[0])).booleanValue();
    }

    public static boolean bondDevice(BluetoothDevice device) {
        try {
            createBond(device.getClass(), device);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
