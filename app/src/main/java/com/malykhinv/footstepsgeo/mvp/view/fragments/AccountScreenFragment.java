package com.malykhinv.footstepsgeo.mvp.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.malykhinv.footstepsgeo.databinding.FragmentAccountScreenBinding;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.AccountScreenPresenter;

public class AccountScreenFragment extends Fragment {

    private FragmentAccountScreenBinding b;
    private View view;
    private AccountScreenPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        b = FragmentAccountScreenBinding.inflate(inflater, container, false);
        if (view == null) {
            view = b.getRoot();
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (presenter == null) {
            presenter = new AccountScreenPresenter(this);
        }
    }
}