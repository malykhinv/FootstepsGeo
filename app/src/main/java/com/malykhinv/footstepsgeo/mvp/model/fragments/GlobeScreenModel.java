package com.malykhinv.footstepsgeo.mvp.model.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;

import java.util.List;

public class GlobeScreenModel extends MainModel {

    private final String TAG = this.getClass().getName();
    private final Context context = App.getAppComponent().getContext();
    private final DatabaseReference usersReference = App.getAppComponent().getDbUsersReference();
    private final LocationManager locationManager = App.getAppComponent().getLocationManager();
    private final LocationListener locationListener;
    private Callback callback;

    public GlobeScreenModel() {
        locationListener = new LocationListener() {

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                Log.d(TAG, "onProviderEnabled: " + provider);
                getMostAccurateLocation();
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.d(TAG, "onProviderDisabled: " + provider);
                try {
                    getMostAccurateLocation();
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            }

            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude() +", " + location.getLatitude());
                if (callback != null) {
                    callback.onLocationChanged(location);
                }
            }
        };
    }

    public interface Callback {

        void onLocationChanged(Location location);
        void onError(Exception e);
    }

    public void registerCallback(Callback callback) {
        this.callback = callback;
    }

    public void trackLocation() {
        getMostAccurateLocation();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);
        Log.d(TAG, "trackLocation: requested");
    }

    public Location getMostAccurateLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location mostAccurateLocation = null;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (mostAccurateLocation == null || location.getAccuracy() < mostAccurateLocation.getAccuracy()) {
                    mostAccurateLocation = location;
                }
            }
        }
        if (mostAccurateLocation != null) {
            Log.d(TAG, "getMostAccurateLocation: " + mostAccurateLocation.getProvider());
        }
        return mostAccurateLocation;
    }

    public void writeUserInfoIntoDatabase(User user) {
        try {
            usersReference.child(user.id).setValue(user);
            Log.d(TAG, "writeUserInfoIntoDatabase: success");
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(e);
            }
            Log.w(TAG, "writeUserInfoIntoDatabase: failure" + e.getMessage());
        }
    }
}
