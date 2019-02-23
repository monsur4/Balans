package com.example.android.balans;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by OKUNIYI MONSURU on 2/15/2019.
 */

public class BalansPagerAdapter extends FragmentPagerAdapter {

    int mNumberOfTabs;
    Context mContext;

    public BalansPagerAdapter(FragmentManager fm, int numberOfTabs, Context context) {
        super(fm);
        mNumberOfTabs = numberOfTabs;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SummaryFragment();
            case 1:
                return new DetailsFragment();
            case 2:
                return new InfoFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}
