package com.apps.fatima.sealocation.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.DialogFragment;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.activities.BoatsOtherActivity;
import com.apps.fatima.sealocation.activities.DiversOtherActivity;
import com.apps.fatima.sealocation.activities.MyOrdersActivity;
import com.apps.fatima.sealocation.activities.SellerOtherActivity;
import com.apps.fatima.sealocation.activities.ServicesOtherActivity;
import com.apps.fatima.sealocation.activities.SupplierOtherActivity;
import com.apps.fatima.sealocation.activities.TankOtherActivity;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FilePath;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.activities.AddTankActivity;
import com.apps.fatima.sealocation.activities.BigSelectMapActivity;
import com.apps.fatima.sealocation.adapter.ImageAdapter;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.apps.fatima.sealocation.adapter.TankAdapter;
import com.apps.fatima.sealocation.adapter.TankRequestAdapter;
import com.apps.fatima.sealocation.model.Image;
import com.apps.fatima.sealocation.model.Tanks;
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

public class ProfileTankFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 51;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_1 = 52;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1 = 53;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_2 = 54;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2 = 55;
    private List<Image> imageList = new ArrayList<>();
    private List<Tanks> tanksList = new ArrayList<>();
    private List<Tanks> tanksRequestList = new ArrayList<>();
    private File fileSchema, licenceFile, tanksFile;//
    private URI mMediaUri;
    private Uri photoUri;
    private Handler handler;
    private ProgressDialog progressDialog;
    private List<SpinnerItem> spinnerItemCityList = new ArrayList<>();
    private EditText usernameEditText, emailEditText, mobileEditText, pageNameEditText;
    private EditText rentValueTanksEditText, otherEnTankTypeEditText, otherArTankTypeEditText, otherEnCityEditText,
            otherArCityEditText;
    private ImageView log_img, imageViewTank, userImage;
    private TextView tanksNoEditText, cityTxt, select_location_tanks, tankTypeTxt, expireTanksTxt,
            noContentImage, noContentTank, noContentRequest, addActivityTxt;
    private double latitude, longitude;
    private RelativeLayout imageTankLayout, infoLayout;
    private String city_id, tankTypeId, tank_id, user_id, token, is_active;
    private Dialog alertDialog;
    private List<SpinnerItem> spinnerItemTankTypeList = new ArrayList<>();
    private TankAdapter tankAdapter;
    private ImageAdapter imageAdapter;
    private ProgressBar progressbar_, progressbar;
    private TankRequestAdapter tankRequestAdapter;
    private boolean isFragmentLoaded = false;
    private RatingBar ratingBar;
    private String boat, diver, tank, supplier, service, product;
    private String name;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_tanks, container, false);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(getActivity(), "token");
        init(view);
        cityList();
        tankTypeList();
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
        RelativeLayout plusLayout = view.findViewById(R.id.plusLayout);
        RelativeLayout minusLayout = view.findViewById(R.id.minusLayout);
        plusLayout.setOnClickListener(this);
        minusLayout.setOnClickListener(this);
        otherEnCityEditText = view.findViewById(R.id.otherEnCityEditText);
        otherArCityEditText = view.findViewById(R.id.otherArCityEditText);
        progressbar_ = view.findViewById(R.id.progressbar_);
        progressbar = view.findViewById(R.id.progressbar);
        otherArTankTypeEditText = view.findViewById(R.id.otherArTankTypeEditText);
        otherEnTankTypeEditText = view.findViewById(R.id.otherEnTankTypeEditText);
        ratingBar = view.findViewById(R.id.ratingBar);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        tanksNoEditText = view.findViewById(R.id.tanksNoEditText);
        noContentImage = view.findViewById(R.id.noContentImage);
        noContentTank = view.findViewById(R.id.noContentTank);
        noContentRequest = view.findViewById(R.id.noContentRequest);
        rentValueTanksEditText = view.findViewById(R.id.rentValueTanksEditText);
        cityTxt = view.findViewById(R.id.cityTxt);
        select_location_tanks = view.findViewById(R.id.select_location_tanks);
        tankTypeTxt = view.findViewById(R.id.tankTypeTxt);
        imageTankLayout = view.findViewById(R.id.imageTankLayout);
        expireTanksTxt = view.findViewById(R.id.expireTanksTxt);
        infoLayout = view.findViewById(R.id.infoLayout);
        TextView updateTxt = view.findViewById(R.id.updateTxt);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        Button updateBtn = view.findViewById(R.id.updateBtn);
        Button select_language_btn = view.findViewById(R.id.select_language_btn);
        RelativeLayout cityLayout = view.findViewById(R.id.cityLayout);
        RelativeLayout tankTypeLayout = view.findViewById(R.id.tankTypeLayout);
        RelativeLayout expireDataTanksLayout = view.findViewById(R.id.expireDataTanksLayout);
        TextView changeImageTxt = view.findViewById(R.id.changeImageTxt);
        TextView licenceTankImage = view.findViewById(R.id.licenceTankImage);
        addActivityTxt = view.findViewById(R.id.addActivityTxt);
        log_img = view.findViewById(R.id.log_img);
        pageNameEditText = view.findViewById(R.id.pageNameEditText);
        imageViewTank = view.findViewById(R.id.imageViewTank);
        userImage = view.findViewById(R.id.userImage);
        userImage.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
        addActivityTxt.setOnClickListener(this);
        updateTxt.setOnClickListener(this);
        Button addImageBtn = view.findViewById(R.id.addImageBtn);
        Button addTankBtn = view.findViewById(R.id.addTankBtn);
        addImageBtn.setOnClickListener(this);
        addTankBtn.setOnClickListener(this);
        changeImageTxt.setOnClickListener(this);
        licenceTankImage.setOnClickListener(this);
        select_language_btn.setOnClickListener(this);
        log_img.setOnClickListener(this);
        select_location_tanks.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        cityLayout.setOnClickListener(this);
        tankTypeLayout.setOnClickListener(this);
        expireDataTanksLayout.setOnClickListener(this);

        RecyclerView recycleView = view.findViewById(R.id.recycleViewTanks);
        tankAdapter = new TankAdapter(getActivity(), tanksList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                if (id == R.id.ic_edit) {
                    String tank_id = tanksList.get(position).getId();
                    Intent intent = new Intent(getActivity(), AddTankActivity.class);
                    intent.putExtra("tank_id", tank_id);
                    startActivityForResult(intent, 12);
                } else if (id == R.id.ic_delete) {
                    AppErrorsManager.showSuccessDialog(getActivity(), getString(R.string.delet_process),
                            getString(R.string.are_you_need_delete_trip), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteTank(token, tank_id);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycleView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setAdapter(tankAdapter);
//        tanksData();

        RecyclerView recycleViewRequest = view.findViewById(R.id.recycleViewRequest);
        tankRequestAdapter = new TankRequestAdapter(getActivity(), tanksRequestList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                String request_id = tanksRequestList.get(position).getId();
                if (id == R.id.ic_approve) {
                    addResponse(request_id, "1");
                } else if (id == R.id.ic_cancel) {
                    addResponse(request_id, "2");
                } else if (id == R.id.ratingTxt) {
                    requestRating(request_id);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager_ = new LinearLayoutManager(getActivity());
        recycleViewRequest.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewRequest.setLayoutManager(mLayoutManager_);
        recycleViewRequest.setItemAnimator(new DefaultItemAnimator());
        recycleViewRequest.setNestedScrollingEnabled(false);
        recycleViewRequest.setAdapter(tankRequestAdapter);

        RecyclerView recycleViewImage = view.findViewById(R.id.recycleViewImage);
        imageAdapter = new ImageAdapter(getActivity(), imageList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                int id = view.getId();
                String image_id = imageList.get(position).getId();
                deleteImage(token, image_id);

            }
        });
        RecyclerView.LayoutManager mLayoutManager_1 = new LinearLayoutManager(getActivity());
        recycleViewImage.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewImage.setLayoutManager(mLayoutManager_1);
        recycleViewImage.setNestedScrollingEnabled(false);
        recycleViewImage.setItemAnimator(new DefaultItemAnimator());
        recycleViewImage.setAdapter(imageAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        int totalCount = Integer.parseInt(tanksNoEditText.getText().toString());
        if (id == R.id.addImageBtn) {
            if (!TextUtils.equals(is_active, "0")) {
                openDialog2();
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
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
        } else if (id == R.id.addActivityTxt) {
            openDialogAddActivity();
        } else if (id == R.id.addTankBtn) {
            if (!TextUtils.equals(is_active, "0")) {
                Intent intent = new Intent(getActivity(), AddTankActivity.class);
                startActivityForResult(intent, 12);
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.logoutBtn) {
            FontManager.logOut(getActivity());
        } else if (id == R.id.updateBtn) {
            updateUser();
        } else if (id == R.id.log_img || id == R.id.changeImageTxt || id == R.id.userImage) {
            if (!TextUtils.equals(is_active, "0")) {
                openDialog();
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.licenceTankImage) {
            openDialog1();
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                tanksNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            tanksNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.expireDataTanksLayout) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "third");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getFragmentManager(), "Date Picker");
        } else if (id == R.id.cityLayout) {
            openWindowCity();
        } else if (id == R.id.tankTypeLayout) {
            openWindowTankType();
        } else if (id == R.id.select_location_tanks) {
            Intent intent = new Intent(getActivity(), BigSelectMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, 10);
        } else if (id == R.id.orderLayout) {
            Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.select_language_btn) {
            AppLanguage.openDialogLanguage(getActivity());
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(Objects.requireNonNull(getActivity()), name, getString(R.string.tank));
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
                        assert response.body() != null;
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String user_info = successObject.getString("user_info");
                            JSONObject userObject = new JSONObject(user_info);
                            AppPreferences.saveString(getActivity(), "user_id", userObject.getString("id"));
                            user_id = userObject.getString("id");
                            name = userObject.getString("name");
                            final String email = userObject.getString("email");
                            final String mobile = userObject.getString("mobile");
                            final String user_image = userObject.getString("user_image");
                            String city = userObject.getString("city");
                            JSONObject cityObject = new JSONObject(city);
                            city_id = cityObject.getString("id");
                            final String city_en = cityObject.getString("name_en");
                            final String city_ar = cityObject.getString("name_ar");

                            final String star_number = userObject.getString("star_number");
                            if (!star_number.equals("null")) {
                                int startCount = Integer.parseInt(star_number);
                                ratingBar.setRating(startCount);
                            }
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
                            String partner_jetski = successObject.getString("partner_jetski");
                            JSONObject jetskiObject = new JSONObject(partner_jetski);
                            tank_id = jetskiObject.getString("id");
                            is_active = jetskiObject.getString("is_active");
                            final String tankType = jetskiObject.getString("type");
                            JSONObject typeObject = new JSONObject(tankType);
                            tankTypeId = typeObject.getString("id");
                            final String type_en = typeObject.getString("title_en");
                            final String type_ar = typeObject.getString("title_ar");

                            final String page_name = jetskiObject.getString("page_name");

                            final String tankNumber = jetskiObject.getString("quantity");
                            final String tankRent = jetskiObject.getString("hourly_price");
                            final String location = jetskiObject.getString("location");
                            String[] namesList = location.split(",");
                            String name1 = namesList[0];
                            String name2 = namesList[1];
                            latitude = Double.parseDouble(name1);
                            longitude = Double.parseDouble(name2);
                            final String driver_licence_end_date = jetskiObject.getString("driver_licence_end_date");
                            final String licence_image = jetskiObject.getString("licence_image");

                            handler.post(new Runnable() {
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
                                    getTankList(token, user_id);
                                    getImages(token);
                                    usernameEditText.setText(name);
                                    if (!email.equals("null"))
                                        emailEditText.setText(email);
                                    mobileEditText.setText(mobile);
                                    if (!page_name.equals("null"))
                                        pageNameEditText.setText(page_name);
                                    tanksRequestData();
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
                                        userImage.setVisibility(View.VISIBLE);
                                        userImage.setImageResource(R.drawable.img_user);
                                    }
//
                                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                                        cityTxt.setText(city_ar);
                                        tankTypeTxt.setText(type_ar);
                                    } else {
                                        cityTxt.setText(city_en);
                                        tankTypeTxt.setText(type_en);
                                    }

                                    tanksNoEditText.setText(tankNumber);
                                    rentValueTanksEditText.setText(tankRent);
                                    if (!licence_image.equals("null")) {
                                        progressbar_.setVisibility(View.VISIBLE);
                                        imageTankLayout.setVisibility(View.VISIBLE);
                                        Picasso.get()
                                                .load(FontManager.IMAGE_URL + licence_image).
                                                into(imageViewTank, new Callback() {
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
                                    select_location_tanks.setText(location);
                                    expireTanksTxt.setText(driver_licence_end_date);

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
                            Activity activity = getActivity();
                            if (activity != null && isAdded())
                                AppErrorsManager.showErrorDialog(activity, getString(R.string.error_network));
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
        final String email = emailEditText.getText().toString().trim();
        final String cityName = cityTxt.getText().toString().trim();
        final String cityName_ar = otherArCityEditText.getText().toString().trim();
        final String cityName_en = otherEnCityEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String page_name = pageNameEditText.getText().toString().trim();

        final String tankType = tankTypeTxt.getText().toString().trim();
        final String tankType_ar = otherArTankTypeEditText.getText().toString().trim();
        final String tankType_en = otherEnTankTypeEditText.getText().toString().trim();
        final String tankNumber = tanksNoEditText.getText().toString().trim();
        final String rentValueTank = rentValueTanksEditText.getText().toString().trim();
        final String locationTank = select_location_tanks.getText().toString().trim();
        final String expireTxtTank = expireTanksTxt.getText().toString().trim();
        int is_valid = 0;
        if (!TextUtils.isEmpty(email) && !email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            emailEditText.requestFocus();
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
                is_valid = 0;
            }
        }
        if (is_valid == 0) {
            if (TextUtils.isEmpty(mobile)) {
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
            } else if (TextUtils.isEmpty(tankType)) {
                tankTypeTxt.setError(getString(R.string.error_field_required));
                tankTypeTxt.requestFocus();
                is_valid = 1;
            } else if (TextUtils.equals(tankType, getString(R.string.other))) {
                if (TextUtils.isEmpty(tankType_en)) {
                    otherEnTankTypeEditText.setError(getString(R.string.error_field_required));
                    otherEnTankTypeEditText.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(tankType_ar)) {
                    otherArTankTypeEditText.setError(getString(R.string.error_field_required));
                    otherArTankTypeEditText.requestFocus();
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
            }
            if (is_valid == 0) {
                if (TextUtils.isEmpty(tankNumber)) {
                    tanksNoEditText.setError(getString(R.string.error_field_required));
                    tanksNoEditText.requestFocus();
                    is_valid = 1;
                } else if (tankNumber.equals("0")) {
                    AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.you_should_enter_tank_no));
                    is_valid = 1;
                } else if (TextUtils.isEmpty(rentValueTank)) {
                    rentValueTanksEditText.setError(getString(R.string.error_field_required));
                    rentValueTanksEditText.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(locationTank)) {
                    select_location_tanks.setError(getString(R.string.error_field_required));
                    select_location_tanks.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(expireTxtTank)) {
                    expireTanksTxt.setError(getString(R.string.error_field_required));
                    expireTanksTxt.requestFocus();
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
                if (is_valid == 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                            if (TextUtils.equals(tankType, getString(R.string.other))) {
                                builder.addFormDataPart("jetski_type_title_ar", tankType_ar);
                                builder.addFormDataPart("jetski_type_title_en", tankType_en);
                            } else
                                builder.addFormDataPart("type", tankTypeId);

                            builder.addFormDataPart("quantity", tankNumber)
                                    .addFormDataPart("hourly_price", rentValueTank)
                                    .addFormDataPart("location", locationTank)
                                    .addFormDataPart("driver_licence_end_date", expireTxtTank)
                                    .addFormDataPart("email", email)
                                    .addFormDataPart("page_name", page_name)
                                    .addFormDataPart("mobile", mobile);

                            if (TextUtils.equals(cityName, getString(R.string.other))) {
                                builder.addFormDataPart("city_title_ar", cityName_ar);
                                builder.addFormDataPart("city_title_en", cityName_en);
                            } else
                                builder.addFormDataPart("city", city_id);
//
                            if (fileSchema != null) {
                                builder.addFormDataPart("user_image", fileSchema.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), fileSchema));
                            }


                            if (licenceFile != null) {
                                builder.addFormDataPart("jetski_licence_image", licenceFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), licenceFile));
                            }
                            RequestBody requestBody = builder.build();
                            updateUserInfo(requestBody);
                        }
                    }).start();
                }
            }
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
                            + "update_jetki_info").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            String error = jsonObject.optString("error");
                            if (jsonObject.has("error")) {
                                final JSONObject jsonError = new JSONObject(error);
                                handler.post(new Runnable() {
                                    public void run() {
                                        progressDialog.hide();
                                        if (jsonError.has("name")) {
                                            String userNameError = jsonError.optString("name");
                                            usernameEditText.setError(userNameError.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
                                            usernameEditText.requestFocus();
                                        } else if (jsonError.has("email")) {
                                            String emailError = jsonError.optString("email");
                                            emailEditText.setError(emailError.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
                                            emailEditText.requestFocus();
                                        } else if (jsonError.has("mobile")) {
                                            String user_mobile = jsonError.optString("mobile");
                                            mobileEditText.setError(user_mobile.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", ""));
                                            mobileEditText.requestFocus();
                                        }
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

    public void getTankList(final String token, final String user_id) {
        tanksList.clear();
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
                            + "partner-jetski/" + user_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
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
                                    String quantity = boatObject.getString("quantity");
                                    String hourly_price = boatObject.getString("hourly_price");
                                    Tanks tanks;
                                    if (type_id.equals("null")) {
                                        tanks = new Tanks(id, "", "", quantity, hourly_price);
                                    } else {
                                        JSONObject typeObject = new JSONObject(type_id);
                                        String title_ar = typeObject.getString("title_ar");
                                        String title_en = typeObject.getString("title_en");
                                        tanks = new Tanks(id, title_ar, title_en, quantity, hourly_price);
                                    }
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
                        .addFormDataPart("partner_id", tank_id)
                        .addFormDataPart("object_tb", "partner_jetski")
                        .addFormDataPart("is_default", "1")
                        .addFormDataPart("object_id", tank_id);
//                partner_boats,partner_divers,partner_jetski,partner_sellers,partner_services,partner_supplies
                if (tanksFile != null) {
                    builder.addFormDataPart("image", tanksFile.getName(),
                            RequestBody.create(MediaType.parse("jpeg/png"), tanksFile));
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
                            + "jetski/" + tank_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaaservices", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String divers = successObject.getString("jetski");
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
                                    JSONArray imageArray = new JSONArray(images);
                                    for (int j = 0; j < imageArray.length(); j++) {
                                        JSONObject jsonObject1 = imageArray.getJSONObject(j);
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

    public void deleteTank(final String token, final String boat_id) {
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
                            + "jetski/" + boat_id).delete()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("deleted", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            if (success.equals("true")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        getTankList(token, user_id);
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

    public void deleteBoatBook(Context context, final String token, final String boat_id) {
        InternetConnectionUtils.isInternetAvailable(context, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "booking_tank_action_delete/" + boat_id).delete()
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
                            if (success.equals("1")) {
                                tanksRequestData();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowTankType() {
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

                for (int j = 0; j < spinnerItemTankTypeList.size(); j++) {

                    final String text = spinnerItemTankTypeList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemTankTypeList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemTankTypeList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemTankTypeList.get(j));
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
                        tankTypeId = filteredList.get(position).getId();
                        tankTypeTxt.setText(status);
                        if (TextUtils.equals(tankTypeTxt.getText().toString(), getString(R.string.other))) {
                            otherArTankTypeEditText.setVisibility(View.VISIBLE);
                            otherEnTankTypeEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherArTankTypeEditText.setVisibility(View.GONE);
                            otherEnTankTypeEditText.setVisibility(View.GONE);
                        }
                        tankTypeTxt.setError(null);
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
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemTankTypeList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                    status = spinnerItemTankTypeList.get(position).getTextA();
                } else {
                    status = spinnerItemTankTypeList.get(position).getText();
                }
                tankTypeId = spinnerItemTankTypeList.get(position).getId();
                tankTypeTxt.setText(status);
                if (TextUtils.equals(tankTypeTxt.getText().toString(), getString(R.string.other))) {
                    otherArTankTypeEditText.setVisibility(View.VISIBLE);
                    otherEnTankTypeEditText.setVisibility(View.VISIBLE);
                } else {
                    otherArTankTypeEditText.setVisibility(View.GONE);
                    otherEnTankTypeEditText.setVisibility(View.GONE);
                }
                tankTypeTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
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

    public void tankTypeList() {
        spinnerItemTankTypeList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "jetski-types").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("Jetski_types");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                tankTypeId = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem cityData = new SpinnerItem(tankTypeId, name_en, name_ar);
                                spinnerItemTankTypeList.add(cityData);
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

    public void tanksRequestData() {
        tanksRequestList.clear();
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
                            + "partner-book-jetski/" + user_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("tankList", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String trips = successObject.getString("books");
                            if (trips.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentRequest.setVisibility(View.VISIBLE);

                                    }
                                });
                            } else {
                                JSONArray tripsArray = new JSONArray(trips);
                                for (int i = 0; i < tripsArray.length(); i++) {
                                    JSONObject boatObject = tripsArray.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String title_ar = boatObject.getString("type_name");
                                    String title_en = boatObject.getString("type_name");
                                    String guid = boatObject.getString("guid");
                                    String mobile = boatObject.getString("mobile");
                                    String quantity = boatObject.getString("quantity");
                                    String is_rating = boatObject.getString("is_rating");
                                    String approved = boatObject.getString("approved");
                                    String name = boatObject.getString("name");
                                    Tanks boat = new Tanks(id, quantity, title_ar, title_en, guid,
                                            mobile, is_rating, approved, name);
                                    tanksRequestList.add(boat);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentRequest.setVisibility(View.GONE);
                                            tankRequestAdapter.notifyDataSetChanged();
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

    public void addResponse(final String id, final String approved) {
        Log.e("yyyy", "uuuuu");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", id)
                        .addFormDataPart("approved", approved);
                RequestBody requestBody = builder.build();
                addResponseInfo(requestBody);
            }
        }).start();
    }

    public void addResponseInfo(final RequestBody requestBody) {
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
                            + "book-jetski-response").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("book-jetski-response", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            if (jsonObject.has("success")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        AppErrorsManager.showSuccessDialog(getActivity(),
                                                getString(R.string.your_order_sent), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        tanksRequestData();
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

    public void requestRating(final String id) {
        Log.e("yyyy", "uuuuu");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", id);
                RequestBody requestBody = builder.build();
                requestRatingInfo(requestBody);
            }
        }).start();
    }

    public void requestRatingInfo(final RequestBody requestBody) {
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
                            + "request_tank_rating").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("request_rating", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            if (jsonObject.has("success")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        AppErrorsManager.showSuccessDialog(getActivity(),
                                                getString(R.string.your_request_sent), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                        tanksRequestData();
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
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
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
        fileSchema = new File(pdfPathHolder);
        Picasso.get().load(uri).into(log_img);
        userImage.setVisibility(View.GONE);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile() {
        Log.e("uriii", mMediaUri.toString());
        fileSchema = new File(mMediaUri);
//        Picasso.get().load(photoUri).into(log_img);
        userImage.setVisibility(View.GONE);
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
        tanksFile = new File(pdfPathHolder);
        insertImage();
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
        tanksFile = new File(mMediaUri);
        insertImage();
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
        licenceFile = new File(pdfPathHolder);
        imageTankLayout.setVisibility(View.VISIBLE);
        progressbar_.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageViewTank, new Callback() {
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile1() {
        Log.e("uriii", mMediaUri.toString());
        licenceFile = new File(mMediaUri);
        imageTankLayout.setVisibility(View.VISIBLE);
        progressbar_.setVisibility(View.GONE);
        Picasso.get().load(photoUri).into(imageViewTank, new Callback() {
            @Override
            public void onSuccess() {
                progressbar_.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressbar_.setVisibility(View.GONE);
            }
        });
        imageViewTank.setRotation(90);
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
            select_location_tanks.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_tanks.setError(null);
        } else if (requestCode == 12 && data != null) {
            Log.e("dddd0", "dddd");
            getTankList(token, user_id);
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
