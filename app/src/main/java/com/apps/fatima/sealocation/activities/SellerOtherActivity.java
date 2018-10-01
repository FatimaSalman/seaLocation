package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

public class SellerOtherActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText noteEditText, valueEditText, productNameEditText;
    private TextView productTypeTxt;
    private String productTypeId, token;
    private List<SpinnerItem> spinnerItemProductTypeList = new ArrayList<>();
    private Dialog alertDialog;
    private ProgressDialog progressDialog;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_other);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");
        init();
        productTypeList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);

        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);

        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        productNameEditText = findViewById(R.id.productNameEditText);
        noteEditText = findViewById(R.id.noteEditText);
        valueEditText = findViewById(R.id.valueEditText);
        productTypeTxt = findViewById(R.id.productTypeTxt);

        RelativeLayout typeLayout = findViewById(R.id.typeLayout);
        Button approveBtn = findViewById(R.id.approveBtn);
        backLayout.setOnClickListener(this);
        typeLayout.setOnClickListener(this);
        approveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            register();
        } else if (id == R.id.typeLayout) {
            openWindowProductType();
        }
    }

    public void register() {

        final String productName = productNameEditText.getText().toString().trim();
        String productType = productTypeTxt.getText().toString().trim();
        final String productValue = valueEditText.getText().toString().trim();
        final String productNote = noteEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productName)) {
            productNameEditText.setError(getString(R.string.error_field_required));
            productNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(productType)) {
            productTypeTxt.setError(getString(R.string.error_field_required));
            productTypeTxt.requestFocus();
        } else if (TextUtils.isEmpty(productNote)) {
            noteEditText.setError(getString(R.string.error_field_required));
            noteEditText.requestFocus();
        } else if (TextUtils.isEmpty(productValue)) {
            valueEditText.setError(getString(R.string.error_field_required));
            valueEditText.requestFocus();
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("product_name", productName)
                            .addFormDataPart("product_type", productTypeId)
                            .addFormDataPart("product_description", productNote)
                            .addFormDataPart("product_price", productValue);

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
                progressDialog = new ProgressDialog(SellerOtherActivity.this);
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
                            + "add_seller_activity").post(requestBody)
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
                                        Intent intent = new Intent(SellerOtherActivity.this, MainActivity.class);
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
                                    AppErrorsManager.showErrorDialog(SellerOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(SellerOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(SellerOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(SellerOtherActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void productTypeList() {
        spinnerItemProductTypeList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "products-types").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("product_type");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                productTypeId = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem cityData = new SpinnerItem(productTypeId, name_en, name_ar);
                                spinnerItemProductTypeList.add(cityData);
                            }

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(SellerOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(SellerOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(SellerOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(SellerOtherActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void openWindowProductType() {
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

                for (int j = 0; j < spinnerItemProductTypeList.size(); j++) {

                    final String text = spinnerItemProductTypeList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemProductTypeList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(SellerOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemProductTypeList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemProductTypeList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(SellerOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(SellerOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(SellerOtherActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        productTypeId = filteredList.get(position).getId();
                        productTypeTxt.setText(status);
                        productTypeTxt.setError(null);
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
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, spinnerItemProductTypeList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(SellerOtherActivity.this).equals("ar")) {
                    status = spinnerItemProductTypeList.get(position).getTextA();
                } else {
                    status = spinnerItemProductTypeList.get(position).getText();
                }
                productTypeId = spinnerItemProductTypeList.get(position).getId();
                productTypeTxt.setText(status);
                productTypeTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new

                DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        alertDialog.getWindow().
                setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
    }

}
