package com.example.android.balans;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.example.android.balans.BalansDatabaseContract.MealInfoEntry;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BalansAddItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_SINGLE_MEAL = 1;
    public static String MEAL_ID = "com.example.android.balans.MEAL_ID";
    private int mMealId;
    private boolean mIsNewMeal;
    public static final int ID_NOT_SET = -1;
    String[] costRange;
    public static final int NUMBER_PICKER_MAX_COST = 1000;
    public static final int NUMBER_PICKER_MIN_COST = 0;
    public static final int NUMBER_PICKER_STEP = 50;
    public static final int NUMBER_PICKER_RANGE = ((NUMBER_PICKER_MAX_COST - NUMBER_PICKER_MIN_COST) / NUMBER_PICKER_STEP) + 1;
    private BalansOpenHelper mDbOpenHelper;
    private Spinner mSpinnerMeals;
    private NumberPicker mNumberPickerCost;
    private EditText mEditTextDetails;
    private List<String> mMealsList;
    private Cursor mMealCursor;
    private Button mButtonAddMeal;
    private boolean mIsCancelling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balans_add_item);

        mDbOpenHelper = new BalansOpenHelper(this);

        mSpinnerMeals = findViewById(R.id.spinner_meal);
        String[] mealsArray = new String[]{"Breakfast", "Lunch", "Dinner"};
        mMealsList = Arrays.asList(mealsArray);
        ArrayAdapter<String> adapterMeals = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, mMealsList);
        adapterMeals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMeals.setAdapter(adapterMeals);

        mEditTextDetails = findViewById(R.id.edit_text_details);

        mButtonAddMeal = findViewById(R.id.button_add_meal);
        mButtonAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMeal();
                finish();
            }
        });

        createNumberPicker();

        readDisplayStateValues();
    }

    private void createNumberPicker() {
        mNumberPickerCost = findViewById(R.id.number_picker_cost);
        costRange = initializeCostRange();
        mNumberPickerCost.setMaxValue(costRange.length - 1);
        mNumberPickerCost.setMinValue(0);
        mNumberPickerCost.setWrapSelectorWheel(true);
        mNumberPickerCost.setDisplayedValues(costRange);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mMealId = intent.getIntExtra(MEAL_ID, ID_NOT_SET);
        mIsNewMeal = (mMealId == ID_NOT_SET);
        if (!mIsNewMeal){
            //editSelectedMeal();
            getLoaderManager().initLoader(LOADER_SINGLE_MEAL, null, this);
        } else{
            createNewMeal();
        }

    }

    private void createNewMeal() {
        final ContentValues values = new ContentValues();
        values.put(MealInfoEntry.COLUMN_MEAL_TYPE, "");
        values.put(MealInfoEntry.COLUMN_MEAL_COST, "");
        values.put(MealInfoEntry.COLUMN_MEAL_DETAILS, "");
        values.put(MealInfoEntry.COLUMN_MEAL_TIME_STAMP, "");

        AsyncTask taskInsert = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getWritableDatabase();
                mMealId = (int)sqLiteDatabase.insert(MealInfoEntry.TABLE_NAME, null, values);
                return null;
            }
        };
        taskInsert.execute();
    }

    private void editSelectedMeal() {
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getReadableDatabase();
        String[] mealColumns = new String[]{
                MealInfoEntry.COLUMN_MEAL_ID,
                MealInfoEntry.COLUMN_MEAL_TYPE,
                MealInfoEntry.COLUMN_MEAL_COST,
                MealInfoEntry.COLUMN_MEAL_DETAILS
        };

        String selection = MealInfoEntry.COLUMN_MEAL_ID + " =?";
        String[] selectionArgs = {Integer.toString(mMealId)};

        Cursor cursor = sqLiteDatabase.query(
                MealInfoEntry.TABLE_NAME,
                mealColumns,
                selection,
                selectionArgs,
                null, null, null );

        int mealTypePosition = cursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_TYPE);
        int mealCostPosition = cursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_COST);
        int mealDetailsPosition = cursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_DETAILS);

        cursor.moveToNext();
        String mealType = cursor.getString(mealTypePosition);
        int mealCost = cursor.getInt(mealCostPosition);
        String mealDetails = cursor.getString(mealDetailsPosition);
        cursor.close();

        int mealTypeIndex = mMealsList.indexOf(mealType);
        mSpinnerMeals.setSelection(mealTypeIndex);
        int mealCostIndexInNumberPicker = mealCost/NUMBER_PICKER_STEP;
        mNumberPickerCost.setValue(mealCostIndexInNumberPicker);

        mEditTextDetails.setText(mealDetails);
    }

    private String[] initializeCostRange() {
        String[] valueSet = new String[NUMBER_PICKER_RANGE];
        for(int i = NUMBER_PICKER_MIN_COST; i<= NUMBER_PICKER_MAX_COST; i+= NUMBER_PICKER_STEP){
            valueSet[i/ NUMBER_PICKER_STEP] = Integer.toString(i);
        }
        return valueSet;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_balans_add_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_cancel){
            mIsCancelling = true;
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader loader = null;
        if (id == LOADER_SINGLE_MEAL){
            loader = createLoaderSingleMeal();
        }
        return loader;
    }

    private CursorLoader createLoaderSingleMeal() {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getReadableDatabase();
                String[] mealColumns = new String[]{
                        MealInfoEntry.COLUMN_MEAL_ID,
                        MealInfoEntry.COLUMN_MEAL_TYPE,
                        MealInfoEntry.COLUMN_MEAL_COST,
                        MealInfoEntry.COLUMN_MEAL_DETAILS
                };

                String selection = MealInfoEntry.COLUMN_MEAL_ID + " =?";
                String[] selectionArgs = {Integer.toString(mMealId)};

                Cursor cursor = sqLiteDatabase.query(
                        MealInfoEntry.TABLE_NAME,
                        mealColumns,
                        selection,
                        selectionArgs,
                        null, null, null );
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LOADER_SINGLE_MEAL){
            loadMeal(cursor);
        }
    }

    private void loadMeal(Cursor cursor) {
        mMealCursor = cursor;
        int mealTypePosition = mMealCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_TYPE);
        int mealCostPosition = mMealCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_COST);
        int mealDetailsPosition = mMealCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_DETAILS);

        mMealCursor.moveToNext();
        String mealType = mMealCursor.getString(mealTypePosition);
        int mealCost = mMealCursor.getInt(mealCostPosition);
        String mealDetails = mMealCursor.getString(mealDetailsPosition);

        int mealTypeIndex = mMealsList.indexOf(mealType);
        mSpinnerMeals.setSelection(mealTypeIndex);
        int mealCostIndexInNumberPicker = mealCost/NUMBER_PICKER_STEP;
        mNumberPickerCost.setValue(mealCostIndexInNumberPicker);

        mEditTextDetails.setText(mealDetails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_SINGLE_MEAL){
            if (mMealCursor != null){
                mMealCursor.close();
            }
        }
    }

    private void saveMealToDatabase(String mealType, double mealCost, String mealDetails, long mealTimestamp){
        final String selection = MealInfoEntry.COLUMN_MEAL_ID + " = ?";
        final String[] selectionArgs = {Integer.toString(mMealId)};

        final ContentValues values = new ContentValues();
        values.put(MealInfoEntry.COLUMN_MEAL_TYPE, mealType);
        values.put(MealInfoEntry.COLUMN_MEAL_COST, mealCost);
        values.put(MealInfoEntry.COLUMN_MEAL_DETAILS, mealDetails);
        values.put(MealInfoEntry.COLUMN_MEAL_TIME_STAMP, mealTimestamp);

        AsyncTask taskUpdate = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                db.update(MealInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                return null;
            }
        };
        taskUpdate.execute();
    }

    private void saveMeal(){
        int selectedPosition = mSpinnerMeals.getSelectedItemPosition();
        String mealType = mMealsList.get(selectedPosition);
        int selectedCost = mNumberPickerCost.getValue();
        double mealCost = Double.parseDouble(costRange[selectedCost]);
        String mealDetails = mEditTextDetails.getText().toString();
        long mealTimeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());//compare date.getTime

        saveMealToDatabase(mealType, mealCost, mealDetails, mealTimeStamp);
    }

    private long getCurrentTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
        return time;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            if(mIsNewMeal){
                deleteMealFromDatabase();
            }
        }
    }

    private void deleteMealFromDatabase() {
        final String selection = MealInfoEntry.COLUMN_MEAL_ID + " = ? ";
        final String[] selectionArgs = {Integer.toString(mMealId)};

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getWritableDatabase();
                sqLiteDatabase.delete(MealInfoEntry.TABLE_NAME, selection, selectionArgs);
                return null;
            }
        };
        task.execute();

    }
}
