package com.moonshine.pokemongonotifications.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.moonshine.pokemongonotifications.GoogleAuthActivity;
import com.moonshine.pokemongonotifications.MainActivity;
import com.moonshine.pokemongonotifications.Utils.UserPreferences;
import com.moonshine.pokemongonotifications.network.GoogleManager;
import com.moonshine.pokemongonotifications.network.GoogleService;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import okhttp3.OkHttpClient;

public class RefreshTokenService extends Service {
    private GoogleManager mGoogleManager;
    private GoogleManager.CallBack mCallbackGoogle;
    private static String TAG = "RefreshTokenService";
    private AsyncTask<Void, Void, Void> refresher;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mGoogleManager == null){
            mGoogleManager = GoogleManager.getInstance();
        }
        if(mCallbackGoogle == null){
            mCallbackGoogle = new GoogleManager.CallBack() {
                @Override
                public void authSuccessful(String authToken, String refreshToken) {
//                    showProgress(false);
                    Log.d(TAG, "authSuccessful() called with: authToken = [" + authToken + "]");
                    UserPreferences.saveToken(getApplicationContext(), authToken);
                    UserPreferences.setLoginType(getApplicationContext(), "google");
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
//                        MainActivity.start(LoginActivity.this, authToken, MainActivity.PROVIDER_GOOGLE);
                    stopSelf();
                }

                @Override
                public void authFailed(String message) {
//                    showProgress(false);
                    Log.d(TAG, "authFailed() called with: message = [" + message + "]");
//                    Snackbar.make((View)mLoginFormView.getParent(), "Google Login Failed", Snackbar.LENGTH_LONG).show();
                    stopSelf();
                }

                @Override
                public void authRequested(GoogleService.AuthRequest body) {
//                    GoogleAuthActivity.startForResult(LoginActivity.this, REQUEST_USER_AUTH,
//                            body.getVerificationUrl(), body.getUserCode());
//                    mDeviceCode = body.getDeviceCode();
                    Log.d(TAG, "authRequested(), this is not going to work sadly!");
                    stopSelf();
                }
            };
        }
        refreshToken();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(refresher != null) {
            refresher.cancel(true);
        }
    }

    private void refreshToken(){
        refresher = new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
//                mGoogleManager.requestToken(UserPreferences.getDeviceCode(getApplicationContext()), mCallbackGoogle);
                mGoogleManager.refreshToken(UserPreferences.getRefreshToken(getApplicationContext()), mCallbackGoogle);
                return null;
            }

        };
        refresher.execute();
    }
}
