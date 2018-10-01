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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.apps.fatima.sealocation.R;
import com.squareup.picasso.Picasso;

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

public class SupplierOtherActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA_4 = 58;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_4 = 59;
    private ImageView imageRecordNew;
    private TextView select_location_requirement;
    private EditText recordNoCommercialEditText, shopNameEditText, shopDescribeEditText;
    private RelativeLayout imageRecordLayout;
    private URI mMediaUri;
    private File recordFile;
    private Uri photoUri;
    private ProgressDialog progressDialog;
    private Handler handler;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_other);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(this, "token");
        init();
    }

    public void init() {
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        FontManager.applyFont(this, layout);
        ImageView ic_back = findViewById(R.id.ic_back);
        TextView attachImageTxt1 = findViewById(R.id.attachImageTxt1);
        Button approveBtn = findViewById(R.id.approveBtn);
        if (AppLanguage.getLanguage(this).equals("en")) {
            ic_back.setImageResource(R.drawable.ic_right_arrow);
        }

        imageRecordNew = findViewById(R.id.imageRecordNew);

        select_location_requirement = findViewById(R.id.select_location_requirement);

        imageRecordLayout = findViewById(R.id.imageRecordLayout);

        backLayout.setOnClickListener(this);

        attachImageTxt1.setOnClickListener(this);

        select_location_requirement.setOnClickListener(this);

        approveBtn.setOnClickListener(this);

        recordNoCommercialEditText = findViewById(R.id.recordNoCommercialEditText);
        shopNameEditText = findViewById(R.id.shopNameEditText);
        shopDescribeEditText = findViewById(R.id.shopDescribeEditText);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.approveBtn) {
            register();
        } else if (id == R.id.attachImageTxt1) {
            openDialog4();
        } else if (id == R.id.select_location_requirement) {
            Intent intent = new Intent(this, BigSelectMapActivity.class);
            startActivityForResult(intent, 13);
        }
    }

    public void register() {
        final String recordNumber = recordNoCommercialEditText.getText().toString().trim();
        final String shopName = shopNameEditText.getText().toString().trim();
        final String describeShop = shopDescribeEditText.getText().toString().trim();
        final String requirementLocation = select_location_requirement.getText().toString().trim();

        if (TextUtils.isEmpty(recordNumber)) {
            recordNoCommercialEditText.setError(getString(R.string.error_field_required));
            recordNoCommercialEditText.requestFocus();
        } else if (TextUtils.isEmpty(shopName)) {
            shopNameEditText.setError(getString(R.string.error_field_required));
            shopNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(describeShop)) {
            shopDescribeEditText.setError(getString(R.string.error_field_required));
            shopDescribeEditText.requestFocus();
        } else if (recordFile == null) {
            AppErrorsManager.showErrorDialog(this, getString(R.string.you_should_attach_record_image));
        } else if (TextUtils.isEmpty(requirementLocation)) {
            select_location_requirement.setError(getString(R.string.error_field_required));
            select_location_requirement.requestFocus();
        } else {
            Log.e("yyyy", "uuuuu");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                    builder.addFormDataPart("supplier_registration_no", recordNumber)
                            .addFormDataPart("supplier_shop_name", shopName)
                            .addFormDataPart("supplier_description", describeShop)
                            .addFormDataPart("supplier_location", requirementLocation);

                    if (recordFile != null) {
                        builder.addFormDataPart("supplier_registration_image", recordFile.getName(),
                                RequestBody.create(MediaType.parse("jpeg/png"), recordFile));
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
                progressDialog = new ProgressDialog(SupplierOtherActivity.this);
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
                            + "add_supplier_activity").post(requestBody)
                            .addHeader("Authorization", "Bearer " + token).build();
                    final okhttp3.Response response;
                    try {
                        response = client.newCall(request).execute();
                        String response_data = Objects.requireNonNull(response.body()).string();
                        Log.e("aaa", response_data);
                        try {
                            final JSONObject jsonObject = new JSONObject(response_data);
                            if (jsonObject.has("error")) {
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
                                        Intent intent = new Intent(SupplierOtherActivity.this, MainActivity.class);
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
                                    AppErrorsManager.showErrorDialog(SupplierOtherActivity.this, e.getMessage());
                                }
                            });
                        }
                        if (!response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    AppErrorsManager.showErrorDialog(SupplierOtherActivity.this, response + "");
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                                AppErrorsManager.showErrorDialog(SupplierOtherActivity.this, e.getMessage());
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            AppErrorsManager.showErrorDialog(SupplierOtherActivity.this, getString(R.string.error_network));
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
        if (requestCode == 13 && data != null) {
            select_location_requirement.setText(data.getDoubleExtra("latitude", 0)
                    + " , " + data.getDoubleExtra("longitude", 0));
            select_location_requirement.setError(null);
        } else if (requestCode == GALLERY_REQUEST_CODE_SCHEMA_4) {
            if (resultCode == RESULT_OK) {
                getImageFromGalleryFile4(data);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_4) {
            if (resultCode == RESULT_OK) {
                getImageFromCameraFile4();
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

}


