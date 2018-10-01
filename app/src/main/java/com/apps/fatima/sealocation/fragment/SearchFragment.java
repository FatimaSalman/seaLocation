package com.apps.fatima.sealocation.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.activities.BoatDetailsActivity;
import com.apps.fatima.sealocation.activities.DriverDetailsActivity;
import com.apps.fatima.sealocation.activities.RequirementSeaDetailsActivity;
import com.apps.fatima.sealocation.activities.SellProductsDetailsActivity;
import com.apps.fatima.sealocation.activities.ServicesSeaDetailsActivity;
import com.apps.fatima.sealocation.activities.TankDetailsActivity;
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

import okhttp3.OkHttpClient;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private List<SpinnerItem> spinnerItemProductTypeList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemCityList = new ArrayList<>();
    private Handler handler;
    private String city_id = "", productTypeId = "";
    private TextView cityTxt, productTypeTxt, noContentSearch;
    private Dialog alertDialog;
    private List<Item> itemList = new ArrayList<>();
    private EditText usernameEditText, pageNameEditText;
    private ProgressDialog progressDialog;
    private ItemAdapter itemAdapter;
    private boolean isFragmentLoaded = false;
    private AlertDialog deleteDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        handler = new Handler(Looper.getMainLooper());
        init(view);
        cityList();
        productTypeList();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(View view) {
        RelativeLayout layout = view.findViewById(R.id.layout);
        TextView nameTxt = Objects.requireNonNull(getActivity()).findViewById(R.id.nameTxt);
        FontManager.applyFont(getActivity(), layout);
        FontManager.applyFont(getActivity(), nameTxt);

//        nameTxt.setText(R.string.search);
        cityTxt = view.findViewById(R.id.cityTxt);
        productTypeTxt = view.findViewById(R.id.productTypeTxt);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        pageNameEditText = view.findViewById(R.id.pageNameEditText);
        noContentSearch = view.findViewById(R.id.noContentSearch);

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

        RelativeLayout cityLayout = view.findViewById(R.id.cityLayout);
        cityLayout.setOnClickListener(this);
        RelativeLayout typeLayout = view.findViewById(R.id.typeLayout);
        typeLayout.setOnClickListener(this);
        Button searchBtn = view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);

        RecyclerView recycleView = view.findViewById(R.id.recycleView);
        itemAdapter = new ItemAdapter(getActivity(), itemList, new OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(View view, int position) {
                Item item = itemList.get(position);
                String boat = item.getBoat();
                String diver = item.getDiver();
                String seller = item.getSeller();
                String service = item.getService();
                String tank = item.getJekski();
                String supplier = item.getSupplier();
                if (item.getBoat() == null && item.getDiver() == null && item.getSeller() == null
                        && item.getService() == null && item.getSupplier() == null
                        && item.getJekski() == null) {
                    Log.e("type", "nono");
                } else {
                    if (item.getBoat() != null && item.getDiver() == null && item.getSeller() == null
                            && item.getService() == null && item.getSupplier() == null
                            && item.getJekski() == null) {
                        Log.e("type", "boat");
                        Intent intent = new Intent(getActivity(), BoatDetailsActivity.class);
                        intent.putExtra("id", item.getBoat_id());
                        intent.putExtra("user_id", item.getUser_id());
                        intent.putExtra("name", item.getUserName());
                        intent.putExtra("image", item.getImage());
                        intent.putExtra("name_ar", item.getName_ar());
                        intent.putExtra("name_en", item.getName_en());
                        startActivity(intent);
                        if (deleteDialog != null)
                            deleteDialog.dismiss();
                    } else if (item.getBoat() == null && item.getDiver() != null && item.getSeller() == null
                            && item.getService() == null && item.getSupplier() == null
                            && item.getJekski() == null) {
                        Log.e("type", "diver");
                        Intent intent = new Intent(getActivity(), DriverDetailsActivity.class);
                        intent.putExtra("id", item.getDiver_id());
                        intent.putExtra("name", item.getUserName());
                        intent.putExtra("image", item.getImage());
                        intent.putExtra("name_ar", item.getName_ar());
                        intent.putExtra("name_en", item.getName_en());
                        intent.putExtra("diver_bio", item.getDiver_bio());
                        intent.putExtra("title_en", item.getTitle_en());
                        intent.putExtra("title_ar", item.getTitle_ar());
                        startActivity(intent);
                        if (deleteDialog != null)
                            deleteDialog.dismiss();
                    } else if (item.getBoat() == null && item.getDiver() == null && item.getSeller() != null
                            && item.getService() == null && item.getSupplier() == null
                            && item.getJekski() == null) {
                        Log.e("type", "seller");
                        Intent intent = new Intent(getActivity(), SellProductsDetailsActivity.class);
                        intent.putExtra("id", item.getSeller_id());
                        intent.putExtra("user_id", item.getUser_id());
                        intent.putExtra("name", item.getUserName());
                        intent.putExtra("image", item.getImage());
                        intent.putExtra("name_ar", item.getName_ar());
                        intent.putExtra("name_en", item.getName_en());
                        intent.putExtra("email", item.getEmail());
                        intent.putExtra("mobile", item.getMobile());
                        startActivity(intent);
                        if (deleteDialog != null)
                            deleteDialog.dismiss();
                    } else if (item.getBoat() == null && item.getDiver() == null && item.getSeller() == null
                            && item.getService() != null && item.getSupplier() == null
                            && item.getJekski() == null) {
                        Log.e("type", "service");
                        Intent intent = new Intent(getActivity(), ServicesSeaDetailsActivity.class);
                        intent.putExtra("id", item.getService_id());
                        intent.putExtra("user_id", item.getUser_id());
                        intent.putExtra("name", item.getUserName());
                        intent.putExtra("image", item.getImage());
                        intent.putExtra("name_ar", item.getName_ar());
                        intent.putExtra("name_en", item.getName_en());
                        intent.putExtra("email", item.getEmail());
                        intent.putExtra("mobile", item.getMobile());
                        startActivity(intent);
                        if (deleteDialog != null)
                            deleteDialog.dismiss();
                    } else if (item.getBoat() == null && item.getDiver() == null && item.getSeller() == null
                            && item.getService() == null && item.getSupplier() != null
                            && item.getJekski() == null) {
                        Log.e("type", "supplier");
                        Intent intent = new Intent(getActivity(), RequirementSeaDetailsActivity.class);
                        intent.putExtra("id", item.getSupplier_id());
                        intent.putExtra("user_id", item.getUser_id());
                        intent.putExtra("name", item.getUserName());
                        intent.putExtra("image", item.getImage());
                        intent.putExtra("name_ar", item.getName_ar());
                        intent.putExtra("name_en", item.getName_en());
                        intent.putExtra("email", item.getEmail());
                        intent.putExtra("mobile", item.getMobile());
                        startActivity(intent);
                        if (deleteDialog != null)
                            deleteDialog.dismiss();
                    } else if (item.getBoat() == null && item.getDiver() == null && item.getSeller() == null
                            && item.getService() == null && item.getSupplier() == null
                            && item.getJekski() != null) {
                        Log.e("type", "tank");
                        Intent intent = new Intent(getActivity(), TankDetailsActivity.class);
                        intent.putExtra("id", item.getJekski_id());
                        intent.putExtra("user_id", item.getUser_id());
                        intent.putExtra("name", item.getUserName());
                        intent.putExtra("image", item.getImage());
                        intent.putExtra("name_ar", item.getName_ar());
                        intent.putExtra("name_en", item.getName_en());
                        startActivity(intent);
                        if (deleteDialog != null)
                            deleteDialog.dismiss();
                    } else {
                        Log.e("type", "mix");
                        openDialog(boat, diver, tank, service, seller, supplier, item);
                    }

//                    Log.e("type", "mix");
//                    openDialog(boat, diver, tank, service, seller, supplier, item);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycleView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setAdapter(itemAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowCity() {
        @SuppressLint("InflateParams")
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_spinner, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCityList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCityList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
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
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemCityList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowProductType() {
        @SuppressLint("InflateParams")
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_spinner, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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

                for (int j = 0; j < spinnerItemProductTypeList.size(); j++) {

                    final String text = spinnerItemProductTypeList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemProductTypeList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemProductTypeList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemProductTypeList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        productTypeId = filteredList.get(position).getId();
                        productTypeTxt.setText(status);
                        productTypeTxt.setError(null);
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
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemProductTypeList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                    status = spinnerItemProductTypeList.get(position).getTextA();
                } else {
                    status = spinnerItemProductTypeList.get(position).getText();
                }
                productTypeId = spinnerItemProductTypeList.get(position).getId();
                productTypeTxt.setText(status);
                productTypeTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new

                DefaultItemAnimator());
        recyclerView.setAdapter(spinnerAdapter);
        alertDialog = dialog.create();
        Objects.requireNonNull(alertDialog.getWindow()).
                setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
    }

    public void cityList() {
        spinnerItemCityList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
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
                                    AppErrorsManager.showErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(getActivity(), response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(getActivity(), e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void productTypeList() {
        spinnerItemProductTypeList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "categories").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("categories");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String productTypeId = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem cityData = new SpinnerItem(productTypeId, name_en, name_ar);
                                spinnerItemProductTypeList.add(cityData);
                            }

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(getActivity(), response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(getActivity(), e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.error_network));
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });

        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    String url = FontManager.URL
                            + "find-partners?user_name=" + user_name + "&page_name=" + page_name + "&city=" + city_id
                            + "&type=" + productTypeId;
                    Log.e("url", url);
                    okhttp3.Request request = new okhttp3.Request.Builder().url(url).get()
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = response.body().string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String partners = successObject.getString("partners");
                            if (partners.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentSearch.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                JSONArray jsonArray = new JSONArray(partners);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject boatObject = jsonArray.getJSONObject(i);
                                    String user_id = boatObject.getString("id");
                                    String name = boatObject.getString("name");
                                    String user_image = boatObject.getString("user_image");
                                    String city = boatObject.getString("city");
                                    String email = boatObject.getString("email");
                                    String mobile = boatObject.getString("mobile");
                                    String boat = null, diver = null, supplier = null,
                                            service = null, seller = null, jekski = null,
                                            boat_id = "", diver_id = "", supplier_id = "",
                                            service_id = "", seller_id = "", jekski_id = "",
                                            diver_bio = "", title_ar = "", title_en = "", page_name = "";

                                    if (boatObject.has("partner_boat")) {
                                        boat = "partner_boat";
                                        String partner_boat = boatObject.getString("partner_boat");
                                        JSONArray partner_boatObject = new JSONArray(partner_boat);
                                        for (int j = 0; j < partner_boatObject.length(); j++) {
                                            JSONObject jsonObject1 = partner_boatObject.getJSONObject(0);
                                            boat_id = jsonObject1.getString("id");
                                            page_name = jsonObject1.getString("page_name");
                                            Log.e("id///", boat_id);
                                        }
                                    }
                                    if (boatObject.has("partner_diver")) {
                                        diver = "partner_diver";
                                        String partner_diver = boatObject.getString("partner_diver");
                                        JSONObject partner_partner_diver = new JSONObject(partner_diver);
                                        diver_id = partner_partner_diver.getString("id");
                                        diver_bio = partner_partner_diver.getString("diver_bio");
                                        String licenses = partner_partner_diver.getString("licenses");
                                        page_name = partner_partner_diver.getString("page_name");
                                        if (!licenses.equals("[]")) {
                                            JSONObject licensesObject = new JSONObject(licenses);
                                            if (licensesObject.has("tank")) {
                                                JSONObject tankObject = new JSONObject(licensesObject.getString("tank"));
                                                title_ar = tankObject.getString("title_ar");
                                                title_en = tankObject.getString("title_en");
                                            }
                                            if (licensesObject.has("free")) {
                                                JSONObject freeObject = new JSONObject(licensesObject.getString("free"));
                                                title_ar = freeObject.getString("title_ar");
                                                title_en = freeObject.getString("title_en");
                                            }
                                        }
                                    }
                                    if (boatObject.has("partner_supplier")) {
                                        supplier = "partner_supplier";
                                        String partner_supplier = boatObject.getString("partner_supplier");
                                        JSONObject partner_partner_supplier = new JSONObject(partner_supplier);
                                        supplier_id = partner_partner_supplier.getString("id");
                                        page_name = partner_partner_supplier.getString("page_name");

                                    }
                                    if (boatObject.has("partner_services")) {
                                        service = "partner_services";
                                        String partner_services = boatObject.getString("partner_services");
                                        JSONObject partner_partner_services = new JSONObject(partner_services);
                                        service_id = partner_partner_services.getString("id");
                                        page_name = partner_partner_services.getString("page_name");
                                    }
                                    if (boatObject.has("partner_Seller")) {
                                        seller = "partner_Seller";
                                        String partner_Seller = boatObject.getString("partner_Seller");
                                        JSONArray partner_partner_Seller = new JSONArray(partner_Seller);
                                        for (int j = 0; j < partner_partner_Seller.length(); j++) {
                                            JSONObject jsonObject1 = partner_partner_Seller.getJSONObject(0);
                                            seller_id = jsonObject1.getString("id");
                                            page_name = jsonObject1.getString("page_name");
                                        }
                                    }
                                    if (boatObject.has("partner_jetski")) {
                                        jekski = "partner_jetski";
                                        String partner_jetski = boatObject.getString("partner_jetski");
                                        JSONObject partner_partner_jetski = new JSONObject(partner_jetski);
                                        jekski_id = partner_partner_jetski.getString("id");
                                        page_name = partner_partner_jetski.getString("page_name");
                                    }
                                    Item item;
                                    if (city.equals("null")) {
                                        item = new Item(boat_id, service_id, supplier_id, seller_id,
                                                jekski_id, diver_id, name, city, user_id, user_image, "",
                                                "", email, mobile, boat, service,
                                                seller, diver, jekski, supplier, diver_bio, title_ar, title_en, page_name);
                                    } else {
                                        JSONObject cityObject = new JSONObject(city);
                                        String name_ar = cityObject.getString("name_ar");
                                        String name_en = cityObject.getString("name_en");
                                        item = new Item(boat_id, service_id, supplier_id, seller_id,
                                                jekski_id, diver_id, name, city, user_id, user_image, name_ar,
                                                name_en, email, mobile, boat, service,
                                                seller, diver, jekski, supplier, diver_bio, title_ar, title_en, page_name);
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
                                    AppErrorsManager.showErrorDialog(getActivity(), e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(getActivity(), response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(getActivity(), e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openDialog(String boat, String diver, String tank, String service, String seller,
                           String supplier, final Item item) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.popup_item_row, null);
        deleteDialog = new AlertDialog.Builder(getActivity()).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(Objects.requireNonNull(getActivity()), fontLayout);
        deleteDialog.setView(dialogView);
        if (boat != null) {
            dialogView.findViewById(R.id.boatLayout).setVisibility(View.VISIBLE);
            dialogView.findViewById(R.id.boatLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BoatDetailsActivity.class);
                    intent.putExtra("id", item.getBoat_id());
                    intent.putExtra("user_id", item.getUser_id());
                    intent.putExtra("name", item.getUserName());
                    intent.putExtra("image", item.getImage());
                    intent.putExtra("name_ar", item.getName_ar());
                    intent.putExtra("name_en", item.getName_en());
                    intent.putExtra("mobile", item.getMobile());
                    startActivity(intent);
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.dismiss();
        }
        if (diver != null) {
            dialogView.findViewById(R.id.driverLayout).setVisibility(View.VISIBLE);
            dialogView.findViewById(R.id.driverLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DriverDetailsActivity.class);
                    intent.putExtra("id", item.getDiver_id());
                    intent.putExtra("name", item.getUserName());
                    intent.putExtra("image", item.getImage());
                    intent.putExtra("name_ar", item.getName_ar());
                    intent.putExtra("name_en", item.getName_en());
                    intent.putExtra("diver_bio", item.getDiver_bio());
                    intent.putExtra("title_en", item.getTitle_en());
                    intent.putExtra("title_ar", item.getTitle_ar());
                    intent.putExtra("mobile", item.getMobile());
                    startActivity(intent);
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.dismiss();
        }
        if (tank != null) {
            dialogView.findViewById(R.id.tankLayout).setVisibility(View.VISIBLE);
            dialogView.findViewById(R.id.tankLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TankDetailsActivity.class);
                    intent.putExtra("id", item.getJekski_id());
                    intent.putExtra("user_id", item.getUser_id());
                    intent.putExtra("name", item.getUserName());
                    intent.putExtra("image", item.getImage());
                    intent.putExtra("name_ar", item.getName_ar());
                    intent.putExtra("name_en", item.getName_en());
                    startActivity(intent);
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.dismiss();
        }
        if (service != null) {
            dialogView.findViewById(R.id.serviceLayout).setVisibility(View.VISIBLE);
            dialogView.findViewById(R.id.serviceLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ServicesSeaDetailsActivity.class);
                    intent.putExtra("id", item.getService_id());
                    intent.putExtra("user_id", item.getUser_id());
                    intent.putExtra("name", item.getUserName());
                    intent.putExtra("image", item.getImage());
                    intent.putExtra("name_ar", item.getName_ar());
                    intent.putExtra("name_en", item.getName_en());
                    intent.putExtra("email", item.getEmail());
                    intent.putExtra("mobile", item.getMobile());
                    startActivity(intent);
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.dismiss();
        }
        if (seller != null) {
            dialogView.findViewById(R.id.sellerLayout).setVisibility(View.VISIBLE);
            dialogView.findViewById(R.id.sellerLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SellProductsDetailsActivity.class);
                    intent.putExtra("id", item.getSeller_id());
                    intent.putExtra("user_id", item.getUser_id());
                    intent.putExtra("name", item.getUserName());
                    intent.putExtra("image", item.getImage());
                    intent.putExtra("name_ar", item.getName_ar());
                    intent.putExtra("name_en", item.getName_en());
                    intent.putExtra("email", item.getEmail());
                    intent.putExtra("mobile", item.getMobile());
                    startActivity(intent);
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.dismiss();
        }
        if (supplier != null) {
            dialogView.findViewById(R.id.requirementLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), RequirementSeaDetailsActivity.class);
                    intent.putExtra("id", item.getSupplier_id());
                    intent.putExtra("user_id", item.getUser_id());
                    intent.putExtra("name", item.getUserName());
                    intent.putExtra("image", item.getImage());
                    intent.putExtra("name_ar", item.getName_ar());
                    intent.putExtra("name_en", item.getName_en());
                    intent.putExtra("email", item.getEmail());
                    intent.putExtra("mobile", item.getMobile());
                    startActivity(intent);
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.dismiss();
        }

        deleteDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cityLayout) {
            openWindowCity();
        } else if (id == R.id.searchBtn) {
            searchData();
        } else if (id == R.id.typeLayout) {
            openWindowProductType();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded) {
            // Load your data here or do network operations here
            isFragmentLoaded = true;
        }
    }
}
