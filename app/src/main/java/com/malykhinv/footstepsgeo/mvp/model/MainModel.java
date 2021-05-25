package com.malykhinv.footstepsgeo.mvp.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;

import java.util.Arrays;
import java.util.Random;

public class MainModel{
    private static final int PERSONAL_CODE_LENGTH = 6;
    private static final int IMAGE_COUNT = 121;
    private static final String ALPHABET09 = "ABCDEFGHIKLMNOPQRSTVXYZ0123456789";
    private final String TAG = this.getClass().getName();
    private final DatabaseReference usersReference = App.getAppComponent().getDbUsersReference();
    private User user;
    private Callback callback;

    public interface Callback {
        void onErrorWhileLoadingUser(String message);
    }

    public void registerCallback(Callback callback) {
        this.callback = callback;
    }

    public User findUserById(String userId) {
        Log.d(TAG, "findUserById: " + userId);
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    Log.d(TAG, "findUserById: success. " + userId);
                    user = snapshot.getValue(User.class);
                } else {
                    Log.d(TAG, "findUserById: failure");
                    user = null;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "findUserById: failure. " + error.getMessage());
                user = null;
                if (callback != null) {
                    callback.onErrorWhileLoadingUser(error.getMessage());
                }
            }
        });

        return user;
    }

    public void writeNewUserIntoDatabase(String userId, String userName) {
        String personalCode = generateNewPersonalCode();
        int imageNumber = generateNewImageNumber();
        int batteryLevel = 0;
        User newUser = new User(userName, personalCode, imageNumber, null, null, null, null, batteryLevel);
        usersReference.child(userId).setValue(newUser);
        Log.d(TAG, "createNewUser: " + userName + ", " + personalCode);
    }

    public String generateNewPersonalCode() {
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
}
