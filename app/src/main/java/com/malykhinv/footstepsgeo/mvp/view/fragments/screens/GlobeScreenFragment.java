package com.malykhinv.footstepsgeo.mvp.view.fragments.screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.FragmentGlobeScreenBinding;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.GlobeScreenPresenter;
import com.malykhinv.footstepsgeo.mvp.view.fragments.FriendsScrollHorizontalFragment;
import com.malykhinv.footstepsgeo.mvp.view.fragments.UserDetailsFragment;
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
    private static final float TRANSPARENCY_LEVEL_OPAQUE = 1.0f;
    private static final float TRANSPARENCY_LEVEL_TRANSPARENT = 0.5f;
    private final Context context = App.getAppComponent().getContext();
    private FragmentManager fragmentManager;
    private View view;
    private FragmentGlobeScreenBinding binding;
    private GoogleMap googleMap;
    private HashMap<String, Marker> markers = new HashMap<>();
    private boolean isFollowing = false;
    private GlobeScreenPresenter presenter;
    public FriendsScrollHorizontalFragment friendsScrollHorizontalFragment;
    public UserDetailsFragment userDetailsFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentGlobeScreenBinding.inflate(inflater, container, false);
        }

        binding.fabMyLocation.setOnClickListener(v -> presenter.onFabWasPressed());

        if (view == null) {
            view = binding.getRoot();
        } else if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (presenter == null) {
            presenter = new GlobeScreenPresenter(this);
            binding.mapView.onCreate(savedInstanceState);
        }

        binding.mapView.onResume();
        presenter.onViewCreated();
    }


    // Fragments

    public void initializeFragments() {
        fragmentManager = getChildFragmentManager();
        if (friendsScrollHorizontalFragment == null) {
            friendsScrollHorizontalFragment = new FriendsScrollHorizontalFragment();
        }
        if (userDetailsFragment == null) {
            userDetailsFragment = new UserDetailsFragment();
        }

        if (isFollowing) {
            showFragment(userDetailsFragment);
        } else {
            showFragment(friendsScrollHorizontalFragment);
        }
    }

    public void showFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(binding.groupSlidePanel.layoutFragmentContainer.getId(), fragment).commit();
    }


    // Permissions

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


    // Map

    public void getMap() {
        if (googleMap == null) {
            binding.mapView.getMapAsync(presenter);
        }
    }

    public void attachMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setMapStyle() {
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @SuppressLint("CheckResult")
    public void loadUserImage(User user) {
        try {
            RequestOptions options = new RequestOptions();
            options.circleCrop();

            Glide.with(this)
                    .asBitmap()
                    .apply(options)
                    .load(user.imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            presenter.onUserImageWasLoaded(user, resource);
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMarker(User user, Bitmap userImage) {
        String userId = user.id;
        Double latitude = user.position.get(POSITION_LAT_INDEX);
        Double longitude = user.position.get(POSITION_LNG_INDEX);

        Bitmap scaledUserImage = Bitmap.createScaledBitmap(userImage, (int) getResources().getDimension(R.dimen.size_userpic_small), (int) getResources().getDimension(R.dimen.size_userpic_small), false);

        if (googleMap != null) {
            Marker userMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(scaledUserImage))
                    .anchor(0.5f, 1)
                    .title(user.name)
                    .visible(true));
            markers.put(userId, userMarker);
        }
    }

    public boolean isMarkerExists(String id) {
        return markers.containsKey(id);
    }

    public void moveUserMarker(User user) {
        Marker marker = markers.get(user.id);
        if (marker != null) {
            Double latitude = user.position.get(POSITION_LAT_INDEX);
            Double longitude = user.position.get(POSITION_LNG_INDEX);
            marker.setPosition(new LatLng(latitude, longitude));
        }
    }

    public void animateCamera(ArrayList<Double> position) {
        try {
            Double latitude = position.get(POSITION_LAT_INDEX);
            Double longitude = position.get(POSITION_LNG_INDEX);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_MIDDLE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveCamera(ArrayList<Double> position) {
        try {
            Double latitude = position.get(POSITION_LAT_INDEX);
            Double longitude = position.get(POSITION_LNG_INDEX);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_MIDDLE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setMarkerTransparent(String id) {
        Marker marker = markers.get(id);
        if (marker != null) {
            marker.setAlpha(TRANSPARENCY_LEVEL_TRANSPARENT);
        }
    }

    public void setMarkerOpaque(String id) {
        Marker marker = markers.get(id);
        if (marker != null) {
            marker.setAlpha(TRANSPARENCY_LEVEL_OPAQUE);
        }
    }
}