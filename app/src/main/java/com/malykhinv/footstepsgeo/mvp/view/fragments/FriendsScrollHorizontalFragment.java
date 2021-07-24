package com.malykhinv.footstepsgeo.mvp.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.malykhinv.footstepsgeo.databinding.FragmentFriendsScrollHorizontalBinding;
import com.malykhinv.footstepsgeo.mvp.view.adapters.FriendsScrollHorizontalAdapter;

public class FriendsScrollHorizontalFragment extends Fragment {

    private View view;
    private FragmentFriendsScrollHorizontalBinding binding;
    private FriendsScrollHorizontalAdapter userlistAdapter;

    @Override
    public View onCreateView(@io.reactivex.rxjava3.annotations.NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendsScrollHorizontalBinding.inflate(inflater, container, false);

        if (view == null) {
            view = binding.getRoot();
        } else if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        userlistAdapter = new FriendsScrollHorizontalAdapter();
        binding.recyclerView.setAdapter(userlistAdapter);
    }
}