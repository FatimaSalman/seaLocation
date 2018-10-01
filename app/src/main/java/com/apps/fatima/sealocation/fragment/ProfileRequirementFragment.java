package com.apps.fatima.sealocation.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.activities.BoatDetailsActivity;
import com.apps.fatima.sealocation.activities.BoatsOtherActivity;
import com.apps.fatima.sealocation.activities.DiversOtherActivity;
import com.apps.fatima.sealocation.activities.DriverDetailsActivity;
import com.apps.fatima.sealocation.activities.MyOrdersActivity;
import com.apps.fatima.sealocation.activities.RequirementSeaDetailsActivity;
import com.apps.fatima.sealocation.activities.SellProductsDetailsActivity;
import com.apps.fatima.sealocation.activities.SellerOtherActivity;
import com.apps.fatima.sealocation.activities.ServicesOtherActivity;
import com.apps.fatima.sealocation.activities.ServicesSeaDetailsActivity;
import com.apps.fatima.sealocation.activities.SupplierOtherActivity;
import com.apps.fatima.sealocation.activities.TankDetailsActivity;
import com.apps.fatima.sealocation.activities.TankOtherActivity;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FilePath;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.activities.BigSelectMapActivity;
import com.apps.fatima.sealocation.adapter.ImageAdapter;
import com.apps.fatima.sealocation.model.Item;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.apps.fatima.sealocation.model.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class ProfileRequirementFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 51;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_1 = 52;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1 = 53;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_2 = 54;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2 = 55;
    private List<Image> imageList = new ArrayList<>();
    private File fileSchema, licenceFile, requirementFile;//
    private URI mMediaUri;
    private Uri photoUri;
    private EditText usernameEditText, emailEditText, mobileEditText, pageNameEditText;
    private EditText recordNoCommercialEditText, shopNameEditText, shopDescribeEditText, otherEnCityEditText,
            otherArCityEditText;
    private TextView select_location_requirement, cityTxt, noContentImage, addActivityTxt;
    private ImageView log_img, imageRecordNew, userImage;
    private RelativeLayout imageRecordLayout, infoLayout;
    private List<SpinnerItem> spinnerItemCityList = new ArrayList<>();
    private Dialog alertDialog;
    private String city_id;
    private Handler handler;
    private double latitude, longitude;
    private ProgressDialog progressDialog;
    private String licence_image, token, supplier_id, is_active;
    private ImageAdapter imageAdapter;
    private ProgressBar progressbar_, progressbar;
    private boolean isFragmentLoaded = false;
    private String boat, diver, tank, supplier, service, product;
    private String name;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_requirement, container, false);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(getActivity(), "token");
        init(view);
        cityList();
        getUserInfo(token);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(View view) {
        RelativeLayout layout = view.findViewById(R.id.layout);
        RelativeLayout shareLayout = view.findViewById(R.id.shareLayout);
        RelativeLayout orderLayout = view.findViewById(R.id.orderLayout);
        TextView nameTxt = Objects.requireNonNull(getActivity()).findViewById(R.id.nameTxt);
//        nameTxt.setText(R.string.my_profile);
        FontManager.applyFont(getActivity(), layout);
        FontManager.applyFont(getActivity(), nameTxt);

        Button addImageBtn = view.findViewById(R.id.addImageBtn);
        Button updateBtn = view.findViewById(R.id.updateBtn);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        Button select_language_btn = view.findViewById(R.id.select_language_btn);
        TextView updateTxt = view.findViewById(R.id.updateTxt);
        addActivityTxt = view.findViewById(R.id.addActivityTxt);
        otherEnCityEditText = view.findViewById(R.id.otherEnCityEditText);
        otherArCityEditText = view.findViewById(R.id.otherArCityEditText);
        progressbar = view.findViewById(R.id.progressbar);
        progressbar_ = view.findViewById(R.id.progressbar_);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        recordNoCommercialEditText = view.findViewById(R.id.recordNoCommercialEditText);
        shopNameEditText = view.findViewById(R.id.shopNameEditText);
        shopDescribeEditText = view.findViewById(R.id.shopDescribeEditText);
        TextView attachImageTxt1 = view.findViewById(R.id.attachImageTxt1);
        select_location_requirement = view.findViewById(R.id.select_location_requirement);
        TextView changeImageTxt = view.findViewById(R.id.changeImageTxt);
        cityTxt = view.findViewById(R.id.cityTxt);
        noContentImage = view.findViewById(R.id.noContentImage);
        infoLayout = view.findViewById(R.id.infoLayout);
        log_img = view.findViewById(R.id.log_img);
        userImage = view.findViewById(R.id.userImage);
        userImage.setOnClickListener(this);
        addActivityTxt.setOnClickListener(this);
        imageRecordNew = view.findViewById(R.id.imageRecordNew);
        imageRecordLayout = view.findViewById(R.id.imageRecordLayout);
        pageNameEditText = view.findViewById(R.id.pageNameEditText);
        RelativeLayout cityLayout = view.findViewById(R.id.cityLayout);

        addImageBtn.setOnClickListener(this);
        select_language_btn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        updateTxt.setOnClickListener(this);
        log_img.setOnClickListener(this);
        changeImageTxt.setOnClickListener(this);
        cityLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
        select_location_requirement.setOnClickListener(this);
        attachImageTxt1.setOnClickListener(this);

        RecyclerView recycleViewImage = view.findViewById(R.id.recycleViewImage);
        imageAdapter = new ImageAdapter(getActivity(), imageList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                int id = view.getId();
                String image_id = imageList.get(position).getId();
                deleteImage(token, image_id);

            }
        });
        RecyclerView.LayoutManager mLayoutManager_ = new LinearLayoutManager(getActivity());
        recycleViewImage.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewImage.setLayoutManager(mLayoutManager_);
        recycleViewImage.setItemAnimator(new DefaultItemAnimator());
        recycleViewImage.setNestedScrollingEnabled(false);
        recycleViewImage.setAdapter(imageAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.addImageBtn) {
            if (!TextUtils.equals(is_active, "0")) {
                openDialog();
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.addActivityTxt) {
            openDialogAddActivity();
        } else if (id == R.id.updateTxt) {
            if (!TextUtils.equals(is_active, "0")) {
                if (infoLayout.getVisibility() == View.VISIBLE) {
                    infoLayout.setVisibility(View.GONE);
                } else {
                    infoLayout.setVisibility(View.VISIBLE);
                }
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.log_img || id == R.id.changeImageTxt || id == R.id.userImage) {
            if (!TextUtils.equals(is_active, "0")) {
                openDialog1();
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.cityLayout) {
            openWindowCity();
        } else if (id == R.id.attachImageTxt1) {
            openDialog2();
        } else if (id == R.id.select_location_requirement) {
            Intent intent = new Intent(getActivity(), BigSelectMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, 10);
        } else if (id == R.id.orderLayout) {
            Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.updateBtn) {
            updateUser();
        } else if (id == R.id.logoutBtn) {
            FontManager.logOut(getActivity());
        } else if (id == R.id.select_language_btn) {
            AppLanguage.openDialogLanguage(getActivity());
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(Objects.requireNonNull(getActivity()), name, getString(R.string.requirement));
        }
    }

    public void getUserInfo(final String token) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
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
                            JSONObject successObject = new JSONObject(success);
                            String user_info = successObject.getString("user_info");
                            JSONObject userObject = new JSONObject(user_info);
                            AppPreferences.saveString(getActivity(), "user_id", userObject.getString("id"));
                            name = userObject.getString("name");
                            final String email = userObject.getString("email");
                            final String mobile = userObject.getString("mobile");
                            final String user_image = userObject.getString("user_image");
                            final String city = userObject.getString("city");
                            JSONObject cityObject = new JSONObject(city);
                            city_id = cityObject.getString("id");
                            final String city_en = cityObject.getString("name_en");
                            final String city_ar = cityObject.getString("name_ar");

                            if (successObject.has("partner_supplier")) {
                                supplier = "yes";
                            } else {
                                supplier = "no";
                            }
                            if (successObject.has("partner_boat")) {
                                boat = "yes";
                            } else {
                                boat = "no";
                            }
                            if (successObject.has("partner_diver")) {
                                diver = "yes";
                            } else {
                                diver = "no";
                            }
                            if (successObject.has("partner_Seller")) {
                                product = "yes";
                            } else {
                                product = "no";
                            }
                            if (successObject.has("partner_services")) {
                                service = "yes";
                            } else {
                                service = "no";
                            }
                            if (successObject.has("partner_jetski")) {
                                tank = "yes";
                            } else {
                                tank = "no";
                            }
                            String partner_supplier = successObject.getString("partner_supplier");
                            JSONObject supplierObject = new JSONObject(partner_supplier);
                            supplier_id = supplierObject.getString("id");
                            is_active = supplierObject.getString("is_active");
                            final String recordNumber = supplierObject.getString("register_id");
                            final String shopName = supplierObject.getString("shop_name");
                            final String shopDescribe = supplierObject.getString("shop_description");
                            final String location = supplierObject.getString("location");
                            final String page_name = supplierObject.getString("page_name");
                            String[] namesList = location.split(",");
                            String name1 = namesList[0];
                            String name2 = namesList[1];
                            latitude = Double.parseDouble(name1);
                            longitude = Double.parseDouble(name2);
                            licence_image = supplierObject.getString("licence_image");

                            handler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    if (TextUtils.equals(supplier, "yes")
                                            && TextUtils.equals(service, "yes")
                                            && TextUtils.equals(product, "yes")
                                            && TextUtils.equals(diver, "yes")
                                            && TextUtils.equals(boat, "yes")
                                            && TextUtils.equals(tank, "yes")) {
                                        addActivityTxt.setVisibility(View.GONE);
                                    } else {
                                        addActivityTxt.setVisibility(View.VISIBLE);
                                    }
                                    getImages(token);
                                    usernameEditText.setText(name);
                                    if (!email.equals("null"))
                                        emailEditText.setText(email);
                                    if (!page_name.equals("null"))
                                        pageNameEditText.setText(page_name);
                                    mobileEditText.setText(mobile);
                                    progressbar.setVisibility(View.VISIBLE);
                                    if (!TextUtils.equals(user_image, "null")) {
                                        userImage.setVisibility(View.GONE);
                                        Picasso.get()
                                                .load(FontManager.IMAGE_URL + user_image)
                                                .into(log_img, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        progressbar.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        progressbar.setVisibility(View.GONE);
                                                    }
                                                });
                                    } else {
                                        progressbar.setVisibility(View.GONE);
                                        userImage.setImageResource(R.drawable.img_user);
                                        userImage.setVisibility(View.VISIBLE);
                                    }

                                    if (AppLanguage.getLanguage(getActivity()).equals("ar"))
                                        cityTxt.setText(city_ar);
                                    else
                                        cityTxt.setText(city_en);


                                    recordNoCommercialEditText.setText(recordNumber);
                                    shopNameEditText.setText(shopName);
                                    shopDescribeEditText.setText(shopDescribe);
                                    if (!licence_image.equals("null")) {
                                        imageRecordLayout.setVisibility(View.VISIBLE);
                                        progressbar_.setVisibility(View.VISIBLE);
                                        Picasso.get()
                                                .load(FontManager.IMAGE_URL + licence_image)
                                                .into(imageRecordNew, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        progressbar_.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        progressbar_.setVisibility(View.GONE);
                                                    }
                                                });
                                    }
                                    select_location_requirement.setText(location);

                                }
                            });

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
    public void openDialogAddActivity() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.popup_item_row_select, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(Objects.requireNonNull(getActivity()), fontLayout);
        deleteDialog.setView(dialogView);
        if (boat != null)
            if (boat.equals("no")) {
                dialogView.findViewById(R.id.boatLayout).setVisibility(View.VISIBLE);
                dialogView.findViewById(R.id.boatLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), BoatsOtherActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                dialogView.findViewById(R.id.boatLayout).setVisibility(View.GONE);
            }

        if (diver != null)
            if (diver.equals("no")) {
                dialogView.findViewById(R.id.driverLayout).setVisibility(View.VISIBLE);
                dialogView.findViewById(R.id.driverLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DiversOtherActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                dialogView.findViewById(R.id.driverLayout).setVisibility(View.GONE);
            }

        if (tank != null)
            if (tank.equals("no")) {
                dialogView.findViewById(R.id.tankLayout).setVisibility(View.VISIBLE);
                dialogView.findViewById(R.id.tankLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), TankOtherActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                dialogView.findViewById(R.id.tankLayout).setVisibility(View.GONE);
            }

        if (service != null)
            if (service.equals("no")) {
                dialogView.findViewById(R.id.serviceLayout).setVisibility(View.VISIBLE);
                dialogView.findViewById(R.id.serviceLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ServicesOtherActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                dialogView.findViewById(R.id.serviceLayout).setVisibility(View.GONE);
            }

        if (product != null)
            if (product.equals("no")) {
                dialogView.findViewById(R.id.sellerLayout).setVisibility(View.VISIBLE);
                dialogView.findViewById(R.id.sellerLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SellerOtherActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                dialogView.findViewById(R.id.sellerLayout).setVisibility(View.GONE);
            }

        if (supplier != null)
            if (supplier.equals("no")) {
                dialogView.findViewById(R.id.requirementLayout).setVisibility(View.VISIBLE);
                dialogView.findViewById(R.id.requirementLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SupplierOtherActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                dialogView.findViewById(R.id.requirementLayout).setVisibility(View.GONE);
            }

        deleteDialog.show();
    }

    public void updateUser() {
//        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String cityName = cityTxt.getText().toString().trim();
        final String cityName_ar = otherArCityEditText.getText().toString().trim();
        final String cityName_en = otherEnCityEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String page_name = pageNameEditText.getText().toString().trim();
        final String recordNumber = recordNoCommercialEditText.getText().toString().trim();
        final String shopName = shopNameEditText.getText().toString().trim();
        final String describeShop = shopDescribeEditText.getText().toString().trim();
        final String requirementLocation = select_location_requirement.getText().toString().trim();

        int is_valid;
        if (TextUtils.isEmpty(recordNumber)) {
            recordNoCommercialEditText.setError(getString(R.string.error_field_required));
            recordNoCommercialEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(shopName)) {
            shopNameEditText.setError(getString(R.string.error_field_required));
            shopNameEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(describeShop)) {
            shopDescribeEditText.setError(getString(R.string.error_field_required));
            shopDescribeEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(cityName)) {
            cityTxt.setError(getString(R.string.error_field_required));
            cityTxt.requestFocus();
            is_valid = 1;
        } else if (TextUtils.equals(cityName, getString(R.string.other))) {
            if (TextUtils.isEmpty(cityName_en)) {
                otherEnCityEditText.setError(getString(R.string.error_field_required));
                otherEnCityEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(cityName_ar)) {
                otherArCityEditText.setError(getString(R.string.error_field_required));
                otherArCityEditText.requestFocus();
                is_valid = 1;
            } else {
                if (TextUtils.isEmpty(requirementLocation)) {
                    select_location_requirement.setError(getString(R.string.error_field_required));
                    select_location_requirement.requestFocus();
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
            }
        } else if (TextUtils.isEmpty(mobile)) {
            mobileEditText.setError(getString(R.string.error_field_required));
            mobileEditText.requestFocus();
            is_valid = 1;
        } else if (mobile.length() > 9 || mobile.length() < 9) {
            mobileEditText.setError(getString(R.string.your_number_must_be_not_more_or_not_less_ten));
            mobileEditText.requestFocus();
            is_valid = 1;
        } else if (mobile.startsWith("0")) {
            mobileEditText.setError(getString(R.string.first_digit_not_zero));
            mobileEditText.requestFocus();
            is_valid = 1;
        }
//        else if (licenceFile == null || licence_image.equals("null")) {
//            AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.you_should_attach_record_image));
//        }
        else if (TextUtils.isEmpty(requirementLocation)) {
            select_location_requirement.setError(getString(R.string.error_field_required));
            select_location_requirement.requestFocus();
            is_valid = 1;
        } else {
            is_valid = 0;
        }
        if (is_valid == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("mobile", mobile)
                            .addFormDataPart("email", email)
                            .addFormDataPart("register_id", recordNumber)
                            .addFormDataPart("page_name", page_name)
                            .addFormDataPart("shop_name", shopName)
                            .addFormDataPart("shop_description", describeShop)
                            .addFormDataPart("location", requirementLocation)
                            .addFormDataPart("id", supplier_id);

                    if (TextUtils.equals(cityName, getString(R.string.other))) {
                        builder.addFormDataPart("city_title_ar", cityName_ar);
                        builder.addFormDataPart("city_title_en", cityName_en);
                    } else
                        builder.addFormDataPart("city", city_id);

                    if (licenceFile != null) {
                        builder.addFormDataPart("licence_image", licenceFile.getName(),
                                RequestBody.create(MediaType.parse("jpeg/png"), licenceFile));
                    }
                    if (fileSchema != null) {
                        builder.addFormDataPart("user_image", fileSchema.getName(),
                                RequestBody.create(MediaType.parse("jpeg/png"), fileSchema));
                    }
                    RequestBody requestBody = builder.build();
                    updateUserInfo(requestBody);
                }
            }).start();
        }
    }

    public void updateUserInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "update_supplier_info").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
//                            String error = jsonObject.optString("error");
                            if (jsonObject.has("error")) {
//                                final JSONObject jsonError = new JSONObject(error);
                                handler.post(new Runnable() {
                                    public void run() {
                                        progressDialog.hide();
//                                        if (jsonError.has("name")) {
//                                            String userNameError = jsonError.optString("name");
//                                            usernameEditText.setError(userNameError.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
//                                            usernameEditText.requestFocus();
//                                        } else if (jsonError.has("email")) {
//                                            String emailError = jsonError.optString("email");
//                                            emailEditText.setError(emailError.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
//                                            emailEditText.requestFocus();
//                                        } else if (jsonError.has("mobile")) {
//                                            String user_mobile = jsonError.optString("mobile");
//                                            mobileEditText.setError(user_mobile.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
//                                            mobileEditText.requestFocus();
//                                        }
                                    }
                                });
                            } else if (jsonObject.has("success")) {
//                                String success = jsonObject.getString("success");
//                                JSONObject jsonError = new JSONObject(success);
//                                final String token = jsonError.getString("token");
//                                final String name = jsonError.getString("name");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        AppErrorsManager.showSuccessDialog(getActivity(), getString(R.string.update_successfully), null);
                                    }
                                });
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

    public void insertImage() {
        Log.e("yyyy", "uuuuu");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("partner_id", supplier_id)
                        .addFormDataPart("object_tb", "partner_supplies")
                        .addFormDataPart("is_default", "1")
                        .addFormDataPart("object_id", supplier_id);
//                partner_boats,partner_divers,partner_jetski,partner_sellers,partner_services,partner_supplies
                if (requirementFile != null) {
                    builder.addFormDataPart("image", requirementFile.getName(),
                            RequestBody.create(MediaType.parse("jpeg/png"), requirementFile));
                }


                RequestBody requestBody = builder.build();
                insertImageInfo(requestBody);
            }
        }).start();
    }

    public void insertImageInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
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
                            + "store-image").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            if (jsonObject.has("success")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        AppErrorsManager.showSuccessDialog(getActivity(),
                                                getString(R.string.add_image_successfully), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        getImages(token);
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

    public void getImages(final String token) {
        imageList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "suppliers/" + supplier_id).get()
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
                            JSONObject successObject = new JSONObject(success);
                            String divers = successObject.getString("supplies");
                            if (!divers.equals("null")) {
                                JSONObject userObject = new JSONObject(divers);
                                String images = userObject.getString("images");
                                if (images.equals("[]")) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            noContentImage.setVisibility(View.VISIBLE);
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
                                        String url = jsonObject1.getString("url");
                                        Image image = new Image(url, id, partner_id, object_id, object_tb);
                                        imageList.add(image);
                                    }
//
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            noContentImage.setVisibility(View.GONE);
                                            imageAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }
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

    public void deleteImage(final String token, final String image_id) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "delete-image/" + image_id).delete()
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
                            if (success.equals("deleted")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        getImages(token);
                                    }
                                });
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
                        if (TextUtils.equals(cityTxt.getText().toString(), getString(R.string.other))) {
                            otherEnCityEditText.setVisibility(View.VISIBLE);
                            otherArCityEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherEnCityEditText.setVisibility(View.GONE);
                            otherArCityEditText.setVisibility(View.GONE);
                        }
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
                if (TextUtils.equals(cityTxt.getText().toString(), getString(R.string.other))) {
                    otherEnCityEditText.setVisibility(View.VISIBLE);
                    otherArCityEditText.setVisibility(View.VISIBLE);
                } else {
                    otherEnCityEditText.setVisibility(View.GONE);
                    otherArCityEditText.setVisibility(View.GONE);
                }
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
                                city_id = jsonObject1.getString("id");
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(Objects.requireNonNull(getActivity()), fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getActivity().getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED) {
                    openGalleryFile();
                } else
                    checkAndRequestPermissions();
//                openGalleryFile();
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getActivity().getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getActivity().getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    private void openGalleryFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(getActivity(), uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        requirementFile = new File(pdfPathHolder);
        insertImage();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void captureImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        }
    }

    String mCurrentPhotoPath;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile() {
        Log.e("uriii", mMediaUri.toString());
        requirementFile = new File(mMediaUri);
        insertImage();
//        Image image = new Image(photoUri);
//        imageList.add(image);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openDialog1() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(Objects.requireNonNull(getActivity()), fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getActivity().getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED) {
                    openGalleryFile1();
                } else
                    checkAndRequestPermissions();
//                openGalleryFile();
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getActivity().getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getActivity().getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera1();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void openGalleryFile1() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_1);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile1(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(getActivity(), uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        fileSchema = new File(pdfPathHolder);
        userImage.setVisibility(View.GONE);
        Picasso.get().load(uri).into(log_img);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void captureImageFromCamera1() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile1() {
        Log.e("uriii", mMediaUri.toString());
        fileSchema = new File(mMediaUri);
        userImage.setVisibility(View.GONE);
//        Picasso.get().load(photoUri).into(log_img);
        try {
            ExifInterface exifObject = new ExifInterface(fileSchema.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            log_img.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openDialog2() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(Objects.requireNonNull(getActivity()), fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getActivity().getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED) {
                    openGalleryFile2();
                } else
                    checkAndRequestPermissions();
//                openGalleryFile();
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getActivity().getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getActivity().getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera2();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void openGalleryFile2() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_2);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile2(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(getActivity(), uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        licenceFile = new File(pdfPathHolder);
        imageRecordLayout.setVisibility(View.VISIBLE);
        progressbar_.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageRecordNew, new Callback() {
            @Override
            public void onSuccess() {
                progressbar_.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressbar_.setVisibility(View.GONE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void captureImageFromCamera2() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile2() {
        Log.e("uriii", mMediaUri.toString());
        licenceFile = new File(mMediaUri);
        imageRecordLayout.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.VISIBLE);
        Picasso.get().load(photoUri).into(imageRecordNew, new Callback() {
            @Override
            public void onSuccess() {
                progressbar_.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressbar_.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE_SCHEMA) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile();
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_1) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile1(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile1();
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_2) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile2(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile2();
            }
        } else if (requestCode == 10 && data != null) {
            select_location_requirement.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_requirement.setError(null);
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
