package com.malykhinv.footstepsgeo.mvp.view;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.presenter.MainPresenter;
import com.malykhinv.footstepsgeo.mvp.view.fragments.AccountScreenFragment;
import com.malykhinv.footstepsgeo.mvp.view.fragments.GlobeScreenFragment;
import com.malykhinv.footstepsgeo.mvp.view.fragments.UserlistScreenFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private MainPresenter presenter;

    private static final int FADE_DURATION = 500;
    private static final int ZOOM_MIDDLE = 15;
    private static final float ZOOM_MAX = 25;
    private static final int REQUEST_CODE_FINE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_COARSE_LOCATION_PERMISSION = 2;
    private UserlistScreenFragment fragmentUserlistScreen;
    private GlobeScreenFragment fragmentGlobeScreen;
    private AccountScreenFragment fragmentAccountScreen;
    LocationManager locationManager;
    ClipboardManager clipboard;
    private String personalCode;
    private boolean cameraFollowsMe = false;
    MapView mapView;
    private GoogleMap worldMap;
    private Marker markerMe;
    private FloatingActionButton fabMyLocation;
    ImageButton ibLogout;
    ImageButton ibCopyPersonalCode;
    TextView textAccountNameGreeting;
    TextView textPersonalCode;
    ImageView imageUserPicturePreview;
    FloatingActionButton fabChangeImagePicture;
    ConstraintLayout layoutCall;
    BottomNavigationView menuBottomNavigation;
    String currentScreen;
    User userMe;

    private final BottomNavigationView.OnNavigationItemSelectedListener menuListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            switch (item.getItemId()){
                case R.id.menuItemUserlist:
                    showUserlistScreen();
                    break;
                case R.id.menuItemGlobe:
                    showGlobeScreen();
                    break;
                case R.id.menuItemAccount:
                    showAccountScreen();
                    break;
            }
            return true;
        }
    };
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
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            getMostAccurateLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {
            getMostAccurateLocation();
        }
    };

    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    public FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){
        userMe = null;
        database = FirebaseDatabase.getInstance();
        usersReference = database.getReference("users");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        fragmentGlobeScreen = new GlobeScreenFragment();
        fragmentUserlistScreen = new UserlistScreenFragment();
        fragmentAccountScreen = new AccountScreenFragment();

        menuBottomNavigation = findViewById(R.id.menuBottomNavigation);
        menuBottomNavigation.setOnNavigationItemSelectedListener(menuListener);

        showGlobeScreen();

        MainModel model = new MainModel();
        presenter = new MainPresenter(model);
        presenter.attachView(this);
        presenter.viewIsReady();
    }

    public void initGlobeScreen(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        fabMyLocation = (FloatingActionButton) fragmentGlobeScreen.getView().findViewById(R.id.fabMyLocation);
        fabMyLocation.setVisibility(View.GONE);
        fabMyLocation.setOnClickListener(this);
        menuBottomNavigation.getMenu().getItem(1).setChecked(true);

        if (userMe != null) updateUI(userMe);
    }

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

    public void showGlobeScreen(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layoutFragmentContainer, fragmentGlobeScreen).commit();
        currentScreen = "GlobeScreen";
    }

    public void showUserlistScreen(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layoutFragmentContainer, fragmentUserlistScreen).commit();
        currentScreen = "UserlistScreen";
    }

    public void showAccountScreen(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layoutFragmentContainer, fragmentAccountScreen).commit();
        currentScreen = "AccountScreen";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        worldMap = googleMap;
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));
        } catch (Resources.NotFoundException e) {
            Log.e("Exception: %s", e.getMessage());
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        }
        setMapUISettings();
        createMyPointer();
    }

    public void setMapUISettings() {
        worldMap.setMaxZoomPreference(ZOOM_MAX);
        worldMap.setPadding(48, 0, 0, 64);
        worldMap.getUiSettings().setScrollGesturesEnabled(true);
        worldMap.getUiSettings().setRotateGesturesEnabled(false);
        worldMap.getUiSettings().setMapToolbarEnabled(false);
        worldMap.getUiSettings().setMyLocationButtonEnabled(false);
        worldMap.getUiSettings().setCompassEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        worldMap.setMyLocationEnabled(true);
    }

    private void createMyPointer() {
        Location location = getMostAccurateLocation();
        if (location != null) {
            markerMe = worldMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("title")
                    .visible(true)
            );
            showFabMyLocation();
            jumpCameraToMyPointer();
            requestLocationUpdates();
        }
    }

    public void showFabMyLocation() {
        fabMyLocation.setAlpha(0f);
        fabMyLocation.setVisibility(View.VISIBLE);
        fabMyLocation.animate().alpha(1f).setDuration(FADE_DURATION);
    }

    private void jumpCameraToMyPointer() {
        Location location = getMostAccurateLocation();
        if (location != null) {
            markerMe.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            worldMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),ZOOM_MIDDLE));
        }
    }

    private void moveCameraToMyPointer() {
        Location location = getMostAccurateLocation();
        if (location != null) {
            markerMe.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            worldMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }

    private void zoomCameraToMyPointer() {
        Location location = getMostAccurateLocation();
        if (location != null) {
            markerMe.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            worldMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_MIDDLE));
        }
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

    public void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION_PERMISSION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_COARSE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createMyPointer();
                    cameraFollowsMe = true;
                    requestLocationUpdates();
                }
            case REQUEST_CODE_COARSE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createMyPointer();
                    cameraFollowsMe = true;
                    requestLocationUpdates();
                }
        }
    }

    public void showToast(int stringResource){
        Toast toast = Toast.makeText(getApplicationContext(),stringResource, Toast.LENGTH_SHORT);
        toast.show();
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

    public void loadCurrentFBUserInfo(){
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String userName = intent.getStringExtra("userName");
        if (userId != null){
            presenter.currentFBUserInfoIsLoaded(database, usersReference, userId, userName);
        }
    }

    public void updateUserData(User user){
        if (user != null) {
            userMe = user;
            updateUI(userMe);
        }
    }

    public void updateUI(User user){
        if (user != null) {
            switch (currentScreen) {
                case "GlobeScreen":

                    break;
                case "AccountScreen":
                    String text = getResources().getString(R.string.hi) + user.name;
                    textAccountNameGreeting.setText(text);
                    break;
                case "UserlistScreen":

                    break;
            }
        }
    }
}
