package com.moonshine.pokemongonotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.moonshine.pokemongonotifications.fragments.SettingsFragment;
import com.moonshine.pokemongonotifications.fragments.TrackerFragment;
import com.moonshine.pokemongonotifications.model.DbPokemon;
import com.moonshine.pokemongonotifications.receivers.PokemonReceiver;
import com.moonshine.pokemongonotifications.receivers.TokenRefreshReceiver;
import com.moonshine.pokemongonotifications.services.RefreshTokenService;
import com.moonshine.pokemongonotifications.services.ScanService;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int LOCATION_REQUEST = 703;

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
        setTitle("Track all pokemon");
        mLastLocation = new Location("");
        mLastLocation.setLatitude(52.5196119d);//your coords of course
        mLastLocation.setLongitude(6.4204943d);
        checkPermissions();
        if (UserPreferences.getLoginType(this).equalsIgnoreCase("google")){
            periodicallyRefreshToken();
        }
    }

    BroadcastReceiver intervalChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopAlarm();
            stopServices();
            if(UserPreferences.isScanEnabled(MainActivity.this)) {
                periodicallyRefreshToken();
                periodicallyStartService();
            }
        }
    };

    BroadcastReceiver startStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserPreferences.isScanEnabled(MainActivity.this)){
                //enabled scan
                periodicallyRefreshToken();
                periodicallyStartService();
            }else{
                stopAlarm();
                stopServices();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(intervalChangeReceiver, new IntentFilter("com.moonshine.intervalChanged"));
        registerReceiver(startStopReceiver, new IntentFilter("com.moonshine.startStopChanged"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(intervalChangeReceiver);
        unregisterReceiver(startStopReceiver);
    }

    private void periodicallyRefreshToken(){
        Intent intent = new Intent(MainActivity.this, TokenRefreshReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        long frequency= 50 * 60 * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST);
        }else{
            if(UserPreferences.isScanEnabled(this)) {
                periodicallyStartService();
            }
        }
    }

    private void periodicallyStartService(){
        Intent intent = new Intent(MainActivity.this, PokemonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        long frequency= UserPreferences.getInterval(this) * 60 * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
        startService(new Intent(this, ScanService.class));
    }

    private void stopAlarm(){
        Intent intent = new Intent(MainActivity.this, PokemonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent2 = new Intent(MainActivity.this, TokenRefreshReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(MainActivity.this, 0, intent2, 0);

        alarmManager.cancel(pendingIntent);
        alarmManager.cancel(pendingIntent2);
    }

    private void stopServices(){
        stopService(new Intent(this, RefreshTokenService.class));
        stopService(new Intent(this, ScanService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if(UserPreferences.isScanEnabled(this)) {
                        periodicallyStartService();
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
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
            setTitle("Track all pokemon");
            showFragment(TrackerFragment.newInstance());
        } else if (id == R.id.nav_tracker_rare) {
            setTitle("Track wanted pokemon");
            showFragment(RareTrackerFragment.newInstance());
        } else if (id == R.id.nav_notifications) {
            setTitle("Notifications");
            showFragment(NotificationPreferenceFragment.newInstance());
        } else if (id == R.id.nav_logout) {
            UserPreferences.clearPreferences(this);
            stopAlarm();
            stopServices();
            List<DbPokemon> storedPokemons = SQLite.select()
                    .from(DbPokemon.class)
                    .queryList();
            for(DbPokemon pkmn : storedPokemons){
                pkmn.delete();
            }
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_settings) {
            setTitle("Settings");
            showFragment(SettingsFragment.newInstance());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
