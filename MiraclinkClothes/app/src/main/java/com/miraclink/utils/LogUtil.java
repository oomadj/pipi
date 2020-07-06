package com.miraclink.utils;

import android.util.Log;

public class LogUtil {
    private static boolean enable = true;

    public static void i(String tag,String msg){
        if(enable){
            Log.i(tag,"xzx--"+msg);
        }
    }

    public static void d(String tag,String msg){
        if(enable){
            Log.d(tag,"xzx--"+msg);
        }
    }

    public static void e(String tag,String msg){
        if(enable){
            Log.e(tag,"xzx--"+msg);
        }
    }

}
