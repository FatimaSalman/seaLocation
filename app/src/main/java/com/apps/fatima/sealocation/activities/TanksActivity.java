package com.apps.fatima.sealocation.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.adapter.ItemAdapter;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.model.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class TanksActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Item> itemList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemCityList = new ArrayList<>();
    private Handler handler;
    private String city_id = "";
    private TextView cityTxt, noContentSearch;
    private EditText usernameEditText, pageNameEditText;
    private Dialog alertDialog;
    private ProgressDialog progressDialog;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanks);
        handler = new Handler(Looper.getMainLooper());
        init();
        cityList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);
        RelativeLayout cityLayout = findViewById(R.id.cityLayout);
        cityLayout.setOnClickListener(this);
        Button searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);
        backLayout.setOnClickListener(this);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        cityTxt = findViewById(R.id.cityTxt);
        noContentSearch = findViewById(R.id.noContentSearch);
        usernameEditText = findViewById(R.id.usernameEditText);
        pageNameEditText = findViewById(R.id.pageNameEditText);

        usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchData();
                    return true;
                }
                return false;
            }
        });
        pageNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchData();
                    return true;
                }
                return false;
            }
        });

        RecyclerView recycleView = findViewById(R.id.recycleView);
        itemAdapter = new ItemAdapter(this, itemList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(TanksActivity.this, TankDetailsActivity.class);
                intent.putExtra("id", itemList.get(position).getId());
                intent.putExtra("user_id", itemList.get(position).getUser_id());
                intent.putExtra("name", itemList.get(position).getUserName());
                intent.putExtra("image", itemList.get(position).getImage());
                intent.putExtra("name_ar", itemList.get(position).getName_ar());
                intent.putExtra("name_en", itemList.get(position).getName_en());
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setAdapter(itemAdapter);
        getBoatList();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.cityLayout) {
            openWindowCity();
        } else if (id == R.id.searchBtn) {
            searchData();
        }
    }

    public void openWindowCity() {
        @SuppressLint("InflateParams")
        View popupView = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(popupView);
        EditText searchEditText = popupView.findViewById(R.id.searchEditText);
        final RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view1);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String query = charSequence.toString().toLowerCase().trim();
                final List<SpinnerItem> filteredList = new ArrayList<>();

                for (int j = 0; j < spinnerItemCityList.size(); j++) {

                    final String text = spinnerItemCityList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemCityList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(TanksActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCityList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCityList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(TanksActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(TanksActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(TanksActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        city_id = filteredList.get(position).getId();
                        cityTxt.setText(status);
                        cityTxt.setError(null);
                        alertDialog.dismiss();
                    }
                });
                recyclerView.setAdapter(spinnerAdapter);
                spinnerAdapter.notifyDataSetChanged();  // data set changed
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
//                filter(editable.toString());
            }
        });
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, spinnerItemCityList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(TanksActivity.this).equals("ar")) {
                    status = spinnerItemCityList.get(position).getTextA();
                } else {
                    status = spinnerItemCityList.get(position).getText();
                }
                city_id = spinnerItemCityList.get(position).getId();
                cityTxt.setText(status);
                cityTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
    }

    public void cityList() {
        spinnerItemCityList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "cities").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("cities");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String city_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("name_en");
                                String name_ar = jsonObject1.getString("name_ar");
                                SpinnerItem cityData = new SpinnerItem(city_id, name_en, name_ar);
                                spinnerItemCityList.add(cityData);
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(TanksActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(TanksActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(TanksActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(TanksActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void searchData() {
        itemList.clear();
        final String user_name = usernameEditText.getText().toString();
        final String page_name = pageNameEditText.getText().toString();
        final String city = cityTxt.getText().toString();
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(TanksActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });

        InternetConnectionUtils.isInternetAvailable(TanksActivity.this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "user-jetski-list?user_name=" + user_name + "&page_name=" + page_name + "&city=" + city_id).get()
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            if (success.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentSearch.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                JSONArray jsonArray = new JSONArray(success);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject boatObject = jsonArray.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String user_id = boatObject.getString("user_id");
                                    String name = boatObject.getString("name");
                                    String user_image = boatObject.getString("user_image");
                                    String city = boatObject.getString("city");
                                    String page_name = boatObject.getString("page_name");
                                    Item item;
                                    if (city.equals("null")) {
                                        item = new Item(name, city, id, user_image);
                                    } else {
                                        JSONObject cityObject = new JSONObject(city);
                                        String name_ar = cityObject.getString("name_ar");
                                        String name_en = cityObject.getString("name_en");
                                        item = new Item(name, city, id, user_image, name_ar, name_en, user_id, "", page_name);
                                    }
                                    itemList.add(item);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentSearch.setVisibility(View.GONE);
                                            itemAdapter.notifyDataSetChanged();
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
                                    AppErrorsManager.showErrorDialog(TanksActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(TanksActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(TanksActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(TanksActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void getBoatList() {
        itemList.clear();
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(TanksActivity.this);
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
                            + "user-jetski-list").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatList", response_data);
                        try {

                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            if (success.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentSearch.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                JSONArray jsonArray = new JSONArray(success);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject boatObject = jsonArray.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String user_id = boatObject.getString("user_id");
                                    String name = boatObject.getString("name");
                                    String user_image = boatObject.getString("user_image");
                                    String city = boatObject.getString("city");
                                    String page_name = boatObject.getString("page_name");
                                    Item item;
                                    if (city.equals("null")) {
                                        item = new Item(name, city, id, user_image);
                                    } else {
                                        JSONObject cityObject = new JSONObject(city);
                                        String name_ar = cityObject.getString("name_ar");
                                        String name_en = cityObject.getString("name_en");
                                        item = new Item(name, city, id, user_image, name_ar, name_en, user_id, "", page_name);
                                    }
                                    itemList.add(item);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            itemAdapter.notifyDataSetChanged();
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
                                    AppErrorsManager.showErrorDialog(TanksActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(TanksActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(TanksActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(TanksActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }
}
