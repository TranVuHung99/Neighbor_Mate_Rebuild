package com.example.neighbormaterebuild.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Navigator {

    public static void loadFragment(FragmentManager mFragmentManager, int containerViewId,
                                    Fragment mfragment, String rootTag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment curFrag = mFragmentManager.getPrimaryNavigationFragment();
        if (curFrag != null) {
            fragmentTransaction.detach(curFrag);
        }

        Fragment fragment = mFragmentManager.findFragmentByTag(rootTag);
        if (fragment == null) {
            fragment = mfragment;
            fragmentTransaction.add(containerViewId, fragment, rootTag);
        } else {
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }
}