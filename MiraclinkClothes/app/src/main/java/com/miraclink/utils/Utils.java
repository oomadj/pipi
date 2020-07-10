package com.miraclink.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Utils {
    public static final String formatContainSecondWithoutSymbol = "yyyyMMddHHmmss";
    public static final String checkStartBack = "[ae,04,a2,a6]";
    public static final String checkStopBack = "[ae,06,a3,a9]";
    public static final byte[] startBackByte ={(byte) 0xAE,0x04, (byte) 0xA2, (byte) 0xA6};
    public static final byte[] stopBackByte ={(byte) 0xAE,0x06, (byte) 0xA3, (byte) 0xA9};
    public static final byte[] addOrCutBackByte = {(byte) 0xAE,0x02, (byte) 0xA1, (byte) 0xA3};
    public static final byte[] timerBackByte = {(byte) 0xAE,0x08, (byte) 0xA4, (byte) 0xAC};

    public static final String startStringBack = "[ae,04,a2,a6]";
    public static final String stopStringBack = "[ae,06,a3,a9]";


    public static String getCurrentUTC() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat format = new SimpleDateFormat(formatContainSecondWithoutSymbol);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String result = format.format(calendar.getTime());

        return result;
    }

    //将毫秒转化为 分钟：秒 的格式   millisecond 毫秒
    public static String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        } else {
            if (second < 10) {
                return minute + ":" + "0" + second;
            } else {
                return minute + ":" + second;
            }
        }
    }

    public static int dpToPx(Context context, int dp) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 500) {       //500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
