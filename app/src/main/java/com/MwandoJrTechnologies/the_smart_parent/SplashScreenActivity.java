package com.MwandoJrTechnologies.the_smart_parent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.MwandoJrTechnologies.the_smart_parent.Profile.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
