package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppLocationManager;
import com.apps.fatima.sealocation.manager.FontManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;

public class BigSelectMapShowActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mMap;
    private Location mLocation;
    private double latitude, longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_map);
        String location = getIntent().getStringExtra("location");
        String[] namesList = location.split(",");
        String name1 = namesList[0];
        String name2 = namesList[1];
        latitude = Double.parseDouble(name1);
        longitude = Double.parseDouble(name2);
        setupViews();
        getCurrentLocation();
    }

    public void setupViews() {

        LinearLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);

        ImageView ic_back = findViewById(R.id.ic_back);

        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        ImageView saveButton = findViewById(R.id.saveImg);
        saveButton.setVisibility(View.GONE);
        backLayout.setOnClickListener(this);

        AppLocationManager.getInstance().getLocation(this, null);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
        SegmentedGroup segmentedGroup = findViewById(R.id.segmentedGroup);

        if (segmentedGroup != null) {
            segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    switch (checkedId) {
                        case R.id.satelliteButton:
                            changeMode(2);
                            break;
                        case R.id.mapButton:
                            changeMode(1);
                            break;
                    }
                }
            });
        }
    }

    private void getCurrentLocation() {
        mLocation = AppLocationManager.getInstance().getCurrentLocation();
        Log.e("location", mLocation + "");
        if (mLocation == null) {
            AppLocationManager.getInstance().getLocation(getApplicationContext(), new AppLocationManager.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    mLocation = location;
                    Log.e("location///", location + "");
                }

                @Override
                public void onError() {
                }
            });
        }
    }

    private void changeMode(int mode) {
        if (mMap != null) {
            mMap.setMapType(mode);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLng locationMark;

        if (FontManager.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            googleMap.setMyLocationEnabled(true);
        } else {
            googleMap.setMyLocationEnabled(false);
        }
        mMap = googleMap;

        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                addMarker(latLng);
//            }
//        });
//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                addMarker(latLng);
//            }
//        });
//
//        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                LatLng midLatLng = googleMap.getCameraPosition().target;
//                addMarker(midLatLng);
//            }
//        });
//        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
//            @Override
//            public void onCameraMove() {
//                LatLng midLatLng = googleMap.getCameraPosition().target;
//                addMarker(midLatLng);
//
//            }
//        });

        Location location = AppLocationManager.getInstance().getCurrentLocation();
        if (location != null) {
            new LatLng(location.getLatitude(), location.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(locationMark).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
        }
        if (latitude != 0 && longitude != 0) {
            locationMark = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(locationMark).icon(BitmapDescriptorFactory.defaultMarker()));
        } else {
            locationMark = new LatLng(25.247003, 44.395114);
            mMap.addMarker(new MarkerOptions().position(locationMark).icon(BitmapDescriptorFactory.defaultMarker()));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationMark, 5));
    }

    private void addMarker(final LatLng latLng) {
        mMap.clear();
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        Marker mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.i_am_here)).snippet(latitude + " , " + longitude)
                .icon(BitmapDescriptorFactory.defaultMarker()).draggable(true));
        mMarker.showInfoWindow();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == 0) {
            AppLocationManager.getInstance().getLocation(getApplicationContext(), null);
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        }
    }
}
