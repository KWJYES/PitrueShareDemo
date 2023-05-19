package com.example.pictureshare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pictureshare.R;
import com.example.pictureshare.adapter.DetailAdapter;
import com.example.pictureshare.pojo.PhotoItem;
import com.example.pictureshare.utils.MyApplication;
import com.example.pictureshare.utils.Network;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    Gson gson = Network.getInstance().getGson();
    TextView title;
    TextView back;
    RecyclerView recyclerView;
    TextView content;
    TextView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initViews();
        Intent intent=getIntent();
        PhotoItem.RecordsDTO item=(PhotoItem.RecordsDTO)intent.getSerializableExtra("item");
        title.setText(item.getTitle());
        List<String> imageUrlList=item.getImageUrlList();
        user.setText("图片数量："+imageUrlList.size()+"\n作者："+item.getUsername());
        content.setText("图片描述：\n"+item.getContent());
        setRV(imageUrlList);
    }

    private void initViews() {
        back=findViewById(R.id.btn_back);
        back.setOnClickListener(v->finish());
        title=findViewById(R.id.title);
        recyclerView=findViewById(R.id.recyclerview);
        content =findViewById(R.id.content);
        user=findViewById(R.id.user);
    }

//    public void getDetail(String shareId){
//        // url路径
//        String url = "http://47.107.52.7:88/member/photo/share/detail?shareId="+shareId+"&userId="+ MyApplication.user.getId();
//
//        // 请求头
//        Headers headers = new Headers.Builder()
//                .add("Accept", "application/json, text/plain, */*")
//                .add("appId", Network.appId)
//                .add("appSecret", Network.appSecret)
//                .build();
//
//        //请求组合创建
//        Request request = new Request.Builder()
//                .url(url)
//                // 将请求头加至请求中
//                .headers(headers)
//                .get()
//                .build();
//        Network.getInstance().request(request, new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                runOnUiThread(() -> {
//                    Toast.makeText(DetailActivity.this, "网络错误！", Toast.LENGTH_SHORT).show();
//                });
//                //TODO 请求失败处理
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
////TODO 请求成功处理
//                Type jsonType = new TypeToken<Network.ResponseBody<PhotoItem.RecordsDTO>>() {
//                }.getType();
//                // 获取响应体的json串
//                String body = response.body().string();
//                Log.d("info", body);
//                // 解析json串到自己封装的状态
//                Network.ResponseBody<PhotoItem.RecordsDTO> dataResponseBody = gson.fromJson(body, jsonType);
//                Log.d("info", dataResponseBody.toString());
//                if (dataResponseBody.getCode() == 200) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });
//                } else {
//                   runOnUiThread(() -> Toast.makeText(DetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }

    private void setRV(List<String> imageUrlList) {
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//默认是VERTICAL
        DetailAdapter adapter=new DetailAdapter(imageUrlList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}