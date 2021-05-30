package com.malykhinv.footstepsgeo.mvp.presenter;

import android.util.Log;

import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.MainActivity;

public class MainPresenter {

    private final String TAG = this.getClass().getName();
    private final MainActivity view;
    private final MainModel model;
    private String[] currentGoogleUserInfo;

    public MainPresenter(MainActivity view) {
        this.view = view;
        this.model = App.getAppComponent().getMainModel();
    }


    // Call from View:

    public void onViewIsReady() {
        view.loadCurrentGoogleUserInfo();
    }

    public void onCurrentGoogleUserInfoWasLoaded(String userId, String userName) {
        Log.d(TAG, "onCurrentGoogleUserInfoWasLoaded: " + userName);
        currentGoogleUserInfo = new String[] {userId, userName};
        model.setCurrentGoogleUserInfo(userId, userName);
        model.findUserById(userId);
    }
}
