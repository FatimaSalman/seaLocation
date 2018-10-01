package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class TankOtherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA_2 = 54;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2 = 55;

    private ImageView imageViewTank;

    private TextView expireTanksTxt, select_location_tanks, tankTypeTxt;

    private EditText rentValueTanksEditText;
    private TextView tanksNoEditText;
    private RelativeLayout imageTankLayout;
    private int year, month, day;
    static final int DATE_DIALOG_ID_3 = 300;
    private URI mMediaUri;
    private File tankFile;
    private Uri photoUri;
    private ProgressDialog progressDialog;
    private Handler handler;
    private List<SpinnerItem> spinnerItemTankTypeList = new ArrayList<>();
    private AlertDialog alertDialog;
    private String tankTypeId, token;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tank_other);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");

        init();
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        tankTypeList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);

        TextView licenceTankImage = findViewById(R.id.licenceTankImage);

        Button approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        imageViewTank = findViewById(R.id.imageViewTank);

        select_location_tanks = findViewById(R.id.select_location_tanks);
        tankTypeTxt = findViewById(R.id.tankTypeTxt);

        imageTankLayout = findViewById(R.id.imageTankLayout);

        RelativeLayout expireDataTanksLayout = findViewById(R.id.expireDataTanksLayout);

        RelativeLayout tankTypeLayout = findViewById(R.id.tankTypeLayout);

        expireTanksTxt = findViewById(R.id.expireTanksTxt);
        backLayout.setOnClickListener(this);

        tankTypeLayout.setOnClickListener(this);

        licenceTankImage.setOnClickListener(this);

        expireDataTanksLayout.setOnClickListener(this);
        select_location_tanks.setOnClickListener(this);
        RelativeLayout plusLayout = findViewById(R.id.plusLayout);
        RelativeLayout minusLayout = findViewById(R.id.minusLayout);
        plusLayout.setOnClickListener(this);
        minusLayout.setOnClickListener(this);
        approveBtn.setOnClickListener(this);

        tanksNoEditText = findViewById(R.id.tanksNoEditText);
        rentValueTanksEditText = findViewById(R.id.rentValueTanksEditText);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int totalCount = Integer.parseInt(tanksNoEditText.getText().toString());
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            register();
        } else if (id == R.id.minusLayout) {
            if (totalCount != 0)
                tanksNoEditText.setText(String.valueOf(totalCount - 1));
        } else if (id == R.id.plusLayout) {
            tanksNoEditText.setText(String.valueOf(totalCount + 1));
        } else if (id == R.id.licenceTankImage) {
            openDialog2();
        } else if (id == R.id.expireDataTanksLayout) {
            showDialog(DATE_DIALOG_ID_3);
        } else if (id == R.id.select_location_tanks) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 11);
        } else if (id == R.id.tankTypeLayout) {
            openWindowTankTpe();
        }
    }

    public void register() {
        final String tankType = tankTypeTxt.getText().toString().trim();
        final String tankNumber = tanksNoEditText.getText().toString().trim();
        final String rentValueTank = rentValueTanksEditText.getText().toString().trim();
        final String locationTank = select_location_tanks.getText().toString().trim();
        final String expireTxtTank = expireTanksTxt.getText().toString().trim();

        if (TextUtils.isEmpty(tankType)) {
            tankTypeTxt.setError(getString(R.string.error_field_required));
            tankTypeTxt.requestFocus();
        } else if (TextUtils.isEmpty(tankNumber)) {
            tanksNoEditText.setError(getString(R.string.error_field_required));
            tanksNoEditText.requestFocus();
        } else if (tankNumber.equals("0")) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_enter_tank_no));
        } else if (TextUtils.isEmpty(rentValueTank)) {
            rentValueTanksEditText.setError(getString(R.string.error_field_required));
            rentValueTanksEditText.requestFocus();
        } else if (tankFile == null) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_image));
        } else if (TextUtils.isEmpty(locationTank)) {
            select_location_tanks.setError(getString(R.string.error_field_required));
            select_location_tanks.requestFocus();
        } else if (TextUtils.isEmpty(expireTxtTank)) {
            expireTanksTxt.setError(getString(R.string.error_field_required));
            expireTanksTxt.requestFocus();
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("jetski_type", tankTypeId)
                            .addFormDataPart("jetski_quantity", tankNumber)
                            .addFormDataPart("jetski_hourly", rentValueTank)
                            .addFormDataPart("jetski_location", locationTank)
                            .addFormDataPart("jetski_licence_expire_date", expireTxtTank);

                    if (tankFile != null) {
                        builder.addFormDataPart("jetski_licence_image", tankFile.getName(),
                                RequestBody.create(MediaType.parse("jpeg/png"), tankFile));
                    }

                    RequestBody requestBody = builder.build();
                    registerUserInfo(requestBody);
                }
            }).start();
        }
    }

    public void registerUserInfo(final RequestBody requestBody) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(TankOtherActivity.this);
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
                            + "add_jetski_activity").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token).build();
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
                                    }
                                });
                            } else if (jsonObject.has("success")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        Intent intent = new Intent(TankOtherActivity.this, MainActivity.class);
                                        intent.putExtra("profile", "profile");
                                        startActivity(intent);
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
                                    AppErrorsManager.showErrorDialog(TankOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(TankOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(TankOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(TankOtherActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

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
                    if (AppLanguage.getLanguage(TankOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemTankTypeList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemTankTypeList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(TankOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(TankOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(TankOtherActivity.this).equals("ar")) {
                            status = filteredList.get(position).getTextA();
                        } else {
                            status = filteredList.get(position).getText();
                        }
                        tankTypeId = filteredList.get(position).getId();
                        tankTypeTxt.setText(status);
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
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, spinnerItemTankTypeList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(TankOtherActivity.this).equals("ar")) {
                    status = spinnerItemTankTypeList.get(position).getTextA();
                } else {
                    status = spinnerItemTankTypeList.get(position).getText();
                }
                tankTypeId = spinnerItemTankTypeList.get(position).getId();
                tankTypeTxt.setText(status);
                tankTypeTxt.setError(null);
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

    public void tankTypeList() {
        spinnerItemTankTypeList.clear();
        InternetConnectionUtils.isInternetAvailable(this, new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    final OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL +
                            "jetski-types").get().build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = response.body().string();
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
                                    AppErrorsManager.showErrorDialog(TankOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(TankOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(TankOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(TankOtherActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_3:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(this, myDateListenerTanks, year, month, day);
                datePickerDialog2.getDatePicker().setMinDate(c.getTimeInMillis());
                return datePickerDialog2;
        }
        return null;
    }


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
        if (requestCode == 11 && data != null) {
            select_location_tanks.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_tanks.setError(null);
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_2) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile2(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile2();
            }
        }
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

}
