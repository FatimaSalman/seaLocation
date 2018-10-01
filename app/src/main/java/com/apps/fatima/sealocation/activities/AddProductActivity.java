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

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;

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

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView productTypeTxt, title;
    private EditText productNameEditText, noteEditText, valueEditText, otherEnProductEditText, otherArProductEditText;
    private ProgressDialog progressDialog;
    private Handler handler;
    private String token, product_id;
    private String productTypeId;
    private Button approveBtn;
    private List<SpinnerItem> spinnerItemProductTypeList = new ArrayList<>();
    private Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_form);
        token = AppPreferences.getString(this, "token");
        handler = new Handler(Looper.getMainLooper());
        init();
        productTypeList();
        product_id = getIntent().getStringExtra("product_id");
        init();
        if (product_id != null) {
            getProduct(product_id);
            approveBtn.setText(getString(R.string.update_data));
            title.setText(getString(R.string.update_data));
        }
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

        otherArProductEditText = findViewById(R.id.otherArProductEditText);
        otherEnProductEditText = findViewById(R.id.otherEnProductEditText);

        productNameEditText = findViewById(R.id.productNameEditText);
        productTypeTxt = findViewById(R.id.productTypeTxt);
        noteEditText = findViewById(R.id.noteEditText);
        valueEditText = findViewById(R.id.valueEditText);
        title = findViewById(R.id.title);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            if (approveBtn.getText().toString().equals(getString(R.string.approve))) {
                approveProduct();
            } else {
                updateProduct(product_id);
            }
        } else if (id == R.id.typeLayout) {
            openWindowProductType();
        }
    }

    public void approveProduct() {
        final String productType = productTypeTxt.getText().toString().trim();
        final String productType_ar = otherArProductEditText.getText().toString().trim();
        final String productType_en = otherEnProductEditText.getText().toString().trim();
        final String productName = productNameEditText.getText().toString().trim();
        final String productValue = valueEditText.getText().toString().trim();
        final String note = noteEditText.getText().toString().trim();
        int is_valid;
        if (TextUtils.isEmpty(productName)) {
            productNameEditText.setError(getString(R.string.error_field_required));
            productNameEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(productType)) {
            productTypeTxt.setError(getString(R.string.error_field_required));
            productTypeTxt.requestFocus();
            is_valid = 1;
        } else if (TextUtils.equals(productType, getString(R.string.other))) {
            if (TextUtils.isEmpty(productType_en)) {
                otherEnProductEditText.setError(getString(R.string.error_field_required));
                otherEnProductEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(productType_ar)) {
                otherArProductEditText.setError(getString(R.string.error_field_required));
                otherArProductEditText.requestFocus();
                is_valid = 1;
            } else {
                if (TextUtils.isEmpty(note)) {
                    noteEditText.setError(getString(R.string.error_field_required));
                    noteEditText.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(productValue)) {
                    valueEditText.setError(getString(R.string.error_field_required));
                    valueEditText.requestFocus();
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
            }
        } else if (TextUtils.isEmpty(note)) {
            noteEditText.setError(getString(R.string.error_field_required));
            noteEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(productValue)) {
            valueEditText.setError(getString(R.string.error_field_required));
            valueEditText.requestFocus();
            is_valid = 1;
        } else {
            is_valid = 0;
        }
        if (is_valid == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("product_name", productName)
                            .addFormDataPart("product_price", productValue)
                            .addFormDataPart("product_description", note);

                    if (TextUtils.equals(productType, getString(R.string.other))) {
                        builder.addFormDataPart("product_type_ar", productType_ar);
                        builder.addFormDataPart("product_type_en", productType_en);
                    } else
                        builder.addFormDataPart("product_type", productTypeId);

                    RequestBody requestBody = builder.build();
                    approveProductInfo(requestBody);
                }
            }).start();
        }
    }

    public void approveProductInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddProductActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(AddProductActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "store-produts").post(requestBody)
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
                                    AppErrorsManager.showErrorDialog(AddProductActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddProductActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddProductActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddProductActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                    if (AppLanguage.getLanguage(AddProductActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemProductTypeList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemProductTypeList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(AddProductActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(AddProductActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(AddProductActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        productTypeId = filteredList.get(position).getId();
                        productTypeTxt.setText(status);
                        if (TextUtils.equals(productTypeTxt.getText().toString(), getString(R.string.other))) {
                            otherEnProductEditText.setVisibility(View.VISIBLE);
                            otherArProductEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherEnProductEditText.setVisibility(View.GONE);
                            otherArProductEditText.setVisibility(View.GONE);
                        }
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
                if (AppLanguage.getLanguage(AddProductActivity.this).equals("ar")) {
                    status = spinnerItemProductTypeList.get(position).getTextA();
                } else {
                    status = spinnerItemProductTypeList.get(position).getText();
                }
                productTypeId = spinnerItemProductTypeList.get(position).getId();
                productTypeTxt.setText(status);
                if (TextUtils.equals(productTypeTxt.getText().toString(), getString(R.string.other))) {
                    otherEnProductEditText.setVisibility(View.VISIBLE);
                    otherArProductEditText.setVisibility(View.VISIBLE);
                } else {
                    otherEnProductEditText.setVisibility(View.GONE);
                    otherArProductEditText.setVisibility(View.GONE);
                }
                productTypeTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new

                DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().

                getAttributes());
        alertDialog.getWindow().

                setAttributes(lp);
        alertDialog.show();
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
                                    AppErrorsManager.showErrorDialog(AddProductActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(AddProductActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(AddProductActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(AddProductActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getProduct(final String product_id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddProductActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(AddProductActivity.this, new InternetAvailableCallback() {
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
                            + "get_product/" + product_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("productData", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
//                            String id = successObject.getString("id");
//                            String user_id = successObject.getString("user_id");
                            String type_id = successObject.getString("product_type");
                            JSONObject typeObject = new JSONObject(type_id);
                            final String type_ar = typeObject.getString("title_ar");
                            final String type_en = typeObject.getString("title_en");
                            productTypeId = typeObject.getString("id");
                            final String product_name = successObject.getString("product_name");
                            final String product_description = successObject.getString("product_description");
                            final String product_price = successObject.getString("product_price");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    if (AppLanguage.getLanguage(AddProductActivity.this).equals("ar"))
                                        productTypeTxt.setText(type_ar);
                                    else
                                        productTypeTxt.setText(type_en);
                                    productNameEditText.setText(product_name);
                                    valueEditText.setText(product_price);
                                    noteEditText.setText(product_description);
                                }
                            });


                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddProductActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddProductActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddProductActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddProductActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void updateProduct(final String product_id) {
        final String productType = productTypeTxt.getText().toString().trim();
        final String productType_ar = otherArProductEditText.getText().toString().trim();
        final String productType_en = otherEnProductEditText.getText().toString().trim();
        final String productName = productNameEditText.getText().toString().trim();
        final String productValue = valueEditText.getText().toString().trim();
        final String note = noteEditText.getText().toString().trim();
        int is_valid;
        if (TextUtils.isEmpty(productName)) {
            productNameEditText.setError(getString(R.string.error_field_required));
            productNameEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(productType)) {
            productTypeTxt.setError(getString(R.string.error_field_required));
            productTypeTxt.requestFocus();
            is_valid = 1;
        } else if (TextUtils.equals(productType, getString(R.string.other))) {
            if (TextUtils.isEmpty(productType_en)) {
                otherEnProductEditText.setError(getString(R.string.error_field_required));
                otherEnProductEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(productType_ar)) {
                otherArProductEditText.setError(getString(R.string.error_field_required));
                otherArProductEditText.requestFocus();
                is_valid = 1;
            } else {
                if (TextUtils.isEmpty(note)) {
                    noteEditText.setError(getString(R.string.error_field_required));
                    noteEditText.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(productValue)) {
                    valueEditText.setError(getString(R.string.error_field_required));
                    valueEditText.requestFocus();
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
            }
        } else if (TextUtils.isEmpty(note)) {
            noteEditText.setError(getString(R.string.error_field_required));
            noteEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(productValue)) {
            valueEditText.setError(getString(R.string.error_field_required));
            valueEditText.requestFocus();
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
                            .addFormDataPart("product_name", productName)
                            .addFormDataPart("product_price", productValue)
                            .addFormDataPart("product_description", note)
                            .addFormDataPart("id", product_id);

                    if (TextUtils.equals(productType, getString(R.string.other))) {
                        builder.addFormDataPart("product_type_ar", productType_ar);
                        builder.addFormDataPart("product_type_en", productType_en);
                    } else
                        builder.addFormDataPart("product_type", productTypeId);

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
                progressDialog = new ProgressDialog(AddProductActivity.this);
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
                            + "store-produts").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(AddProductActivity.this,
                                                getString(R.string.update_successfully), new DialogInterface.OnClickListener() {
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
                                    AppErrorsManager.showErrorDialog(AddProductActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddProductActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddProductActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddProductActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }
}
