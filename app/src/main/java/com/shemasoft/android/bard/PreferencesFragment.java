package com.shemasoft.android.bard;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by jv on 7/15/2015.
 */
public class PreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
