package com.example.pictureshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictureshare.R;
import com.example.pictureshare.callback.ItemClickCallback;
import com.example.pictureshare.pojo.PhotoItem;

import java.util.ArrayList;
import java.util.List;

public class PhotoItemAdapter extends RecyclerView.Adapter<PhotoItemAdapter.ViewHolder> {
    List<PhotoItem> photoItems = new ArrayList<>();
    Context context;
    ItemClickCallback<PhotoItem> itemItemClickCallback;

    public void setItemItemClickCallback(ItemClickCallback<PhotoItem> itemItemClickCallback) {
        this.itemItemClickCallback = itemItemClickCallback;
    }

    public PhotoItemAdapter(List<PhotoItem> photoItems) {
        this.photoItems = photoItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context==null) context= parent.getContext();
        /*加载item布局*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == -1) return;//视图在刷新，点击无法得到位置
            itemItemClickCallback.onClick(photoItems.get(pos));
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Glide.with(context).load(photoItems.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return photoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView = imageView.findViewById(R.id.imageview);
        }
    }
}
