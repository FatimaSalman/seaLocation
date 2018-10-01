package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.model.Item;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private List<Item> itemList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, city, pageNameTxt;
        ImageView userImage, arrow;
        RelativeLayout layout;
        ProgressBar progressbar;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            userName = view.findViewById(R.id.userNameTxt);
            pageNameTxt = view.findViewById(R.id.pageNameTxt);
            city = view.findViewById(R.id.cityTxt);
            userImage = view.findViewById(R.id.img);
            arrow = view.findViewById(R.id.arrow);
            progressbar = view.findViewById(R.id.progressbar);
        }
    }


    public ItemAdapter(Context context, List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Item item = itemList.get(position);
        holder.userName.setText(item.getUserName());
        if (item.getPageName() != null) {
            if (TextUtils.equals(item.getPageName(), "null") || item.getPageName().isEmpty()) {
                holder.pageNameTxt.setVisibility(View.GONE);
            } else {
                holder.pageNameTxt.setText(item.getPageName());
                holder.pageNameTxt.setVisibility(View.VISIBLE);
            }
        }
        if (AppLanguage.getLanguage(context).equals("en"))
            holder.arrow.setImageResource(R.drawable.ic_forward_arrow);
        if (item.getCityName().equals("null")) {
            holder.city.setText(item.getCityName());
        } else {
            if (AppLanguage.getLanguage(context).equals("ar")) {
                holder.city.setText(item.getName_ar());
            } else {
                holder.city.setText(item.getName_en());
            }
        }
        holder.progressbar.setVisibility(View.VISIBLE);
        if (item.getImage().equals("null")) {
            holder.userImage.setImageResource(R.drawable.img_user);
            holder.progressbar.setVisibility(View.GONE);
        } else {
            Picasso.get().load(FontManager.IMAGE_URL + item.getImage()).into(holder.userImage, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressbar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    holder.progressbar.setVisibility(View.GONE);
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}