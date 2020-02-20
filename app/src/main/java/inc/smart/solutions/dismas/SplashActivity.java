package inc.smart.solutions.dismas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import inc.smart.solutions.dismas.utils.Typewriter;

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
