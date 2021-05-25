package com.malykhinv.footstepsgeo.di;

import android.app.Application;

import com.malykhinv.footstepsgeo.di.modules.AppModule;

public class App extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}

