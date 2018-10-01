package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class DiversOtherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA_3 = 56;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_3 = 57;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_6 = 62;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_6 = 63;

    private ImageView freeRadioButton, divingRadio, imageViewNew, imageViewNewOther;

    private TextView select_location_driving, licenceLevelTxt, licenceLevelOtherTxt, companyTxt,
            companyOtherTxt;

    private EditText driverNameEditText, licenceDrivingEditText, noteEditText;
    private EditText driverNameOtherEditText, licenceDrivingOtherEditText;
    private RelativeLayout imageNewLayout, imageNewOtherLayout, otherLayout;
    private URI mMediaUri;
    private File driverFile, driverOtherFile;
    private Uri photoUri;
    private ProgressDialog progressDialog;
    private Handler handler;
    private int diving_cylinder = 0, diving_free = 0;
    private List<SpinnerItem> spinnerItemCompanyList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemLevelList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemLevelOtherList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemCompanyOtherList = new ArrayList<>();
    private SpinnerAdapter spinnerAdapter;
    private AlertDialog alertDialog;
    private String company_id, level_id, companyOther_id, levelOther_id, token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divers_other);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");
        init();
        licenceLevelList();
        CompanyList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);

        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);
        TextView select_licence_new = findViewById(R.id.select_licence_new);
        TextView select_licence_other_new = findViewById(R.id.select_licence_other_new);
        Button approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        imageViewNew = findViewById(R.id.imageViewNew);
        imageViewNewOther = findViewById(R.id.imageViewOtherNew);
        select_location_driving = findViewById(R.id.select_location_driving);

        licenceLevelTxt = findViewById(R.id.licenceLevelTxt);
        otherLayout = findViewById(R.id.otherLayout);
        licenceLevelOtherTxt = findViewById(R.id.licenceLevelOtherTxt);
        companyTxt = findViewById(R.id.companyTxt);
        companyOtherTxt = findViewById(R.id.companyOtherTxt);

        imageNewLayout = findViewById(R.id.imageNewLayout);
        imageNewOtherLayout = findViewById(R.id.imageNewOtherLayout);
        freeRadioButton = findViewById(R.id.freeRadioButton);
        divingRadio = findViewById(R.id.divingRadio);


        RelativeLayout licenceLevelOtherLayout = findViewById(R.id.licenceLevelOtherLayout);
        RelativeLayout companyOtherLayout = findViewById(R.id.companyOtherLayout);
        RelativeLayout licenceLevelLayout = findViewById(R.id.licenceLevelLayout);
        RelativeLayout companyLayout = findViewById(R.id.companyLayout);
        RelativeLayout freeRadioButtonLayout = findViewById(R.id.freeRadioButtonLayout);
        RelativeLayout divingCLayout = findViewById(R.id.divingCLayout);

        backLayout.setOnClickListener(this);
        freeRadioButtonLayout.setOnClickListener(this);
        divingCLayout.setOnClickListener(this);

        licenceLevelOtherLayout.setOnClickListener(this);
        companyOtherLayout.setOnClickListener(this);
        licenceLevelLayout.setOnClickListener(this);
        companyLayout.setOnClickListener(this);

        select_licence_other_new.setOnClickListener(this);
        select_licence_new.setOnClickListener(this);

        select_location_driving.setOnClickListener(this);

        approveBtn.setOnClickListener(this);

        driverNameEditText = findViewById(R.id.driverNameEditText);
        licenceDrivingEditText = findViewById(R.id.licenceDrivingEditText);
        noteEditText = findViewById(R.id.noteEditText);

        driverNameOtherEditText = findViewById(R.id.driverOtherNameEditText);
        licenceDrivingOtherEditText = findViewById(R.id.licenceDrivingOtherEditText);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            register();
        } else if (id == R.id.select_licence_new) {
            openDialog3();
        } else if (id == R.id.select_licence_other_new) {
            openDialog6();
        } else if (id == R.id.divingCLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(divingRadio.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadio.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_check_circle);
                    diving_cylinder = 1;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState())
                            .equals(Objects.requireNonNull(freeRadioButton.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (Objects.equals(divingRadio.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadio.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_circle);
                    diving_cylinder = 0;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState())
                            .equals(Objects.requireNonNull(freeRadioButton.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_check_circle);
                    diving_cylinder = 1;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                            (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (divingRadio.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_circle);
                    diving_cylinder = 0;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                            (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            }
        } else if (id == R.id.freeRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(freeRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(freeRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving_free = 1;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState())
                            .equals(Objects.requireNonNull(divingRadio.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (Objects.equals(freeRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(freeRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_circle);
                    diving_free = 0;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState())
                            .equals(Objects.requireNonNull(divingRadio.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving_free = 1;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                            (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (freeRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_circle);
                    diving_free = 0;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                            (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            }
        } else if (id == R.id.select_location_driving) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 12);
        } else if (id == R.id.licenceLevelLayout) {
            openWindowLevel();
        } else if (id == R.id.companyLayout) {
            openWindowCompany();
        } else if (id == R.id.licenceLevelOtherLayout) {
            openWindowLevelOther();
        } else if (id == R.id.companyOtherLayout) {
            openWindowCompanyOther();
        }
    }

    public void register() {

        final String driverName = driverNameEditText.getText().toString().trim();
        final String licenceNumber = licenceDrivingEditText.getText().toString().trim();
        final String licenceLevel = licenceLevelTxt.getText().toString().trim();
        final String company = companyTxt.getText().toString().trim();
        final String noteDriver = noteEditText.getText().toString().trim();
        final String locationDriver = select_location_driving.getText().toString().trim();

        final String driverNameOther = driverNameOtherEditText.getText().toString().trim();
        final String licenceNumberOther = licenceDrivingOtherEditText.getText().toString().trim();
        final String licenceLevelOther = licenceLevelOtherTxt.getText().toString().trim();
        final String companyOther = companyOtherTxt.getText().toString().trim();

        int is_valid;

        if (TextUtils.isEmpty(driverName)) {
            driverNameEditText.setError(getString(R.string.error_field_required));
            driverNameEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(licenceNumber)) {
            licenceDrivingEditText.setError(getString(R.string.error_field_required));
            licenceDrivingEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(licenceLevel)) {
            licenceLevelTxt.setError(getString(R.string.error_field_required));
            licenceLevelTxt.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(company)) {
            companyTxt.setError(getString(R.string.error_field_required));
            companyTxt.requestFocus();
            is_valid = 1;
        } else if (diving_cylinder == 0 && diving_free == 0) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_choose_diving_type));
            is_valid = 1;
        } else if (TextUtils.isEmpty(noteDriver)) {
            noteEditText.setError(getString(R.string.error_field_required));
            noteEditText.requestFocus();
            is_valid = 1;
        } else if (driverFile == null) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image));
            is_valid = 1;
        } else if (diving_cylinder == 1 && diving_free == 1) {
            if (TextUtils.isEmpty(driverNameOther)) {
                driverNameOtherEditText.setError(getString(R.string.error_field_required));
                driverNameOtherEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(licenceNumberOther)) {
                licenceDrivingOtherEditText.setError(getString(R.string.error_field_required));
                licenceDrivingOtherEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(licenceLevelOther)) {
                licenceLevelOtherTxt.setError(getString(R.string.error_field_required));
                licenceLevelOtherTxt.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(companyOther)) {
                companyOtherTxt.setError(getString(R.string.error_field_required));
                companyOtherTxt.requestFocus();
                is_valid = 1;
            } else if (driverOtherFile == null) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image));
                is_valid = 1;
            } else {
                is_valid = 0;
            }
        } else {
            is_valid = 0;
        }

        if (is_valid == 0) {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                    if (diving_free == 1 && diving_cylinder == 0) {
                        builder.addFormDataPart("diver_free_full_name", driverName)
                                .addFormDataPart("diver_free_licence_no", licenceNumber)
                                .addFormDataPart("diver_free_licence_level", level_id)
                                .addFormDataPart("diver_free_licence_issuer", company_id)
                                .addFormDataPart("diver_tank_diving_type", String.valueOf(diving_cylinder))
                                .addFormDataPart("diver_free_diving_type", String.valueOf(diving_free))
                                .addFormDataPart("diver_about", noteDriver);
                        if (!TextUtils.isEmpty(locationDriver)) {
                            builder.addFormDataPart("diver_location", locationDriver);
                        }
                        if (driverFile != null) {
                            builder.addFormDataPart("diver_free_licence_image", driverFile.getName(),
                                    RequestBody.create(MediaType.parse("jpeg/png"), driverFile));
                        }
                    }
                    if (diving_free == 0 && diving_cylinder == 1) {
                        builder.addFormDataPart("diver_tank_full_name", driverName)
                                .addFormDataPart("diver_tank_licence_no", licenceNumber)
                                .addFormDataPart("diver_tank_licence_level", level_id)
                                .addFormDataPart("diver_tank_licence_issuer", company_id)
                                .addFormDataPart("diver_tank_diving_type", String.valueOf(diving_cylinder))
                                .addFormDataPart("diver_free_diving_type", String.valueOf(diving_free))
                                .addFormDataPart("diver_about", noteDriver);
                        if (!TextUtils.isEmpty(locationDriver)) {
                            builder.addFormDataPart("diver_location", locationDriver);
                        }
                        if (driverFile != null) {
                            builder.addFormDataPart("diver_tank_licence_image", driverFile.getName(),
                                    RequestBody.create(MediaType.parse("jpeg/png"), driverFile));
                        }
                    }
                    if (diving_free == 1 && diving_cylinder == 1) {
                        builder.addFormDataPart("diver_tank_full_name", driverName)
                                .addFormDataPart("diver_tank_licence_no", licenceNumber)
                                .addFormDataPart("diver_tank_licence_level", level_id)
                                .addFormDataPart("diver_tank_licence_issuer", company_id)
                                .addFormDataPart("diver_tank_diving_type", String.valueOf(diving_cylinder))
                                .addFormDataPart("diver_free_diving_type", String.valueOf(diving_free))
                                .addFormDataPart("diver_about", noteDriver);
                        if (!TextUtils.isEmpty(locationDriver)) {
                            builder.addFormDataPart("diver_location", locationDriver);
                        }
                        if (driverFile != null) {
                            builder.addFormDataPart("diver_tank_licence_image", driverFile.getName(),
                                    RequestBody.create(MediaType.parse("jpeg/png"), driverFile));
                        }
                        builder.addFormDataPart("diver_free_full_name", driverNameOther)
                                .addFormDataPart("diver_free_licence_no", licenceNumberOther)
                                .addFormDataPart("diver_free_licence_level", levelOther_id)
                                .addFormDataPart("diver_free_licence_issuer", companyOther_id);
                        if (driverOtherFile != null) {
                            builder.addFormDataPart("diver_free_licence_image", driverOtherFile.getName(),
                                    RequestBody.create(MediaType.parse("jpeg/png"), driverOtherFile));
                        }
                    }


                    RequestBody requestBody = builder.build();
                    registerUserInfo(requestBody);
                }
            }).start();
        }

    }

    public void registerUserInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(DiversOtherActivity.this);
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
                            + "add_diving_activity").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token).build();
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
                                        Intent intent = new Intent(DiversOtherActivity.this, MainActivity.class);
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
                                    AppErrorsManager.showErrorDialog(DiversOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(DiversOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(DiversOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(DiversOtherActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowCompany() {
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

                for (int j = 0; j < spinnerItemCompanyList.size(); j++) {

                    final String text = spinnerItemCompanyList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemCompanyList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCompanyList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCompanyList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(DiversOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(DiversOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        company_id = filteredList.get(position).getId();
                        companyTxt.setText(status);
                        companyTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemCompanyList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                    status = spinnerItemCompanyList.get(position).getTextA();
                } else {
                    status = spinnerItemCompanyList.get(position).getText();
                }
                company_id = spinnerItemCompanyList.get(position).getId();
                companyTxt.setText(status);
                companyTxt.setError(null);
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
    public void openWindowCompanyOther() {
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

                for (int j = 0; j < spinnerItemCompanyOtherList.size(); j++) {

                    final String text = spinnerItemCompanyOtherList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemCompanyOtherList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCompanyOtherList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCompanyOtherList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(DiversOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(DiversOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        companyOther_id = filteredList.get(position).getId();
                        companyOtherTxt.setText(status);
                        companyOtherTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemCompanyOtherList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                    status = spinnerItemCompanyOtherList.get(position).getTextA();
                } else {
                    status = spinnerItemCompanyOtherList.get(position).getText();
                }
                companyOther_id = spinnerItemCompanyOtherList.get(position).getId();
                companyOtherTxt.setText(status);
                companyOtherTxt.setError(null);
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
    public void openWindowLevel() {
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

                for (int j = 0; j < spinnerItemLevelList.size(); j++) {

                    final String text = spinnerItemLevelList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemLevelList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemLevelList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemLevelList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(DiversOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(DiversOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        level_id = filteredList.get(position).getId();
                        licenceLevelTxt.setText(status);
                        licenceLevelTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemLevelList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                    status = spinnerItemLevelList.get(position).getTextA();
                } else {
                    status = spinnerItemLevelList.get(position).getText();
                }
                level_id = spinnerItemLevelList.get(position).getId();
                licenceLevelTxt.setText(status);
                licenceLevelTxt.setError(null);
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
    public void openWindowLevelOther() {
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

                for (int j = 0; j < spinnerItemLevelOtherList.size(); j++) {

                    final String text = spinnerItemLevelOtherList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemLevelOtherList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemLevelOtherList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemLevelOtherList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(DiversOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(DiversOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        levelOther_id = filteredList.get(position).getId();
                        licenceLevelOtherTxt.setText(status);
                        licenceLevelOtherTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemLevelOtherList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(DiversOtherActivity.this).equals("ar")) {
                    status = spinnerItemLevelOtherList.get(position).getTextA();
                } else {
                    status = spinnerItemLevelOtherList.get(position).getText();
                }
                levelOther_id = spinnerItemLevelOtherList.get(position).getId();
                licenceLevelOtherTxt.setText(status);
                licenceLevelOtherTxt.setError(null);
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

    public void licenceLevelList() {
        spinnerItemLevelList.clear();
        spinnerItemLevelOtherList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "diving-license-levels").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("diving_license_levels");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                level_id = jsonObject1.getString("id");
                                levelOther_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem level = new SpinnerItem(level_id, name_en, name_ar);
                                SpinnerItem levelOther = new SpinnerItem(levelOther_id, name_en, name_ar);
                                spinnerItemLevelList.add(level);
                                spinnerItemLevelOtherList.add(levelOther);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(DiversOtherActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(DiversOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(DiversOtherActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(DiversOtherActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void CompanyList() {
        spinnerItemCompanyList.clear();
        spinnerItemCompanyOtherList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "diving-license-issuers").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("issuers");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                company_id = jsonObject1.getString("id");
                                companyOther_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem company = new SpinnerItem(company_id, name_en, name_ar);
                                SpinnerItem companyOther = new SpinnerItem(companyOther_id, name_en, name_ar);
                                spinnerItemCompanyList.add(company);
                                spinnerItemCompanyOtherList.add(companyOther);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(DiversOtherActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(DiversOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(DiversOtherActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(DiversOtherActivity.this, getString(R.string.error_network));
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
        if (requestCode == 12 && data != null) {
            select_location_driving.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_driving.setError(null);
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_3) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile3(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_3) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile3();
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_6) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile6(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_6) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile6();
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

    public void openDialog3() {
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
                    openGalleryFile3();
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
                    captureImageFromCamera3();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void openGalleryFile3() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_3);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile3(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        driverFile = new File(pdfPathHolder);
        imageNewLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageViewNew);

    }

    private void captureImageFromCamera3() {
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
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_3);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile3() {
        Log.e("uriii", mMediaUri.toString());
        driverFile = new File(mMediaUri);
        imageNewLayout.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageViewNew);
//        imageViewNew.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(driverFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageViewNew.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDialog6() {
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
                    openGalleryFile6();
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
                    captureImageFromCamera6();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGalleryFile6() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_6);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile6(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        driverOtherFile = new File(pdfPathHolder);
        imageNewOtherLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageViewNewOther);

    }

    private void captureImageFromCamera6() {
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
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_6);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile6() {
        Log.e("uriii", mMediaUri.toString());
        driverOtherFile = new File(mMediaUri);
        imageNewOtherLayout.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageViewNewOther);
//        imageViewNewOther.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(driverOtherFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageViewNewOther.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
