package net.zpavelocity.im.util;


import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;

public class JsonUtil {
    static Gson gson = null;

    static {
        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
    }

    public static byte[] object2JsonBytes(Object obj) {
        String json = pojo2json(obj);
        try {
            return json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T jsonByte2Object(byte[] bytes, Class<T> tClass) {
        String json = null;
        try {
            json = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        T t = json2pojo(json, tClass);
        return t;
    }

    public static String pojo2json(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T json2pojo(String json, Class<T> tClass) {
        return JSONObject.parseObject(json, tClass);
    }
}
