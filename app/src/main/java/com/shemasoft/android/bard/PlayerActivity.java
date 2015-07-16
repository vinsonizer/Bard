package com.shemasoft.android.bard;

import android.app.Fragment;
import android.util.Log;

public class PlayerActivity extends SingleFragmentActivity {

    private static final String TAG = "PlayerActivity";

    @Override
    public Fragment createFragment() {
        Log.d(TAG, "Loading Player Fragment");
        return new PlayerFragment();
    }

}
