package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.Product;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productType, productNote, equ_value, name_product;
        ImageView ic_delete, ic_arrow, ic_edit;
        RelativeLayout layout, topLayout, layout1;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            productType = view.findViewById(R.id.product_type);
            productNote = view.findViewById(R.id.note_product);
            equ_value = view.findViewById(R.id.equ_value);
            ic_delete = view.findViewById(R.id.ic_delete);
            ic_edit = view.findViewById(R.id.ic_edit);
            ic_edit.setVisibility(View.VISIBLE);
            ic_arrow = view.findViewById(R.id.ic_arrow_down);
            topLayout = view.findViewById(R.id.topLayout);
            layout1 = view.findViewById(R.id.layout1);
            name_product = view.findViewById(R.id.name_product);
        }
    }


    public ProductAdapter(Context context, List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Product product = productList.get(position);
        holder.productNote.setText(product.getProduct_note());
        holder.name_product.setText(product.getProduct_name());
        if (AppLanguage.getLanguage(context).equals("ar"))
            holder.productType.setText(product.getType_ar());
        else
            holder.productType.setText(product.getType_en());
        holder.equ_value.setText(product.getPrice());
        holder.topLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
//                if (holder.ic_arrow.getDrawable().getConstantState().equals
//                        (context.getResources().getDrawable(R.drawable.ic_arrow_down_list).getConstantState())) {
//                    holder.ic_arrow.setImageResource(R.drawable.ic_arrow_up);
//                    holder.layout1.setVisibility(View.VISIBLE);
//                } else if (holder.ic_arrow.getDrawable().getConstantState().equals
//                        (context.getResources().getDrawable(R.drawable.ic_arrow_up).getConstantState())) {
//                    holder.ic_arrow.setImageResource(R.drawable.ic_arrow_down_list);
//                    holder.layout1.setVisibility(View.GONE);
//                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (Objects.equals(holder.ic_arrow.getDrawable().getConstantState(),
                            Objects.requireNonNull(holder.ic_arrow.getContext()
                                    .getDrawable(R.drawable.ic_arrow_down_list)).getConstantState())) {
                        holder.ic_arrow.setImageResource(R.drawable.ic_arrow_up);
                        holder.layout1.setVisibility(View.VISIBLE);
                    } else if (Objects.equals(holder.ic_arrow.getDrawable().getConstantState(),
                            Objects.requireNonNull(holder.ic_arrow.getContext()
                                    .getDrawable(R.drawable.ic_arrow_up)).getConstantState())) {
                        holder.ic_arrow.setImageResource(R.drawable.ic_arrow_down_list);
                        holder.layout1.setVisibility(View.GONE);
                    }

                } else {
                    if (Objects.equals(holder.ic_arrow.getDrawable().getConstantState(),
                            context.getResources().getDrawable(R.drawable.ic_arrow_down_list).getConstantState())) {
                        holder.ic_arrow.setImageResource(R.drawable.ic_arrow_up);
                        holder.layout1.setVisibility(View.VISIBLE);
                    } else if (Objects.equals(holder.ic_arrow.getDrawable().getConstantState(),
                            context.getResources().getDrawable(R.drawable.ic_arrow_up).getConstantState())) {
                        holder.ic_arrow.setImageResource(R.drawable.ic_arrow_down_list);
                        holder.layout1.setVisibility(View.GONE);
                    }
                }
            }
        });

        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        holder.ic_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}