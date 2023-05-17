package com.example.pictureshare.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pictureshare.R;
import com.example.pictureshare.adapter.PhotoItemAdapter;
import com.example.pictureshare.pojo.PhotoItem;

import java.util.List;


public class HomeFragment extends Fragment {


    private View view;
    RecyclerView recyclerView;
    private List<PhotoItem> photoItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.rv_home);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setRV(List<PhotoItem> photoItems) {
        this.photoItems.addAll(photoItems);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        PhotoItemAdapter adapter = new PhotoItemAdapter(this.photoItems);
        adapter.setItemItemClickCallback(item -> {

        });
        recyclerView.setAdapter(adapter);
    }
}