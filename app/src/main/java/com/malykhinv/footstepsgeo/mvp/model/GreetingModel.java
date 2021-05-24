package com.malykhinv.footstepsgeo.mvp.model;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.di.App;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GreetingModel {

    private static final int TIMER_DURATION = 500;
    private static final int RC_SIGN_IN = 4469;
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String TAG = this.getClass().getName();
    private final Context context = App.getAppComponent().getContext();
    private Callback callback;
    private Disposable disposable;
    private GoogleSignInClient googleSignInClient;

    public interface Callback {
        void onConnectionStatusReceived(boolean isConnectionEstablished);
        void onSignedIn(FirebaseUser currentUser);
        void onErrorWhileSigningIn(String message);
        void onGoogleSignInClientInitialized();
    }

    public void registerCallback(Callback callback) {
        this.callback = callback;
    }

    public void initGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
        if (callback != null) {
            callback.onGoogleSignInClientInitialized();
        }
    }

    public void trackConnection() {
        Observer<Long> timerObserver = new Observer<Long>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe: ");
                disposable = d;
                getConnectionStatus();
            }

            @Override
            public void onNext(@NonNull Long tick) {
                Log.d(TAG, "onNext: ");
                getConnectionStatus();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.w(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };

        Observable<Long> timerObservable = Observable.timer(TIMER_DURATION, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        timerObservable.subscribe(timerObserver);
    }

    public void dispose() {
        callback = null;
        disposable.dispose();
    }

    private void getConnectionStatus() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (callback != null) {
            callback.onConnectionStatusReceived(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
        }
    }

    public void getCurrentUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            if (callback != null) {
                callback.onSignedIn(currentUser);
            }
        }
    }

    public Intent getSignInIntent() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInIntent.putExtra("request_code", RC_SIGN_IN);
        return signInIntent;
    }

    public void signIn(int requestCode, Intent data) {
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(Objects.requireNonNull(account).getIdToken());
            Log.d(TAG, "handleSignInResult: " + account.getId());
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult: ", e);
            if (callback != null) {
                callback.onErrorWhileSigningIn(e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "firebaseAuthWithGoogle: success");
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (callback != null) {
                            callback.onSignedIn(currentUser);
                        }
                    } else {
                        Log.w(TAG, "firebaseAuthWithGoogle: failure", task.getException());
                        if (callback != null) {
                            callback.onErrorWhileSigningIn(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }
}
