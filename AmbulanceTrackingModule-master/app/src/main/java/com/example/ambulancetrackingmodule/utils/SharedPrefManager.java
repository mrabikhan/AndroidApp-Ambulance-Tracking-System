package com.example.ambulancetrackingmodule.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPrefManager {


//	private static SharedPreferences sharedPreferences = null;
    public static SharedPreferences openPref_(Context contex, String mPrefName) {
        SharedPreferences sharedPreferences = contex.getSharedPreferences(mPrefName,
                Context.MODE_PRIVATE);

        return sharedPreferences;

    }
// get String value
    public static String getStringValue(Context context, String key,
                                  String defaultValue, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(key, defaultValue);
        return result;
    }

    public static void setStringValue(Context context, String key, String value, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                context.MODE_PRIVATE);
        Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value);
        prefsPrivateEditor.commit();
//		prefsPrivateEditor = null;
//		SharedPrefManager.sharedPreferences = null;
    }
// get Boolean
    public static boolean getBooleanValue(Context context, String key,
                                   boolean defaultValue, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                Context.MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean(key, defaultValue);
        return result;
    }

    public static void setBooleanValue(Context context, String key, boolean value, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                Context.MODE_PRIVATE);
        Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putBoolean(key, value);
        prefsPrivateEditor.commit();
    }

// Int
    public static void setIntValue(Context context, String key, int value, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                Context.MODE_PRIVATE);
        Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putInt(key, value);
        prefsPrivateEditor.commit();
    }

    public static int getIntValue(Context context, String key,
                                  int defaultValue, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                Context.MODE_PRIVATE);
        int result = sharedPreferences.getInt(key, defaultValue);
//		SharedPrefManager.sharedPreferences = null;
        return result;
    }

    public static void setValueLong(Context context, String key, long value, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                Context.MODE_PRIVATE);
        Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.putLong(key, value);
        prefsPrivateEditor.commit();
    }

    public static long getValueLong(Context context, String key,
                                    long defaultValue, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                Context.MODE_PRIVATE);
        //		SharedPrefManager.sharedPreferences = null;
        return sharedPreferences.getLong(key, 1l);
    }

    public static void ClearAllPref(Context context, String mFileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mFileName,
                Context.MODE_PRIVATE);
        Editor prefsPrivateEditor = sharedPreferences.edit();
        prefsPrivateEditor.clear();
        prefsPrivateEditor.commit();
    }

}
