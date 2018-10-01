package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.fragment.DatePickerFragment;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.SpinnerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class AddBoatActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView divingRadioButton, fishingRadioButton, picnicRadioButton;
    private TextView select_location, areaWidthTxt, areaHieghtTxt, passengersNoEditText, expireLicenceTxt;
    private EditText boatNoEditText, boatNameEditText, heightEditText,
            widthEditText, rentValueEditText;
    private int diving = 0, fishing = 0, picnic = 0;
    private ProgressDialog progressDialog;
    private Handler handler;
    private String token, boat_id, width_unit_id, height_unit_id;
    private double latitude, longitude;
    private Button approveBtn;
    private List<SpinnerItem> spinnerItemWidthUnitsList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemHeightUnitsList = new ArrayList<>();
    private Dialog alertDialog;
    private SpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_boat_form);
        token = AppPreferences.getString(this, "token");
        handler = new Handler(Looper.getMainLooper());
        boat_id = getIntent().getStringExtra("boat_id");
        init();
        if (boat_id != null) {
            getBoatDetails(token);
            approveBtn.setText(getString(R.string.update_data));
        }
        measureHightUnitList();
        measureWidthUnitList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout divingLayout = findViewById(R.id.divingLayout);
        RelativeLayout fishingRadioButtonLayout = findViewById(R.id.fishingRadioButtonLayout);
        RelativeLayout picnicRadioButtonLayout = findViewById(R.id.picnicRadioButtonLayout);
        FontManager.applyFont(this, layout);
        RelativeLayout expireDateLicenceLayout = findViewById(R.id.expireDateLicenceLayout);
        expireDateLicenceLayout.setOnClickListener(this);
        ImageView ic_back = findViewById(R.id.ic_back);

        approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        select_location = findViewById(R.id.select_location);
        areaWidthTxt = findViewById(R.id.areaWidthTxt);
        areaHieghtTxt = findViewById(R.id.areaHieghtTxt);

        divingRadioButton = findViewById(R.id.divingRadioButton);
        fishingRadioButton = findViewById(R.id.fishingRadioButton);
        picnicRadioButton = findViewById(R.id.picnicRadioButton);

        backLayout.setOnClickListener(this);
        picnicRadioButtonLayout.setOnClickListener(this);
        fishingRadioButtonLayout.setOnClickListener(this);
        divingLayout.setOnClickListener(this);
        select_location.setOnClickListener(this);
        areaWidthTxt.setOnClickListener(this);
        areaHieghtTxt.setOnClickListener(this);
        approveBtn.setOnClickListener(this);

        boatNoEditText = findViewById(R.id.boatNoEditText);
        expireLicenceTxt = findViewById(R.id.expireLicenceTxt);
        boatNameEditText = findViewById(R.id.boatNameEditText);
        passengersNoEditText = findViewById(R.id.passengersNoEditText);
        heightEditText = findViewById(R.id.heightEditText);
        widthEditText = findViewById(R.id.widthEditText);
        rentValueEditText = findViewById(R.id.rentValueEditText);
        RelativeLayout minusLayout = findViewById(R.id.minusLayout);
        RelativeLayout plusLayout = findViewById(R.id.plusLayout);
        minusLayout.setOnClickListener(this);
        plusLayout.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int totalCount = Integer.parseInt(passengersNoEditText.getText().toString());
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.expireDateLicenceLayout) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "second");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "Date Picker");
        } else if (id == R.id.approveBtn) {
            if (approveBtn.getText().toString().equals(getString(R.string.approve))) {
                addBoat();
            } else {
                updateBoat();
            }
        } else if (id == R.id.areaHieghtTxt) {
            openWindowHeightUnits();
        } else if (id == R.id.areaWidthTxt) {
            openWindowWidthUnits();
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                passengersNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            passengersNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.divingLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(divingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving = 1;
                } else if (Objects.equals(divingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    diving = 0;
                }
            } else {
                if (Objects.requireNonNull(divingRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving = 1;
                } else if (divingRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    diving = 0;
                }
            }
        } else if (id == R.id.fishingRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(fishingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(fishingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    fishing = 1;
                } else if (Objects.equals(fishingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(fishingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    fishing = 0;
                }
            } else {
                if (Objects.requireNonNull(fishingRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    fishing = 1;
                } else if (fishingRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    fishing = 0;
                }
            }
        } else if (id == R.id.picnicRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(picnicRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(picnicRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                    picnic = 1;
                } else if (Objects.equals(picnicRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(picnicRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    picnic = 0;
                }
            } else {
                if (Objects.requireNonNull(picnicRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                    picnic = 1;
                } else if (picnicRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    picnic = 0;
                }
            }
        } else if (id == R.id.select_location) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, 10);
        }
    }

    public void addBoat() {

        final String boatNumber = boatNoEditText.getText().toString().trim();
        final String boatName = boatNameEditText.getText().toString().trim();
        final String passengersNumber = passengersNoEditText.getText().toString().trim();
        final String width = widthEditText.getText().toString().trim();
        final String height = heightEditText.getText().toString().trim();
        final String heightTxt = areaHieghtTxt.getText().toString().trim();
        final String widthTxt = areaWidthTxt.getText().toString().trim();
        final String rentValue = rentValueEditText.getText().toString().trim();
        final String expireLicence = expireLicenceTxt.getText().toString().trim();
        final String location = select_location.getText().toString().trim();

        if (TextUtils.isEmpty(boatNumber)) {
            boatNoEditText.setError(getString(R.string.error_field_required));
            boatNoEditText.requestFocus();
        } else if (TextUtils.isEmpty(boatName)) {
            boatNameEditText.setError(getString(R.string.error_field_required));
            boatNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(passengersNumber)) {
            passengersNoEditText.setError(getString(R.string.error_field_required));
            passengersNoEditText.requestFocus();
        } else if (passengersNumber.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_quantity));
        } else if (TextUtils.isEmpty(height)) {
            heightEditText.setError(getString(R.string.error_field_required));
            heightEditText.requestFocus();
        } else if (TextUtils.isEmpty(heightTxt)) {
            areaHieghtTxt.setError(getString(R.string.error_field_required));
            areaHieghtTxt.requestFocus();
        } else if (TextUtils.isEmpty(width)) {
            widthEditText.setError(getString(R.string.error_field_required));
            widthEditText.requestFocus();
        } else if (TextUtils.isEmpty(widthTxt)) {
            areaWidthTxt.setError(getString(R.string.error_field_required));
            areaWidthTxt.requestFocus();
        } else if (TextUtils.isEmpty(location)) {
            select_location.setError(getString(R.string.error_field_required));
            select_location.requestFocus();
        } else if (TextUtils.isEmpty(rentValue)) {
            rentValueEditText.setError(getString(R.string.error_field_required));
            rentValueEditText.requestFocus();
        } else if (TextUtils.isEmpty(expireLicence)) {
            expireLicenceTxt.setError(getString(R.string.error_field_required));
            expireLicenceTxt.requestFocus();
        } else if (diving == 0 && fishing == 0 && picnic == 0) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
        } else {
            Log.e("yyyy", "uuuuu");
            Log.e("height_unit_id", height_unit_id);
            Log.e("width_unit_id", width_unit_id);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("boat_no", boatNumber)
                            .addFormDataPart("boat_name", boatName)
                            .addFormDataPart("boat_passengers", passengersNumber)
                            .addFormDataPart("boat_length", height)
                            .addFormDataPart("boat_width", width)
                            .addFormDataPart("lenght_id", height_unit_id)
                            .addFormDataPart("width_id", width_unit_id)
                            .addFormDataPart("boat_location", location)
                            .addFormDataPart("boat_hourly", rentValue)
                            .addFormDataPart("boat_trip_fishing", String.valueOf(fishing))
                            .addFormDataPart("boat_trip_diving", String.valueOf(diving))
                            .addFormDataPart("boat_trip_tour", String.valueOf(picnic))
                            .addFormDataPart("boat_licence_expire_date", "")
                            .addFormDataPart("boat_driving_licence_expire_date", expireLicence);

                    RequestBody requestBody = builder.build();
                    addBoatInfo(requestBody);
                }
            }).start();
        }
    }

    public void addBoatInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddBoatActivity.this);
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
                            + "boat").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
//                            String error = jsonObject.optString("error");
                            if (jsonObject.has("error")) {
//                                final JSONObject jsonError = new JSONObject(error);
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
                                        Intent intent = new Intent();
                                        setResult(12, intent);
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
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddBoatActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void updateBoat() {
        final String boatNumber = boatNoEditText.getText().toString().trim();
        final String boatName = boatNameEditText.getText().toString().trim();
        final String passengersNumber = passengersNoEditText.getText().toString().trim();
        final String width = widthEditText.getText().toString().trim();
        final String height = heightEditText.getText().toString().trim();
        final String rentValue = rentValueEditText.getText().toString().trim();
        final String expireLicence = expireLicenceTxt.getText().toString().trim();
        final String location = select_location.getText().toString().trim();
        final String heightTxt = areaHieghtTxt.getText().toString().trim();
        final String widthTxt = areaWidthTxt.getText().toString().trim();

        if (TextUtils.isEmpty(boatNumber)) {
            boatNoEditText.setError(getString(R.string.error_field_required));
            boatNoEditText.requestFocus();
        } else if (TextUtils.isEmpty(boatName)) {
            boatNameEditText.setError(getString(R.string.error_field_required));
            boatNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(passengersNumber)) {
            passengersNoEditText.setError(getString(R.string.error_field_required));
            passengersNoEditText.requestFocus();
        } else if (passengersNumber.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_quantity));
        } else if (TextUtils.isEmpty(height)) {
            heightEditText.setError(getString(R.string.error_field_required));
            heightEditText.requestFocus();
        } else if (TextUtils.isEmpty(heightTxt)) {
            areaHieghtTxt.setError(getString(R.string.error_field_required));
            areaHieghtTxt.requestFocus();
        } else if (TextUtils.isEmpty(width)) {
            widthEditText.setError(getString(R.string.error_field_required));
            widthEditText.requestFocus();
        } else if (TextUtils.isEmpty(widthTxt)) {
            areaWidthTxt.setError(getString(R.string.error_field_required));
            areaWidthTxt.requestFocus();
        } else if (TextUtils.isEmpty(location)) {
            select_location.setError(getString(R.string.error_field_required));
            select_location.requestFocus();
        } else if (TextUtils.isEmpty(rentValue)) {
            rentValueEditText.setError(getString(R.string.error_field_required));
            rentValueEditText.requestFocus();
        } else if (TextUtils.isEmpty(expireLicence)) {
            expireLicenceTxt.setError(getString(R.string.error_field_required));
            expireLicenceTxt.requestFocus();
        } else if (diving == 0 && fishing == 0 && picnic == 0) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("boat_no", boatNumber)
                            .addFormDataPart("boat_name", boatName)
                            .addFormDataPart("boat_passengers", passengersNumber)
                            .addFormDataPart("boat_length", height)
                            .addFormDataPart("lenght_id", height_unit_id)
                            .addFormDataPart("boat_width", width)
                            .addFormDataPart("width_id", width_unit_id)
                            .addFormDataPart("boat_location", location)
                            .addFormDataPart("boat_hourly", rentValue)
                            .addFormDataPart("boat_trip_fishing", String.valueOf(fishing))
                            .addFormDataPart("boat_trip_diving", String.valueOf(diving))
                            .addFormDataPart("boat_trip_tour", String.valueOf(picnic))
                            .addFormDataPart("boat_licence_expire_date", "")
                            .addFormDataPart("boat_driving_licence_expire_date", expireLicence)
                            .addFormDataPart("id", boat_id);

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
                progressDialog = new ProgressDialog(AddBoatActivity.this);
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
                            + "boat").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(AddBoatActivity.this, getString(R.string.update_successfully), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent();
                                                setResult(12, intent);
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
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddBoatActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getBoatDetails(final String token) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddBoatActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(AddBoatActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "boat/" + boat_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        assert response.body() != null;
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String partner_boat = successObject.getString("boats");
                            JSONObject boatObject = new JSONObject(partner_boat);
                            final String boatName = boatObject.getString("name");
                            final String boatNumber = boatObject.getString("number");
                            final String passengerNumber = boatObject.getString("passengers");
                            final String lenght = boatObject.getString("lenght");
                            final String width = boatObject.getString("width");
                            String lenght_id = boatObject.getString("lenght_unit");
                            JSONObject lenghtObject = new JSONObject(lenght_id);
                            height_unit_id = lenghtObject.getString("id");
                            final String unit_en = lenghtObject.getString("name_en");
                            final String unit_ar = lenghtObject.getString("name_ar");

                            String width_id = boatObject.getString("width_unit");
                            JSONObject widthObject = new JSONObject(width_id);
                            width_unit_id = widthObject.getString("id");
                            final String unit_w_en = widthObject.getString("name_en");
                            final String unit_w_ar = widthObject.getString("name_ar");

                            final String location = boatObject.getString("location");
                            final String[] namesList = location.split(",");
                            String name1 = namesList[0];
                            String name2 = namesList[1];
                            latitude = Double.parseDouble(name1);
                            longitude = Double.parseDouble(name2);
                            final String hourly_price = boatObject.getString("hourly_price");
                            final String driver_licence_end_date = boatObject.getString("driver_licence_end_date");
                            final String trip_type = boatObject.getString("trip_type");
                            JSONObject tripObject = new JSONObject(trip_type);
                            final String fishingString = tripObject.getString("fishing");
                            final String divingString = tripObject.getString("diving");
                            final String tourString = tripObject.getString("tour");
                            fishing = Integer.parseInt(fishingString);
                            diving = Integer.parseInt(divingString);
                            picnic = Integer.parseInt(tourString);


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    boatNoEditText.setText(boatNumber);
                                    boatNameEditText.setText(boatName);
                                    passengersNoEditText.setText(passengerNumber);
                                    heightEditText.setText(lenght);
                                    widthEditText.setText(width);
                                    rentValueEditText.setText(hourly_price);
                                    expireLicenceTxt.setText(driver_licence_end_date);
                                    select_location.setText(location);
                                    if (AppLanguage.getLanguage(AddBoatActivity.this).equals("ar")) {
                                        areaHieghtTxt.setText(unit_ar);
                                        areaWidthTxt.setText(unit_w_ar);
                                    } else {
                                        areaHieghtTxt.setText(unit_en);
                                        areaWidthTxt.setText(unit_w_en);
                                    }

                                    if (TextUtils.equals(fishingString, "1")) {
                                        fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    } else {
                                        fishingRadioButton.setImageResource(R.drawable.ic_circle);
                                    }
                                    if (TextUtils.equals(divingString, "1")) {
                                        divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    } else {
                                        divingRadioButton.setImageResource(R.drawable.ic_circle);
                                    }
                                    if (TextUtils.equals(tourString, "1")) {
                                        picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    } else {
                                        picnicRadioButton.setImageResource(R.drawable.ic_circle);
                                    }
                                }
                            });

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddBoatActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowWidthUnits() {
        @SuppressLint("InflateParams")
        View popupView = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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

                for (int j = 0; j < spinnerItemWidthUnitsList.size(); j++) {

                    final String text = spinnerItemWidthUnitsList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemWidthUnitsList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(AddBoatActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemWidthUnitsList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemWidthUnitsList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(AddBoatActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(AddBoatActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(AddBoatActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        width_unit_id = filteredList.get(position).getId();
                        areaWidthTxt.setText(status);
                        areaWidthTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemWidthUnitsList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(AddBoatActivity.this).equals("ar")) {
                    status = spinnerItemWidthUnitsList.get(position).getTextA();
                } else {
                    status = spinnerItemWidthUnitsList.get(position).getText();
                }
                width_unit_id = spinnerItemWidthUnitsList.get(position).getId();
                areaWidthTxt.setText(status);
                areaWidthTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowHeightUnits() {
        @SuppressLint("InflateParams")
        View popupView = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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

                for (int j = 0; j < spinnerItemHeightUnitsList.size(); j++) {

                    final String text = spinnerItemHeightUnitsList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemHeightUnitsList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(AddBoatActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemHeightUnitsList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemHeightUnitsList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(AddBoatActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(AddBoatActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(AddBoatActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        height_unit_id = filteredList.get(position).getId();
                        areaHieghtTxt.setText(status);
                        areaHieghtTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemHeightUnitsList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(AddBoatActivity.this).equals("ar")) {
                    status = spinnerItemHeightUnitsList.get(position).getTextA();
                } else {
                    status = spinnerItemHeightUnitsList.get(position).getText();
                }
                height_unit_id = spinnerItemHeightUnitsList.get(position).getId();
                areaHieghtTxt.setText(status);
                areaHieghtTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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

    public void measureWidthUnitList() {
        spinnerItemWidthUnitsList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "measure_units").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("measure_units", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("Measure_units");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                width_unit_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("name_en");
                                String name_ar = jsonObject1.getString("name_ar");
                                SpinnerItem cityData = new SpinnerItem(width_unit_id, name_en, name_ar);
                                spinnerItemWidthUnitsList.add(cityData);
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(AddBoatActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void measureHightUnitList() {
        spinnerItemHeightUnitsList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "measure_units").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("measure_units", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("Measure_units");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                height_unit_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("name_en");
                                String name_ar = jsonObject1.getString("name_ar");
                                SpinnerItem cityData = new SpinnerItem(height_unit_id, name_en, name_ar);
                                spinnerItemHeightUnitsList.add(cityData);
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(AddBoatActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(AddBoatActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(AddBoatActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

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
