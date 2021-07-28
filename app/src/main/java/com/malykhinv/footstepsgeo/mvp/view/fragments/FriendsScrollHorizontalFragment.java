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
import com.malykhinv.footstepsgeo.databinding.FragmentFriendsScrollHorizontalBinding;
import com.malykhinv.footstepsgeo.mvp.view.adapters.FriendsScrollHorizontalAdapter;

import java.util.HashMap;

public class FriendsScrollHorizontalFragment extends Fragment {

    private View view;
    private FragmentFriendsScrollHorizontalBinding binding;
    private FriendsScrollHorizontalAdapter friendListAdapter;

    @Override
    public View onCreateView(@io.reactivex.rxjava3.annotations.NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentFriendsScrollHorizontalBinding.inflate(inflater, container, false);
        }

        if (view == null) {
            view = binding.getRoot();
        } else if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @io.reactivex.rxjava3.annotations.NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        if (friendListAdapter == null) {
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
            friendListAdapter = new FriendsScrollHorizontalAdapter();
            binding.recyclerView.setAdapter(friendListAdapter);
        }
    }

    public void updateUI(HashMap<String, User> mapOfFriends) {
        if (friendListAdapter != null) {
            friendListAdapter.clearItems();

            if (mapOfFriends != null) {
                if (mapOfFriends.size() > 0) {
                    setNoFriendsTextVisible(false);
                    friendListAdapter.setItems(mapOfFriends);
                } else {
                    setNoFriendsTextVisible(true);
                }
            }
        }
    }

    private void setNoFriendsTextVisible(boolean visible) {
        if (visible) {
            binding.textNoFriends.setVisibility(View.VISIBLE);
        } else {
            binding.textNoFriends.setVisibility(View.INVISIBLE);
        }
    }
}