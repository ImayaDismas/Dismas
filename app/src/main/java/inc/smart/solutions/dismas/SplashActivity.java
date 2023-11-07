package inc.smart.solutions.dismas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import inc.smart.solutions.dismas.databinding.ActivityMainBinding;
import inc.smart.solutions.dismas.databinding.ActivitySplashBinding;
import inc.smart.solutions.dismas.utils.Typewriter;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.tvSlogan.setCharacterDelay(50);
        binding.tvSlogan.animateText(getResources().getString(R.string.slogan));

        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(getResources().getString(R.string.slogan).length() * 50 + 1000);
//                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                    finish();

                } catch (Exception ignored) {}
            }
        };
        background.start();
    }
}
