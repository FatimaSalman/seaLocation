
package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.fragment.DatePickerFragment;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FilePath;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class BoatsOtherActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int GALLERY_REQUEST_CODE_SCHEMA_1 = 52;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1 = 53;

    private ImageView divingRadioButton, fishingRadioButton, picnicRadioButton, imageView;

    private TextView expireDateLicence, expireDateForm, select_location, passengersNoEditText,
            areaWidthTxt, areaHieghtTxt;

    private EditText boatNoEditText, boatNameEditText, heightEditText, widthEditText, rentValueEditText;
    private int year, month, day;
    static final int DATE_DIALOG_ID = 100;
    static final int DATE_DIALOG_ID_2 = 200;
    private URI mMediaUri;
    private File boatFile;
    private Uri photoUri;
    private ProgressDialog progressDialog;
    private Handler handler;
    private int diving = 0, fishing = 0, picnic = 0;
    private List<SpinnerItem> spinnerItemWidthUnitsList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemHeightUnitsList = new ArrayList<>();
    private SpinnerAdapter spinnerAdapter;
    private AlertDialog alertDialog;
    private String width_unit_id, height_unit_id, token;
    private Calendar c;
    private RelativeLayout imageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boats_other);
        handler = new Handler(Looper.getMainLooper());
        init();
        token = AppPreferences.getString(this, "token");
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        measureWidthUnitList();
        measureHightUnitList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout plusLayout = findViewById(R.id.plusLayout);
        RelativeLayout minusLayout = findViewById(R.id.minusLayout);

        RelativeLayout divingLayout = findViewById(R.id.divingLayout);
        RelativeLayout fishingRadioButtonLayout = findViewById(R.id.fishingRadioButtonLayout);
        RelativeLayout picnicRadioButtonLayout = findViewById(R.id.picnicRadioButtonLayout);

        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);

        TextView licenceImage = findViewById(R.id.licenceImage);

        Button approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        select_location = findViewById(R.id.select_location);
        imageLayout = findViewById(R.id.imageLayout);
        imageView = findViewById(R.id.imageView);

        areaWidthTxt = findViewById(R.id.areaWidthTxt);
        areaHieghtTxt = findViewById(R.id.areaHieghtTxt);

        divingRadioButton = findViewById(R.id.divingRadioButton);
        fishingRadioButton = findViewById(R.id.fishingRadioButton);
        picnicRadioButton = findViewById(R.id.picnicRadioButton);

        RelativeLayout expireDateLicenceLayout = findViewById(R.id.expireDateLicenceLayout);
        RelativeLayout expireDataLayout = findViewById(R.id.expireDataLayout);

        expireDateForm = findViewById(R.id.expireTxt);
        expireDateLicence = findViewById(R.id.expireLicenceTxt);
        backLayout.setOnClickListener(this);
        areaHieghtTxt.setOnClickListener(this);
        areaWidthTxt.setOnClickListener(this);

        divingLayout.setOnClickListener(this);
        picnicRadioButtonLayout.setOnClickListener(this);
        fishingRadioButtonLayout.setOnClickListener(this);

        picnicRadioButtonLayout.setOnClickListener(this);
        plusLayout.setOnClickListener(this);
        minusLayout.setOnClickListener(this);

        licenceImage.setOnClickListener(this);

        expireDataLayout.setOnClickListener(this);
        expireDateLicenceLayout.setOnClickListener(this);
        select_location.setOnClickListener(this);

        approveBtn.setOnClickListener(this);

        boatNoEditText = findViewById(R.id.boatNoEditText);
        boatNameEditText = findViewById(R.id.boatNameEditText);
        passengersNoEditText = findViewById(R.id.passengersNoEditText);
        heightEditText = findViewById(R.id.heightEditText);
        widthEditText = findViewById(R.id.widthEditText);
        rentValueEditText = findViewById(R.id.rentValueEditText);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        int totalCount = Integer.parseInt(passengersNoEditText.getText().toString());
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            register();
        } else if (id == R.id.licenceImage) {
            openDialog1();
        } else if (id == R.id.areaHieghtTxt) {
            openWindowHeightUnits();
        } else if (id == R.id.areaWidthTxt) {
            openWindowWidthUnits();
        } else if (id == R.id.select_location) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 10);
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                passengersNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            passengersNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.divingLayout || id == R.id.divingRadioButton) {
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
        } else if (id == R.id.fishingRadioButtonLayout || id == R.id.fishingRadioButton) {
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
        } else if (id == R.id.picnicRadioButtonLayout || id == R.id.picnicRadioButton) {
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
        } else if (id == R.id.expireDataLayout) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "first");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "Date Picker");
        } else if (id == R.id.expireDateLicenceLayout) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "second");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "Date Picker");
        }
    }


    public void register() {
        final String boatNumber = boatNoEditText.getText().toString().trim();
        final String boatName = boatNameEditText.getText().toString().trim();
        final String passengersNumber = passengersNoEditText.getText().toString().trim();
        final String width = widthEditText.getText().toString().trim();
        final String widthTxt = areaWidthTxt.getText().toString().trim();
        final String height = heightEditText.getText().toString().trim();
        final String heightTxt = areaHieghtTxt.getText().toString().trim();
        final String rentValue = rentValueEditText.getText().toString().trim();
        final String location = select_location.getText().toString().trim();
        final String expireTxt = expireDateForm.getText().toString().trim();
        final String expireLicenceTxt = expireDateLicence.getText().toString().trim();

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
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_passenger_no));
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
        } else if (diving == 0 && fishing == 0 && picnic == 0) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
        } else if (TextUtils.isEmpty(expireTxt)) {
            expireDateForm.setError(getString(R.string.error_field_required));
            expireDateForm.requestFocus();
        } else if (TextUtils.isEmpty(expireLicenceTxt)) {
            expireDateLicence.setError(getString(R.string.error_field_required));
            expireDateLicence.requestFocus();
        } else if (boatFile == null) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image));
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                    builder.addFormDataPart("boat_no", boatNumber)
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
                            .addFormDataPart("boat_licence_expire_date", expireTxt)
                            .addFormDataPart("boat_driving_licence_expire_date", expireLicenceTxt);

                    if (boatFile != null) {
                        builder.addFormDataPart("boat_licence_image", boatFile.getName(),
                                RequestBody.create(MediaType.parse("jpeg/png"), boatFile));
                    }

                    RequestBody requestBody = builder.build();
                    registerUserInfo(requestBody);
                }
            }).start();
        }

    }

    private void registerUserInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(BoatsOtherActivity.this);
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
                            + "add_boat_activity").post(requestBody)
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
                                        Intent intent = new Intent(BoatsOtherActivity.this, MainActivity.class);
                                        intent.putExtra("profile", "profile");
                                        startActivity(intent);
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
                                    AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, getString(R.string.error_network));
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
                    if (AppLanguage.getLanguage(BoatsOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemWidthUnitsList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemWidthUnitsList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(BoatsOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(BoatsOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(BoatsOtherActivity.this).equals("ar")) {
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
                if (AppLanguage.getLanguage(BoatsOtherActivity.this).equals("ar")) {
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
                    if (AppLanguage.getLanguage(BoatsOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemHeightUnitsList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemHeightUnitsList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(BoatsOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(BoatsOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(BoatsOtherActivity.this).equals("ar")) {
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
                if (AppLanguage.getLanguage(BoatsOtherActivity.this).equals("ar")) {
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
                                    AppErrorsManager.showErrorDialog(BoatsOtherActivity.this,  e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, getString(R.string.error_network));
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
                                    AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(BoatsOtherActivity.this,e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(BoatsOtherActivity.this, getString(R.string.error_network));
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
            case DATE_DIALOG_ID_2:
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, myDateListener_, year, month, day);
                datePickerDialog1.getDatePicker().setMinDate(c.getTimeInMillis());
                return datePickerDialog1;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            expireDateForm.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2 + 1).append("/").append(arg1));
            expireDateForm.setError(null);
            day = arg3;
            month = arg2 + 1;
            year = arg1;
        }
    };
    private DatePickerDialog.OnDateSetListener myDateListener_ = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            expireDateLicence.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2 + 1).append("/").append(arg1));
            expireDateLicence.setError(null);
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
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_1) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile1(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile1();
            }
        }
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    public void openDialog1() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED) {
                    openGalleryFile1();
                } else
                    checkAndRequestPermissions();
//                openGalleryFile();
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera1();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
//        String mCurrentPhotoPath = image.getAbsolutePath();
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void openGalleryFile1() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_1);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile1(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        boatFile = new File(pdfPathHolder);
        imageLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageView);

    }

    private void captureImageFromCamera1() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile1() {
        Log.e("uriii", mMediaUri.toString());
        boatFile = new File(mMediaUri);
        imageLayout.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageView);
//        imageView.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(boatFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageView.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



