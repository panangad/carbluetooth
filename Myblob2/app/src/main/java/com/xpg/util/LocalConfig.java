package com.xpg.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalConfig {
    public static final int BOOLEAN_TYPE = 2;
    public static final int FLOAT_TYPE = 3;
    public static final int INTEGER_TYPE = 1;
    public static final int LONG_TYPE = 4;
    public static final int STRING_TYPE = 5;

    public static Object readConfig(Context context, String fileName, String key, Object defValue, int valueType) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, 0);
        switch (valueType) {
            case 1:
                return Integer.valueOf(preferences.getInt(key, ((Integer) defValue).intValue()));
            case 2:
                return Boolean.valueOf(preferences.getBoolean(key, ((Boolean) defValue).booleanValue()));
            case 3:
                return Float.valueOf(preferences.getFloat(key, ((Float) defValue).floatValue()));
            case 4:
                return Long.valueOf(preferences.getLong(key, ((Long) defValue).longValue()));
            case STRING_TYPE /*5*/:
                return preferences.getString(key, (String) defValue);
            default:
                return null;
        }
    }

    public static void saveConfig(Context context, String fileName, String key, Object value, int valueType, boolean isCommit) {
        Editor editor = context.getSharedPreferences(fileName, 0).edit();
        switch (valueType) {
            case 1:
                editor.putInt(key, ((Integer) value).intValue());
                break;
            case 2:
                editor.putBoolean(key, ((Boolean) value).booleanValue());
                break;
            case 3:
                editor.putFloat(key, ((Float) value).floatValue());
                break;
            case 4:
                editor.putLong(key, ((Long) value).longValue());
                break;
            case STRING_TYPE /*5*/:
                editor.putString(key, (String) value);
                break;
        }
        if (isCommit) {
            editor.commit();
        }
    }

    public static void saveConfig(Context context, String fileName, String key, Object value, int valueType, boolean useDefValue, Object defValue) {
        Editor editor = context.getSharedPreferences(fileName, 0).edit();
        switch (valueType) {
            case 1:
                if (!useDefValue) {
                    editor.putInt(key, ((Integer) defValue).intValue());
                    break;
                } else {
                    editor.putInt(key, ((Integer) value).intValue());
                    break;
                }
            case 2:
                if (!useDefValue) {
                    editor.putBoolean(key, ((Boolean) defValue).booleanValue());
                    break;
                } else {
                    editor.putBoolean(key, ((Boolean) value).booleanValue());
                    break;
                }
            case 3:
                if (!useDefValue) {
                    editor.putFloat(key, ((Float) defValue).floatValue());
                    break;
                } else {
                    editor.putFloat(key, ((Float) value).floatValue());
                    break;
                }
            case 4:
                if (!useDefValue) {
                    editor.putLong(key, ((Long) defValue).longValue());
                    break;
                } else {
                    editor.putLong(key, ((Long) value).longValue());
                    break;
                }
            case STRING_TYPE /*5*/:
                if (!useDefValue) {
                    editor.putString(key, (String) defValue);
                    break;
                } else {
                    editor.putString(key, (String) value);
                    break;
                }
        }
        editor.commit();
    }

    public boolean delete(Context context, String fileName, String key) {
        Editor editor = context.getSharedPreferences(fileName, 0).edit();
        editor.remove(key);
        editor.commit();
        return false;
    }
}
