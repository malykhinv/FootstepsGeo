package com.malykhinv.footstepsgeo.di.modules;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FirebaseModule {

    private static final String DB_USERS_PATH = "users";
    private static final String DB_PERSONAL_CODES_PATH = "pc";

    @Provides
    @Singleton
    public FirebaseDatabase provideDb() {
        return FirebaseDatabase.getInstance();
    }

    @Provides
    @Singleton
    @Named(DB_USERS_PATH)
    public DatabaseReference provideDbUsersReference() {
        // id: User - Allows to get a User by id
        return provideDb().getReference(DB_USERS_PATH);
    }

    @Provides
    @Singleton
    @Named(DB_PERSONAL_CODES_PATH)
    public DatabaseReference provideDbPersonalCodesReference() {
        // personalCode: id - Allows to get an id by personal code
        return provideDb().getReference(DB_PERSONAL_CODES_PATH);
    }
}
