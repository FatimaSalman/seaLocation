package com.apps.fatima.sealocation.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.apps.fatima.sealocation.R;
import com.apps.fatima.sealocation.activities.MainActivity;

import java.util.Locale;

public class AppLanguage {

    public final static String ARABIC = "ar";
    public final static String PERSIAN = "fa";

    public static String getLanguage(Context context) {
        return AppPreferences.getString(context, "language", Locale.getDefault().getLanguage());
    }

    public static void saveLanguage(Context context, String language) {
        AppPreferences.saveString(context, "language", language);
    }

    public static void openDialogLanguage(final Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_language_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(context).create();
        RelativeLayout fontLayout = dialogView.findViewById(R.id.layout);
        FontManager.applyFont(context, fontLayout);
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.arabicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLang(context, "ar");
                AppLanguage.saveLanguage(context, "ar");
                context.startActivity(new Intent(context, MainActivity.class));
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.englishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLang(context, "en");
                AppLanguage.saveLanguage(context, "en");
                context.startActivity(new Intent(context, MainActivity.class));
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private static void changeLang(Context context, String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        ((Activity) context).getBaseContext().getResources().updateConfiguration(config,
                ((Activity) context).getBaseContext().getResources().getDisplayMetrics());
//        updateTexts();
    }
}
