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

public class GlobeScreenPresenter implements OnMapReadyCallback, MainModel.GlobeCallback {

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
        model.registerGlobeCallback(this);
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

    private void checkPermissions() {
        Log.d(TAG, "checkPermissions: ");
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
            initializeCurrentUserOnMap(currentUser);
            model.trackDeviceLocation();
        }
    }

    public void onFabWasPressed() {
        userToFollow = model.getCurrentUser();
        if (userToFollow != null && userToFollow.position != null) {
            isCameraFollows = true;
            view.animateCamera(userToFollow.position);
        }
    }


    // Call from Model:

    @Override
    public void onCurrentUserReceived(User user) {
        if (user != null) {
            currentGoogleUserId = model.getCurrentGoogleUserId();
            if (user.id.equals(currentGoogleUserId)) {
                initializeCurrentUserOnMap(user);
            }
        }
    }

    private void initializeCurrentUserOnMap(User user) {
        if (view.areAllPermissionsGranted()) {
            Location location = model.getMostAccurateLocation();
            if (location != null) {
                user.position = new Double[] {location.getLatitude(), location.getLongitude()};
                placeUserOnMap(user);
            }
        } else {
            checkPermissions();
        }
    }

    private void placeUserOnMap(User user) {
        if (user != null) {
            if (user.position != null && !view.isMarkerExists(user.id)) {
                view.createUserMarker(user);
                if (user.id.equals(model.getCurrentGoogleUserId())) {
                    view.moveCamera(user.position);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Double[] position = new Double[] {location.getLatitude(), location.getLongitude()};
            model.updateCurrentUserState(position);
            User currentUser = model.getCurrentUser();
            if (currentUser != null) {
                model.writeCurrentUserIntoDb();
                view.moveUserMarker(currentUser);
            }

            if (userToFollow != null && userToFollow.id.equals(currentGoogleUserId) && isCameraFollows) {
                view.animateCamera(position);
            }
        }
    }

    @Override
    public void onCurrentUserWasWrittenIntoDatabase(User user) {
        initializeCurrentUserOnMap(user);
    }

    @Override
    public void onFriendUserReceived(User user) {
        placeUserOnMap(user);
    }

    @Override
    public void onError(Exception e) {
        view.showMessage(e.getMessage());
    }
}
