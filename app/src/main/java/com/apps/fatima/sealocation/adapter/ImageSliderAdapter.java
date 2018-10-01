package com.apps.fatima.sealocation.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.model.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Image> pictureList;
    private boolean doNotifyDataSetChangedOnce = false;

    public ImageSliderAdapter(Context context, List<Image> pictureList) {
        this.mContext = context;
        this.pictureList = pictureList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (doNotifyDataSetChangedOnce) {
            doNotifyDataSetChangedOnce = false;
            notifyDataSetChanged();
        }

        return pictureList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.gallery_item, container, false);
        doNotifyDataSetChangedOnce = true;

        ImageView imageView = itemView.findViewById(R.id.photoItemImageView);
        final ProgressBar progressbar = itemView.findViewById(R.id.progressbar);
//        imageView.setImageResource(pictureList.get(position).getImage_url());
        Log.e("imageeee", pictureList.get(position).getImage_url());
        progressbar.setVisibility(View.VISIBLE);
        Picasso.get().load(pictureList.get(position).getImage_url()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressbar.setVisibility(View.GONE);
            }
        });

        container.addView(itemView, 0);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}