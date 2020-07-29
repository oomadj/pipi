package com.miraclink.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreUtils {
    public static final String PRENAME = "userdata";

    //保存ID
    public static void setCurrentID(Context context, String PI) {
        SharedPreferences sp = context.getSharedPreferences(PRENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Current_ID", PI);
        ed.apply();  // commit() or apply()
    }

    //
    public static String getCurrentID(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PRENAME, Context.MODE_PRIVATE);
        return sp.getString("Current_ID", "");
    }

    //
    public static void removeCurrentID(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PRENAME, Context.MODE_PRIVATE).edit();
        editor.remove("Current_ID");
        editor.apply();
    }

    //选中的用户id
    public static void setCheckID(Context context, String id) {
        SharedPreferences sp = context.getSharedPreferences(PRENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Check_ID", id);
        ed.apply();
    }

    public static String getCheckID(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PRENAME, Context.MODE_PRIVATE);
        return sp.getString("Check_ID", "");
    }

    public static void removeCheckID(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PRENAME, Context.MODE_PRIVATE).edit();
        editor.remove("Check_ID");
        editor.apply();
    }
}
