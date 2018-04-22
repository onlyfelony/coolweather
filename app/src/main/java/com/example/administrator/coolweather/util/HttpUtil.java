package com.example.administrator.coolweather.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 从服务器端获取数据
 */
public class HttpUtil {



    public static void sendHttpRequest(String address, Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);

    }



}