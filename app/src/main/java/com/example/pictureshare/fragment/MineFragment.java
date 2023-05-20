package com.example.pictureshare.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pictureshare.R;
import com.example.pictureshare.activity.DetailActivity;
import com.example.pictureshare.adapter.DetailAdapter;
import com.example.pictureshare.adapter.PhotoItemAdapter;
import com.example.pictureshare.callback.ItemClickCallback;
import com.example.pictureshare.pojo.PhotoItem;
import com.example.pictureshare.utils.MyApplication;
import com.example.pictureshare.utils.Network;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MineFragment extends Fragment {
    TextView username;
    TextView userId;
    SmartRefreshLayout smartRefreshLayout;
    RecyclerView recyclerView;
    private int i;
    private List<PhotoItem.RecordsDTO> photoItems;
    private PhotoItemAdapter adapter;
    Gson gson = Network.getInstance().getGson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        username = view.findViewById(R.id.tv_user);
        userId = view.findViewById(R.id.tv_id);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        recyclerView = view.findViewById(R.id.rv_mine);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoItems = new ArrayList<>();
        username.setText("用户：" + MyApplication.user.getUsername());
        userId.setText("ID：" + MyApplication.user.getId());
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            photoItems.clear();
            i = 1;
            getMyPhotos(1);
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            i=photoItems.size()/30+2;
            getMyPhotos(i);
        });
        getMyPhotos(1);
    }

    private void getMyPhotos(int i) {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/share/myself?current=" + i + "&size=30&userId=" + MyApplication.user.getId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Network.appId)
                .add("appSecret", Network.appSecret)
                .build();

        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .get()
                .build();
        Network.getInstance().request(request, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "网络错误！", Toast.LENGTH_SHORT).show());
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<Network.ResponseBody<PhotoItem>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("info", body);
                // 解析json串到自己封装的状态
                Network.ResponseBody<PhotoItem> dataResponseBody = gson.fromJson(body, jsonType);
                Log.d("info", dataResponseBody.toString());
                if (dataResponseBody.getCode() == 200) {
                    Log.d("tag", "当前页：" + i);
                    if (dataResponseBody.getData() != null) {
                        photoItems.addAll(dataResponseBody.getData().getRecords());
                    }
                    getActivity().runOnUiThread(() -> {
                        smartRefreshLayout.finishRefresh();
                        smartRefreshLayout.finishLoadMore();
                        if (i == 1) setRV();
                        else adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private void setRV() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotoItemAdapter(this.photoItems);
        adapter.setItemItemClickCallback(item -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("item", item);
            getActivity().startActivity(intent);
        });
        adapter.setLongClickCallback(item -> {
            //弹出对话框
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("删除些分享");
            dialog.setMessage("确定要删除这个图片分享吗？");
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定", (dialogInterface, i) -> deleteImg(item));
            dialog.setNegativeButton("取消", (dialogInterface, i) -> {
            });
            dialog.show();
        });
        adapter.setLikeClickCallback(item -> {
            if (item.getHasLike())
                unlike(item.getId(), MyApplication.user.getId(), item);
            else like((String) item.getCollectId(), (String) item.getLikeId(), item);
        });
        recyclerView.setAdapter(adapter);
    }

    private void deleteImg(PhotoItem.RecordsDTO item) {

        // url路径
        String url = "http://47.107.52.7:88/member/photo/share/delete?shareId=" + item.getId() + "&userId=" + MyApplication.user.getId();

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
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "网络错误！", Toast.LENGTH_SHORT).show());
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<Network.ResponseBody<PhotoItem>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("info", body);
                // 解析json串到自己封装的状态
                Network.ResponseBody<PhotoItem> dataResponseBody = gson.fromJson(body, jsonType);
                Log.d("info", dataResponseBody.toString());
                if (dataResponseBody.getCode() == 200) {
                    photoItems.remove(item);
                    getActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void like(String shareId, String userId, PhotoItem.RecordsDTO item) {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/like?shareId=" + shareId + "&userId=" + userId;

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
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "网络错误！", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                });
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //TODO 请求成功处理
                Type jsonType = new TypeToken<Network.ResponseBody<Object>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("info", body);
                // 解析json串到自己封装的状态
                Network.ResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
                Log.d("info", dataResponseBody.toString());
                if (dataResponseBody.getCode() == 200) {
                    item.setHasLike(true);
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "点赞成功", Toast.LENGTH_SHORT).show());
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "点赞失败\n" + dataResponseBody.getMsg(), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private void unlike(String collectId, String likeId, PhotoItem.RecordsDTO item) {

        // url路径
        String url = "http://47.107.52.7:88/member/photo/like/cancel?collectId=" + collectId + "&likeId=" + likeId;

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
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "网络错误！", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                });
                //TODO 请求失败处理
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//TODO 请求成功处理
                Type jsonType = new TypeToken<Network.ResponseBody<Object>>() {
                }.getType();
                // 获取响应体的json串
                String body = response.body().string();
                Log.d("info", body);
                // 解析json串到自己封装的状态
                Network.ResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
                Log.d("info", dataResponseBody.toString());
                if (dataResponseBody.getCode() == 200) {
                    item.setHasLike(false);
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "取消点赞成功", Toast.LENGTH_SHORT).show());
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "取消点赞失败\n" + dataResponseBody.getMsg(), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }
}