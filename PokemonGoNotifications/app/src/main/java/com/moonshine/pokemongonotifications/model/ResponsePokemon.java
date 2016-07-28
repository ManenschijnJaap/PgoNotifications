package com.moonshine.pokemongonotifications.model;

/**
 * Created by jaapmanenschijn on 26/07/16.
 */
public class ResponsePokemon {
    private PokemonKey key;

    private long encounterId;

    private String pokemonName;

    private boolean wasReturnedToUser;

    private long expirationTimestamp;

    private double latitude;

    private double longitude;

    public PokemonKey getKey() {
        return key;
    }

    public void setKey(PokemonKey key) {
        this.key = key;
    }

    public long getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(long encounterId) {
        this.encounterId = encounterId;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public void setPokemonName(String pokemonName) {
        this.pokemonName = pokemonName;
    }

    public boolean isWasReturnedToUser() {
        return wasReturnedToUser;
    }

    public void setWasReturnedToUser(boolean wasReturnedToUser) {
        this.wasReturnedToUser = wasReturnedToUser;
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
}
