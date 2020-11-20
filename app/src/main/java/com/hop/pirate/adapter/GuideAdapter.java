package com.hop.pirate.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.hop.pirate.R;

import java.util.List;

public class GuideAdapter extends PagerAdapter{

    private Context mContext;
    private int[] images ;

    public GuideAdapter(Context context, int[] images) {
        mContext = context;
        this.images = images;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.item_pager, null);
        view.findViewById(R.id.image).setBackgroundResource(images[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return images ==null ? 0 : images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}