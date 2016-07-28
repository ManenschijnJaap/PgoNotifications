package com.moonshine.pokemongonotifications.network;

import android.content.Context;
import android.location.Location;

import com.moonshine.pokemongonotifications.Utils.UserPreferences;
import com.moonshine.pokemongonotifications.model.LoginResponse;
import com.moonshine.pokemongonotifications.model.ResponsePokemon;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jaapmanenschijn on 26/07/16.
 */
public class RestClient {
    private static RestClient instance;
    private PGoApiService service;

    private RestClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new NetworkRequestLoggingInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pkmngoapi.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(PGoApiService.class);
    }

    public static RestClient getInstance(){
        if (instance == null){
            instance = new RestClient();
        }
        return instance;
    }

    public Call<PokemonResponse> getPokemon(Context context){
        String deviceId = UserPreferences.getUniqueId(context);
        Call<PokemonResponse> call = service.getPokemon(deviceId, System.currentTimeMillis());
        return call;
    }

    public Call<Void> startFetchingPokemon(Location location, Context context){
        String deviceId = UserPreferences.getUniqueId(context);
        String type = UserPreferences.getLoginType(context);
        String username = UserPreferences.getUsername(context);
        String password = UserPreferences.getPassword(context);
        String token = UserPreferences.getToken(context);
        Call<Void> call = null;
        if(type.equalsIgnoreCase("ptc")){
            call = service.startFetchingPokemonWithPtc(deviceId, username, password, location.getLatitude(), location.getLongitude(), location.getAltitude(), System.currentTimeMillis());
        }else{
            call = service.startFetchingPokemonWithGoogle(deviceId, token, location.getLatitude(), location.getLongitude(), location.getAltitude(), System.currentTimeMillis());
        }
        return call;
    }

    public Call<LoginResponse> getLoginUrl(){
        return service.getLoginUrl();
    }

    public Call<LoginResponse> login(String access){
        return service.loginWithGoogle(access);
    }
}
