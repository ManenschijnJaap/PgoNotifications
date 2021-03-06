package com.moonshine.pokemongonotifications.network;

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chris on 7/21/2016.
 */
public class GoogleManager {
    private static final String TAG = "GoogleManager";

    private static GoogleManager ourInstance = new GoogleManager();

    private static final String BASE_URL = "https://www.google.com";
    private static final String SECRET = "NCjF1TLi2CcY6t5mt0ZveuL7";
    private static final String CLIENT_ID = "848232511240-73ri3t7plvk96pj4f85uj8otdat2alem.apps.googleusercontent.com";
    private static final String OAUTH_TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v4/token";
    private static final String OAUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/device/code";

    private final OkHttpClient mClient;
    private final GoogleService mGoogleService;

    public static GoogleManager getInstance() {
        return ourInstance;
    }

    private GoogleManager() {
        mClient = new OkHttpClient.Builder()
                .addInterceptor(new NetworkRequestLoggingInterceptor())
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

        mGoogleService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mClient)
                .build()
                .create(GoogleService.class);
    }

    public void authUser(final CallBack callBack) {
        HttpUrl url = HttpUrl.parse(OAUTH_ENDPOINT).newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("scope", "openid email https://www.googleapis.com/auth/userinfo.email")
                .build();

        Callback<GoogleService.AuthRequest> googleCallback = new Callback<GoogleService.AuthRequest>() {
            @Override
            public void onResponse(Call<GoogleService.AuthRequest> call, Response<GoogleService.AuthRequest> response) {
                GoogleService.AuthRequest body = response.body();
                callBack.authRequested(body);
            }

            @Override
            public void onFailure(Call<GoogleService.AuthRequest> call, Throwable t) {
                t.printStackTrace();
                callBack.authFailed("Failed on getting the information for the user auth");
            }
        };
        Call<GoogleService.AuthRequest> call = mGoogleService.requestAuth(url.toString());
        call.enqueue(googleCallback);
    }

    public void requestToken(String deviceCode, final CallBack callBack){
        HttpUrl url = HttpUrl.parse(OAUTH_TOKEN_ENDPOINT).newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", SECRET)
                .addQueryParameter("code", deviceCode)
                .addQueryParameter("grant_type", "http://oauth.net/grant_type/device/1.0")
                .addQueryParameter("scope", "openid email https://www.googleapis.com/auth/userinfo.email")
                .build();

        Callback<GoogleService.TokenResponse> googleCallback = new Callback<GoogleService.TokenResponse>() {
            @Override
            public void onResponse(Call<GoogleService.TokenResponse> call, Response<GoogleService.TokenResponse> response) {
                if(response != null && response.body() != null && response.body().getIdToken() != null) {
                    callBack.authSuccessful(response.body().getIdToken(), response.body().getRefreshToken());
                }else{
                    callBack.authFailed("Failed on requesting the id token");
                }
            }

            @Override
            public void onFailure(Call<GoogleService.TokenResponse> call, Throwable t) {
                t.printStackTrace();
                callBack.authFailed("Failed on requesting the id token");
            }
        };
        Call<GoogleService.TokenResponse> call = mGoogleService.requestToken(url.toString());
        call.enqueue(googleCallback);
    }

    public void refreshToken(String refreshToken, final CallBack callBack){
        HttpUrl url = HttpUrl.parse(OAUTH_TOKEN_ENDPOINT).newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", SECRET)
                .addQueryParameter("grant_type", "refresh_token")
                .addQueryParameter("refresh_token", refreshToken)
                .build();

        Callback<GoogleService.TokenResponse> googleCallback = new Callback<GoogleService.TokenResponse>() {
            @Override
            public void onResponse(Call<GoogleService.TokenResponse> call, Response<GoogleService.TokenResponse> response) {
                if(response != null && response.body() != null && response.body().getIdToken() != null) {
                    callBack.authSuccessful(response.body().getIdToken(), response.body().getRefreshToken());
                }else{
                    callBack.authFailed("Failed on requesting the id token");
                }
            }

            @Override
            public void onFailure(Call<GoogleService.TokenResponse> call, Throwable t) {
                t.printStackTrace();
                callBack.authFailed("Failed on requesting the id token");
            }
        };
        Call<GoogleService.TokenResponse> call = mGoogleService.requestToken(url.toString());
        call.enqueue(googleCallback);
    }

    public interface CallBack {
        void authSuccessful(String authToken, String refreshToken);
        void authFailed(String message);
        void authRequested(GoogleService.AuthRequest body);
    }
}
