package com.shemasoft.android.bard;

import android.support.v4.app.Fragment;

public class PlayerActivity extends SingleFragmentActivity {

    private static final String TAG = "PlayerActivity";

    @Override
    public Fragment createFragment() {
        return new PlayerFragment();
    }
}
