package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.fatima.sealocation.fragment.DatePickerFragment;
import com.apps.fatima.sealocation.manager.AppErrorsManager;
import com.apps.fatima.sealocation.manager.AppLanguage;
import com.apps.fatima.sealocation.manager.AppPreferences;
import com.apps.fatima.sealocation.manager.FilePath;
import com.apps.fatima.sealocation.manager.FontManager;
import com.apps.fatima.sealocation.manager.InternetAvailableCallback;
import com.apps.fatima.sealocation.manager.InternetConnectionUtils;
import com.apps.fatima.sealocation.manager.OnItemClickListener;
import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.adapter.SpinnerAdapter;
import com.apps.fatima.sealocation.model.SpinnerItem;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class RegisterPartnerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 51;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_1 = 52;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1 = 53;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_2 = 54;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2 = 55;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_3 = 56;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_3 = 57;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_4 = 58;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_4 = 59;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_5 = 60;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_5 = 61;
    private static final int GALLERY_REQUEST_CODE_SCHEMA_6 = 62;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_6 = 63;

    private ImageView boatsRadioButton, tanksRadioButton, driversRadioButton,
            seaRequirementsRadioButton, seaServicesRadioButton, divingRadioButton,
            fishingRadioButton, picnicRadioButton, freeRadioButton, divingRadio, imageView,
            imageViewTank, imageViewNew, imageViewNewOther, imageRecordNew, imageRecordNew_,
            logo_user;
    private CircularImageView log_img;
    private RelativeLayout boat_layout, tanks_layout, drivers_layout, requirement_sea_layout,
            services_sea_layout, otherLayout;
    private TextView expireDateLicence, expireDateForm, expireTanksTxt, select_location,
            select_location_tanks, select_location_driving, select_location_requirement,
            select_location_service, cityTxt, tankTypeTxt, licenceLevelTxt, licenceLevelOtherTxt,
            companyTxt, companyOtherTxt, activityTypeTxt, areaWidthTxt, areaHieghtTxt, passengersNoEditText;
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText,
            mobileEditText;
    private EditText boatNoEditText, boatNameEditText, heightEditText,
            widthEditText, rentValueEditText;
    private EditText rentValueTanksEditText;
    private TextView tanksNoEditText;
    private EditText driverNameEditText, licenceDrivingEditText, noteEditText;
    private EditText driverNameOtherEditText, licenceDrivingOtherEditText;
    private EditText recordNoCommercialEditText, shopNameEditText, shopDescribeEditText;
    private EditText describeEditText, recordNoEditText, otherEnEditText, otherArEditText,
            otherEnTankTypeEditText, otherArTankTypeEditText, otherEnLicenceLevelEditText,
            otherArLicenceLevelEditText, otherEnCompanyEditText, otherArCompanyEditText,
            otherEnCompanyOtherEditText, otherArCompanyOtherEditText, otherEnLicencetEditText,
            otherArLicencetEditText, otherEnCityEditText, otherArCityEditText;
    private RelativeLayout imageLayout, imageTankLayout, imageNewLayout, imageNewOtherLayout,
            imageRecordLayout, imageRecordLayout_;
    private int year, month, day;
    static final int DATE_DIALOG_ID = 100;
    static final int DATE_DIALOG_ID_2 = 200;
    static final int DATE_DIALOG_ID_3 = 300;
    private URI mMediaUri;
    private File fileSchema, boatFile, tankFile, driverFile, driverOtherFile, recordFile, serviceFile;
    private Uri photoUri;
    private ProgressDialog progressDialog;
    private Handler handler;
    private int register_boat = 0, register_tank = 0, register_diver = 0,
            register_supplier = 0, register_services = 0, diving_cylinder = 0, diving_free = 0;
    private int diving = 0, fishing = 0, picnic = 0;
    private List<SpinnerItem> spinnerItemCityList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemWidthUnitsList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemHeightUnitsList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemTankTypeList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemCompanyList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemLevelList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemLevelOtherList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemCompanyOtherList = new ArrayList<>();
    private List<SpinnerItem> spinnerItemActivityList = new ArrayList<>();
    private SpinnerAdapter spinnerAdapter;
    private AlertDialog alertDialog;
    private String city_id, tankTypeId, company_id, level_id, companyOther_id, levelOther_id,
            activity_id, width_unit_id, height_unit_id;
    private CheckBox agreeLicenseCheckBox;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_partner);
        handler = new Handler(Looper.getMainLooper());
        init();
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        cityList();
        measureWidthUnitList();
        measureHightUnitList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout plusLayout = findViewById(R.id.plusLayout);
        RelativeLayout minusLayout = findViewById(R.id.minusLayout);
        RelativeLayout plusTLayout = findViewById(R.id.plusTLayout);
        RelativeLayout minusTLayout = findViewById(R.id.minusTLayout);

        RelativeLayout boatsRadioButtonLayout = findViewById(R.id.boatsRadioButtonLayout);
        RelativeLayout tanksRadioButtonLayout = findViewById(R.id.tanksRadioButtonLayout);
        RelativeLayout driversRadioButtonLayout = findViewById(R.id.driversRadioButtonLayout);
        RelativeLayout seaRequirementsRadioButtonLayout = findViewById(R.id.seaRequirementsRadioButtonLayout);
        RelativeLayout seaServicesRadioButtonLayout = findViewById(R.id.seaServicesRadioButtonLayout);

        RelativeLayout divingLayout = findViewById(R.id.divingLayout);
        RelativeLayout fishingRadioButtonLayout = findViewById(R.id.fishingRadioButtonLayout);
        RelativeLayout picnicRadioButtonLayout = findViewById(R.id.picnicRadioButtonLayout);

        RelativeLayout divingCLayout = findViewById(R.id.divingCLayout);
        RelativeLayout freeRadioButtonLayout = findViewById(R.id.freeRadioButtonLayout);
        agreeLicenseCheckBox = findViewById(R.id.agreeLicenseCheckBox);
        agreeLicenseCheckBox.setOnClickListener(this);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);
        TextView select_image = findViewById(R.id.select_image);
        ImageView ic_info = findViewById(R.id.ic_info);
        TextView licenceImage = findViewById(R.id.licenceImage);
        TextView select_licence_new = findViewById(R.id.select_licence_new);
        TextView licenceTankImage = findViewById(R.id.licenceTankImage);
        TextView attachImageTxt1 = findViewById(R.id.attachImageTxt1);
        TextView attachImageTxt = findViewById(R.id.attachImageTxt);
        TextView select_licence_other_new = findViewById(R.id.select_licence_other_new);
        Button approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }
        select_location = findViewById(R.id.select_location);
        log_img = findViewById(R.id.log_img);
        logo_user = findViewById(R.id.logo_user);
        imageView = findViewById(R.id.imageView);
        imageViewTank = findViewById(R.id.imageViewTank);
        imageViewNew = findViewById(R.id.imageViewNew);
        imageViewNewOther = findViewById(R.id.imageViewOtherNew);
        imageRecordNew = findViewById(R.id.imageRecordNew);
        imageRecordNew_ = findViewById(R.id.imageRecordNew_);
        select_location_tanks = findViewById(R.id.select_location_tanks);
        select_location_driving = findViewById(R.id.select_location_driving);
        select_location_requirement = findViewById(R.id.select_location_requirement);
        select_location_service = findViewById(R.id.select_location_service);
        cityTxt = findViewById(R.id.cityTxt);
        tankTypeTxt = findViewById(R.id.tankTypeTxt);
        licenceLevelTxt = findViewById(R.id.licenceLevelTxt);
        licenceLevelOtherTxt = findViewById(R.id.licenceLevelOtherTxt);
        companyTxt = findViewById(R.id.companyTxt);
        companyOtherTxt = findViewById(R.id.companyOtherTxt);
        activityTypeTxt = findViewById(R.id.activityTypeTxt);
        areaWidthTxt = findViewById(R.id.areaWidthTxt);
        areaHieghtTxt = findViewById(R.id.areaHieghtTxt);
        boatsRadioButton = findViewById(R.id.boatsRadioButton);
        tanksRadioButton = findViewById(R.id.tanksRadioButton);
        driversRadioButton = findViewById(R.id.driversRadioButton);
        imageLayout = findViewById(R.id.imageLayout);
        imageTankLayout = findViewById(R.id.imageTankLayout);
        imageNewLayout = findViewById(R.id.imageNewLayout);
        imageNewOtherLayout = findViewById(R.id.imageNewOtherLayout);
        imageRecordLayout = findViewById(R.id.imageRecordLayout);
        imageRecordLayout_ = findViewById(R.id.imageRecordLayout_);
        seaRequirementsRadioButton = findViewById(R.id.seaRequirementsRadioButton);
        seaServicesRadioButton = findViewById(R.id.seaServicesRadioButton);
        divingRadioButton = findViewById(R.id.divingRadioButton);
        fishingRadioButton = findViewById(R.id.fishingRadioButton);
        picnicRadioButton = findViewById(R.id.picnicRadioButton);
        freeRadioButton = findViewById(R.id.freeRadioButton);
        divingRadio = findViewById(R.id.divingRadio);
        boat_layout = findViewById(R.id.boat_layout);
        tanks_layout = findViewById(R.id.tanks_layout);
        drivers_layout = findViewById(R.id.drivers_layout);
        requirement_sea_layout = findViewById(R.id.requirement_sea_layout);
        services_sea_layout = findViewById(R.id.services_sea_layout);
        otherLayout = findViewById(R.id.otherLayout);
        RelativeLayout expireDateLicenceLayout = findViewById(R.id.expireDateLicenceLayout);
        RelativeLayout expireDataLayout = findViewById(R.id.expireDataLayout);
        RelativeLayout expireDataTanksLayout = findViewById(R.id.expireDataTanksLayout);
        RelativeLayout cityLayout = findViewById(R.id.cityLayout);
        RelativeLayout tankTypeLayout = findViewById(R.id.tankTypeLayout);
        RelativeLayout licenceLevelOtherLayout = findViewById(R.id.licenceLevelOtherLayout);
        RelativeLayout companyOtherLayout = findViewById(R.id.companyOtherLayout);
        RelativeLayout licenceLevelLayout = findViewById(R.id.licenceLevelLayout);
        RelativeLayout activityLayout = findViewById(R.id.activityLayout);
        RelativeLayout companyLayout = findViewById(R.id.companyLayout);
        expireDateForm = findViewById(R.id.expireTxt);
        expireDateLicence = findViewById(R.id.expireLicenceTxt);
        expireTanksTxt = findViewById(R.id.expireTanksTxt);
        backLayout.setOnClickListener(this);
        areaHieghtTxt.setOnClickListener(this);
        areaWidthTxt.setOnClickListener(this);

        boatsRadioButtonLayout.setOnClickListener(this);
        tanksRadioButtonLayout.setOnClickListener(this);
        seaRequirementsRadioButtonLayout.setOnClickListener(this);
        seaServicesRadioButtonLayout.setOnClickListener(this);
        driversRadioButtonLayout.setOnClickListener(this);

        divingLayout.setOnClickListener(this);
        picnicRadioButtonLayout.setOnClickListener(this);
        fishingRadioButtonLayout.setOnClickListener(this);
        divingCLayout.setOnClickListener(this);
        freeRadioButtonLayout.setOnClickListener(this);

        attachImageTxt1.setOnClickListener(this);
        tankTypeLayout.setOnClickListener(this);
        licenceLevelOtherLayout.setOnClickListener(this);
        companyOtherLayout.setOnClickListener(this);
        activityLayout.setOnClickListener(this);
        licenceLevelLayout.setOnClickListener(this);
        companyLayout.setOnClickListener(this);
        attachImageTxt.setOnClickListener(this);
        freeRadioButton.setOnClickListener(this);
        divingRadio.setOnClickListener(this);
        boatsRadioButton.setOnClickListener(this);
        plusLayout.setOnClickListener(this);
        minusLayout.setOnClickListener(this);
        plusTLayout.setOnClickListener(this);
        minusTLayout.setOnClickListener(this);
        log_img.setOnClickListener(this);
        logo_user.setOnClickListener(this);
        select_image.setOnClickListener(this);
        licenceImage.setOnClickListener(this);
        licenceTankImage.setOnClickListener(this);
        select_licence_other_new.setOnClickListener(this);
        select_licence_new.setOnClickListener(this);
        tanksRadioButton.setOnClickListener(this);
        driversRadioButton.setOnClickListener(this);
        seaRequirementsRadioButton.setOnClickListener(this);
        seaServicesRadioButton.setOnClickListener(this);
        picnicRadioButton.setOnClickListener(this);
        fishingRadioButton.setOnClickListener(this);
        divingRadioButton.setOnClickListener(this);
        expireDataLayout.setOnClickListener(this);
        expireDateLicenceLayout.setOnClickListener(this);
        expireDataTanksLayout.setOnClickListener(this);
        select_location.setOnClickListener(this);
        select_location_tanks.setOnClickListener(this);
        select_location_driving.setOnClickListener(this);
        select_location_requirement.setOnClickListener(this);
        select_location_service.setOnClickListener(this);
        approveBtn.setOnClickListener(this);
        cityLayout.setOnClickListener(this);
        ic_info.setOnClickListener(this);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        mobileEditText = findViewById(R.id.mobileEditText);

        boatNoEditText = findViewById(R.id.boatNoEditText);
        boatNameEditText = findViewById(R.id.boatNameEditText);
        passengersNoEditText = findViewById(R.id.passengersNoEditText);
        heightEditText = findViewById(R.id.heightEditText);
        widthEditText = findViewById(R.id.widthEditText);
        rentValueEditText = findViewById(R.id.rentValueEditText);

        tanksNoEditText = findViewById(R.id.tanksNoEditText);
        rentValueTanksEditText = findViewById(R.id.rentValueTanksEditText);

        driverNameEditText = findViewById(R.id.driverNameEditText);
        licenceDrivingEditText = findViewById(R.id.licenceDrivingEditText);
        noteEditText = findViewById(R.id.noteEditText);

        driverNameOtherEditText = findViewById(R.id.driverOtherNameEditText);
        licenceDrivingOtherEditText = findViewById(R.id.licenceDrivingOtherEditText);

        recordNoCommercialEditText = findViewById(R.id.recordNoCommercialEditText);
        shopNameEditText = findViewById(R.id.shopNameEditText);
        shopDescribeEditText = findViewById(R.id.shopDescribeEditText);

        describeEditText = findViewById(R.id.describeEditText);
        recordNoEditText = findViewById(R.id.recordNoEditText);

        otherEnEditText = findViewById(R.id.otherEnEditText);
        otherArEditText = findViewById(R.id.otherArEditText);
        otherArTankTypeEditText = findViewById(R.id.otherArTankTypeEditText);
        otherEnTankTypeEditText = findViewById(R.id.otherEnTankTypeEditText);
        otherEnLicenceLevelEditText = findViewById(R.id.otherEnLicenceLevelEditText);
        otherArLicenceLevelEditText = findViewById(R.id.otherArLicenceLevelEditText);
        otherEnCompanyEditText = findViewById(R.id.otherEnCompanyEditText);
        otherArCompanyEditText = findViewById(R.id.otherArCompanyEditText);
        otherEnCompanyOtherEditText = findViewById(R.id.otherEnCompanyOtherEditText);
        otherArCompanyOtherEditText = findViewById(R.id.otherArCompanyOtherEditText);
        otherEnLicencetEditText = findViewById(R.id.otherEnLicencetEditText);
        otherArLicencetEditText = findViewById(R.id.otherArLicencetEditText);
        otherArCityEditText = findViewById(R.id.otherArCityEditText);
        otherEnCityEditText = findViewById(R.id.otherEnCityEditText);


        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();
                String localeString = ims.getLocale();
                Locale locale = new Locale(localeString);
                final String currentLanguage = locale.getDisplayLanguage();
                if (currentLanguage.equals("Arabic")) {
                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.username_english_characters));
                } else {
                    Log.e("currentLanguage", currentLanguage);
                }
            }
        });


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        int totalCount = Integer.parseInt(passengersNoEditText.getText().toString());
        int totalCountT = Integer.parseInt(tanksNoEditText.getText().toString());
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.log_img || id == R.id.select_image || id == R.id.logo_user) {
            openDialog();
        } else if (id == R.id.approveBtn) {
            register();
        } else if (id == R.id.licenceImage) {
            openDialog1();
        } else if (id == R.id.licenceTankImage) {
            openDialog2();
        } else if (id == R.id.select_licence_new) {
            openDialog3();
        } else if (id == R.id.attachImageTxt1) {
            openDialog4();
        } else if (id == R.id.attachImageTxt) {
            openDialog5();
        } else if (id == R.id.areaHieghtTxt) {
            openWindowHeightUnits();
        } else if (id == R.id.areaWidthTxt) {
            openWindowWidthUnits();
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                passengersNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            passengersNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.minusTLayout) {
            if (totalCountT != 0)
                tanksNoEditText.setText(String.valueOf(totalCountT - 1));
        } else if (id == R.id.plusTLayout) {
            tanksNoEditText.setText(String.valueOf(totalCountT + 1));
        } else if (id == R.id.select_licence_other_new) {
            openDialog6();
        } else if (id == R.id.boatsRadioButtonLayout || id == R.id.boatsRadioButton) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(boatsRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(boatsRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    boatsRadioButton.setImageResource(R.drawable.ic_check_circle);
                    boat_layout.setVisibility(View.VISIBLE);
                    register_boat = 1;
                } else if (Objects.equals(boatsRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(boatsRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    boatsRadioButton.setImageResource(R.drawable.ic_circle);
                    boat_layout.setVisibility(View.GONE);
                    register_boat = 0;
                }
            } else {
                if (Objects.requireNonNull(boatsRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    boatsRadioButton.setImageResource(R.drawable.ic_check_circle);
                    boat_layout.setVisibility(View.VISIBLE);
                    register_boat = 1;
                } else if (boatsRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    boatsRadioButton.setImageResource(R.drawable.ic_circle);
                    boat_layout.setVisibility(View.GONE);
                    register_boat = 0;
                }
            }
        } else if (id == R.id.tanksRadioButtonLayout || id == R.id.tanksRadioButton) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(tanksRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(tanksRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    tanksRadioButton.setImageResource(R.drawable.ic_check_circle);
                    tanks_layout.setVisibility(View.VISIBLE);
                    register_tank = 1;
                    tankTypeList();
                } else if (Objects.equals(tanksRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(tanksRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    tanksRadioButton.setImageResource(R.drawable.ic_circle);
                    tanks_layout.setVisibility(View.GONE);
                    register_tank = 0;
                }
            } else {
                if (Objects.requireNonNull(tanksRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    tanksRadioButton.setImageResource(R.drawable.ic_check_circle);
                    tanks_layout.setVisibility(View.VISIBLE);
                    register_tank = 1;
                    tankTypeList();
                } else if (tanksRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    tanksRadioButton.setImageResource(R.drawable.ic_circle);
                    tanks_layout.setVisibility(View.GONE);
                    register_tank = 0;
                }
            }
        } else if (id == R.id.driversRadioButtonLayout || id == R.id.driversRadioButton) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(driversRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(driversRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    driversRadioButton.setImageResource(R.drawable.ic_check_circle);
                    drivers_layout.setVisibility(View.VISIBLE);
                    register_diver = 1;
                    licenceLevelList();
                    CompanyList();
                } else if (Objects.equals(driversRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(driversRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    driversRadioButton.setImageResource(R.drawable.ic_circle);
                    drivers_layout.setVisibility(View.GONE);
                    register_diver = 0;
                }
            } else {
                if (Objects.requireNonNull(driversRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    driversRadioButton.setImageResource(R.drawable.ic_check_circle);
                    drivers_layout.setVisibility(View.VISIBLE);
                    register_diver = 1;
                    licenceLevelList();
                    CompanyList();
                } else if (driversRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    driversRadioButton.setImageResource(R.drawable.ic_circle);
                    drivers_layout.setVisibility(View.GONE);
                    register_diver = 0;
                }
            }
        } else if (id == R.id.seaRequirementsRadioButtonLayout || id == R.id.seaRequirementsRadioButton) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(seaRequirementsRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(seaRequirementsRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    seaRequirementsRadioButton.setImageResource(R.drawable.ic_check_circle);
                    requirement_sea_layout.setVisibility(View.VISIBLE);
                    register_supplier = 1;
                } else if (Objects.equals(seaRequirementsRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(seaRequirementsRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    seaRequirementsRadioButton.setImageResource(R.drawable.ic_circle);
                    requirement_sea_layout.setVisibility(View.GONE);
                    register_supplier = 0;
                }
            } else {
                if (Objects.requireNonNull(seaRequirementsRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    seaRequirementsRadioButton.setImageResource(R.drawable.ic_check_circle);
                    requirement_sea_layout.setVisibility(View.VISIBLE);
                    register_supplier = 1;
                } else if (seaRequirementsRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    seaRequirementsRadioButton.setImageResource(R.drawable.ic_circle);
                    requirement_sea_layout.setVisibility(View.GONE);
                    register_supplier = 0;
                }
            }
        } else if (id == R.id.seaServicesRadioButtonLayout || id == R.id.seaServicesRadioButton) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(seaServicesRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(seaServicesRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    seaServicesRadioButton.setImageResource(R.drawable.ic_check_circle);
                    services_sea_layout.setVisibility(View.VISIBLE);
                    register_services = 1;
                    ActivityTypeList();
                } else if (Objects.equals(seaServicesRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(seaServicesRadioButton.getContext()
                                .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                    seaServicesRadioButton.setImageResource(R.drawable.ic_circle);
                    services_sea_layout.setVisibility(View.GONE);
                    register_services = 0;
                }
            } else {
                if (Objects.requireNonNull(seaServicesRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    seaServicesRadioButton.setImageResource(R.drawable.ic_check_circle);
                    services_sea_layout.setVisibility(View.VISIBLE);
                    register_services = 1;
                    ActivityTypeList();
                } else if (seaServicesRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    seaServicesRadioButton.setImageResource(R.drawable.ic_circle);
                    services_sea_layout.setVisibility(View.GONE);
                    register_services = 0;
                }
            }
        } else if (id == R.id.divingLayout || id == R.id.divingRadioButton) {
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

        } else if (id == R.id.fishingRadioButtonLayout || id == R.id.fishingRadioButton) {
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
        } else if (id == R.id.picnicRadioButtonLayout || id == R.id.picnicRadioButton) {
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
        } else if (id == R.id.divingRadio || id == R.id.divingCLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(divingRadio.getDrawable().getConstantState(),
                        Objects.requireNonNull(divingRadio.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_check_circle);
                    diving_cylinder = 1;
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                            (Objects.requireNonNull(freeRadioButton.getContext()
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
                    if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                            (Objects.requireNonNull(freeRadioButton.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_check_circle);
                    diving_cylinder = 1;
                    if (Objects.equals(freeRadioButton.getDrawable().getConstantState(),
                            getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (divingRadio.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    divingRadio.setImageResource(R.drawable.ic_circle);
                    diving_cylinder = 0;
                    if (Objects.equals(freeRadioButton.getDrawable().getConstantState(),
                            getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            }

        } else if (id == R.id.freeRadioButton || id == R.id.freeRadioButtonLayout) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if (Objects.equals(freeRadioButton.getDrawable().getConstantState(),
                        Objects.requireNonNull(freeRadioButton.getContext()
                                .getDrawable(R.drawable.ic_circle)).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving_free = 1;
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                            (Objects.requireNonNull(divingRadio.getContext()
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
                    if (Objects.requireNonNull(divingRadio.getDrawable().getConstantState()).equals
                            (Objects.requireNonNull(divingRadio.getContext()
                                    .getDrawable(R.drawable.ic_check_circle)).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                if (Objects.requireNonNull(freeRadioButton.getDrawable().getConstantState()).equals
                        (getResources().getDrawable(R.drawable.ic_circle).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_check_circle);
                    diving_free = 1;
                    if (Objects.equals(divingRadio.getDrawable().getConstantState(),
                            getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherLayout.setVisibility(View.GONE);
                    }
                } else if (freeRadioButton.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                    freeRadioButton.setImageResource(R.drawable.ic_circle);
                    diving_free = 0;
                    if (Objects.equals(divingRadio.getDrawable().getConstantState(),
                            getResources().getDrawable(R.drawable.ic_check_circle).getConstantState())) {
                        otherLayout.setVisibility(View.GONE);
                    }
                }
            }
        } else if (id == R.id.expireDataLayout) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "first");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "Date Picker");
        } else if (id == R.id.expireDateLicenceLayout) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("first", "second");
            newFragment.setArguments(bundle);
            assert getFragmentManager() != null;
            newFragment.show(getSupportFragmentManager(), "Date Picker");
        } else if (id == R.id.expireDataTanksLayout) {
            showDialog(DATE_DIALOG_ID_3);
        } else if (id == R.id.select_location) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 10);
        } else if (id == R.id.select_location_tanks) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 11);
        } else if (id == R.id.select_location_driving) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 12);
        } else if (id == R.id.select_location_requirement) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 13);
        } else if (id == R.id.select_location_service) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 14);
        } else if (id == R.id.cityLayout) {
            openWindowCity();
        } else if (id == R.id.tankTypeLayout) {
            openWindowTankTpe();
        } else if (id == R.id.licenceLevelLayout) {
            openWindowLevel();
        } else if (id == R.id.companyLayout) {
            openWindowCompany();
        } else if (id == R.id.licenceLevelOtherLayout) {
            openWindowLevelOther();
        } else if (id == R.id.companyOtherLayout) {
            openWindowCompanyOther();
        } else if (id == R.id.activityLayout) {
            openWindowActivity();
        } else if (id == R.id.ic_info) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (id == R.id.agreeLicenseCheckBox) {
            agreeLicenseCheckBox.setChecked(false);
            Intent intent = new Intent(this, PrivacyActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    public void register() {
        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        final String cityName = cityTxt.getText().toString().trim();
        final String cityName_ar = otherArCityEditText.getText().toString().trim();
        final String cityName_en = otherEnCityEditText.getText().toString().trim();

        final String mobile = mobileEditText.getText().toString().trim();

        final String boatNumber = boatNoEditText.getText().toString().trim();
        final String boatName = boatNameEditText.getText().toString().trim();
        final String passengersNumber = passengersNoEditText.getText().toString().trim();
        final String width = widthEditText.getText().toString().trim();
        final String widthTxt = areaWidthTxt.getText().toString().trim();
        final String height = heightEditText.getText().toString().trim();
        final String heightTxt = areaHieghtTxt.getText().toString().trim();
        final String rentValue = rentValueEditText.getText().toString().trim();
        final String location = select_location.getText().toString().trim();
        final String expireTxt = expireDateForm.getText().toString().trim();
        final String expireLicenceTxt = expireDateLicence.getText().toString().trim();

        final String tankType = tankTypeTxt.getText().toString().trim();
        final String tankType_ar = otherArTankTypeEditText.getText().toString().trim();
        final String tankType_en = otherEnTankTypeEditText.getText().toString().trim();
        final String tankNumber = tanksNoEditText.getText().toString().trim();
        final String rentValueTank = rentValueTanksEditText.getText().toString().trim();
        final String locationTank = select_location_tanks.getText().toString().trim();
        final String expireTxtTank = expireTanksTxt.getText().toString().trim();

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

        final String driverNameOther = driverNameOtherEditText.getText().toString().trim();
        final String licenceNumberOther = licenceDrivingOtherEditText.getText().toString().trim();
        final String licenceLevelOther = licenceLevelOtherTxt.getText().toString().trim();
        final String licenceLevelOther_ar = otherArLicencetEditText.getText().toString().trim();
        final String licenceLevelOther_en = otherEnLicencetEditText.getText().toString().trim();
        final String companyOther = companyOtherTxt.getText().toString().trim();
        final String companyOther_ar = otherArCompanyOtherEditText.getText().toString().trim();
        final String companyOther_en = otherEnCompanyOtherEditText.getText().toString().trim();

        final String recordNumber = recordNoCommercialEditText.getText().toString().trim();
        final String shopName = shopNameEditText.getText().toString().trim();
        final String describeShop = shopDescribeEditText.getText().toString().trim();
        final String requirementLocation = select_location_requirement.getText().toString().trim();

        final String activityType = activityTypeTxt.getText().toString().trim();
        final String title_ar = otherArEditText.getText().toString().trim();
        final String title_en = otherEnEditText.getText().toString().trim();
        final String activityDescribe = describeEditText.getText().toString().trim();
        final String activityRecordNumber = recordNoEditText.getText().toString().trim();
        final String serviceLocation = select_location_service.getText().toString().trim();
        int is_valid = 0;
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            usernameEditText.requestFocus();
            is_valid = 1;
        } else if (FontManager.textPersian(username)) {
            Log.e("true", FontManager.textPersian(username) + "");
            usernameEditText.setError(getString(R.string.username_english_characters));
            usernameEditText.requestFocus();
            is_valid = 1;
        } else if (!TextUtils.isEmpty(email) && !email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            emailEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            passwordEditText.requestFocus();
            is_valid = 1;
        } else if (password.length() < 6) {
            passwordEditText.setError(getString(R.string.pass_characters));
            passwordEditText.requestFocus();
            is_valid = 1;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.error_field_required));
            confirmPasswordEditText.requestFocus();
            is_valid = 1;
        } else if (!TextUtils.equals(confirmPassword, password)) {
            confirmPasswordEditText.setError(getString(R.string.correct_pass));
            confirmPasswordEditText.requestFocus();
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
            } else if (register_services == 0 && register_supplier == 0 && register_diver == 0 &&
                    register_tank == 0 && register_boat == 0) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_activity));
                is_valid = 1;
            } else if (!agreeLicenseCheckBox.isChecked()) {
                AppErrorsManager.showErrorDialog(this, getString(R.string.agree_of_terms));
                is_valid = 1;
            } else {
                is_valid = 0;
            }
        }
        if (is_valid == 0) {
            if (register_boat == 1) {
                if (TextUtils.isEmpty(boatNumber)) {
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
                    AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_passenger_no));
                    is_valid = 1;
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
                    AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_select_trip_type));
                    is_valid = 1;
                } else if (TextUtils.isEmpty(expireTxt)) {
                    expireDateForm.setError(getString(R.string.error_field_required));
                    expireDateForm.requestFocus();
                    is_valid = 1;
                } else if (TextUtils.isEmpty(expireLicenceTxt)) {
                    expireDateLicence.setError(getString(R.string.error_field_required));
                    expireDateLicence.requestFocus();
                    is_valid = 1;
                } else if (boatFile == null) {
                    AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image));
                    is_valid = 1;
                } else {
                    is_valid = 0;
                }
            }
            if (is_valid == 0) {
                if (register_tank == 1) {
                    if (TextUtils.isEmpty(tankType)) {
                        is_valid = 1;
                        tankTypeTxt.setError(getString(R.string.error_field_required));
                        tankTypeTxt.requestFocus();
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
                            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_tank_no));
                            is_valid = 1;
                        } else if (TextUtils.isEmpty(rentValueTank)) {
                            rentValueTanksEditText.setError(getString(R.string.error_field_required));
                            rentValueTanksEditText.requestFocus();
                            is_valid = 1;
                        } else if (tankFile == null) {
                            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image));
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
                    }
                }
            }
            if (is_valid == 0) {
                if (register_diver == 1) {
                    if (TextUtils.isEmpty(driverName)) {
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
                                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_choose_diving_type));
                                is_valid = 1;
                            } else if (TextUtils.isEmpty(noteDriver)) {
                                noteEditText.setError(getString(R.string.error_field_required));
                                noteEditText.requestFocus();
                                is_valid = 1;
                            } else if (driverFile == null) {
                                AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image));
                                is_valid = 1;
                            } else if (diving_cylinder == 1 && diving_free == 1) {
                                if (TextUtils.isEmpty(driverNameOther)) {
                                    driverNameOtherEditText.setError(getString(R.string.error_field_required));
                                    driverNameOtherEditText.requestFocus();
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
                                    }
                                    if (is_valid == 0) {
                                        if (driverOtherFile == null) {
                                            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image));
                                            is_valid = 1;
                                        } else {
                                            is_valid = 0;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (is_valid == 0) {
                if (register_supplier == 1) {
                    if (TextUtils.isEmpty(recordNumber)) {
                        is_valid = 1;
                        recordNoCommercialEditText.setError(getString(R.string.error_field_required));
                        recordNoCommercialEditText.requestFocus();
                    } else if (TextUtils.isEmpty(shopName)) {
                        is_valid = 1;
                        shopNameEditText.setError(getString(R.string.error_field_required));
                        shopNameEditText.requestFocus();
                    } else if (TextUtils.isEmpty(describeShop)) {
                        is_valid = 1;
                        shopDescribeEditText.setError(getString(R.string.error_field_required));
                        shopDescribeEditText.requestFocus();
                    } else if (recordFile == null) {
                        is_valid = 1;
                        AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_record_image));
                    } else if (TextUtils.isEmpty(requirementLocation)) {
                        is_valid = 1;
                        select_location_requirement.setError(getString(R.string.error_field_required));
                        select_location_requirement.requestFocus();
                    } else {
                        is_valid = 0;
                    }
                }
            }
            if (is_valid == 0) {
                if (register_services == 1) {
                    if (TextUtils.isEmpty(activityType)) {
                        is_valid = 1;
                        activityTypeTxt.setError(getString(R.string.error_field_required));
                        activityTypeTxt.requestFocus();
                    } else if (TextUtils.equals(activityType, getString(R.string.other))) {
                        if (TextUtils.isEmpty(title_en)) {
                            otherEnEditText.setError(getString(R.string.error_field_required));
                            otherEnEditText.requestFocus();
                            is_valid = 1;
                        } else if (TextUtils.isEmpty(title_ar)) {
                            otherArEditText.setError(getString(R.string.error_field_required));
                            otherArEditText.requestFocus();
                            is_valid = 1;
                        } else {
                            is_valid = 0;
                        }
                    }
                    if (is_valid == 0) {
                        if (TextUtils.isEmpty(activityDescribe)) {
                            is_valid = 1;
                            describeEditText.setError(getString(R.string.error_field_required));
                            describeEditText.requestFocus();
                        } else {
                            is_valid = 0;
                        }
                    }
                }
            }
            if (is_valid == 0) {
                Log.e("yyyy", "uuuuu" + city_id);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                        builder.addFormDataPart("email", email)
                                .addFormDataPart("password", password)
                                .addFormDataPart("name", username)
                                .addFormDataPart("mobile", mobile)
                                .addFormDataPart("user_type", "1");

                        if (TextUtils.equals(cityName, getString(R.string.other))) {
                            builder.addFormDataPart("city_title_ar", cityName_ar);
                            builder.addFormDataPart("city_title_en", cityName_en);
                        } else
                            builder.addFormDataPart("city", city_id);

                        builder.addFormDataPart("register_boat", String.valueOf(register_boat))
                                .addFormDataPart("register_jetski", String.valueOf(register_tank))
                                .addFormDataPart("register_diver", String.valueOf(register_diver))
                                .addFormDataPart("register_supplier", String.valueOf(register_supplier))
                                .addFormDataPart("register_services", String.valueOf(register_services));

                        if (fileSchema != null) {
                            builder.addFormDataPart("user_image", fileSchema.getName(),
                                    RequestBody.create(MediaType.parse("jpeg/png"), fileSchema));
                        }

                        if (register_boat == 1) {
                            builder.addFormDataPart("boat_no", boatNumber)
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
                                    .addFormDataPart("boat_driving_licence_expire_date", expireLicenceTxt);

                            if (boatFile != null) {
                                builder.addFormDataPart("boat_licence_image", boatFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), boatFile));
                            }
                        }
                        if (register_tank == 1) {
                            if (TextUtils.equals(tankType, getString(R.string.other))) {
                                builder.addFormDataPart("jetski_type_title_ar", tankType_ar);
                                builder.addFormDataPart("jetski_type_title_en", tankType_en);
                            } else
                                builder.addFormDataPart("jetski_type", tankTypeId);

                            builder.addFormDataPart("jetski_quantity", tankNumber)
                                    .addFormDataPart("jetski_hourly", rentValueTank)
                                    .addFormDataPart("jetski_location", locationTank)
                                    .addFormDataPart("jetski_licence_expire_date", expireTxtTank);

                            if (tankFile != null) {
                                builder.addFormDataPart("jetski_licence_image", tankFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), tankFile));
                            }
                        }
                        if (register_diver == 1) {
                            if (diving_free == 1 && diving_cylinder == 0) {
                                builder.addFormDataPart("diver_free_full_name", driverName)
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
                                if (driverFile != null) {
                                    builder.addFormDataPart("diver_free_licence_image", driverFile.getName(),
                                            RequestBody.create(MediaType.parse("jpeg/png"), driverFile));
                                }
                            }
                            if (diving_free == 0 && diving_cylinder == 1) {
                                builder.addFormDataPart("diver_tank_full_name", driverName)
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
                                if (driverFile != null) {
                                    builder.addFormDataPart("diver_tank_licence_image", driverFile.getName(),
                                            RequestBody.create(MediaType.parse("jpeg/png"), driverFile));
                                }
                            }
                            if (diving_free == 1 && diving_cylinder == 1) {
                                builder.addFormDataPart("diver_tank_full_name", driverName)
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
                                if (driverFile != null) {
                                    builder.addFormDataPart("diver_tank_licence_image", driverFile.getName(),
                                            RequestBody.create(MediaType.parse("jpeg/png"), driverFile));
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

                                if (driverOtherFile != null) {
                                    builder.addFormDataPart("diver_free_licence_image", driverOtherFile.getName(),
                                            RequestBody.create(MediaType.parse("jpeg/png"), driverOtherFile));
                                }
                            }
                        }
                        if (register_supplier == 1) {
                            builder.addFormDataPart("supplier_registration_no", recordNumber)
                                    .addFormDataPart("supplier_shop_name", shopName)
                                    .addFormDataPart("supplier_description", describeShop)
                                    .addFormDataPart("supplier_location", requirementLocation);
                            if (recordFile != null) {
                                builder.addFormDataPart("supplier_registration_image", recordFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), recordFile));
                            }
                        }
                        if (register_services == 1) {
                            if (TextUtils.equals(activityType, getString(R.string.other))) {
                                builder.addFormDataPart("activity_type_title_ar", title_ar);
                                builder.addFormDataPart("activity_type_title_en", title_en);
                                builder.addFormDataPart("service_activity_description", "");
                            } else
                                builder.addFormDataPart("service_activity_type", activity_id);

                            builder.addFormDataPart("service_activity_description", activityDescribe);
                            if (!TextUtils.isEmpty(activityRecordNumber)) {
                                builder.addFormDataPart("service_registration_no", activityRecordNumber);
                            }
                            if (!TextUtils.isEmpty(serviceLocation)) {
                                builder.addFormDataPart("service_location", serviceLocation);
                            }
                            if (serviceFile != null) {
                                builder.addFormDataPart("service_registration_image", serviceFile.getName(),
                                        RequestBody.create(MediaType.parse("jpeg/png"), serviceFile));
                            }
                        }

                        RequestBody requestBody = builder.build();
                        registerUserInfo(requestBody);
                    }
                }).start();
            }
        }

    }

    public void registerUserInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(RegisterPartnerActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            }
        });
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
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
                            + "register").post(requestBody).build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        assert response.body() != null;
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
                                            if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("en"))
                                                usernameEditText.setError(userNameError
                                                        .replaceAll("\\[", "")
                                                        .replaceAll("]", "")
                                                        .replaceAll("\"", ""));
                                            else
                                                usernameEditText.setError(getString(R.string.this_name_already_taken));
                                            usernameEditText.requestFocus();
                                        } else if (jsonError.has("email")) {
                                            String emailError = jsonError.optString("email");
                                            if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("en"))
                                                emailEditText.setError(emailError
                                                        .replaceAll("\\[", "")
                                                        .replaceAll("]", "")
                                                        .replaceAll("\"", ""));
                                            else
                                                emailEditText.setError(getString(R.string.this_email_already_taken));
                                            emailEditText.requestFocus();
                                        } else if (jsonError.has("mobile")) {
                                            String user_mobile = jsonError.optString("mobile");
                                            if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("en"))
                                                mobileEditText.setError(user_mobile
                                                        .replaceAll("\\[", "")
                                                        .replaceAll("]", "")
                                                        .replaceAll("\"", ""));
                                            else
                                                mobileEditText.setError(getString(R.string.mobile_already_taken));
                                            mobileEditText.requestFocus();
                                        }
                                    }
                                });
                            } else if (jsonObject.has("success")) {
                                String success = jsonObject.getString("success");
                                JSONObject jsonError = new JSONObject(success);
                                final String token = jsonError.getString("token");
                                AppPreferences.saveString(RegisterPartnerActivity.this, "token", token);
//                                final String name = jsonError.getString("name");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        startActivity(new Intent(RegisterPartnerActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                });
                            }
                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCityList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCityList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemCityList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
    public void openWindowWidthUnits() {
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

                for (int j = 0; j < spinnerItemWidthUnitsList.size(); j++) {

                    final String text = spinnerItemWidthUnitsList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemWidthUnitsList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemWidthUnitsList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemWidthUnitsList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemWidthUnitsList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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

                for (int j = 0; j < spinnerItemHeightUnitsList.size(); j++) {

                    final String text = spinnerItemHeightUnitsList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemHeightUnitsList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemHeightUnitsList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemHeightUnitsList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemHeightUnitsList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
    public void openWindowTankTpe() {
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

                for (int j = 0; j < spinnerItemTankTypeList.size(); j++) {

                    final String text = spinnerItemTankTypeList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemTankTypeList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemTankTypeList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemTankTypeList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemTankTypeList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openWindowCompany() {
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

                for (int j = 0; j < spinnerItemCompanyList.size(); j++) {

                    final String text = spinnerItemCompanyList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemCompanyList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCompanyList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCompanyList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemCompanyList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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

                for (int j = 0; j < spinnerItemCompanyOtherList.size(); j++) {

                    final String text = spinnerItemCompanyOtherList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemCompanyOtherList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemCompanyOtherList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemCompanyOtherList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemCompanyOtherList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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

                for (int j = 0; j < spinnerItemLevelList.size(); j++) {

                    final String text = spinnerItemLevelList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemLevelList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemLevelList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemLevelList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemLevelList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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

                for (int j = 0; j < spinnerItemLevelOtherList.size(); j++) {

                    final String text = spinnerItemLevelOtherList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemLevelOtherList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemLevelOtherList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemLevelOtherList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemLevelOtherList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
    public void openWindowActivity() {
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

                for (int j = 0; j < spinnerItemActivityList.size(); j++) {

                    final String text = spinnerItemActivityList.get(j).getText().toLowerCase();
                    final String textAr = spinnerItemActivityList.get(j).getTextA().toLowerCase();
                    if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemActivityList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemActivityList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(RegisterPartnerActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(RegisterPartnerActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        activity_id = filteredList.get(position).getId();
                        activityTypeTxt.setText(status);
                        if (TextUtils.equals(activityTypeTxt.getText().toString(), getString(R.string.other))) {
                            otherArEditText.setVisibility(View.VISIBLE);
                            otherEnEditText.setVisibility(View.VISIBLE);
                        } else {
                            otherArEditText.setVisibility(View.GONE);
                            otherEnEditText.setVisibility(View.GONE);
                        }
                        activityTypeTxt.setError(null);
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
        spinnerAdapter = new SpinnerAdapter(this, spinnerItemActivityList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(RegisterPartnerActivity.this).equals("ar")) {
                    status = spinnerItemActivityList.get(position).getTextA();
                } else {
                    status = spinnerItemActivityList.get(position).getText();
                }
                activity_id = spinnerItemActivityList.get(position).getId();
                activityTypeTxt.setText(status);
                if (TextUtils.equals(activityTypeTxt.getText().toString(), getString(R.string.other))) {
                    otherArEditText.setVisibility(View.VISIBLE);
                    otherEnEditText.setVisibility(View.VISIBLE);
                } else {
                    otherArEditText.setVisibility(View.GONE);
                    otherEnEditText.setVisibility(View.GONE);
                }
                activityTypeTxt.setError(null);
                alertDialog.dismiss();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
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
                                city_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("name_en");
                                String name_ar = jsonObject1.getString("name_ar");
                                SpinnerItem cityData = new SpinnerItem(city_id, name_en, name_ar);
                                spinnerItemCityList.add(cityData);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void measureWidthUnitList() {
        spinnerItemWidthUnitsList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
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
                                width_unit_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("name_en");
                                String name_ar = jsonObject1.getString("name_ar");
                                SpinnerItem cityData = new SpinnerItem(width_unit_id, name_en, name_ar);
                                spinnerItemWidthUnitsList.add(cityData);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void measureHightUnitList() {
        spinnerItemHeightUnitsList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void tankTypeList() {
        spinnerItemTankTypeList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void licenceLevelList() {
        spinnerItemLevelList.clear();
        spinnerItemLevelOtherList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void CompanyList() {
        spinnerItemCompanyList.clear();
        spinnerItemCompanyOtherList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    public void ActivityTypeList() {
        spinnerItemActivityList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "service-activity-type").get().build();
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
                            JSONArray jsonArray = successObject.getJSONArray("activity_type");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                activity_id = jsonObject1.getString("id");
                                String name_en = jsonObject1.getString("title_en");
                                String name_ar = jsonObject1.getString("title_ar");
                                SpinnerItem company = new SpinnerItem(activity_id, name_en, name_ar);
                                spinnerItemActivityList.add(company);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, response + "");
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(RegisterPartnerActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                return datePickerDialog;
            case DATE_DIALOG_ID_2:
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, myDateListener_, year, month, day);
                datePickerDialog1.getDatePicker().setMinDate(c.getTimeInMillis());
                return datePickerDialog1;
            case DATE_DIALOG_ID_3:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(this, myDateListenerTanks, year, month, day);
                datePickerDialog2.getDatePicker().setMinDate(c.getTimeInMillis());
                return datePickerDialog2;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            expireDateForm.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2 + 1).append("/").append(arg1));
            expireDateForm.setError(null);
            day = arg3;
            month = arg2 + 1;
            year = arg1;
        }
    };
    private DatePickerDialog.OnDateSetListener myDateListener_ = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            expireDateLicence.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2 + 1).append("/").append(arg1));
            expireDateLicence.setError(null);
            day = arg3;
            month = arg2 + 1;
            year = arg1;
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListenerTanks = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            expireTanksTxt.setText(new StringBuilder().append(arg3).append("/")
                    .append(arg2 + 1).append("/").append(arg1));
            expireTanksTxt.setError(null);
            day = arg3;
            month = arg2 + 1;
            year = arg1;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data != null) {
            select_location.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location.setError(null);
        } else if (requestCode == 11 && data != null) {
            select_location_tanks.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_tanks.setError(null);
        } else if (requestCode == 12 && data != null) {
            select_location_driving.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_driving.setError(null);
        } else if (requestCode == 13 && data != null) {
            select_location_requirement.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_requirement.setError(null);
        } else if (requestCode == 14 && data != null) {
            select_location_service.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_service.setError(null);
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA) {
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
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_4) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile4(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_4) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile4();
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_5) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile5(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_5) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile5();
            }
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_6) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile6(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_6) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile6();
            }
        } else if (requestCode == 1 && data != null) {
            agreeLicenseCheckBox.setChecked(true);
        }
    }

    public void openDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
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
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    public void openDialog1() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
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
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera1();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    public void openDialog2() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
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
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera2();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    public void openDialog3() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
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
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera3();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    public void openDialog4() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED) {
                    openGalleryFile4();
                } else
                    checkAndRequestPermissions();
//                openGalleryFile();
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera4();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    public void openDialog5() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED) {
                    openGalleryFile5();
                } else
                    checkAndRequestPermissions();
//                openGalleryFile();
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera5();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });
        deleteDialog.show();
    }

    public void openDialog6() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(this, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.galleryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED) {
                    openGalleryFile6();
                } else
                    checkAndRequestPermissions();
//                openGalleryFile();
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                int hasPerm2 = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                int hasPerm3 = pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
                if (hasPerm2 == PackageManager.PERMISSION_GRANTED || hasPerm3 == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera6();
                } else
                    checkAndRequestPermissions();

                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
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

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        fileSchema = new File(pdfPathHolder);
        logo_user.setVisibility(View.GONE);
        Picasso.get().load(uri).into(log_img);

    }

    private void captureImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        fileSchema = new File(mMediaUri);
        logo_user.setVisibility(View.GONE);
//        Picasso.get().load(photoUri).into(log_img);
        try {
            ExifInterface exifObject = new ExifInterface(fileSchema.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            log_img.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        boatFile = new File(pdfPathHolder);
        imageLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageView);

    }

    private void captureImageFromCamera1() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_1);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile1() {
        Log.e("uriii", mMediaUri.toString());
        boatFile = new File(mMediaUri);
        imageLayout.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageView);
//        imageView.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(boatFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageView.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        tankFile = new File(pdfPathHolder);
        imageTankLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageViewTank);

    }

    private void captureImageFromCamera2() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile2() {
        Log.e("uriii", mMediaUri.toString());
        tankFile = new File(mMediaUri);
        imageTankLayout.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageViewTank);
//        imageViewTank.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(tankFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageViewTank.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        driverFile = new File(pdfPathHolder);
        imageNewLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageViewNew);

    }

    private void captureImageFromCamera3() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_3);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile3() {
        Log.e("uriii", mMediaUri.toString());
        driverFile = new File(mMediaUri);
        imageNewLayout.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageViewNew);
//        imageViewNew.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(driverFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageViewNew.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openGalleryFile4() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_4);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile4(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        recordFile = new File(pdfPathHolder);
        imageRecordLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageRecordNew);

    }

    private void captureImageFromCamera4() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_4);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile4() {
        Log.e("uriii", mMediaUri.toString());
        recordFile = new File(mMediaUri);
        imageRecordLayout.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageRecordNew);
//        imageRecordNew.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(recordFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageRecordNew.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openGalleryFile5() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_5);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile5(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        serviceFile = new File(pdfPathHolder);
        imageRecordLayout_.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageRecordNew_);

    }

    private void captureImageFromCamera5() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_5);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile5() {
        Log.e("uriii", mMediaUri.toString());
        serviceFile = new File(mMediaUri);
        imageRecordLayout_.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageRecordNew_);
//        imageRecordNew_.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(serviceFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageRecordNew_.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openGalleryFile6() {
        Intent intent = new Intent();
        intent.setType("*/*");
        String[] mimetypes = {"image/*"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_CODE_SCHEMA_6);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImageFromGalleryFile6(Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        Log.e("image", uri + "");

        String pdfPathHolder = FilePath.getPath(this, uri);
        Log.e("pdfPathHolder", pdfPathHolder + "");
        assert pdfPathHolder != null;
        driverOtherFile = new File(pdfPathHolder);
        imageNewOtherLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(imageViewNewOther);

    }

    private void captureImageFromCamera6() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                mMediaUri = photoFile.toURI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_6);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile6() {
        Log.e("uriii", mMediaUri.toString());
        driverOtherFile = new File(mMediaUri);
        imageNewOtherLayout.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(imageViewNewOther);
//        imageViewNewOther.setRotation(90);
        try {
            ExifInterface exifObject = new ExifInterface(driverOtherFile.getPath());

            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                capturedPhoto.setImageBitmap(imageRotate);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Bitmap imageRotate = FontManager.rotateBitmap(bitmap, orientation);
            imageViewNewOther.setImageBitmap(imageRotate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
