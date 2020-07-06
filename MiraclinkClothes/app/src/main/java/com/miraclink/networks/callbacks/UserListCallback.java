package com.miraclink.networks.callbacks;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.User;
import com.miraclink.networks.CallbackUtils;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.CheckFormatJson;
import com.miraclink.utils.LogUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UserListCallback implements Callback {
    private static final String TAG = UserListCallback.class.getSimpleName();
    private Context context;
    private JsonParser parser;
    private Gson gson;
    private IUserDatabaseManager iUserDatabaseManager;

    public UserListCallback(Context context) {
        this.context = context;
        parser = new JsonParser();
        gson = new Gson();
        iUserDatabaseManager = UserDatabaseManager.getInstance(context, AppExecutors.getInstance());
    }

    @Override
    public void onFailure(Call call, IOException e) {
        CallbackUtils.baseOnFailure(context, call, e, this);
        LogUtil.d(TAG, "call onfailure");
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        LogUtil.d(TAG, "call onresponse");
        if (response.isSuccessful()) {  //get user data to send broadcast to user list fragment  --xzx
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            String responseBodyString = responseBody.string();
            LogUtil.d(TAG, "call success:" + responseBodyString);
            boolean isValidJson = CheckFormatJson.isJsonValid(call, response, TAG);
            if (!isValidJson) {
                return;
            }
            JsonArray jsonArray = parser.parse(responseBodyString).getAsJsonArray();
            if (jsonArray != null && jsonArray.size() > 0) {
                Type listType = new TypeToken<ArrayList<User>>() {
                }.getType();
                ArrayList<User> users = gson.fromJson(jsonArray, listType);
                context.sendBroadcast(new Intent(BroadCastAction.USER_LIST_DATA).putParcelableArrayListExtra("DATA", users));

                iUserDatabaseManager.queryAllUser(userList -> {
                    if (userList.isEmpty()) {
                        insertUserList(users);
                    } else {
                        updateUserList(userList, users);
                    }
                });

            }

        } else {
            CallbackUtils.responseFail(response, this, context);
        }
    }

    private void insertUserList(ArrayList<User> list) {
        iUserDatabaseManager.insertUserList(list);
    }

    private void insertUser(User user) {
        iUserDatabaseManager.insertUser(user);
    }

    private void updateUserList(List<User> listLocal, List<User> listServer) {
        {
            for (User serverData : listServer) {
                for (int i = 0; i < listLocal.size(); i++) {
                    User localData = listLocal.get(i);
                    if (localData.getID() == serverData.getID()) {
                        listLocal.remove(i);
                        iUserDatabaseManager.updateUser(localData);
                    }

                    if (i == listLocal.size() - 1) {
                        insertUser(serverData);
                    }
                }
            }
        }
    }
}
