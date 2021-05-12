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

    private static final int DURATION = 200;
    private static final int USER_ACTION_DELAY = 2000;
    private static final int[] illustrations = new int[] {R.drawable.image_greeting_1, R.drawable.image_greeting_2, R.drawable.image_greeting_3};
    private ActivityGreetingBinding b;
    private GreetingPresenter presenter;
    private boolean isAboutToClose = false;
    private boolean isOnline = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            presenter = new GreetingPresenter(this);
        }
        bind();
        presenter.onViewIsReady();
    }

    private void bind() {
        b = ActivityGreetingBinding.inflate(getLayoutInflater());
        View view = b.getRoot();
        setContentView(view);

        b.buttonSignIn.setOnClickListener(v -> {
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
        b.imageGreetingIllustration.setImageResource(illustrations[randomIndex]);
    }

    private void animateIllustration(){
        Animation floating = AnimationUtils.loadAnimation(this, R.anim.animation_floating_slow);
        b.imageGreetingIllustration.startAnimation(floating);
        b.imageGreetingIllustration.animate().alpha(1).setDuration(DURATION);
    }

    public void setConnectionMode(boolean isConnectionEstablished) {
        if (isOnline != isConnectionEstablished) {
            isOnline = isConnectionEstablished;
            b.buttonSignIn.setVisibility(View.VISIBLE);

            if (isConnectionEstablished) {
                b.textAppTitle.setText(R.string.app_name);
                b.textGreeting.setText(R.string.greeting_core);
                b.textGreeting.setTextColor(this.getColor(R.color.black));
                b.buttonSignIn.setClickable(true);
                b.buttonSignIn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_fade_in_fast));
                b.buttonSignIn.setVisibility(View.VISIBLE);
            } else {
                b.textAppTitle.setText(R.string.oh_no);
                b.textGreeting.setText(R.string.you_are_offline);
                b.textGreeting.setTextColor(this.getColor(R.color.orange));
                b.buttonSignIn.setClickable(false);
                b.buttonSignIn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_fade_out_fast));
                b.buttonSignIn.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (presenter != null) {
            presenter.onSignInActivityResult(requestCode, resultCode, data);
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
        if (presenter != null) {
            presenter.onDestroyView();
        }
        super.onDestroy();
    }
}
