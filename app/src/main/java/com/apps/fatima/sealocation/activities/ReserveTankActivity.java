package com.apps.fatima.sealocation.activities;

import android.app.ProgressDialog;

import android.content.DialogInterface;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


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

public class ReserveTankActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private Handler handler;
    private TextView person_number;
    private String tank_id, token, partner_id, name, quantity, type_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve_tank_form);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");
        tank_id = getIntent().getStringExtra("tank_id");
        quantity = getIntent().getStringExtra("quantity");
        type_name = getIntent().getStringExtra("type_name");
        partner_id = getIntent().getStringExtra("partner_id");
        Log.e("dfdf", partner_id);
        name = getIntent().getStringExtra("name");
        init();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);

        ImageView ic_back = findViewById(R.id.ic_back);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        person_number = findViewById(R.id.person_number);
        RelativeLayout plusView = findViewById(R.id.plusView);
        RelativeLayout minusView = findViewById(R.id.minusView);
        Button reserveBtn = findViewById(R.id.reserveBtn);

        backLayout.setOnClickListener(this);
        reserveBtn.setOnClickListener(this);
        plusView.setOnClickListener(this);
        minusView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String count = person_number.getText().toString().trim();
        int totalCount = Integer.parseInt(count);
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.plusView) {
            person_number.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.minusView) {
            if (totalCount != 0)
                person_number.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.reserveBtn) {
            reserveTank();
        }
    }

    public void reserveTank() {
        final String personNumber = person_number.getText().toString().trim();
        int aa = Integer.parseInt(personNumber);
        int aaa = Integer.parseInt(quantity);
        Log.e("quuuu", personNumber);
        if (TextUtils.isEmpty(personNumber)) {
            person_number.setError(getString(R.string.error_field_required));
            person_number.requestFocus();
        } else if (personNumber.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_required_no));
        } else if (aa > aaa) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_most) + " " + aaa);
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("quantity", personNumber)
                            .addFormDataPart("partner_id", partner_id)
                            .addFormDataPart("tank_id", tank_id)
                            .addFormDataPart("type_name", type_name);

                    RequestBody requestBody = builder.build();
                    reserveFishingInfo(requestBody);
                }
            }).start();
        }
    }

    public void reserveFishingInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(ReserveTankActivity.this);
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
                            + "book-jetski").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(ReserveTankActivity.this, " " +
                                                getString(R.string.we_send_the_request_to) + " " + name + " "
                                                + getString(R.string.and_we_will_send_contact_number_with_the_code_request) +
                                                " ", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
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
                                    AppErrorsManager.showErrorDialog(ReserveTankActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(ReserveTankActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(ReserveTankActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(ReserveTankActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }


}
