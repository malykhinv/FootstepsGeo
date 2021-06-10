package com.malykhinv.footstepsgeo.mvp.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobeScreenFragment extends Fragment {

    private static final List<String> REQUIRED_PERMISSIONS = Stream.of(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .collect(Collectors.toList());
    private static final int POSITION_LAT_INDEX = 0;
    private static final int POSITION_LNG_INDEX = 1;
    private static final int ZOOM_MIDDLE = 15;
    private static final float ZOOM_MAX = 25;
    private final Context context = App.getAppComponent().getContext();
    private View view;
    private FragmentGlobeScreenBinding b;
    private GlobeScreenPresenter presenter;
    private GoogleMap googleMap;
    private HashMap<String, Marker> markers = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentGlobeScreenBinding.inflate(inflater, container, false);

        b.fabMyLocation.setOnClickListener(v -> presenter.onFabWasPressed());

        if (view == null) {
            view = b.getRoot();
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        b.mapView.onCreate(savedInstanceState);
        b.mapView.onResume();

        if (presenter == null) {
            presenter = new GlobeScreenPresenter(this);
            presenter.onViewCreated();
        }
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

    public void askForPermissions(String[] permissions) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .requestEachCombined(permissions)
                .subscribe(permission -> {
                    if (presenter != null) {
                        presenter.onRequestPermissionsResult(permission.granted);
                    }
            });
    }

    public void getMap() {
        if (googleMap == null) {
            b.mapView.getMapAsync(presenter);
        }
    }

    public void attachMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setMapStyle() {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));
    }

    public void setMapUiSettings() {
        googleMap.setMaxZoomPreference(ZOOM_MAX);
        googleMap.setPadding((int) context.getResources().getDimension(R.dimen.padding_horizontal), 0, 0, (int) context.getResources().getDimension(R.dimen.padding_large));
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
    }

    public void createUserMarker(User user) {
        if (user != null && user.position != null && user.id != null) {
            String userId = user.id;
            Double latitude = user.position.get(POSITION_LAT_INDEX);
            Double longitude = user.position.get(POSITION_LNG_INDEX);

            if (googleMap != null) {
                Marker userMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(user.id)
                        .visible(true));
                markers.put(userId, userMarker);
            }
        }
    }

    public void moveUserMarker(User user) {
        if (user != null && user.id != null & user.position != null) {
            Marker marker = markers.get(user.id);
            if (marker != null) {
                Double latitude = user.position.get(POSITION_LAT_INDEX);
                Double longitude = user.position.get(POSITION_LNG_INDEX);
                marker.setPosition(new LatLng(latitude, longitude));
            }
        }
    }

    public void animateCamera(ArrayList<Double> position) {
        if (position != null && googleMap != null) {
            Double latitude = position.get(POSITION_LAT_INDEX);
            Double longitude = position.get(POSITION_LNG_INDEX);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_MIDDLE));
        }
    }

    public void moveCamera(ArrayList<Double> position) {
        if (position != null && googleMap != null) {
            Double latitude = position.get(POSITION_LAT_INDEX);
            Double longitude = position.get(POSITION_LNG_INDEX);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_MIDDLE));
        }
    }

    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isMarkerExists(String id) {
        return markers.containsKey(id);
    }
}