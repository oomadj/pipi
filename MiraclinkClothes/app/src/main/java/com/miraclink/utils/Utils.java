package com.miraclink.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.EditText;

import com.miraclink.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static String getComposeText(Context context,int i) {
        String text = null;
        switch (i) {
            case 1:
                text = context.getResources().getString(R.string.compose_alone);
                break;
            case 2:
                text = context.getResources().getString(R.string.compose1);
                break;
            case 3:
                text = context.getResources().getString(R.string.compose2);
                break;
            case 4:
                text = context.getResources().getString(R.string.compose3);
                break;
            case 5:
                text = context.getResources().getString(R.string.compose4);
                break;
            default:
                text = context.getResources().getString(R.string.compose_alone);
                break;
        }
        return text;
    }

    public static String getModeText(Context context,int i) {
        String text = null;
        switch (i) {
            case 1:
                text = context.getResources().getString(R.string.mode1);
                break;
            case 2:
                text = context.getResources().getString(R.string.mode2);
                break;
            case 3:
                text = context.getResources().getString(R.string.mode3);
                break;
            case 4:
                text = context.getResources().getString(R.string.mode4);
                break;
            case 5:
                text = context.getResources().getString(R.string.mode5);
                break;
            default:
                break;
        }
        return text;
    }

    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat();
        Date date = new Date();
        return "20"+format.format(date);
    }

    public static boolean isEditEmpty(EditText editText){
        if (editText.getText().toString().isEmpty()){
            return true;
        }else {
            return false;
        }
    }
}
