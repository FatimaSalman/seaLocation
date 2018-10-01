package com.apps.fatima.sealocation.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.InetAddress;


public class InternetConnectionUtils {

    @SuppressWarnings("deprecation")
    private static boolean isNetworkConnected(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI)
                haveConnectedWifi = true;
            if (ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_MOBILE)
                haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void isInternetAvailable(Context context, final InternetAvailableCallback callback) {
        if (isNetworkConnected(context)) {
            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        InetAddress ipAddress = InetAddress.getByName("www.google.com");

                        if (ipAddress == null) {

                            callback.onInternetAvailable(false);

                        } else {
                            callback.onInternetAvailable(true);
                        }

                    } catch (final Exception e) {

                        callback.onInternetAvailable(false);
                        Log.i("InternetConnectionUtils", "Exception = " + e.getMessage());
                    }

                }
            }).start();
        } else {
            callback.onInternetAvailable(false);
        }

    }
}
