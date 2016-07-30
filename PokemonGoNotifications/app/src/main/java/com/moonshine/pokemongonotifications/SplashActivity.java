package com.moonshine.pokemongonotifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moonshine.pokemongonotifications.Utils.UserPreferences;
import com.moonshine.pokemongonotifications.network.RestClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        RestClient.getInstance().checkVersion().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    new android.app.AlertDialog.Builder(SplashActivity.this).setTitle("Wrong version").setMessage("This version of the app is not supported. Please update it!").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
                }else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (UserPreferences.getToken(SplashActivity.this) != null || (UserPreferences.getUsername(SplashActivity.this) != null && UserPreferences.getPassword(SplashActivity.this) != null)) {
                                //show main activity
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            } else {
                                //show login activity
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    }, 2500);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
//                new AlertDialog.Builder(SplashActivity.this).setTitle("Wrong version").setMessage("This version of the app is not supported. Please update it!").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                }).show();
            }
        });

    }
}
