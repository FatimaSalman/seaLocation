package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.FilePath;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.github.siyamed.shapeimageview.CircularImageView;
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

public class RegisterProductsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 51;
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText,
            noteEditText, mobileEditText, valueEditText, productNameEditText, otherEnCityEditText,
            otherArCityEditText, otherEnProductEditText, otherArProductEditText;
    private TextView productTypeTxt, cityTxt;
    private CircularImageView log_img;
    private ImageView logo_user;
    private File fileSchema;
    private String city_id, productTypeId;
    private SpinnerAdapter spinnerAdapter;
    private List<SpinnerItem> spinnerItemCityList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemProductTypeList = new ArrayList<>();
    private Dialog alertDialog;
    private ProgressDialog progressDialog;
    private Handler handler;
    private URI mMediaUri;
    private CheckBox agreeLicenseCheckBox;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_products);
        handler = new Handler(Looper.getMainLooper());
        init();
        cityList();
        productTypeList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        agreeLicenseCheckBox = findViewById(R.id.agreeLicenseCheckBox);

        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);
        ImageView ic_info = findViewById(R.id.ic_info);
        TextView select_image = findViewById(R.id.select_image);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        productNameEditText = findViewById(R.id.productNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        logo_user = findViewById(R.id.logo_user);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        noteEditText = findViewById(R.id.noteEditText);
        valueEditText = findViewById(R.id.valueEditText);
        productTypeTxt = findViewById(R.id.productTypeTxt);
        cityTxt = findViewById(R.id.cityTxt);
        log_img = findViewById(R.id.log_img);
        otherArCityEditText = findViewById(R.id.otherArCityEditText);
        otherEnCityEditText = findViewById(R.id.otherEnCityEditText);
        otherArProductEditText = findViewById(R.id.otherArProductEditText);
        otherEnProductEditText = findViewById(R.id.otherEnProductEditText);

        RelativeLayout typeLayout = findViewById(R.id.typeLayout);
        RelativeLayout cityLayout = findViewById(R.id.cityLayout);
        Button approveBtn = findViewById(R.id.approveBtn);
        backLayout.setOnClickListener(this);
        logo_user.setOnClickListener(this);
        typeLayout.setOnClickListener(this);
        cityLayout.setOnClickListener(this);
        approveBtn.setOnClickListener(this);
        log_img.setOnClickListener(this);
        select_image.setOnClickListener(this);
        ic_info.setOnClickListener(this);
        ic_info.setOnClickListener(this);
        agreeLicenseCheckBox.setOnClickListener(this);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
                String localeString = ims.getLocale();
                Locale locale = new Locale(localeString);
                final String currentLanguage = locale.getDisplayLanguage();
                if (currentLanguage.equals("Arabic")) {
                    AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, getString(R.string.username_english_characters));
                } else {
                    Log.e("currentLanguage", currentLanguage);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            register();
        } else if (id == R.id.typeLayout) {
            openWindowProductType();
        } else if (id == R.id.cityLayout) {
            openWindowCity();
        } else if (id == R.id.log_img || id == R.id.select_image || id == R.id.logo_user) {
            openDialog();
        } else if (id == R.id.ic_info) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (id == R.id.agreeLicenseCheckBox) {
            agreeLicenseCheckBox.setChecked(false);
            Intent intent = new Intent(this, PrivacyActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    public void register() {
        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        final String cityName = cityTxt.getText().toString().trim();
        final String cityName_ar = otherArCityEditText.getText().toString().trim();
        final String cityName_en = otherEnCityEditText.getText().toString().trim();
        final String productName = productNameEditText.getText().toString().trim();
        final String productType = productTypeTxt.getText().toString().trim();
        final String productType_ar = otherArProductEditText.getText().toString().trim();
        final String productType_en = otherEnProductEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String productValue = valueEditText.getText().toString().trim();
        final String productNote = noteEditText.getText().toString().trim();

        int is_valid = 0;
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            usernameEditText.requestFocus();
            is_valid = 1;
        } else if (FontManager.textPersian(username)) {
            Log.e("true", FontManager.textPersian(username) + "");
            usernameEditText.setError(getString(R.string.username_english_characters));
            usernameEditText.requestFocus();
            is_valid = 1;
        } else if (!TextUtils.isEmpty(email) && !email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            emailEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            passwordEditText.requestFocus();
            is_valid = 1;
        } else if (password.length() < 6) {
            passwordEditText.setError(getString(R.string.pass_characters));
            passwordEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.error_field_required));
            confirmPasswordEditText.requestFocus();
            is_valid = 1;
        } else if (!TextUtils.equals(confirmPassword, password)) {
            confirmPasswordEditText.setError(getString(R.string.correct_pass));
            confirmPasswordEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(productName)) {
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
                is_valid = 0;
            }
        }
        if (is_valid == 0) {
            if (TextUtils.isEmpty(productNote)) {
                noteEditText.setError(getString(R.string.error_field_required));
                noteEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(mobile)) {
                mobileEditText.setError(getString(R.string.error_field_required));
                mobileEditText.requestFocus();
                is_valid = 1;
            } else if (mobile.length() > 9 || mobile.length() < 9) {
                mobileEditText.setError(getString(R.string.your_number_must_be_not_more_or_not_less_ten));
                mobileEditText.requestFocus();
                is_valid = 1;
            } else if (mobile.startsWith("0")) {
                mobileEditText.setError(getString(R.string.first_digit_not_zero));
                mobileEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.equals(cityName, getString(R.string.other))) {
                if (TextUtils.isEmpty(cityName_en)) {
                    otherEnCityEditText.setError(getString(R.string.error_field_required));
                    otherEnCityEditText.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(cityName_ar)) {
                    otherArCityEditText.setError(getString(R.string.error_field_required));
                    otherArCityEditText.requestFocus();
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
            }
            if (is_valid == 0) {
                if (TextUtils.isEmpty(productValue)) {
                    valueEditText.setError(getString(R.string.error_field_required));
                    valueEditText.requestFocus();
                    is_valid = 1;
                } else if (!agreeLicenseCheckBox.isChecked()) {
                    AppErrorsManager.showErrorDialog(this, getString(R.string.agree_of_terms));
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
                                    .addFormDataPart("email", email)
                                    .addFormDataPart("password", password)
                                    .addFormDataPart("name", username)
                                    .addFormDataPart("mobile", mobile)
                                    .addFormDataPart("user_type", "1")
                                    .addFormDataPart("register_seller", "1")
                                    .addFormDataPart("product_name", productName)
                                    .addFormDataPart("product_description", productNote)
                                    .addFormDataPart("product_price", productValue);

                            if (TextUtils.equals(cityName, getString(R.string.other))) {
                                builder.addFormDataPart("city_title_ar", cityName_ar);
                                builder.addFormDataPart("city_title_en", cityName_en);
                            } else
                                builder.addFormDataPart("city", city_id);

                            if (TextUtils.equals(productType, getString(R.string.other))) {
                                builder.addFormDataPart("product_type_ar", productType_ar);
                                builder.addFormDataPart("product_type_en", productType_en);
                            } else
                                builder.addFormDataPart("product_type", productTypeId);

                            if (fileSchema != null) {
                                builder.addFormDataPart("user_image", fileSchema.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), fileSchema));
                            }

                            RequestBody requestBody = builder.build();
                            registerUserInfo(requestBody);
                        }
                    }).start();
                }
            }
        }
    }

    public void registerUserInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(RegisterProductsActivity.this);
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
                            + "register").post(requestBody)
//                            .addHeader("Authorization", "Bearer 6AcQL03QVQotSAYGHsZM9ZnCqnqKSVlS9WfuuQu0")
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            String error = jsonObject.optString("error");
                            if (jsonObject.has("error")) {
                                final JSONObject jsonError = new JSONObject(error);
                                handler.post(new Runnable() {
                                    public void run() {
                                        progressDialog.hide();
                                        if (jsonError.has("name")) {
                                            String userNameError = jsonError.optString("name");
                                            usernameEditText.setError(userNameError.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
                                            usernameEditText.requestFocus();
                                        } else if (jsonError.has("email")) {
                                            String emailError = jsonError.optString("email");
                                            emailEditText.setError(emailError.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
                                            emailEditText.requestFocus();
                                        } else if (jsonError.has("mobile")) {
                                            String user_mobile = jsonError.optString("mobile");
                                            mobileEditText.setError(user_mobile.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
                                            mobileEditText.requestFocus();
                                        }
                                    }
                                });
                            } else if (jsonObject.has("success")) {
//                                String success = jsonObject.getString("success");
//                                JSONObject jsonError = new JSONObject(success);
//                                final String token = jsonError.getString("token");
//                                final String name = jsonError.getString("name");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        startActivity(new Intent(RegisterProductsActivity.this, LoginActivity.class));
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
                                    AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void cityList() {
        spinnerItemCityList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "cities").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("cities");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                city_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("name_en");
                                String name_ar = jsonObject1.getString("name_ar");
                                SpinnerItem cityData = new SpinnerItem(city_id, name_en, name_ar);
                                spinnerItemCityList.add(cityData);
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, getString(R.string.error_network));
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
                                    AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterProductsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowCity() {
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

                for (int j = 0; j < spinnerItemCityList.size(); j++) {

                    final String text = spinnerItemCityList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemCityList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterProductsActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCityList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCityList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterProductsActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterProductsActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterProductsActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        city_id = filteredList.get(position).getId();
                        cityTxt.setText(status);
                        if (TextUtils.equals(cityTxt.getText().toString(), getString(R.string.other))) {
                            otherEnCityEditText.setVisibility(View.VISIBLE);
                            otherArCityEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherEnCityEditText.setVisibility(View.GONE);
                            otherArCityEditText.setVisibility(View.GONE);
                        }
                        cityTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemCityList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterProductsActivity.this).equals("ar")) {
                    status = spinnerItemCityList.get(position).getTextA();
                } else {
                    status = spinnerItemCityList.get(position).getText();
                }
                city_id = spinnerItemCityList.get(position).getId();
                cityTxt.setText(status);
                if (TextUtils.equals(cityTxt.getText().toString(), getString(R.string.other))) {
                    otherEnCityEditText.setVisibility(View.VISIBLE);
                    otherArCityEditText.setVisibility(View.VISIBLE);
                } else {
                    otherEnCityEditText.setVisibility(View.GONE);
                    otherArCityEditText.setVisibility(View.GONE);
                }
                cityTxt.setError(null);
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
                    if (AppLanguage.getLanguage(RegisterProductsActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemProductTypeList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemProductTypeList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterProductsActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterProductsActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterProductsActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemProductTypeList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterProductsActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new

                DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        Objects.requireNonNull(alertDialog.getWindow()).
                setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
    }

    private void openGalleryFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        fileSchema = new File(pdfPathHolder);
        logo_user.setVisibility(View.GONE);
        Picasso.get().load(uri).into(log_img);

    }

    private void captureImageFromCamera() {
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
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        }
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile() {
        Log.e("uriii", mMediaUri.toString());
        fileSchema = new File(mMediaUri);
//        try {
//            Bitmap bitmap = handleSamplingAndRotationBitmap(RegisterProductsActivity.this, mMediaUri.toString());
//        Picasso.get().load(mMediaUri.toString()).into(log_img);
        logo_user.setVisibility(View.GONE);
        try {
            ExifInterface exifObject = new ExifInterface(fileSchema.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            log_img.setImageBitmap(imageRotate);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE_SCHEMA) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile();
            }
        } else if (requestCode == 1 && data != null) {
            agreeLicenseCheckBox.setChecked(true);
        }
    }

    public void openDialog() {
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
                    openGalleryFile();
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
                    captureImageFromCamera();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
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

}
