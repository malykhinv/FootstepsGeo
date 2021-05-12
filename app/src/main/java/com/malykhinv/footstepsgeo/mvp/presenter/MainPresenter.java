package com.malykhinv.footstepsgeo.mvp.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.MainActivity;

public class MainPresenter {

    private MainActivity view;
    private final MainModel model;

    public MainPresenter(MainModel model){
        this.model = model;
    }

    public void attachView(MainActivity activity){
        view = activity;
    }

    public void detachView(){
        view = null;
    }

    public void viewIsReady(){
        loadData();
    }

    public void loadData(){
        view.loadCurrentFBUserInfo();
        //view.loadFriends();
    }

    public void currentFBUserInfoIsLoaded(FirebaseDatabase database, DatabaseReference usersReference, String userId, String userName){
        model.loadDatabase(database, usersReference);
        User user = model.findUserById(database, usersReference, userId);
        if (user == null){
            model.createNewUser(userId, userName);
        } else {
            view.updateUserData(user);
        }
    }
}
