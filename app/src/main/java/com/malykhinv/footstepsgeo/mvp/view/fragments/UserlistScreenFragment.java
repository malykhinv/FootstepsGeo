package com.malykhinv.footstepsgeo.mvp.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.malykhinv.footstepsgeo.databinding.FragmentUserlistScreenBinding;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.UserlistScreenPresenter;

public class UserlistScreenFragment extends Fragment {

    private FragmentUserlistScreenBinding b;
    private View view;
    private UserlistScreenPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentUserlistScreenBinding.inflate(inflater, container, false);

        if (view == null) {
            view = b.getRoot();
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (presenter == null) {
            presenter = new UserlistScreenPresenter(this);
        }
    }
}