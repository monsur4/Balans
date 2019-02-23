package com.example.android.balans;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.balans.BalansDatabaseContract.MealInfoEntry;

/**
 * Created by OKUNIYI MONSURU on 2/15/2019.
 */

public class DetailsFragment extends Fragment
        implements BalansPreviousItemsActivity.OnDataReceivedListener{

    private static final String TAG = Context.class.getSimpleName();
    public static final int LOADER_RECENT_WEEK_MEALS = 30;
    BalansPreviousItemsActivity mActivity;
    String mWeekInYear;
    RecyclerView mRecyclerViewDetails;
    DetailsRecyclerAdapter mDetailsRecyclerAdapter;
    private Cursor mCursor;
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate sCalled");
        mActivity = (BalansPreviousItemsActivity)getActivity();
        mActivity.setOnDataReceivedListener(this);
        mContext = mActivity.getApplicationContext();/*determine the proper way of getting the context of a fragment*/
        mDetailsRecyclerAdapter = new DetailsRecyclerAdapter(mContext, mCursor);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView sCalled");
        View view = inflater.inflate(R.layout.fragment_details, container, false);
            mRecyclerViewDetails = view.findViewById(R.id.recycler_view_details);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        final DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL/*linearLayoutManager.getOrientation()*/);
        mRecyclerViewDetails.setLayoutManager(linearLayoutManager);
        mRecyclerViewDetails.addItemDecoration(dividerItemDecoration);
        mRecyclerViewDetails.setAdapter(mDetailsRecyclerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDataReceived(String weekInYear) {
        Log.d(TAG, "onDataReceived sCalled");
        mWeekInYear = weekInYear;
        mCursor = loadRecentWeekMeals();
        mDetailsRecyclerAdapter.changeCursor(mCursor);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public Cursor loadRecentWeekMeals(){
        String path = "week/" + mWeekInYear;
        Uri uriRecentWeekMeals = Uri.withAppendedPath(MealInfoEntry.CONTENT_URI, path);
        String[] mealColumns = new String[]{
                MealInfoEntry.COLUMN_MEAL_ID,
                MealInfoEntry.COLUMN_MEAL_TYPE,
                MealInfoEntry.COLUMN_MEAL_COST,
                MealInfoEntry.COLUMN_MEAL_DETAILS,
                MealInfoEntry.COLUMN_MEAL_TIME_STAMP};//"strftime('%Y-%m-%d', datetime(" + MealInfoEntry.COLUMN_MEAL_TIME_STAMP + ", 'unixepoch')) == ?";
        String selection = "strftime ('%W', datetime(" + MealInfoEntry.COLUMN_MEAL_TIME_STAMP + ", 'unixepoch')) == ?";
        String[] selectionArgs = new String[]{mWeekInYear};
        String firstOrder = "strftime ('%w', datetime(" + MealInfoEntry.COLUMN_MEAL_TIME_STAMP + ", 'unixepoch'))";
        String sortOrder = firstOrder + ", " + "(case " + MealInfoEntry.COLUMN_MEAL_TYPE + " when 'Breakfast' then 1 when 'Lunch' then 2 else 3 end)";
        Cursor cursor = mActivity.getContentResolver().query(uriRecentWeekMeals, mealColumns, selection, selectionArgs, sortOrder);
        return cursor;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCursor.close();
    }
}
