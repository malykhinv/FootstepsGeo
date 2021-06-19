package com.malykhinv.footstepsgeo.mvp.view.fragments.screens;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.User;
import com.malykhinv.footstepsgeo.databinding.DialogAddFriendBinding;
import com.malykhinv.footstepsgeo.databinding.FragmentFriendsScreenBinding;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.FriendsScreenPresenter;
import com.malykhinv.footstepsgeo.mvp.view.adapters.FriendsScrollVerticalAdapter;

import java.util.ArrayList;

public class FriendsScreenFragment extends Fragment {

    private FragmentFriendsScreenBinding b;
    private View view;
    private FriendsScreenPresenter presenter;
    private FriendsScrollVerticalAdapter userlistAdapter;
    private AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentFriendsScreenBinding.inflate(inflater, container, false);

        b.toolbar.getMenu().getItem(0).setOnMenuItemClickListener(v -> {
           if (presenter != null) {
               presenter.onAddFriendMenuItemWasClicked();
           }
            return false;
        });

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
            presenter = new FriendsScreenPresenter(this);
        }

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        b.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        userlistAdapter = new FriendsScrollVerticalAdapter(presenter);
        b.recyclerView.setAdapter(userlistAdapter);
    }

    public void updateUI(ArrayList<User> friendUsers) {
        if (friendUsers != null && friendUsers.size() > 0) {
            b.textFriendCount.setText(String.valueOf(friendUsers.size()));
            userlistAdapter.clearItems();
            userlistAdapter.setItems(friendUsers);
        } else {
            b.textFriendCount.setText(R.string.init_count);
        }
    }

    public void showAddFriendDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_friend, (ViewGroup) getView(), false);
        dialogBuilder.setView(dialogView);

        DialogAddFriendBinding b = DialogAddFriendBinding.bind(dialogView);
        b.buttonSubmit.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.onSubmitCodeButtonWasPressed(b.editTextFriendCode.getText().toString());
            }
        });
        b.imageButtonCloseDialog.setOnClickListener((v -> {
            if (presenter != null) {
                presenter.onCloseDialogWindowButtonWasClicked();
            }
        }));

        dialog = dialogBuilder.show();
    }

    public void closeDialogWindow() {
        try {
            dialog.dismiss();
            dialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}