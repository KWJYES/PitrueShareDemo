package com.example.pictureshare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pictureshare.R;
import com.example.pictureshare.pojo.User;
import com.example.pictureshare.utils.MyApplication;
import com.example.pictureshare.utils.Network;
import com.google.gson.reflect.TypeToken;
import com.permissionx.guolindev.PermissionX;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_password;
    Button btn_login;
    Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        et_username = findViewById(R.id.username);
        et_password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_sign);
        btn_signup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        btn_login.setOnClickListener(v -> login());
    }

    private void login() {
        String pwd = et_password.getText().toString();
        String uname = et_username.getText().toString();
        if (pwd.equals("") || uname.equals("")) return;
        // url路径
        String url = "http://47.107.52.7:88/member/photo/user/login?password=" + pwd + "&username=" + uname;

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Network.appId)
                .add("appSecret", Network.appSecret)
                .add("Content-Type", "application/json")
                .build();

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .post(RequestBody.create("", MEDIA_TYPE_JSON))
                .build();
        Network.getInstance().request(request, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "网络错误！", Toast.LENGTH_SHORT).show();
                });
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<Network.ResponseBody<User>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("info", body);
                // 解析json串到自己封装的状态
                Network.ResponseBody<User> dataResponseBody = Network.getInstance().getGson().fromJson(body, jsonType);
                Log.d("info", dataResponseBody.toString());
                if (dataResponseBody.getCode() == 200) {
                    MyApplication.user = dataResponseBody.getData();
                    runOnUiThread(() -> {
                        Log.d("info", uname + "\n" + pwd);
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    });
                } else if (dataResponseBody.getCode() == 500) {
                    runOnUiThread(() -> {
                        Log.d("info", uname + "\n" + pwd);
                        Toast.makeText(LoginActivity.this, "当前登录用户不存在", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

}