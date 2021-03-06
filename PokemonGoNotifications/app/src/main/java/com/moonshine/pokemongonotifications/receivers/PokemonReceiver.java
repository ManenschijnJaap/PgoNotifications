package com.moonshine.pokemongonotifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moonshine.pokemongonotifications.services.ScanService;

public class PokemonReceiver extends BroadcastReceiver {
    public PokemonReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ScanService.class));
    }
}
