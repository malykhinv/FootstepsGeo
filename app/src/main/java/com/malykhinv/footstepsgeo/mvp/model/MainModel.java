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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainModel {

    private static final int CURRENT_GOOGLE_USER_ID_INDEX = 0;
    private static final int CURRENT_GOOGLE_USER_USERNAME_INDEX = 1;
    private static final int PERSONAL_CODE_LENGTH = 6;
    private static final int IMAGE_COUNT = 121;
    private static final String ALPHABET09 = "ABCDEFGHIKLMNOPQRSTVXYZ0123456789";
    private final String TAG = this.getClass().getName();
    private final Context context = App.getAppComponent().getContext();
    private final DatabaseReference usersReference = App.getAppComponent().getDbUsersReference();
    private final DatabaseReference personalCodesReference = App.getAppComponent().getDbPersonalCodesReference();
    private final LocationManager locationManager = App.getAppComponent().getLocationManager();
    private LocationListener locationListener;
    private String[] currentGoogleUserInfo;
    private User user;
    private User currentUser;
    private HashMap<String, User> friends;
    private MainCallback mainCallback;
    private UserlistCallback userlistCallback;
    private GlobeCallback globeCallback;
    private Disposable disposable;


    // Callbacks:

    public interface MainCallback {
        void onCurrentUserReceived(User user);
        void onNullCurrentUserReceived();
        void onCurrentUserWasWrittenIntoDatabase(User user);
    }

    public interface UserlistCallback {
        void onCurrentUserReceived(User user);
        void onFriendUserReceived(User user);
    }

    public interface GlobeCallback {
        void onCurrentUserReceived(User user);
        void onCurrentUserWasWrittenIntoDatabase(User user);
        void onFriendUserReceived(User user);
        void onLocationChanged(Location location);
        void onError(Exception e);
    }

    public void registerMainCallback(MainCallback mainCallback) {
        this.mainCallback = mainCallback;
    }

    public void registerUserlistCallback(UserlistCallback userlistCallback) {
        this.userlistCallback = userlistCallback;
    }

    public void registerGlobeCallback(GlobeCallback globeCallback) {
        this.globeCallback = globeCallback;
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


    // Firebase: current user

    public void loadCurrentUserFromDb() {
        Log.d(TAG, "getCurrentUserFromDb: " + getCurrentGoogleUserId());
        usersReference.child(getCurrentGoogleUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    user = snapshot.getValue(User.class);
                    if (mainCallback != null) {
                        mainCallback.onCurrentUserReceived(user);
                    }
                    if (userlistCallback != null) {
                        userlistCallback.onCurrentUserReceived(user);
                    }
                    if (globeCallback != null) {
                        globeCallback.onCurrentUserReceived(user);
                    }
                } else {
                    Log.d(TAG, "getCurrentUserFromDb: failure");
                    user = null;
                    if (mainCallback != null) {
                        mainCallback.onNullCurrentUserReceived();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createNewUser() {
        String userId = getCurrentGoogleUserId();
        String userName = getCurrentGoogleUserName();
        String personalCode = generateNewPersonalCode();
        int imageNumber = generateNewImageNumber();
        LatLng latLng = null;
        String phoneNumber = null;
        int batteryLevel = App.getAppComponent().getBatteryLevel();
        ArrayList<String> friendsIds = null;

        User newUser = new User(userId, userName, personalCode, imageNumber, latLng, phoneNumber, System.currentTimeMillis(), batteryLevel, friendsIds);
        usersReference.child(userId).setValue(newUser);

        Log.d(TAG, "createNewUser: " + userName + ", " + personalCode);
        if (mainCallback != null) {
            mainCallback.onCurrentUserWasWrittenIntoDatabase(newUser);
        }
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

    public void writeCurrentUserIntoDb() {
        if (currentUser != null) {
            Log.d(TAG, "writeUserIntoDatabase: " + user.id);
            usersReference.child(user.id).setValue(user);
        }
    }

    public void updateCurrentUserState(LatLng latLng) {
        if (currentUser != null) {
            if (latLng != null) {
                currentUser.latLng = latLng;
            }
            currentUser.lastLocationTime = System.currentTimeMillis();
            currentUser.batteryLevel = App.getAppComponent().getBatteryLevel();
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }


    // Firebase: friends

    public void loadIdFromDb(String personalCode) {
        Log.d(TAG, "loadIdFromDb: " + personalCode);
        personalCodesReference.child(personalCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    Log.d(TAG, "loadIdFromDb: success. " + personalCode);
                    String friendsId = snapshot.getValue(String.class);
//                    if (userlistCallback != null) {
//                        userlistCallback.onFriendsIdReceived(friendsId);
//                    }
                } else {
                    Log.d(TAG, "loadIdFromDb: failure");
                    user = null;
//                    if (userlistCallback != null) {
//                        userlistCallback.onNullFriendsIdReceived();
//                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadUserFromDb(String id) {
        Log.d(TAG, "loadUserFromDb: " + id);
        usersReference.child(getCurrentGoogleUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {

                    if (friends.containsKey(id)) {
                        friends.replace(id, snapshot.getValue(User.class));
                    } else {
                        friends.put(id, snapshot.getValue(User.class));
                    }

                    if (userlistCallback != null) {
                        userlistCallback.onFriendUserReceived(friends.get(id));
                    }
                    if (globeCallback != null) {
                        globeCallback.onFriendUserReceived(friends.get(id));
                    }

                } else {
                    Log.d(TAG, "loadUserFromDb: failure");
                    user = null;
                    if (mainCallback != null) {
                        mainCallback.onNullCurrentUserReceived();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addFriend(String id) {
        if (currentUser != null && !id.equals(getCurrentGoogleUserId()) && !currentUser.friendsIds.contains(id)) {
            currentUser.friendsIds.add(id);
            writeCurrentUserIntoDb();
            loadUserFromDb(id);
        }
    }

    public void trackFriends() {
        Observer<Long> friendsObserver = new Observer<Long>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Long tick) {
                if (currentUser != null) {
                    ArrayList<String> friendsIds = currentUser.friendsIds;
                    if (friendsIds != null) {
                        for (String id : friendsIds) {
                            loadUserFromDb(id);
                        }
                    }
                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        Observable<Long> friendsOnTimer = Observable.timer(2000, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        friendsOnTimer.subscribe(friendsObserver);
    }

    public void dispose() {
        disposable.dispose();
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
                    if (globeCallback != null) {
                        globeCallback.onError(e);
                    }
                }
            }

            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLatitude() +", " + location.getLatitude());
                if (globeCallback != null) {
                    globeCallback.onLocationChanged(location);
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
}
