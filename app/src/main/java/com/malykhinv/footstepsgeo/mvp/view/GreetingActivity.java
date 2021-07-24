package com.malykhinv.footstepsgeo.mvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.malykhinv.footstepsgeo.R;
import com.malykhinv.footstepsgeo.databinding.ActivityGreetingBinding;
import com.malykhinv.footstepsgeo.mvp.presenter.GreetingPresenter;

import java.util.Random;

public class GreetingActivity extends AppCompatActivity {

    private final String SIGN_OUT_EXTRA_LABEL = "sign_out";
    private static final int DURATION = 200;
    private static final int USER_ACTION_DELAY = 2000;
    private static final int[] illustrations = new int[] {R.drawable.image_greeting_1, R.drawable.image_greeting_2, R.drawable.image_greeting_3};
    private ActivityGreetingBinding binding;
    private GreetingPresenter presenter;
    private boolean isAboutToClose = false;
    private boolean isOnline = false;
    private boolean isUndefinedConnectionStatus = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (presenter == null) {
            presenter = new GreetingPresenter(this);
        }
        bind();
        presenter.onViewIsReady();
    }

    public void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean signOutFlag = extras.getBoolean(SIGN_OUT_EXTRA_LABEL);
            if (signOutFlag) {
                presenter.signOut();
            }
        }
    }

    private void bind() {
        binding = ActivityGreetingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.buttonSignIn.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.onSignInButtonWasClicked();
            }
        });
    }

    public void showIllustration() {
        setRandomIllustration();
        animateIllustration();
    }

    private void setRandomIllustration(){
        Random random = new Random();
        int randomIndex = random.nextInt(illustrations.length);
        binding.imageGreetingIllustration.setImageResource(illustrations[randomIndex]);
    }

    private void animateIllustration(){
        Animation floating = AnimationUtils.loadAnimation(this, R.anim.animation_floating_slow);
        binding.imageGreetingIllustration.startAnimation(floating);
        binding.imageGreetingIllustration.animate().alpha(1).setDuration(DURATION);
    }

    public void setConnectionMode(boolean isConnectionEstablished) {
        if (isOnline != isConnectionEstablished || isUndefinedConnectionStatus) {
            isUndefinedConnectionStatus = false;
            isOnline = isConnectionEstablished;
            if (isConnectionEstablished) {
                showOnlineMode();
            } else {
                showOfflineMode();
            }
        }
    }

    private void showOnlineMode() {
        binding.textAppTitle.setText(R.string.app_name);
        binding.textGreeting.setText(R.string.greeting_core);
        binding.textGreeting.setTextColor(this.getColor(R.color.black));
        binding.buttonSignIn.setClickable(true);
        if (binding.buttonSignIn.getVisibility() == View.INVISIBLE) {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.buttonSignIn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_fade_in_fast));
        }
    }

    private void showOfflineMode() {
        binding.textAppTitle.setText(R.string.oh_no);
        binding.textGreeting.setText(R.string.you_are_offline);
        binding.textGreeting.setTextColor(this.getColor(R.color.green_a700));
        binding.buttonSignIn.setClickable(false);
        if (binding.buttonSignIn.getVisibility() == View.VISIBLE) {
            binding.buttonSignIn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_fade_out_fast));
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (presenter != null) {
            presenter.onSignInActivityResult(requestCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (isAboutToClose) finish();
        else {
            isAboutToClose = true;
            showMessage(getString(R.string.press_back_again_to_quit));
            Handler handler = new Handler();
            handler.postDelayed(() -> isAboutToClose = false, USER_ACTION_DELAY);
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroyView();
        presenter = null;
        super.onDestroy();
    }
}
