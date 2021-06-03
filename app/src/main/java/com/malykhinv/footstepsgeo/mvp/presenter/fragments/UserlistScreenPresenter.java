package com.malykhinv.footstepsgeo.mvp.presenter.fragments;

import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.fragments.UserlistScreenFragment;

import java.util.HashMap;

public class UserlistScreenPresenter implements MainModel.UserlistCallback {

    private final UserlistScreenFragment view;
    private final MainModel model;
    private HashMap<String, User> friends;

    public UserlistScreenPresenter(UserlistScreenFragment view) {
        this.view = view;
        this.model = App.getAppComponent().getMainModel();
        model.registerUserlistCallback(this);
    }

    @Override
    public void onCurrentUserReceived(User user) {
        if (user != null) {
            model.dispose();
            model.trackFriends();
        }
    }

    @Override
    public void onFriendUserReceived(User user) {
        if (user != null) {
            friends.put(user.id, user);
        }
    }
}
