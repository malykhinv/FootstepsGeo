package com.malykhinv.footstepsgeo.mvp.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.MainCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainModel {

    private static final int CURRENT_GOOGLE_USER_ID_INDEX = 0;
    private static final int CURRENT_GOOGLE_USER_USERNAME_INDEX = 1;
    private static final int PERSONAL_CODE_LENGTH = 6;
    private static final int IMAGE_COUNT = 121;
    private static final String ALPHABET09 = "ABCDEFGHIKLMNOPQRSTVXYZ0123456789";
    private final String TAG = this.getClass().getName();
    private final Context context = App.getAppComponent().getContext();
    private final DatabaseReference usersReference = App.getAppComponent().getDbUsersReference();
    private final LocationManager locationManager = App.getAppComponent().getLocationManager();
    private LocationListener locationListener;
    private String[] currentGoogleUserInfo;
    private User user;
    private User currentUser;
    private DbCallback dbCallback;
    private MapCallback mapCallback;


    // Callbacks:

    public interface DbCallback extends MainCallback {
        void onUserReceived(User user);
        void onNullUserReceived(String userId);
        void onCurrentUserWasWrittenIntoDatabase(User user);
    }

    public interface MapCallback extends MainCallback {
        void onLocationChanged(Location location);
    }

    public void registerDbCallback(DbCallback dbCallback) {
        this.dbCallback = dbCallback;
    }

    public void registerMapCallback(MapCallback mapCallback) {
        this.mapCallback = mapCallback;
    }


    // Google account info:

    public void setCurrentGoogleUserInfo(String userId, String userName) {
        currentGoogleUserInfo = new String[] {userId, userName};
    }

    public String getCurrentGoogleUserId() {
        return currentGoogleUserInfo[CURRENT_GOOGLE_USER_ID_INDEX];
    }

    public String getCurrentGoogleUserName() {
        return currentGoogleUserInfo[CURRENT_GOOGLE_USER_USERNAME_INDEX];
    }

    // Firebase:

    public void findUserById(String userId) {
        Log.d(TAG, "findUserById: " + userId);
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    Log.d(TAG, "findUserById: success. " + userId);
                    user = snapshot.getValue(User.class);
                    if (dbCallback != null) {
                        dbCallback.onUserReceived(user);
                    }
                } else {
                    Log.d(TAG, "findUserById: failure");
                    user = null;
                    if (dbCallback != null) {
                        dbCallback.onNullUserReceived(userId);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "findUserById: failure. " + error.getMessage());
                user = null;
                if (dbCallback != null) {
                    dbCallback.onError(error.toException());
                }
            }
        });
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User createNewUser() {
        String userId = getCurrentGoogleUserId();
        String userName = getCurrentGoogleUserName();
        String personalCode = generateNewPersonalCode();
        int imageNumber = generateNewImageNumber();
        int batteryLevel = 0;

        User newUser = new User(userId, userName, personalCode, imageNumber, null, null, System.currentTimeMillis(), batteryLevel);

        Log.d(TAG, "createNewUser: " + userName + ", " + personalCode);
        return newUser;
    }

    private String generateNewPersonalCode() {
        char[] randomCode = new char[PERSONAL_CODE_LENGTH];
        String alphabet09 = ALPHABET09;
        Random random = new Random();
        for (int i = 0; i < randomCode.length; i++){
            randomCode[i] = alphabet09.charAt(random.nextInt(alphabet09.length()));
        }
        Log.d(TAG, "generateNewPersonalCode: " + Arrays.toString(randomCode));
        return new String(randomCode);
    }

    private int generateNewImageNumber() {
        Random random = new Random();
        return random.nextInt(IMAGE_COUNT);
    }

    public void writeCurrentUserIntoDatabase(User user) {
        if (user != null) {
            Log.d(TAG, "writeUserIntoDatabase: " + user.id);
            usersReference.child(user.id).setValue(user);
            if (dbCallback != null) {
                dbCallback.onCurrentUserWasWrittenIntoDatabase(user);
            }
        }
    }

    public ArrayList<String> getFriendsList(String userId) {
        ArrayList<String> friendList = new ArrayList<>();
        // TODO
        Log.d(TAG, "getFriendList: " + friendList);
        return friendList;
    }


    // Maps:

    public void initializeLocationListener() {
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
                    if (mapCallback != null) {
                        mapCallback.onError(e);
                    }
                }
            }

            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude() +", " + location.getLatitude());
                if (mapCallback != null) {
                    mapCallback.onLocationChanged(location);
                }
            }
        };
    }

    public void trackDeviceLocation() {
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

    public void setCurrentUserLocation(Location location) {
        if (currentUser != null) {
            currentUser.location = location;
        }
    }

    public void setCurrentUserLastLocationTime(long lastLocationTime) {
        if (currentUser != null) {
            currentUser.lastLocationTime = lastLocationTime;
        }
    }

    public void setCurrentUserBatteryLevel(int batteryLevel) {
        if (currentUser != null) {
            currentUser.batteryLevel = batteryLevel;
        }
    }

    public void writeUserInfoIntoDatabase(User user) {
        try {
            usersReference.child(user.id).setValue(user);
            Log.d(TAG, "writeUserInfoIntoDatabase: success");
        } catch (Exception e) {
            if (dbCallback != null) {
                dbCallback.onError(e);
            }
            Log.w(TAG, "writeUserInfoIntoDatabase: failure" + e.getMessage());
        }
    }
}
