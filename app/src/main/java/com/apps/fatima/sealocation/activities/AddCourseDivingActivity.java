package com.apps.fatima.sealocation.activities;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.apps.fatima.sealocation.R;
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

public class AddCourseDivingActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView yesRadioButton, noRadioButton;

    private EditText courseNameEditText, durationEditText, requirementEditText, valueEditText,
            valueEqEditText;
    private int available, flag;

    private ProgressDialog progressDialog;
    private Handler handler;
    private String token, course_id, diver_id, equValue;
    private Button approveBtn;
    private RelativeLayout priceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diving_course_form);
        token = AppPreferences.getString(this, "token");
        handler = new Handler(Looper.getMainLooper());
        diver_id = getIntent().getStringExtra("diver_id");
        course_id = getIntent().getStringExtra("course_id");
        init();
        if (course_id != null) {
            Log.e("course_id", course_id);
            getCourseDetails(token);
            approveBtn.setText(getString(R.string.update_data));
        }
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout divingLayout = findViewById(R.id.divingLayout);
        RelativeLayout fishingRadioButtonLayout = findViewById(R.id.fishingRadioButtonLayout);
        FontManager.applyFont(this, layout);

        ImageView ic_back = findViewById(R.id.ic_back);

        approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        yesRadioButton = findViewById(R.id.yesRadioButton);
        priceLayout = findViewById(R.id.priceLayout);
        noRadioButton = findViewById(R.id.noRadioButton);

        courseNameEditText = findViewById(R.id.courseNameEditText);
        durationEditText = findViewById(R.id.durationEditText);
        requirementEditText = findViewById(R.id.requirementEditText);
        valueEditText = findViewById(R.id.valueEditText);
        valueEqEditText = findViewById(R.id.valueEqEditText);

        backLayout.setOnClickListener(this);
        fishingRadioButtonLayout.setOnClickListener(this);
        divingLayout.setOnClickListener(this);
        approveBtn.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            if (approveBtn.getText().toString().equals(getString(R.string.approve))) {
                addCourse();
            } else {
                updateCourse();
            }
        } else if (id == R.id.fishingRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(noRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(noRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    noRadioButton.setImageResource(R.drawable.ic_check_circle);
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    available = 0;
                    equValue = "0";
                    Log.e("equValue", equValue);
                    priceLayout.setVisibility(View.GONE);
                } else if (Objects.equals(noRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(noRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    noRadioButton.setImageResource(R.drawable.ic_circle);
                }
            } else {
                if (Objects.requireNonNull(noRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    noRadioButton.setImageResource(R.drawable.ic_check_circle);
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    available = 0;
                    equValue = "0";
                    Log.e("equValue", equValue);
                    priceLayout.setVisibility(View.GONE);
                } else if (noRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    noRadioButton.setImageResource(R.drawable.ic_circle);
                }
            }
        } else if (id == R.id.divingLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(yesRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(yesRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_check_circle);
                    noRadioButton.setImageResource(R.drawable.ic_circle);
                    available = 1;
                    priceLayout.setVisibility(View.VISIBLE);
                } else if (Objects.equals(yesRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(yesRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.GONE);
                }
            } else {
                if (Objects.requireNonNull(yesRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_check_circle);
                    noRadioButton.setImageResource(R.drawable.ic_circle);
                    available = 1;
                    priceLayout.setVisibility(View.VISIBLE);
                } else if (yesRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    yesRadioButton.setImageResource(R.drawable.ic_circle);
                    priceLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addCourse() {
        final String courseName = courseNameEditText.getText().toString().trim();
        final String duration = durationEditText.getText().toString().trim();
        final String value = valueEditText.getText().toString().trim();
        final String requirement = requirementEditText.getText().toString().trim();

        if (TextUtils.isEmpty(courseName)) {
            courseNameEditText.setError(getString(R.string.error_field_required));
            courseNameEditText.requestFocus();
            flag = 1;
        } else if (TextUtils.isEmpty(duration)) {
            durationEditText.setError(getString(R.string.error_field_required));
            durationEditText.requestFocus();
            flag = 1;
        } else if (TextUtils.isEmpty(value)) {
            valueEditText.setError(getString(R.string.error_field_required));
            valueEditText.requestFocus();
            flag = 1;
        } else if (TextUtils.isEmpty(requirement)) {
            requirementEditText.setError(getString(R.string.error_field_required));
            requirementEditText.requestFocus();
            flag = 1;
        } else if (Objects.requireNonNull(yesRadioButton.getDrawable().getConstantState()).equals
                (getResources().getDrawable(R.drawable.ic_circle).getConstantState()) &&
                Objects.requireNonNull(noRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_diving_equi));
            flag = 1;
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (Objects.equals(yesRadioButton.getDrawable().getConstantState(),
                    Objects.requireNonNull(yesRadioButton.getContext()
                            .getDrawable(R.drawable.ic_circle)).getConstantState()) &&
                    Objects.equals(noRadioButton.getDrawable().getConstantState(),
                            Objects.requireNonNull(noRadioButton.getContext()
                                    .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_diving_equi));
                flag = 1;
            } else {
                if (available == 1) {
                    equValue = valueEqEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(equValue)) {
                        valueEqEditText.setError(getString(R.string.error_field_required));
                        valueEqEditText.requestFocus();
                        flag = 1;
                    } else {
                        flag = 0;
                    }
                } else {
                    flag = 0;
                }
            }
        } else if (available == 1) {
            equValue = valueEqEditText.getText().toString().trim();
            if (TextUtils.isEmpty(equValue)) {
                valueEqEditText.setError(getString(R.string.error_field_required));
                valueEqEditText.requestFocus();
                flag = 1;
            } else {
                flag = 0;
            }
        } else {
            flag = 0;
        }
        if (flag == 0) {
            Log.e("yyyy", "uuuuu");
            Log.e("equValue", equValue);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("diver_id", diver_id)
                            .addFormDataPart("title", courseName)
                            .addFormDataPart("period", duration)
                            .addFormDataPart("price", value)
                            .addFormDataPart("details", "details")
                            .addFormDataPart("requirements", requirement)
                            .addFormDataPart("gears_available", String.valueOf(available))
                            .addFormDataPart("gears_price", equValue);

                    RequestBody requestBody = builder.build();
                    addCourseInfo(requestBody);
                }
            }).start();
        }
    }

    public void addCourseInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddCourseDivingActivity.this);
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
                            + "diving-cource").post(requestBody)
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
                                        setResult(14, intent);
                                        finish();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateCourse() {

        final String courseName = courseNameEditText.getText().toString().trim();
        final String duration = durationEditText.getText().toString().trim();
        final String value = valueEditText.getText().toString().trim();
        final String requirement = requirementEditText.getText().toString().trim();


        if (TextUtils.isEmpty(courseName)) {
            courseNameEditText.setError(getString(R.string.error_field_required));
            courseNameEditText.requestFocus();
            flag = 1;
        } else if (TextUtils.isEmpty(duration)) {
            durationEditText.setError(getString(R.string.error_field_required));
            durationEditText.requestFocus();
            flag = 1;
        } else if (TextUtils.isEmpty(value)) {
            valueEditText.setError(getString(R.string.error_field_required));
            valueEditText.requestFocus();
            flag = 1;
        } else if (TextUtils.isEmpty(requirement)) {
            requirementEditText.setError(getString(R.string.error_field_required));
            requirementEditText.requestFocus();
            flag = 1;
        } else if (Objects.requireNonNull(yesRadioButton.getDrawable().getConstantState()).equals
                (getResources().getDrawable(R.drawable.ic_circle).getConstantState()) &&
                Objects.requireNonNull(noRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_diving_equi));
            flag = 1;
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (Objects.equals(yesRadioButton.getDrawable().getConstantState(),
                    Objects.requireNonNull(yesRadioButton.getContext()
                            .getDrawable(R.drawable.ic_circle)).getConstantState()) &&
                    Objects.equals(noRadioButton.getDrawable().getConstantState(),
                            Objects.requireNonNull(noRadioButton.getContext()
                                    .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_diving_equi));
                flag = 1;
            } else {
                flag = 0;
            }
        } else if (available == 1) {
            equValue = valueEqEditText.getText().toString().trim();
            if (TextUtils.isEmpty(equValue)) {
                valueEqEditText.setError(getString(R.string.error_field_required));
                valueEqEditText.requestFocus();
                flag = 1;
            } else {
                flag = 0;
            }
        }
        if (flag == 0) {
            Log.e("equValue", equValue);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("diver_id", diver_id)
                            .addFormDataPart("title", courseName)
                            .addFormDataPart("period", duration)
                            .addFormDataPart("price", value)
                            .addFormDataPart("details", "details")
                            .addFormDataPart("requirements", requirement)
                            .addFormDataPart("gears_available", String.valueOf(available))
                            .addFormDataPart("gears_price", equValue)
                            .addFormDataPart("id", course_id);

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
                progressDialog = new ProgressDialog(AddCourseDivingActivity.this);
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
                            + "diving-cource").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(AddCourseDivingActivity.this, getString(R.string.update_successfully), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent();
                                                setResult(14, intent);
                                                finish();
                                            }
                                        });

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getCourseDetails(final String token) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(AddCourseDivingActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(AddCourseDivingActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "diving-cources/" + course_id).get()
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
                            String course = successObject.getString("cources");

                            JSONObject boatObject = new JSONObject(course);
//                            String id = boatObject.getString("id");
                            diver_id = boatObject.getString("diver_id");
                            final String title = boatObject.getString("title");
                            final String period = boatObject.getString("period");
                            final String price = boatObject.getString("price");
                            final String requirements = boatObject.getString("requirements");
                            available = boatObject.getInt("gears_available");
                            final String gears_price = boatObject.getString("gears_price");


                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    courseNameEditText.setText(title);
                                    durationEditText.setText(period);
                                    valueEditText.setText(price);
                                    requirementEditText.setText(requirements);
                                    if (available == 1) {
                                        yesRadioButton.setImageResource(R.drawable.ic_check_circle);
                                        valueEqEditText.setVisibility(View.VISIBLE);
                                        valueEqEditText.setText(gears_price);
                                    } else {
                                        noRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AddCourseDivingActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

}
