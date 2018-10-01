package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.activities.BoatDetailsActivity;
import com.apps.fatima.sealocation.activities.DriverDetailsActivity;
import com.apps.fatima.sealocation.activities.TankDetailsActivity;

import com.apps.fatima.sealocation.model.Orders;

import org.w3c.dom.Text;

import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    private List<Orders> ordersList;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView codeTxt, typeTxt, mobileTxt;

        MyViewHolder(View view) {
            super(view);
            codeTxt = view.findViewById(R.id.codeTxt);
            mobileTxt = view.findViewById(R.id.mobileTxt);
            typeTxt = view.findViewById(R.id.typeTxt);
        }
    }


    public MyOrdersAdapter(Context context, List<Orders> ordersList) {
        this.ordersList = ordersList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderl_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Orders orders = ordersList.get(position);
        holder.codeTxt.setText(orders.getCode_request());

        if (TextUtils.equals(orders.getTrip_type(), "boat")) {
            holder.typeTxt.setText(context.getString(R.string.boat));
        } else if (TextUtils.equals(orders.getTrip_type(), "trip")) {
            holder.typeTxt.setText(context.getString(R.string.boat_trip));
        } else {
            holder.typeTxt.setText(orders.getTrip_type());
        }
        if (TextUtils.equals(orders.getApproved(), "0")) {
            holder.mobileTxt.setText(context.getString(R.string.dash));
        } else {
            holder.mobileTxt.setText(orders.getMobile());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orders.getBoat_id() != null && !orders.getBoat_id().isEmpty()) {
                    Intent intent = new Intent(context, BoatDetailsActivity.class);
                    intent.putExtra("user_id", orders.getUser_id());
                    intent.putExtra("id", orders.getBoat_id());
                    intent.putExtra("partner_id", orders.getPartner_id());
                    context.startActivity(intent);
                } else if (orders.getDiver_id() != null && !orders.getDiver_id().isEmpty()) {
                    Intent intent = new Intent(context, DriverDetailsActivity.class);
                    intent.putExtra("user_id", orders.getUser_id());
                    intent.putExtra("id", orders.getDiver_id());
                    intent.putExtra("partner_id", orders.getPartner_id());
                    context.startActivity(intent);
                } else if (orders.getTank_id() != null && !orders.getTank_id().isEmpty()) {
                    Intent intent = new Intent(context, TankDetailsActivity.class);
                    intent.putExtra("user_id", orders.getUser_id());
                    intent.putExtra("id", orders.getTank_id());
                    intent.putExtra("partner_id", orders.getPartner_id());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }
}