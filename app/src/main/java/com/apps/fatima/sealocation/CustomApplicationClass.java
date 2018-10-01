package com.apps.fatima.sealocation;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.branch.referral.Branch;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public final class CustomApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Initialize the Branch object
        Branch.getAutoInstance(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}