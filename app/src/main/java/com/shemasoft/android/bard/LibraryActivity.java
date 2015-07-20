package com.shemasoft.android.bard;

import android.support.v4.app.Fragment;

/**
 * @author Jason Vinson
 *         <p/>
 *         Represents an AudioBook library
 */
public class LibraryActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new LibraryFragment();
    }
}
