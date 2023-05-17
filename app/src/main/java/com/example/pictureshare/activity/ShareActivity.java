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
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pictureshare.R;
import com.example.pictureshare.adapter.SharePreviewItemAdapter;
import com.example.pictureshare.pojo.SharePreviewItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<SharePreviewItem> sharePreviewItemList;
    FloatingActionButton floatingActionButton;
    TextView btn_back;
    TextView btn_push;
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
                            String path=handleImage(imageUri);
                            if (path!=null)
                                itemList.add(new SharePreviewItem(path));
                        }
                        setRV(itemList);
                    } else {
                        Toast.makeText(ShareActivity.this, "未选择", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                        if (allGranted) {
                            final int maxNumPhotosAndVideos = 10;
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setType("image/*");
                            startActivityLauncher.launch(Intent.createChooser(intent, "Select Picture"));
                        } else {
                            Toast.makeText(ShareActivity.this, "您拒绝了使用相机", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_share_preview);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->finish());
        btn_push = findViewById(R.id.btn_push);
        btn_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发布
            }
        });
        floatingActionButton = findViewById(R.id.btn_add);
        floatingActionButton.setOnClickListener(v -> openAlbum());
        sharePreviewItemList = new ArrayList<>();
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