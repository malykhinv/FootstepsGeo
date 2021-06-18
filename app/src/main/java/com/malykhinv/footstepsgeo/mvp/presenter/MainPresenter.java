package com.malykhinv.footstepsgeo.mvp.presenter;

import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.MainActivity;

public class MainPresenter implements MainModel.MainCallback {

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

    public void onCurrentGoogleUserInfoWasLoaded(String userId, String userName, String imageUrl) {
        model.setCurrentGoogleUserInfo(userId, userName, imageUrl);
        model.loadCurrentUserFromDb();
    }


    // Call from Model:

    @Override
    public void onCurrentUserReceived(User user) {
        try {
            model.setCurrentUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNullCurrentUserReceived() {
        model.createNewUser();
    }

    @Override
    public void onCurrentUserWasWrittenIntoDatabase(User user) {
        model.loadCurrentUserFromDb();
    }
}
