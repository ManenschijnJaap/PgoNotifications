package com.moonshine.pokemongonotifications;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.moonshine.pokemongonotifications.fragments.NotificationPreferenceFragment;
import com.moonshine.pokemongonotifications.fragments.RareTrackerFragment;
import com.moonshine.pokemongonotifications.fragments.TrackerFragment;
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
        navigationView.setCheckedItem(R.id.nav_tracker);

        showFragment(TrackerFragment.newInstance());
        mLastLocation = new Location("");
        mLastLocation.setLatitude(52.5196119d);//your coords of course
        mLastLocation.setLongitude(6.4204943d);
        getPokemon();
    }

    private void showFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
    }

    private RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo getAuth(OkHttpClient httpClient){

        String type = UserPreferences.getLoginType(this);
        if(type.equalsIgnoreCase("ptc")){
            return new PtcLogin(httpClient).login(UserPreferences.getToken(MainActivity.this));
        }else {
            return new GoogleLogin(httpClient).login(UserPreferences.getToken(MainActivity.this));
        }
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

                RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo auth = getAuth(httpClient);
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
            showFragment(TrackerFragment.newInstance());
        } else if (id == R.id.nav_tracker_rare) {
            showFragment(RareTrackerFragment.newInstance());
        } else if (id == R.id.nav_notifications) {
            showFragment(NotificationPreferenceFragment.newInstance());
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
