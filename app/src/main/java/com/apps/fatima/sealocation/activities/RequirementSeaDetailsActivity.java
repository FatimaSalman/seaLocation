package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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

public class RequirementSeaDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private ViewPager galleryPager;
    private boolean isPaused;
    private Timer timer;
    private List<Image> pictureList = new ArrayList<>();
    private TextView shopNameTxt, noContentImage, pageNameTxt, describeTxt;
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
        setContentView(R.layout.activity_requirement_details);

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

    public void init() {
        RelativeLayout shareLayout = findViewById(R.id.shareLayout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        shareLayout.setOnClickListener(this);

        RelativeLayout layout = findViewById(R.id.layout);
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
        if (AppLanguage.getLanguage(RequirementSeaDetailsActivity.this).equals("ar"))
            cityNameTxt.setText(name_ar);
        else
            cityNameTxt.setText(name_en);

        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        shopNameTxt = findViewById(R.id.shopNameTxt);
        pageLayout = findViewById(R.id.pageLayout);
        pageNameTxt = findViewById(R.id.pageNameTxt);
        describeTxt = findViewById(R.id.describeTxt);
        noContentImage = findViewById(R.id.noContentImage);
        TextView emailTxt = findViewById(R.id.emailTxt);
        if (email.equals("null"))
            emailTxt.setText(R.string.no_email);
        else
            emailTxt.setText(email);
        TextView phoneTxt = findViewById(R.id.phoneTxt);
        phoneTxt.setText(mobile);

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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(this, name, getString(R.string.requirement));
        }
    }

    public void getRequirementDetailsInfo(final String id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(RequirementSeaDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(RequirementSeaDetailsActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "suppliers/" + id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        assert response.body() != null;
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String services = successObject.getString("supplies");
                            if (services.equals("null")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentImage.setVisibility(View.VISIBLE);
                                        vUserProfileRoot.setVisibility(View.GONE);
                                    }
                                });
                            } else {
                                JSONObject jsonObject1 = new JSONObject(services);
                                final String shop_name = jsonObject1.getString("shop_name");
                                final String location = jsonObject1.getString("location");
                                final String page_name = jsonObject1.getString("page_name");
                                final String shop_description = jsonObject1.getString("shop_description");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        describeTxt.setText(shop_description);
                                        if (page_name.equals("null") || page_name.isEmpty()) {
                                            pageLayout.setVisibility(View.GONE);
                                        } else {
                                            pageNameTxt.setText(page_name);
                                            pageLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                final String images = jsonObject1.getString("images");
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
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
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
                                String[] namesList = location.split(",");
                                String name1 = namesList[0];
                                String name2 = namesList[1];
                                latitude = Double.parseDouble(name1);
                                longitude = Double.parseDouble(name2);
                                final LatLng location_ = new LatLng(latitude, longitude);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        shopNameTxt.setText(shop_name);
                                        if (mMap != null) {
                                            Log.e("location", location);
                                            mMap.clear();
                                            mMap.addMarker(new MarkerOptions().position(location_).icon(BitmapDescriptorFactory.defaultMarker()));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location_, 5));
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
                                    AppErrorsManager.showErrorDialog(RequirementSeaDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(RequirementSeaDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(RequirementSeaDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(RequirementSeaDetailsActivity.this, getString(R.string.error_network));
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
