package com.miraclink.networks.callbacks;

import android.content.Context;

import com.google.gson.JsonParser;
import com.miraclink.networks.CallbackUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginCallback implements Callback {
    private static final String TAG = LoginCallback.class.getSimpleName();
    private Context context;
    private JsonParser parser;


    public LoginCallback(Context context) {
        this.context = context;
        parser = new JsonParser();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        CallbackUtils.baseOnFailure(context,call,e,this);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()){

        }else {
            CallbackUtils.responseFail(response,this,context);
        }
    }
}
