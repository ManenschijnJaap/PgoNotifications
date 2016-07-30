package com.moonshine.pokemongonotifications.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;

import com.moonshine.pokemongonotifications.model.DbPokemon;
import com.moonshine.pokemongonotifications.model.DbPokemon_Table;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jaapmanenschijn on 23/07/16.
 */
public class PokemonUtils {
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getResourceId(Context context, String pVariableName, String pResourcename, String pPackageName) {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int dpToPixels(Context context, int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    public static List<Long> convertStringToList(String ids) {
        ArrayList<Long> list = new ArrayList<>();
        if (!ids.isEmpty()) {
            String[] seperated = ids.split(",");
            if (seperated.length > 0) {
                for (int i = 0; i < seperated.length; i++) {
                    Long id = Long.valueOf(seperated[i]);
                    list.add(id);
                }
            }
        }
        return list;
    }

    /**
     * Retrieves the list of nearby pokemon, sorted on closest first.
     * @param context Context, needed for internal methods
     * @param amount The amount of pokemon you want to get
     * @param filtered Indicate if you want the list to be filtered or not. If filtered, it will only show pokemon you want a notification for
     * @return The list of pokemon
     */
    public static List<DbPokemon> getNearbyPokemon(Context context, int amount, boolean filtered) {
        List<DbPokemon> storedPokemons = SQLite.select()
                .from(DbPokemon.class)
                .where(DbPokemon_Table.ignored.eq(false))
                .queryList();

        //Let's clear the list first of any pokemon that have expired
        for (DbPokemon pkmn : storedPokemons) {
            if (pkmn.getExpirationTimestamp() <= System.currentTimeMillis()) {
                pkmn.delete();
            }
        }
        storedPokemons = SQLite.select()
                .from(DbPokemon.class)
                .queryList();
        List<DbPokemon> wantedPokemons = null;
        if (filtered) {
            List<Long> selectedPokemon = PokemonUtils.convertStringToList(UserPreferences.getNotificationIds(context));
            wantedPokemons = new ArrayList<>();
            for (DbPokemon pkmn : storedPokemons) {
                if (!pkmn.isIgnored() && selectedPokemon.contains(Long.parseLong(pkmn.getPokemonId() + ""))) {
                    wantedPokemons.add(pkmn);
                }
            }
        } else {
            wantedPokemons = new ArrayList<>();
            for (DbPokemon pkmn : storedPokemons) {
                if (!pkmn.isIgnored()) {
                        wantedPokemons.add(pkmn);
                }
            }
        }
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(wantedPokemons.size() <= amount){
                return wantedPokemons;
            }
            return new ArrayList<DbPokemon>(wantedPokemons.subList(0, amount - 1));
        }
        final Location lastKnownLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Collections.sort(wantedPokemons, new Comparator<DbPokemon>() {
            @Override
            public int compare(DbPokemon lhs, DbPokemon rhs) {
                if (lhs.getLocation() == null && rhs.getLocation() != null){
                    return 1;
                }
                if (lhs.getLocation() != null && rhs.getLocation() == null){
                    return -1;
                }
                if (lhs.getLocation().distanceTo(lastKnownLocation) < rhs.getLocation().distanceTo(lastKnownLocation)){
                    return -1;
                }else if(lhs.getLocation().distanceTo(lastKnownLocation) > rhs.getLocation().distanceTo(lastKnownLocation)){
                    return 1;
                }
                return 0;
            }
        });
        if(wantedPokemons.size() <= amount){
            return wantedPokemons;
        }
        return new ArrayList<DbPokemon>(wantedPokemons.subList(0, amount));
    }

    public static BearingDirection getBearingDirection(float degrees){
        if (degrees < 0){
            degrees = 360 + degrees;
        }
        if (degrees >= 337.5 && degrees <= 22.5){
            return BearingDirection.NORTH;
        }
        if (degrees >= 22.5 && degrees <= 67.5){
            return BearingDirection.NORTHEAST;
        }
        if (degrees >= 67.5 && degrees <= 112.5){
            return BearingDirection.EAST;
        }
        if (degrees >= 112.5 && degrees <= 157.5){
            return BearingDirection.SOUTHEAST;
        }
        if (degrees >= 157.5 && degrees <= 202.5){
            return BearingDirection.SOUTH;
        }
        if (degrees >= 202.5 && degrees <= 247.5){
            return BearingDirection.SOUTHWEST;
        }
        if (degrees >= 247.5 && degrees <= 292.5){
            return BearingDirection.WEST;
        }
        return BearingDirection.NORTHWEST;
    }

    public static Drawable getRotateDrawable(final Drawable d, final float angle) {
        final Drawable[] arD = { d };
        return new LayerDrawable(arD) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, d.getBounds().width() / 2, d.getBounds().height() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }
}
