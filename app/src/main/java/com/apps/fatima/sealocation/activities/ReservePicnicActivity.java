package com.apps.fatima.sealocation.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.fragment.DatePickerFragment;
import com.apps.fatima.sealocation.fragment.TimePickerFragment;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ReservePicnicActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private Handler handler;
    private EditText durationEditText;
    private TextView person_number, timeTxt, dateTxt;
    private String user_id, boat_id, token, boatName, passengerNumber, name, partner_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_boat_diving_fishing_form);
        handler = new Handler(Looper.getMainLooper());
        user_id = getIntent().getStringExtra("user_id");
        partner_id = getIntent().getStringExtra("partner_id");
        boat_id = getIntent().getStringExtra("boat_id");
        boatName = getIntent().getStringExtra("boat_name");
        name = getIntent().getStringExtra("name");
        passengerNumber = getIntent().getStringExtra("passengerNumber");
        Log.e("passengerNumber", passengerNumber);
        token = AppPreferences.getString(this, "token");
        init();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout plusLayout = findViewById(R.id.plusLayout);
        RelativeLayout minusLayout = findViewById(R.id.minusLayout);
        FontManager.applyFont(this, layout);

        ImageView ic_back = layout.findViewById(R.id.ic_back);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        person_number = layout.findViewById(R.id.person_number);
        durationEditText = layout.findViewById(R.id.durationEditText);
        EditText directionEditText = layout.findViewById(R.id.directionEditText);
        directionEditText.setVisibility(View.GONE);
        timeTxt = layout.findViewById(R.id.timeTxt);
        dateTxt = layout.findViewById(R.id.dateTxt);
        TextView typeTxt = layout.findViewById(R.id.typeTxt);
        TextView boat_name = layout.findViewById(R.id.boat_name);
        typeTxt.setText(getString(R.string.picnic));
        boat_name.setText(boatName);
        Button reserveBtn = layout.findViewById(R.id.reserveBtn);

        RelativeLayout fontLayout = layout.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        backLayout.setOnClickListener(this);
        timeTxt.setOnClickListener(this);
        dateTxt.setOnClickListener(this);
        plusLayout.setOnClickListener(this);
        minusLayout.setOnClickListener(this);
        reserveBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int totalCount = Integer.parseInt(person_number.getText().toString());
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                person_number.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            person_number.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.dateTxt) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "date");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "Date Picker");
        } else if (id == R.id.timeTxt) {
            DialogFragment newFragment = new TimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("time", "time");
            bundle.putString("first", "date");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "timePicker");
        } else if (id == R.id.reserveBtn) {
            reserveFishing();
        }
    }

    public void reserveFishing() {
        final String personNumber = person_number.getText().toString().trim();
        final String time = timeTxt.getText().toString().trim();
        final String date = dateTxt.getText().toString().trim();
        final String durationTrip = durationEditText.getText().toString().trim();
        int personCount = Integer.parseInt(personNumber);
        int passengerCount = Integer.parseInt(passengerNumber);
        if (TextUtils.isEmpty(personNumber)) {
            person_number.setError(getString(R.string.error_field_required));
            person_number.requestFocus();
        } else if (TextUtils.equals(personNumber, "0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_quantity));
        } else if (personCount > passengerCount) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_most) + " " + passengerCount);
        } else if (TextUtils.isEmpty(time)) {
            timeTxt.setError(getString(R.string.error_field_required));
            timeTxt.requestFocus();
        } else if (TextUtils.isEmpty(date)) {
            dateTxt.setError(getString(R.string.error_field_required));
            dateTxt.requestFocus();
        } else if (TextUtils.isEmpty(durationTrip)) {
            durationEditText.setError(getString(R.string.error_field_required));
            durationEditText.requestFocus();
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("boat_id", boat_id)
                            .addFormDataPart("user_id", user_id)
                            .addFormDataPart("partner_id", partner_id)
                            .addFormDataPart("passengers", personNumber)
                            .addFormDataPart("start_time", time)
                            .addFormDataPart("start_date", date)
                            .addFormDataPart("duration", durationTrip)
                            .addFormDataPart("trip_type", getString(R.string.picnic));

                    RequestBody requestBody = builder.build();
                    reserveFishingInfo(requestBody);
                }
            }).start();
        }
    }

    public void reserveFishingInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(ReservePicnicActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    OkHttpClient client;

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(5, TimeUnit.MINUTES)
                            .writeTimeout(5, TimeUnit.MINUTES)
                            .readTimeout(5, TimeUnit.MINUTES);

                    client = builder.build();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "book-boat").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            if (jsonObject.has("error")) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        progressDialog.hide();

                                    }
                                });
                            } else if (jsonObject.has("success")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        AppErrorsManager.showSuccessDialog(ReservePicnicActivity.this, " " +
                                                getString(R.string.we_send_the_request_to) + " " + name + " "
                                                + getString(R.string.and_we_will_send_contact_number_with_the_code_request) +
                                                " ", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(ReservePicnicActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(ReservePicnicActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(ReservePicnicActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(ReservePicnicActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }


}
