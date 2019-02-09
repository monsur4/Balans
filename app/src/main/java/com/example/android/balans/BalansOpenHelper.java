package com.example.android.balans;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by OKUNIYI MONSURU on 2/2/2019.
 */

public class BalansOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Balans.db";
    public static final int DATABASE_VERSION = 1;

    public BalansOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(BalansDatabaseContract.MealInfoEntry.SQL_CREATE_TABLE);
        DatabaseWorker worker = new DatabaseWorker(sqLiteDatabase);
        worker.insertSampleMeals();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
