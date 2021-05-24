package com.malykhinv.footstepsgeo.di;

import android.content.ClipboardManager;
import android.content.Context;
import android.location.LocationManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    Context getContext();
    ClipboardManager getClipboard();
    LocationManager getLocationManager();
}
