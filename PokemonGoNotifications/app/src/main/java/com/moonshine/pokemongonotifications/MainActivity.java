package com.moonshine.pokemongonotifications;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.moonshine.pokemongonotifications.Utils.UserPreferences;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.auth.GoogleLogin;
import com.pokegoapi.auth.PtcLogin;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    PokemonGo go = null;

    private static final float GRANULARITY = 0.0008f;
    private static final float SEARCH_RANGE = 0.005f;

    Location        mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        AsyncTask<Object,Object,Object>asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                OkHttpClient httpClient = new OkHttpClient();
                RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo auth = new PtcLogin(httpClient).login(UserPreferences.getToken(MainActivity.this));
                PokemonGo pokemonGo = null;
                try {
                    pokemonGo = new PokemonGo(auth,httpClient);
                } catch (LoginFailedException e) {
                    e.printStackTrace();
                } catch (RemoteServerException e) {
                    e.printStackTrace();
                }
//                if (pokemonGo != null) {
//                    Log.d("Profile Info: ", pokemonGo.getPlayerProfile().toString());
//                }
                return null;
            }
        };
//        asyncTask.execute();
        mLastLocation = new Location("");
        mLastLocation.setLatitude(52.5196119d);//your coords of course
        mLastLocation.setLongitude(6.4204943d);
        setupMenu();
        getPokemon();
    }

    private void getPokemon(){
        Log.d("getPokemon","trying to get catchable Pokemon");
        AsyncTask<Object,Object,Object>asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                /*try {
                    List<Point> spawnPoints = pokemonGo.getMap().getSpawnPoints();
                    mMapWrapperFragment.setSpawnPoints(spawnPoints);
                }catch(Exception e){
                    Log.d("Error spawnPoints:",e.getMessage());
                }*/

                float lat=-SEARCH_RANGE;
                float lon = -SEARCH_RANGE;
                List<CatchablePokemon> pokeList=null;
                Location loc = new Location(mLastLocation);
                OkHttpClient httpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                String token = UserPreferences.getToken(MainActivity.this);
                RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo auth = new PtcLogin(httpClient).login(UserPreferences.getToken(MainActivity.this));
                PokemonGo pokemonGo = null;
                try {
                    pokemonGo = new PokemonGo(auth,httpClient);
                } catch (LoginFailedException e) {
                    e.printStackTrace();
                    return null;
                } catch (RemoteServerException e) {
                    e.printStackTrace();
                    return null;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return null;
                }
                while(lat<SEARCH_RANGE) {
                    loc.setLatitude(mLastLocation.getLatitude()+lat);
                    while (lon < SEARCH_RANGE){
                        loc.setLongitude(mLastLocation.getLongitude()+lon);
                        pokemonGo.setLocation(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
                        try {
                            pokeList = pokemonGo.getMap().getCatchablePokemon();
                            Thread.sleep(100);
                        } catch (Exception e) {
                            Log.d("Error pokeFetch:", e.getMessage());
                        }

                        if (pokeList != null) {
                            for (CatchablePokemon pokemon : pokeList){
                                Log.d("Catchable", "poke on lat " + loc.getLatitude() + " lon:" + loc.getLongitude()+" poke:"+pokemon.getPokemonId());
                            }
//                            Log.d("Catchable", "poke on lat " + loc.getLatitude() + " lon:" + loc.getLongitude()+" poke:"+pokeList.size());
//                            mMapWrapperFragment.setPokemonMarkers(pokeList);
                        }
                        lon+=GRANULARITY;
                    }
                    lon=-SEARCH_RANGE;
                    lat+=GRANULARITY;
                }
                return null;
            }
        };
        asyncTask.execute();
    }

    private void setupMenu(){
        TextView username = (TextView) findViewById(R.id.loggedInAsTextView);
        if(username != null){
            username.setText(UserPreferences.getUsername(this));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tracker) {
            // Handle the camera action
        } else if (id == R.id.nav_tracker_rare) {

        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.nav_logout) {
            UserPreferences.clearPreferences(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
