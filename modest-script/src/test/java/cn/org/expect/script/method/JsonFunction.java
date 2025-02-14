package cn.org.expect.script.method;

import cn.org.expect.script.annotation.EasyVariableExtension;
import org.json.JSONArray;
import org.json.JSONObject;

@EasyVariableExtension
public class JsonFunction {

    public static JSONObject getJSONObject(String json) {
        return new JSONObject(json);
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
        return jsonObject.getJSONObject(key);
    }

    public static JSONObject getJSONObject(JSONArray jsonObject, int index) {
        return jsonObject.getJSONObject(index);
    }

    public static JSONObject getJSONObject(JSONArray jsonObject, long index) {
        return getJSONObject(jsonObject, (int) index);
    }

    public static int length(JSONArray array) {
        return array.length();
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String key) {
        return jsonObject.getJSONArray(key);
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) {
        return jsonObject.getBoolean(key);
    }

    public static boolean getBoolean(JSONObject jsonObject, String key, boolean defValue) {
        return jsonObject.optBoolean(key, defValue);
    }

    public static int getInt(JSONObject jsonObject, String key) {
        return jsonObject.getInt(key);
    }

    public static int getInt(JSONObject jsonObject, String key, int defValue) {
        return jsonObject.optInt(key, defValue);
    }

    public static int getInt(JSONObject jsonObject, String key, long defValue) {
        return getInt(jsonObject, key, (int) defValue);
    }

    public static long getLong(JSONObject jsonObject, String key) {
        return jsonObject.getLong(key);
    }

    public static long getLong(JSONObject jsonObject, String key, long defValue) {
        return jsonObject.optLong(key, defValue);
    }

    public static String getString(JSONObject jsonObject, String key) {
        return jsonObject.getString(key);
    }

    public static String getString(JSONObject jsonObject, String key, String defValue) {
        return jsonObject.optString(key, defValue);
    }

    public static boolean has(JSONObject jsonObject, String key) {
        return jsonObject.has(key);
    }
}
