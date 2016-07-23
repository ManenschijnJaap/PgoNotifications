package com.moonshine.pokemongonotifications.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.moonshine.pokemongonotifications.R;
import com.moonshine.pokemongonotifications.Utils.UserPreferences;
import com.moonshine.pokemongonotifications.adapters.NotificationsGridAdapter;
import com.moonshine.pokemongonotifications.model.NotificationPokemon;
import com.moonshine.pokemongonotifications.view.CheckableLayout;


public class NotificationPreferenceFragment extends Fragment {

    public NotificationPreferenceFragment() {
        // Required empty public constructor
    }

    public static NotificationPreferenceFragment newInstance() {
        NotificationPreferenceFragment fragment = new NotificationPreferenceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_preference, container, false);
        GridView mGrid = (GridView) view.findViewById(R.id.pokeGrid);
        final NotificationsGridAdapter mAdapter = new NotificationsGridAdapter(getContext());
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationPokemon pkmn = (NotificationPokemon) mAdapter.getItem(position);
                pkmn.setSelected(!pkmn.isSelected());
                if(view instanceof CheckableLayout){
                    CheckableLayout layout = (CheckableLayout) view;
                    layout.setChecked(pkmn.isSelected());
                }

                String ids = "";
                for (NotificationPokemon pokemon : mAdapter.getNotificationPokemons()){
                    if(pokemon.isSelected()){
                        if(ids.isEmpty()){
                            ids += pokemon.getId()+"";
                        }else{
                            ids += ","+pokemon.getId();
                        }
                    }
                }
                UserPreferences.updateNotificationIds(getContext(), ids);
            }
        });

        mGrid.setAdapter(mAdapter);
        return view;
    }
}
