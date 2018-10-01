package com.apps.fatima.sealocation.manager;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

public class AppLocationManager {

    private static Location currentLocation;

    private Context mContext;
    private Handler handler;
    private Timer timer;
    private LocationManager locationManager;
    private LocationResult locationResult;
    private boolean isGpsEnabled = false;
    private boolean isNetworkEnabled = false;

    private AppLocationManager() {
    }

    public static AppLocationManager getInstance() {
        return new AppLocationManager();
    }

    public void getLocation(Context context, LocationResult result) {

        this.mContext = context;

        handler = new Handler(Looper.getMainLooper());
        // I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (locationManager == null)
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }
        try {
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        // don't start listeners if no provider is enabled
        if (!isGpsEnabled && !isNetworkEnabled)

        {
            if (locationResult != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationResult.onError();
                    }
                });
            }
            return;
        }

        if (FontManager.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) && isGpsEnabled)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if (FontManager.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) && isNetworkEnabled)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListenerNetwork);
        timer = new Timer();
        timer.schedule(new GetLastLocation(), 3000);
    }

    private LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            currentLocation = location;
            if (locationResult != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationResult.gotLocation(currentLocation);
                    }
                });
            }
            if (FontManager.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerNetwork);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            currentLocation = location;
            if (locationResult != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationResult.gotLocation(currentLocation);
                    }
                });
            }
            if (FontManager.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerGps);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            if (FontManager.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                locationManager.removeUpdates(locationListenerGps);
                locationManager.removeUpdates(locationListenerNetwork);
            } else {
                return;
            }


            Location networkLocation = null, gpsLocation = null;
            if (isGpsEnabled)
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (isNetworkEnabled)
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // if there are both values use the latest one
            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation.getTime() > networkLocation.getTime()) {
                    currentLocation = gpsLocation;
                    if (locationResult != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                locationResult.gotLocation(currentLocation);
                            }
                        });
                    }
                    return;
                } else {
                    currentLocation = networkLocation;
                    if (locationResult != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                locationResult.gotLocation(currentLocation);
                            }
                        });
                    }
                    return;
                }
            } else if (gpsLocation != null) {
                currentLocation = gpsLocation;
                if (locationResult != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            locationResult.gotLocation(currentLocation);
                        }
                    });
                }
                return;
            } else if (networkLocation != null) {
                currentLocation = networkLocation;
                if (locationResult != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            locationResult.gotLocation(currentLocation);
                        }
                    });
                }
                return;
            }
            if (locationResult != null) {
                locationResult.gotLocation(null);
            }
        }
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);

        public abstract void onError();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}