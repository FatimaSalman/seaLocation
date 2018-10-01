package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.adapter.ImageSliderAdapter;
import com.apps.fatima.sealocation.adapter.ProductDetailsAdapter;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.model.Image;
import com.apps.fatima.sealocation.model.Product;
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

import static android.Manifest.permission.CALL_PHONE;

public class SellProductsDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager galleryPager;
    private boolean isPaused;
    private Timer timer;
    private List<Image> pictureList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private TextView noContentImage, noProductImage, pageNameTxt;
    private Handler handler;
    private ProgressDialog progressDialog;
    //    private double latitude, longitude;
//    private GoogleMap mMap;
    private ImageSliderAdapter galleryViewPagerAdapter;
    private String image, email, mobile, name_ar, name_en, name, user_id;
    private RelativeLayout vUserProfileRoot, pageLayout;
    private ProductDetailsAdapter productAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(SellProductsDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        String id = getIntent().getStringExtra("id");
        user_id = getIntent().getStringExtra("user_id");
        image = getIntent().getStringExtra("image");
        name_ar = getIntent().getStringExtra("name_ar");
        name = getIntent().getStringExtra("name");
        name_en = getIntent().getStringExtra("name_en");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");
        init();
        getImages(id);
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

        noContentImage = findViewById(R.id.noContentImage);
        noProductImage = findViewById(R.id.noProductImage);
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
        vUserProfileRoot = findViewById(R.id.vUserProfileRoot);
//        AppLocationManager.getInstance().getLocation(this, null);
//        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);

        RecyclerView recycleView = findViewById(R.id.recycleView);
        productAdapter = new ProductDetailsAdapter(this, productList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setAdapter(productAdapter);
        getProductList(user_id);
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
            @Override
            public void onClick(View v) {

                if (galleryPager.getCurrentItem() == (galleryPager.getAdapter().getCount() - 1)) {
                    galleryPager.setCurrentItem(0);
                } else {
                    galleryPager.setCurrentItem(galleryPager.getCurrentItem() + 1);
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (galleryPager.getCurrentItem() == 0) {
                    galleryPager.setCurrentItem(galleryPager.getAdapter().getCount() - 1);
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

    public void getImages(final String id) {
        pictureList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "sellers/" + id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = response.body().string();
                        Log.e("aaaservices", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String divers = successObject.getString("sellers");
                            JSONObject diversObject = new JSONObject(divers);
                            final String page_name = diversObject.getString("page_name");
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
                            String images = diversObject.getString("images");
                            if (images.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentImage.setVisibility(View.VISIBLE);
                                        vUserProfileRoot.setVisibility(View.GONE);
                                    }
                                });
                            } else {
                                JSONArray jsonArray = new JSONArray(images);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String id = jsonObject1.getString("id");
                                    String partner_id = jsonObject1.getString("partner_id");
                                    String object_id = jsonObject1.getString("object_id");
                                    String object_tb = jsonObject1.getString("object_tb");
                                    String url = FontManager.IMAGE_URL + jsonObject1.getString("url");
                                    Image image = new Image(url, id, partner_id, object_id, object_tb);
                                    pictureList.add(image);
                                }
//
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentImage.setVisibility(View.GONE);
                                        vUserProfileRoot.setVisibility(View.VISIBLE);
                                        galleryViewPagerAdapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(SellProductsDetailsActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(SellProductsDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(SellProductsDetailsActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(SellProductsDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(this, name, getString(R.string.sell_sea_products));
        } else if (id == R.id.emailTxt) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + email));
            emailIntent.setPackage("com.google.android.gm");
            try {
                startActivity(emailIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.phoneTxt) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + getString(R.string.code_no) + mobile));
            if (ContextCompat.checkSelfPermission(this,
                    CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
                requestPermissions(new String[]{CALL_PHONE}, 1);
            }
        }
    }

    public void getProductList(final String user_id) {
        productList.clear();
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
                            + "sellers-products/" + user_id).get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("productList", response_data);
                        try {
                            Log.e("user_id", user_id);
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String sellers = successObject.getString("sellers");
                            if (sellers.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noProductImage.setVisibility(View.VISIBLE);
                                    }
                                });

                            } else {
                                JSONArray sellersObject = new JSONArray(sellers);

                                for (int i = 0; i < sellersObject.length(); i++) {
                                    JSONObject sellerObject = sellersObject.getJSONObject(i);
                                    String id = sellerObject.getString("id");
                                    String type_id = sellerObject.getString("product_type");
                                    String product_name = sellerObject.getString("product_name");
                                    String product_price = sellerObject.getString("product_price");
                                    JSONObject typeObject = new JSONObject(type_id);
                                    String type_ar = typeObject.getString("title_ar");
                                    String type_en = typeObject.getString("title_en");
                                    String product_description = sellerObject.getString("product_description");


                                    Product product = new Product(id, product_name, type_ar,
                                            type_en, product_description, product_price);

                                    productList.add(product);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            noProductImage.setVisibility(View.GONE);
                                            progressDialog.hide();
                                            productAdapter.notifyDataSetChanged();
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
                                    AppErrorsManager.showErrorDialog(SellProductsDetailsActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(SellProductsDetailsActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(SellProductsDetailsActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(SellProductsDetailsActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

}
