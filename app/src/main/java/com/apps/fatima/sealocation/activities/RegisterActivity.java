package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad = AppLanguage.getLanguage(this); // your language
        Locale locale;
        Log.e("ddd", languageToLoad);
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
        setContentView(R.layout.activity_register);
        init();
        checkAndRequestPermissions();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout customerLayout = findViewById(R.id.customerLayout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout partnerLayout = findViewById(R.id.partnerLayout);
        RelativeLayout productsLayout = findViewById(R.id.productsLayout);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);
        ImageView ic_back1 = findViewById(R.id.ic_back1);
        ImageView ic_back2 = findViewById(R.id.ic_back2);
        ImageView ic_back3 = findViewById(R.id.ic_back3);
        ImageView ic_info = findViewById(R.id.ic_info);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
            ic_back1.setImageResource(R.drawable.ic_forward_arrow);
            ic_back2.setImageResource(R.drawable.ic_forward_arrow);
            ic_back3.setImageResource(R.drawable.ic_forward_arrow);
        }
        backLayout.setOnClickListener(this);
        ic_info.setOnClickListener(this);
        customerLayout.setOnClickListener(this);
        partnerLayout.setOnClickListener(this);
        productsLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.customerLayout) {
            Intent intent = new Intent(RegisterActivity.this, CustomerRegisterActivity.class);
            startActivity(intent);
        } else if (id == R.id.partnerLayout) {
            Intent intent = new Intent(RegisterActivity.this, RegisterPartnerActivity.class);
            startActivity(intent);
        } else if (id == R.id.productsLayout) {
            Intent intent = new Intent(RegisterActivity.this, RegisterProductsActivity.class);
            startActivity(intent);
        } else if (id == R.id.ic_info) {
            startActivity(new Intent(this, AboutUsActivity.class));
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
}
