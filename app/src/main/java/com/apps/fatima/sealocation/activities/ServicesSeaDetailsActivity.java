package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.adapter.ImageSliderAdapter;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppLocationManager;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.model.Image;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;

import static android.Manifest.permission.CALL_PHONE;

public class ServicesSeaDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private ViewPager galleryPager;
    private boolean isPaused;
    private Timer timer;
    private List<Image> pictureList = new ArrayList<>();
    private TextView serviceNameTxt, describeTxt, typeTxt, noContentImage, pageNameTxt;
    private Handler handler;
    private ProgressDialog progressDialog;
    private double latitude, longitude;
    private GoogleMap mMap;
    private String image, name_ar, name_en, name, email, mobile;
    private ImageSliderAdapter galleryViewPagerAdapter;
    private RelativeLayout vUserProfileRoot, pageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_details);

        handler = new Handler(Looper.getMainLooper());
        String id = getIntent().getStringExtra("id");
        image = getIntent().getStringExtra("image");
        name_ar = getIntent().getStringExtra("name_ar");
        name = getIntent().getStringExtra("name");
        name_en = getIntent().getStringExtra("name_en");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");
        init();
        getRequirementDetailsInfo(id);
        setUpGallerySlider();

    }

    @SuppressLint("SetTextI18n")
    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout shareLayout = findViewById(R.id.shareLayout);
        shareLayout.setOnClickListener(this);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);
        ImageView ic_email = findViewById(R.id.ic_email);
        ic_email.setColorFilter(getResources().getColor(R.color.colorWhite));
        ImageView ic_phone = findViewById(R.id.ic_phone);
        ic_phone.setColorFilter(getResources().getColor(R.color.colorWhite));

        backLayout.setOnClickListener(this);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }


        serviceNameTxt = findViewById(R.id.serviceNameTxt);
        describeTxt = findViewById(R.id.describeTxt);
        typeTxt = findViewById(R.id.typeTxt);
        pageLayout = findViewById(R.id.pageLayout);
        pageNameTxt = findViewById(R.id.pageNameTxt);

        ImageView userImage = findViewById(R.id.userImage);
        final ProgressBar progressbar = findViewById(R.id.progressbar);

        if (image.equals("null")) {
            userImage.setImageResource(R.drawable.img_user);
        } else {
            progressbar.setVisibility(View.VISIBLE);
            Picasso.get().load(FontManager.IMAGE_URL + image).into(userImage, new Callback() {
                @Override
                public void onSuccess() {
                    progressbar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    progressbar.setVisibility(View.GONE);
                }
            });
        }
        TextView userNameTxt = findViewById(R.id.userNameTxt);
        userNameTxt.setText(name);

        TextView cityNameTxt = findViewById(R.id.cityNameTxt);
        if (AppLanguage.getLanguage(this).equals("ar"))
            cityNameTxt.setText(name_ar);
        else
            cityNameTxt.setText(name_en);

        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        noContentImage = findViewById(R.id.noContentImage);
        TextView emailTxt = findViewById(R.id.emailTxt);
        if (email != null) {
            if (email.equals("null")) {
                emailTxt.setText(getString(R.string.no_email));
            } else {
                emailTxt.setText(email);
                emailTxt.setOnClickListener(this);
            }
        } else {
            emailTxt.setText(getString(R.string.no_email));
        }

        TextView phoneTxt = findViewById(R.id.phoneTxt);
        phoneTxt.setText(getString(R.string.code_no) + "" + mobile);
        phoneTxt.setOnClickListener(this);
        AppLocationManager.getInstance().getLocation(this, null);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
    }

    private void setUpGallerySlider() {
        galleryPager = findViewById(R.id.galleryViewPager);
        final ImageView nextButton = findViewById(R.id.nextButton);
        final ImageView backButton = findViewById(R.id.backButton);

        if (AppLanguage.getLanguage(this).equals("en")) {
            nextButton.setImageResource(R.drawable.ic_right_arrow_white);
            backButton.setImageResource(R.drawable.ic_left_arrow_white);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (galleryPager.getCurrentItem() == (Objects.requireNonNull(galleryPager.getAdapter()).getCount() - 1)) {
                    galleryPager.setCurrentItem(0);
                } else {
                    galleryPager.setCurrentItem(galleryPager.getCurrentItem() + 1);
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (galleryPager.getCurrentItem() == 0) {
                    galleryPager.setCurrentItem(Objects.requireNonNull(galleryPager.getAdapter()).getCount() - 1);
                } else {
                    galleryPager.setCurrentItem(galleryPager.getCurrentItem() - 1);
                }
            }
        });

        galleryViewPagerAdapter = new ImageSliderAdapter(this, pictureList);
        galleryPager.setAdapter(galleryViewPagerAdapter);
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isPaused) {
                            nextButton.performClick();
                        }
                    }
                });
            }
        }, 5000, 5000);
    }

    @Override
    public void finish() {
        if (timer != null) timer.cancel();
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(this, name, getString(R.string.sea_services));
        } else if (id == R.id.phoneTxt) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + getString(R.string.code_no) + mobile));
            if (ContextCompat.checkSelfPermission(this,
                    CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
                requestPermissions(new String[]{CALL_PHONE}, 1);
            }
        } else if (id == R.id.emailTxt) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + email));
            emailIntent.setPackage("com.google.android.gm");
            try {
                startActivity(emailIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getRequirementDetailsInfo(final String id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(ServicesSeaDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(ServicesSeaDetailsActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "services/" + id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String user_info = successObject.getString("services");
                            if (user_info.equals("null")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentImage.setVisibility(View.VISIBLE);
                                        vUserProfileRoot.setVisibility(View.GONE);
                                    }
                                });
                            } else {

                                final JSONObject userObject = new JSONObject(user_info);
                                final String location = userObject.getString("location");
                                final String page_name = userObject.getString("page_name");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (page_name.equals("null") || page_name.isEmpty()) {
                                            pageLayout.setVisibility(View.GONE);
                                        } else {
                                            pageNameTxt.setText(page_name);
                                            pageLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                final String activity_description = userObject.getString("activity_description");
                                String activity_type = userObject.getString("service_activity");
                                JSONObject typeObject = new JSONObject(activity_type);
                                final String title_ar = typeObject.getString("title_ar");
                                final String title_en = typeObject.getString("title_en");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        serviceNameTxt.setText(name);
                                        describeTxt.setText(activity_description);
                                        if (AppLanguage.getLanguage(ServicesSeaDetailsActivity.this).equals("ar"))
                                            typeTxt.setText(title_ar);
                                        else
                                            typeTxt.setText(title_en);

                                        if (!location.equals("")) {
                                            String[] namesList = location.split(",");
                                            String name1 = namesList[0];
                                            String name2 = namesList[1];
                                            latitude = Double.parseDouble(name1);
                                            longitude = Double.parseDouble(name2);
                                            final LatLng location_ = new LatLng(latitude, longitude);
                                            if (mMap != null) {
                                                Log.e("location", location);
                                                mMap.clear();
                                                mMap.addMarker(new MarkerOptions().position(location_).icon(BitmapDescriptorFactory.defaultMarker()));
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location_, 5));
                                            }
                                        }
                                    }
                                });

                                final String images = userObject.getString("images");
                                if (images.equals("[]")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentImage.setVisibility(View.VISIBLE);
                                            vUserProfileRoot.setVisibility(View.GONE);
                                        }
                                    });
                                } else {

                                    JSONArray jsonArray = new JSONArray(images);
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(j);
                                        String url = FontManager.IMAGE_URL + jsonObject2.getString("url");
                                        Log.e("url", url);
                                        Image image = new Image(url);
                                        pictureList.add(image);
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentImage.setVisibility(View.GONE);
                                            vUserProfileRoot.setVisibility(View.VISIBLE);
                                            galleryViewPagerAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }

                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(ServicesSeaDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(ServicesSeaDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(ServicesSeaDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(ServicesSeaDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng locationMark;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (mMap != null) mMap.setMyLocationEnabled(true);
            return;
        }


        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap = googleMap;

        if (latitude != 0 && longitude != 0) {
            locationMark = new LatLng(latitude, longitude);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(locationMark).icon(BitmapDescriptorFactory.defaultMarker()));
        } else {
            Location location = AppLocationManager.getInstance().getCurrentLocation();
            if (location != null) {
                locationMark = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                locationMark = new LatLng(25.247003, 44.395114);
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationMark, 5));

    }

}
