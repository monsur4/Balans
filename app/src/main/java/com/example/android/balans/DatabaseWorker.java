package com.example.android.balans;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by OKUNIYI MONSURU on 2/2/2019.
 */

public class DatabaseWorker {
    private SQLiteDatabase mDb;
    public DatabaseWorker (SQLiteDatabase sqLiteDatabase){
        mDb = sqLiteDatabase;
    }

    public void insertSampleMeals(){

        insertMeal("Breakfast", 200, "rice and egg", 1546417200);//jan 2, 2019 8:20am
        insertMeal("Lunch", 250, "Bread and beans", 1546443300);//jan 2 3:35pm
        insertMeal("Dinner", 300, "Spaghetti", 1546456500);//jan 2 7:15pm

        insertMeal("Breakfast", 250, "rice and egg", 1546507800); //jan 3, 2019 9:30am
        insertMeal("Lunch", 300, "Porridge beans", 1546524300); //jan 3, 2:05pm
        insertMeal("Dinner", 350, "Chicken and Chips", 1546546500); //jan 3 8:15pm

        insertMeal("Breakfast", 250, "Indomie", 1546590600); //jan 4, 2019 8:30am
        insertMeal("Lunch", 300, "rice and egg", 1546570800); //jan 4, 2019 3:00pm
        insertMeal("Dinner", 200, "Bread and akara", 1546637400); //jan 4, 2019 9:30pm

    }

    private void insertMeal( String meal_type, double meal_cost, String meal_details, long meal_time_stamp){
        ContentValues values = new ContentValues();
        values.put(BalansDatabaseContract.MealInfoEntry.COLUMN_MEAL_TYPE, meal_type);
        values.put(BalansDatabaseContract.MealInfoEntry.COLUMN_MEAL_COST, meal_cost);
        values.put(BalansDatabaseContract.MealInfoEntry.COLUMN_MEAL_DETAILS, meal_details);
        values.put(BalansDatabaseContract.MealInfoEntry.COLUMN_MEAL_TIME_STAMP, meal_time_stamp);

        long id = mDb.insert(BalansDatabaseContract.MealInfoEntry.TABLE_NAME, null, values);
    }
}
