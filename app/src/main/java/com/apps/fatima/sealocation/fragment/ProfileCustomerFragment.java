package com.apps.fatima.sealocation.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.apps.fatima.sealocation.activities.MainActivity;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Callback;
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

import static android.app.Activity.RESULT_OK;

public class ProfileCustomerFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE_SCHEMA = 50;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 51;
    private URI mMediaUri;
    private Uri photoUri;
    private File fileSchema;
    private CircularImageView log_img;
    private ImageView logo_user;
    private EditText usernameEditText, emailEditText, mobileEditText, passwordEditText;
    private Handler handler;
    private ProgressDialog progressDialog;
    private String token;
    private ProgressBar progressbar;
    private boolean isFragmentLoaded = false;
    private String password;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        handler = new Handler(Looper.getMainLooper());
        token = AppPreferences.getString(getActivity(), "token");
        getUserInfo(token);
        init(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void init(View view) {
        RelativeLayout layout = view.findViewById(R.id.layout);
        TextView nameTxt = Objects.requireNonNull(getActivity()).findViewById(R.id.nameTxt);
//        nameTxt.setText(R.string.my_profile);
        FontManager.applyFont(getActivity(), layout);
        FontManager.applyFont(getActivity(), nameTxt);
        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        Button updateBtn = view.findViewById(R.id.updateBtn);
        Button select_language_btn = view.findViewById(R.id.select_language_btn);
        TextView changeImageTxt = view.findViewById(R.id.changeImageTxt);
        log_img = view.findViewById(R.id.log_img);
        logo_user = view.findViewById(R.id.logo_user);

        progressbar = view.findViewById(R.id.progressbar);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        mobileEditText = view.findViewById(R.id.mobileEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);


        logoutBtn.setOnClickListener(this);
        select_language_btn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        changeImageTxt.setOnClickListener(this);
        log_img.setOnClickListener(this);
        logo_user.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.logoutBtn) {
            FontManager.logOut(getActivity());
        } else if (id == R.id.updateBtn) {
            updateUser();
        } else if (id == R.id.log_img || id == R.id.changeImageTxt || id == R.id.logo_user) {
            openDialog();
        } else if (id == R.id.select_language_btn) {
            openDialogLanguage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openDialogLanguage() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_language_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getActivity()).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(Objects.requireNonNull(getActivity()), fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.arabicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLang("ar");
                AppLanguage.saveLanguage(getActivity(), "ar");
                startActivity(new Intent(getActivity(), MainActivity.class));
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.englishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLang("en");
                AppLanguage.saveLanguage(getActivity(), "en");
                startActivity(new Intent(getActivity(), MainActivity.class));
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        Objects.requireNonNull(getActivity()).getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
//        updateTexts();
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

//                            String id = jsonObject.getString("id");
                            final String name = userObject.getString("name");
                            final String email = userObject.getString("email");
                            final String mobile = userObject.getString("mobile");
                            final String user_image = userObject.getString("user_image");

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    usernameEditText.setText(name);
                                    if (!email.equals("null"))
                                        emailEditText.setText(email);
                                    mobileEditText.setText(mobile);
//                                    TextView guestTextView = findViewById(R.id.guestTextView);
//                                    guestTextView.setText(name);
                                    if (user_image.equals("null")) {
                                        logo_user.setVisibility(View.VISIBLE);
                                        logo_user.setImageResource(R.drawable.img_user);
                                        log_img.setVisibility(View.GONE);
                                    } else {
                                        logo_user.setVisibility(View.GONE);
                                        log_img.setVisibility(View.VISIBLE);

                                        Picasso.get()
                                                .load(FontManager.IMAGE_URL + user_image) // web image url
                                                .fit().centerInside()
//                                                .transform(transformation)
                                                .rotate(90)                    //if you want to rotate by 90 degrees
//                                                .error(R.drawable.ic_launcher)
//                                                .placeholder(R.drawable.ic_launcher)
                                                .into(log_img);
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

    public void updateUser() {
        final String userName = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String mobile = mobileEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            usernameEditText.requestFocus();
        } else if (!TextUtils.isEmpty(email) && !email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            emailEditText.requestFocus();
        } else if (TextUtils.isEmpty(mobile)) {
            mobileEditText.setError(getString(R.string.error_field_required));
            mobileEditText.requestFocus();
        } else if (mobile.length() > 9 || mobile.length() < 9) {
            mobileEditText.setError(getString(R.string.your_number_must_be_not_more_or_not_less_ten));
            mobileEditText.requestFocus();
        } else if (mobile.startsWith("0")) {
            mobileEditText.setError(getString(R.string.first_digit_not_zero));
            mobileEditText.requestFocus();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("email", email)
                            .addFormDataPart("name", userName)
                            .addFormDataPart("mobile", mobile);
                    if (fileSchema != null) {
                        builder.addFormDataPart("user_image", fileSchema.getName(),
                                RequestBody.create(MediaType.parse("jpeg/png"), fileSchema));
                    }
                    if (!TextUtils.isEmpty(password)) {
                        builder.addFormDataPart("password", password);
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
                    OkHttpClient client;

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(5, TimeUnit.MINUTES)
                            .writeTimeout(5, TimeUnit.MINUTES)
                            .readTimeout(5, TimeUnit.MINUTES);

                    client = builder.build();
                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                            + "update-profile").post(requestBody)
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
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        if (!TextUtils.isEmpty(password)) {
                                            AppErrorsManager.showSuccessDialog(getActivity(), getString(R.string.update_successfully), new DialogInterface.OnClickListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    FontManager.logOut(getActivity());
                                                    Objects.requireNonNull(getActivity()).finish();
                                                }
                                            });
                                        } else
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
        progressbar.setVisibility(View.VISIBLE);
        logo_user.setVisibility(View.GONE);
        log_img.setVisibility(View.VISIBLE);
        Picasso.get().load(uri).into(log_img, new Callback() {
            @Override
            public void onSuccess() {
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressbar.setVisibility(View.GONE);
            }
        });
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void getImageFromCameraFile() {
        Log.e("uriii", mMediaUri.toString());
        fileSchema = new File(mMediaUri);
        progressbar.setVisibility(View.GONE);
        logo_user.setVisibility(View.GONE);
        log_img.setVisibility(View.VISIBLE);
//        Picasso.get().load(photoUri).into(log_img, new Callback() {
//            @Override
//            public void onSuccess() {
//                progressbar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                progressbar.setVisibility(View.GONE);
//            }
//        });
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
