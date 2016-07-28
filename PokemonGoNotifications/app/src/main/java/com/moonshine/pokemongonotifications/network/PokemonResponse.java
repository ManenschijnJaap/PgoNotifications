package com.moonshine.pokemongonotifications.network;

import com.moonshine.pokemongonotifications.model.ResponsePokemon;

import java.util.List;

/**
 * Created by jaapmanenschijn on 28/07/16.
 */
public class PokemonResponse {
    private List<ResponsePokemon> pokemon;
    private boolean findingPokemon;

    public List<ResponsePokemon> getPokemon() {
        return pokemon;
    }

    public void setPokemon(List<ResponsePokemon> pokemon) {
        this.pokemon = pokemon;
    }

    public boolean isFindingPokemon() {
        return findingPokemon;
    }

    public void setFindingPokemon(boolean findingPokemon) {
        this.findingPokemon = findingPokemon;
    }
}
