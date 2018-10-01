package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.adapter.ImageSliderAdapter;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.adapter.BoatDetailsAdapter;
import com.apps.fatima.sealocation.adapter.ComingEventsDetailsAdapter;
import com.apps.fatima.sealocation.model.Boat;
import com.apps.fatima.sealocation.model.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class BoatDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager galleryPager;
    private boolean isPaused;
    private Timer timer;
    private List<Image> pictureList = new ArrayList<>();
    private List<Boat> boatList = new ArrayList<>();
    private List<Boat> boatEventList = new ArrayList<>();
    private Handler handler;
    private ProgressDialog progressDialog;
    private String image;
    private String name_ar;
    private String name_en;
    private String name;
    private String user_id;
    private String token;
    private String partner_id;
    private String page_name;
    private ImageSliderAdapter galleryViewPagerAdapter;
    private BoatDetailsAdapter boatAdapter;
    private ComingEventsDetailsAdapter comingEventsAdapter;
    private TextView noContentEvent, noContentImage, noContentBoat, pageNameTxt;
    private RelativeLayout vUserProfileRoot, pageLayout;
    private String boatName;
    private ImageView userImage;
    private TextView userNameTxt, cityNameTxt;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_details);
        handler = new Handler(Looper.getMainLooper());
//        String token = AppPreferences.getString(this, "token");
        String id = getIntent().getStringExtra("id");
        Log.e("id////", id);
        image = getIntent().getStringExtra("image");
        name_ar = getIntent().getStringExtra("name_ar");
        name = getIntent().getStringExtra("name");
        page_name = getIntent().getStringExtra("page_name");
        name_en = getIntent().getStringExtra("name_en");
        user_id = getIntent().getStringExtra("user_id");
//        String mobile = getIntent().getStringExtra("mobile");
        partner_id = getIntent().getStringExtra("partner_id");
        if (partner_id != null)
            Log.e("partner_id////", partner_id);
        token = AppPreferences.getString(this, "token");

        init();
        setUpGallerySlider();
        getBoatDetailsInfo(id);
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back_);
        userImage = findViewById(R.id.userImage);
        userNameTxt = findViewById(R.id.userNameTxt);
        cityNameTxt = findViewById(R.id.cityNameTxt);
        pageNameTxt = findViewById(R.id.pageNameTxt);
        pageLayout = findViewById(R.id.pageLayout);
        noContentEvent = findViewById(R.id.noContentEvent);
        noContentImage = findViewById(R.id.noContentImage);
        noContentBoat = findViewById(R.id.noContentBoat);
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        progressbar = findViewById(R.id.progressbar);
        RelativeLayout shareLayout = findViewById(R.id.shareLayout);
        shareLayout.setOnClickListener(this);

        userNameTxt.setText(name);

        if (AppLanguage.getLanguage(this).equals("ar"))
            cityNameTxt.setText(name_ar);
        else
            cityNameTxt.setText(name_en);
        if (image != null)
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
        backLayout.setOnClickListener(this);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        RecyclerView recycleViewCourses = findViewById(R.id.recycleViewCourses);
        boatAdapter = new BoatDetailsAdapter(this, boatList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                Log.e("partener_id", partner_id);
                Log.e("user_id", AppPreferences.getString(BoatDetailsActivity.this, "user_id"));
                if (!AppPreferences.getBoolean(BoatDetailsActivity.this, "loggedin"))
                    startActivity(new Intent(BoatDetailsActivity.this, LoginActivity.class));
                else {
                    if (id == R.id.btnRequestNow) {
                        if (!TextUtils.equals(AppPreferences.getString(
                                BoatDetailsActivity.this, "user_id"), partner_id)) {
                            reserveNow(boatList.get(position).getId());
                        } else {
                            AppErrorsManager.showErrorDialog(BoatDetailsActivity.this,
                                    getString(R.string.you_donot_reserve_you_boat));
                        }
                    } else if (id == R.id.btnRequestLater) {
                        if (!TextUtils.equals(AppPreferences.getString(
                                BoatDetailsActivity.this, "user_id"), partner_id)) {
                            String passangerNumber = boatList.get(position).getPassNumber();
                            openDialog(passangerNumber, boatList.get(position).getId());
                        } else {
                            AppErrorsManager.showErrorDialog(BoatDetailsActivity.this,
                                    getString(R.string.you_donot_reserve_you_boat));
                        }

                    }
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleViewCourses.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleViewCourses.setLayoutManager(mLayoutManager);
        recycleViewCourses.setItemAnimator(new DefaultItemAnimator());
        recycleViewCourses.setNestedScrollingEnabled(false);
        recycleViewCourses.setAdapter(boatAdapter);
        if (partner_id != null) {
            getBoatList(partner_id);
        } else {
            getBoatList(user_id);
        }

        RecyclerView recycleViewTrip = findViewById(R.id.recycleViewTrip);
        comingEventsAdapter = new ComingEventsDetailsAdapter(this, boatEventList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!TextUtils.equals(AppPreferences.getString(
                        BoatDetailsActivity.this, "user_id"), partner_id)) {
                    String time = boatEventList.get(position).getDate();
                    String time_ = time.replace("  ", " ");

                    Locale loc = new Locale("en", "US");
                    SimpleDateFormat format1 = new SimpleDateFormat("dd/M/yyyy", loc);
                    try {
                        Date date = format1.parse(boatEventList.get(position).getTiming());
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        Date date1 = cal.getTime();
                        Log.e("diff", "Date1" + date1);
                        Log.e("diff", "Date" + date);
                        int diff = date.compareTo(date1);
                        Log.e("difff", diff + "");
                        if (diff > 0) {
                            reserveTripNow(boatEventList.get(position).getId(), boatEventList.get(position).getTrip_id());
                        } else if (diff == 0) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm aa", loc);
                            DateFormat df = new SimpleDateFormat("HH:mm aa", loc);
                            Date startDate;
                            try {
                                Date today = Calendar.getInstance().getTime();
                                String reportDate = df.format(today);
                                Log.e("repotdate", reportDate);
                                startDate = simpleDateFormat.parse(reportDate);

                                Date endDate = simpleDateFormat.parse(time_);

                                long difference = endDate.getTime() - startDate.getTime();
                                if (difference < 0) {
                                    Date dateMax = simpleDateFormat.parse("24:00 AM");
                                    Date dateMin = simpleDateFormat.parse("00:00 PM");
                                    difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
                                }
                                int days = (int) (difference / (1000 * 60 * 60 * 24));
                                int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                                int min1 = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                                Log.e("log_tag", "Hours: " + hours + ", Mins: " + min1);
                                if (hours > 0) {
                                    reserveTripNow(boatEventList.get(position).getId(), boatEventList.get(position).getTrip_id());
                                } else if (hours == 0) {
                                    if (min1 > 0) {
                                        reserveTripNow(boatEventList.get(position).getId(), boatEventList.get(position).getTrip_id());
                                    } else {
                                        AppErrorsManager.showErrorDialog(BoatDetailsActivity.this,
                                                getString(R.string.date_of_trip_expired));
                                    }
                                } else {
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this,
                                            getString(R.string.date_of_trip_expired));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AppErrorsManager.showErrorDialog(BoatDetailsActivity.this,
                                    getString(R.string.date_of_trip_expired));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this,
                            getString(R.string.you_donot_reserve_you_trip_boat));
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager_ = new LinearLayoutManager(this);
        recycleViewTrip.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleViewTrip.setLayoutManager(mLayoutManager_);
        recycleViewTrip.setItemAnimator(new DefaultItemAnimator());
        recycleViewTrip.setNestedScrollingEnabled(false);
        recycleViewTrip.setAdapter(comingEventsAdapter);
        if (partner_id != null) {
            getBoatTripList(partner_id);
        } else {
            getBoatTripList(user_id);
        }
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

    public void openDialog(final String passengerNumber, final String id) {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        TextView text = dialogView.findViewById(R.id.text);
        text.setText(getString(R.string.trip_type));
        Button btnOne = dialogView.findViewById(R.id.galleryBtn);
        btnOne.setText(R.string.fishing_diving);
        Button btnTwo = dialogView.findViewById(R.id.cameraBtn);
        btnTwo.setText(getString(R.string.picnic));

        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatDetailsActivity.this, ReserveFishingActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("name", name);
                intent.putExtra("partner_id", partner_id);
                intent.putExtra("passengerNumber", passengerNumber);
                intent.putExtra("boat_id", id);
                intent.putExtra("boat_name", boatName);
                startActivity(intent);
                deleteDialog.dismiss();
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatDetailsActivity.this, ReservePicnicActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("name", name);
                intent.putExtra("partner_id", partner_id);
                intent.putExtra("passengerNumber", passengerNumber);
                intent.putExtra("boat_id", id);
                intent.putExtra("boat_name", boatName);
                startActivity(intent);
                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(this, name, getString(R.string.boat));
        }
    }

    public void getBoatDetailsInfo(final String id) {
        pictureList.clear();
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(BoatDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(BoatDetailsActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "boat/" + id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String user_info = successObject.getString("boats");
                            JSONObject userObject = new JSONObject(user_info);
                            Log.e("user_info", user_info);
                            partner_id = userObject.getString("user_id");
                            page_name = userObject.getString("page_name");
                            String user = userObject.getString("user");
                            JSONObject user1Object = new JSONObject(user);
                            String cityObject = user1Object.getString("city");
                            JSONObject object = new JSONObject(cityObject);
                            final String name_ar = object.getString("name_ar");
                            final String name_en = object.getString("name_en");
                            final String image = user1Object.getString("user_image");
                            final String name = user1Object.getString("name");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    userNameTxt.setText(name);
                                    if (page_name.equals("null") || page_name.isEmpty()) {
                                        pageLayout.setVisibility(View.GONE);
                                    } else {
                                        pageNameTxt.setText(page_name);
                                        pageLayout.setVisibility(View.VISIBLE);
                                    }
                                    if (AppLanguage.getLanguage(BoatDetailsActivity.this).equals("ar"))
                                        cityNameTxt.setText(name_ar);
                                    else
                                        cityNameTxt.setText(name_en);
                                    if (image.equals("null")) {
                                        userImage.setImageResource(R.drawable.img_user);
                                    } else {
                                        progressbar.setVisibility(View.VISIBLE);
                                        Picasso.get()
                                                .load(FontManager.IMAGE_URL + image)
                                                .into(userImage, new Callback() {
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
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String url = FontManager.IMAGE_URL + jsonObject1.getString("url");
                                    Log.e("url", url);
                                    Image imagesTxt = new Image(url);
                                    pictureList.add(imagesTxt);
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

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getBoatList(final String user_id) {
        boatList.clear();
        InternetConnectionUtils.isInternetAvailable(BoatDetailsActivity.this, new InternetAvailableCallback() {
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
                            + "boat-list/" + user_id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatList", response_data);
                        try {

                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            if (TextUtils.equals(success, "[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentBoat.setVisibility(View.VISIBLE);
                                        progressDialog.hide();
                                    }
                                });
                            } else {
                                JSONArray successObject = new JSONArray(success);
                                for (int i = 0; i < successObject.length(); i++) {
                                    JSONObject boatObject = successObject.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    boatName = boatObject.getString("name");
                                    String passengerNumber = boatObject.getString("passengers");
                                    String lenght = boatObject.getString("lenght");
                                    String width = boatObject.getString("width");
                                    String hourly_price = boatObject.getString("hourly_price");
                                    String location = boatObject.getString("location");
                                    String width_unit = boatObject.getString("width_unit");
                                    JSONObject widthObject = new JSONObject(width_unit);
                                    String widthMeasureEn = widthObject.getString("name_en");
                                    String widthMeasureAr = widthObject.getString("name_ar");
                                    String lenght_unit = boatObject.getString("lenght_unit");
                                    JSONObject heightObject = new JSONObject(lenght_unit);
                                    String heightMeasureEn = heightObject.getString("name_en");
                                    String heightMeasureAr = heightObject.getString("name_ar");

                                    Boat boat = new Boat(id, boatName, width, lenght, hourly_price,
                                            passengerNumber, location, widthMeasureEn,
                                            widthMeasureAr, heightMeasureEn, heightMeasureAr);
                                    boatList.add(boat);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentBoat.setVisibility(View.GONE);
                                            boatAdapter.notifyDataSetChanged();
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
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getBoatTripList(final String user_id) {
        boatEventList.clear();
        InternetConnectionUtils.isInternetAvailable(BoatDetailsActivity.this, new InternetAvailableCallback() {
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
                            + "partner-trips/" + user_id).get()
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatList", response_data);
                        try {

                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String boat_trip = successObject.getString("trips");
                            if (boat_trip.equals("[]")) {
                                Log.e("ddddd", "dddddddddddddddddddddddddddddddd");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentEvent.setVisibility(View.VISIBLE);
                                    }
                                });

                            } else {
                                JSONArray tripObject = new JSONArray(boat_trip);

                                for (int i = 0; i < tripObject.length(); i++) {
                                    JSONObject boatObject = tripObject.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String boat_id = boatObject.getString("boat_id");
                                    String start_date = boatObject.getString("start_date");
                                    String start_time = boatObject.getString("start_time");
                                    String start_location = boatObject.getString("start_location");
                                    String trip_route = boatObject.getString("trip_route");
                                    String trip_duration = boatObject.getString("trip_duration");
                                    String trip_terms = boatObject.getString("trip_terms");
                                    String trip_price = boatObject.getString("trip_price");
                                    String available_seats = boatObject.getString("available_seats");
                                    String boat_name = boatObject.getString("boat_name");
                                    String trip_type = boatObject.getString("trip_type");

                                    Boat boat = new Boat(id, boat_id, boat_name, trip_type, start_date,
                                            start_location, trip_route, trip_duration, trip_terms,
                                            available_seats, trip_price, start_time);

                                    boatEventList.add(boat);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            noContentEvent.setVisibility(View.GONE);
                                            progressDialog.hide();
                                            comingEventsAdapter.notifyDataSetChanged();
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
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void reserveNow(final String boat_id) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("boat_id", boat_id)
                        .addFormDataPart("partner_id", partner_id)
                        .addFormDataPart("user_id", user_id);

                RequestBody requestBody = builder.build();
                reserveNowInfo(requestBody);
            }
        }).start();

    }

    public void reserveNowInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(BoatDetailsActivity.this);
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
                            + "book-boat-now").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(BoatDetailsActivity.this, " " +
                                                getString(R.string.we_send_the_request_to) + " " + name + " "
                                                + getString(R.string.and_we_will_send_contact_number_with_the_code_request) + " ", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
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
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void reserveTripNow(final String boat_id, final String trip_id) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("boat_id", boat_id)
                        .addFormDataPart("trip_id", trip_id)
                        .addFormDataPart("partner_id", partner_id)
                        .addFormDataPart("user_id", user_id);

                RequestBody requestBody = builder.build();
                reserveTripNowInfo(requestBody);
            }
        }).start();

    }

    public void reserveTripNowInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(BoatDetailsActivity.this);
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
                            + "store_trip_now").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(BoatDetailsActivity.this,
                                                " " + getString(R.string.we_send_the_request_to) + " " + name + " "
                                                        + getString(R.string.and_we_will_send_contact_number_with_the_code_request) +
                                                        " ", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
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
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(BoatDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }


}
