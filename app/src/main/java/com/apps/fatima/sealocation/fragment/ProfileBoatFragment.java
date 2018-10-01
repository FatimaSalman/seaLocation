package com.apps.fatima.sealocation.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import com.apps.fatima.sealocation.adapter.BoatEventRequestAdapter;
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
import com.apps.fatima.sealocation.activities.AddBoatActivity;
import com.apps.fatima.sealocation.activities.AddBoatTripActivity;
import com.apps.fatima.sealocation.activities.BigSelectMapActivity;
import com.apps.fatima.sealocation.adapter.BoatAdapter;
import com.apps.fatima.sealocation.adapter.BoatRequestAdapter;
import com.apps.fatima.sealocation.adapter.ComingEventsAdapter;
import com.apps.fatima.sealocation.adapter.ImageAdapter;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.apps.fatima.sealocation.model.Boat;
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

public class ProfileBoatFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 51;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_1 = 52;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1 = 53;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_2 = 54;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2 = 55;
    private List<Image> imageList = new ArrayList<>();
    private List<Boat> boatList = new ArrayList<>();
    private List<Boat> boatEventList = new ArrayList<>();
    private List<Boat> boatRequestList = new ArrayList<>();
    private List<Boat> boatEventRequestList = new ArrayList<>();
    private File fileSchema, boatFile, licenceFile;//
    private URI mMediaUri;
    private Uri photoUri;
    private Handler handler;
    private ProgressDialog progressDialog;
    private ImageView log_img, divingRadioButton, fishingRadioButton, picnicRadioButton, imageView, userImage;
    private EditText usernameEditText, emailEditText, mobileEditText, pageNameEditText;
    private EditText boatNoEditText, boatNameEditText, heightEditText,
            widthEditText, rentValueEditText, otherEnCityEditText,
            otherArCityEditText;
    private TextView cityTxt, select_location, expireDateLicence, expireDateForm, noContentEvent,
            noContentImage, noContentRequest, noContentRequest_, areaWidthTxt, areaHieghtTxt,
            passengersNoEditText, addActivityTxt, noContentBoat;
    private String city_id, boat_id, token, user_id;
    private int fishing, diving, picnic;
    private List<SpinnerItem> spinnerItemCityList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemWidthUnitsList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemHeightUnitsList = new ArrayList<>();
    private Dialog alertDialog;
    private RelativeLayout imageLicenceLayout, infoLayout;
    private double latitude, longitude;
    private String boatName, boatNumber, passengerNumber, lenght, width, hourly_price,
            driver_licence_end_date, boat_licence_end_date, licence_image, location,
            fishingString, divingString, tourString, page_name, width_unit_id, height_unit_id,
            unit_en, unit_ar, unit_w_en, unit_w_ar, is_active;
    private BoatAdapter boatAdapter;
    private ComingEventsAdapter comingEventsAdapter;
    private ImageAdapter imageAdapter;
    private ProgressBar progressbar, progressbar_;
    private BoatRequestAdapter boatRequestAdapter;
    private boolean isFragmentLoaded = false;
    private BoatEventRequestAdapter boatEventRequestAdapter;
    private RatingBar ratingBar;
    private String boat, diver, tank, supplier, service, product;
    private SpinnerAdapter spinnerAdapter;
    private String name;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_boat, container, false);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(getActivity(), "token");
        user_id = AppPreferences.getString(getActivity(), "user_id");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        init(view);
        cityList();
        measureWidthUnitList();
        measureHightUnitList();
        getUserInfo(token);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(View view) {
        RelativeLayout layout = view.findViewById(R.id.layout);
        RelativeLayout shareLayout = view.findViewById(R.id.shareLayout);
        RelativeLayout orderLayout = view.findViewById(R.id.orderLayout);
        RelativeLayout divingLayout = view.findViewById(R.id.divingLayout);
        RelativeLayout fishingRadioButtonLayout = view.findViewById(R.id.fishingRadioButtonLayout);
        RelativeLayout picnicRadioButtonLayout = view.findViewById(R.id.picnicRadioButtonLayout);
        TextView nameTxt = Objects.requireNonNull(getActivity()).findViewById(R.id.nameTxt);
//        nameTxt.setText(R.string.my_profile);
        FontManager.applyFont(getActivity(), layout);
        FontManager.applyFont(getActivity(), nameTxt);

        Button addImageBtn = view.findViewById(R.id.addImageBtn);
        Button addBoatBtn = view.findViewById(R.id.addBoatBtn);
        Button addTripBtn = view.findViewById(R.id.addTripBtn);

        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        Button updateBtn = view.findViewById(R.id.updateBtn);
        Button select_language_btn = view.findViewById(R.id.select_language_btn);
        RelativeLayout cityLayout = view.findViewById(R.id.cityLayout);
        RelativeLayout expireDataLayout = view.findViewById(R.id.expireDataLayout);
        RelativeLayout expireDateLicenceLayout = view.findViewById(R.id.expireDateLicenceLayout);
        TextView changeImageTxt = view.findViewById(R.id.changeImageTxt);
        addActivityTxt = view.findViewById(R.id.addActivityTxt);
        log_img = view.findViewById(R.id.log_img);
        progressbar = view.findViewById(R.id.progressbar);
        progressbar_ = view.findViewById(R.id.progressbar_);
        ratingBar = view.findViewById(R.id.ratingBar);
        noContentBoat = view.findViewById(R.id.noContentBoat);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        infoLayout = view.findViewById(R.id.infoLayout);
        emailEditText = view.findViewById(R.id.emailEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        pageNameEditText = view.findViewById(R.id.pageNameEditText);
        TextView updateTxt = view.findViewById(R.id.updateTxt);

        userImage = view.findViewById(R.id.userImage);
        userImage.setOnClickListener(this);
        addActivityTxt.setOnClickListener(this);
        updateTxt.setOnClickListener(this);
        boatNoEditText = view.findViewById(R.id.boatNoEditText);
        boatNameEditText = view.findViewById(R.id.boatNameEditText);
        passengersNoEditText = view.findViewById(R.id.passengersNoEditText);
        heightEditText = view.findViewById(R.id.heightEditText);
        widthEditText = view.findViewById(R.id.widthEditText);
        rentValueEditText = view.findViewById(R.id.rentValueEditText);
        otherEnCityEditText = view.findViewById(R.id.otherEnCityEditText);
        otherArCityEditText = view.findViewById(R.id.otherArCityEditText);
        cityTxt = view.findViewById(R.id.cityTxt);
        select_location = view.findViewById(R.id.select_location);
        divingRadioButton = view.findViewById(R.id.divingRadioButton);
        fishingRadioButton = view.findViewById(R.id.fishingRadioButton);
        picnicRadioButton = view.findViewById(R.id.picnicRadioButton);
        imageLicenceLayout = view.findViewById(R.id.imageLicenceLayout);
        imageView = view.findViewById(R.id.imageView);
        TextView licenceImage = view.findViewById(R.id.licenceImage);
        expireDateForm = view.findViewById(R.id.expireTxt);
        noContentEvent = view.findViewById(R.id.noContentEvent);
        noContentImage = view.findViewById(R.id.noContentImage);
        noContentRequest = view.findViewById(R.id.noContentRequest);
        noContentRequest_ = view.findViewById(R.id.noContentRequest_);
        areaWidthTxt = view.findViewById(R.id.areaWidthTxt);
        areaHieghtTxt = view.findViewById(R.id.areaHieghtTxt);
        expireDateLicence = view.findViewById(R.id.expireLicenceTxt);
        licenceImage.setOnClickListener(this);
        areaWidthTxt.setOnClickListener(this);
        areaHieghtTxt.setOnClickListener(this);
        expireDataLayout.setOnClickListener(this);
        select_language_btn.setOnClickListener(this);
        expireDateLicenceLayout.setOnClickListener(this);
        picnicRadioButtonLayout.setOnClickListener(this);
        fishingRadioButtonLayout.setOnClickListener(this);
        divingLayout.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        select_location.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        changeImageTxt.setOnClickListener(this);
        log_img.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
        cityLayout.setOnClickListener(this);

        addImageBtn.setOnClickListener(this);
        addBoatBtn.setOnClickListener(this);
        addTripBtn.setOnClickListener(this);
        RelativeLayout minusLayout = view.findViewById(R.id.minusLayout);
        RelativeLayout plusLayout = view.findViewById(R.id.plusLayout);
        minusLayout.setOnClickListener(this);
        plusLayout.setOnClickListener(this);
        RecyclerView recycleView = view.findViewById(R.id.recycleViewBoats);
        boatAdapter = new BoatAdapter(getActivity(), boatList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                final String id_boat = boatList.get(position).getId();

                if (id == R.id.ic_edit) {
                    Intent intent = new Intent(getActivity(), AddBoatActivity.class);
                    intent.putExtra("boat_id", id_boat);
                    startActivityForResult(intent, 12);
                } else if (id == R.id.ic_delete) {
                    AppErrorsManager.showSuccessDialog(getActivity(), getString(R.string.delet_process),
                            getString(R.string.are_you_need_delete_boat), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteBoat(token, id_boat);
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
        recycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setNestedScrollingEnabled(false);
        recycleView.setAdapter(boatAdapter);
        getBoatList(token);

        RecyclerView recycleViewActivity = view.findViewById(R.id.recycleViewActivity);
        comingEventsAdapter = new ComingEventsAdapter(getActivity(), boatEventList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                final String trip_id = boatEventList.get(position).getTrip_id();

                if (id == R.id.ic_edit) {
                    Intent intent = new Intent(getActivity(), AddBoatTripActivity.class);
                    intent.putExtra("trip_id", trip_id);
                    startActivityForResult(intent, 13);
                } else if (id == R.id.ic_delete) {
                    AppErrorsManager.showSuccessDialog(getActivity(), getString(R.string.delet_process),
                            getString(R.string.are_you_need_delete_trip), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteTripBoat(token, trip_id);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager_ = new LinearLayoutManager(getActivity());
        recycleViewActivity.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
        recycleViewActivity.setLayoutManager(mLayoutManager_);
        recycleViewActivity.setItemAnimator(new DefaultItemAnimator());
        recycleViewActivity.setNestedScrollingEnabled(false);
        recycleViewActivity.setAdapter(comingEventsAdapter);
        getBoatTripList(token, user_id);

        RecyclerView recycleViewBoatRequest = view.findViewById(R.id.recycleViewBoatRequest);
        boatRequestAdapter = new BoatRequestAdapter(getActivity(), boatRequestList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                String request_id = boatRequestList.get(position).getId();
                if (id == R.id.ic_approve) {
                    addResponse(request_id, "1", "boat");
                } else if (id == R.id.ic_cancel) {
                    addResponse(request_id, "2", "boat");
                } else if (id == R.id.ratingTxt) {
                    requestRating(request_id);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager__ = new LinearLayoutManager(getActivity());
        recycleViewBoatRequest.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewBoatRequest.setLayoutManager(mLayoutManager__);
        recycleViewBoatRequest.setItemAnimator(new DefaultItemAnimator());
        recycleViewBoatRequest.setNestedScrollingEnabled(false);
        recycleViewBoatRequest.setAdapter(boatRequestAdapter);
        boatRequestData();

        RecyclerView recycleViewEventRequest = view.findViewById(R.id.recycleViewEventRequest);
        boatEventRequestAdapter = new BoatEventRequestAdapter(getActivity(), boatEventRequestList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                String request_id = boatEventRequestList.get(position).getId();
                Log.e("request_id", request_id);
                if (id == R.id.ic_approve) {
                    addResponse(request_id, "1", "trip");
                } else if (id == R.id.ic_cancel) {
                    addResponse(request_id, "2", "trip");
                } else if (id == R.id.ratingTxt) {
                    requestRating(request_id);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager___ = new LinearLayoutManager(getActivity());
        recycleViewEventRequest.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewEventRequest.setLayoutManager(mLayoutManager___);
        recycleViewEventRequest.setItemAnimator(new DefaultItemAnimator());
        recycleViewEventRequest.setNestedScrollingEnabled(false);
        recycleViewEventRequest.setAdapter(boatEventRequestAdapter);
        boatTripRequestData();

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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        int totalCount = Integer.parseInt(passengersNoEditText.getText().toString());
        if (id == R.id.addImageBtn) {
            if (!TextUtils.equals(is_active, "0")) {
                openDialog2();
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.orderLayout) {
            Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
            startActivity(intent);
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
        } else if (id == R.id.areaHieghtTxt) {
            openWindowHeightUnits();
        } else if (id == R.id.areaWidthTxt) {
            openWindowWidthUnits();
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                passengersNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            passengersNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.addBoatBtn) {
            if (!TextUtils.equals(is_active, "0")) {
                Intent intent = new Intent(getActivity(), AddBoatActivity.class);
                startActivityForResult(intent, 12);
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.addTripBtn) {
            if (!TextUtils.equals(is_active, "0")) {
                Intent intent = new Intent(getActivity(), AddBoatTripActivity.class);
                startActivityForResult(intent, 13);
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.logoutBtn) {
            FontManager.logOut(getActivity());
        } else if (id == R.id.select_language_btn) {
            AppLanguage.openDialogLanguage(getActivity());
        } else if (id == R.id.updateBtn) {
            updateUser();
        } else if (id == R.id.log_img || id == R.id.changeImageTxt || id == R.id.userImage) {
            if (!TextUtils.equals(is_active, "0")) {
                openDialog();
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.licenceImage) {
            openDialog1();
        } else if (id == R.id.cityLayout) {
            openWindowCity();
        } else if (id == R.id.select_location) {
            Intent intent = new Intent(getActivity(), BigSelectMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, 10);
        } else if (id == R.id.divingLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(divingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving = 1;
                } else if (Objects.equals(divingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    diving = 0;
                }
            } else {
                if (Objects.requireNonNull(divingRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving = 1;
                } else if (divingRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    divingRadioButton.setImageResource(R.drawable.ic_circle);
                    diving = 0;
                }
            }
        } else if (id == R.id.fishingRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(fishingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(fishingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    fishing = 1;
                } else if (Objects.equals(fishingRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(fishingRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    fishing = 0;
                }
            } else {
                if (Objects.requireNonNull(fishingRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                    fishing = 1;
                } else if (fishingRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    fishingRadioButton.setImageResource(R.drawable.ic_circle);
                    fishing = 0;
                }
            }
        } else if (id == R.id.picnicRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(picnicRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(picnicRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                    picnic = 1;
                } else if (Objects.equals(picnicRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(picnicRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    picnic = 0;
                }
            } else {
                if (Objects.requireNonNull(picnicRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                    picnic = 1;
                } else if (picnicRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    picnicRadioButton.setImageResource(R.drawable.ic_circle);
                    picnic = 0;
                }
            }
        } else if (id == R.id.expireDataLayout) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "first");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getFragmentManager(), "Date Picker");
        } else if (id == R.id.expireDateLicenceLayout) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "second");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getFragmentManager(), "Date Picker");
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(Objects.requireNonNull(getActivity()), name, getString(R.string.boat));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowWidthUnits() {
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

                for (int j = 0; j < spinnerItemWidthUnitsList.size(); j++) {

                    final String text = spinnerItemWidthUnitsList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemWidthUnitsList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemWidthUnitsList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemWidthUnitsList.get(j));
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
                        width_unit_id = filteredList.get(position).getId();
                        areaWidthTxt.setText(status);
                        areaWidthTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemWidthUnitsList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                    status = spinnerItemWidthUnitsList.get(position).getTextA();
                } else {
                    status = spinnerItemWidthUnitsList.get(position).getText();
                }
                width_unit_id = spinnerItemWidthUnitsList.get(position).getId();
                areaWidthTxt.setText(status);
                areaWidthTxt.setError(null);
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
    public void openWindowHeightUnits() {
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

                for (int j = 0; j < spinnerItemHeightUnitsList.size(); j++) {

                    final String text = spinnerItemHeightUnitsList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemHeightUnitsList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemHeightUnitsList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemHeightUnitsList.get(j));
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
                        height_unit_id = filteredList.get(position).getId();
                        areaHieghtTxt.setText(status);
                        areaHieghtTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemHeightUnitsList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                    status = spinnerItemHeightUnitsList.get(position).getTextA();
                } else {
                    status = spinnerItemHeightUnitsList.get(position).getText();
                }
                height_unit_id = spinnerItemHeightUnitsList.get(position).getId();
                areaHieghtTxt.setText(status);
                areaHieghtTxt.setError(null);
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

    public void measureWidthUnitList() {
        spinnerItemWidthUnitsList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "measure_units").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        assert response.body() != null;
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("measure_units", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("Measure_units");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                width_unit_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("name_en");
                                String name_ar = jsonObject1.getString("name_ar");
                                SpinnerItem cityData = new SpinnerItem(width_unit_id, name_en, name_ar);
                                spinnerItemWidthUnitsList.add(cityData);
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

    public void measureHightUnitList() {
        spinnerItemHeightUnitsList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "measure_units").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("measure_units", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("Measure_units");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                height_unit_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("name_en");
                                String name_ar = jsonObject1.getString("name_ar");
                                SpinnerItem cityData = new SpinnerItem(height_unit_id, name_en, name_ar);
                                spinnerItemHeightUnitsList.add(cityData);
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

    public void getUserInfo(final String token) {

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
//                            user_id = userObject.getString("id");
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

                            String partner_boat = successObject.getString("partner_boat");
                            JSONArray boatArray = new JSONArray(partner_boat);
                            for (int i = 0; i < boatArray.length(); i++) {
                                JSONObject boatObject = boatArray.getJSONObject(0);
                                boat_id = boatObject.getString("id");
                                boatName = boatObject.getString("name");
                                boatNumber = boatObject.getString("number");
                                passengerNumber = boatObject.getString("passengers");
                                lenght = boatObject.getString("lenght");
                                width = boatObject.getString("width");
                                page_name = boatObject.getString("page_name");
                                location = boatObject.getString("location");
                                is_active = boatObject.getString("is_active");

                                String lenght_id = boatObject.getString("lenght_id");
                                JSONObject lenghtObject = new JSONObject(lenght_id);
                                height_unit_id = lenghtObject.getString("id");
                                unit_en = lenghtObject.getString("name_en");
                                unit_ar = lenghtObject.getString("name_ar");

                                String width_id = boatObject.getString("width_id");
                                JSONObject widthObject = new JSONObject(width_id);
                                width_unit_id = widthObject.getString("id");
                                unit_w_en = widthObject.getString("name_en");
                                unit_w_ar = widthObject.getString("name_ar");

                                String[] namesList = location.split(",");
                                String name1 = namesList[0];
                                String name2 = namesList[1];
                                latitude = Double.parseDouble(name1);
                                longitude = Double.parseDouble(name2);
                                hourly_price = boatObject.getString("hourly_price");
                                driver_licence_end_date = boatObject.getString("driver_licence_end_date");
                                boat_licence_end_date = boatObject.getString("boat_licence_end_date");
                                licence_image = boatObject.getString("licence_image");
                                final String trip_type = boatObject.getString("trip_type");
                                JSONObject tripObject = new JSONObject(trip_type);
                                fishingString = tripObject.getString("fishing");
                                divingString = tripObject.getString("diving");
                                tourString = tripObject.getString("tour");
                                fishing = Integer.parseInt(fishingString);
                                diving = Integer.parseInt(divingString);
                                picnic = Integer.parseInt(tourString);
                                Log.e("trip_type", trip_type);
                            }

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
                                    getImages(token);
                                    usernameEditText.setText(name);
                                    if (!email.equals("null"))
                                        emailEditText.setText(email);
                                    mobileEditText.setText(mobile);
                                    if (!page_name.equals("null"))
                                        pageNameEditText.setText(page_name);

                                    if (!TextUtils.equals(user_image, "null")) {
                                        progressbar.setVisibility(View.VISIBLE);
                                        userImage.setVisibility(View.GONE);
                                        log_img.setVisibility(View.VISIBLE);
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
                                        log_img.setVisibility(View.GONE);
                                        userImage.setVisibility(View.VISIBLE);
                                        userImage.setImageResource(R.drawable.img_user);
                                    }
//
                                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                                        cityTxt.setText(city_ar);
                                        areaHieghtTxt.setText(unit_ar);
                                        areaWidthTxt.setText(unit_w_ar);
                                    } else {
                                        cityTxt.setText(city_en);
                                        areaHieghtTxt.setText(unit_en);
                                        areaWidthTxt.setText(unit_w_en);
                                    }


                                    boatNoEditText.setText(boatNumber);
                                    boatNameEditText.setText(boatName);
                                    passengersNoEditText.setText(passengerNumber);
                                    heightEditText.setText(lenght);
                                    widthEditText.setText(width);
                                    rentValueEditText.setText(hourly_price);
                                    if (!licence_image.equals("null")) {
                                        imageLicenceLayout.setVisibility(View.VISIBLE);
                                        progressbar_.setVisibility(View.VISIBLE);
                                        Picasso.get()
                                                .load(FontManager.IMAGE_URL + licence_image)
                                                .into(imageView, new Callback() {
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
                                    select_location.setText(location);
                                    expireDateLicence.setText(driver_licence_end_date);
                                    expireDateForm.setText(boat_licence_end_date);

                                    if (TextUtils.equals(fishingString, "1")) {
                                        fishingRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    } else {
                                        fishingRadioButton.setImageResource(R.drawable.ic_circle);
                                    }
                                    if (TextUtils.equals(divingString, "1")) {
                                        divingRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    } else {
                                        divingRadioButton.setImageResource(R.drawable.ic_circle);
                                    }
                                    if (TextUtils.equals(tourString, "1")) {
                                        picnicRadioButton.setImageResource(R.drawable.ic_check_circle);
                                    } else {
                                        picnicRadioButton.setImageResource(R.drawable.ic_circle);
                                    }
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
        final String email = emailEditText.getText().toString().trim();
        final String cityName = cityTxt.getText().toString().trim();
        final String cityName_ar = otherArCityEditText.getText().toString().trim();
        final String cityName_en = otherEnCityEditText.getText().toString().trim();
        final String page_name = pageNameEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String boatNumber = boatNoEditText.getText().toString().trim();
        final String boatName = boatNameEditText.getText().toString().trim();
        final String passengersNumber = passengersNoEditText.getText().toString().trim();
        final String width = widthEditText.getText().toString().trim();
        final String height = heightEditText.getText().toString().trim();
        final String widthTxt = areaWidthTxt.getText().toString().trim();
        final String heightTxt = areaHieghtTxt.getText().toString().trim();
        final String rentValue = rentValueEditText.getText().toString().trim();
        final String location = select_location.getText().toString().trim();
        final String expireTxt = expireDateForm.getText().toString().trim();
        final String expireLicenceTxt = expireDateLicence.getText().toString().trim();
        final String city = cityTxt.getText().toString().trim();

        int is_valid = 0;
        if (!TextUtils.isEmpty(email) && !email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            emailEditText.requestFocus();
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
            } else if (TextUtils.isEmpty(boatNumber)) {
                boatNoEditText.setError(getString(R.string.error_field_required));
                boatNoEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(boatName)) {
                boatNameEditText.setError(getString(R.string.error_field_required));
                boatNameEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(passengersNumber)) {
                passengersNoEditText.setError(getString(R.string.error_field_required));
                passengersNoEditText.requestFocus();
                is_valid = 1;
            } else if (passengersNumber.equals("0")) {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.you_should_enter_quantity));
                is_valid = 1;
            } else if (TextUtils.isEmpty(city)) {
                cityTxt.setError(getString(R.string.error_field_required));
                is_valid = 1;
                cityTxt.requestFocus();
            } else if (TextUtils.isEmpty(height)) {
                heightEditText.setError(getString(R.string.error_field_required));
                heightEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(heightTxt)) {
                areaHieghtTxt.setError(getString(R.string.error_field_required));
                areaHieghtTxt.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(width)) {
                widthEditText.setError(getString(R.string.error_field_required));
                widthEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(widthTxt)) {
                areaWidthTxt.setError(getString(R.string.error_field_required));
                areaWidthTxt.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(location)) {
                select_location.setError(getString(R.string.error_field_required));
                select_location.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(rentValue)) {
                rentValueEditText.setError(getString(R.string.error_field_required));
                rentValueEditText.requestFocus();
                is_valid = 1;
            } else if (diving == 0 && fishing == 0 && picnic == 0) {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.you_should_select_trip_type));
                is_valid = 1;
            } else if (TextUtils.isEmpty(expireTxt)) {
                expireDateForm.setError(getString(R.string.error_field_required));
                is_valid = 1;
                expireDateForm.requestFocus();
            } else if (TextUtils.isEmpty(expireLicenceTxt)) {
                expireDateLicence.setError(getString(R.string.error_field_required));
                is_valid = 1;
                expireDateLicence.requestFocus();
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
                                .addFormDataPart("page_name", page_name)
                                .addFormDataPart("boat_no", boatNumber)
                                .addFormDataPart("boat_name", boatName)
                                .addFormDataPart("boat_passengers", passengersNumber)
                                .addFormDataPart("boat_length", height)
                                .addFormDataPart("lenght_id", height_unit_id)
                                .addFormDataPart("boat_width", width)
                                .addFormDataPart("width_id", width_unit_id)
                                .addFormDataPart("boat_location", location)
                                .addFormDataPart("boat_hourly", rentValue)
                                .addFormDataPart("boat_trip_fishing", String.valueOf(fishing))
                                .addFormDataPart("boat_trip_diving", String.valueOf(diving))
                                .addFormDataPart("boat_trip_tour", String.valueOf(picnic))
                                .addFormDataPart("boat_licence_expire_date", expireTxt)
                                .addFormDataPart("boat_driving_licence_expire_date", expireLicenceTxt)
                                .addFormDataPart("id", boat_id);

                        if (TextUtils.equals(cityName, getString(R.string.other))) {
                            builder.addFormDataPart("city_title_ar", cityName_ar);
                            builder.addFormDataPart("city_title_en", cityName_en);
                        } else
                            builder.addFormDataPart("city", city_id);

                        if (fileSchema != null) {
                            builder.addFormDataPart("user_image", fileSchema.getName(),
                                    RequestBody.create(MediaType.parse("jpeg/png"), fileSchema));
                        }
                        if (licenceFile != null) {
                            builder.addFormDataPart("boat_licence_image", licenceFile.getName(),
                                    RequestBody.create(MediaType.parse("jpeg/png"), licenceFile));
                        }
                        RequestBody requestBody = builder.build();
                        updateUserInfo(requestBody);
                    }
                }).start();
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
                    OkHttpClient client;

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(5, TimeUnit.MINUTES)
                            .writeTimeout(5, TimeUnit.MINUTES)
                            .readTimeout(5, TimeUnit.MINUTES);
                    client = builder.build();

                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "update_boat_profile").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Accept", "application/json")
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
                                        AppErrorsManager.showSuccessDialog(getActivity(), getString(R.string.update_successfully), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                getBoatList(token);
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

    public void getBoatList(final String token) {
        boatList.clear();
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
                            + "user-boats").get()
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
                            if (TextUtils.equals(success, "[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentBoat.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                JSONArray successObject = new JSONArray(success);
                                for (int i = 0; i < successObject.length(); i++) {
                                    JSONObject boatObject = successObject.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String boatName = boatObject.getString("name");
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

    public void deleteBoat(final String token, final String boat_id) {
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
                            + "boat/" + boat_id).delete()
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
                            if (success.equals("true")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        getBoatList(token);
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
                            + "booking_action_delete/" + boat_id).delete()
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
                                boatRequestData();

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

    public void deleteTripBoat(final String token, final String boat_id) {
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
                            + "delete-trip/" + boat_id).delete()
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
                            if (success.equals("true")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        getBoatTripList(token, user_id);
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

    public void boatRequestData() {
        boatRequestList.clear();
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
                            + "partner-book-boat/" + user_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatListRequest", response_data);
                        try {

                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String trips = successObject.getString("trips");
                            if (TextUtils.equals(trips, "[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentRequest.setVisibility(View.VISIBLE);
                                    }
                                });

                            } else {
                                JSONArray tripsArray = new JSONArray(trips);
                                for (int i = 0; i < tripsArray.length(); i++) {
                                    JSONObject boatObject = tripsArray.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String boatName = boatObject.getString("boat_name");
                                    String user_name = boatObject.getString("user_name");
                                    String guid = boatObject.getString("guid");
                                    String mobile = boatObject.getString("mobile");
                                    String created_at = boatObject.getString("created_at");
                                    String approved = boatObject.getString("approved");
                                    String is_rating = boatObject.getString("is_rating");
                                    String passengers = boatObject.getString("passengers");
                                    String start_time = boatObject.getString("start_time");
                                    String start_date = boatObject.getString("start_date");
                                    String duration = boatObject.getString("duration");
                                    String trip_type = boatObject.getString("trip_type");
                                    String route = boatObject.getString("route");
                                    Log.e("route", route);
                                    Boat boat = new Boat(id, boatName, guid, mobile, created_at,
                                            approved, is_rating, passengers, start_time, start_date,
                                            duration, trip_type, route, user_name, "");
                                    boatRequestList.add(boat);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentRequest.setVisibility(View.GONE);
                                            boatRequestAdapter.notifyDataSetChanged();
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

    public void boatTripRequestData() {
        boatEventRequestList.clear();
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
                            + "partner_book_boat_trip/" + user_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("boatListRequest", response_data);
                        try {

                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            String trips = successObject.getString("trips");
                            if (TextUtils.equals(trips, "[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentRequest_.setVisibility(View.VISIBLE);
                                    }
                                });

                            } else {
                                JSONArray tripsArray = new JSONArray(trips);
                                for (int i = 0; i < tripsArray.length(); i++) {
                                    JSONObject boatObject = tripsArray.getJSONObject(i);
                                    String id = boatObject.getString("book_id");
                                    String boatName = boatObject.getString("boat_name");
                                    String guid = boatObject.getString("guid");
                                    String mobile = boatObject.getString("mobile");
                                    String created_at = boatObject.getString("created_at");
                                    String approved = boatObject.getString("approved");
                                    String is_rating = boatObject.getString("is_rating");
                                    String user_name = boatObject.getString("user_name");

                                    String start_date = boatObject.getString("start_date");
                                    String start_time = boatObject.getString("start_time");
                                    String trip_type = boatObject.getString("trip_type");

                                    Boat boat = new Boat(id, boat_id, boatName, trip_type, start_date
                                            , start_time, approved, is_rating, user_name, guid);
                                    boatEventRequestList.add(boat);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentRequest_.setVisibility(View.GONE);
                                            boatEventRequestAdapter.notifyDataSetChanged();
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

    public void getBoatTripList(final String token, final String user_id) {
        boatEventList.clear();
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
                            + "partner-trips/" + user_id).get()
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
                        .addFormDataPart("partner_id", boat_id)
                        .addFormDataPart("object_tb", "partner_boats")
                        .addFormDataPart("is_default", "1")
                        .addFormDataPart("object_id", boat_id);
//                partner_boats,partner_divers,partner_jetski,partner_sellers,partner_services,partner_supplies
                if (boatFile != null) {
                    builder.addFormDataPart("image", boatFile.getName(),
                            RequestBody.create(MediaType.parse("jpeg/png"), boatFile));
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
                            + "boat/" + boat_id).get()
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
                            String user_info = successObject.getString("boats");
                            if (!user_info.equals("null")) {
                                JSONObject userObject = new JSONObject(user_info);
                                final String images = userObject.getString("images");
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

    public void addResponse(final String id, final String approved, final String trip) {
        Log.e("yyyy", "uuuuu");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", id)
                        .addFormDataPart("approved", approved);
                RequestBody requestBody = builder.build();
                addResponseInfo(requestBody, trip);
            }
        }).start();
    }

    public void addResponseInfo(final RequestBody requestBody, final String trip) {
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
                            + "book-response").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("book-response", response_data);
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
                                                        if (trip.equals("boat"))
                                                            boatRequestData();
                                                        else if (trip.equals("trip"))
                                                            boatTripRequestData();
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
                            + "request_rating").post(requestBody)
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
                                                        boatRequestData();
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
        userImage.setVisibility(View.GONE);
        log_img.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(log_img);
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
        userImage.setVisibility(View.GONE);
        log_img.setVisibility(View.VISIBLE);
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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        boatFile = new File(pdfPathHolder);
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
        boatFile = new File(mMediaUri);
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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        imageLicenceLayout.setVisibility(View.VISIBLE);
        progressbar_.setVisibility(View.GONE);
        Picasso.get().load(uri).into(imageView, new Callback() {
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
        imageLicenceLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(photoUri).into(imageView);
        imageView.setRotation(90);
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
            select_location.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location.setError(null);
        } else if (requestCode == 12 && data != null) {
            Log.e("dddd0", "dddd");
            progressDialog.show();
            getBoatList(token);
        } else if (requestCode == 13 && data != null) {
            Log.e("dddd130", "dddd");
            progressDialog.show();
            getBoatTripList(token, user_id);
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
