package com.moonshine.pokemongonotifications.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.moonshine.pokemongonotifications.Utils.PokemonUtils;
import com.moonshine.pokemongonotifications.Utils.UserPreferences;
import com.moonshine.pokemongonotifications.model.NotificationPokemon;
import com.moonshine.pokemongonotifications.view.CheckableLayout;
import com.pokegoapi.api.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaapmanenschijn on 23/07/16.
 */
public class NotificationsGridAdapter extends BaseAdapter {
    private static final Long AMOUNT_OF_POKEMON = 151l;
    private List<NotificationPokemon> notificationPokemons;

    public NotificationsGridAdapter(Context context){
        setupPokemonList(context);
    }

    public List<NotificationPokemon> getNotificationPokemons() {
        return notificationPokemons;
    }

    private void setupPokemonList(Context context){
        notificationPokemons = new ArrayList<>();
        List<Long> selectedPokemon = PokemonUtils.convertStringToList(UserPreferences.getNotificationIds(context));
        for(Long i = 1l; i <= AMOUNT_OF_POKEMON; i++){
            NotificationPokemon pkmn = new NotificationPokemon();
            pkmn.setId(i);
            pkmn.setImageId(PokemonUtils.getResourceId(context, "p"+i, "drawable", context.getPackageName()));
            pkmn.setSelected(selectedPokemon.contains(pkmn.getId()));
            notificationPokemons.add(pkmn);
        }
    }






    @Override
    public int getCount() {
        return notificationPokemons.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationPokemons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckableLayout l;
        ImageView i;

        if (convertView == null) {
            int px = PokemonUtils.dpToPixels(parent.getContext(), 100);
            i = new ImageView(parent.getContext());
            i.setScaleType(ImageView.ScaleType.
            FIT_XY);
            i.setLayoutParams(new ViewGroup.LayoutParams(px, px));
            l = new CheckableLayout(parent.getContext());
            l.setLayoutParams(new GridView.LayoutParams(px, px));
            l.addView(i);
        } else {
            l = (CheckableLayout) convertView;
            i = (ImageView) l.getChildAt(0);
        }
        NotificationPokemon pkmn = (NotificationPokemon) getItem(position);
        l.setChecked(pkmn.isSelected());
        i.setImageResource(pkmn.getImageId());
        return l;
    }
}
