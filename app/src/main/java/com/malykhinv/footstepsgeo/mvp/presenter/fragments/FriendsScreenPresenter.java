package com.malykhinv.footstepsgeo.mvp.presenter.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MenuItem;

import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.fragments.screens.FriendsScreenFragment;

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

    public void onViewCreated() {
        view.initializeRecyclerView();
        model.loadAllFriendsFromDb();

        HashMap<String, User> mapOfFriends = model.getMapOfFriends();
        if (mapOfFriends == null || mapOfFriends.size() == 0) {
            view.hideUserLoadingProgress();
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onFriendOptionWasClicked(MenuItem item, String userId) {
        switch (item.getItemId()) {
            case R.id.menuItemUpdateFriendInfo: {
                    model.loadFriendFromDb(userId);
                break;
            }
            case R.id.menuItemGetRoute: {

                break;
            }
            case R.id.menuItemRemoveFriend: {
                model.removeFriend(userId);
                break;
            }
        }
    }

    public void onAddFriendMenuItemWasClicked() {
        view.showAddFriendDialog();
    }

    public void onSubmitCodeButtonWasPressed(String personalCode) {
        view.closeDialogWindow();
        view.showUserLoadingProgress();
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
            model.addFriend(user.id);
        }

        view.updateUI(mapOfFriends);
        view.hideUserLoadingProgress();
    }

    @Override
    public void onFriendIdReceived(String friendsId) {
        model.loadFriendFromDb(friendsId);
    }

    @Override
    public void onNullFriendIdReceived() {
        view.closeDialogWindow();
        view.hideUserLoadingProgress();
        String message = context.getString(R.string.error_no_user);
        view.showMessage(message);
    }

    @Override
    public void onFriendWasRemoved(HashMap<String, User> mapOfFriends) {
        view.updateUI(mapOfFriends);
    }
}
