package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.activities.BigSelectMapShowActivity;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.model.Trip;

import java.util.List;
import java.util.Objects;

public class TripDivingAdapter extends RecyclerView.Adapter<TripDivingAdapter.MyViewHolder> {

    private List<Trip> tripList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tripType, timing, durationTrip, conditionTrip, seatNumber, tripPrice, tripLocation, tripDirection, divingEquTxt, divingPriceTxt;
        TextView time, tripDuration, conditionsTrip, seatNumber_, trip_price, trip_name, tripLocationTxt, tripDirectionTxt, divingEqu, divingPrice;
        ImageView ic_delete, ic_arrow, ic_edit;
        RelativeLayout layout, valueLayout, edit_delete_layout, topLayout, divingPriceLayout;
        Button btnRequest;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            tripType = view.findViewById(R.id.trip_type);
            timing = view.findViewById(R.id.timing);
            durationTrip = view.findViewById(R.id.tripDurationTxt);
            conditionTrip = view.findViewById(R.id.conditionsTripTxt);
            seatNumber = view.findViewById(R.id.seatNumberTxt);
            tripPrice = view.findViewById(R.id.tripPriceTxt);
            ic_delete = view.findViewById(R.id.ic_delete);
            ic_arrow = view.findViewById(R.id.ic_arrow_down);
            ic_edit = view.findViewById(R.id.ic_edit);
            valueLayout = view.findViewById(R.id.valueLayout);
            btnRequest = view.findViewById(R.id.btnRequest);
            topLayout = view.findViewById(R.id.topLayout);
            tripDirectionTxt = view.findViewById(R.id.tripDirectionTxt);
            edit_delete_layout = view.findViewById(R.id.edit_delete_layout);
            edit_delete_layout.setVisibility(View.GONE);
            time = view.findViewById(R.id.time);
            tripLocationTxt = view.findViewById(R.id.tripLocationTxt);
            divingEquTxt = view.findViewById(R.id.divingEquTxt);
            divingPriceTxt = view.findViewById(R.id.divingPriceTxt);
            time.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            trip_name = view.findViewById(R.id.trip_name);
            divingPriceLayout = view.findViewById(R.id.divingPriceLayout);
            tripDuration = view.findViewById(R.id.tripDuration);
            tripDuration.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            conditionsTrip = view.findViewById(R.id.conditionsTrip);
            conditionsTrip.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            seatNumber_ = view.findViewById(R.id.seatNumber);
            seatNumber_.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            trip_price = view.findViewById(R.id.trip_price);
            trip_price.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            tripLocation = view.findViewById(R.id.tripLocation);
            tripLocation.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            tripDirection = view.findViewById(R.id.tripDirection);
            tripDirection.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            divingEqu = view.findViewById(R.id.divingEqu);
            divingEqu.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
            divingPrice = view.findViewById(R.id.divingPrice);
            divingPrice.setTypeface(FontManager.getTypefaceTextInputBold(context), Typeface.BOLD);
        }
    }


    public TripDivingAdapter(Context context, List<Trip> tripList, OnItemClickListener listener) {
        this.tripList = tripList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Trip trip = tripList.get(position);
        holder.trip_name.setText(trip.getTripName());

        if (trip.getTripType().equals("3")) {
            holder.tripType.setText(context.getString(R.string.diving_trip_beach));
            holder.tripDirectionTxt.setVisibility(View.GONE);
            holder.tripDirection.setVisibility(View.GONE);
        } else if (trip.getTripType().equals("0") || trip.getTripType().equals("1") || trip.getTripType().equals("2")) {
            holder.tripType.setText(context.getString(R.string.diving_trip_boat));
            holder.tripDirectionTxt.setText(trip.getTrip_route());
        }

        holder.timing.setText(trip.getTiming() + " " + trip.getTime());

        holder.tripLocationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BigSelectMapShowActivity.class);
                intent.putExtra("location", trip.getStart_location());
                context.startActivity(intent);
            }
        });

        holder.durationTrip.setText(trip.getTripDuration());
        holder.conditionTrip.setText(trip.getConditionTrip());
        holder.seatNumber.setText(trip.getSeatNumber());
        holder.tripPrice.setText(trip.getTripPrice());

        if (TextUtils.equals(trip.getGears_available(), "1")) {
            holder.divingPriceLayout.setVisibility(View.VISIBLE);
            holder.divingEquTxt.setText(R.string.available);
            holder.divingPriceTxt.setText(trip.getGears_price());
        } else if (TextUtils.equals(trip.getGears_available(), "0")) {
            holder.divingEquTxt.setText(R.string.un_avaliable);
            holder.divingPriceLayout.setVisibility(View.GONE);
        }

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
        holder.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}