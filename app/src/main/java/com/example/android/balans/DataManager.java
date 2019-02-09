package com.example.android.balans;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.example.android.balans.BalansDatabaseContract.MealInfoEntry;

/**
 * Created by OKUNIYI MONSURU on 12/27/2018.
 */

public class DataManager {
    public static final int TOTAL_ID = -1234;
    private static DataManager sInstance = null;

    List<MealInfo> mMeals = new ArrayList<>();


    public List<MealInfo> getMeals() {
        return mMeals;
    }

    public List<MealInfo> getTotalMeals() {
        double totalCost = 0.00;
        for (int i=0; i < mMeals.size(); i++){
            totalCost += mMeals.get(i).getCost();
        }
        MealInfo total = new MealInfo(TOTAL_ID,"Total", totalCost, null, 0);
        List<MealInfo> totalMeals = new ArrayList<MealInfo>(mMeals);
        totalMeals.add(total);
        return totalMeals;
    }

    public static DataManager getInstance(){
        if(sInstance == null){
            sInstance = new DataManager();
        }
        return sInstance;
    }

    public static void loadTodaysMealsFromDatabase(BalansOpenHelper dbHelper){
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        final String[] mealColumns = {
                MealInfoEntry.COLUMN_MEAL_ID,
                MealInfoEntry.COLUMN_MEAL_TYPE,
                MealInfoEntry.COLUMN_MEAL_COST,
                MealInfoEntry.COLUMN_MEAL_DETAILS,
                MealInfoEntry.COLUMN_MEAL_TIME_STAMP};
        final String selection = "strftime('%Y-%m-%d', datetime(" + MealInfoEntry.COLUMN_MEAL_TIME_STAMP + ", 'unixepoch')) == ?";
        final String[] selectionArgs = new String[]{"2019-01-03"};
        final String order = "(case " + MealInfoEntry.COLUMN_MEAL_TYPE + " when 'Breakfast' then 1 when 'Lunch' then 2 else 3 end)";

        final Cursor mealCursor = sqLiteDatabase.query(MealInfoEntry.TABLE_NAME, mealColumns, selection,
                selectionArgs, null, null, order);

        loadMeals(mealCursor);
    }

    private static void loadMeals(Cursor cursor) {
        int mealIdPosition = cursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_ID);
        final int mealTypePosition = cursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_TYPE);
        final int mealCostPosition = cursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_COST);
        int mealDetailsPosition = cursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_DETAILS);
        int mealTimeStampPosition = cursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_TIME_STAMP);

        DataManager dataManager = DataManager.getInstance();
        dataManager.mMeals.clear();
        while(cursor.moveToNext()){
            MealInfo meal = new MealInfo(
                    cursor.getInt(mealIdPosition),
                    cursor.getString(mealTypePosition),
                    cursor.getInt(mealCostPosition),
                    cursor.getString(mealDetailsPosition),
                    cursor.getLong(mealTimeStampPosition));
            dataManager.mMeals.add(meal);
        }
        cursor.close();
    }

    private void initializeMeals() {
        mMeals.add(new MealInfo("Breakfast", 250.00, "Beans and Bread", 1546417200));
        mMeals.add(new MealInfo("Lunch", 300.00, "Rice and Egg", 1546443300));
        mMeals.add(new MealInfo("Dinner", 500.00, "Chicken and Chips", 1546456500));
    }
}
