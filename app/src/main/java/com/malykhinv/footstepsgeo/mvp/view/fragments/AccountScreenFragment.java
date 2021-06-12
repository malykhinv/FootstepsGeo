package com.malykhinv.footstepsgeo.mvp.view.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.databinding.FragmentAccountScreenBinding;
import com.malykhinv.footstepsgeo.di.App;
import com.malykhinv.footstepsgeo.mvp.presenter.fragments.AccountScreenPresenter;

public class AccountScreenFragment extends Fragment {

    private final ClipboardManager clipboardManager = App.getAppComponent().getClipboard();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentAccountScreenBinding b;
    private View view;
    private AccountScreenPresenter presenter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        b = FragmentAccountScreenBinding.inflate(inflater, container, false);

        b.buttonShareCode.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.onShareCodeButtonWasClicked();
            }
        });

        b.imageUserpic.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.onUserpicWasClicked(v);
            }
        });

        b.imageButtonCopyPersonalCode.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.onCopyPersonalCodeButtonWasClicked();
            }
        });

        if (view == null) {
            view = b.getRoot();
        } else if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (presenter == null) {
            presenter = new AccountScreenPresenter(this);
        }
    }

    @SuppressLint("CheckResult")
    public void showUserpic(String imageUrl) {
        if (imageUrl != null) {
            RequestOptions options = new RequestOptions();
            options.circleCrop();
            Glide.with(this)
                    .load(imageUrl)
                    .apply(options)
                    .into(b.imageUserpic);
        }
    }

    public void showPersonalCode(String personalCode) {
        if (personalCode != null) {
            b.textPersonalCode.setText(personalCode);
        }
    }

    public void showUsername(String name) {
        if (name != null) {
            b.textAccountNameGreeting.setText(name);
        }
    }

    public void showAccountOptionsPopup(View view) {
        PopupMenu popup = new PopupMenu(this.getContext(), view);
        popup.setOnMenuItemClickListener(v -> {
            if (presenter != null) {
                presenter.onSignOutButtonWasClicked();
            }
            return false;
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_account_options, popup.getMenu());
        popup.show();
    }

    public void shareCode(String personalCode) {
        if (personalCode != null) {
            String textToShare = getString(R.string.my_code_in_app_use_it_to_find_me, getString(R.string.app_name), personalCode);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, textToShare);
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
        }
    }

    public void copyToClipboard(String textToCopy) {
        if (textToCopy != null) {
            ClipData clip = ClipData.newPlainText("clip", textToCopy);
            clipboardManager.setPrimaryClip(clip);
            showMessage(getString(R.string.your_personal_code_copied_to_clipboard));
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter = null;
        view = null;
    }
}