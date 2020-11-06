package com.miraclink.networks;


import android.content.Context;

import com.google.gson.JsonObject;
import com.miraclink.networks.callbacks.CheckVersionCallback;
import com.miraclink.networks.callbacks.DownApkCallback;
import com.miraclink.networks.callbacks.LoginCallback;
import com.miraclink.networks.callbacks.UserListCallback;
import com.miraclink.utils.MyApplication;
import com.miraclink.utils.Utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

// created by xuzhixin on 20/6/16
public class NetworkController {
    public static final String TAG = NetworkController.class.getSimpleName();
    public static final boolean isDebug = true;  //TODO is test

    private static NetworkController INSTANCE;
    private Context context;
    private MediaType mediaTypeJSON;
    private String serverUrl = "https://www.baidu.com";
    private String checkVersionUrl = "http://192.168.0.181:8804";
    private String token;
    private OkHttpClient client;

    private NetworkController() {
        context = MyApplication.getInstance();
        mediaTypeJSON = MediaType.parse("application/json; charset=utf-8");
        if (isDebug){
            client = new OkHttpClient();
        }else {
            client = getClient();
        }

    }

    public static NetworkController getInstance() {
        if (INSTANCE == null) {
            synchronized (NetworkController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkController();
                }
            }
        }
        return INSTANCE;
    }

    private OkHttpClient getClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new MyInterceptor(context));
        return builder.build();
    }

    //登录得到  //TODO get token
    public void getToken(String token){
        this.token = "2323432343"; //token;
    }

    private Request httpGetRequest(StringBuilder targetURL){
        return new Request.Builder()
                .url(targetURL.toString())
                //.addHeader("Authorization", token)
                .tag(Utils.getCurrentUTC())
                .build();
    }

    private Request httpPostRequest(StringBuilder targetURL,String bodyString){
        return new Request.Builder()
                .url(targetURL.toString())
                .addHeader("Authorization",token)
                .post(RequestBody.create(mediaTypeJSON,bodyString))
                .tag(Utils.getCurrentUTC())
                .build();
    }

    private Request httpPostRequestLogin(StringBuilder targetURL, String bodyString) {
        return new Request.Builder()
                .url(targetURL.toString())
                .post(RequestBody.create(mediaTypeJSON, bodyString))
                .tag(Utils.getCurrentUTC())
                .build();
    }

    private Request httpPutRequest(StringBuilder targetUrl,String bodyString){
        return new Request.Builder()
                .url(targetUrl.toString())
                .put(RequestBody.create(mediaTypeJSON,bodyString))
                .tag(Utils.getCurrentUTC())
                .build();
    }

    //登录
    public void putLogin(String userName,String passWord){
        StringBuilder targetURL = new StringBuilder(serverUrl);
        targetURL.append("/v1/User/login");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username",userName);
        jsonObject.addProperty("password",passWord);
        Request request = httpPostRequestLogin(targetURL,jsonObject.toString());
        client.newCall(request).enqueue(new LoginCallback(context));
    }

    //get user list
    public void getUserList(){
        StringBuilder targetURL = new StringBuilder(serverUrl);
        targetURL.append("/test/user");
        Request request = httpGetRequest(targetURL);
        client.newCall(request).enqueue(new UserListCallback(context));
    }

    //appType  3:bell    4:clothes
    public void postCheckVersion(int version) {
        StringBuilder targetUrl = new StringBuilder(checkVersionUrl);
        targetUrl.append("/app/version/checkUpdate");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("appType", 4);
        jsonObject.addProperty("currentVersion", version);
        jsonObject.addProperty("sysType", 0);
        Request request = httpPostRequestLogin(targetUrl, jsonObject.toString());
        client.newCall(request).enqueue(new CheckVersionCallback(context));
    }

    public void getDownApk(String downUrl) {
        StringBuilder targetUrl = new StringBuilder(downUrl);
        Request request = httpGetRequest(targetUrl);
        client.newCall(request).enqueue(new DownApkCallback(context));
    }

}
