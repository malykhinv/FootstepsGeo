package com.malykhinv.footstepsgeo.mvp.presenter.fragments;

import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.model.MainModel;
import com.malykhinv.footstepsgeo.mvp.view.fragments.AccountScreenFragment;

public class AccountScreenPresenter implements MainModel.AccountCallback{

    private final AccountScreenFragment view;
    private final MainModel model;

    public AccountScreenPresenter(AccountScreenFragment view) {
        this.view = view;
        this.model = App.getAppComponent().getMainModel();
        model.registerAccountCallback(this);
    }

    @Override
    public void onCurrentUserReceived(User user) {

    }
}
