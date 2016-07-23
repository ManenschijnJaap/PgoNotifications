package com.moonshine.pokemongonotifications.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moonshine.pokemongonotifications.services.RefreshTokenService;
import com.moonshine.pokemongonotifications.services.ScanService;

public class TokenRefreshReceiver extends BroadcastReceiver {
    public TokenRefreshReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RefreshTokenService.class));
    }
}
