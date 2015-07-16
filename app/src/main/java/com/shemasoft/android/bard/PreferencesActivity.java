package com.shemasoft.android.bard;


import android.app.Fragment;

/**
 * Created by jv on 7/15/2015.
 */
public class PreferencesActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new PreferencesFragment();
    }
}
