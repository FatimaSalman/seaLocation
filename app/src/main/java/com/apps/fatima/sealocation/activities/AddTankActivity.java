package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
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

public class AddTankActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText rentValueTanksEditText;
    private TextView tankTypeTxt, tanksNoEditText, otherEnTankTypeEditText, otherArTankTypeEditText;
    private ProgressDialog progressDialog;
    private Handler handler;
    private List<SpinnerItem> spinnerItemTankTypeList = new ArrayList<>();
    private String tankTypeId, token, tank_id, quantity, hourly_price;
    private Dialog alertDialog;
    private Button approveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tank_form);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");
        tank_id = getIntent().getStringExtra("tank_id");
//        Log.e("tank_id",tank_id);
        init();
        if (tank_id != null) {
            approveBtn.setText(getString(R.string.update_data));
            getTankDetails(token);
        }

        tankTypeList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);

        ImageView ic_back = findViewById(R.id.ic_back);
        approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        RelativeLayout typeLayout = findViewById(R.id.typeLayout);

        backLayout.setOnClickListener(this);
        typeLayout.setOnClickListener(this);
        approveBtn.setOnClickListener(this);

        otherArTankTypeEditText = findViewById(R.id.otherArTankTypeEditText);
        otherEnTankTypeEditText = findViewById(R.id.otherEnTankTypeEditText);
        tankTypeTxt = findViewById(R.id.tankTypeTxt);
        tanksNoEditText = findViewById(R.id.tanksNoEditText);
        rentValueTanksEditText = findViewById(R.id.rentValueTanksEditText);
        RelativeLayout minusLayout = findViewById(R.id.minusLayout);
        RelativeLayout plusLayout = findViewById(R.id.plusLayout);
        minusLayout.setOnClickListener(this);
        plusLayout.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        int totalCount = Integer.parseInt(tanksNoEditText.getText().toString());
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                tanksNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            tanksNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.approveBtn) {
            if (approveBtn.getText().toString().equals(getString(R.string.approve))) {
                addTank();
            } else {
                updateTank();
            }
        } else if (id == R.id.typeLayout) {
            openWindowTankType();
        }
    }

    public void addTank() {

        final String tankType = tankTypeTxt.getText().toString().trim();
        final String tankType_ar = otherArTankTypeEditText.getText().toString().trim();
        final String tankType_en = otherEnTankTypeEditText.getText().toString().trim();
        final String number = tanksNoEditText.getText().toString().trim();
        final String value = rentValueTanksEditText.getText().toString().trim();
        int is_valid;
        if (TextUtils.isEmpty(tankType)) {
            tankTypeTxt.setError(getString(R.string.error_field_required));
            tankTypeTxt.requestFocus();
            is_valid = 1;
        } else if (TextUtils.equals(tankType, getString(R.string.other))) {
            if (TextUtils.isEmpty(tankType_en)) {
                otherEnTankTypeEditText.setError(getString(R.string.error_field_required));
                otherEnTankTypeEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(tankType_ar)) {
                otherArTankTypeEditText.setError(getString(R.string.error_field_required));
                otherArTankTypeEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(value)) {
                rentValueTanksEditText.setError(getString(R.string.error_field_required));
                rentValueTanksEditText.requestFocus();
                is_valid = 1;
            } else if (number.equals("0")) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.enter_available_number));
                is_valid = 1;
            } else {
                is_valid = 0;
            }
        } else if (TextUtils.isEmpty(value)) {
            rentValueTanksEditText.setError(getString(R.string.error_field_required));
            rentValueTanksEditText.requestFocus();
            is_valid = 1;
        } else if (number.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.enter_available_number));
            is_valid = 1;
        } else {
            is_valid = 0;
        }
        if (is_valid == 0) {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("quantity", number)
                            .addFormDataPart("hourly_price", value)
                            .addFormDataPart("location", "33.22,858.888")
                            .addFormDataPart("driver_licence_end_date", "3/3/2018");

                    if (TextUtils.equals(tankType, getString(R.string.other))) {
                        builder.addFormDataPart("jetski_type_title_ar", tankType_ar);
                        builder.addFormDataPart("jetski_type_title_en", tankType_en);
                    } else
                        builder.addFormDataPart("type", tankTypeId);

                    RequestBody requestBody = builder.build();
                    addTankInfo(requestBody);
                }
            }).start();
        }
    }

    public void addTankInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddTankActivity.this);
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
                            + "store-jetski").post(requestBody)
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
//                                String success = jsonObject.getString("success");

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
                                    AppErrorsManager.showErrorDialog(AddTankActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddTankActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddTankActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddTankActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void tankTypeList() {
        spinnerItemTankTypeList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "jetski-types").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("Jetski_types");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                tankTypeId = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem cityData = new SpinnerItem(tankTypeId, name_en, name_ar);
                                spinnerItemTankTypeList.add(cityData);
                            }

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(AddTankActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(AddTankActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(AddTankActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(AddTankActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getTankDetails(final String token) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddTankActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(AddTankActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "jetski/" + tank_id).get()
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
                            String partner_boat = successObject.getString("jetski");
                            JSONObject boatObject = new JSONObject(partner_boat);
//                            String boat_id = boatObject.getString("id");
                            String type = boatObject.getString("jetski_type");
                            JSONObject typeObject = new JSONObject(type);
                            tankTypeId = typeObject.getString("id");
                            final String title_ar = typeObject.getString("title_ar");
                            final String title_en = typeObject.getString("title_en");

                            quantity = boatObject.getString("quantity");
                            hourly_price = boatObject.getString("hourly_price");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    tanksNoEditText.setText(quantity);
                                    rentValueTanksEditText.setText(hourly_price);
                                    if (AppLanguage.getLanguage(AddTankActivity.this).equals("ar"))
                                        tankTypeTxt.setText(title_ar);
                                    else
                                        tankTypeTxt.setText(title_en);
                                }
                            });

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddTankActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddTankActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddTankActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddTankActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void updateTank() {

        final String tankType = tankTypeTxt.getText().toString().trim();
        final String tankType_ar = otherArTankTypeEditText.getText().toString().trim();
        final String tankType_en = otherEnTankTypeEditText.getText().toString().trim();
        final String number = tanksNoEditText.getText().toString().trim();
        final String value = rentValueTanksEditText.getText().toString().trim();

        int is_valid;
        if (TextUtils.isEmpty(tankType)) {
            tankTypeTxt.setError(getString(R.string.error_field_required));
            tankTypeTxt.requestFocus();
            is_valid = 1;
        } else if (TextUtils.equals(tankType, getString(R.string.other))) {
            if (TextUtils.isEmpty(tankType_en)) {
                otherEnTankTypeEditText.setError(getString(R.string.error_field_required));
                otherEnTankTypeEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(tankType_ar)) {
                otherArTankTypeEditText.setError(getString(R.string.error_field_required));
                otherArTankTypeEditText.requestFocus();
                is_valid = 1;
            } else if (number.equals("0")) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.enter_available_number));
                is_valid = 1;
            } else if (TextUtils.isEmpty(value)) {
                rentValueTanksEditText.setError(getString(R.string.error_field_required));
                rentValueTanksEditText.requestFocus();
                is_valid = 1;
            } else {
                is_valid = 0;
            }
        } else if (number.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.enter_available_number));
            is_valid = 1;
        } else if (TextUtils.isEmpty(value)) {
            rentValueTanksEditText.setError(getString(R.string.error_field_required));
            rentValueTanksEditText.requestFocus();
            is_valid = 1;
        } else {
            is_valid = 0;
        }
        if (is_valid == 0) {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("quantity", number)
                            .addFormDataPart("hourly_price", value)
                            .addFormDataPart("location", "33.22,858.888")
                            .addFormDataPart("driver_licence_end_date", "3/3/2018")
                            .addFormDataPart("id", tank_id);

                    if (TextUtils.equals(tankType, getString(R.string.other))) {
                        builder.addFormDataPart("jetski_type_title_ar", tankType_ar);
                        builder.addFormDataPart("jetski_type_title_en", tankType_en);
                    } else
                        builder.addFormDataPart("type", tankTypeId);
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
                progressDialog = new ProgressDialog(AddTankActivity.this);
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
                            + "store-jetski").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(AddTankActivity.this, getString(R.string.update_successfully), new DialogInterface.OnClickListener() {
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
                                    AppErrorsManager.showErrorDialog(AddTankActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddTankActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddTankActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddTankActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowTankType() {
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

                for (int j = 0; j < spinnerItemTankTypeList.size(); j++) {

                    final String text = spinnerItemTankTypeList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemTankTypeList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(AddTankActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemTankTypeList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemTankTypeList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(AddTankActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(AddTankActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(AddTankActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        tankTypeId = filteredList.get(position).getId();
                        tankTypeTxt.setText(status);
                        if (TextUtils.equals(tankTypeTxt.getText().toString(), getString(R.string.other))) {
                            otherArTankTypeEditText.setVisibility(View.VISIBLE);
                            otherEnTankTypeEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherArTankTypeEditText.setVisibility(View.GONE);
                            otherEnTankTypeEditText.setVisibility(View.GONE);
                        }
                        tankTypeTxt.setError(null);
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
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, spinnerItemTankTypeList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(AddTankActivity.this).equals("ar")) {
                    status = spinnerItemTankTypeList.get(position).getTextA();
                } else {
                    status = spinnerItemTankTypeList.get(position).getText();
                }
                tankTypeId = spinnerItemTankTypeList.get(position).getId();
                tankTypeTxt.setText(status);
                if (TextUtils.equals(tankTypeTxt.getText().toString(), getString(R.string.other))) {
                    otherArTankTypeEditText.setVisibility(View.VISIBLE);
                    otherEnTankTypeEditText.setVisibility(View.VISIBLE);
                } else {
                    otherArTankTypeEditText.setVisibility(View.GONE);
                    otherEnTankTypeEditText.setVisibility(View.GONE);
                }
                tankTypeTxt.setError(null);
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
}
