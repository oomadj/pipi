package com.miraclink.networks;

import android.content.Context;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallbackUtils {

    //TODO base on failed
    public static void baseOnFailure(Context context, Call call, IOException e, Callback callback){

    }

    //TODO send failed log to net
    public static void responseFail(Response response,Callback callback,Context context){

    }
}
