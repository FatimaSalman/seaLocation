
package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.apps.fatima.sealocation.adapter.TankDetailsAdapter;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.model.Image;
import com.apps.fatima.sealocation.model.Tanks;
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
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class TankDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager galleryPager;
    private boolean isPaused;
    private Timer timer;
    private List<Image> pictureList = new ArrayList<>();
    private List<Tanks> tanksList = new ArrayList<>();
    private RelativeLayout vUserProfileRoot, pageLayout;
    private TextView noContentImage, noContentTank, pageNameTxt;
    private Handler handler;
    private ProgressDialog progressDialog;
    private String image, name_ar, name_en, name, user_id, partner_id;
    private ImageSliderAdapter galleryViewPagerAdapter;
    private TankDetailsAdapter tankAdapter;
    private ImageView userImage;
    private TextView userNameTxt, cityNameTxt;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tank_details);
        handler = new Handler(Looper.getMainLooper());
        String id = getIntent().getStringExtra("id");
        user_id = getIntent().getStringExtra("user_id");
        partner_id = getIntent().getStringExtra("partner_id");
        image = getIntent().getStringExtra("image");
        name_ar = getIntent().getStringExtra("name_ar");
        name = getIntent().getStringExtra("name");
        name_en = getIntent().getStringExtra("name_en");
        getTankDetailsInfo(id);
        init();
        setUpGallerySlider();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout shareLayout = findViewById(R.id.shareLayout);
        shareLayout.setOnClickListener(this);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back_);
        backLayout.setOnClickListener(this);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        userImage = findViewById(R.id.userImage);
        progressbar = findViewById(R.id.progressbar);
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

        userNameTxt = findViewById(R.id.userNameTxt);
        userNameTxt.setText(name);

        cityNameTxt = findViewById(R.id.cityNameTxt);
        if (AppLanguage.getLanguage(this).equals("ar"))
            cityNameTxt.setText(name_ar);
        else
            cityNameTxt.setText(name_en);

        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);

        noContentImage = findViewById(R.id.noContentImage);

        noContentTank = findViewById(R.id.noContentTank);

        pageLayout = findViewById(R.id.pageLayout);

        pageNameTxt = findViewById(R.id.pageNameTxt);

        final RecyclerView recycleViewCourses = findViewById(R.id.recycleViewCourses);
        tankAdapter = new TankDetailsAdapter(this, tanksList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TankDetailsAdapter.MyViewHolder myViewHolder =
                        (TankDetailsAdapter.MyViewHolder) recycleViewCourses.findViewHolderForLayoutPosition(position);
                myViewHolder.tankType.getText().toString().trim();
                if (!AppPreferences.getBoolean(TankDetailsActivity.this, "loggedin"))
                    startActivity(new Intent(TankDetailsActivity.this, LoginActivity.class));
                else {
                    if (!TextUtils.equals(AppPreferences.getString(
                            TankDetailsActivity.this, "user_id"), user_id)) {
                        Intent intent = new Intent(TankDetailsActivity.this, ReserveTankActivity.class);
                        intent.putExtra("quantity", tanksList.get(position).getTankNumber());
                        intent.putExtra("tank_id", tanksList.get(position).getId());
                        intent.putExtra("partner_id", user_id);
                        intent.putExtra("name", name);
                        intent.putExtra("type_name", myViewHolder.tankType.getText().toString().trim());
                        startActivity(intent);
                    } else {
                        AppErrorsManager.showErrorDialog(TankDetailsActivity.this,
                                getString(R.string.you_donot_reserve_you_tank));
                    }

                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleViewCourses.addItemDecoration(new

                DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleViewCourses.setLayoutManager(mLayoutManager);
        recycleViewCourses.setItemAnimator(new

                DefaultItemAnimator());
        recycleViewCourses.setNestedScrollingEnabled(false);
        recycleViewCourses.setAdapter(tankAdapter);

        if (partner_id != null)
            getTankList(partner_id);
        else
            getTankList(user_id);

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

    public void getTankList(final String user_id) {
        tanksList.clear();
        InternetConnectionUtils.isInternetAvailable(TankDetailsActivity.this, new InternetAvailableCallback() {
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
                            + "partner-jetski/" + user_id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatList", response_data);
                        try {

                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            if (success.equals("[]")) {
                                Log.e("ddddd", "dddddddddddddddddddddddddddddddd");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentTank.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                JSONArray tripObject = new JSONArray(success);
                                for (int i = 0; i < tripObject.length(); i++) {
                                    JSONObject boatObject = tripObject.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String type_id = boatObject.getString("type");
                                    JSONObject typeObject = new JSONObject(type_id);
                                    String title_ar = typeObject.getString("title_ar");
                                    String title_en = typeObject.getString("title_en");
                                    String quantity = boatObject.getString("quantity");
                                    String hourly_price = boatObject.getString("hourly_price");
                                    Tanks tanks = new Tanks(id, title_ar, title_en, quantity, hourly_price);

                                    tanksList.add(tanks);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            noContentTank.setVisibility(View.GONE);
                                            progressDialog.hide();
                                            tankAdapter.notifyDataSetChanged();
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
                                    AppErrorsManager.showErrorDialog(TankDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(TankDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(TankDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(TankDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
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

    public void openDialog() {
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
                Intent intent = new Intent(TankDetailsActivity.this, ReserveFishingActivity.class);
                startActivity(intent);
                deleteDialog.dismiss();
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TankDetailsActivity.this, ReservePicnicActivity.class);
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
            FontManager.shareTextUrl(this, name, getString(R.string.tank));
        }
    }

    public void getTankDetailsInfo(final String id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(TankDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(TankDetailsActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "jetski/" + id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String user_info = successObject.getString("jetski");
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
                                JSONObject userObject = new JSONObject(user_info);
                                final String page_name = userObject.getString("page_name");
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
                                        if (page_name.equals("null") || page_name.isEmpty()) {
                                            pageLayout.setVisibility(View.GONE);
                                        } else {
                                            pageNameTxt.setText(page_name);
                                            pageLayout.setVisibility(View.VISIBLE);
                                        }

                                        if (image != null)
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

                                        userNameTxt.setText(name);

                                        if (AppLanguage.getLanguage(TankDetailsActivity.this).equals("ar"))
                                            cityNameTxt.setText(name_ar);
                                        else
                                            cityNameTxt.setText(name_en);
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
                                        Image imageTxt = new Image(url);
                                        pictureList.add(imageTxt);
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
                                    AppErrorsManager.showErrorDialog(TankDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(TankDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(TankDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(TankDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }
}
