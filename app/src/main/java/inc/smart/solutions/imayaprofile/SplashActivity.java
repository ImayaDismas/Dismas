package inc.smart.solutions.imayaprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(2*1000);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();

                } catch (Exception ignored) {}
            }
        };
        background.start();
    }
}
