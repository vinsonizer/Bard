package com.shemasoft.android.bard;


import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Jason Vinson
 *
 * Simple activity for preferences management, would use fragments
 * except they aren't able to use the support library...
 */
public class PreferencesActivity extends PreferenceActivity {

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
