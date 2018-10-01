package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.model.SpinnerItem;

import java.util.List;
import java.util.Locale;


public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.MyViewHolder> {
    private List<SpinnerItem> spinnerItemList;
    OnItemClickListener listener;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        LinearLayout item;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.item_val);
            item = view.findViewById(R.id.fontLayout);
            if (AppLanguage.getLanguage(context).equals("ar"))
                FontManager.applyFont(context, item);

        }
    }

    public SpinnerAdapter(Context context, List<SpinnerItem> spinnerItemList, OnItemClickListener listener) {
        this.spinnerItemList = spinnerItemList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.toolbar_spinner_item_dropdown,
                parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SpinnerItem spinnerItem = spinnerItemList.get(position);
        if (AppLanguage.getLanguage(context).equals("ar")) {
            holder.title.setText(spinnerItem.getTextA());
        } else {
            holder.title.setText(spinnerItem.getText());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return spinnerItemList.size();
    }

}