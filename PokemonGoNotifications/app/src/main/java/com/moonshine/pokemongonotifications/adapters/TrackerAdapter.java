package com.moonshine.pokemongonotifications.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.moonshine.pokemongonotifications.R;
import com.moonshine.pokemongonotifications.Utils.BearingDirection;
import com.moonshine.pokemongonotifications.Utils.PokemonUtils;
import com.moonshine.pokemongonotifications.model.DbPokemon;
import com.pokegoapi.api.pokemon.Pokemon;

import java.util.List;

/**
 * Created by jaapmanenschijn on 24/07/16.
 */
public class TrackerAdapter extends BaseAdapter {
    private Context mContext;
    private List<DbPokemon> pokemonList;
    private Location currentLocation;

    public TrackerAdapter(Context context, List<DbPokemon> pokemonList, Location location){
        super();
        this.mContext = context;
        this.pokemonList = pokemonList;
        this.currentLocation = location;
    }


    @Override
    public int getCount() {
        return pokemonList.size();
    }

    @Override
    public Object getItem(int position) {
        return pokemonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DbPokemon pkmn = (DbPokemon) getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracker_item, null);
        }
        ImageView pokemonImageView = (ImageView) convertView.findViewById(R.id.pokemonImage);
        pokemonImageView.setImageResource(PokemonUtils.getResourceId(parent.getContext(), "p"+pkmn.getPokemonId(), "drawable", parent.getContext().getPackageName()));
        configureIndicators(convertView, currentLocation, pkmn.getLocation());
        return convertView;
    }

    private void configureIndicators(View view, Location ownLocation, Location targetLocation){
        float distance = ownLocation.distanceTo(targetLocation);
        float bearing = ownLocation.bearingTo(targetLocation);
        ImageView iv = (ImageView) view.findViewById(R.id.pokemonImage);
        ImageView arrow = (ImageView) view.findViewById(R.id.compass);
        if(bearing < 0){
            bearing = bearing + 360;
        }
        arrow.setImageDrawable(PokemonUtils.getRotateDrawable(mContext.getResources().getDrawable(R.drawable.direction), bearing));

        if(distance < 50){
            iv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.green_bg));
        }else if(distance < 150){

            iv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orange_bg));
        }else{

            iv.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.red_bg));
        }

    }
}
