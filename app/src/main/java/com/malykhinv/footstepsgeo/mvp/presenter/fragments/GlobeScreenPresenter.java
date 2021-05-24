package com.malykhinv.footstepsgeo.mvp.presenter.fragments;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.fragments.GlobeScreenModel;
import com.malykhinv.footstepsgeo.mvp.view.fragments.GlobeScreenFragment;

import java.util.ArrayList;

public class GlobeScreenPresenter implements OnMapReadyCallback, GlobeScreenModel.Callback {

    private Context context = App.getAppComponent().getContext();
    private GlobeScreenFragment view;
    private GlobeScreenModel model;

    public GlobeScreenPresenter(GlobeScreenFragment view) {
        this.view = view;
        this.model = new GlobeScreenModel();
        model.registerCallback(this);
    }


    // Call from View:

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        view.attachMap(googleMap);
        view.setMapStyle();
        view.setMapUiSettings();
        checkPermissions();
    }

    private void checkPermissions() {
        ArrayList<String> missingPermissions = view.getMissingPermissions();
        if (missingPermissions.size() > 0) {
            for (int i = 0; i < missingPermissions.size(); i++) {
                view.askForPermissions(missingPermissions.get(i), i);
            }
        }
    }

    public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                view.showMessage(String.format(context.getString(R.string.permission_is_needed), permissions[i]));
            }
        }
    }


    // Call from Model:
}
