package com.miraclink.utils;

import com.google.gson.JsonObject;

public class JsonUtils {
    public static Boolean isJsonObject(JsonObject jsonObject, String key) {
        if (!jsonObject.has(key)) {
            return false;
        }
        if (!jsonObject.get(key).isJsonObject()) {
            return false;
        }
        return true;
    }

    public static Boolean isJsonArray(JsonObject jsonObject, String key) {
        if (!jsonObject.has(key)) {
            return false;
        }
        if (!jsonObject.get(key).isJsonArray()) {
            return false;
        }
        return true;
    }


}
