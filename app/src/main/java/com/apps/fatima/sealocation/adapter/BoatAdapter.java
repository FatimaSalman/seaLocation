package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.activities.BigSelectMapShowActivity;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.Boat;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BoatAdapter extends RecyclerView.Adapter<BoatAdapter.MyViewHolder> {

    private List<Boat> boatList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView boatName, passNumber, width, height, value, tripLocationTxt, widthMesaure, heightMesaure;
        ImageView ic_delete, ic_arrow, ic_edit;
        RelativeLayout layout, valueLayout, topLayout;
        LinearLayout request;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            boatName = view.findViewById(R.id.boat_name);
            passNumber = view.findViewById(R.id.passengersNo);
            width = view.findViewById(R.id.width_number);
            height = view.findViewById(R.id.height_number);
            value = view.findViewById(R.id.trip_valueTxt);
            ic_delete = view.findViewById(R.id.ic_delete);
            topLayout = view.findViewById(R.id.topLayout);
            ic_arrow = view.findViewById(R.id.ic_arrow_down);
            ic_edit = view.findViewById(R.id.ic_edit);
            valueLayout = view.findViewById(R.id.valueLayout);
            request = view.findViewById(R.id.request);
            tripLocationTxt = view.findViewById(R.id.tripLocationTxt);
            widthMesaure = view.findViewById(R.id.widthMesaure);
            heightMesaure = view.findViewById(R.id.heightMesaure);
            request.setVisibility(View.GONE);
        }
    }

    public BoatAdapter(Context context, List<Boat> boatList, OnItemClickListener listener) {
        this.boatList = boatList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.boat_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Boat boat = boatList.get(position);
        holder.boatName.setText(boat.getBoatName());
        holder.passNumber.setText(boat.getPassNumber());
        holder.width.setText(boat.getWidth());
        holder.height.setText(boat.getHeight());
        if (AppLanguage.getLanguage(context).equals("ar")) {
            holder.widthMesaure.setText(boat.getWidthMeasureAr());
            holder.heightMesaure.setText(boat.getHeightMeasureAr());
        } else {
            holder.heightMesaure.setText(boat.getHeightMeasureEn());
            holder.widthMesaure.setText(boat.getWidthMeasureEn());
        }
        holder.value.setText(boat.getValue());
        holder.tripLocationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BigSelectMapShowActivity.class);
                intent.putExtra("location", boat.getLocation());
                context.startActivity(intent);
            }
        });
        holder.topLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
//                if (holder.ic_arrow.getDrawable().getConstantState().equals
//                        (context.getResources().getDrawable(R.drawable.ic_arrow_down_list).getConstantState())) {
//                    holder.ic_arrow.setImageResource(R.drawable.ic_arrow_up);
//                    holder.valueLayout.setVisibility(View.VISIBLE);
//                } else if (holder.ic_arrow.getDrawable().getConstantState().equals
//                        (context.getResources().getDrawable(R.drawable.ic_arrow_up).getConstantState())) {
//                    holder.ic_arrow.setImageResource(R.drawable.ic_arrow_down_list);
//                    holder.valueLayout.setVisibility(View.GONE);
//                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (Objects.equals(holder.ic_arrow.getDrawable().getConstantState(),
                            Objects.requireNonNull(holder.ic_arrow.getContext()
                                    .getDrawable(R.drawable.ic_arrow_down_list)).getConstantState())) {
                        holder.ic_arrow.setImageResource(R.drawable.ic_arrow_up);
                        holder.valueLayout.setVisibility(View.VISIBLE);
                    } else if (Objects.equals(holder.ic_arrow.getDrawable().getConstantState(),
                            Objects.requireNonNull(holder.ic_arrow.getContext()
                                    .getDrawable(R.drawable.ic_arrow_up)).getConstantState())) {
                        holder.ic_arrow.setImageResource(R.drawable.ic_arrow_down_list);
                        holder.valueLayout.setVisibility(View.GONE);
                    }

                } else {
                    if (Objects.equals(holder.ic_arrow.getDrawable().getConstantState(),
                            context.getResources().getDrawable(R.drawable.ic_arrow_down_list).getConstantState())) {
                        holder.ic_arrow.setImageResource(R.drawable.ic_arrow_up);
                        holder.valueLayout.setVisibility(View.VISIBLE);
                    } else if (Objects.equals(holder.ic_arrow.getDrawable().getConstantState(),
                            context.getResources().getDrawable(R.drawable.ic_arrow_up).getConstantState())) {
                        holder.ic_arrow.setImageResource(R.drawable.ic_arrow_down_list);
                        holder.valueLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
        holder.ic_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        holder.ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boatList.size();
    }
}