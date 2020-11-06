package com.miraclink.networks.callbacks;

import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miraclink.networks.CallbackUtils;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.CheckFormatJson;
import com.miraclink.utils.JsonUtils;
import com.miraclink.utils.LogUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CheckVersionCallback implements Callback {
    private static final String TAG = CheckVersionCallback.class.getSimpleName();
    private Context context;
    private JsonParser parser;

    public CheckVersionCallback(Context context) {
        this.context = context;
        parser = new JsonParser();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        LogUtil.i(TAG, "callback failure" + e);
        CallbackUtils.baseOnFailure(context, call, e, this);
        context.sendBroadcast(new Intent(BroadCastAction.CHECK_VERSION_FAILED).putExtra("update_version_failed", 1));
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        LogUtil.i(TAG, "callback response");
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            String responseBodyString = responseBody.string();
            LogUtil.i(TAG, "callback response :" + responseBodyString);
            boolean isValidJson = CheckFormatJson.isJsonValid(call, response, TAG);
            if (!isValidJson) {
                return;
            }
            JsonObject jsonObject = parser.parse(responseBodyString).getAsJsonObject();
            int code = jsonObject.get("code").getAsInt();
            if (code == 0 || code == 200) {
                if (JsonUtils.isJsonObject(jsonObject, "data")) {
                    String url = null;
                    JsonObject jsonObjectInfo = jsonObject.get("data").getAsJsonObject();
                    if (jsonObjectInfo.has("url")) {
                        url = jsonObjectInfo.get("url").getAsString();
                    }
                    if (url != null) {
                        String content = jsonObjectInfo.get("updateContent").getAsString();
                        int version = jsonObjectInfo.get("updateVersion").getAsInt();
                        int flag = jsonObjectInfo.get("enforceFlag").getAsInt();
                        Intent intent = new Intent(BroadCastAction.CHECK_VERSION_DATA);
                        intent.putExtra("url", url);
                        intent.putExtra("content", content);
                        intent.putExtra("version", version);
                        intent.putExtra("flag", flag);
                        context.sendBroadcast(intent);
                    } else {
                        context.sendBroadcast(new Intent(BroadCastAction.CHECK_VERSION_FAILED).putExtra("update_version_failed", 2));
                    }

                }
            } else {
                context.sendBroadcast(new Intent(BroadCastAction.CHECK_VERSION_FAILED).putExtra("update_version_failed", 1));
            }

        } else {
            context.sendBroadcast(new Intent(BroadCastAction.CHECK_VERSION_FAILED).putExtra("update_version_failed", 1));
        }
    }

}
