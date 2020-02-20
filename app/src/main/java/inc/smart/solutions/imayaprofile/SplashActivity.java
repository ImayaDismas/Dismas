package inc.smart.solutions.imayaprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import inc.smart.solutions.imayaprofile.utils.Typewriter;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Typewriter tvSlogan = findViewById(R.id.tvSlogan);
        tvSlogan.setCharacterDelay(50);
        tvSlogan.animateText(getResources().getString(R.string.slogan));

        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(getResources().getString(R.string.slogan).length() * 50 + 1000);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();

                } catch (Exception ignored) {}
            }
        };
        background.start();
    }
}
