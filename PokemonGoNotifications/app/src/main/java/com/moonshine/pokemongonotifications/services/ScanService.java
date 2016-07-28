package com.moonshine.pokemongonotifications.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.moonshine.pokemongonotifications.R;
import com.moonshine.pokemongonotifications.Utils.PokemonUtils;
import com.moonshine.pokemongonotifications.Utils.UserPreferences;
import com.moonshine.pokemongonotifications.model.DbPokemon;
import com.moonshine.pokemongonotifications.model.ResponsePokemon;
import com.moonshine.pokemongonotifications.network.PokemonResponse;
import com.moonshine.pokemongonotifications.network.RestClient;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import okhttp3.OkHttpClient;
import retrofit2.Response;

/**
 * Created by jaapmanenschijn on 23/07/16.
 */
public class ScanService extends Service {
    private static final float GRANULARITY = 0.0008f;
    private static final float SEARCH_RANGE = 0.005f;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;


    private Location mLastLocation;
    private LocationListener mListener;
    private AsyncTask<Void, Void, Void> scanner;
    private boolean fetchingPokemon = true;

    public ScanService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                getPokemon();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mListener);
        } catch (java.lang.SecurityException e) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ee) {
//            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mListener);
            } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());

            }
        }





        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(scanner != null) {
            scanner.cancel(true);
        }
    }

    private void getPokemon(){
        scanner = new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                Location loc = new Location(mLastLocation);

                //TODO massive error handling!
                try {
                    Response<Void> response = RestClient.getInstance().startFetchingPokemon(loc, getApplicationContext()).execute();
                    if (response != null && response.isSuccessful()){
                        while(fetchingPokemon){
                            Response<PokemonResponse> pkmnResponse = RestClient.getInstance().getPokemon(getApplicationContext()).execute();
                            if(pkmnResponse != null && pkmnResponse.isSuccessful()){
                                if(pkmnResponse.body() != null){
                                    if(pkmnResponse.body().getPokemon() != null) {
                                        importPokemon(pkmnResponse.body().getPokemon());
                                    }
                                    fetchingPokemon = pkmnResponse.body().isFindingPokemon();
                                    if(fetchingPokemon){
                                        Thread.sleep(10000);
                                    }else{
                                        Log.e("TAG", "Yay, the check works!");
                                    }
                                }
                            }else{
                                Log.e("SCANSERVICE", "Error getting pokemon: "+pkmnResponse.errorBody().string());
                            }
                        }
                    }else{
                        Log.e("SCANSERVICE", "Error starting fetch: "+response.errorBody().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                stopSelf();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ScanService.this.stopSelf();
            }
        };
        scanner.execute();









    }

    private synchronized void importPokemon(List<ResponsePokemon> pkmns){
        //First, let's check if we need to remove any pokemon from the list
        long currentTime = System.currentTimeMillis();
        List<DbPokemon> storedPokemons = SQLite.select()
                .from(DbPokemon.class)
                .queryList();
        for (DbPokemon pkmn : storedPokemons){
            if (pkmn.getExpirationTimestamp() <= currentTime){
                pkmn.delete();
            }
        }

        //Now let's add any pokemon we found that is not in the DB yet
        storedPokemons = SQLite.select()
                .from(DbPokemon.class)
                .queryList();
        for(ResponsePokemon pkm : pkmns){
            if (pkm.getExpirationTimestamp() > 0) {
                boolean exists = false;
                for (DbPokemon dbPkmn : storedPokemons) {
                    if (dbPkmn.getSpawnPointId().equalsIgnoreCase(pkm.getKey().getSpawnPointId()) && dbPkmn.getEncounterId() == pkm.getEncounterId() && dbPkmn.getPokemonId() == pkm.getKey().getPokemonId()) {
                        exists = true;
                    }
                }
                if (!exists) {
                    DbPokemon dbPkmn = new DbPokemon();
                    dbPkmn.setEncounterId(pkm.getEncounterId());
                    dbPkmn.setExpirationTimestamp(pkm.getExpirationTimestamp());
                    dbPkmn.setLatitude(pkm.getLatitude());
                    dbPkmn.setLongitude(pkm.getLongitude());
                    dbPkmn.setPokemonId(pkm.getKey().getPokemonId());
                    dbPkmn.setSpawnPointId(pkm.getKey().getSpawnPointId());
                    dbPkmn.setPokemonName(pkm.getPokemonName());
                    dbPkmn.save();

                }
            }
        }

        //Now, we need to show a notification if we are interested!
        storedPokemons = SQLite.select()
                .from(DbPokemon.class)
                .queryList();
        List<Long> selectedPokemon = PokemonUtils.convertStringToList(UserPreferences.getNotificationIds(getApplicationContext()));
        for(DbPokemon pkmn : storedPokemons){
            if (!pkmn.isHasShownNotification() && selectedPokemon.contains(Long.parseLong(pkmn.getPokemonId()+""))){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pokemon_recovery);
                Notification notification = builder.setContentTitle("Pokemon Notification")
                        .setContentText("Found "+ pkmn.getPokemonName() +" in your area!")
                        .setTicker("New Message Alert!")
                        .setVibrate(new long[] { 0, 200, 150, 200, 150, 400 })
                        .setSound(soundUri)
                        .setSmallIcon(R.mipmap.ic_launcher).build();


                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(pkmn.getPokemonId(), notification);
                pkmn.setHasShownNotification(true);
                pkmn.save();
            }
        }

    }
}
