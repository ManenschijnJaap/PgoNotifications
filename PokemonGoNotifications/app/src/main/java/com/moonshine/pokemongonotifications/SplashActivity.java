package com.moonshine.pokemongonotifications;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moonshine.pokemongonotifications.Utils.UserPreferences;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(UserPreferences.getRefreshToken(SplashActivity.this) != null || (UserPreferences.getUsername(SplashActivity.this) != null && UserPreferences.getPassword(SplashActivity.this) != null)){
                    //show main activity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else{
                    //show login activity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 2500);
    }
}
