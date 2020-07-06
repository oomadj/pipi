package com.miraclink.networks;

import android.content.Context;

import com.google.gson.Gson;
import com.miraclink.utils.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyInterceptor implements Interceptor {
    private Context context;
    private Gson gson;

    public MyInterceptor(Context context) {
        this.context = context;
        gson = new Gson();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;
        Response.Builder responseBuilder = new Response.Builder()
                .code(200)
                .message("")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .addHeader("content-type", "application/json");

        Request request = chain.request();
        LogUtil.i("MI","response url:"+request.url());
        if (request.url().toString().equals("https://www.baidu.com/test/user")) {
            String responseString = "[{\n" +
                    "\t\"ID\": \"20206290001\",\n" +
                    "\t\"name\": \"xuzhixin\",\n" +
                    "\t\"age\": 15,\n" +
                    "\t\"sex\": 0,\n" +
                    "\t\"height\": 177,\n" +
                    "\t\"weight\": 66,\n" +
                    "\t\"time\": 20,\n" +
                    "\t\"strong\": 30,\n" +
                    "\t\"rate\": 5,\n" +
                    "\t\"compose\": 1,\n" +
                    "\t\"mode\": 1\n" +
                    "}, {\n" +
                    "\t\"ID\": \"20206290002\",\n" +
                    "\t\"name\": \"hema\",\n" +
                    "\t\"age\": 24,\n" +
                    "\t\"sex\": 1,\n" +
                    "\t\"height\": 197,\n" +
                    "\t\"weight\": 96,\n" +
                    "\t\"time\": 20,\n" +
                    "\t\"strong\": 30,\n" +
                    "\t\"rate\": 5,\n" +
                    "\t\"compose\": 1,\n" +
                    "\t\"mode\": 1\n" +
                    "}, {\n" +
                    "\t\"ID\": \"20206290003\",\n" +
                    "\t\"name\": \"pipixia\",\n" +
                    "\t\"age\": 33,\n" +
                    "\t\"sex\": 1,\n" +
                    "\t\"height\": 107,\n" +
                    "\t\"weight\": 56,\n" +
                    "\t\"time\": 20,\n" +
                    "\t\"strong\": 30,\n" +
                    "\t\"rate\": 5,\n" +
                    "\t\"compose\": 1,\n" +
                    "\t\"mode\": 1\n" +
                    "}]";
            LogUtil.i("MI","response my string");
            responseBuilder.body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()));//将数据设置到body中
            response = responseBuilder.build(); //builder模式构建response
        } else {
            LogUtil.i("MI","response ------ string");
            response = chain.proceed(request);
        }

        return response;
    }
}
