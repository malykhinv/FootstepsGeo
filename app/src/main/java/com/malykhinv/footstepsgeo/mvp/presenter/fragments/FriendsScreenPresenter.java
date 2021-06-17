package com.malykhinv.footstepsgeo.mvp.presenter.fragments;

import android.annotation.SuppressLint;
import android.view.MenuItem;

import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.fragments.screens.FriendsScreenFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsScreenPresenter implements MainModel.UserlistCallback {

    private final FriendsScreenFragment view;
    private final MainModel model;
    private HashMap<String, User> mapOfFriends;
    private ArrayList<User> listOfFriends;

    public FriendsScreenPresenter(FriendsScreenFragment view) {
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
            mapOfFriends.put(user.id, user);
            listOfFriends.add(user);
            view.updateUI(listOfFriends);
        }
    }
}
