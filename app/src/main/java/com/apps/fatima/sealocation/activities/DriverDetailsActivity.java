package com.apps.fatima.sealocation.activities;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.apps.fatima.sealocation.adapter.OfferCourseAdapter;
import com.apps.fatima.sealocation.adapter.TripDivingAdapter;
import com.apps.fatima.sealocation.model.Course;
import com.apps.fatima.sealocation.model.Image;
import com.apps.fatima.sealocation.model.Trip;
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

public class DriverDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager galleryPager;
    private boolean isPaused;
    private Timer timer;
    private List<Image> pictureList = new ArrayList<>();
    private List<Course> courseList = new ArrayList<>();
    private List<Trip> tripList = new ArrayList<>();
    //    private PopupWindow pw;
    private TextView noContentImage, noContentCourses, noContentTrip, pageNameTxt;
    private Handler handler;
    private ProgressDialog progressDialog;
    private ImageSliderAdapter galleryViewPagerAdapter;
    private RelativeLayout vUserProfileRoot, pageLayout;
    private String image;
    private String name_ar;
    private String name_en;
    private String name;
    private String type;
    private String diver_id;
    private String user_id;
    private String diver_bio;
    private String title_ar;
    private String title_en;
    private String token;
    private OfferCourseAdapter courseAdapter;
    private TripDivingAdapter tripAdapter;
    private ImageView userImage;
    private TextView userNameTxt, cityNameTxt, levelNameTxt, driver_details;
    private ProgressBar progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");
        diver_id = getIntent().getStringExtra("id");
        image = getIntent().getStringExtra("image");
        name_ar = getIntent().getStringExtra("name_ar");
        name = getIntent().getStringExtra("name");
        name_en = getIntent().getStringExtra("name_en");
        user_id = getIntent().getStringExtra("user_id");
        diver_bio = getIntent().getStringExtra("diver_bio");
        title_ar = getIntent().getStringExtra("title_ar");
        title_en = getIntent().getStringExtra("title_en");
//        String mobile = getIntent().getStringExtra("mobile");
        init();
        getImages(diver_id);
        setUpGallerySlider();
    }

    public void init() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(DriverDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        RelativeLayout shareLayout = findViewById(R.id.shareLayout);
        shareLayout.setOnClickListener(this);

        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back_);
        userImage = findViewById(R.id.userImage);
        userNameTxt = findViewById(R.id.userNameTxt);
        cityNameTxt = findViewById(R.id.cityNameTxt);
        levelNameTxt = findViewById(R.id.levelNameTxt);
        driver_details = findViewById(R.id.driver_details);
        noContentImage = findViewById(R.id.noContentImage);
        pageNameTxt = findViewById(R.id.pageNameTxt);
        noContentCourses = findViewById(R.id.noContentCourses);
        noContentTrip = findViewById(R.id.noContentTrip);
        pageLayout = findViewById(R.id.pageLayout);
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
        progressbar = findViewById(R.id.progressbar);

        userNameTxt.setText(name);
        driver_details.setText(diver_bio);


        if (AppLanguage.getLanguage(this).equals("ar")) {
            cityNameTxt.setText(name_ar);
            if (title_ar != null)
                levelNameTxt.setText(title_ar);
            else
                levelNameTxt.setText(getString(R.string.no_level));
        } else {
            cityNameTxt.setText(name_en);
            if (title_en != null)
                levelNameTxt.setText(title_en);
            else
                levelNameTxt.setText(getString(R.string.no_level));
        }
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
        courseAdapter = new OfferCourseAdapter(this, courseList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!AppPreferences.getBoolean(DriverDetailsActivity.this, "loggedin"))
                    startActivity(new Intent(DriverDetailsActivity.this, LoginActivity.class));
                else {
                    if (!TextUtils.equals(AppPreferences.getString(
                            DriverDetailsActivity.this, "user_id"), user_id)) {
                        reserveNow(courseList.get(position).getId());
                    } else {
                        AppErrorsManager.showErrorDialog(DriverDetailsActivity.this,
                                getString(R.string.you_donot_reserve_you_course));
                    }
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleViewCourses.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleViewCourses.setLayoutManager(mLayoutManager);
        recycleViewCourses.setItemAnimator(new DefaultItemAnimator());
        recycleViewCourses.setNestedScrollingEnabled(false);
        recycleViewCourses.setAdapter(courseAdapter);
        getCourseList(diver_id);

        RecyclerView recycleViewTrip = findViewById(R.id.recycleViewTrip);
        tripAdapter = new TripDivingAdapter(this, tripList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String trip_type = tripList.get(position).getTripType();
                String trip_name = tripList.get(position).getTripName();
                if (trip_type.equals("3")) {
                    type = getString(R.string.diving_trip_beach);
                } else if (trip_type.equals("0") || trip_type.equals("1") || trip_type.equals("2")) {
                    type = getString(R.string.diving_trip_boat);
                }
                if (!AppPreferences.getBoolean(DriverDetailsActivity.this, "loggedin"))
                    startActivity(new Intent(DriverDetailsActivity.this, LoginActivity.class));
                else {
                    if (!TextUtils.equals(AppPreferences.getString(
                            DriverDetailsActivity.this, "user_id"), user_id)) {
                        String time = tripList.get(position).getTime();
                        String time_ = time.replace("  ", " ");

                        Locale loc = new Locale("en", "US");
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/M/yyyy", loc);
                        try {
                            Date date = format1.parse(tripList.get(position).getTiming());
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
                                reserveTripNow(tripList.get(position).getId(), type, trip_name);
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
                                        reserveTripNow(tripList.get(position).getId(), type, trip_name);
                                    } else if (hours == 0) {
                                        if (min1 > 0) {
                                            reserveTripNow(tripList.get(position).getId(), type, trip_name);
                                        } else {
                                            AppErrorsManager.showErrorDialog(DriverDetailsActivity.this,
                                                    getString(R.string.date_of_trip_expired));
                                        }
                                    } else {
                                        AppErrorsManager.showErrorDialog(DriverDetailsActivity.this,
                                                getString(R.string.date_of_trip_expired));
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                AppErrorsManager.showErrorDialog(DriverDetailsActivity.this,
                                        getString(R.string.date_of_trip_expired));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AppErrorsManager.showErrorDialog(DriverDetailsActivity.this,
                                getString(R.string.you_donot_reserve_you_trip_diving));
                    }
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager_ = new LinearLayoutManager(this);
        recycleViewTrip.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleViewTrip.setLayoutManager(mLayoutManager_);
        recycleViewTrip.setItemAnimator(new DefaultItemAnimator());
        recycleViewTrip.setNestedScrollingEnabled(false);

        recycleViewTrip.setAdapter(tripAdapter);
        getBoatTripList(diver_id);

    }

    private void setUpGallerySlider() {
        galleryPager = findViewById(R.id.galleryViewPager);
        final ImageView nextButton = findViewById(R.id.nextButton);
        final ImageView backButton = findViewById(R.id.backButton);

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

    public void getImages(final String diver_id) {
        pictureList.clear();
        InternetConnectionUtils.isInternetAvailable(DriverDetailsActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "diver/" + diver_id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String divers = successObject.getString("diver");
                            if (divers.equals("null")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentImage.setVisibility(View.VISIBLE);
                                        vUserProfileRoot.setVisibility(View.GONE);
                                        progressDialog.hide();
                                    }
                                });
                            } else {
                                JSONObject diversObject = new JSONObject(divers);
                                String images = diversObject.getString("images");
                                final String page_name = diversObject.getString("page_name");
                                String user = diversObject.getString("user");
                                final String diver_bio = diversObject.getString("diver_bio");
                                JSONObject user1Object = new JSONObject(user);
                                String cityObject = user1Object.getString("city");
                                JSONObject object = new JSONObject(cityObject);
                                final String name_ar = object.getString("name_ar");
                                final String name_en = object.getString("name_en");
                                final String image = user1Object.getString("user_image");
                                final String name = user1Object.getString("name");
                                String licenses = diversObject.getString("licenses");
                                JSONArray licensesArray = new JSONArray(licenses);
                                for (int j = 0; j < licensesArray.length(); j++) {
                                    JSONObject object1 = licensesArray.getJSONObject(j);
                                    title_en = object1.getString("title_en");
                                    title_ar = object1.getString("title_ar");
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        userNameTxt.setText(name);
                                        driver_details.setText(diver_bio);

                                        if (AppLanguage.getLanguage(DriverDetailsActivity.this).equals("ar")) {
                                            cityNameTxt.setText(name_ar);
                                            if (title_ar != null)
                                                levelNameTxt.setText(title_ar);
                                            else
                                                levelNameTxt.setText(getString(R.string.no_level));
                                        } else {
                                            cityNameTxt.setText(name_en);
                                            if (title_en != null)
                                                levelNameTxt.setText(title_en);
                                            else
                                                levelNameTxt.setText(getString(R.string.no_level));
                                        }
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
                                        if (page_name.equals("null") || page_name.isEmpty()) {
                                            pageLayout.setVisibility(View.GONE);
                                        } else {
                                            pageNameTxt.setText(page_name);
                                            pageLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                if (images.equals("[]")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            noContentImage.setVisibility(View.VISIBLE);
                                            vUserProfileRoot.setVisibility(View.GONE);
                                            progressDialog.hide();
                                        }
                                    });
                                } else {
                                    JSONArray jsonArray = new JSONArray(images);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String url = FontManager.IMAGE_URL + jsonObject1.getString("url");
                                        Log.e("url", url);
                                        Image imageTxt = new Image(url);
                                        pictureList.add(imageTxt);
                                    }
//
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
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getCourseList(final String diver_id) {
        courseList.clear();
        InternetConnectionUtils.isInternetAvailable(DriverDetailsActivity.this, new InternetAvailableCallback() {
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
                            + "partner-cources/" + diver_id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatList", response_data);
                        try {

                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String cources = successObject.getString("cources");
                            if (cources.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentCourses.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                JSONArray courcesObject = new JSONArray(cources);
                                for (int i = 0; i < courcesObject.length(); i++) {
                                    JSONObject courseObject = courcesObject.getJSONObject(i);
                                    String id = courseObject.getString("id");
//                                String diver_id = courseObject.getString("diver_id");
                                    String title = courseObject.getString("title");
                                    String period = courseObject.getString("period");
                                    String price = courseObject.getString("price");
                                    String requirements = courseObject.getString("requirements");
                                    String gears_available = courseObject.getString("gears_available");
                                    String gears_price = courseObject.getString("gears_price");
                                    Course course = new Course(id, title, requirements, period,
                                            price, gears_available, gears_price);
                                    courseList.add(course);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentCourses.setVisibility(View.GONE);
                                            courseAdapter.notifyDataSetChanged();
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
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getBoatTripList(final String diver_id) {
        tripList.clear();
        InternetConnectionUtils.isInternetAvailable(DriverDetailsActivity.this, new InternetAvailableCallback() {
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
                            + "diver-trips/" + diver_id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("tripListttt", response_data);
                        try {

                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String boat_trip = successObject.getString("trips");
                            if (boat_trip.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentTrip.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                JSONArray tripObject = new JSONArray(boat_trip);
                                for (int i = 0; i < tripObject.length(); i++) {
                                    JSONObject boatObject = tripObject.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String boat_id = boatObject.getString("boat_id");
                                    String diver_id = boatObject.getString("diver_id");
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
                                    String for_diver = boatObject.getString("for_diver");
                                    String gears_available = boatObject.getString("gears_available");
                                    String gears_price = boatObject.getString("gears_price");
                                    String trip_name = boatObject.getString("title");

                                    Trip trip = new Trip(id, boat_id, diver_id, boat_name,
                                            trip_type, start_date, start_location, trip_route,
                                            trip_duration, trip_terms, available_seats, trip_price,
                                            for_diver, gears_available, gears_price, trip_name, start_time);
                                    tripList.add(trip);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            noContentTrip.setVisibility(View.GONE);
                                            progressDialog.hide();
                                            tripAdapter.notifyDataSetChanged();
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
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void reserveNow(final String course_id) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("course", course_id + " // " + user_id);
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("course_id", course_id)
                        .addFormDataPart("partner_id", user_id);

                RequestBody requestBody = builder.build();
                reserveNowInfo(requestBody);
            }
        }).start();

    }

    public void reserveNowInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(DriverDetailsActivity.this);
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
                            + "book-course").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(DriverDetailsActivity.this, " " +
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
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void reserveTripNow(final String trip_id, final String type, final String name) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("trip_id", trip_id)
                        .addFormDataPart("trip_name", name)
                        .addFormDataPart("trip_type", type)
                        .addFormDataPart("partner_id", user_id);

                RequestBody requestBody = builder.build();
                reserveTripNowInfo(requestBody);
            }
        }).start();

    }

    public void reserveTripNowInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(DriverDetailsActivity.this);
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
                            + "store_now_trip").post(requestBody)
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
                                        AppErrorsManager.showSuccessDialog(DriverDetailsActivity.this, " " +
                                                        getString(R.string.we_send_the_request_to) + " " + name + " "
                                                        + getString(R.string.and_we_will_send_contact_number_with_the_code_request) + " "
                                                , new DialogInterface.OnClickListener() {
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
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(DriverDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(this, name, getString(R.string.driver));
        }
    }
}
