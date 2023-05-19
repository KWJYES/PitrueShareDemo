package com.example.pictureshare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;

import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.pictureshare.R;
import com.example.pictureshare.utils.Network;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText et_username;
    EditText et_password;
    Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void initViews() {
        et_username = findViewById(R.id.username);
        et_password = findViewById(R.id.password);
        btn_signup = findViewById(R.id.btn_sign);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        String pwd = et_password.getText().toString();
        String uname = et_username.getText().toString();
        if (pwd.equals("") || uname.equals("")) return;
        // url路径
        String url = "http://47.107.52.7:88/member/photo/user/register";

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Network.appId)
                .add("appSecret", Network.appSecret)
                .add("Content-Type", "application/json")
                .build();

        // 请求体
        // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("password", pwd);
        bodyMap.put("username", uname);
        // 将Map转换为字符串类型加入请求体中
        String body = Network.getInstance().getGson().toJson(bodyMap);

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .post(RequestBody.create(body, MEDIA_TYPE_JSON))
                .build();
        Network.getInstance().request(request, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(SignUpActivity.this, "网络错误！", Toast.LENGTH_SHORT).show();
                });
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<Network.ResponseBody<String>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("info", body);
                // 解析json串到自己封装的状态
                Network.ResponseBody<String> dataResponseBody = Network.getInstance().getGson().fromJson(body, jsonType);
                Log.d("info", dataResponseBody.toString());
                if (dataResponseBody.getCode() == 500) {
                    runOnUiThread(() -> {
                        Log.d("info", uname+"\n"+pwd);
                        Toast.makeText(SignUpActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                    });
                    return;
                } else if (dataResponseBody.getCode() == 200) {
                    runOnUiThread(() -> {
                        Log.d("info", uname+"\n"+pwd);
                        Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
        });
    }
}