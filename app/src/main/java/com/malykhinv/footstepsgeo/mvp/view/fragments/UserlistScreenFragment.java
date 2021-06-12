package com.malykhinv.footstepsgeo.mvp.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.FragmentUserlistScreenBinding;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.UserlistScreenPresenter;
import com.malykhinv.footstepsgeo.mvp.view.adapters.UserlistAdapter;

import java.util.ArrayList;

public class UserlistScreenFragment extends Fragment {

    private FragmentUserlistScreenBinding b;
    private View view;
    private UserlistScreenPresenter presenter;
    private UserlistAdapter userlistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentUserlistScreenBinding.inflate(inflater, container, false);

        if (view == null) {
            view = b.getRoot();
        } else if (view.getParent() != null) {
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

        initializeRecyclerView();


        // TEMPORARY
        ArrayList<User> friendUsers = new ArrayList<>();
        friendUsers.add(new User("1", "Simon", "USUFHA", "https://img.chainimage.com/images/nature-wallpaper-hd-widescreen-images-desktop-background-227-14.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("2", "Irusya", "USUFHA", "https://i.redd.it/2s78x6cxtum01.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("1", "Simon", "USUFHA", "https://img.chainimage.com/images/nature-wallpaper-hd-widescreen-images-desktop-background-227-14.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("2", "Irusya", "USUFHA", "https://i.redd.it/2s78x6cxtum01.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("1", "Simon", "USUFHA", "https://img.chainimage.com/images/nature-wallpaper-hd-widescreen-images-desktop-background-227-14.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("2", "Irusya", "USUFHA", "https://i.redd.it/2s78x6cxtum01.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("1", "Simon", "USUFHA", "https://img.chainimage.com/images/nature-wallpaper-hd-widescreen-images-desktop-background-227-14.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("2", "Irusya", "USUFHA", "https://i.redd.it/2s78x6cxtum01.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("1", "Simon", "USUFHA", "https://img.chainimage.com/images/nature-wallpaper-hd-widescreen-images-desktop-background-227-14.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("2", "Irusya", "USUFHA", "https://i.redd.it/2s78x6cxtum01.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("1", "Simon", "USUFHA", "https://img.chainimage.com/images/nature-wallpaper-hd-widescreen-images-desktop-background-227-14.jpg", 0, null, null, 0, 100, null));
        friendUsers.add(new User("2", "Irusya", "USUFHA", "https://i.redd.it/2s78x6cxtum01.jpg", 0, null, null, 0, 100, null));
        updateUI(friendUsers);
    }

    private void initializeRecyclerView() {
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        userlistAdapter = new UserlistAdapter();
        b.recyclerView.setAdapter(userlistAdapter);
    }

    public void updateUI(ArrayList<User> friendUsers) {
        b.textFriendCount.setText(String.valueOf(friendUsers.size()));
        userlistAdapter.clearItems();
        userlistAdapter.setItems(friendUsers);
    }
}