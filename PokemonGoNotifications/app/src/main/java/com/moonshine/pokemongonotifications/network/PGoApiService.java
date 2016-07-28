package com.moonshine.pokemongonotifications.network;

import com.moonshine.pokemongonotifications.model.LoginResponse;
import com.moonshine.pokemongonotifications.model.ResponsePokemon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jaapmanenschijn on 26/07/16.
 */
public interface PGoApiService {
    @GET("pokemon")
    Call<PokemonResponse> getPokemon(@Query("deviceId")String deviceId, @Query("timestamp")long timestamp);

    @GET("fetch/ptc")
    Call<Void> startFetchingPokemonWithPtc(@Query("deviceId")String deviceId, @Query("username")String uname, @Query("password")String pw, @Query("latitude")double latitude, @Query("longitude")double longitude, @Query("altitude")double altitude, @Query("timestamp")long timestamp);

    @GET("fetch/google")
    Call<Void> startFetchingPokemonWithGoogle(@Query("deviceId")String deviceId, @Query("refreshToken")String refreshToken, @Query("latitude")double latitude, @Query("longitude")double longitude, @Query("altitude")double altitude, @Query("timestamp")long timestamp);

    @GET("google/login/url")
    Call<LoginResponse> getLoginUrl();

    @GET("google/login")
    Call<LoginResponse> loginWithGoogle(@Query("access")String access);

}
