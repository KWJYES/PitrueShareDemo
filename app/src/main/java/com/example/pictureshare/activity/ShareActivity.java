package com.example.pictureshare.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pictureshare.R;
import com.example.pictureshare.adapter.SharePreviewItemAdapter;
import com.example.pictureshare.pojo.SharePreviewItem;
import com.example.pictureshare.pojo.UpLoadDao;
import com.example.pictureshare.utils.MyApplication;
import com.example.pictureshare.utils.Network;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShareActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<SharePreviewItem> sharePreviewItemList;
    FloatingActionButton floatingActionButton;
    TextView btn_back;
    TextView btn_push;
    EditText et_title;
    EditText et_content;
    Gson gson = Network.getInstance().getGson();
    private ActivityResultLauncher<Intent> startActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    List<SharePreviewItem> itemList = new ArrayList<>();
                    Intent data = result.getData();
                    if (data != null) {
                        ClipData imageNames = data.getClipData();
                        if (imageNames != null) {
                            for (int i = 0; i < imageNames.getItemCount(); i++) {
                                Uri imageUri = imageNames.getItemAt(i).getUri();
                                String path = handleImage(imageUri);
                                if (path != null)
                                    itemList.add(new SharePreviewItem(path));
                            }
                        } else {
                            Uri imageUri = data.getData();
                            String path = handleImage(imageUri);
                            if (path != null)
                                itemList.add(new SharePreviewItem(path));
                        }
                        setRV(itemList);
                    } else {
                        Toast.makeText(ShareActivity.this, "未选择", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    private ProgressDialog progressDialog;

    private String handleImage(Uri uri) {
        String imagePath = null;
        //从android4.4开始选择相册中的图片不再返回图片的真实Uri了，而是一个封装的Uri，所以要对其进行解析
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri
            String codeID = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //如果uri的authority(柄)是media格式的话，不得,进一步解析
                String id = codeID.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(codeID));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri用普通方法处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是文件类型的uri可以直接获取图片路径
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));//MediaStore.Images.Media.DATA已弃用
            }
            cursor.close();
        }
        return path;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
    }

    private void openAlbum() {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        final int maxNumPhotosAndVideos = 10;
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setType("image/*");
                        startActivityLauncher.launch(Intent.createChooser(intent, "Select Picture"));
                    } else {
                        Toast.makeText(ShareActivity.this, "您拒绝了使用相机", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initView() {
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        recyclerView = findViewById(R.id.rv_share_preview);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());
        btn_push = findViewById(R.id.btn_push);
        btn_push.setOnClickListener(v -> push());
        floatingActionButton = findViewById(R.id.btn_add);
        floatingActionButton.setOnClickListener(v -> openAlbum());
        sharePreviewItemList = new ArrayList<>();
        progressDialog = new ProgressDialog(ShareActivity.this);
        progressDialog.setTitle("发布相片");
        progressDialog.setMessage("正在发布，请稍等");
        progressDialog.setCancelable(false);//如果传入false则不能通过Back键取消
    }

    private void push() {
        progressDialog.show();
        new Thread(() -> {
            String imageCode = upload();
            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/add";

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
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", MyApplication.user.getId());
            bodyMap.put("title", et_title.getText().toString());
            bodyMap.put("content", et_content.getText().toString());
            // 将Map转换为字符串类型加入请求体中
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                Type jsonType = new TypeToken<Network.ResponseBody<UpLoadDao>>() {
                }.getType();
                OkHttpClient client = Network.getInstance().getClient();
                //发起请求，传入callback进行回调
                Response response = client.newCall(request).execute();
                String rbody = response.body().string();
                Log.d("info", rbody);
                // 解析json串到自己封装的状态
                Network.ResponseBody<UpLoadDao> dataResponseBody = gson.fromJson(rbody, jsonType);
                Log.d("info", dataResponseBody.toString());
                if (dataResponseBody.getCode() == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(ShareActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ShareActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    });
                }
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    private String upload() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/image/upload";

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Network.appId)
                .add("appSecret", Network.appSecret)
                .add("Content-Type", "multipart/form-data")
                .build();


        MediaType mediaType = MediaType.parse("application/octet-stream");//设置类型，类型为八位字节流
        //RequestBody requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (int i = 0; i < sharePreviewItemList.size(); i++) {
            File file = new File(sharePreviewItemList.get(i).imagePath);
            builder.addFormDataPart("fileList", file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")));//文件名,请求体里的文件
        }

        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .post(builder.build())
                .build();
        //TODO 请求成功处理
        Type jsonType = new TypeToken<Network.ResponseBody<UpLoadDao>>() {
        }.getType();
        // 获取响应体的json串
        Response response = null;
        try {
            response = Network.getInstance().getClient().newCall(request).execute();
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            Network.ResponseBody<UpLoadDao> dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info", dataResponseBody.toString());
            return dataResponseBody.getData().getImageCode();
        } catch (IOException e) {
            Log.d("info", "上传文件失败");
            throw new RuntimeException(e);
        }

    }

    private void setRV(List<SharePreviewItem> sharePreviewItems) {
        sharePreviewItemList.addAll(sharePreviewItems);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        SharePreviewItemAdapter adapter = new SharePreviewItemAdapter(sharePreviewItemList);
        adapter.setItemItemClickCallback(item -> {

        });
        recyclerView.setAdapter(adapter);
    }
}