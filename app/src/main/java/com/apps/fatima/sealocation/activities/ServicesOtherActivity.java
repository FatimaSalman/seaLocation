package com.apps.fatima.sealocation.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ServicesOtherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA_5 = 60;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_5 = 61;

    private ImageView imageRecordNew_;

    private TextView select_location_service, activityTypeTxt;

    private EditText describeEditText, recordNoEditText, otherEnEditText, otherArEditText;
    private RelativeLayout imageRecordLayout_;
    private URI mMediaUri;
    private File serviceFile;
    private Uri photoUri;
    private ProgressDialog progressDialog;
    private Handler handler;
    private List<SpinnerItem> spinnerItemActivityList = new ArrayList<>();
    private AlertDialog alertDialog;
    private String activity_id, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_other);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");
        init();
        ActivityTypeList();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);

        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);

        TextView attachImageTxt = findViewById(R.id.attachImageTxt);
        Button approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        imageRecordNew_ = findViewById(R.id.imageRecordNew_);

        select_location_service = findViewById(R.id.select_location_service);

        activityTypeTxt = findViewById(R.id.activityTypeTxt);

        imageRecordLayout_ = findViewById(R.id.imageRecordLayout_);

        RelativeLayout activityLayout = findViewById(R.id.activityLayout);

        backLayout.setOnClickListener(this);

        activityLayout.setOnClickListener(this);

        attachImageTxt.setOnClickListener(this);

        select_location_service.setOnClickListener(this);
        approveBtn.setOnClickListener(this);

        describeEditText = findViewById(R.id.describeEditText);
        recordNoEditText = findViewById(R.id.recordNoEditText);
        otherEnEditText = findViewById(R.id.otherEnEditText);
        otherArEditText = findViewById(R.id.otherArEditText);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            register();
        } else if (id == R.id.attachImageTxt) {
            openDialog5();
        } else if (id == R.id.select_location_service) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 14);
        } else if (id == R.id.activityLayout) {
            openWindowActivity();
        }
    }

    public void register() {
        final String activityType = activityTypeTxt.getText().toString().trim();
        final String title_ar = otherArEditText.getText().toString().trim();
        final String title_en = otherEnEditText.getText().toString().trim();
        final String activityDescribe = describeEditText.getText().toString().trim();
        final String activityRecordNumber = recordNoEditText.getText().toString().trim();
        final String serviceLocation = select_location_service.getText().toString().trim();
        int is_valid;

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
        } else if (TextUtils.isEmpty(activityDescribe)) {
            is_valid = 1;
            describeEditText.setError(getString(R.string.error_field_required));
            describeEditText.requestFocus();
        } else {
            is_valid = 0;
        }

        if (is_valid == 0) {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

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
                progressDialog = new ProgressDialog(ServicesOtherActivity.this);
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
                            + "add_services_activity").post(requestBody)
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
                                        Intent intent = new Intent(ServicesOtherActivity.this, MainActivity.class);
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
                                    AppErrorsManager.showErrorDialog(ServicesOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(ServicesOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(ServicesOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(ServicesOtherActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

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
                    if (AppLanguage.getLanguage(ServicesOtherActivity.this).equals("ar")) {
                        if (textAr.contains(query)) {
                            filteredList.add(spinnerItemActivityList.get(j));
                        }
                    } else {
                        if (text.contains(query)) {
                            filteredList.add(spinnerItemActivityList.get(j));
                        }
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(ServicesOtherActivity.this));
                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(ServicesOtherActivity.this, filteredList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status;
                        if (AppLanguage.getLanguage(ServicesOtherActivity.this).equals("ar")) {
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
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, spinnerItemActivityList, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String status;
                if (AppLanguage.getLanguage(ServicesOtherActivity.this).equals("ar")) {
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
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.show();
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

                        } catch (final JSONException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(ServicesOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AppErrorsManager.showErrorDialog(ServicesOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AppErrorsManager.showErrorDialog(ServicesOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            AppErrorsManager.showErrorDialog(ServicesOtherActivity.this, getString(R.string.error_network));
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 14 && data != null) {
            select_location_service.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_service.setError(null);
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_5) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile5(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_5) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile5();
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
}
