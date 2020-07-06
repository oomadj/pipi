package com.miraclink.utils;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CheckFormatJson {
    public static boolean isJsonValid(Call call, Response response, String TAG) throws IOException {
       // SlackTools slackTools = SlackTools.getInstance();
        Gson gson = new Gson();
        Request callRequest = call.request();
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        String responseBodyString = responseBody.string();

        if (responseBodyString.equals("")) {
            String errMsg = "[[ responseBodyString empty warning ]]";
            //slackTools.sendJsonEmptyFromSlack(TAG, callRequest, response, responseBodyString, errMsg);
            return false;
        } else {
            try {
                Object o = gson.fromJson(responseBodyString, Object.class);
            } catch (Exception e) {
               // slackTools.sendJsonEmptyFromSlack(TAG, callRequest, response, responseBodyString,
                //        e.getMessage());
                return false;
            }
            return true;
        }
    }
}
