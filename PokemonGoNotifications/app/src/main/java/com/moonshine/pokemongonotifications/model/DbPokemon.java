package com.moonshine.pokemongonotifications.model;

import com.moonshine.pokemongonotifications.database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by jaapmanenschijn on 23/07/16.
 */
@Table(database = AppDatabase.class)
public class DbPokemon extends BaseModel {
    @Column
    @PrimaryKey
    private String spawnPointId;

    @Column
    private long encounterId;

    @Column
    private int pokemonId;

    @Column
    private String pokemonName;

    @Column
    private boolean hasShownNotification;

    @Column
    private long expirationTimestamp;

    @Column
    private double latitude;

    @Column
    private double longitude;

    public String getSpawnPointId() {
        return spawnPointId;
    }

    public void setSpawnPointId(String spawnPointId) {
        this.spawnPointId = spawnPointId;
    }

    public long getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(long encounterId) {
        this.encounterId = encounterId;
    }

    public int getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(int pokemonId) {
        this.pokemonId = pokemonId;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public boolean isHasShownNotification() {
        return hasShownNotification;
    }

    public void setHasShownNotification(boolean hasShownNotification) {
        this.hasShownNotification = hasShownNotification;
    }
}
