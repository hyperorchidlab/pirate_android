package com.hop.pirate.util;

import android.support.v4.app.Fragment;


public interface FragmentNavigatorAdapter {

    Fragment onCreateFragment(int position);

    String getTag(int position);

    int getCount();
}
