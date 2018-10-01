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

import java.util.List;

public class BoatEventRequestAdapter extends RecyclerView.Adapter<BoatEventRequestAdapter.MyViewHolder> {

    private final ProfileBoatFragment profileBoatFragment;
    private List<Boat> boatList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView boatName, code, ratingTxt, countdownTimerText, timing, duration, type,
                passengersNo, rout, user_name, mobileNoTxt;
        ImageView ic_cancel, ic_approve;
        RelativeLayout layout, routLayout, durationLayout, typeLayout, passengersLayout;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            boatName = view.findViewById(R.id.boatNameTxt);
            code = view.findViewById(R.id.codeRequestTxt);
            ic_cancel = view.findViewById(R.id.ic_cancel);
            ic_approve = view.findViewById(R.id.ic_approve);
            ratingTxt = view.findViewById(R.id.ratingTxt);
            timing = view.findViewById(R.id.timing);
            user_name = view.findViewById(R.id.userNameTxt);
            duration = view.findViewById(R.id.duration);
            type = view.findViewById(R.id.type);
            passengersNo = view.findViewById(R.id.passengersNo);
            rout = view.findViewById(R.id.rout);
            countdownTimerText = view.findViewById(R.id.countdownText);
            countdownTimerText.setVisibility(View.GONE);
            passengersLayout = view.findViewById(R.id.passengersLayout);
            routLayout = view.findViewById(R.id.routLayout);
            typeLayout = view.findViewById(R.id.typeLayout);
            durationLayout = view.findViewById(R.id.durationLayout);
            mobileNoTxt = view.findViewById(R.id.mobileNoTxt);
        }
    }


    public BoatEventRequestAdapter(Context context, List<Boat> boatList, OnItemClickListener listener) {
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Boat boat = boatList.get(position);
        Log.e("dtata event", boat.getBoatName() + " // " + boat.getCode());
        holder.boatName.setText(boat.getBoatName());
        holder.code.setText(boat.getCode());
        holder.user_name.setText(boat.getUser_name());

        holder.timing.setText(boat.getTiming() + " " + boat.getDate());

        if (boat.getDurationTrip() != null) {
            if (!boat.getDurationTrip().isEmpty()) {
                holder.durationLayout.setVisibility(View.VISIBLE);
                holder.duration.setText(boat.getDurationTrip());
            } else {
                holder.durationLayout.setVisibility(View.GONE);
            }
        } else {
            holder.durationLayout.setVisibility(View.GONE);
        }

//        if (boat.getTrip_type() != null) {
//            if (!boat.getTrip_type().isEmpty()) {
//                holder.typeLayout.setVisibility(View.VISIBLE);
//                holder.type.setText(boat.getTrip_type());
//            } else {
//                holder.typeLayout.setVisibility(View.VISIBLE);
//                holder.type.setText(context.getString(R.string.shortest_time));
//            }
//        } else {
//            holder.typeLayout.setVisibility(View.VISIBLE);
//            holder.type.setText(context.getString(R.string.shortest_time));
//        }

        switch (boat.getBoatType()) {
            case "2":
                holder.type.setText(context.getString(R.string.diving));
                break;
            case "1":
                holder.type.setText(context.getString(R.string.fishing));
                break;
            case "0":
                holder.type.setText(context.getString(R.string.picnic));
                break;
        }

        if (boat.getPassNumber() != null) {
            if (boat.getPassNumber().isEmpty()) {
                holder.passengersLayout.setVisibility(View.GONE);
            } else if (boat.getPassNumber().equals("0")) {
                holder.passengersLayout.setVisibility(View.GONE);
            } else {
                holder.passengersLayout.setVisibility(View.VISIBLE);
                holder.passengersNo.setText(boat.getPassNumber());
            }
        } else {
            holder.passengersLayout.setVisibility(View.GONE);
        }

        if (boat.getDirectionTrip() != null) {
            if (boat.getDirectionTrip().isEmpty()) {
                holder.routLayout.setVisibility(View.GONE);
            } else if (boat.getDirectionTrip().equals("null")) {
                holder.routLayout.setVisibility(View.GONE);
            } else {
                holder.rout.setText(boat.getDirectionTrip());
                holder.routLayout.setVisibility(View.VISIBLE);
            }
        } else {
            holder.routLayout.setVisibility(View.GONE);
        }
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

        switch (boat.getApproved()) {
            case "0":
                holder.ic_approve.setVisibility(View.VISIBLE);
                holder.ic_cancel.setVisibility(View.VISIBLE);
                holder.mobileNoTxt.setText(context.getString(R.string.dash));
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
                holder.ratingTxt.setVisibility(View.GONE);
                holder.ic_approve.setVisibility(View.GONE);
                holder.ic_cancel.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return boatList.size();
    }
}