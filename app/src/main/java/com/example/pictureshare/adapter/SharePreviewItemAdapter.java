package com.example.pictureshare.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictureshare.R;
import com.example.pictureshare.callback.ItemClickCallback;
import com.example.pictureshare.pojo.SharePreviewItem;

import java.util.ArrayList;
import java.util.List;

public class SharePreviewItemAdapter extends RecyclerView.Adapter<SharePreviewItemAdapter.ViewHolder> {
    List<SharePreviewItem> sharePreviewItems = new ArrayList<>();
    Context context;
    ItemClickCallback<SharePreviewItem> itemItemClickCallback;

    public void setItemItemClickCallback(ItemClickCallback<SharePreviewItem> itemItemClickCallback) {
        this.itemItemClickCallback = itemItemClickCallback;
    }

    public SharePreviewItemAdapter(List<SharePreviewItem> sharePreviewItems) {
        this.sharePreviewItems = sharePreviewItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context= parent.getContext();
        /*加载item布局*/
        View view = LayoutInflater.from(context).inflate(R.layout.item_share_preview, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == -1) return;//视图在刷新，点击无法得到位置
            itemItemClickCallback.onClick(sharePreviewItems.get(pos));
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("TAGG",sharePreviewItems.get(position).imagePath);
        Glide.with(context).load(sharePreviewItems.get(position).imagePath).centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return sharePreviewItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
        }
    }
}
