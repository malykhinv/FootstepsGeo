package com.malykhinv.footstepsgeo.mvp.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.FragmentGlobeScreenBinding;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.GlobeScreenPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobeScreenFragment extends Fragment {

    private static final List<String> REQUIRED_PERMISSIONS = Stream.of(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .collect(Collectors.toList());
    private static final int ZOOM_MIDDLE = 15;
    private static final float ZOOM_MAX = 25;
    private final Context context = App.getAppComponent().getContext();
    private FragmentGlobeScreenBinding b;
    private GlobeScreenPresenter presenter;
    private GoogleMap googleMap;
    private Marker markerMe;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentGlobeScreenBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        b.mapView.onCreate(savedInstanceState);
        b.mapView.onResume();

        presenter = new GlobeScreenPresenter(this);
        presenter.onViewCreated();

    }

    public boolean areAllPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> getMissingPermissions() {
        ArrayList<String> missingPermissions = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

    public void askForPermissions(String[] permissions, int requestCode) {
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        presenter.onRequestPermissionsResult(permissions, grantResults);
    }

    public void getMap() {
        b.mapView.getMapAsync(presenter);
    }

    public void attachMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setMapStyle() {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));
    }

    public void setMapUiSettings() {
        googleMap.setMaxZoomPreference(ZOOM_MAX);
        googleMap.setPadding((int) context.getResources().getDimension(R.dimen.padding_horizontal), 0, 0, (int) context.getResources().getDimension(R.dimen.padding_big));
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
    }

    public void createCurrentUserPointer(Location location) {
        if (location != null) {
            markerMe = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("title")
                    .visible(true)
            );
        }
    }

    public void animateCamera(User userToFollow) {
        if (userToFollow.location != null) {
            Location location = userToFollow.location;
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_MIDDLE));
        }
    }

    public void moveCamera(Location location) {
        if (location != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_MIDDLE));
        }
    }

    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}