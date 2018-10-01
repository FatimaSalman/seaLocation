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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.activities.BigSelectMapShowActivity;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.model.Boat;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BoatDetailsAdapter extends RecyclerView.Adapter<BoatDetailsAdapter.MyViewHolder> {

    private List<Boat> boatList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView boatName, passNumber, width, heightMesaure, widthMesaure, height, value, tripLocationTxt;
        ImageView ic_delete, ic_arrow, ic_edit;
        RelativeLayout layout, valueLayout, edit_delete_layout, topLayout;
        Button btnRequestNow, btnRequestLater;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            boatName = view.findViewById(R.id.boat_name);
            heightMesaure = view.findViewById(R.id.heightMesaure);
            widthMesaure = view.findViewById(R.id.widthMesaure);
            passNumber = view.findViewById(R.id.passengersNo);
            width = view.findViewById(R.id.width_number);
            height = view.findViewById(R.id.height_number);
            value = view.findViewById(R.id.trip_valueTxt);
            ic_delete = view.findViewById(R.id.ic_delete);
            ic_arrow = view.findViewById(R.id.ic_arrow_down);
            ic_edit = view.findViewById(R.id.ic_edit);
            topLayout = view.findViewById(R.id.topLayout);
            btnRequestNow = view.findViewById(R.id.btnRequestNow);
            btnRequestLater = view.findViewById(R.id.btnRequestLater);
            valueLayout = view.findViewById(R.id.valueLayout);
            edit_delete_layout = view.findViewById(R.id.edit_delete_layout);
            edit_delete_layout.setVisibility(View.GONE);
            tripLocationTxt = view.findViewById(R.id.tripLocationTxt);
        }
    }


    public BoatDetailsAdapter(Context context, List<Boat> boatList, OnItemClickListener listener) {
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
        if (AppLanguage.getLanguage(context).equals("ar")) {
            holder.widthMesaure.setText(boat.getWidthMeasureAr());
            holder.heightMesaure.setText(boat.getHeightMeasureAr());
        } else {
            holder.heightMesaure.setText(boat.getHeightMeasureEn());
            holder.widthMesaure.setText(boat.getWidthMeasureEn());
        }

        holder.tripLocationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BigSelectMapShowActivity.class);
                intent.putExtra("location", boat.getLocation());
                context.startActivity(intent);
            }
        });

        holder.boatName.setText(boat.getBoatName());
        holder.passNumber.setText(boat.getPassNumber());
        holder.width.setText(boat.getWidth());
        holder.height.setText(boat.getHeight());
        holder.value.setText(boat.getValue());
        holder.topLayout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
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
        holder.btnRequestLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        holder.btnRequestNow.setOnClickListener(new View.OnClickListener() {
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