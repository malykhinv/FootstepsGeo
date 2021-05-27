package com.malykhinv.footstepsgeo.di;

import android.content.ClipboardManager;
import android.content.Context;
import android.location.LocationManager;
import android.os.BatteryManager;

import com.google.firebase.database.DatabaseReference;
import com.malykhinv.footstepsgeo.di.modules.AppModule;
import com.malykhinv.footstepsgeo.di.modules.FirebaseModule;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, FirebaseModule.class})
public interface AppComponent {
    Context getContext();
    BatteryManager getBatteryManager();
    int getBatteryLevel();
    ClipboardManager getClipboard();
    LocationManager getLocationManager();
    MainModel getMainModel();

    DatabaseReference getDbUsersReference();
}
