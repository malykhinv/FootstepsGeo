package com.malykhinv.footstepsgeo.mvp.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malykhinv.footstepsgeo.User;

import java.util.Random;

public class MainModel{
    private final int PERSONAL_CODE_LENGTH = 12;
    private final int IMAGE_COUNT = 121;
    private final String ALPHABET09 = "ABCDEFGHIKLMNOPQRSTVXYZ0123456789";
    FirebaseDatabase database;
    DatabaseReference usersReference;
    User user;

    public void loadDatabase(FirebaseDatabase db, DatabaseReference reference){
        database = db;
        usersReference = reference;
    }

    public User findUserById(FirebaseDatabase database, DatabaseReference usersReference, String userId){
        user = new User(null, null, 0, null, null, null, null, 0);
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                } else {user = null;
                    //usersReference.child(userId).setValue("null");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                user = null;
            }
        });

        if (user != null){
            return user;
        } else return null;
    }

    public void createNewUser(String userId, String userName){
        String personalCode = setNewPersonalCode();
        Random random = new Random();
        int imageNumber = random.nextInt(IMAGE_COUNT);
        int batteryLevel = 0;
        User newUser = new User(userName, personalCode, imageNumber, null, null, null, null, batteryLevel);
        usersReference.child(userId).setValue("newUser");
    }

    public String setNewPersonalCode(){
        char[] randomCode = new char[PERSONAL_CODE_LENGTH];
        String alphabet09 = ALPHABET09;
        Random random = new Random();

        for (int i = 0; i < randomCode.length; i++){
            randomCode[i] = alphabet09.charAt(random.nextInt(alphabet09.length()));
        }
        return new String(randomCode);
    }

}
