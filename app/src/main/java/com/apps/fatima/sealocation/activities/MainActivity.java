package com.apps.fatima.sealocation.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.apps.fatima.sealocation.fragment.CustomerFragment;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.fragment.CategoriesFragment;
import com.apps.fatima.sealocation.fragment.ContactUsFragment;
import com.apps.fatima.sealocation.fragment.ProfileBoatFragment;
import com.apps.fatima.sealocation.fragment.ProfileDrivingFragment;
import com.apps.fatima.sealocation.fragment.ProfileFragment;
import com.apps.fatima.sealocation.fragment.ProfileProductFragment;
import com.apps.fatima.sealocation.fragment.ProfileRequirementFragment;
import com.apps.fatima.sealocation.fragment.ProfileServiceFragment;
import com.apps.fatima.sealocation.fragment.ProfileTankFragment;
import com.apps.fatima.sealocation.fragment.SearchFragment;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_search,
            R.drawable.ic_earth,
            R.drawable.ic_profile
    };
    String type;
    private ImageView ic_back;
    private Handler handler;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        init();
        handler = new Handler(Looper.getMainLooper());
        String without = getIntent().getStringExtra("without");
        if (!TextUtils.equals(without, "without")) {
            if (!AppPreferences.getBoolean(this, "loggedin")) {
                startActivity(new Intent(this, SelectActivity.class));
            } else {
                getUserInfo(AppPreferences.getString(this, "token"));
            }
        } else ic_back.setVisibility(View.VISIBLE);


        FontManager.hideKeyboard(this);
        type = getIntent().getStringExtra("type");
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


//        Intent in = getIntent();
//        Uri data = in.getData();
//        Log.e("data", data + "");

        if (getIntent() != null) {
            if (TextUtils.equals(getIntent().getStringExtra("boat"), "boat")) {
                Log.e("notify", "boat");
                viewPager.setCurrentItem(3);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(2)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(3)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorDark), PorterDuff.Mode.SRC_IN);
            } else if (TextUtils.equals(getIntent().getStringExtra("jetski"), "jetski")) {
                Log.e("notify", "jetski");
                viewPager.setCurrentItem(3);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(2)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(3)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorDark), PorterDuff.Mode.SRC_IN);
            } else if (TextUtils.equals(getIntent().getStringExtra("diver"), "diver")) {
                Log.e("notify", "jetski");
                viewPager.setCurrentItem(3);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(2)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(3)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorDark), PorterDuff.Mode.SRC_IN);
            } else {
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorDark), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(2)).getIcon()).setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                if (AppPreferences.getBoolean(this, "loggedin"))
                    Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(3)).getIcon()).setColorFilter(getResources()
                            .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
            }
        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                assert tab.getIcon() != null;
                tab.getIcon().setColorFilter(getResources()
                        .getColor(R.color.colorDark), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                assert tab.getIcon() != null;
                tab.getIcon().setColorFilter(getResources()
                        .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

//    public void forceCrash(View view) {
//        throw new RuntimeException("This is a crash");
//    }


    public void init() {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        ImageView ic_info = findViewById(R.id.ic_info);
        ic_back = findViewById(R.id.ic_back);

        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        ic_info.setOnClickListener(this);
        ic_back.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setupTabIcons() {
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(tabIcons[2]);
        if (AppPreferences.getBoolean(this, "loggedin"))
            Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(tabIcons[3]);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CategoriesFragment(), getString(R.string.categories));
        adapter.addFrag(new SearchFragment(), getString(R.string.search));
        adapter.addFrag(new ContactUsFragment(), getString(R.string.contact_us));
        if (AppPreferences.getBoolean(this, "loggedin")) {
            if (TextUtils.equals(AppPreferences.getString(this, "user_type"), "0")) {
                adapter.addFrag(new CustomerFragment(), getString(R.string.my_profile));
            } else {
                Log.e("type", AppPreferences.getString(this, "type"));
                if (TextUtils.equals(AppPreferences.getString(this, "type"), "sellers")) {
                    adapter.addFrag(new ProfileProductFragment(), getString(R.string.my_profile));
                } else if (TextUtils.equals(AppPreferences.getString(this, "type"), "diver")) {
                    adapter.addFrag(new ProfileDrivingFragment(), getString(R.string.my_profile));
                } else if (TextUtils.equals(AppPreferences.getString(this, "type"), "boat")) {
                    adapter.addFrag(new ProfileBoatFragment(), getString(R.string.my_profile));
                } else if (TextUtils.equals(AppPreferences.getString(this, "type"), "jetski")) {
                    adapter.addFrag(new ProfileTankFragment(), getString(R.string.my_profile));
                } else if (TextUtils.equals(AppPreferences.getString(this, "type"), "supplier")) {
                    adapter.addFrag(new ProfileRequirementFragment(), getString(R.string.my_profile));
                } else if (TextUtils.equals(AppPreferences.getString(this, "type"), "services")) {
                    adapter.addFrag(new ProfileServiceFragment(), getString(R.string.my_profile));
                } else if (TextUtils.equals(AppPreferences.getString(this, "type"), "other")) {
                    adapter.addFrag(new ProfileFragment(), getString(R.string.my_profile));
                }
            }
        }

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ic_info) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (id == R.id.ic_back) {
            finish();
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void getUserInfo(final String token) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        InternetConnectionUtils.isInternetAvailable(MainActivity.this, new InternetAvailableCallback() {
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
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            final JSONObject successObject = new JSONObject(success);

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
                                        AppPreferences.saveString(MainActivity.this, "type", "sellers");
                                    } else if (successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            !successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(MainActivity.this, "type", "diver");
                                    } else if (!successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(MainActivity.this, "type", "boat");
                                    } else if (!successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            !successObject.has("partner_boat") &&
                                            successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(MainActivity.this, "type", "jetski");
                                    } else if (!successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            !successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            successObject.has("partner_supplier") &&
                                            !successObject.has("partner_services")) {
                                        AppPreferences.saveString(MainActivity.this, "type", "supplier");
                                    } else if (!successObject.has("partner_diver") &&
                                            !successObject.has("partner_Seller") &&
                                            !successObject.has("partner_boat") &&
                                            !successObject.has("partner_jetski") &&
                                            !successObject.has("partner_supplier") &&
                                            successObject.has("partner_services")) {
                                        AppPreferences.saveString(MainActivity.this, "type", "services");
                                    } else {
                                        AppPreferences.saveString(MainActivity.this, "type", "other");
                                    }
                                    if (successObject.has("partner_boat")) {
                                        AppPreferences.saveString(MainActivity.this,
                                                "partner_boat", "partner_boat");
                                    }
                                    if (successObject.has("partner_jetski")) {
                                        AppPreferences.saveString(MainActivity.this,
                                                "partner_jetski", "partner_jetski");
                                    }
                                    if (successObject.has("partner_diver")) {
                                        AppPreferences.saveString(MainActivity.this,
                                                "partner_diver", "partner_diver");
                                    }
                                    if (successObject.has("partner_supplier")) {
                                        AppPreferences.saveString(MainActivity.this,
                                                "partner_supplier", "partner_supplier");
                                    }
                                    if (successObject.has("partner_services")) {
                                        AppPreferences.saveString(MainActivity.this,
                                                "partner_services", "partner_services");
                                    }
                                    if (successObject.has("partner_Seller")) {
                                        AppPreferences.saveString(MainActivity.this,
                                                "partner_Seller", "partner_Seller");
                                    }
                                    if (TextUtils.equals(getIntent().getStringExtra("profile"), "profile")) {
                                        viewPager.setCurrentItem(3);
                                        Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getIcon()).setColorFilter(getResources()
                                                .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                                        Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(1)).getIcon()).setColorFilter(getResources()
                                                .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                                        Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(2)).getIcon()).setColorFilter(getResources()
                                                .getColor(R.color.colorGray), PorterDuff.Mode.SRC_IN);
                                        Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(3)).getIcon()).setColorFilter(getResources()
                                                .getColor(R.color.colorDark), PorterDuff.Mode.SRC_IN);
                                    }
                                }
                            });

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(MainActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(MainActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(MainActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(MainActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }


}