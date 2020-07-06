package com.miraclink.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreUtils {
    public static final String PRENAME ="userdata";

    //保存ID
    public static void setCurrentID(Context context,String PI) {
        SharedPreferences sp = context.getSharedPreferences(PRENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Current_ID", PI);
        ed.apply();  // commit() or apply()
    }

    //
    public static String getCurrentID(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PRENAME, Context.MODE_PRIVATE);
        return sp.getString("Current_ID", "null");
    }
}
