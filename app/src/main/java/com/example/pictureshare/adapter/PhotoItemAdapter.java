package com.example.pictureshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pictureshare.R;
import com.example.pictureshare.callback.ItemClickCallback;
import com.example.pictureshare.pojo.PhotoItem;

import java.util.ArrayList;
import java.util.List;

public class PhotoItemAdapter extends RecyclerView.Adapter<PhotoItemAdapter.ViewHolder> {
    List<PhotoItem.RecordsDTO> photoItems = new ArrayList<>();
    Context context;
    ItemClickCallback<PhotoItem.RecordsDTO> likeClickCallback;

    public void setLikeClickCallback(ItemClickCallback<PhotoItem.RecordsDTO> likeClickCallback) {
        this.likeClickCallback = likeClickCallback;
    }

    ItemClickCallback<PhotoItem.RecordsDTO> itemItemClickCallback;

    public void setItemItemClickCallback(ItemClickCallback<PhotoItem.RecordsDTO> itemItemClickCallback) {
        this.itemItemClickCallback = itemItemClickCallback;
    }

    public PhotoItemAdapter(List<PhotoItem.RecordsDTO> photoItems) {
        this.photoItems = photoItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context= parent.getContext();
        /*加载item布局*/
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == -1) return;//视图在刷新，点击无法得到位置
            itemItemClickCallback.onClick(photoItems.get(pos));
        });
        holder.islike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos == -1) return;//视图在刷新，点击无法得到位置
                likeClickCallback.onClick(photoItems.get(pos));
                if (!photoItems.get(pos).getHasLike()){
                    Glide.with(context).load(R.drawable.like_fill).into(holder.islike);
                }else {
                    Glide.with(context).load(R.drawable.like).into(holder.islike);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(photoItems.get(position).getImageUrlList().get(0)).centerCrop().into(holder.imageView);
        holder.likeNum.setText((photoItems.get(position).getLikeNum()+""));
        if (photoItems.get(position).getHasLike()){
            Glide.with(context).load(R.drawable.like_fill).into(holder.islike);
        }else {
            Glide.with(context).load(R.drawable.like).into(holder.islike);
        }
        holder.user.setText(photoItems.get(position).getUsername());
        holder.title.setText(photoItems.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return photoItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView likeNum;
        TextView user;
        TextView title;
        ImageView islike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            likeNum = itemView.findViewById(R.id.likeNum);
            islike = itemView.findViewById(R.id.like);
            user = itemView.findViewById(R.id.user);
            title = itemView.findViewById(R.id.title);
        }
    }
}
