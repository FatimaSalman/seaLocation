package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.fragment.ProfileBoatFragment;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.Boat;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class BoatRequestAdapter extends RecyclerView.Adapter<BoatRequestAdapter.MyViewHolder> {

    private final ProfileBoatFragment profileBoatFragment;
    private List<Boat> boatList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView boatName, code, countdownTimerText, ratingTxt, timing, duration, type,
                passengersNo, rout, userNameTxt, mobileNoTxt;
        ImageView ic_cancel, ic_approve;
        RelativeLayout layout, routLayout, durationLayout, typeLayout, passengersLayout;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            boatName = view.findViewById(R.id.boatNameTxt);
            timing = view.findViewById(R.id.timing);
            duration = view.findViewById(R.id.duration);
            type = view.findViewById(R.id.type);
            userNameTxt = view.findViewById(R.id.userNameTxt);
            passengersNo = view.findViewById(R.id.passengersNo);
            rout = view.findViewById(R.id.rout);
            code = view.findViewById(R.id.codeRequestTxt);
            passengersLayout = view.findViewById(R.id.passengersLayout);
            ic_cancel = view.findViewById(R.id.ic_cancel);
            routLayout = view.findViewById(R.id.routLayout);
            ic_approve = view.findViewById(R.id.ic_approve);
            typeLayout = view.findViewById(R.id.typeLayout);
            durationLayout = view.findViewById(R.id.durationLayout);
            ratingTxt = view.findViewById(R.id.ratingTxt);
            mobileNoTxt = view.findViewById(R.id.mobileNoTxt);
            countdownTimerText = view.findViewById(R.id.countdownText);
            countdownTimerText.setVisibility(View.GONE);
        }
    }

    public BoatRequestAdapter(Context context, List<Boat> boatList, OnItemClickListener listener) {
        this.boatList = boatList;
        this.context = context;
        this.listener = listener;
        profileBoatFragment = new ProfileBoatFragment();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.boat_request_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        final Boat boat = boatList.get(position);
        holder.boatName.setText(boat.getBoatName());
        holder.code.setText(boat.getCode());
        holder.userNameTxt.setText(boat.getUser_name());

        holder.ic_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        holder.ratingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        holder.ic_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-mm-d");
            Date currentDate = formatDate.parse(boat.getCreated_at());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat outDate = new SimpleDateFormat("yyyy-mm-d");
            String date = outDate.format(currentDate);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-mm-d hh:mm:ss");
            Date currentTime = formatTime.parse(boat.getCreated_at());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat outTime = new SimpleDateFormat("HH:mm a");
            String time = outTime.format(currentTime);

            Log.e("time", time);
            Log.e("date", date);
//        HH:mm a
            if (!boat.getTiming().isEmpty())
                holder.timing.setText(boat.getDate() + " " + boat.getTiming());
            else
                holder.timing.setText(date + " " + time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!boat.getDurationTrip().isEmpty()) {
            holder.durationLayout.setVisibility(View.VISIBLE);
            holder.duration.setText(boat.getDurationTrip());
        } else {
            holder.durationLayout.setVisibility(View.GONE);
        }

        if (!boat.getTrip_type().isEmpty()) {
            holder.typeLayout.setVisibility(View.VISIBLE);
            holder.type.setText(boat.getTrip_type());
        } else {
            holder.typeLayout.setVisibility(View.VISIBLE);
            holder.type.setText(context.getString(R.string.shortest_time));
        }

        if (boat.getPassNumber().isEmpty()) {
            holder.passengersLayout.setVisibility(View.GONE);
        } else if (boat.getPassNumber().equals("0")) {
            holder.passengersLayout.setVisibility(View.GONE);
        } else {
            holder.passengersLayout.setVisibility(View.VISIBLE);
            holder.passengersNo.setText(boat.getPassNumber());
        }
        if (boat.getDirectionTrip().isEmpty()) {
            holder.rout.setText(R.string.not_found);
        } else if (boat.getDirectionTrip().equals("null")) {
            holder.rout.setText(R.string.not_found);
        } else {
            holder.rout.setText(boat.getDirectionTrip());
        }
        Log.e("approved", boat.getApproved());
        switch (boat.getApproved()) {

            case "0":
                holder.ic_approve.setVisibility(View.VISIBLE);
                holder.ic_cancel.setVisibility(View.VISIBLE);
                holder.mobileNoTxt.setText(context.getString(R.string.dash));
                if (boat.getApproved().equals("0")) {
                    new CountDownTimer(660000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            int secondNumber = (int) (millisUntilFinished / 1000);
                            holder.countdownTimerText.setText("seconds remaining: " + secondNumber);
                            secondNumber--;
                        }

                        public void onFinish() {
                            holder.countdownTimerText.setText("0");
                            if (boat.getApproved().equals("0")) {
                                profileBoatFragment.deleteBoatBook(context,
                                        AppPreferences.getString(context, "token"), boat.getId());
                            }
                        }
                    }.start();
                }
                break;
            case "1":
                holder.mobileNoTxt.setText(boat.getMobile());
                if (boat.getIs_rating().equals("0")) {
                    holder.ratingTxt.setVisibility(View.VISIBLE);
                }
                holder.ic_approve.setVisibility(View.GONE);
                holder.ic_cancel.setVisibility(View.GONE);
                break;
            case "2":
                holder.mobileNoTxt.setText(boat.getMobile());
                holder.ic_approve.setVisibility(View.GONE);
                holder.ic_cancel.setVisibility(View.GONE);
                break;
            case "3":
                holder.mobileNoTxt.setText(boat.getMobile());
                holder.ic_approve.setVisibility(View.GONE);
                holder.ic_cancel.setVisibility(View.GONE);
                holder.ratingTxt.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return boatList.size();
    }
}