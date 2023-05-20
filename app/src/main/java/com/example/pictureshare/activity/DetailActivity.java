package com.example.pictureshare.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pictureshare.R;
import com.example.pictureshare.adapter.DetailAdapter;
import com.example.pictureshare.pojo.PhotoItem;
import com.example.pictureshare.pojo.SharePreviewItem;
import com.example.pictureshare.utils.DownloadSaveImg;
import com.example.pictureshare.utils.MyApplication;
import com.example.pictureshare.utils.Network;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.permissionx.guolindev.PermissionX;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
    TextView btn_save;
    private List<String> imageUrlList;
    ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> startActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    saveImg();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initViews();
        Intent intent=getIntent();
        PhotoItem.RecordsDTO item=(PhotoItem.RecordsDTO)intent.getSerializableExtra("item");
        title.setText(item.getTitle());
        imageUrlList = item.getImageUrlList();
        user.setText("图片数量："+ imageUrlList.size()+"\n作者："+item.getUsername());
        content.setText("图片描述：\n"+item.getContent());
        setRV(imageUrlList);
    }

    private void initViews() {
        progressDialog = new ProgressDialog(DetailActivity.this);
        progressDialog.setTitle("保存图片");
        progressDialog.setMessage("正在下载图片，请稍等");
        progressDialog.setCancelable(false);
        back=findViewById(R.id.btn_back);
        back.setOnClickListener(v->finish());
        title=findViewById(R.id.title);
        recyclerView=findViewById(R.id.recyclerview);
        content =findViewById(R.id.content);
        user=findViewById(R.id.user);
        btn_save=findViewById(R.id.btn_sava);
        btn_save.setOnClickListener(view -> {
            PermissionX.init(this)
                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            saveImg();
                        } else {
                            Toast.makeText(DetailActivity.this, "您拒绝了文件读写权限", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void saveImg() {
        progressDialog.show();
        for (String path:imageUrlList){
            DownloadSaveImg.donwloadImg(DetailActivity.this,path);
        }
        progressDialog.cancel();
    }

    private void setRV(List<String> imageUrlList) {
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//默认是VERTICAL
        DetailAdapter adapter=new DetailAdapter(imageUrlList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}