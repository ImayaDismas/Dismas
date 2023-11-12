package inc.smart.solutions.dismas;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Date;

import inc.smart.solutions.dismas.databinding.ActivityLandingBinding;
import inc.smart.solutions.dismas.utils.DateUtils;
import inc.smart.solutions.dismas.viewmodels.SplashViewModel;

public class LandingActivity extends AppCompatActivity {
    private ActivityLandingBinding binding;
    private SplashViewModel splashViewModel;
    private ObjectAnimator rotationAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);

        binding = ActivityLandingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        delaySplashScreen();
        setSplashTimers(); // delay the setting of flag to true by 1000 milliseconds

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000); // Duration in milliseconds
        fadeIn.setRepeatMode(Animation.REVERSE); // Optional: This will reverse the animation when it reaches the end
        fadeIn.setRepeatCount(Animation.INFINITE);

        // Create an ObjectAnimator to rotate the text 360 degrees
        rotationAnimator = ObjectAnimator.ofFloat(binding.tvResults, "rotation", 0f, 360f);
        rotationAnimator.setDuration(1000); // Set the duration of the animation in milliseconds
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Infinite rotation

        // Start the rotation animation
        rotationAnimator.start();

        binding.tvSearching.startAnimation(fadeIn);

        Animation pulse1 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        Animation pulse2 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        Animation pulse3 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        Animation pulse4 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        Animation pulse5 = AnimationUtils.loadAnimation(this, R.anim.pulse);
        Animation pulse6 = AnimationUtils.loadAnimation(this, R.anim.pulse);

        binding.soundRipple1.startAnimation(pulse1);
        binding.soundRipple2.startAnimation(pulse2);
        binding.soundRipple3.startAnimation(pulse3);
        binding.soundRipple4.startAnimation(pulse4);
        binding.soundRipple5.startAnimation(pulse5);
        binding.soundRipple6.startAnimation(pulse6);

        // Load the animation from the XML resource
        Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.infinite_zoom);

        // Set a listener to restart the animation when it ends
        zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended
                // Perform actions after the animation is complete
                binding.tvSearching.clearAnimation();
                rotationAnimator.cancel();
                // Set the rotation to 0
                binding.tvResults.setRotation(0);

                binding.tvSearching.setText(getString(R.string.search_successful));
                binding.tvResults.setCharacterDelay(50);
                String text = String.format(getString(R.string.dismas_i_nyagaka_found_s), DateUtils.formatMonthDayYearTime(new Date()));
                binding.tvResults.setText(text);
                binding.tvResults.setTextColor(ContextCompat.getColor(LandingActivity.this, R.color.hack));
                binding.tvResults.animateText(text);
                delayAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });

        // Start the animation
        binding.button.ivKnob.startAnimation(zoomAnimation);
    }

    private void delayAnimation(){
        // Schedule a task to be executed after the expected animation duration
        new Handler().postDelayed(() -> {
            // This code will be executed when the animation is likely to be complete

            binding.button.ivKnob.setImageDrawable(ContextCompat.getDrawable(LandingActivity.this, R.drawable.ic_target_end));
            // Perform the rotation animation or logic here
            rotateKnob();
        }, 2000);
    }

    private void rotateKnob() {
        // Implement your rotation logic here
        // Example: Rotate the knob 360 degrees to the right on each click
        binding.button.ivKnob.animate().rotationBy(360).setDuration(2000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                // Animation ended
                // Perform actions after the animation is complete

                startMain();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
                // Animation canceled
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {
                // Animation repeated
            }
        }).start();
    }

    private void startMain(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void setSplashTimers(){
        Handler handler = new Handler();
        handler.postDelayed(() -> splashViewModel.setReady(true), 1000);
    }


    private void delaySplashScreen(){
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // check if the initial data is ready.
                        if (splashViewModel.isReady()) {
                            // content is ready; start drawing.
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            // content is not ready; suspend.
                            return false;
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.particleView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.particleView.pause();
    }
}
