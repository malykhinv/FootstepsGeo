package com.malykhinv.footstepsgeo.mvp.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.databinding.ActivityMainBinding;
import com.malykhinv.footstepsgeo.mvp.presenter.MainPresenter;
import com.malykhinv.footstepsgeo.mvp.view.fragments.screens.AccountScreenFragment;
import com.malykhinv.footstepsgeo.mvp.view.fragments.screens.FriendsScreenFragment;
import com.malykhinv.footstepsgeo.mvp.view.fragments.screens.GlobeScreenFragment;

public class MainActivity extends AppCompatActivity {

    private static final int INITIAL_MENU_ITEM_INDEX = 1;
    private static final int USER_ACTION_DELAY = 2000;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private ActivityMainBinding b;
    private MainPresenter presenter;
    private FriendsScreenFragment fragmentUserlistScreen;
    private GlobeScreenFragment fragmentGlobeScreen;
    private AccountScreenFragment fragmentAccountScreen;
    private boolean isAboutToClose = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind();
        initializeFragments();
        initializeBottomNavigation();
        presenter = new MainPresenter(this);
        presenter.onViewIsReady();
    }

    private void bind() {
        b = ActivityMainBinding.inflate(getLayoutInflater());
        View view = b.getRoot();
        setContentView(view);
    }

    private void initializeFragments() {
        fragmentUserlistScreen = new FriendsScreenFragment();
        fragmentGlobeScreen = new GlobeScreenFragment();
        fragmentAccountScreen = new AccountScreenFragment();
    }

    @SuppressLint("NonConstantResourceId")
    private void initializeBottomNavigation() {
        b.menuBottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuItemUserlist: {
                    showFragment(fragmentUserlistScreen);
                    return true;
                }
                case R.id.menuItemGlobe: {
                    showFragment(fragmentGlobeScreen);
                    return true;
                }
                case R.id.menuItemAccount: {
                    showFragment(fragmentAccountScreen);
                    return true;
                }
            }
            return false;
        });
        showInitialFragment();
    }

    private void showInitialFragment() {
        b.menuBottomNavigation.setSelectedItemId(b.menuBottomNavigation.getMenu().getItem(INITIAL_MENU_ITEM_INDEX).getItemId());
    }

    private void showFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(b.layoutFragmentContainer.getId(), fragment).commit();
    }

    public void loadCurrentGoogleUserInfo() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String userName = intent.getStringExtra("userName");
        String imageUrl = intent.getStringExtra("imageUrl");
        if (userId != null){
            presenter.onCurrentGoogleUserInfoWasLoaded(userId, userName, imageUrl);
        }
    }

    @Override
    public void onBackPressed() {
        int initialMenuItemId = b.menuBottomNavigation.getMenu().getItem(INITIAL_MENU_ITEM_INDEX).getItemId();
        if (b.menuBottomNavigation.getSelectedItemId() != initialMenuItemId) {
            showInitialFragment();
        } else {
            if (isAboutToClose) finish();
            else {
                isAboutToClose = true;
                showMessage(getString(R.string.press_back_again_to_quit));
                Handler handler = new Handler();
                handler.postDelayed(() -> isAboutToClose = false, USER_ACTION_DELAY);
            }
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentUserlistScreen = null;
        fragmentGlobeScreen = null;
        fragmentAccountScreen = null;
        presenter = null;
        b = null;
    }
}
