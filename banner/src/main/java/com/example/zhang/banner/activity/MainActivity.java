package com.example.zhang.banner.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.zhang.banner.CustomTransformer;
import com.example.zhang.banner.R;
import com.example.zhang.banner.adapter.ViewPagerAdapter;
import com.zhouwei.indicatorview.CircleIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private int [] imageIds = {R.mipmap.image1,R.mipmap.image2,R.mipmap.image3,R.mipmap.image4,R.mipmap.image5,R.mipmap.image6,R.mipmap.image7,R.mipmap.image8};
    private List<ImageView> list;
    private ViewPager mViewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private CircleIndicatorView indicatorView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:{
                    switchViewPager();
                    break;
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initAdapter();

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        indicatorView = (CircleIndicatorView) findViewById(R.id.indicatorView);
        list = new ArrayList<ImageView>();
        for (int i = 0;i<imageIds.length;i++){
            ImageView iv = new ImageView(this);
            iv.setBackgroundResource(imageIds[i]);
            list.add(iv);
        }
    }

    private void initAdapter() {
        //viewpager的适配器
        viewPagerAdapter = new ViewPagerAdapter(list);
        //给viewpager添加动画效果
        mViewPager.setPageTransformer(false,new CustomTransformer());
        // 添加监听器
        mViewPager.addOnPageChangeListener(onPageChangeListener);

        //填充适配器
        mViewPager.setAdapter(viewPagerAdapter);
        //将CircleIndicatorView与viewpager相关联   必须放在填充适配器下面
        indicatorView.setUpWithViewPager(mViewPager);
        //设置第一次加载的位置，放在中间，实现开始时左右都可以滑动
        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() / 2);

        //发送消息开始轮播
        handler.sendEmptyMessageDelayed(0,1000);

    }
    private void switchViewPager() {
        //获取当前item
        int currentItem = mViewPager.getCurrentItem();
        currentItem++;//自动加1
        mViewPager.setCurrentItem(currentItem);
        handler.removeMessages(0);//发送之前先取消，确保消息只有一个
        handler.sendEmptyMessageDelayed(0,1000);
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}
