package com.moonshine.pokemongonotifications.model;

/**
 * Created by jaapmanenschijn on 26/07/16.
 */
public class PokemonKey {
    private String deviceId;

    private String spawnPointId;

    private int pokemonId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSpawnPointId() {
        return spawnPointId;
    }

    public void setSpawnPointId(String spawnPointId) {
        this.spawnPointId = spawnPointId;
    }

    public int getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(int pokemonId) {
        this.pokemonId = pokemonId;
    }
}
