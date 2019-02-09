package com.example.android.balans;

/**
 * Created by OKUNIYI MONSURU on 12/27/2018.
 */

public class MealInfo {
    private int mMealId;
    private String mType;
    private double mCost;
    private String mDetails;
    private long mTimeStamp;

    public MealInfo( int id, String type, double cost, String details, long timeStamp) {
        mMealId = id;
        mType = type;
        mCost = cost;
        mDetails = details;
        mTimeStamp = timeStamp;
    }

    public MealInfo( String type, double cost, String details, long timeStamp) {
        mType = type;
        mCost = cost;
        mDetails = details;
        mTimeStamp = timeStamp;
    }

    public String getName() {
        return mType;
    }

    public double getCost() {
        return mCost;
    }

    @Override
    public String toString() {
        return mType;
    }

    public String getDetails() {
        return mDetails;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public int getMealId() {
        return mMealId;
    }
}
