package com.shemasoft.android.bard;

import android.support.v4.app.Fragment;

public class PlayerActivity extends SingleFragmentActivity {

    private static final String TAG = "PlayerActivity";

    @Override
    public Fragment createFragment() {
        Integer index = getIntent().getIntExtra(PlayerFragment.EXTRA_BOOKINDEX, 0);
        return PlayerFragment.newInstance(index);
    }
}
