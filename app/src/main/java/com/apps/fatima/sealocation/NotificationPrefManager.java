package com.apps.fatima.sealocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class NotificationPrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "Notify";
    private static final String NOTIFY_COUNT = "Notify_Count";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public NotificationPrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public int getNotifyCount() {
        return pref.getInt(NOTIFY_COUNT, 0);
    }

    public void setNotifyCount(int count) {
        editor.putInt(NOTIFY_COUNT, count);
        editor.commit();
    }


}
