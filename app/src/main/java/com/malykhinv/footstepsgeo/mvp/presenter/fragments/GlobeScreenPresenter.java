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
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.fragments.GlobeScreenFragment;

import java.util.ArrayList;

public class GlobeScreenPresenter implements OnMapReadyCallback, MainModel.DbCallback, MainModel.MapCallback {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private final String TAG = this.getClass().getName();
    private final Context context = App.getAppComponent().getContext();
    private final GlobeScreenFragment view;
    private final MainModel model;
    private String currentGoogleUserId;
    private User userToFollow;
    private boolean isCameraFollows = false;

    public GlobeScreenPresenter(GlobeScreenFragment view) {
        this.view = view;
        this.model = App.getAppComponent().getMainModel();
        model.registerDbCallback(this);
        model.registerMapCallback(this);
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
        model.initializeLocationListener();
    }

    private void placeUserOnMap(User user) {
        if (user != null) {
            user.location = model.getMostAccurateLocation();
            if (user.location != null) {
                view.createUserMarker(user);
                view.moveCamera(user.location);
            }
            model.trackDeviceLocation();
        }
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
            User currentUser = model.getCurrentUser();
            placeUserOnMap(currentUser);
        }
    }

    public void onFabWasPressed() {
        userToFollow = model.getCurrentUser();
        if (userToFollow != null && userToFollow.location != null) {
            isCameraFollows = true;
            view.animateCamera(userToFollow.location);
        }
    }


    // Call from Model:

    @Override
    public void onCurrentUserWasWrittenIntoDatabase(User user) {
        initializeCurrentUserOnMap(user);
    }

    @Override
    public void onUserReceived(User user) {
        if (user != null) {
            currentGoogleUserId = model.getCurrentGoogleUserId();
            if (user.id.equals(currentGoogleUserId)) {
                initializeCurrentUserOnMap(user);
            } else {
                // TODO
            }
        }
    }

    private void initializeCurrentUserOnMap(User user) {
        model.setCurrentUser(user);

        if (view.areAllPermissionsGranted()) {
            placeUserOnMap(user);
        } else {
            checkPermissions();
        }
    }

    @Override
    public void onNullUserReceived(String userId) {
        User user = model.createNewUser();
        if (user != null) {
            model.writeCurrentUserIntoDatabase(user);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            view.moveUserMarker(userToFollow);

            model.setCurrentUserLocation(location);
            model.setCurrentUserLastLocationTime(System.currentTimeMillis());
            model.setCurrentUserBatteryLevel(App.getAppComponent().getBatteryLevel());
            model.writeUserInfoIntoDatabase(userToFollow);

            if (userToFollow != null && userToFollow.id.equals(currentGoogleUserId) && isCameraFollows) {
                view.animateCamera(location);
            }
        }
    }

    @Override
    public void onError(Exception e) {
        view.showMessage(e.getMessage());
    }
}
