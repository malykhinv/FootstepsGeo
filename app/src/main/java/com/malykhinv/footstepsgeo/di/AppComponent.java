package com.malykhinv.footstepsgeo.di;

import android.content.ClipboardManager;
import android.content.Context;
import android.location.LocationManager;

import com.google.firebase.database.DatabaseReference;
import com.malykhinv.footstepsgeo.di.modules.AppModule;
import com.malykhinv.footstepsgeo.di.modules.FirebaseModule;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, FirebaseModule.class})
public interface AppComponent {
    Context getContext();
    int getBatteryLevel();
    ClipboardManager getClipboard();
    LocationManager getLocationManager();
    MainModel getMainModel();

    @Named("users") DatabaseReference getDbUsersReference();
    @Named("pc") DatabaseReference getDbPersonalCodesReference();
}
