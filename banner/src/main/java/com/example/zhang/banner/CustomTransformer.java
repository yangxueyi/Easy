package com.example.zhang.banner;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Zhang on 2017/6/14.
 */

public class CustomTransformer implements ViewPager.PageTransformer {
    //图片缩放比例
    private static final float MIN_SCALE = 0.8F;

    @Override
    public void transformPage(View view, float position) {
        if(position<-1){
            view.setScaleY(MIN_SCALE);
        }else if(position<=1){
            float scale = Math.max(MIN_SCALE,1-Math.abs(position));
            view.setScaleY(scale);
        }else{
            view.setScaleY(MIN_SCALE);
        }
    }
}
