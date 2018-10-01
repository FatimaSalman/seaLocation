package com.apps.fatima.sealocation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.Course;

import java.util.List;
import java.util.Objects;

public class OfferCourseAdapter extends RecyclerView.Adapter<OfferCourseAdapter.MyViewHolder> {

    private List<Course> courseList;
    private Context context;
    private OnItemClickListener listener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseDuration, courseValue, courseRequirement, equ_value;
        ImageView ic_delete, ic_arrow, ic_edit, yesRadioButton, noRadioButton;
        RelativeLayout layout, valueLayout, edit_delete_layout, topLayout, valueDurationLayout;
        Button btnRequest;

        MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.layout);
            FontManager.applyFont(context, layout);
            courseName = view.findViewById(R.id.course_name);
            courseDuration = view.findViewById(R.id.courseDuration);
            courseValue = view.findViewById(R.id.course_value);
            courseRequirement = view.findViewById(R.id.course_requirement);
            equ_value = view.findViewById(R.id.equ_value);
            yesRadioButton = view.findViewById(R.id.yesRadioButton);
            noRadioButton = view.findViewById(R.id.noRadioButton);
            ic_delete = view.findViewById(R.id.ic_delete);
            ic_arrow = view.findViewById(R.id.ic_arrow_down);
            ic_edit = view.findViewById(R.id.ic_edit);
            valueLayout = view.findViewById(R.id.valueLayout);
            btnRequest = view.findViewById(R.id.btnRequest);
            topLayout = view.findViewById(R.id.topLayout);
            valueDurationLayout = view.findViewById(R.id.valueDurationLayout);
            edit_delete_layout = view.findViewById(R.id.edit_delete_layout);
            edit_delete_layout.setVisibility(View.GONE);
        }
    }


    public OfferCourseAdapter(Context context, List<Course> courseList, OnItemClickListener listener) {
        this.courseList = courseList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Course course = courseList.get(position);
        holder.courseName.setText(course.getCourseName());
        holder.courseDuration.setText(course.getCourseDuration());
        holder.courseRequirement.setText(course.getCourseRequirement());
        holder.courseValue.setText(course.getCourseValue());
        if (TextUtils.equals(course.getDivingStatus(), "1")) {
            holder.yesRadioButton.setImageResource(R.drawable.ic_check_circle);
            holder.noRadioButton.setImageResource(R.drawable.ic_circle);
            holder.valueDurationLayout.setVisibility(View.VISIBLE);
            holder.equ_value.setText(course.getGears_price());
        } else {
            holder.yesRadioButton.setImageResource(R.drawable.ic_circle);
            holder.noRadioButton.setImageResource(R.drawable.ic_check_circle);
            holder.valueDurationLayout.setVisibility(View.GONE);
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
        return courseList.size();
    }
}