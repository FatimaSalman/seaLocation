package com.apps.fatima.sealocation.activities;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import okhttp3.OkHttpClient;


public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {
    private Handler handler;
    private ProgressDialog progressDialog;
    private TextView txt, mobileTxt, emailTxt, addressTxt, rightTxt, companyTxt;
    private ScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_activity);
        handler = new Handler(Looper.getMainLooper());
        init();
        aboutUs();
    }


    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);
        txt = findViewById(R.id.txt);
        mobileTxt = findViewById(R.id.mobileTxt);
        emailTxt = findViewById(R.id.emailTxt);
        addressTxt = findViewById(R.id.addressTxt);
        rightTxt = findViewById(R.id.rightTxt);
        companyTxt = findViewById(R.id.companyTxt);
        scrollView = findViewById(R.id.scrollView);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        backLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        }
    }

    public void aboutUs() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.setVisibility(View.GONE);
                progressDialog = new ProgressDialog(AboutUsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "settings").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        assert response.body() != null;
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("settings", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String settings = successObject.getString("settings");
                            JSONArray settingsObject = new JSONArray(settings);

                            JSONObject complaintJson = settingsObject.getJSONObject(1);
                            final String value = complaintJson.getString("value");
                            final String value_en = complaintJson.getString("value_en");

                            JSONObject phoneJson = settingsObject.getJSONObject(2);
                            final String phone_value = phoneJson.getString("value");
                            final String phone_value_en = phoneJson.getString("value_en");

                            JSONObject emailJson = settingsObject.getJSONObject(3);
                            final String email_value = emailJson.getString("value");
                            final String email_value_en = emailJson.getString("value_en");

                            JSONObject addressJson = settingsObject.getJSONObject(4);
                            final String address_value = addressJson.getString("value");
                            final String address_value_en = addressJson.getString("value_en");

                            JSONObject rightJson = settingsObject.getJSONObject(5);
                            final String right_value = rightJson.getString("value");
                            final String right_value_en = rightJson.getString("value_en");

                            JSONObject companyJson = settingsObject.getJSONObject(6);
                            final String company_value = companyJson.getString("value");
                            final String company_value_en = companyJson.getString("value_en");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    scrollView.setVisibility(View.VISIBLE);
                                    if (AppLanguage.getLanguage(AboutUsActivity.this).equals("ar")) {
                                        txt.setText(value);
                                        mobileTxt.setText(phone_value);
                                        emailTxt.setText(email_value);
                                        addressTxt.setText(address_value);
                                        rightTxt.setText(right_value);
                                        companyTxt.setText(company_value);
                                    } else {
                                        txt.setText(value_en);
                                        mobileTxt.setText(phone_value_en);
                                        emailTxt.setText(email_value_en);
                                        addressTxt.setText(address_value_en);
                                        rightTxt.setText(right_value_en);
                                        companyTxt.setText(company_value_en);
                                    }
                                }
                            });
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AboutUsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(AboutUsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(AboutUsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(AboutUsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }
}
