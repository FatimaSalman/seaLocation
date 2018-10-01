package com.apps.fatima.sealocation.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.AppErrorsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private TextView textView;
    private TextView tv;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar datetime = Calendar.getInstance();
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

        if (getArguments() != null) {
            if (Objects.equals(getArguments().getString("time"), "time")) {
                textView = getActivity().findViewById(R.id.timeTxt);
            } else
                textView = Objects.requireNonNull(getActivity()).findViewById(R.id.timing);
        }

        String dateTxt = tv.getText().toString();
Log.e("fdfdfdftime",dateTxt);
        Locale loc = new Locale("en", "US");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd/M/yyyy", loc);
        try {
            Date date = format.parse(dateTxt);
            Date date1 = new Date();
            int diff = date1.compareTo(date);
            Log.e("diff", diff + "");

            if (diff > 0) {
                System.out.println("The date is future day");
                if (hourOfDay < datetime.get(Calendar.HOUR_OF_DAY)) {
                    AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.correct_time));
                    textView.setText("");
                } else if (hourOfDay == datetime.get(Calendar.HOUR_OF_DAY)) {
                    if (minute < datetime.get(Calendar.MINUTE)) {
                        AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.correct_time));
                        textView.setText("");
                    } else {
                        String aM = "AM";
                        if (view.getCurrentHour() > 11) {
//                            hourOfDay = hourOfDay - 12;
                            aM = "PM";
                        }
                        if (hourOfDay == 12) {
                            hourOfDay = 12;
                        }
                        textView.setText(hourOfDay + ":" + minute + "  " + aM);
                        textView.setError(null);
                    }
                } else {
                    String aM = "AM";
                    if (view.getCurrentHour() > 11) {
//                        hourOfDay = hourOfDay - 12;
                        aM = "PM";
                    }
                    if (hourOfDay == 12) {
                        hourOfDay = 12;
                    }
                    String min;
                    if (minute < 10)
                        min = "0" + minute;
                    else
                        min = String.valueOf(minute);

                    textView.setText(hourOfDay + ":" + min + "  " + aM);
                    textView.setError(null);
                }
            } else {
                Log.e("fdfdfdd", "The date is older than current day");
                String aM = "AM";
                if (view.getCurrentHour() > 11) {
//                    hourOfDay = hourOfDay - 12;
                    aM = "PM";
                }
                Log.e("hour of day", hourOfDay + "");
                if (hourOfDay == 12) {
                    hourOfDay = 12;
                }
                String min;
                if (minute < 10)
                    min = "0" + minute;
                else
                    min = String.valueOf(minute);
                textView.setText(hourOfDay + ":" + min + "  " + aM);
                textView.setError(null);
            }
            Log.e("dfdf0", date + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}