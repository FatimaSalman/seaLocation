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

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.fragment.ProfileDrivingFragment;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.Course;

import java.util.List;

public class CourseRequestAdapter extends RecyclerView.Adapter<CourseRequestAdapter.MyViewHolder> {

    private final ProfileDrivingFragment profileDrivingFragment;
    private List<Course> courseList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView boatName, code, ratingTxt, countdownTimerText, nameTxt, mobileNoTxt; //
        ImageView ic_cancel, ic_approve;
        RelativeLayout layout;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            boatName = view.findViewById(R.id.boatNameTxt);
            code = view.findViewById(R.id.codeRequestTxt);
            ic_cancel = view.findViewById(R.id.ic_cancel);
            ic_approve = view.findViewById(R.id.ic_approve);
            ratingTxt = view.findViewById(R.id.ratingTxt);
            nameTxt = view.findViewById(R.id.nameTxt);
            countdownTimerText = view.findViewById(R.id.countdownTimerText);
            mobileNoTxt = view.findViewById(R.id.mobileNoTxt);
        }
    }


    public CourseRequestAdapter(Context context, List<Course> courseList, OnItemClickListener listener) {
        this.courseList = courseList;
        this.context = context;
        this.listener = listener;
        profileDrivingFragment = new ProfileDrivingFragment();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_request_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Course boat = courseList.get(position);
        holder.boatName.setText(boat.getCourseName());
        holder.code.setText(boat.getGuid());
        holder.nameTxt.setText(boat.getUser_name());

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
                        if (boat.getApproved().equals("0")) {
                            profileDrivingFragment.deleteBoatBook(context,
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
                holder.ic_approve.setVisibility(View.GONE);
                holder.ic_cancel.setVisibility(View.GONE);
                holder.ratingTxt.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }
}