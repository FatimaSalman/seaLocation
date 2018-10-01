package com.apps.fatima.sealocation.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;


import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AppPreferences {

    public static void delete(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveStringSet(Context context, String key, Set<String> values) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, null);
        editor.apply();

        editor.putStringSet(key, values);
        editor.apply();
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void saveFloat(Context context, String key, float value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void saveOrEditBoolean(Context context, String key,
                                         boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        String value = null;
        try {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            value = sharedPreferences.getString(key, "0");

        } catch (Exception e) {

            Log.e("Ramzy", "AppPreferences getString() error = " + e.getMessage());
        }

        return value;
    }

    public static String getString(Context context, String key, String defaultValue) {
        String value = null;
        try {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            value = sharedPreferences.getString(key, defaultValue);

        } catch (Exception ignored) {

        }

        return value;
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defaultValues) {
        Set<String> value = null;
        try {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            value = sharedPreferences.getStringSet(key, defaultValues);

        } catch (Exception ignored) {

        }

        return value;
    }

    public static Set<String> getStringSet(Context context, String key) {
        Set<String> value = null;
        try {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            value = sharedPreferences.getStringSet(key, null);

        } catch (Exception ignored) {

        }

        return value;
    }

    public static long getLong(Context context, String key) {
        long value = 0;
        try {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            value = sharedPreferences.getLong(key, 0);

        } catch (Exception ignored) {

        }

        return value;
    }

    public static float getFloat(Context context, String key) {
        float value = 0;
        try {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            value = sharedPreferences.getFloat(key, 0);

        } catch (Exception ignored) {

        }

        return value;
    }

    public static int getInt(Context context, String key) {
        int value = 0;
        try {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            value = sharedPreferences.getInt(key, -1);

        } catch (Exception ignored) {

        }

        return value;
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    private static boolean getBoolean(Context context, String key, boolean defaultValue) {
        boolean value = false;
        try {

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            value = sharedPreferences.getBoolean(key, defaultValue);

        } catch (Exception ignored) {

            Log.e("Ramzy", "AppPreferences getBoolean error = " + ignored.getMessage());
        }

        return value;
    }

    public static boolean removeKey(Context context, String key) {
        boolean isClear;
        try {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
            isClear = true;

        } catch (Exception e) {
            isClear = false;
        }

        return isClear;
    }

    public static boolean clearSettings(Context context) {
        boolean isClear;
        String currentLanguage = AppPreferences.getString(context, "language");
        try {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            isClear = true;

        } catch (Exception e) {
            isClear = false;
        }

        //Set default language
        if (currentLanguage.equals("0")) {
            currentLanguage = Locale.getDefault().getLanguage();
        }

        AppPreferences.saveString(context, "language", currentLanguage);
        return isClear;
    }

    public static void clearLangCurrent(Context context) {
        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = mySPrefs.edit();
        editor.remove("position");
        editor.apply();
    }

    public static void clearAll(Context context) {
        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = mySPrefs.edit();
        editor.clear();
        editor.apply();
    }

    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getAll();
    }
}
