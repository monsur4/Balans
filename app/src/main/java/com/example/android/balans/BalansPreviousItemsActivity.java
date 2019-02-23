package com.example.android.balans;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class BalansPreviousItemsActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener{
    private static final String TAG = Context.class.getSimpleName();
    public static final String WEEK_IN_YEAR = "week_in_year";
    private static final String BUNDLE_EXTRA = "bundle_extras";
    Toolbar mToolbar;

    TabLayout mTabLayout;

    TabItem mTabItemSummary;
    TabItem mTabItemDetails;
    TabItem mTabItemInfo;

    PagerAdapter mPagerAdapter;
    ViewPager mViewPager;

    String mCurrentWeek;

    private OnDataReceivedListener mDataReceivedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balans_previous_items);

        mToolbar = findViewById(R.id.toolbar);

        mTabLayout = findViewById(R.id.tab_layout);

        mTabItemSummary = findViewById(R.id.tab_item_summary);
        mTabItemDetails = findViewById(R.id.tab_item_details);
        mTabItemInfo = findViewById(R.id.tab_item_info);

        mPagerAdapter = new BalansPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), this);

        mViewPager = findViewById(R.id.view_pager);

        mToolbar.setTitle("Recent Meals");
        setSupportActionBar(mToolbar);

        mTabLayout.addOnTabSelectedListener(this);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        Intent intent = getIntent();
        mCurrentWeek = intent.getStringExtra(BalansPreviousItemsActivity.WEEK_IN_YEAR);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabSelected sCalled");
        mViewPager.setCurrentItem(tab.getPosition());
        mDataReceivedListener.onDataReceived(mCurrentWeek);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public interface OnDataReceivedListener{
        void onDataReceived(String weekInYear);
    }

    public void setOnDataReceivedListener(OnDataReceivedListener listener){
        Log.d(TAG, "setOnDataReceivedListener sCalled");
        mDataReceivedListener = listener;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

    }

}
