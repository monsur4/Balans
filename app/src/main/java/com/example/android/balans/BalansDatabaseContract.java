package com.example.android.balans;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by OKUNIYI MONSURU on 2/2/2019.
 */

public final class BalansDatabaseContract {
    private BalansDatabaseContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.balans";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MEALS = "meal_info";


    public static class MealInfoEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MEALS);
        public static final String TABLE_NAME = "meal_info";
        public static final String COLUMN_MEAL_ID = BaseColumns._ID;
        public static final String COLUMN_MEAL_TYPE = "meal_type";
        public static final String COLUMN_MEAL_COST = "meal_cost";
        public static final String COLUMN_MEAL_DETAILS = "meal_details";
        public static final String COLUMN_MEAL_TIME_STAMP = "meal_time_stamp";

        //CREATE TABLE meal_info (_ID, meal_type, meal_cost, meal_details, meal_time_stamp)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_MEAL_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_MEAL_TYPE + " TEXT NOT NULL, " +
                        COLUMN_MEAL_COST + " REAL NOT NULL, " +
                        COLUMN_MEAL_DETAILS + " TEXT , " +
                        COLUMN_MEAL_TIME_STAMP + " INTEGER)";
    }

}
