package com.malykhinv.footstepsgeo.mvp.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final int CURRENT_GOOGLE_USER_IMAGEURL_INDEX = 2;
    private static final int PERSONAL_CODE_LENGTH = 6;
    private static final String ALPHABET09 = "ABCDEFGHIKLMNPQRSTVXYZ123456789";
    private final Context context = App.getAppComponent().getContext();
    private final DatabaseReference usersReference = App.getAppComponent().getDbUsersReference();
    private final DatabaseReference personalCodesReference = App.getAppComponent().getDbPersonalCodesReference();
    private final LocationManager locationManager = App.getAppComponent().getLocationManager();
    private LocationListener locationListener;
    private String[] currentGoogleUserInfo;
    private User currentUser;
    private HashMap<String, User> mapOfFriends = new HashMap<>(Collections.emptyMap());
    private MainCallback mainCallback;
    private FriendsCallback friendsCallback;
    private GlobeCallback globeCallback;
    private AccountCallback accountCallback;
    private Disposable disposable;


    // Callbacks:

    public interface MainCallback {
        void onCurrentUserReceived(User user);
        void onNullCurrentUserReceived();
        void onCurrentUserWasWrittenIntoDatabase(User user);
    }

    public interface FriendsCallback {
        void onFriendUserReceived(User user);
        void onFriendIdReceived(String friendsId);
        void onNullFriendIdReceived();
    }

    public interface GlobeCallback {
        void onCurrentUserReceived(User user);
        void onCurrentUserWasWrittenIntoDatabase(User user);
        void onFriendUserReceived(User user);
        void onLocationChanged(Location location);
        void onError(Exception e);
    }

    public interface AccountCallback {
        void onCurrentUserReceived(User user);
    }

    public void registerMainCallback(MainCallback mainCallback) {
        this.mainCallback = mainCallback;
    }

    public void registerUserlistCallback(FriendsCallback friendsCallback) {
        this.friendsCallback = friendsCallback;
    }

    public void registerGlobeCallback(GlobeCallback globeCallback) {
        this.globeCallback = globeCallback;
    }

    public void registerAccountCallback(AccountCallback accountCallback) {
        this.accountCallback = accountCallback;
    }


    // Google: account info

    public void setCurrentGoogleUserInfo(String userId, String userName, String imageUrl) {
        currentGoogleUserInfo = new String[] {userId, userName, imageUrl};
    }

    public String getCurrentGoogleUserId() {
        return currentGoogleUserInfo[CURRENT_GOOGLE_USER_ID_INDEX];
    }

    public String getCurrentGoogleUserName() {
        return currentGoogleUserInfo[CURRENT_GOOGLE_USER_USERNAME_INDEX];
    }

    public String getCurrentGoogleUserImageUrl() {
        return currentGoogleUserInfo[CURRENT_GOOGLE_USER_IMAGEURL_INDEX];
    }


    // Firebase: current user

    public void loadCurrentUserFromDb() {
        usersReference.child(getCurrentGoogleUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    currentUser = snapshot.getValue(User.class);
                    if (mainCallback != null) {
                        mainCallback.onCurrentUserReceived(currentUser);
                    }
                    if (globeCallback != null) {
                        globeCallback.onCurrentUserReceived(currentUser);
                    }
                    if (accountCallback != null) {
                        accountCallback.onCurrentUserReceived(currentUser);
                    }
                } else {
                    currentUser = null;
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
        String imageUrl = getCurrentGoogleUserImageUrl();
        ArrayList<Double> position = null;
        long millis = System.currentTimeMillis();
        int batteryLevel = App.getAppComponent().getBatteryLevel();
        ArrayList<String> friendsIds = null;

        // Create an user
        User newUser = new User(userId, userName, personalCode, imageUrl, position, millis, batteryLevel, friendsIds);
        usersReference.child(userId).setValue(newUser);

        // Create an entry to make it possible to search for users by their personal codes
        personalCodesReference.child(personalCode).setValue(userId);

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
        return new String(randomCode);
    }

    public void writeCurrentUserIntoDb() {
        try {
            usersReference.child(currentUser.id).setValue(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentUserState(ArrayList<Double> position) {
        try {
            if (position != null) {
                currentUser.position = position;
            }
            currentUser.lastLocationTime = System.currentTimeMillis();
            currentUser.batteryLevel = App.getAppComponent().getBatteryLevel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserPersonalCode() {
        return currentUser.personalCode;
    }

    public void clearCurrentUser() {
        currentUser = null;
    }


    // Firebase: friends

    public void loadIdFromDb(String personalCode) {
        personalCodesReference.child(personalCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    String friendsId = snapshot.getValue(String.class);
                    if (friendsCallback != null) {
                        friendsCallback.onFriendIdReceived(friendsId);
                    }
                } else {
                    if (friendsCallback != null) {
                        friendsCallback.onNullFriendIdReceived();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadFriendFromDb(String id) {
        usersReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {

                    if (!id.equals(getCurrentGoogleUserId())) {
                        if (mapOfFriends.containsKey(id)) {
                            mapOfFriends.replace(id, snapshot.getValue(User.class));
                        } else {
                            mapOfFriends.put(id, snapshot.getValue(User.class));

                            updateCurrentUserFriendsIds(mapOfFriends);
                        }

                        if (friendsCallback != null) {
                            friendsCallback.onFriendUserReceived(mapOfFriends.get(id));
                        }

                        if (globeCallback != null) {
                            globeCallback.onFriendUserReceived(mapOfFriends.get(id));
                        }
                    }
                } else {
                    if (mainCallback != null) {
                        mainCallback.onNullCurrentUserReceived();
                    }
                }
            }

            private void updateCurrentUserFriendsIds(HashMap<String, User> mapOfFriends) {
                try {
                    currentUser.friendsIds = new ArrayList<>(mapOfFriends.keySet());
                    writeCurrentUserIntoDb();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadAllFriendsFromDb() {
        try {
            ArrayList<String> listOfFriendsIds = currentUser.friendsIds;
            for (String id : listOfFriendsIds) {
                if (!id.equals(currentUser.id)) {
                    loadFriendFromDb(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                loadAllFriendsFromDb();
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

    public void addFriend(String id) {
        if (!id.equals(getCurrentGoogleUserId())) {
            ArrayList<String> listOfFriendsIds = new ArrayList<>(mapOfFriends.keySet());
            listOfFriendsIds.add(id);
            currentUser.friendsIds = listOfFriendsIds;
            writeCurrentUserIntoDb();

            loadFriendFromDb(id);
        }
    }

    public void removeFriend(String id) {
        ArrayList<String> listOfFriendsIds = new ArrayList<>(mapOfFriends.keySet());
        if (listOfFriendsIds.contains(id)) {
            listOfFriendsIds.remove(id);
            currentUser.friendsIds = listOfFriendsIds;

            writeCurrentUserIntoDb();

            mapOfFriends.remove(id);
        }
    }

    public void dispose() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public HashMap<String, User> getMapOfFriends() {
        return mapOfFriends;
    }

    public void setMapOfFriends(HashMap<String, User> mapOfFriends) {
        this.mapOfFriends = mapOfFriends;
    }

    public ArrayList<String> getListOfFriendsIds() {
        return new ArrayList<>(mapOfFriends.keySet());
    }


    // Maps:

    public void initializeLocationListener() {
        locationListener = new LocationListener() {

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                getMostAccurateLocation();
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
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
        return mostAccurateLocation;
    }
}
