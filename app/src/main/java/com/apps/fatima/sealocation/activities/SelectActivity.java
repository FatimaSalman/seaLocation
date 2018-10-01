package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login_btn, register_btn, select_language_btn, guest_btn;
    private Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        String languageToLoad = AppLanguage.getLanguage(this); // your language
        Log.e("ddd", languageToLoad);
        switch (languageToLoad) {
            case "العربية":
                myLocale = new Locale("ar");
                break;
            case "English":
                myLocale = new Locale("en");
                break;
            default:
                myLocale = new Locale(languageToLoad);
                break;
        }
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_select);
        Log.e("login", AppPreferences.getBoolean(this, "loggedin") + "");

        if (AppPreferences.getBoolean(this, "loggedin") ){
            startActivity(new Intent(this, MainActivity.class));
        }

        init();
        checkAndRequestPermissions();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        FontManager.applyFont(this, layout);
        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);
        select_language_btn = findViewById(R.id.select_language_btn);
        guest_btn = findViewById(R.id.guest_btn);
        login_btn.setOnClickListener(this);
        register_btn.setOnClickListener(this);
        guest_btn.setOnClickListener(this);
        select_language_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_btn) {
            Intent intent = new Intent(SelectActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.register_btn) {
            Intent intent = new Intent(SelectActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if (id == R.id.guest_btn) {
            Intent intent = new Intent(SelectActivity.this, MainActivity.class);
            intent.putExtra("type", "driving");
            intent.putExtra("without", "without");
//            AppPreferences.saveOrEditBoolean(this, "without", true);
            startActivity(intent);
        } else if (id == R.id.select_language_btn) {
            openDialog();
        }
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
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

    public void openDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_language_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.arabicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLang("ar");
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.englishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLang("en");
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        AppLanguage.saveLanguage(SelectActivity.this, lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }

    private void updateTexts() {
        login_btn.setText(getString(R.string.login));
        register_btn.setText(getString(R.string.register));
        guest_btn.setText(getString(R.string.browse_guest));
        select_language_btn.setText(getString(R.string.select_lanaguage));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null) {
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }
}
