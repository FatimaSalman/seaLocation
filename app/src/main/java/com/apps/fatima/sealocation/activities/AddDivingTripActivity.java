package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.fragment.DatePickerFragment;
import com.apps.fatima.sealocation.fragment.TimePickerFragment;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;

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

public class AddDivingTripActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView boatRadioButton, beachRadioButton, yesRadioButton, noRadioButton;

    private TextView timing, select_location, dateTxt, seatNoEditText;// nameTxt,

    private EditText directionEditText, durationEditText, conditionsEditText,
            priceEditText, priceEquipmentEditText, tripTitleEditText;
    private String tripType = "-1", token, trip_id, boat_id, available = "-1";
    private int year, month, day;
    static final int DATE_DIALOG_ID = 100;
    private ProgressDialog progressDialog;
    private Handler handler;
    private double latitude, longitude;
    private List<SpinnerItem> boatList = new ArrayList<>();
    private Button approveBtn;
    private RelativeLayout priceLayout;//boatTripLayout,
    private int valid = 1;
    //    private String boatName;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diving_trip_form);
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
        getBoatList();
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout divingLayout = findViewById(R.id.divingLayout);
        RelativeLayout beachRadioButtonLayout = findViewById(R.id.beachRadioButtonLayout);
        RelativeLayout yesLayout = findViewById(R.id.yesLayout);
        RelativeLayout noLayout = findViewById(R.id.noLayout);
        FontManager.applyFont(this, layout);

        ImageView ic_back = findViewById(R.id.ic_back);


        approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        boatRadioButton = findViewById(R.id.boatRadioButton);
        priceLayout = findViewById(R.id.priceLayout);
        beachRadioButton = findViewById(R.id.beachRadioButton);
        yesRadioButton = findViewById(R.id.yesRadioButton);
        noRadioButton = findViewById(R.id.noRadioButton);
        timing = findViewById(R.id.timing);
        dateTxt = findViewById(R.id.dateTxt);
        select_location = findViewById(R.id.select_location);
//        nameTxt = findViewById(R.id.nameTxt);
//        RelativeLayout nameLayout = findViewById(R.id.nameLayout);
//        boatTripLayout = findViewById(R.id.boatTripLayout);

        backLayout.setOnClickListener(this);
//        nameLayout.setOnClickListener(this);
        timing.setOnClickListener(this);
        dateTxt.setOnClickListener(this);
        divingLayout.setOnClickListener(this);
        beachRadioButtonLayout.setOnClickListener(this);
        yesLayout.setOnClickListener(this);
        noLayout.setOnClickListener(this);
        select_location.setOnClickListener(this);

        approveBtn.setOnClickListener(this);

        directionEditText = findViewById(R.id.directionEditText);
        durationEditText = findViewById(R.id.durationEditText);
        conditionsEditText = findViewById(R.id.conditionsEditText);
        seatNoEditText = findViewById(R.id.seatNoEditText);
        priceEditText = findViewById(R.id.priceEditText);
        priceEquipmentEditText = findViewById(R.id.priceEquipmentEditText);
        tripTitleEditText = findViewById(R.id.tripTitleEditText);

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
        }
//        else if (id == R.id.nameLayout) {
//            openWindowName();
//        }
        else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                seatNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            seatNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.approveBtn) {
            if (approveBtn.getText().toString().equals(getString(R.string.approve))) {
                addTripBoat();
            } else {
                updateBoat();
            }
        } else if (id == R.id.select_location) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, 10);
        } else if (id == R.id.yesLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(yesRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(yesRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_check_circle);
                    noRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.VISIBLE);
                    available = "1";
                } else if (Objects.equals(yesRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(yesRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.GONE);
                    available = "-1";
                }
            } else {
                if (Objects.requireNonNull(yesRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_check_circle);
                    noRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.VISIBLE);
                    available = "1";
                } else if (yesRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.GONE);
                    available = "-1";
                }
            }
        } else if (id == R.id.noLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(noRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(noRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    noRadioButton.setImageResource(R.drawable.ic_check_circle);
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.GONE);
                    available = "0";
                } else if (Objects.equals(noRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(noRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    noRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.GONE);
                    available = "-1";
                }
            } else {
                if (Objects.requireNonNull(noRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    noRadioButton.setImageResource(R.drawable.ic_check_circle);
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.GONE);
                    available = "0";
                } else if (Objects.requireNonNull(yesRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.GONE);
                    available = "-1";
                }
            }
        } else if (id == R.id.divingLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(boatRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(boatRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    boatRadioButton.setImageResource(R.drawable.ic_check_circle);
                    beachRadioButton.setImageResource(R.drawable.ic_circle);
//                boatTripLayout.setVisibility(View.VISIBLE);
                    directionEditText.setVisibility(View.VISIBLE);
                    tripType = "2";
                } else if (Objects.equals(boatRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(boatRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    boatRadioButton.setImageResource(R.drawable.ic_circle);
//                boatTripLayout.setVisibility(View.GONE);
                    directionEditText.setVisibility(View.GONE);
                    tripType = "-1";
                }
            } else {
                if (Objects.requireNonNull(boatRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    boatRadioButton.setImageResource(R.drawable.ic_check_circle);
                    beachRadioButton.setImageResource(R.drawable.ic_circle);
//                boatTripLayout.setVisibility(View.VISIBLE);
                    directionEditText.setVisibility(View.VISIBLE);
                    tripType = "2";
                } else if (Objects.requireNonNull(boatRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    boatRadioButton.setImageResource(R.drawable.ic_circle);
//                boatTripLayout.setVisibility(View.GONE);
                    directionEditText.setVisibility(View.GONE);
                    tripType = "-1";
                }
            }
        } else if (id == R.id.beachRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(beachRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(beachRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    beachRadioButton.setImageResource(R.drawable.ic_check_circle);
                    boatRadioButton.setImageResource(R.drawable.ic_circle);
                    tripType = "3";
//                boatTripLayout.setVisibility(View.GONE);
                    directionEditText.setVisibility(View.GONE);
//                boatName = "";
                } else if (Objects.equals(beachRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(beachRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    beachRadioButton.setImageResource(R.drawable.ic_circle);
//                boatTripLayout.setVisibility(View.GONE);
                    directionEditText.setVisibility(View.GONE);
                    tripType = "-1";
                }
            } else {
                if (Objects.requireNonNull(beachRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    beachRadioButton.setImageResource(R.drawable.ic_check_circle);
                    boatRadioButton.setImageResource(R.drawable.ic_circle);
                    tripType = "3";
//                boatTripLayout.setVisibility(View.GONE);
                    directionEditText.setVisibility(View.GONE);
//                boatName = "";
                } else if (Objects.requireNonNull(beachRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    beachRadioButton.setImageResource(R.drawable.ic_circle);
//                boatTripLayout.setVisibility(View.GONE);
                    directionEditText.setVisibility(View.GONE);
                    tripType = "-1";
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
//        boatName = nameTxt.getText().toString().trim();
        final String eqPirce = priceEquipmentEditText.getText().toString().trim();
        final String title = tripTitleEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            tripTitleEditText.setError(getString(R.string.error_field_required));
            tripTitleEditText.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(timing_txt)) {
            timing.setError(getString(R.string.error_field_required));
            timing.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(date_txt)) {
            dateTxt.setError(getString(R.string.error_field_required));
            dateTxt.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(location)) {
            select_location.setError(getString(R.string.error_field_required));
            select_location.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(tripDuration)) {
            durationEditText.setError(getString(R.string.error_field_required));
            durationEditText.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(tripCondition)) {
            conditionsEditText.setError(getString(R.string.error_field_required));
            conditionsEditText.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(seatNumber)) {
            seatNoEditText.setError(getString(R.string.error_field_required));
            seatNoEditText.requestFocus();
            valid = 1;
        } else if (seatNumber.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_seat_no));
            valid = 1;
        } else if (TextUtils.isEmpty(price)) {
            priceEditText.setError(getString(R.string.error_field_required));
            priceEditText.requestFocus();
            valid = 1;
        } else if (tripType.equals("-1")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
            valid = 1;
        } else if (tripType.equals("2")) {
            if (TextUtils.isEmpty(tripDirection)) {
                directionEditText.setError(getString(R.string.error_field_required));
                directionEditText.requestFocus();
                valid = 1;
            }
//            else valid = 0;
//            else if (TextUtils.isEmpty(boatName)) {
//                nameTxt.setError(getString(R.string.error_field_required));
//                nameTxt.requestFocus();
//                valid = 1;
//            }
            else if (available.equals("-1")) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_diving_equi));
                valid = 1;
            } else if (available.equals("1")) {
                if (TextUtils.isEmpty(eqPirce)) {
                    priceEquipmentEditText.setError(getString(R.string.error_field_required));
                    priceEquipmentEditText.requestFocus();
                    valid = 1;
                } else {
                    valid = 0;
                }
            } else valid = 0;
        } else if (available.equals("-1")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_diving_equi));
            valid = 1;
        } else if (available.equals("1")) {
            if (TextUtils.isEmpty(eqPirce)) {
                priceEquipmentEditText.setError(getString(R.string.error_field_required));
                priceEquipmentEditText.requestFocus();
                valid = 1;
            } else {
                valid = 0;
            }
        } else {
            valid = 0;
        }
        if (valid == 0) {
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
//                            .addFormDataPart("boat_name", boatName)
                            .addFormDataPart("trip_type", String.valueOf(tripType))
                            .addFormDataPart("available_seats", seatNumber)
//                            .addFormDataPart("boat_id", boat_id)
                            .addFormDataPart("gears_price", eqPirce)
                            .addFormDataPart("gears_available", String.valueOf(available))
                            .addFormDataPart("for_diver", "1")
                            .addFormDataPart("title", title);

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
                progressDialog = new ProgressDialog(AddDivingTripActivity.this);
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
                                    AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getBoatList() {
        boatList.clear();
        InternetConnectionUtils.isInternetAvailable(AddDivingTripActivity.this, new InternetAvailableCallback() {
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
                            + "boat").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatListName", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String boats = successObject.getString("boats");
                            JSONArray boatsObject = new JSONArray(boats);
                            for (int i = 0; i < boatsObject.length(); i++) {
                                JSONObject boatObject = boatsObject.getJSONObject(i);
                                boat_id = boatObject.getString("id");
                                String boatName = boatObject.getString("name");
                                SpinnerItem boat = new SpinnerItem(boat_id, boatName, boatName);
                                boatList.add(boat);
                                Log.e("boatList", boatList.size() + "");
                            }

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
//                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, getString(R.string.error_network));
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
                progressDialog = new ProgressDialog(AddDivingTripActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(AddDivingTripActivity.this, new InternetAvailableCallback() {
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
//                            final String boat_name = boatObject.getString("boat_name");
                            tripType = boatObject.getString("trip_type");
                            available = boatObject.getString("gears_available");
                            final String gears_price = boatObject.getString("gears_price");
                            final String title = boatObject.getString("title");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();

                                    durationEditText.setText(trip_duration);
                                    conditionsEditText.setText(trip_terms);
                                    seatNoEditText.setText(available_seats);
                                    priceEditText.setText(trip_price);
                                    select_location.setText(start_location);
//                                    if (!TextUtils.equals(boat_name, "null"))
//                                        nameTxt.setText(boat_name);
                                    timing.setText(start_time);
                                    dateTxt.setText(start_date);
                                    tripTitleEditText.setText(title);

                                    if (TextUtils.equals(tripType, "2")) {
                                        directionEditText.setVisibility(View.VISIBLE);
//                                        boatTripLayout.setVisibility(View.VISIBLE);
                                        boatRadioButton.setImageResource(R.drawable.ic_check_circle);
                                        directionEditText.setText(trip_route);
                                    } else if (TextUtils.equals(tripType, "1")) {
//                                        boatTripLayout.setVisibility(View.VISIBLE);
                                        directionEditText.setVisibility(View.VISIBLE);
                                        boatRadioButton.setImageResource(R.drawable.ic_check_circle);
                                        directionEditText.setText(trip_route);
                                    } else if (TextUtils.equals(tripType, "0")) {
//                                        boatTripLayout.setVisibility(View.VISIBLE);
                                        directionEditText.setVisibility(View.VISIBLE);
                                        boatRadioButton.setImageResource(R.drawable.ic_check_circle);
                                        directionEditText.setText(trip_route);
                                    } else if (TextUtils.equals(tripType, "3")) {
//                                        boatTripLayout.setVisibility(View.GONE);
                                        directionEditText.setVisibility(View.GONE);
                                        beachRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    }

                                    if (TextUtils.equals(available, "1")) {
                                        yesRadioButton.setImageResource(R.drawable.ic_check_circle);
                                        priceLayout.setVisibility(View.VISIBLE);
                                        priceEquipmentEditText.setText(gears_price);
                                    } else {
                                        noRadioButton.setImageResource(R.drawable.ic_check_circle);
                                        priceLayout.setVisibility(View.GONE);
                                    }
                                }
                            });

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, getString(R.string.error_network));
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
//        final String boatName = nameTxt.getText().toString().trim();
        final String eqPirce = priceEquipmentEditText.getText().toString().trim();
        final String title = tripTitleEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            tripTitleEditText.setError(getString(R.string.error_field_required));
            tripTitleEditText.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(timing_txt)) {
            timing.setError(getString(R.string.error_field_required));
            timing.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(date_txt)) {
            dateTxt.setError(getString(R.string.error_field_required));
            dateTxt.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(location)) {
            select_location.setError(getString(R.string.error_field_required));
            select_location.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(tripDirection)) {
            directionEditText.setError(getString(R.string.error_field_required));
            directionEditText.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(tripDuration)) {
            durationEditText.setError(getString(R.string.error_field_required));
            durationEditText.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(tripCondition)) {
            conditionsEditText.setError(getString(R.string.error_field_required));
            conditionsEditText.requestFocus();
            valid = 1;
        } else if (TextUtils.isEmpty(seatNumber)) {
            seatNoEditText.setError(getString(R.string.error_field_required));
            seatNoEditText.requestFocus();
            valid = 1;
        } else if (seatNumber.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_seat_no));
            valid = 1;
        } else if (TextUtils.isEmpty(price)) {
            priceEditText.setError(getString(R.string.error_field_required));
            priceEditText.requestFocus();
            valid = 1;
        } else if (tripType.equals("-1")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
            valid = 1;
        } else if (tripType.equals("2")) {
//            if (TextUtils.isEmpty(boatName)) {
//                nameTxt.setError(getString(R.string.error_field_required));
//                nameTxt.requestFocus();
//                valid = 1;
//            } else
            if (tripType.equals("-1")) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
                valid = 1;
            } else {
                valid = 0;
            }
        } else if (available.equals("-1")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_diving_equi));
            valid = 1;
        } else if (available.equals("1")) {
            if (TextUtils.isEmpty(eqPirce)) {
                priceEquipmentEditText.setError(getString(R.string.error_field_required));
                priceEquipmentEditText.requestFocus();
                valid = 1;
            } else {
                valid = 0;
            }
        } else {
            valid = 0;
        }
        if (valid == 0) {
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
//                            .addFormDataPart("boat_name", boatName)
                            .addFormDataPart("trip_type", tripType)
                            .addFormDataPart("available_seats", seatNumber)
//                            .addFormDataPart("boat_id", boat_id)
                            .addFormDataPart("gears_price", eqPirce)
                            .addFormDataPart("gears_available", available)
                            .addFormDataPart("for_diver", "1")
                            .addFormDataPart("title", title)
                            .addFormDataPart("id", trip_id);

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
                progressDialog = new ProgressDialog(AddDivingTripActivity.this);
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
                                        AppErrorsManager.showSuccessDialog(AddDivingTripActivity.this, getString(R.string.update_successfully), new DialogInterface.OnClickListener() {
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
                                    AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddDivingTripActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
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
