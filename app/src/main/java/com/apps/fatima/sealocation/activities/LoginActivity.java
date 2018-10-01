package com.apps.fatima.sealocation.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.apps.fatima.sealocation.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText usernameEditText, passwordEditText;
    private ProgressDialog progressDialog;
    private Handler handler;
    private String refreshedToken;
    private String is_active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad = AppLanguage.getLanguage(this); // your language
        Locale locale;
//        Log.e("ddd", languageToLoad);
        switch (languageToLoad) {
            case "العربية":
                locale = new Locale("ar");
                break;
            case "English":
                locale = new Locale("en");
                break;
            default:
                locale = new Locale(languageToLoad);
                break;
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_login);
        handler = new Handler(Looper.getMainLooper());
        if (AppPreferences.getBoolean(this, "loggedin"))
            startActivity(new Intent(this, MainActivity.class));

        refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.e("tokenFireabase", refreshedToken);
        init();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);
        TextView welcomeTxt = findViewById(R.id.welcomeTxt);
        ImageView ic_back = findViewById(R.id.ic_back);
        Button login_btn = findViewById(R.id.login_btn);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        RelativeLayout registerNowLayout = findViewById(R.id.registerNowLayout);

        welcomeTxt.setTypeface(FontManager.getTypefaceTextInputBold(this));
        backLayout.setOnClickListener(this);
        registerNowLayout.setOnClickListener(this);
        login_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.login_btn) {
            loginUser();
        } else if (id == R.id.registerNowLayout) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    public void loginUser() {
        final String name = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            usernameEditText.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            passwordEditText.requestFocus();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("name", name)
                            .addFormDataPart("password", password)
                            .addFormDataPart("fcm_token", refreshedToken);
                    RequestBody requestBody = builder.build();
                    loginUserInfo(requestBody);
                }
            }).start();
        }
    }

    public void loginUserInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(LoginActivity.this);
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
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "login").post(requestBody)
//                            .addHeader("Authorization", "Bearer 6AcQL03QVQotSAYGHsZM9ZnCqnqKSVlS9WfuuQu0")
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            if (jsonObject.has("error")) {
                                final String msg = jsonObject.getString("error");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        if (AppLanguage.getLanguage(LoginActivity.this).equals("ar")) {
                                            AppErrorsManager.showErrorDialog(LoginActivity.this,
                                                    getString(R.string.username_or_passowrd_error));
                                        } else {
                                            AppErrorsManager.showErrorDialog(LoginActivity.this, msg);
                                        }
                                    }
                                });
                            } else if (jsonObject.has("success")) {
                                JSONObject successJson = jsonObject.getJSONObject("success");
                                final String token = successJson.getString("token");
                                final String user_type = successJson.getString("user_type");
                                AppPreferences.saveString(LoginActivity.this, "token", token);
                                if (jsonObject.has("user")) {
                                    JSONObject userJson = jsonObject.getJSONObject("user");
                                    is_active = userJson.getString("is_active");
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        if (!TextUtils.equals(is_active, "0")) {
                                            AppPreferences.saveOrEditBoolean(LoginActivity.this, "loggedin", true);
                                            AppPreferences.saveOrEditBoolean(LoginActivity.this, "without", false);
                                            AppPreferences.saveString(LoginActivity.this, "user_type", user_type);
                                            getUserInfo(token);
                                        } else {
                                            AppErrorsManager.showErrorDialog(LoginActivity.this, getString(R.string.waiting_admin_to_active));
                                        }
                                    }
                                });
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(LoginActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(LoginActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(LoginActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(LoginActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getUserInfo(final String token) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(LoginActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "user-details").get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("userData", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            final JSONObject successObject = new JSONObject(success);
                            String user_info = successObject.getString("user_info");
                            JSONObject userObject = new JSONObject(user_info);
                            Log.e("user_info", user_info);
                            String id = userObject.getString("id");
                            AppPreferences.saveString(LoginActivity.this, "user_id", id);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    if (successObject.has("partner_Seller") &&
                                            !successObject.has("partner_diver") &&
                                            !successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(LoginActivity.this, "type", "sellers");
                                    } else if (successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            !successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(LoginActivity.this, "type", "diver");
                                    } else if (!successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(LoginActivity.this, "type", "boat");
                                    } else if (!successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            !successObject.has("partner_boat") &&
                                            successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(LoginActivity.this, "type", "jetski");
                                    } else if (!successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            !successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(LoginActivity.this, "type", "supplier");
                                    } else if (!successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            !successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            successObject.has("partner_services")) {
                                        AppPreferences.saveString(LoginActivity.this, "type", "services");
                                    } else {
                                        AppPreferences.saveString(LoginActivity.this, "type", "other");
                                    }
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            });

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(LoginActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(LoginActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(LoginActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(LoginActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }
}
