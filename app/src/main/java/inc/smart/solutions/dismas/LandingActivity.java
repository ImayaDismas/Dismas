package inc.smart.solutions.dismas;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import inc.smart.solutions.dismas.databinding.ActivityLandingBinding;
import inc.smart.solutions.dismas.viewmodels.SplashViewModel;

public class LandingActivity extends AppCompatActivity {
    private ActivityLandingBinding binding;
    private SplashViewModel splashViewModel;

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

        binding.tvSlogan.setCharacterDelay(100);
        binding.tvSlogan.animateText(getResources().getString(R.string.slogan));

        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(getResources().getString(R.string.slogan).length() * 50L + 1000);
//                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                    finish();

                } catch (Exception ignored) {}
            }
        };
        background.start();
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
}
