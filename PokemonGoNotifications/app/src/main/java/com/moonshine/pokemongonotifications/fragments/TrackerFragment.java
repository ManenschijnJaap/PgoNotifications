package com.moonshine.pokemongonotifications.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.moonshine.pokemongonotifications.R;
import com.moonshine.pokemongonotifications.Utils.PokemonUtils;
import com.moonshine.pokemongonotifications.adapters.TrackerAdapter;
import com.moonshine.pokemongonotifications.model.DbPokemon;

import java.util.List;

public class TrackerFragment extends Fragment {

    private GridView mGrid;
    private Handler mHandler;
    private int mInterval = 5000;

    public TrackerFragment() {
        // Required empty public constructor
    }

    public static TrackerFragment newInstance() {
        TrackerFragment fragment = new TrackerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        startUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdates();
    }

    private void startUpdates(){
        if(mHandler == null){
            mHandler = new Handler();
        }
        mStatusChecker.run();
    }

    private void stopUpdates(){
        if(mHandler != null){
            mHandler.removeCallbacks(mStatusChecker);
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateGrid(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);
        mGrid = (GridView) view.findViewById(R.id.pokeGrid);
        mGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final DbPokemon pkmn = (DbPokemon) mGrid.getAdapter().getItem(position);
                new AlertDialog.Builder(getContext()).setTitle("Alert").setMessage("Are you sure you want to ignore this "+pkmn.getPokemonName()+"?\nBy doing so, this pokemon won't show up in your tracker anymore!").setNegativeButton("Cancel", null).setPositiveButton("Ignore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pkmn.setIgnored(true);
                        pkmn.save();
                        stopUpdates();
                        startUpdates();
                    }
                }).show();
                return false;
            }
        });
        return view;
    }

    private void updateGrid(){
        List<DbPokemon> trackedPkmn = PokemonUtils.getNearbyPokemon(getContext(), 10, false);
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final Location lastKnownLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastKnownLocation != null) {
            mGrid.setAdapter(new TrackerAdapter(getContext(), trackedPkmn, lastKnownLocation));
        }
    }


}
