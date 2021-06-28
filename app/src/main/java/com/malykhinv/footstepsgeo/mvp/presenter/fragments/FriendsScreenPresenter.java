package com.malykhinv.footstepsgeo.mvp.presenter.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MenuItem;

import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.fragments.screens.FriendsScreenFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsScreenPresenter implements MainModel.FriendsCallback {

    private final Context context = App.getAppComponent().getContext();
    private final FriendsScreenFragment view;
    private final MainModel model;

    public FriendsScreenPresenter(FriendsScreenFragment view) {
        this.view = view;
        this.model = App.getAppComponent().getMainModel();
        model.registerUserlistCallback(this);
    }


    // Call from View:

    @SuppressLint("NonConstantResourceId")
    public void onFriendOptionWasClicked(MenuItem item, int index, String userId) {
        switch (item.getItemId()) {
            case R.id.menuItemUpdateFriendInfo: {
                    ArrayList<String> listOfFriendsIds = model.getListOfFriendsIds();
                    model.loadUserFromDb(listOfFriendsIds.get(index));
                break;
            }
            case R.id.menuItemGetRoute: {

                break;
            }
            case R.id.menuItemRemoveFriend: {
                try {
                    model.removeFriend(userId);

                    HashMap<String, User> mapOfFriends = model.getMapOfFriends();
                    view.updateUI(mapOfFriends);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void onAddFriendMenuItemWasClicked() {
        view.showAddFriendDialog();
    }

    public void onSubmitCodeButtonWasPressed(String personalCode) {
        view.closeDialogWindow();
        try {
            model.loadIdFromDb(personalCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCloseDialogWindowButtonWasClicked() {
        view.closeDialogWindow();
    }


    // Call from Model:

    @Override
    public void onFriendUserReceived(User user) {
        HashMap<String, User> mapOfFriends = model.getMapOfFriends();

        if (!mapOfFriends.containsKey(user.id)) {
            mapOfFriends.put(user.id, user);
            model.setMapOfFriends(mapOfFriends);
            view.updateUI(mapOfFriends);
        }

        ArrayList<String> listOfFriendsIds = model.getListOfFriendsIds();
        if (!listOfFriendsIds.contains(user.id)) {
            model.addFriend(user.id);
        }
    }

    @Override
    public void onFriendIdReceived(String friendsId) {
        model.loadUserFromDb(friendsId);
    }

    @Override
    public void onNullFriendIdReceived() {
        view.closeDialogWindow();
        String message = context.getString(R.string.error_no_user);
        view.showMessage(message);
    }
}
