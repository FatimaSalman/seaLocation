package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.fragment.DatePickerFragment;
import com.apps.fatima.sealocation.fragment.TimePickerFragment;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class AddBoatTripActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView divingRadioButton, fishingRadioButton, picnicRadioButton;

    private TextView timing, select_location, nameTxt, dateTxt, seatNoEditText;

    private EditText directionEditText, durationEditText, conditionsEditText, priceEditText;
    private String type, token, boat_id, trip_id;
    private int year, month, day;
    static final int DATE_DIALOG_ID = 100;
    private ProgressDialog progressDialog;
    private Handler handler;
    private double latitude, longitude;
    private List<SpinnerItem> boatList = new ArrayList<>();
    private Dialog alertDialog;
    private Button approveBtn;
    private Calendar c;
    private SpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_boat_trip_form);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");
        handler = new Handler(Looper.getMainLooper());
        trip_id = getIntent().getStringExtra("trip_id");
        init();
        if (trip_id != null) {
            Log.e("trip_id", trip_id);
            getBoatTripDetails(token);
            approveBtn.setText(getString(R.string.update_data));
        }
        getBoatList(token);
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout divingLayout = findViewById(R.id.divingLayout);
        RelativeLayout fishingRadioButtonLayout = findViewById(R.id.fishingRadioButtonLayout);
        RelativeLayout picnicRadioButtonLayout = findViewById(R.id.picnicRadioButtonLayout);
        RelativeLayout nameLayout = findViewById(R.id.nameLayout);
        FontManager.applyFont(this, layout);

        ImageView ic_back = findViewById(R.id.ic_back);


        approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        divingRadioButton = findViewById(R.id.divingRadioButton);
        fishingRadioButton = findViewById(R.id.fishingRadioButton);
        picnicRadioButton = findViewById(R.id.picnicRadioButton);
        timing = findViewById(R.id.timing);
        dateTxt = findViewById(R.id.dateTxt);
        select_location = findViewById(R.id.select_location);
        nameTxt = findViewById(R.id.nameTxt);

        backLayout.setOnClickListener(this);
        nameLayout.setOnClickListener(this);
        timing.setOnClickListener(this);
        dateTxt.setOnClickListener(this);
        picnicRadioButtonLayout.setOnClickListener(this);
        fishingRadioButtonLayout.setOnClickListener(this);
        divingLayout.setOnClickListener(this);
        select_location.setOnClickListener(this);

        approveBtn.setOnClickListener(this);

        directionEditText = findViewById(R.id.directionEditText);
        durationEditText = findViewById(R.id.durationEditText);
        conditionsEditText = findViewById(R.id.conditionsEditText);
        seatNoEditText = findViewById(R.id.seatNoEditText);
        priceEditText = findViewById(R.id.priceEditText);

        RelativeLayout minusLayout = findViewById(R.id.minusLayout);
        RelativeLayout plusLayout = findViewById(R.id.plusLayout);
        minusLayout.setOnClickListener(this);
        plusLayout.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        int totalCount = Integer.parseInt(seatNoEditText.getText().toString());
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            if (approveBtn.getText().toString().equals(getString(R.string.approve))) {
                addTripBoat();
            } else {
                updateBoat();
            }
        } else if (id == R.id.nameLayout) {
            openWindowName();
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                seatNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            seatNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.select_location) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, 10);
        } else if (id == R.id.divingLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(divingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "2";
                } else if (Objects.equals(divingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "-1";
                }
            } else {
                if (Objects.requireNonNull(divingRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "2";
                } else if (divingRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "-1";
                }
            }
        } else if (id == R.id.fishingRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(fishingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(fishingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "1";
                } else if (Objects.equals(fishingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(fishingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "-1";
                }
            } else {
                if (Objects.requireNonNull(fishingRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "1";
                } else if (fishingRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "-1";
                }
            }
        } else if (id == R.id.picnicRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(picnicRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(picnicRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "0";
                } else if (Objects.equals(picnicRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(picnicRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "-1";
                }
            } else {
                if (Objects.requireNonNull(picnicRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "0";
                } else if (picnicRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    type = "-1";
                }
            }
        } else if (id == R.id.timing) {
            DialogFragment newFragment = new TimePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("time", "time1");
            bundle.putString("first", "date");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "timePicker");
        } else if (id == R.id.dateTxt) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "date");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "Date Picker");
        }
    }

    public void addTripBoat() {

        final String timing_txt = timing.getText().toString().trim();
        final String date_txt = dateTxt.getText().toString().trim();
        final String location = select_location.getText().toString().trim();
        final String tripDirection = directionEditText.getText().toString().trim();
        final String tripDuration = durationEditText.getText().toString().trim();
        final String tripCondition = conditionsEditText.getText().toString().trim();
        final String seatNumber = seatNoEditText.getText().toString().trim();
        final String price = priceEditText.getText().toString().trim();
        final String boatName = nameTxt.getText().toString().trim();

        if (TextUtils.isEmpty(date_txt)) {
            dateTxt.setError(getString(R.string.error_field_required));
            dateTxt.requestFocus();
        } else if (TextUtils.isEmpty(timing_txt)) {
            timing.setError(getString(R.string.error_field_required));
            timing.requestFocus();
        } else if (TextUtils.isEmpty(location)) {
            select_location.setError(getString(R.string.error_field_required));
            select_location.requestFocus();
        } else if (TextUtils.isEmpty(tripDirection)) {
            directionEditText.setError(getString(R.string.error_field_required));
            directionEditText.requestFocus();
        } else if (TextUtils.isEmpty(tripDuration)) {
            durationEditText.setError(getString(R.string.error_field_required));
            durationEditText.requestFocus();
        } else if (TextUtils.isEmpty(tripCondition)) {
            conditionsEditText.setError(getString(R.string.error_field_required));
            conditionsEditText.requestFocus();
        } else if (TextUtils.isEmpty(seatNumber)) {
            seatNoEditText.setError(getString(R.string.error_field_required));
            seatNoEditText.requestFocus();
        } else if (seatNumber.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_seat_no));
        } else if (TextUtils.isEmpty(price)) {
            priceEditText.setError(getString(R.string.error_field_required));
            priceEditText.requestFocus();
        } else if (TextUtils.isEmpty(boatName)) {
            nameTxt.setError(getString(R.string.error_field_required));
            nameTxt.requestFocus();
        } else if (type == null) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("start_time", timing_txt)
                            .addFormDataPart("start_date", date_txt)
                            .addFormDataPart("trip_terms", tripCondition)
                            .addFormDataPart("trip_route", tripDirection)
                            .addFormDataPart("trip_duration", tripDuration)
                            .addFormDataPart("trip_price", price)
                            .addFormDataPart("start_location", location)
                            .addFormDataPart("boat_name", boatName)
                            .addFormDataPart("trip_type", type)
                            .addFormDataPart("available_seats", seatNumber)
                            .addFormDataPart("boat_id", boat_id);

                    RequestBody requestBody = builder.build();
                    addTripBoatInfo(requestBody);
                }
            }).start();
        }
    }

    public void addTripBoatInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddBoatTripActivity.this);
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
                            + "boat-trip").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            if (jsonObject.has("success")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        Intent intent = new Intent();
                                        setResult(13, intent);
                                        finish();
                                    }
                                });
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getBoatList(final String token) {
        boatList.clear();
        InternetConnectionUtils.isInternetAvailable(AddBoatTripActivity.this, new InternetAvailableCallback() {
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
                            + "user-boats").get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatList", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONArray successObject = new JSONArray(success);
                            for (int i = 0; i < successObject.length(); i++) {
                                JSONObject boatObject = successObject.getJSONObject(i);
                                String id = boatObject.getString("id");
                                String boatName = boatObject.getString("name");
                                SpinnerItem boat = new SpinnerItem(id, boatName, boatName);
                                boatList.add(boat);
                                Log.e("boatList", boatList.size() + "");
                            }

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getBoatTripDetails(final String token) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddBoatTripActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(AddBoatTripActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "boat-trip/" + trip_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String partner_boat = successObject.getString("boats");
                            JSONObject boatObject = new JSONObject(partner_boat);
//                            String id = boatObject.getString("id");
                            boat_id = boatObject.getString("boat_id");
                            final String start_date = boatObject.getString("start_date");
                            final String start_time = boatObject.getString("start_time");
                            final String trip_route = boatObject.getString("trip_route");
                            final String trip_duration = boatObject.getString("trip_duration");
                            final String trip_terms = boatObject.getString("trip_terms");
                            final String trip_price = boatObject.getString("trip_price");
                            final String start_location = boatObject.getString("start_location");
                            final String[] namesList = start_location.split(",");
                            String name1 = namesList[0];
                            String name2 = namesList[1];
                            latitude = Double.parseDouble(name1);
                            longitude = Double.parseDouble(name2);
                            final String available_seats = boatObject.getString("available_seats");
                            final String boat_name = boatObject.getString("boat_name");
                            type = boatObject.getString("trip_type");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();

                                    directionEditText.setText(trip_route);
                                    durationEditText.setText(trip_duration);
                                    conditionsEditText.setText(trip_terms);
                                    seatNoEditText.setText(available_seats);
                                    priceEditText.setText(trip_price);
                                    select_location.setText(start_location);
                                    nameTxt.setText(boat_name);
                                    dateTxt.setText(start_date);
                                    timing.setText(start_time);

                                    if (TextUtils.equals(type, "2")) {
                                        divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    } else if (TextUtils.equals(type, "1")) {
                                        fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    } else if (TextUtils.equals(type, "0")) {
                                        picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    }
                                }
                            });

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void updateBoat() {

        final String timing_txt = timing.getText().toString().trim();
        final String date_txt = dateTxt.getText().toString().trim();
        final String location = select_location.getText().toString().trim();
        final String tripDirection = directionEditText.getText().toString().trim();
        final String tripDuration = durationEditText.getText().toString().trim();
        final String tripCondition = conditionsEditText.getText().toString().trim();
        final String seatNumber = seatNoEditText.getText().toString().trim();
        final String price = priceEditText.getText().toString().trim();
        final String boatName = nameTxt.getText().toString().trim();

        if (TextUtils.isEmpty(date_txt)) {
            dateTxt.setError(getString(R.string.error_field_required));
            dateTxt.requestFocus();
        } else if (TextUtils.isEmpty(timing_txt)) {
            timing.setError(getString(R.string.error_field_required));
            timing.requestFocus();
        } else if (TextUtils.isEmpty(location)) {
            select_location.setError(getString(R.string.error_field_required));
            select_location.requestFocus();
        } else if (TextUtils.isEmpty(tripDirection)) {
            directionEditText.setError(getString(R.string.error_field_required));
            directionEditText.requestFocus();
        } else if (TextUtils.isEmpty(tripDuration)) {
            durationEditText.setError(getString(R.string.error_field_required));
            durationEditText.requestFocus();
        } else if (TextUtils.isEmpty(tripCondition)) {
            conditionsEditText.setError(getString(R.string.error_field_required));
            conditionsEditText.requestFocus();
        } else if (TextUtils.isEmpty(seatNumber)) {
            seatNoEditText.setError(getString(R.string.error_field_required));
            seatNoEditText.requestFocus();
        } else if (seatNumber.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_seat_no));
        } else if (TextUtils.isEmpty(price)) {
            priceEditText.setError(getString(R.string.error_field_required));
            priceEditText.requestFocus();
        } else if (TextUtils.isEmpty(boatName)) {
            nameTxt.setError(getString(R.string.error_field_required));
            nameTxt.requestFocus();
        } else if (type == null) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("start_time", timing_txt)
                            .addFormDataPart("start_date", date_txt)
                            .addFormDataPart("trip_terms", tripCondition)
                            .addFormDataPart("trip_route", tripDirection)
                            .addFormDataPart("trip_duration", tripDuration)
                            .addFormDataPart("trip_price", price)
                            .addFormDataPart("start_location", location)
                            .addFormDataPart("boat_name", boatName)
                            .addFormDataPart("trip_type", type)
                            .addFormDataPart("available_seats", seatNumber)
                            .addFormDataPart("id", trip_id)
                            .addFormDataPart("boat_id", boat_id);

                    RequestBody requestBody = builder.build();
                    updateInfo(requestBody);
                }
            }).start();
        }
    }

    public void updateInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddBoatTripActivity.this);
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
                            + "boat-trip").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Accept", "application/json")
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("update", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            if (jsonObject.has("success")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        progressDialog.hide();
                                        AppErrorsManager.showSuccessDialog(AddBoatTripActivity.this, getString(R.string.update_successfully), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent();
                                                setResult(13, intent);
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
                                    AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddBoatTripActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowName() {
        @SuppressLint("InflateParams")
        View popupView = LayoutInflater.from(AddBoatTripActivity.this).inflate(R.layout.toolbar_spinner, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddBoatTripActivity.this);
        dialog.setView(popupView);
        EditText searchEditText = popupView.findViewById(R.id.searchEditText);
        final RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view1);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String query = charSequence.toString().toLowerCase().trim();
                final List<SpinnerItem> filteredList = new ArrayList<>();

                for (int j = 0; j < boatList.size(); j++) {

                    final String text = boatList.get(j).getText().toLowerCase();
                    final String textAr = boatList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(AddBoatTripActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(boatList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(boatList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(AddBoatTripActivity.this));
                spinnerAdapter = new SpinnerAdapter(AddBoatTripActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(AddBoatTripActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        boat_id = filteredList.get(position).getId();
                        nameTxt.setText(status);
                        nameTxt.setError(null);
                        alertDialog.dismiss();
                    }
                });
                recyclerView.setAdapter(spinnerAdapter);
                spinnerAdapter.notifyDataSetChanged();  // data set changed
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
//                filter(editable.toString());
            }
        });
        spinnerAdapter = new SpinnerAdapter(AddBoatTripActivity.this, boatList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(AddBoatTripActivity.this).equals("ar")) {
                    status = boatList.get(position).getTextA();
                } else {
                    status = boatList.get(position).getText();
                }
                boat_id = boatList.get(position).getId();
                nameTxt.setText(status);
                nameTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddBoatTripActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                return datePickerDialog;

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            dateTxt.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2 + 1).append("/").append(arg1));
            dateTxt.setError(null);
            day = arg3;
            month = arg2 + 1;
            year = arg1;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data != null) {
            select_location.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location.setError(null);
        }
    }

}
