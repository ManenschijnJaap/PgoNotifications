package com.moonshine.pokemongonotifications.auth;

import com.pokegoapi.auth.GoogleAuthJson;
import com.pokegoapi.auth.GoogleAuthTokenJson;
import com.pokegoapi.auth.GoogleLogin;
import com.pokegoapi.exceptions.LoginFailedException;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.util.Log;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import okhttp3.OkHttpClient;

/**
 * Created by jaapmanenschijn on 22/07/16.
 */
public class GoogleLoginExtension extends GoogleLogin {
    public interface GoogleLoginListener{
        public void openConsentUrl(String url, String codeToEnter);
    }

    GoogleLoginListener listener;
    public GoogleLoginExtension(OkHttpClient client, GoogleLoginListener listener) {
        super(client);
        this.client = client;
        this.listener = listener;
    }

    public static final String SECRET = "NCjF1TLi2CcY6t5mt0ZveuL7";
    public static final String CLIENT_ID = "848232511240-73ri3t7plvk96pj4f85uj8otdat2alem.apps.googleusercontent.com";
    public static final String OAUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/device/code";
    public static final String OAUTH_TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v4/token";
    private static final String TAG = GoogleLogin.class.getSimpleName();

    private final OkHttpClient client;


    /**
     * Returns an AuthInfo object given a token, this should not be an access token but rather an id_token
     *
     * @param token the id_token stored from a previous oauth attempt.
     * @return AuthInfo a AuthInfo proto structure to be encapsulated in server requests
     */
    public AuthInfo login(String token) {
        AuthInfo.Builder builder = AuthInfo.newBuilder();
        builder.setProvider("google");
        builder.setToken(AuthInfo.JWT.newBuilder().setContents(token).setUnknown2(59).build());
        return builder.build();
    }

    /**
     * Starts a login flow for google using a username and password, this uses googles device oauth endpoint,
     * a URL and code is displayed, not really ideal right now.
     *
     * @param username Google username
     * @param password Google password
     * @return AuthInfo a AuthInfo proto structure to be encapsulated in server requests
     */
    public AuthInfo login(String username, String password) throws LoginFailedException {
        try {
            HttpUrl url = HttpUrl.parse(OAUTH_ENDPOINT).newBuilder()
                    .addQueryParameter("client_id", CLIENT_ID)
                    .addQueryParameter("scope", "openid email https://www.googleapis.com/auth/userinfo.email")
                    .build();

            //Create empty body
            RequestBody reqBody = RequestBody.create(null, new byte[0]);

            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", reqBody)
                    .build();

            Response response = client.newCall(request).execute();

            Gson gson = new GsonBuilder().create();

            GoogleAuthJson googleAuth = gson.fromJson(response.body().string(), GoogleAuthJson.class);
            Log.d(TAG, "Get user to go to:"
                    + googleAuth.getVerificationUrl()
                    + " and enter code:" + googleAuth.getUserCode());
            if(listener != null){
                listener.openConsentUrl(googleAuth.getVerificationUrl(), googleAuth.getUserCode());
            }

            GoogleAuthTokenJson token;
            while ((token = poll(googleAuth)) == null) {
                Thread.sleep(googleAuth.getInterval() * 1000);
            }



            Log.d(TAG, "Got token: " + token.getIdToken());

            AuthInfo.Builder authbuilder = AuthInfo.newBuilder();
            authbuilder.setProvider("google");
            authbuilder.setToken(AuthInfo.JWT.newBuilder().setContents(token.getIdToken()).setUnknown2(59).build());

            return authbuilder.build();
        } catch (Exception e) {
            throw new LoginFailedException();
        }

    }


    private GoogleAuthTokenJson poll(GoogleAuthJson json) throws URISyntaxException, IOException {
        HttpUrl url = HttpUrl.parse(OAUTH_TOKEN_ENDPOINT).newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", SECRET)
                .addQueryParameter("code", json.getDeviceCode())
                .addQueryParameter("grant_type", "http://oauth.net/grant_type/device/1.0")
                .addQueryParameter("scope", "openid email https://www.googleapis.com/auth/userinfo.email")
                .build();

        //Empty request body
        RequestBody reqBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", reqBody)
                .build();

        Response response = client.newCall(request).execute();

        Gson gson = new GsonBuilder().create();
        GoogleAuthTokenJson token = gson.fromJson(response.body().string(), GoogleAuthTokenJson.class);

        if (token.getError() == null) {
            return token;
        } else {
            return null;
        }

    }


}
