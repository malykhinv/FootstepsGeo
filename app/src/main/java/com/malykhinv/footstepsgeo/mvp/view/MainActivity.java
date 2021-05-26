package com.malykhinv.footstepsgeo.mvp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.ActivityMainBinding;
import com.malykhinv.footstepsgeo.mvp.presenter.MainPresenter;
import com.malykhinv.footstepsgeo.mvp.view.fragments.AccountScreenFragment;
import com.malykhinv.footstepsgeo.mvp.view.fragments.GlobeScreenFragment;
import com.malykhinv.footstepsgeo.mvp.view.fragments.UserlistScreenFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int INITIAL_MENU_ITEM_INDEX = 1;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private ActivityMainBinding b;
    private MainPresenter presenter;

    private UserlistScreenFragment fragmentUserlistScreen;
    private GlobeScreenFragment fragmentGlobeScreen;
    private AccountScreenFragment fragmentAccountScreen;
    LocationManager locationManager;
    ClipboardManager clipboard;
    private boolean cameraFollowsMe = false;
    private GoogleMap worldMap;
    private Marker markerMe;
    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind();
        initializeFragments();
        initializeBottomNavigation();
        presenter = new MainPresenter(this);
        presenter.onViewIsReady();
    }

    private void bind() {
        b = ActivityMainBinding.inflate(getLayoutInflater());
        View view = b.getRoot();
        setContentView(view);
    }

    private void initializeFragments() {
        fragmentUserlistScreen = new UserlistScreenFragment();
        fragmentGlobeScreen = new GlobeScreenFragment();
        fragmentAccountScreen = new AccountScreenFragment();
    }

    @SuppressLint("NonConstantResourceId")
    private void initializeBottomNavigation() {
        b.menuBottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuItemUserlist: {
                    showFragment(fragmentUserlistScreen);
                    return true;
                }
                case R.id.menuItemGlobe: {
                    showFragment(fragmentGlobeScreen);
                    return true;
                }
                case R.id.menuItemAccount: {
                    showFragment(fragmentAccountScreen);
                    return true;
                }
            }
            return false;
        });
        b.menuBottomNavigation.setSelectedItemId(b.menuBottomNavigation.getMenu().getItem(INITIAL_MENU_ITEM_INDEX).getItemId());
    }

    private void showFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(b.layoutFragmentContainer.getId(), fragment).commit();
    }

    public void loadCurrentGoogleUserInfo() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String userName = intent.getStringExtra("userName");
        if (userId != null){
            presenter.onCurrentGoogleUserInfoWasLoaded(userId, userName);
        }
    }

    public void updateUserData(User user){
        if (user != null) {
            this.user = user;
        }
    }












    final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                if (cameraFollowsMe) {
                    moveCameraToMyPointer();
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {
            getMostAccurateLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {
            getMostAccurateLocation();
        }
    };






//    public void initGlobeScreen(Bundle savedInstanceState) {
//        mapView = (MapView) findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.onResume();
//        mapView.getMapAsync(this);
//
//        fabMyLocation = (FloatingActionButton) fragmentGlobeScreen.getView().findViewById(R.id.fabMyLocation);
//        fabMyLocation.setVisibility(View.GONE);
//        fabMyLocation.setOnClickListener(this);
//        menuBottomNavigation.getMenu().getItem(1).setChecked(true);
//
//        if (userMe != null) updateUI(userMe);
//    }

    public void initAccountScreen(){

//        personalCode = "P6NYRA3E";
//        textPersonalCode.setText(personalCode);
//
//        ibLogout.setOnClickListener(this);
//
//        ibCopyPersonalCode.setOnClickListener(this);
//
//        fabChangeImagePicture.setOnClickListener(this);
//
//        layoutCall.setOnClickListener(this);
//
//        if (userMe != null) updateUI(userMe);
    }







    public void showFabMyLocation() {
//        fabMyLocation.setAlpha(0f);
//        fabMyLocation.setVisibility(View.VISIBLE);
//        fabMyLocation.animate().alpha(1f).setDuration(FADE_DURATION);
    }

    private void jumpCameraToMyPointer() {
//        Location location = getMostAccurateLocation();
//        if (location != null) {
//            markerMe.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
//            worldMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),ZOOM_MIDDLE));
//        }
    }

    private void moveCameraToMyPointer() {
//        Location location = getMostAccurateLocation();
//        if (location != null) {
//            markerMe.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
//            worldMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
//        }
    }

    private void zoomCameraToMyPointer() {
//        Location location = getMostAccurateLocation();
//        if (location != null) {
//            markerMe.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
//            worldMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_MIDDLE));
//        }
    }

    private Location getMostAccurateLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location mostAccurateLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (mostAccurateLocation == null || l.getAccuracy() < mostAccurateLocation.getAccuracy()) {
                    mostAccurateLocation = l;
                }
            }
        }
        return mostAccurateLocation;
    }

    private void requestLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1, 1, listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, listener);
    }



    public void shareViaSocialMedia(String textToShare){
        Intent shareViaSocialMediaIntent = new Intent(Intent.ACTION_SEND);
        shareViaSocialMediaIntent.setType("text/plain");
        shareViaSocialMediaIntent.putExtra(Intent.EXTRA_TEXT,textToShare);
        startActivity(Intent.createChooser(shareViaSocialMediaIntent,getString(R.string.share_via)));
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.fabMyLocation:
//                zoomCameraToMyPointer();
//                cameraFollowsMe = true;
//                break;
//            case R.id.imageButtonLogout:
//                break;
//            case R.id.fabChangeImagePicture:
//                break;
//            case R.id.imageButtonCopyPersonalCode:
//                ClipData clip = ClipData.newPlainText("PersonalCode", textPersonalCode.getText());
//                clipboard.setPrimaryClip(clip);
//                showToast(R.string.your_personal_code_copied_to_clipboard);
//                break;
//            case R.id.layoutSharePersonalCode:
//                String textToShare = getString(R.string.my_code_in_app_use_it_to_find_me,getString(R.string.app_name),personalCode);
//                shareViaSocialMedia(textToShare);
//                break;
//        }
    }



    @Override
    public void onBackPressed() {
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
