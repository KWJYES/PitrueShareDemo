package com.example.pictureshare.utils;

import android.os.NetworkOnMainThreadException;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Map;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Network {
    private static volatile Network instance;
    public static final String appId="7055ff52d5584c409a79df30bfca4f4a";
    public static final String appSecret="166011b3ca60ce99e4f5d9be4d1425fe7c6a2";
    private static final String baseUrl="http://47.107.52.7:88";
    private static Gson gson;

    public OkHttpClient getClient() {
        return client;
    }

    private static OkHttpClient client;

    public Gson getGson() {
        return gson;
    }

    private Network(){
        gson = new Gson();
        client=new OkHttpClient();
    }
    public static Network getInstance(){
        if (instance==null){
            synchronized (Network.class){
                if (instance==null){
                    instance=new Network();
                }
            }
        }
        return instance;
    }

    public void request(Request request,Callback callback){
        client.newCall(request).enqueue(callback);
    }

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class ResponseBody <T> {

        /**
         * 业务响应码
         */
        private int code;
        /**
         * 响应提示信息
         */
        private String msg;
        /**
         * 响应数据
         */
        private T data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public T getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}
