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

import com.apps.fatima.sealocation.activities.AddCourseDivingActivity;
import com.apps.fatima.sealocation.activities.AddDivingTripActivity;
import com.apps.fatima.sealocation.activities.BoatsOtherActivity;
import com.apps.fatima.sealocation.activities.DiversOtherActivity;
import com.apps.fatima.sealocation.activities.MyOrdersActivity;
import com.apps.fatima.sealocation.activities.SellerOtherActivity;
import com.apps.fatima.sealocation.activities.ServicesOtherActivity;
import com.apps.fatima.sealocation.activities.SupplierOtherActivity;
import com.apps.fatima.sealocation.activities.TankOtherActivity;
import com.apps.fatima.sealocation.adapter.CourseRequestAdapter;
import com.apps.fatima.sealocation.adapter.TripEventRequestAdapter;
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
import com.apps.fatima.sealocation.activities.LoginActivity;
import com.apps.fatima.sealocation.adapter.CourseAdapter;
import com.apps.fatima.sealocation.adapter.ImageAdapter;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.apps.fatima.sealocation.adapter.TripAdapter;
import com.apps.fatima.sealocation.model.Course;
import com.apps.fatima.sealocation.model.Image;
import com.apps.fatima.sealocation.model.Trip;
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

public class ProfileDrivingFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 51;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_1 = 52;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1 = 53;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_2 = 54;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2 = 55;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_3 = 56;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_3 = 57;
    private List<Image> imageList = new ArrayList<>();
    private List<Course> courseList = new ArrayList<>();
    private List<Trip> tripList = new ArrayList<>();
    private List<Course> courseRequestList = new ArrayList<>();
    private List<Trip> tripEventList = new ArrayList<>();
    private File fileSchema, driverFile, licenceFile, otherLicenceFile;//
    private URI mMediaUri;
    private Uri photoUri;
    private EditText usernameEditText, emailEditText, mobileEditText, pageNameEditText;
    private EditText driverNameEditText, licenceDrivingEditText, noteEditText;
    private EditText driverOtherNameEditText, licenceDrivingOtherEditText,
            otherEnLicenceLevelEditText, otherArLicenceLevelEditText, otherEnCompanyEditText,
            otherArCompanyEditText, otherEnCompanyOtherEditText, otherArCompanyOtherEditText,
            otherEnLicencetEditText, otherArLicencetEditText, otherEnCityEditText,
            otherArCityEditText;
    private TextView licenceLevelTxt, licenceLevelOtherTxt, companyTxt, companyOtherTxt,
            select_location_driving, cityTxt, noContentImage, noContentCourses, noContentTrip,
            noContentCourse, noContentTripEvent, addActivityTxt;
    private ImageView freeRadioButton, divingRadio, imageViewNew, log_img, imageViewOtherNew, userImage;
    private RelativeLayout imageNewLayout, otherLayout, imageNewOtherLayout;
    private Handler handler;
    private ProgressDialog progressDialog;
    private double latitude, longitude;
    private int diving_cylinder, diving_free;
    private List<SpinnerItem> spinnerItemCompanyList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemLevelList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemLevelOtherList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemCompanyOtherList = new ArrayList<>();
    private SpinnerAdapter spinnerAdapter;
    private Dialog alertDialog;
    private String city_id, company_id, companyOther_id, level_id, levelOther_id, diver_id, token, is_active;
    private List<SpinnerItem> spinnerItemCityList = new ArrayList<>();
    private CourseAdapter courseAdapter;
    private ImageAdapter imageAdapter;
    private TripAdapter tripAdapter;
    private ProgressBar progressbarTwo, progressbarOne, progressbar;
    private String user_id;
    private boolean isFragmentLoaded = false;
    private CourseRequestAdapter courseRequestAdapter;
    private TripEventRequestAdapter tripEventRequestAdapter;
    private RatingBar ratingBar;
    private RelativeLayout infoLayout;
    private String boat, diver, tank, supplier, service, product;
    private String name;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_driving, container, false);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(getActivity(), "token");
        getUserInfo(token);
        init(view);
        companyList();
        licenceLevelList();
        cityList();
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

        progressbarTwo = view.findViewById(R.id.progressbarTwo);
        userImage = view.findViewById(R.id.userImage);
        ratingBar = view.findViewById(R.id.ratingBar);
        infoLayout = view.findViewById(R.id.infoLayout);
        progressbar = view.findViewById(R.id.progressbar);
        noContentCourse = view.findViewById(R.id.noContentCourse);
        noContentTripEvent = view.findViewById(R.id.noContentTripEvent);
        progressbarOne = view.findViewById(R.id.progressbarOne);
        Button addImageBtn = view.findViewById(R.id.addImageBtn);
        Button addCourseBtn = view.findViewById(R.id.addCourseBtn);
        Button addTripBtn = view.findViewById(R.id.addTripBtn);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        RelativeLayout licenceLevelLayout = view.findViewById(R.id.licenceLevelLayout);
        RelativeLayout licenceLevelOtherLayout = view.findViewById(R.id.licenceLevelOtherLayout);
        RelativeLayout companyLayout = view.findViewById(R.id.companyLayout);
        RelativeLayout cityLayout = view.findViewById(R.id.cityLayout);
        RelativeLayout companyOtherLayout = view.findViewById(R.id.companyOtherLayout);
        TextView select_licence_new = view.findViewById(R.id.select_licence_new);
        TextView select_licence_other_new = view.findViewById(R.id.select_licence_other_new);
        TextView changeImageTxt = view.findViewById(R.id.changeImageTxt);
        TextView updateTxt = view.findViewById(R.id.updateTxt);
        addActivityTxt = view.findViewById(R.id.addActivityTxt);
        Button select_language_btn = view.findViewById(R.id.select_language_btn);
        Button updateBtn = view.findViewById(R.id.updateBtn);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        pageNameEditText = view.findViewById(R.id.pageNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        licenceLevelTxt = view.findViewById(R.id.licenceLevelTxt);
        companyTxt = view.findViewById(R.id.companyTxt);
        cityTxt = view.findViewById(R.id.cityTxt);
        noContentImage = view.findViewById(R.id.noContentImage);
        noContentCourses = view.findViewById(R.id.noContentCourses);
        noContentTrip = view.findViewById(R.id.noContentTrip);
        companyOtherTxt = view.findViewById(R.id.companyOtherTxt);
        licenceLevelOtherTxt = view.findViewById(R.id.licenceLevelOtherTxt);
        log_img = view.findViewById(R.id.log_img);
        log_img.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
        otherLayout = view.findViewById(R.id.otherLayout);
        select_location_driving = view.findViewById(R.id.select_location_driving);
        freeRadioButton = view.findViewById(R.id.freeRadioButton);
        divingRadio = view.findViewById(R.id.divingRadio);
        imageViewNew = view.findViewById(R.id.imageViewNew);
        imageViewOtherNew = view.findViewById(R.id.imageViewOtherNew);
        imageNewLayout = view.findViewById(R.id.imageNewLayout);
        imageNewOtherLayout = view.findViewById(R.id.imageNewOtherLayout);
        licenceLevelLayout.setOnClickListener(this);
        licenceLevelOtherLayout.setOnClickListener(this);
        addImageBtn.setOnClickListener(this);
        updateTxt.setOnClickListener(this);
        cityLayout.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        addCourseBtn.setOnClickListener(this);
        addTripBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        companyLayout.setOnClickListener(this);
        companyOtherLayout.setOnClickListener(this);
        select_location_driving.setOnClickListener(this);
        userImage.setOnClickListener(this);
        changeImageTxt.setOnClickListener(this);
        addActivityTxt.setOnClickListener(this);
        select_licence_new.setOnClickListener(this);
        select_licence_other_new.setOnClickListener(this);
        divingRadio.setOnClickListener(this);
        freeRadioButton.setOnClickListener(this);
        select_language_btn.setOnClickListener(this);


        driverNameEditText = view.findViewById(R.id.driverNameEditText);
        licenceDrivingEditText = view.findViewById(R.id.licenceDrivingEditText);
        driverOtherNameEditText = view.findViewById(R.id.driverOtherNameEditText);
        licenceDrivingOtherEditText = view.findViewById(R.id.licenceDrivingOtherEditText);
        noteEditText = view.findViewById(R.id.noteEditText);

        otherEnLicenceLevelEditText = view.findViewById(R.id.otherEnLicenceLevelEditText);
        otherArLicenceLevelEditText = view.findViewById(R.id.otherArLicenceLevelEditText);
        otherEnCompanyEditText = view.findViewById(R.id.otherEnCompanyEditText);
        otherArCompanyEditText = view.findViewById(R.id.otherArCompanyEditText);
        otherEnCompanyOtherEditText = view.findViewById(R.id.otherEnCompanyOtherEditText);
        otherArCompanyOtherEditText = view.findViewById(R.id.otherArCompanyOtherEditText);
        otherEnLicencetEditText = view.findViewById(R.id.otherEnLicencetEditText);
        otherArLicencetEditText = view.findViewById(R.id.otherArLicencetEditText);
        otherArCityEditText = view.findViewById(R.id.otherArCityEditText);
        otherEnCityEditText = view.findViewById(R.id.otherEnCityEditText);

        RecyclerView recycleViewCourses = view.findViewById(R.id.recycleViewCourses);
        courseAdapter = new CourseAdapter(getActivity(), courseList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                String course_id = courseList.get(position).getId();
                if (id == R.id.ic_edit) {
                    Intent intent = new Intent(getActivity(), AddCourseDivingActivity.class);
                    intent.putExtra("course_id", course_id);
                    startActivityForResult(intent, 14);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycleViewCourses.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewCourses.setLayoutManager(mLayoutManager);
        recycleViewCourses.setItemAnimator(new DefaultItemAnimator());
        recycleViewCourses.setNestedScrollingEnabled(false);
        recycleViewCourses.setAdapter(courseAdapter);

        RecyclerView recycleViewImage = view.findViewById(R.id.recycleViewImage);
        imageAdapter = new ImageAdapter(getActivity(), imageList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String image_id = imageList.get(position).getId();
                deleteImage(token, image_id);

            }
        });
        RecyclerView.LayoutManager mLayoutManager__ = new LinearLayoutManager(getActivity());
        recycleViewImage.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewImage.setLayoutManager(mLayoutManager__);
        recycleViewImage.setItemAnimator(new DefaultItemAnimator());
        recycleViewImage.setNestedScrollingEnabled(false);
        recycleViewImage.setAdapter(imageAdapter);

        RecyclerView recycleViewTrip = view.findViewById(R.id.recycleViewTrip);
        tripAdapter = new TripAdapter(getActivity(), tripList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                final String trip_id = tripList.get(position).getId();
                if (id == R.id.ic_edit) {
                    Intent intent = new Intent(getActivity(), AddDivingTripActivity.class);
                    intent.putExtra("trip_id", trip_id);
                    startActivityForResult(intent, 13);
                } else if (id == R.id.ic_delete) {
                    AppErrorsManager.showSuccessDialog(getActivity(), getString(R.string.delet_process),
                            getString(R.string.are_you_need_delete_trip), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteTrip(token, trip_id);
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
        recycleViewTrip.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
        recycleViewTrip.setLayoutManager(mLayoutManager_);
        recycleViewTrip.setItemAnimator(new DefaultItemAnimator());
        recycleViewCourses.setNestedScrollingEnabled(false);
        recycleViewTrip.setAdapter(tripAdapter);

        RecyclerView recycleViewCoursesRequest = view.findViewById(R.id.recycleViewCoursesRequest);
        courseRequestAdapter = new CourseRequestAdapter(getActivity(), courseRequestList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                String request_id = courseRequestList.get(position).getId();
                if (id == R.id.ic_approve) {
                    addResponse(request_id, "1");
                } else if (id == R.id.ic_cancel) {
                    addResponse(request_id, "2");
                } else if (id == R.id.ratingTxt) {
                    requestRating(request_id);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager__1 = new LinearLayoutManager(getActivity());
        recycleViewCoursesRequest.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewCoursesRequest.setLayoutManager(mLayoutManager__1);
        recycleViewCoursesRequest.setItemAnimator(new DefaultItemAnimator());
        recycleViewCoursesRequest.setNestedScrollingEnabled(false);
        recycleViewCoursesRequest.setAdapter(courseRequestAdapter);


        RecyclerView recycleViewTripRequest = view.findViewById(R.id.recycleViewTripRequest);
        tripEventRequestAdapter = new TripEventRequestAdapter(getActivity(), tripEventList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                String request_id = tripEventList.get(position).getId();
                if (id == R.id.ic_approve) {
                    addTripResponse(request_id, "1");
                } else if (id == R.id.ic_cancel) {
                    addTripResponse(request_id, "2");
                } else if (id == R.id.ratingTxt) {
                    requestTripRating(request_id);
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager___ = new LinearLayoutManager(getActivity());
        recycleViewTripRequest.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleViewTripRequest.setLayoutManager(mLayoutManager___);
        recycleViewTripRequest.setItemAnimator(new DefaultItemAnimator());
        recycleViewTripRequest.setNestedScrollingEnabled(false);
        recycleViewTripRequest.setAdapter(tripEventRequestAdapter);
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
        } else if (id == R.id.orderLayout) {
            Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
            startActivity(intent);
        } else if (id == R.id.addCourseBtn) {
            if (!TextUtils.equals(is_active, "0")) {
                Intent intent = new Intent(getActivity(), AddCourseDivingActivity.class);
                intent.putExtra("diver_id", diver_id);
                startActivityForResult(intent, 14);
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
        } else if (id == R.id.addTripBtn) {
            if (!TextUtils.equals(is_active, "0")) {
                Intent intent = new Intent(getActivity(), AddDivingTripActivity.class);
                startActivityForResult(intent, 13);
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.updateBtn) {
            updateDriver();
        } else if (id == R.id.cityLayout) {
            openWindowCity();
        } else if (id == R.id.logoutBtn) {
            logOut();
        } else if (id == R.id.select_language_btn) {
            AppLanguage.openDialogLanguage(getActivity());
        } else if (id == R.id.licenceLevelLayout) {
            openWindowLevel();
        } else if (id == R.id.licenceLevelOtherLayout) {
            openWindowLevelOther();
        } else if (id == R.id.companyLayout) {
            openWindowCompany();
        } else if (id == R.id.companyOtherLayout) {
            openWindowCompanyOther();
        } else if (id == R.id.select_licence_new) {
            openDialog1();
        } else if (id == R.id.select_licence_other_new) {
            openDialog3();
        } else if (id == R.id.log_img || id == R.id.changeImageTxt || id == R.id.userImage) {
            if (!TextUtils.equals(is_active, "0")) {
                openDialog2();
            } else {
                AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.waiting_admin_to_active));
            }
        } else if (id == R.id.select_location_driving) {
            Intent intent = new Intent(getActivity(), BigSelectMapActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivityForResult(intent, 10);
        } else if (id == R.id.divingRadio) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(divingRadio.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadio.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_check_circle);
                    diving_cylinder = 1;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState())
                            .equals(Objects.requireNonNull(freeRadioButton.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (Objects.equals(divingRadio.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadio.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_circle);
                    diving_cylinder = 0;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState())
                            .equals(Objects.requireNonNull(freeRadioButton.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_check_circle);
                    diving_cylinder = 1;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                            (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (divingRadio.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_circle);
                    diving_cylinder = 0;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                            (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            }
        } else if (id == R.id.freeRadioButton) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(freeRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(freeRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving_free = 1;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState())
                            .equals(Objects.requireNonNull(divingRadio.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (Objects.equals(freeRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(freeRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_circle);
                    diving_free = 0;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState())
                            .equals(Objects.requireNonNull(divingRadio.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_check_circle);
                    diving_free = 1;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                            (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (freeRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_circle);
                    diving_free = 0;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                            (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            }
        } else if (id == R.id.shareLayout) {
            FontManager.shareTextUrl(Objects.requireNonNull(getActivity()), name, getString(R.string.driver));
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
                            String partner_diver = successObject.getString("partner_diver");
                            JSONObject diverObject = new JSONObject(partner_diver);
                            diver_id = diverObject.getString("id");
                            is_active = diverObject.getString("is_active");
                            final String diver_bio = diverObject.getString("diver_bio");
                            final String page_name = diverObject.getString("page_name");
                            final String location = diverObject.getString("location");
                            Log.e("diver_id", diver_id);
                            if (!location.equals("")) {
                                Log.e("location///", "/////");
                                String[] namesList = location.split(",");
                                String name1 = namesList[0];
                                String name2 = namesList[1];
                                latitude = Double.parseDouble(name1);
                                longitude = Double.parseDouble(name2);
                            }
//
                            handler.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void run() {

                                    getCourseList(token);
                                    getImages(token);
                                    getBoatTripList(token);
                                    courseRequestData();
                                    tripRequestData();
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
                                    usernameEditText.setText(name);
                                    if (!email.equals("null"))
                                        emailEditText.setText(email);
                                    mobileEditText.setText(mobile);
                                    if (!page_name.equals("null"))
                                        pageNameEditText.setText(page_name);
                                    progressbar.setVisibility(View.VISIBLE);
                                    if (!TextUtils.equals(user_image, "null")) {
                                        userImage.setVisibility(View.GONE);
                                        Picasso.get()
                                                .load(FontManager.IMAGE_URL + user_image)
                                                .into(log_img, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        progressbar.setVisibility(View.GONE);
                                                        userImage.setVisibility(View.GONE);
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

                                    if (AppLanguage.getLanguage(getActivity()).equals("ar"))
                                        cityTxt.setText(city_ar);
                                    else
                                        cityTxt.setText(city_en);


                                    if (!location.equals(""))
                                        select_location_driving.setText(location);
                                    noteEditText.setText(diver_bio);
//                                    valueEditText.setText(product_price);
                                }
                            });

                            String licenses = diverObject.getString("licenses");
                            JSONObject licensesObject = new JSONObject(licenses);
                            if (licensesObject.has("free") && licensesObject.has("tank")) {
                                String free = licensesObject.getString("free");
                                JSONObject freeObject = new JSONObject(free);
                                final String free_full_name = freeObject.getString("full_name");
                                final String free_diving_licence = freeObject.getString("diving_licence");

                                final String free_diving_licence_level = freeObject.getString("licence_level");
                                JSONObject jsonObject1 = new JSONObject(free_diving_licence_level);
                                final String free_title_en_level = jsonObject1.getString("title_en");
                                final String free_title_ar_level = jsonObject1.getString("title_ar");

                                final String free_diving_licence_provider = freeObject.getString("license_issuers");
                                JSONObject jsonObject2 = new JSONObject(free_diving_licence_provider);
                                final String free_title_en_provider = jsonObject2.getString("title_en");
                                final String free_title_ar_provider = jsonObject2.getString("title_ar");

                                final String free_licence_image = freeObject.getString("licence_image");
//                                String free = freeObject.getString("free");
                                String tank = licensesObject.getString("tank");
                                JSONObject tankObject = new JSONObject(tank);
                                final String tank_full_name = tankObject.getString("full_name");
                                final String tank_diving_licence = tankObject.getString("diving_licence");

                                final String tank_diving_licence_level = tankObject.getString("licence_level");
                                JSONObject jsonObject3 = new JSONObject(tank_diving_licence_level);
                                final String tank_title_en_level = jsonObject3.getString("title_en");
                                final String tank_title_ar_level = jsonObject3.getString("title_ar");

                                final String tank_diving_licence_provider = tankObject.getString("license_issuers");
                                JSONObject jsonObject4 = new JSONObject(tank_diving_licence_provider);
                                final String tank_title_en_provider = jsonObject4.getString("title_en");
                                final String tank_title_ar_provider = jsonObject4.getString("title_ar");
                                final String tank_licence_image = tankObject.getString("licence_image");
//                                String tank = tankObject.getString("free");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        diving_cylinder = 1;
                                        diving_free = 1;
                                        otherLayout.setVisibility(View.VISIBLE);
                                        driverNameEditText.setText(tank_full_name);
                                        licenceDrivingEditText.setText(tank_diving_licence);
                                        driverOtherNameEditText.setText(free_full_name);
                                        licenceDrivingOtherEditText.setText(free_diving_licence);
                                        if (!tank_licence_image.equals("null")) {
                                            imageNewLayout.setVisibility(View.VISIBLE);
                                            progressbarOne.setVisibility(View.VISIBLE);
                                            Picasso.get()
                                                    .load(FontManager.IMAGE_URL + tank_licence_image)
                                                    .into(imageViewNew, new Callback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            progressbarOne.setVisibility(View.GONE);
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            progressbarOne.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }
                                        if (!free_licence_image.equals("null")) {
                                            progressbarTwo.setVisibility(View.GONE);
                                            imageNewOtherLayout.setVisibility(View.VISIBLE);
                                            Picasso.get()
                                                    .load(FontManager.IMAGE_URL + free_licence_image)
                                                    .into(imageViewOtherNew, new Callback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            progressbarTwo.setVisibility(View.GONE);
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            progressbarTwo.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }

                                        if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                                            companyTxt.setText(tank_title_ar_provider);
                                            licenceLevelTxt.setText(tank_title_ar_level);
                                            licenceLevelOtherTxt.setText(free_title_ar_level);
                                            companyOtherTxt.setText(free_title_ar_provider);
                                        } else {
                                            companyTxt.setText(tank_title_en_provider);
                                            licenceLevelTxt.setText(tank_title_en_level);
                                            companyOtherTxt.setText(free_title_en_provider);
                                            licenceLevelOtherTxt.setText(free_title_en_level);
                                        }

                                        freeRadioButton.setImageResource(R.drawable.ic_check_circle);
                                        divingRadio.setImageResource(R.drawable.ic_check_circle);
                                    }
                                });
                            } else if (licensesObject.has("free") && !licensesObject.has("tank")) {
                                String free = licensesObject.getString("free");
                                JSONObject freeObject = new JSONObject(free);
                                final String free_full_name = freeObject.getString("full_name");
                                final String free_diving_licence = freeObject.getString("diving_licence");

                                final String free_diving_licence_level = freeObject.getString("licence_level");
                                JSONObject jsonObject1 = new JSONObject(free_diving_licence_level);
                                final String free_title_en_level = jsonObject1.getString("title_en");
                                final String free_title_ar_level = jsonObject1.getString("title_ar");

                                final String free_diving_licence_provider = freeObject.getString("license_issuers");
                                JSONObject jsonObject2 = new JSONObject(free_diving_licence_provider);
                                final String free_title_en_provider = jsonObject2.getString("title_en");
                                final String free_title_ar_provider = jsonObject2.getString("title_ar");

                                final String free_licence_image = freeObject.getString("licence_image");
////                                String free = freeObject.getString("free");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        progressDialog.hide();
////                                        otherLayout.setVisibility(View.VISIBLE);
                                        diving_cylinder = 0;
                                        diving_free = 1;
                                        driverNameEditText.setText(free_full_name);
                                        licenceDrivingEditText.setText(free_diving_licence);
                                        if (!free_licence_image.equals("null")) {
                                            imageNewLayout.setVisibility(View.VISIBLE);
                                            progressbarOne.setVisibility(View.VISIBLE);
                                            Picasso.get()
                                                    .load(FontManager.IMAGE_URL + free_licence_image)
                                                    .into(imageViewNew, new Callback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            progressbarOne.setVisibility(View.GONE);
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            progressbarOne.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }

                                        if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                                            companyTxt.setText(free_title_ar_provider);
                                            licenceLevelTxt.setText(free_title_ar_level);
                                        } else {
                                            companyTxt.setText(free_title_en_provider);
                                            licenceLevelTxt.setText(free_title_en_level);
                                        }


                                        freeRadioButton.setImageResource(R.drawable.ic_check_circle);
                                        divingRadio.setImageResource(R.drawable.ic_circle);
                                    }
                                });
                            } else if (!licensesObject.has("free") && licensesObject.has("tank")) {
                                String tank = licensesObject.getString("tank");
                                JSONObject tankObject = new JSONObject(tank);
                                final String tank_full_name = tankObject.getString("full_name");
                                final String tank_diving_licence = tankObject.getString("diving_licence");

                                final String tank_diving_licence_level = tankObject.getString("licence_level");
                                JSONObject jsonObject3 = new JSONObject(tank_diving_licence_level);
                                final String tank_title_en_level = jsonObject3.getString("title_en");
                                final String tank_title_ar_level = jsonObject3.getString("title_ar");

                                final String tank_diving_licence_provider = tankObject.getString("license_issuers");
                                JSONObject jsonObject4 = new JSONObject(tank_diving_licence_provider);
                                final String tank_title_en_provider = jsonObject4.getString("title_en");
                                final String tank_title_ar_provider = jsonObject4.getString("title_ar");

                                final String tank_licence_image = tankObject.getString("licence_image");
////                                String tank = tankObject.getString("tank");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
////                                        otherLayout.setVisibility(View.VISIBLE);
                                        diving_cylinder = 1;
                                        diving_free = 0;
                                        driverNameEditText.setText(tank_full_name);
                                        licenceDrivingEditText.setText(tank_diving_licence);
                                        if (!tank_licence_image.equals("null")) {
                                            imageNewLayout.setVisibility(View.VISIBLE);
                                            progressbarOne.setVisibility(View.VISIBLE);
                                            Picasso.get()
                                                    .load(FontManager.IMAGE_URL + tank_licence_image)
                                                    .into(imageViewNew, new Callback() {
                                                        @Override
                                                        public void onSuccess() {
                                                            progressbarOne.setVisibility(View.GONE);
                                                        }

                                                        @Override
                                                        public void onError(Exception e) {
                                                            progressbarOne.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }

                                        if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                                            companyTxt.setText(tank_title_ar_provider);
                                            licenceLevelTxt.setText(tank_title_ar_level);
                                        } else {
                                            companyTxt.setText(tank_title_en_provider);
                                            licenceLevelTxt.setText(tank_title_en_level);
                                        }


                                        freeRadioButton.setImageResource(R.drawable.ic_circle);
                                        divingRadio.setImageResource(R.drawable.ic_check_circle);
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

    public void updateDriver() {
        final String email = emailEditText.getText().toString().trim();
        final String cityName = cityTxt.getText().toString().trim();
        final String cityName_ar = otherArCityEditText.getText().toString().trim();
        final String cityName_en = otherEnCityEditText.getText().toString().trim();
        final String page_name = pageNameEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        final String driverName = driverNameEditText.getText().toString().trim();
        final String licenceNumber = licenceDrivingEditText.getText().toString().trim();
        final String licenceLevel = licenceLevelTxt.getText().toString().trim();
        final String licenceLevel_ar = otherArLicenceLevelEditText.getText().toString().trim();
        final String licenceLevel_en = otherEnLicenceLevelEditText.getText().toString().trim();
        final String company = companyTxt.getText().toString().trim();
        final String company_ar = otherArCompanyEditText.getText().toString().trim();
        final String company_en = otherEnCompanyEditText.getText().toString().trim();
        final String noteDriver = noteEditText.getText().toString().trim();
        final String locationDriver = select_location_driving.getText().toString().trim();

        final String driverNameOther = driverOtherNameEditText.getText().toString().trim();
        final String licenceNumberOther = licenceDrivingOtherEditText.getText().toString().trim();
        final String licenceLevelOther = licenceLevelOtherTxt.getText().toString().trim();
        final String licenceLevelOther_ar = otherArLicencetEditText.getText().toString().trim();
        final String licenceLevelOther_en = otherEnLicencetEditText.getText().toString().trim();
        final String companyOther = companyOtherTxt.getText().toString().trim();
        final String companyOther_ar = otherArCompanyOtherEditText.getText().toString().trim();
        final String companyOther_en = otherEnCompanyOtherEditText.getText().toString().trim();

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
            } else if (TextUtils.isEmpty(driverName)) {
                driverNameEditText.setError(getString(R.string.error_field_required));
                driverNameEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(licenceNumber)) {
                licenceDrivingEditText.setError(getString(R.string.error_field_required));
                licenceDrivingEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(licenceLevel)) {
                licenceLevelTxt.setError(getString(R.string.error_field_required));
                licenceLevelTxt.requestFocus();
                is_valid = 1;
            } else if (TextUtils.equals(licenceLevel, getString(R.string.other))) {
                if (TextUtils.isEmpty(licenceLevel_en)) {
                    otherEnLicenceLevelEditText.setError(getString(R.string.error_field_required));
                    otherEnLicenceLevelEditText.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(licenceLevel_ar)) {
                    otherArLicenceLevelEditText.setError(getString(R.string.error_field_required));
                    otherArLicenceLevelEditText.requestFocus();
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
            }
            if (is_valid == 0) {
                if (TextUtils.isEmpty(company)) {
                    companyTxt.setError(getString(R.string.error_field_required));
                    companyTxt.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.equals(company, getString(R.string.other))) {
                    if (TextUtils.isEmpty(company_en)) {
                        otherEnCompanyEditText.setError(getString(R.string.error_field_required));
                        otherEnCompanyEditText.requestFocus();
                        is_valid = 1;
                    } else if (TextUtils.isEmpty(company_ar)) {
                        otherArCompanyEditText.setError(getString(R.string.error_field_required));
                        otherArCompanyEditText.requestFocus();
                        is_valid = 1;
                    } else {
                        is_valid = 0;
                    }
                }
                if (is_valid == 0) {
                    if (diving_cylinder == 0 && diving_free == 0) {
                        AppErrorsManager.showErrorDialog(getActivity(), getString(R.string.you_should_choose_diving_type));
                        is_valid = 1;
                    } else if (TextUtils.isEmpty(noteDriver)) {
                        noteEditText.setError(getString(R.string.error_field_required));
                        noteEditText.requestFocus();
                        is_valid = 1;
                    } else {
                        is_valid = 0;
                    }
                }
            }
        }
        if (diving_cylinder == 1 && diving_free == 1) {
            if (TextUtils.isEmpty(driverNameOther)) {
                driverOtherNameEditText.setError(getString(R.string.error_field_required));
                driverOtherNameEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(licenceNumberOther)) {
                licenceDrivingOtherEditText.setError(getString(R.string.error_field_required));
                licenceDrivingOtherEditText.requestFocus();
                is_valid = 1;
            } else if (TextUtils.isEmpty(licenceLevelOther)) {
                licenceLevelOtherTxt.setError(getString(R.string.error_field_required));
                licenceLevelOtherTxt.requestFocus();
                is_valid = 1;
            } else if (TextUtils.equals(licenceLevelOther, getString(R.string.other))) {
                if (TextUtils.isEmpty(licenceLevelOther_en)) {
                    otherEnLicencetEditText.setError(getString(R.string.error_field_required));
                    otherEnLicencetEditText.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(licenceLevelOther_ar)) {
                    otherArLicencetEditText.setError(getString(R.string.error_field_required));
                    otherArLicencetEditText.requestFocus();
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
            }
            if (is_valid == 0) {
                if (TextUtils.isEmpty(companyOther)) {
                    companyOtherTxt.setError(getString(R.string.error_field_required));
                    companyOtherTxt.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.equals(companyOther, getString(R.string.other))) {
                    if (TextUtils.isEmpty(companyOther_en)) {
                        otherEnCompanyOtherEditText.setError(getString(R.string.error_field_required));
                        otherEnCompanyOtherEditText.requestFocus();
                        is_valid = 1;
                    } else if (TextUtils.isEmpty(companyOther_ar)) {
                        otherArCompanyOtherEditText.setError(getString(R.string.error_field_required));
                        otherArCompanyOtherEditText.requestFocus();
                        is_valid = 1;
                    } else {
                        is_valid = 0;
                    }
                } else {
                    is_valid = 0;
                }
            }
            if (is_valid == 0) {
                Log.e("yyyy", "uuuuu");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("mobile", mobile)
                                .addFormDataPart("email", email)
                                .addFormDataPart("page_name", page_name);

                        if (TextUtils.equals(cityName, getString(R.string.other))) {
                            builder.addFormDataPart("city_title_ar", cityName_ar);
                            builder.addFormDataPart("city_title_en", cityName_en);
                        } else
                            builder.addFormDataPart("city", city_id);

                        if (fileSchema != null) {
                            builder.addFormDataPart("user_image", fileSchema.getName(),
                                    RequestBody.create(MediaType.parse("jpeg/png"), fileSchema));
                        }
                        if (diving_free == 1 && diving_cylinder == 0) {
                            builder.addFormDataPart("diver_free_full_name", driverName)
                                    .addFormDataPart("id", diver_id)
                                    .addFormDataPart("diver_free_licence_no", licenceNumber);

                            if (TextUtils.equals(licenceLevel, getString(R.string.other))) {
                                builder.addFormDataPart("diving_licence_title_ar", licenceLevel_ar);
                                builder.addFormDataPart("diving_licence_title_en", licenceLevel_en);
                            } else
                                builder.addFormDataPart("diver_free_licence_level", level_id);
//                                    .addFormDataPart("diver_free_licence_level", level_id)

                            if (TextUtils.equals(company, getString(R.string.other))) {
                                builder.addFormDataPart("diving_licence_issuer_title_ar", company_ar);
                                builder.addFormDataPart("diving_licence_issuer_title_en", company_en);
                            } else
                                builder.addFormDataPart("diver_free_licence_issuer", company_id);


                            builder.addFormDataPart("diver_tank_diving_type", String.valueOf(diving_cylinder))
                                    .addFormDataPart("diver_free_diving_type", String.valueOf(diving_free))
                                    .addFormDataPart("diver_about", noteDriver);
                            if (!TextUtils.isEmpty(locationDriver)) {
                                builder.addFormDataPart("diver_location", locationDriver);
                            }
                            if (licenceFile != null) {
                                builder.addFormDataPart("diver_free_licence_image", licenceFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), licenceFile));
                            }
                        }
                        if (diving_free == 0 && diving_cylinder == 1) {
                            builder.addFormDataPart("diver_tank_full_name", driverName)
                                    .addFormDataPart("id", diver_id)
                                    .addFormDataPart("diver_tank_licence_no", licenceNumber);
                            if (TextUtils.equals(licenceLevel, getString(R.string.other))) {
                                builder.addFormDataPart("diving_tank_licence_title_ar", licenceLevel_ar);
                                builder.addFormDataPart("diving_tank_licence_title_en", licenceLevel_en);
                            } else
                                builder.addFormDataPart("diver_tank_licence_level", level_id);

                            if (TextUtils.equals(company, getString(R.string.other))) {
                                builder.addFormDataPart("diving_licence_issuer_title_ar", company_ar);
                                builder.addFormDataPart("diving_licence_issuer_title_en", company_en);
                            } else
                                builder.addFormDataPart("diver_tank_licence_issuer", company_id);

                            builder.addFormDataPart("diver_tank_diving_type", String.valueOf(diving_cylinder))
                                    .addFormDataPart("diver_free_diving_type", String.valueOf(diving_free))
                                    .addFormDataPart("diver_about", noteDriver);
                            if (!TextUtils.isEmpty(locationDriver)) {
                                builder.addFormDataPart("diver_location", locationDriver);
                            }
                            if (licenceFile != null) {
                                builder.addFormDataPart("diver_tank_licence_image", licenceFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), licenceFile));
                            }
                        }
                        if (diving_free == 1 && diving_cylinder == 1) {
                            builder.addFormDataPart("diver_tank_full_name", driverName)
                                    .addFormDataPart("id", diver_id)
                                    .addFormDataPart("diver_tank_licence_no", licenceNumber);
                            if (TextUtils.equals(licenceLevel, getString(R.string.other))) {
                                builder.addFormDataPart("diving_tank_licence_title_ar", licenceLevel_ar);
                                builder.addFormDataPart("diving_tank_licence_title_en", licenceLevel_en);
                            } else
                                builder.addFormDataPart("diver_tank_licence_level", level_id);

                            if (TextUtils.equals(company, getString(R.string.other))) {
                                builder.addFormDataPart("diving_licence_tank_issuer_title_ar", company_ar);
                                builder.addFormDataPart("diving_licence_issuer_tank_title_en", company_en);
                            } else
                                builder.addFormDataPart("diver_tank_licence_issuer", company_id);
                            builder.addFormDataPart("diver_tank_diving_type", String.valueOf(diving_cylinder))
                                    .addFormDataPart("diver_free_diving_type", String.valueOf(diving_free))
                                    .addFormDataPart("diver_about", noteDriver);
                            if (!TextUtils.isEmpty(locationDriver)) {
                                builder.addFormDataPart("diver_location", locationDriver);
                            }
                            if (licenceFile != null) {
                                builder.addFormDataPart("diver_tank_licence_image", licenceFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), licenceFile));
                            }
                            builder.addFormDataPart("diver_free_full_name", driverNameOther)
                                    .addFormDataPart("diver_free_licence_no", licenceNumberOther);
                            if (TextUtils.equals(licenceLevelOther, getString(R.string.other))) {
                                builder.addFormDataPart("diving_licence_title_ar", licenceLevelOther_ar);
                                builder.addFormDataPart("diving_licence_title_en", licenceLevelOther_en);
                            } else
                                builder.addFormDataPart("diver_free_licence_level", levelOther_id);

                            if (TextUtils.equals(companyOther, getString(R.string.other))) {
                                builder.addFormDataPart("diving_licence_issuer_title_ar", companyOther_ar);
                                builder.addFormDataPart("diving_licence_issuer_title_en", companyOther_en);
                            } else
                                builder.addFormDataPart("diver_free_licence_issuer", companyOther_id);

                            if (otherLicenceFile != null) {
                                builder.addFormDataPart("diver_free_licence_image", otherLicenceFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), otherLicenceFile));
                            }
                        }

                        RequestBody requestBody = builder.build();
                        updateDriverInfo(requestBody);
                    }
                }).start();
            }
        }
    }

    public void updateDriverInfo(final RequestBody requestBody) {
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
                            + "update_driver_info").post(requestBody)
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
                                                getString(R.string.update_successfully), null);
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
                        .addFormDataPart("partner_id", diver_id)
                        .addFormDataPart("object_tb", "partner_divers")
                        .addFormDataPart("is_default", "1")
                        .addFormDataPart("object_id", diver_id);
//                partner_boats,partner_divers,partner_jetski,partner_sellers,partner_services,partner_supplies
                if (driverFile != null) {
                    builder.addFormDataPart("image", driverFile.getName(),
                            RequestBody.create(MediaType.parse("jpeg/png"), driverFile));
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
            @TargetApi(Build.VERSION_CODES.KITKAT)
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
                            + "diver/" + diver_id).get()
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
                            String divers = successObject.getString("diver");
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

    public void getBoatTripList(final String token) {
        tripList.clear();
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
                            + "diver-trips/" + diver_id).get()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
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
                                Log.e("ddddd", "dddddddddddddddddddddddddddddddd");
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
                                    String title = boatObject.getString("title");

                                    Trip trip = new Trip(id, boat_id, diver_id, boat_name,
                                            trip_type, start_date, start_location, trip_route,
                                            trip_duration, trip_terms, available_seats, trip_price,
                                            for_diver, gears_available, gears_price, title, start_time);
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

    public void deleteTrip(final String token, final String trip_id) {
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
                            + "delete-trip/" + trip_id).delete()
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
                                        getBoatTripList(token);
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

    public void cityList() {
        spinnerItemCityList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
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
    public void openWindowCompany() {
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

                for (int j = 0; j < spinnerItemCompanyList.size(); j++) {

                    final String text = spinnerItemCompanyList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemCompanyList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCompanyList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCompanyList.get(j));
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
                        company_id = filteredList.get(position).getId();
                        companyTxt.setText(status);
                        if (TextUtils.equals(companyTxt.getText().toString(), getString(R.string.other))) {
                            otherArCompanyEditText.setVisibility(View.VISIBLE);
                            otherEnCompanyEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherArCompanyEditText.setVisibility(View.GONE);
                            otherEnCompanyEditText.setVisibility(View.GONE);
                        }
                        companyTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemCompanyList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                    status = spinnerItemCompanyList.get(position).getTextA();
                } else {
                    status = spinnerItemCompanyList.get(position).getText();
                }
                company_id = spinnerItemCompanyList.get(position).getId();
                companyTxt.setText(status);
                if (TextUtils.equals(companyTxt.getText().toString(), getString(R.string.other))) {
                    otherArCompanyEditText.setVisibility(View.VISIBLE);
                    otherEnCompanyEditText.setVisibility(View.VISIBLE);
                } else {
                    otherArCompanyEditText.setVisibility(View.GONE);
                    otherEnCompanyEditText.setVisibility(View.GONE);
                }
                companyTxt.setError(null);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowCompanyOther() {
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

                for (int j = 0; j < spinnerItemCompanyOtherList.size(); j++) {

                    final String text = spinnerItemCompanyOtherList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemCompanyOtherList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCompanyOtherList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCompanyOtherList.get(j));
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
                        companyOther_id = filteredList.get(position).getId();
                        companyOtherTxt.setText(status);
                        if (TextUtils.equals(companyOtherTxt.getText().toString(), getString(R.string.other))) {
                            otherArCompanyOtherEditText.setVisibility(View.VISIBLE);
                            otherEnCompanyOtherEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherArCompanyOtherEditText.setVisibility(View.GONE);
                            otherEnCompanyOtherEditText.setVisibility(View.GONE);
                        }
                        companyOtherTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemCompanyOtherList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                    status = spinnerItemCompanyOtherList.get(position).getTextA();
                } else {
                    status = spinnerItemCompanyOtherList.get(position).getText();
                }
                companyOther_id = spinnerItemCompanyOtherList.get(position).getId();
                companyOtherTxt.setText(status);
                if (TextUtils.equals(companyOtherTxt.getText().toString(), getString(R.string.other))) {
                    otherArCompanyOtherEditText.setVisibility(View.VISIBLE);
                    otherEnCompanyOtherEditText.setVisibility(View.VISIBLE);
                } else {
                    otherArCompanyOtherEditText.setVisibility(View.GONE);
                    otherEnCompanyOtherEditText.setVisibility(View.GONE);
                }
                companyOtherTxt.setError(null);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowLevel() {
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

                for (int j = 0; j < spinnerItemLevelList.size(); j++) {

                    final String text = spinnerItemLevelList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemLevelList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemLevelList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemLevelList.get(j));
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
                        level_id = filteredList.get(position).getId();
                        licenceLevelTxt.setText(status);
                        if (TextUtils.equals(licenceLevelTxt.getText().toString(), getString(R.string.other))) {
                            otherArLicenceLevelEditText.setVisibility(View.VISIBLE);
                            otherEnLicenceLevelEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherArLicenceLevelEditText.setVisibility(View.GONE);
                            otherEnLicenceLevelEditText.setVisibility(View.GONE);
                        }
                        licenceLevelTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemLevelList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                    status = spinnerItemLevelList.get(position).getTextA();
                } else {
                    status = spinnerItemLevelList.get(position).getText();
                }
                level_id = spinnerItemLevelList.get(position).getId();
                licenceLevelTxt.setText(status);
                if (TextUtils.equals(licenceLevelTxt.getText().toString(), getString(R.string.other))) {
                    otherArLicenceLevelEditText.setVisibility(View.VISIBLE);
                    otherEnLicenceLevelEditText.setVisibility(View.VISIBLE);
                } else {
                    otherArLicenceLevelEditText.setVisibility(View.GONE);
                    otherEnLicenceLevelEditText.setVisibility(View.GONE);
                }
                licenceLevelTxt.setError(null);
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
    public void openWindowLevelOther() {
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

                for (int j = 0; j < spinnerItemLevelOtherList.size(); j++) {

                    final String text = spinnerItemLevelOtherList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemLevelOtherList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemLevelOtherList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemLevelOtherList.get(j));
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
                        levelOther_id = filteredList.get(position).getId();
                        licenceLevelOtherTxt.setText(status);
                        if (TextUtils.equals(licenceLevelOtherTxt.getText().toString(), getString(R.string.other))) {
                            otherArLicencetEditText.setVisibility(View.VISIBLE);
                            otherEnLicencetEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherArLicencetEditText.setVisibility(View.GONE);
                            otherEnLicencetEditText.setVisibility(View.GONE);
                        }
                        licenceLevelOtherTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(getActivity(), spinnerItemLevelOtherList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(getActivity()).equals("ar")) {
                    status = spinnerItemLevelOtherList.get(position).getTextA();
                } else {
                    status = spinnerItemLevelOtherList.get(position).getText();
                }
                levelOther_id = spinnerItemLevelOtherList.get(position).getId();
                licenceLevelOtherTxt.setText(status);
                if (TextUtils.equals(licenceLevelOtherTxt.getText().toString(), getString(R.string.other))) {
                    otherArLicencetEditText.setVisibility(View.VISIBLE);
                    otherEnLicencetEditText.setVisibility(View.VISIBLE);
                } else {
                    otherArLicencetEditText.setVisibility(View.GONE);
                    otherEnLicencetEditText.setVisibility(View.GONE);
                }
                licenceLevelOtherTxt.setError(null);
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

    public void licenceLevelList() {
        spinnerItemLevelList.clear();
        spinnerItemLevelOtherList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "diving-license-levels").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("diving_license_levels");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                level_id = jsonObject1.getString("id");
                                levelOther_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem level = new SpinnerItem(level_id, name_en, name_ar);
                                SpinnerItem levelOther = new SpinnerItem(levelOther_id, name_en, name_ar);
                                spinnerItemLevelList.add(level);
                                spinnerItemLevelOtherList.add(levelOther);
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

    public void companyList() {
        spinnerItemCompanyList.clear();
        spinnerItemCompanyOtherList.clear();
        InternetConnectionUtils.isInternetAvailable(getActivity(), new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "diving-license-issuers").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            JSONObject jsonObject = new JSONObject(response_data);
                            String success = jsonObject.getString("success");
                            JSONObject successObject = new JSONObject(success);
                            JSONArray jsonArray = successObject.getJSONArray("issuers");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                company_id = jsonObject1.getString("id");
                                companyOther_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem company = new SpinnerItem(company_id, name_en, name_ar);
                                SpinnerItem companyOther = new SpinnerItem(companyOther_id, name_en, name_ar);
                                spinnerItemCompanyList.add(company);
                                spinnerItemCompanyOtherList.add(companyOther);
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

    public void getCourseList(final String token) {
        courseList.clear();
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
                            + "partner-cources/" + diver_id).get()
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
                            String cources = successObject.getString("cources");
                            if (cources.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentCourses.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentCourses.setVisibility(View.GONE);
                                    }
                                });
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

    public void courseRequestData() {
        courseRequestList.clear();
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
                            + "partner_book_course/" + user_id).get()
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
                            String trips = successObject.getString("book_course");
                            if (trips.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        noContentCourse.setVisibility(View.VISIBLE);
                                        progressDialog.hide();
                                    }
                                });
                            } else {
                                JSONArray tripsArray = new JSONArray(trips);
                                for (int i = 0; i < tripsArray.length(); i++) {
                                    JSONObject boatObject = tripsArray.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String user_id = boatObject.getString("user_id");
                                    String partner_id = boatObject.getString("partner_id");
                                    String course_id = boatObject.getString("course_id");
                                    String approved = boatObject.getString("approved");
                                    String is_rating = boatObject.getString("is_rating");
                                    String guid = boatObject.getString("guid");
                                    String user_name = boatObject.getString("user_name");
                                    String course_title = boatObject.getString("course_title");
                                    String mobile = boatObject.getString("mobile");
                                    Course boat = new Course(id, user_id, partner_id, course_id,
                                            approved, is_rating, guid, course_title, mobile, user_name);
                                    courseRequestList.add(boat);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentCourse.setVisibility(View.GONE);
                                            courseRequestAdapter.notifyDataSetChanged();
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

    public void tripRequestData() {
        tripEventList.clear();
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
                            + "partner_book_trip/" + user_id).get()
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
                            String trips = successObject.getString("book_trip");
                            if (trips.equals("[]")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        noContentTripEvent.setVisibility(View.VISIBLE);
                                    }
                                });
                            } else {
                                JSONArray tripsArray = new JSONArray(trips);
                                for (int i = 0; i < tripsArray.length(); i++) {
                                    JSONObject boatObject = tripsArray.getJSONObject(i);
                                    String id = boatObject.getString("id");
                                    String user_id = boatObject.getString("user_id");
                                    String user_name = boatObject.getString("user_name");
                                    String partner_id = boatObject.getString("partner_id");
                                    String trip_id = boatObject.getString("trip_id");
                                    String approved = boatObject.getString("approved");
                                    String is_rating = boatObject.getString("is_rating");
                                    String guid = boatObject.getString("guid");
                                    String trip_name = boatObject.getString("trip_name");
                                    String trip_type = boatObject.getString("trip_type");
                                    String mobile = boatObject.getString("mobile");
                                    String start_date = boatObject.getString("start_date");
                                    String start_time = boatObject.getString("start_time");
                                    Trip boat = new Trip(id, user_id, partner_id, trip_id,
                                            approved, is_rating, guid, trip_name, mobile, user_name,
                                            start_date + " " + start_time, trip_type);
                                    tripEventList.add(boat);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.hide();
                                            noContentTripEvent.setVisibility(View.GONE);
                                            tripEventRequestAdapter.notifyDataSetChanged();
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
                            + "booking_course_action").post(requestBody)
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
                                                        courseRequestData();
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

    public void addTripResponse(final String id, final String approved) {
        Log.e("yyyy", "uuuuu");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", id)
                        .addFormDataPart("approved", approved);
                RequestBody requestBody = builder.build();
                addTripResponseInfo(requestBody);
            }
        }).start();
    }

    public void addTripResponseInfo(final RequestBody requestBody) {
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
                            + "booking_action_trip").post(requestBody)
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
                                                        tripRequestData();
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

    public void deleteBoatBook(Context context, final String token, final String course_id) {
        InternetConnectionUtils.isInternetAvailable(context, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "booking_course_action_delete/" + course_id).delete()
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
                                courseRequestData();

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

    public void deleteTripBook(Context context, final String token, final String trip_id) {
        InternetConnectionUtils.isInternetAvailable(context, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "booking_trip_action_delete/" + trip_id).delete()
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
                                tripRequestData();

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
                            + "request_course_rating").post(requestBody)
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
                                                        courseRequestData();
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

    public void requestTripRating(final String id) {
        Log.e("yyyy", "uuuuu");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", id);
                RequestBody requestBody = builder.build();
                requestTripRatingInfo(requestBody);
            }
        }).start();
    }

    public void requestTripRatingInfo(final RequestBody requestBody) {
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
                            + "request_trip_rating").post(requestBody)
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
                                                        tripRequestData();
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
        driverFile = new File(pdfPathHolder);
        insertImage();
//        Image image = new Image(uri);
//        imageList.add(image);
//        imageAdapter.notifyDataSetChanged();
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
        driverFile = new File(mMediaUri);
        insertImage();
//        Image image = new Image(photoUri);
//        imageList.add(image);
//        imageAdapter.notifyDataSetChanged();

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
        imageNewLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageViewNew);
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
        imageNewLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(photoUri).into(imageViewNew);
        imageViewNew.setRotation(90);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openDialog3() {
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
                    openGalleryFile3();
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
                    captureImageFromCamera3();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void openGalleryFile3() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_3);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile3(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(getActivity(), uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        otherLicenceFile = new File(pdfPathHolder);
        imageNewOtherLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageViewOtherNew);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void captureImageFromCamera3() {
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
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_3);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile3() {
        Log.e("uriii", mMediaUri.toString());
        otherLicenceFile = new File(mMediaUri);
        imageNewOtherLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(photoUri).into(imageViewOtherNew);
        imageViewOtherNew.setRotation(90);
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
        fileSchema = new File(pdfPathHolder);
        Picasso.get().load(uri).into(log_img);
        userImage.setVisibility(View.GONE);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile2() {
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
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_3) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile3(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_3) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile3();
            }
        } else if (requestCode == 10 && data != null) {
            select_location_driving.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_driving.setError(null);
        } else if (requestCode == 14) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            getCourseList(token);
        } else if (requestCode == 13) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            getBoatTripList(token);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void logOut() {
        AppPreferences.clearAll(getActivity());
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setAction("logout");
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
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
