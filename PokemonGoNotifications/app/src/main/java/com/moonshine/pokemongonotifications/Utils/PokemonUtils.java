package com.moonshine.pokemongonotifications.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaapmanenschijn on 23/07/16.
 */
public class PokemonUtils {
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getResourceId(Context context, String pVariableName, String pResourcename, String pPackageName)
    {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int dpToPixels(Context context,int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    public static List<Long> convertStringToList(String ids){
        ArrayList<Long> list = new ArrayList<>();
        if(!ids.isEmpty()) {
            String[] seperated = ids.split(",");
            if (seperated.length > 0) {
                for (int i = 0; i < seperated.length; i++) {
                    Long id = Long.valueOf(seperated[i]);
                    list.add(id);
                }
            }
        }
        return list;
    }
}
