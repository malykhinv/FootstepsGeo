package com.malykhinv.footstepsgeo.di.modules;

import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;
import android.location.LocationManager;
import android.os.BatteryManager;

import com.malykhinv.footstepsgeo.mvp.model.MainModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    public BatteryManager provideBatteryManager() {
        return (BatteryManager) provideContext().getSystemService(Context.BATTERY_SERVICE);
    }

    @Provides
    public int provideBatteryLevel() {
        return provideBatteryManager().getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    @Provides
    @Singleton
    public ClipboardManager provideClipboard() {
        return (ClipboardManager) provideContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Provides
    @Singleton
    public LocationManager providesLocationManager() {
        return (LocationManager) provideContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    public MainModel provideMainModel() {
        return new MainModel();
    }
}
