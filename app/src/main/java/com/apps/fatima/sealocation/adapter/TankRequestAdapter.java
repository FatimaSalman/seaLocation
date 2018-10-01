package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.fragment.ProfileTankFragment;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.Tanks;

import java.util.List;
import java.util.Locale;

public class TankRequestAdapter extends RecyclerView.Adapter<TankRequestAdapter.MyViewHolder> {

    private final ProfileTankFragment profileTankFragment;
    private List<Tanks> tanksList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tankType, tankNumber, ratingTxt, code, countdownTimerText, userNameTxt, mobileNoTxt;
        ImageView ic_cancel, ic_approve;
        RelativeLayout layout;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            tankType = view.findViewById(R.id.typeRequestTxt);
            tankNumber = view.findViewById(R.id.numberRequestTxt);
            code = view.findViewById(R.id.codeRequestTxt);
            ratingTxt = view.findViewById(R.id.ratingTxt);
            ic_cancel = view.findViewById(R.id.ic_cancel);
            userNameTxt = view.findViewById(R.id.userNameTxt);
            countdownTimerText = view.findViewById(R.id.countdownTimerText);
            ic_approve = view.findViewById(R.id.ic_approve);
            mobileNoTxt = view.findViewById(R.id.mobileNoTxt);
            countdownTimerText.setVisibility(View.GONE);
        }
    }


    public TankRequestAdapter(Context context, List<Tanks> tanksList, OnItemClickListener listener) {
        this.tanksList = tanksList;
        this.context = context;
        this.listener = listener;
        profileTankFragment = new ProfileTankFragment();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tank_request_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Tanks tanks = tanksList.get(position);
        if (AppLanguage.getLanguage(context).equals("ar"))
            holder.tankType.setText(tanks.getTitle_ar());
        else
            holder.tankType.setText(tanks.getTitle_en());
        holder.tankNumber.setText(tanks.getQuantity());
        holder.code.setText(tanks.getCodeRequest());
        holder.userNameTxt.setText(tanks.getName());

        holder.ic_approve.setOnClickListener(new View.OnClickListener() {
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
        holder.ratingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, position);
            }
        });
        switch (tanks.getApproved()) {
            case "0":
                holder.mobileNoTxt.setText(context.getString(R.string.dash));
                holder.ic_approve.setVisibility(View.VISIBLE);
                holder.ic_cancel.setVisibility(View.VISIBLE);
                new CountDownTimer(660000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        int secondNumber = (int) (millisUntilFinished / 1000);
                        holder.countdownTimerText.setText("seconds remaining: " + secondNumber);
                        secondNumber--;
                    }

                    public void onFinish() {
                        holder.countdownTimerText.setText("0");
                        if (tanks.getApproved().equals("0"))
                            profileTankFragment.deleteBoatBook(context,
                                    AppPreferences.getString(context, "token"), tanks.getId());
                    }
                }.start();
                break;
            case "1":
                holder.mobileNoTxt.setText(tanks.getMobile());
                if (tanks.getIs_rating().equals("0")) {
                    holder.ratingTxt.setVisibility(View.VISIBLE);
                }
                holder.ic_approve.setVisibility(View.GONE);
                holder.ic_cancel.setVisibility(View.GONE);
                break;
            case "2":
                holder.mobileNoTxt.setText(tanks.getMobile());
                holder.ic_approve.setVisibility(View.GONE);
                holder.ic_cancel.setVisibility(View.GONE);
                break;
            case "3":
                holder.mobileNoTxt.setText(tanks.getMobile());
                holder.ic_approve.setVisibility(View.GONE);
                holder.ratingTxt.setVisibility(View.GONE);
                holder.ic_cancel.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tanksList.size();
    }
}