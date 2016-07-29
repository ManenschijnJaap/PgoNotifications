package com.moonshine.pokemongonotifications.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.moonshine.pokemongonotifications.R;
import com.moonshine.pokemongonotifications.Utils.UserPreferences;

public class SettingsFragment extends Fragment {
    private SeekBar seekBar;
    private Button saveButton;
    private Button scanToggleButton;
    private TextView minuteIndicator;

    int step = 1;
    int max = 10;
    int min = 3;
    int value;


    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        saveButton = (Button) view.findViewById(R.id.save);
        scanToggleButton = (Button) view.findViewById(R.id.scanToggle);
        minuteIndicator = (TextView) view.findViewById(R.id.minutes);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = min + (progress * step);
                minuteIndicator.setText("Refresh every: "+value+" minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        int value = UserPreferences.getInterval(getContext());
        seekBar.setProgress((value - min) / step);
        minuteIndicator.setText("Refresh every: "+value+" minutes");

        boolean scanEnabled = UserPreferences.isScanEnabled(getContext());
        if(scanEnabled){
            scanToggleButton.setText("Stop automatic scan");
        }else{
            scanToggleButton.setText("Start automatic scan");
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO send broadcast to indicate settings have changed
                UserPreferences.saveInterval(getContext(), min + (seekBar.getProgress() * step));
                getContext().sendBroadcast(new Intent("com.moonshine.intervalChanged"));
            }
        });
        scanToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO send broadcast to indicate settings have changed
                boolean scanEnabled = UserPreferences.isScanEnabled(getContext());
                UserPreferences.changeScanEnabled(getContext(), !scanEnabled);
                if(!scanEnabled){
                    scanToggleButton.setText("Stop automatic scan");
                }else{
                    scanToggleButton.setText("Start automatic scan");
                }
                getContext().sendBroadcast(new Intent("com.moonshine.startStopChanged"));
            }
        });



        return view;
    }

}
