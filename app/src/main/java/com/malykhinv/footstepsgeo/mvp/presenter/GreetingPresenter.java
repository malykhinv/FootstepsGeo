package com.malykhinv.footstepsgeo.mvp.presenter;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.malykhinv.footstepsgeo.mvp.model.GreetingModel;
import com.malykhinv.footstepsgeo.mvp.view.GreetingActivity;
import com.malykhinv.footstepsgeo.mvp.view.MainActivity;

import java.util.Objects;

public class GreetingPresenter implements GreetingModel.Callback {

    private static final int DEFAULT_REQUEST_CODE = 0;
    private static final int DELAY_LONG = 1000;
    private final String TAG = this.getClass().getName();
    private GreetingActivity view;
    private GreetingModel model;
    private boolean isGoogleSignInClientInitialized = false;

    public GreetingPresenter(GreetingActivity view) {
        this.view = view;
        this.model = new GreetingModel();
        model.registerCallback(this);
    }


    // Call from View:

    public void onViewIsReady() {
        Log.d(TAG, "onViewIsReady: ");
        view.showIllustration();
        Handler handler = new Handler();
        handler.postDelayed(model::trackConnection, DELAY_LONG);
    }

    public void onSignInButtonWasClicked() {
        Log.d(TAG, "onSignInButtonWasClicked: ");
        Intent signInIntent = model.getSignInIntent();
        view.startActivityForResult(signInIntent, signInIntent.getIntExtra("request_code", DEFAULT_REQUEST_CODE));
    }

    public void onSignInActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onSignInActivityResult: " + requestCode + ", " + resultCode);
        model.signIn(requestCode, data);
    }

    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        view = null;
        model.dispose();
        model = null;
    }


    // Call from Model:

    @Override
    public void onConnectionStatusReceived(boolean isConnectionEstablished) {
        Log.d(TAG, "onConnectionStatusReceived: " + isConnectionEstablished);

        view.setConnectionMode(isConnectionEstablished);
        if (isConnectionEstablished && !isGoogleSignInClientInitialized) {
            model.initGoogleSignInClient();
            model.getCurrentUser();
        }
    }

    @Override
    public void onGoogleSignInClientInitialized() {
        Log.d(TAG, "onGoogleSignInClientInitialized: ");
        isGoogleSignInClientInitialized = true;
    }

    @Override
    public void onSignedIn(FirebaseUser currentUser) {
        Log.d(TAG, "onCurrentUserReceived: " + currentUser);
        if (currentUser != null) {
            Intent intent = new Intent(view, MainActivity.class);
            intent.putExtra("userId", currentUser.getUid());
            intent.putExtra("userName", currentUser.getDisplayName());
            intent.putExtra("imageUrl", Objects.requireNonNull(currentUser.getPhotoUrl()).toString());
            view.startActivity(intent);
            view.finish();
        }
    }

    @Override
    public void onErrorWhileSigningIn(String message) {
        Log.d(TAG, "onErrorWhileSigningIn: " + message);
        view.showMessage(message);
    }
}
