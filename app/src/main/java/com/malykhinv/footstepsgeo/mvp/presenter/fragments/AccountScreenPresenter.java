package com.malykhinv.footstepsgeo.mvp.presenter.fragments;

import android.content.Intent;
import android.view.View;

import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.GreetingActivity;
import com.malykhinv.footstepsgeo.mvp.view.fragments.AccountScreenFragment;

public class AccountScreenPresenter implements MainModel.AccountCallback{

    private AccountScreenFragment view;
    private MainModel model;
    private User currentUser;

    public AccountScreenPresenter(AccountScreenFragment view) {
        this.view = view;
        this.model = App.getAppComponent().getMainModel();
        model.registerAccountCallback(this);
        currentUser = model.getCurrentUser();
        showCurrentUserInfo(currentUser);
    }

    private void showCurrentUserInfo(User currentUser) {
        if (view != null && currentUser != null) {
            if (currentUser.imageUrl != null) {
                view.showUserpic(currentUser.imageUrl);
            }
            if (currentUser.personalCode != null) {
                view.showPersonalCode(currentUser.personalCode);
            }
            if (currentUser.name != null) {
                view.showUsername(currentUser.name);
            }
        }
    }

    @Override
    public void onCurrentUserReceived(User currentUser) {
        showCurrentUserInfo(currentUser);
    }

    public void onUserpicWasClicked(View view) {
        this.view.showUserOptionsPopup(view);
    }

    public void onShareCodeButtonWasClicked() {
        String personalCode = model.getCurrentUserPersonalCode();
        if (personalCode != null) {
            view.shareCode(personalCode);
        }
    }

    public void onCopyPersonalCodeButtonWasClicked() {
        String personalCode = model.getCurrentUserPersonalCode();
        if (personalCode != null) {
            view.copyToClipboard(personalCode);
        }
    }

    public void onSignOutButtonWasClicked() {
        view.signOut();
        currentUser = null;
        model.dispose();
        model.clearCurrentUser();
        model = null;

        Intent intent = new Intent(view.getActivity(), GreetingActivity.class);
        view.startActivity(intent);
        view.getActivity().finish();
        view = null;
    }
}
