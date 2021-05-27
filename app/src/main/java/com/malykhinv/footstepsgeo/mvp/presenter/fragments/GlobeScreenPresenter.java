package com.malykhinv.footstepsgeo.mvp.presenter.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.fragments.GlobeScreenModel;
import com.malykhinv.footstepsgeo.mvp.view.fragments.GlobeScreenFragment;

import java.util.ArrayList;

public class GlobeScreenPresenter implements OnMapReadyCallback, GlobeScreenModel.Callback {

    private final String TAG = this.getClass().getName();
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private final Context context = App.getAppComponent().getContext();
    private GlobeScreenFragment view;
    private GlobeScreenModel model;
    private User user;
    private boolean isCameraFollows = false;
    private User userToFollow;

    public GlobeScreenPresenter(GlobeScreenFragment view) {
        this.view = view;
        this.model = new GlobeScreenModel();
        model.registerCallback(this);
    }


    // Call from View:

    public void onViewCreated() {
        Log.d(TAG, "onViewCreated: ");
        view.getMap();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: " + googleMap);
        view.attachMap(googleMap);
        view.setMapStyle();
        view.setMapUiSettings();
        if (view.areAllPermissionsGranted()) {
            putUserOnMap();
        } else {
            checkPermissions();
        }
    }

    private void putUserOnMap() {
        Location location = model.getMostAccurateLocation();
        if (location != null) {
            view.createCurrentUserPointer(location);
            view.moveCamera(location);
        }
        model.trackLocation();
    }

    private void checkPermissions() {
        ArrayList<String> missingPermissions = view.getMissingPermissions();
        if (missingPermissions.size() > 0) {
            String[] missingPermissionsAsStringArray = new String[missingPermissions.size()];
            view.askForPermissions(missingPermissions.toArray(missingPermissionsAsStringArray), PERMISSIONS_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                view.showMessage(String.format(context.getString(R.string.permission_is_needed), permissions[i]));
            }
        }
        if (view.areAllPermissionsGranted()) {
            putUserOnMap();
        }
    }


    // Call from Model:

    @Override
    public void onLocationChanged(Location location) {
        if (userToFollow != null) {
            if (userToFollow == user) {
                updateCurrentUserInfo(location);
                model.writeUserInfoIntoDatabase(user);
            }
            if (isCameraFollows) {
                view.animateCamera(userToFollow);
            }
        }
    }

    private void updateCurrentUserInfo(Location location) {
        if (user != null && location != null) {
            user.location = location;
            user.lastLocationTime = System.currentTimeMillis();
            user.batteryLevel = App.getAppComponent().getBatteryLevel();
        }
    }

    @Override
    public void onError(Exception e) {
        view.showMessage(e.getMessage());
    }
}
