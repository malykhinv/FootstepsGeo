package com.malykhinv.footstepsgeo.mvp.presenter;

import android.util.Log;

import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.MainActivity;

public class MainPresenter implements MainModel.Callback {

    private final String TAG = this.getClass().getName();
    private final MainActivity view;
    private final MainModel model;

    public MainPresenter(MainActivity view) {
        this.view = view;
        this.model = App.getAppComponent().getMainModel();
        model.registerCallback(this);
    }


    // Call from View:

    public void onViewIsReady() {
        view.loadCurrentGoogleUserInfo();
    }

    public void onCurrentGoogleUserInfoWasLoaded(String userId, String userName) {
        Log.d(TAG, "onCurrentGoogleUserInfoWasLoaded: " + userName);
        User user = model.findUserById(userId);
        if (user == null) {
            model.writeNewUserIntoDatabase(userId, userName);
        }
        view.updateUserData(user);
    }


    // Call from Model:

    @Override
    public void onUserWasLoaded(User user) {
        Log.d(TAG, "onUserWasLoaded: " + user.id);
    }

    @Override
    public void onErrorWhileLoadingUser(String message) {
        view.showMessage(message);
    }
}
