package com.moonshine.pokemongonotifications.view;

import android.content.Context;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.moonshine.pokemongonotifications.R;

/**
 * Created by jaapmanenschijn on 23/07/16.
 */
public class CheckableLayout extends FrameLayout implements Checkable {
    private boolean mChecked;

    public CheckableLayout(Context context) {
        super(context);
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        setBackgroundDrawable(checked ?
                getResources().getDrawable(R.drawable.checked_bg)
                : null);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }

}
