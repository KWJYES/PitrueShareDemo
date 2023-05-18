package com.example.pictureshare.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pictureshare.R;
import com.permissionx.guolindev.PermissionX;

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
        et_username=findViewById(R.id.username);
        et_password=findViewById(R.id.password);
        btn_login=findViewById(R.id.btn_login);
        btn_signup=findViewById(R.id.btn_sign);
        btn_signup.setOnClickListener(v->startActivity(new Intent(LoginActivity.this,SignUpActivity.class)));
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}