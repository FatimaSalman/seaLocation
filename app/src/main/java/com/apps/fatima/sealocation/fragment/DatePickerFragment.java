package com.apps.fatima.sealocation.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;

import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.DatePicker;
import android.app.Dialog;

import com.apps.fatima.sealocation.R;

import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    int year, month, day;
    private TextView tv;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current date as the default date in the date picker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        //Create a new DatePickerDialog instance and return it
        /*
            DatePickerDialog Public Constructors - Here we uses first one
            public DatePickerDialog (Context context, DatePickerDialog.OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
            public DatePickerDialog (Context context, int theme, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth)
         */
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        return datePickerDialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //Do something with the date chosen by the user

        if (getArguments() != null) {
            switch (Objects.requireNonNull(getArguments().getString("first"))) {
                case "first":
                    tv = Objects.requireNonNull(getActivity()).findViewById(R.id.expireTxt);
                    break;
                case "third":
                    tv = Objects.requireNonNull(getActivity()).findViewById(R.id.expireTanksTxt);
                    break;
                case "date":
                    tv = Objects.requireNonNull(getActivity()).findViewById(R.id.dateTxt);
                    break;
                default:
                    tv = Objects.requireNonNull(getActivity()).findViewById(R.id.expireLicenceTxt);
                    break;
            }
        }


        String stringOfDate = day + "/" + (month + 1) + "/" + year;
        tv.setText(stringOfDate);
    }
}