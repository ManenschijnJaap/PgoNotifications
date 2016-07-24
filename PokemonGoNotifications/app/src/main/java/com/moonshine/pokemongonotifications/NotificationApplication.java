package com.moonshine.pokemongonotifications;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by jaapmanenschijn on 23/07/16.
 */
public class NotificationApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this)
                .openDatabasesOnInit(true).build());
    }
}
