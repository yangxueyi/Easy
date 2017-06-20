package com.example.zhang.banner.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.ParserAdapter;

import java.util.List;

/**
 * Created by Zhang on 2017/6/14.
 */

public class ViewPagerAdapter extends PagerAdapter {


    List<ImageView> list  ;
   public  ViewPagerAdapter( List<ImageView> list){
       this.list=list;
   }

    @Override
    public int getCount() {
        return list.size()*10000*500;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
       position = position % list.size();

        ViewGroup parent = (ViewGroup) list.get(position).getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(list.get(position));
        return list.get(position);
}

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
