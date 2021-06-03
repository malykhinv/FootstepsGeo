package com.malykhinv.footstepsgeo.mvp.presenter;

import android.util.Log;

import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.MainActivity;

public class MainPresenter implements MainModel.MainCallback {

    private final String TAG = this.getClass().getName();
    private final MainActivity view;
    private final MainModel model;

    public MainPresenter(MainActivity view) {
        this.view = view;
        this.model = App.getAppComponent().getMainModel();
        model.registerMainCallback(this);
    }


    // Call from View:

    public void onViewIsReady() {
        view.loadCurrentGoogleUserInfo();
    }

    public void onCurrentGoogleUserInfoWasLoaded(String userId, String userName) {
        Log.d(TAG, "onCurrentGoogleUserInfoWasLoaded: " + userName);
        model.setCurrentGoogleUserInfo(userId, userName);
        model.loadCurrentUserFromDb();
    }


    // Call from Model:

    @Override
    public void onCurrentUserReceived(User user) {
        if (user != null) {
            model.setCurrentUser(user);
        }
    }

    @Override
    public void onNullCurrentUserReceived() {
        Log.d(TAG, "onNullCurrentUserReceived: ");
        model.createNewUser();
    }

    @Override
    public void onCurrentUserWasWrittenIntoDatabase(User user) {
        model.loadCurrentUserFromDb();
    }
}
