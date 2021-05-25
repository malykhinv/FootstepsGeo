package com.malykhinv.footstepsgeo.di.modules;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    private static final String DB_USERS_PATH = "users";

    @Provides
    @Singleton
    public FirebaseDatabase provideDb() {
        return FirebaseDatabase.getInstance();
    }

    @Provides
    @Singleton
    public DatabaseReference provideDbUsersReference() {
        return provideDb().getReference(DB_USERS_PATH);
    }
}
