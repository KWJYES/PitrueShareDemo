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
    private static final String baseUrl="http://47.107.52.7:88";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static Gson gson;
    private static OkHttpClient client;
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
