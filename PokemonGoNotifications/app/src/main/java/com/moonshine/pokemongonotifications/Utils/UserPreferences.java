package com.moonshine.pokemongonotifications.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jaapmanenschijn on 22/07/16.
 */
public class UserPreferences {
    private static String preferenceName = "PGoNotiPrefs";
    private static String tokenKey = "UserToken";
    private static String usernameKey = "UserName";
    private static String loginTypeKey = "LoginType";

    private static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    public static void setLoginType(Context context, String loginType){
        SharedPreferences prefs = getPreferences(context);
        prefs.edit().putString(loginTypeKey, loginType).commit();
    }

    public static String getLoginType(Context context){
        return getPreferences(context).getString(loginTypeKey, null);
    }

    public static void clearPreferences(Context context){
        getPreferences(context).edit().clear().commit();
    }

    public static void saveToken(Context context, String token){
        SharedPreferences prefs = getPreferences(context);
        prefs.edit().putString(tokenKey, token).commit();
    }

    public static String getToken(Context context){
        return getPreferences(context).getString(tokenKey, null);
    }

    public static void saveUsername(Context context, String username){
        SharedPreferences prefs = getPreferences(context);
        prefs.edit().putString(usernameKey, username).commit();
    }

    public static String getUsername(Context context){
        return getPreferences(context).getString(usernameKey, "Uknown user");
    }
}
