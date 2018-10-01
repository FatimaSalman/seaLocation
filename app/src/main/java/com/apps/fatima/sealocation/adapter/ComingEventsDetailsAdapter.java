package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.activities.BigSelectMapShowActivity;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.Boat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ComingEventsDetailsAdapter extends RecyclerView.Adapter<ComingEventsDetailsAdapter.MyViewHolder> {

    private List<Boat> boatList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView boatName, tripType, timing, locationTrip, directionTrip, durationTrip,
                conditionTrip, seatNumber, tripPrice;
        TextView time, tripLocation, tripDirection, tripDuration,
                conditionsTrip, seatNumber_, trip_price;
        ImageView ic_delete, ic_arrow, ic_edit;
        RelativeLayout layout, valueLayout, edit_delete_layout, topLayout;
        Button btnRequestNow;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            boatName = view.findViewById(R.id.boat_name);
            tripType = view.findViewById(R.id.trip_type);
            btnRequestNow = view.findViewById(R.id.btnRequestNow);
            btnRequestNow.setVisibility(View.VISIBLE);
            timing = view.findViewById(R.id.timing);
            locationTrip = view.findViewById(R.id.tripLocationTxt);
            directionTrip = view.findViewById(R.id.tripDirectionTxt);
            durationTrip = view.findViewById(R.id.tripDurationTxt);
            conditionTrip = view.findViewById(R.id.conditionsTripTxt);
            seatNumber = view.findViewById(R.id.seatNumberTxt);
            tripPrice = view.findViewById(R.id.tripPriceTxt);
            ic_delete = view.findViewById(R.id.ic_delete);
            ic_arrow = view.findViewById(R.id.ic_arrow_down);
            ic_edit = view.findViewById(R.id.ic_edit);
            valueLayout = view.findViewById(R.id.valueLayout);
            edit_delete_layout = view.findViewById(R.id.edit_delete_layout);
            topLayout = view.findViewById(R.id.topLayout);
            edit_delete_layout.setVisibility(View.GONE);
            time = view.findViewById(R.id.time);
            time.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            tripLocation = view.findViewById(R.id.tripLocation);
            tripLocation.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            tripDirection = view.findViewById(R.id.tripDirection);
            tripDirection.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            tripDuration = view.findViewById(R.id.tripDuration);
            tripDuration.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            conditionsTrip = view.findViewById(R.id.conditionsTrip);
            conditionsTrip.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            seatNumber_ = view.findViewById(R.id.seatNumber);
            seatNumber_.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            trip_price = view.findViewById(R.id.trip_price);
            trip_price.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
        }
    }


    public ComingEventsDetailsAdapter(Context context, List<Boat> boatList, OnItemClickListener listener) {
        this.boatList = boatList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.boat_event_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Boat boat = boatList.get(position);
        holder.boatName.setText(boat.getBoatName());
        switch (boat.getBoatType()) {
            case "2":
                holder.tripType.setText(context.getString(R.string.diving));
                break;
            case "1":
                holder.tripType.setText(context.getString(R.string.fishing));
                break;
            case "0":
                holder.tripType.setText(context.getString(R.string.picnic));
                break;
        }
        holder.timing.setText(boat.getTiming() + " " + boat.getDate());

        holder.locationTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BigSelectMapShowActivity.class);
                intent.putExtra("location", boat.getLocationTrip());
                context.startActivity(intent);
            }
        });
        holder.directionTrip.setText(boat.getDirectionTrip());
        holder.durationTrip.setText(boat.getDurationTrip());
        holder.conditionTrip.setText(boat.getConditionTrip());
        holder.seatNumber.setText(boat.getSeatNumber());
        holder.tripPrice.setText(boat.getTripPrice());

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