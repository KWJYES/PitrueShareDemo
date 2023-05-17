package com.example.pictureshare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.pictureshare.R;
import com.example.pictureshare.adapter.MainViewPager2Adapter;
import com.example.pictureshare.fragment.HomeFragment;
import com.example.pictureshare.fragment.MineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager2 mainViewPager2;
    BottomNavigationView bottomNavigation;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mainViewPager2 = findViewById(R.id.main_viewpager2);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ShareActivity.class)));
        initViewPager2();
        initBottomNav();
    }

    private void initViewPager2() {
        List<Fragment> fragmentLis = new ArrayList<>();
        fragmentLis.add(new HomeFragment());
        fragmentLis.add(new MineFragment());
        MainViewPager2Adapter adapter = new MainViewPager2Adapter(this, fragmentLis);
        mainViewPager2.setAdapter(adapter);
        //去除尽头阴影
        View childAt = mainViewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        mainViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigation.setSelectedItemId(bottomNavigation.getMenu().getItem(position).getItemId());
            }
        });
    }

    /**
     * 初始化BottomNav与viewPager联动
     * <p>
     * 按住松手后进入选中
     * <p>
     * 拦截item长按事件，去掉文字吐司
     */
    @SuppressLint({"ClickableViewAccessibility", "NonConstantResourceId"})
    private void initBottomNav() {
        //初始化BottomNav与viewPager联动
        bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_home:
                    mainViewPager2.setCurrentItem(0);
                    return true;
                case R.id.main_my:
                    mainViewPager2.setCurrentItem(2);
                    return true;
            }
            return false;
        });
        //按住松手后进入选中
        bottomNavigation.findViewById(R.id.main_home).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                bottomNavigation.setSelectedItemId(v.getId());
            return false;
        });

        bottomNavigation.findViewById(R.id.main_my).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP)
                bottomNavigation.setSelectedItemId(v.getId());
            return false;
        });
        //拦截item长按事件，去掉文字吐司
        bottomNavigation.findViewById(R.id.main_home).setOnLongClickListener(view -> true);
        bottomNavigation.findViewById(R.id.main_my).setOnLongClickListener(view -> true);
    }
}