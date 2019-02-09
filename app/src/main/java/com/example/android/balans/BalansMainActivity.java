package com.example.android.balans;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.android.balans.BalansDatabaseContract.MealInfoEntry;

public class BalansMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int PICK_IMAGE = 12;
    public static final String THUMBNAIL_PNG = "thumbnail.png";
    public static final int LOADER_TODAYS_MEALS = 0;
    public static final int PENDING_INTENT_MAIN_ACTIVITY = 23;
    RecyclerView recycler;
    private BalansAdapter mBalansAdapter;
    CircularImageView circularImageView;
    TextView textViewDate;
    TextView textViewPlaceHolder;

    private String appPath;
    private ContextWrapper mContextWrapper;
    private File mAppDirectory;
    private String mUserName;
    private File mImageFile;
    private String mEmailAddress;

    private BalansOpenHelper mDbOpenHelper;
    private Cursor mMealsCursor;
    private Date mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balans_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new BalansOpenHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BalansMainActivity.this, BalansAddItemActivity.class);
                startActivity(intent);
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);

        initAppDirectory();
        updateUserNameAndEmail();
        mImageFile = new File(appPath, THUMBNAIL_PNG);

        textViewPlaceHolder = findViewById(R.id.text_view_placeholder);
        circularImageView = findViewById(R.id.circular_image_view);
        textViewPlaceHolder.setOnClickListener(this);
        circularImageView.setOnClickListener(this);

        textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(setDate());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recycler = findViewById(R.id.recycler_view);
        //displayMeals();
        getLoaderManager().initLoader(LOADER_TODAYS_MEALS, null, this);

        setupNewDataEveryMidnight();
    }

    private void setupNewDataEveryMidnight() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean willClearDataAtMidnight = sharedPreferences.getBoolean("clear_data_at_midnight", false);
        if(!willClearDataAtMidnight) {
            setNewDataEveryMidnight();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("clear_data_at_midnight", true);
            editor.apply();
        }
    }

    private void displayMeals() {
        //DataManager.loadTodaysMealsFromDatabase(mDbOpenHelper);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        mBalansAdapter = new BalansAdapter(this);
        recycler.setAdapter(mBalansAdapter);
    }

    private void initAppDirectory() {
        mContextWrapper = new ContextWrapper(getApplicationContext());
        mAppDirectory = mContextWrapper.getDir("profile", Context.MODE_PRIVATE);
        appPath = mAppDirectory.getAbsolutePath();
    }

    private void saveImage(Uri uri) {
        if (!mAppDirectory.exists()){
            mAppDirectory.mkdir();
        }
        File myPath = new File(mAppDirectory, THUMBNAIL_PNG);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(myPath);
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri/*Uri.parse(selectedImagePathString)*/);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void retreiveImage(){
        if (mImageFile.length() > 0) {
            try {
                InputStream inputStream = new FileInputStream(mImageFile);
                Bitmap retreivedImageBitmap = BitmapFactory.decodeStream(inputStream);
                circularImageView.setImageBitmap(retreivedImageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //It appears I'd finally need to create a job or a service to constantly check and update the time at regular intervals
    private String setDate() {
        mDate = new Date();
        DateFormat dateFormat = DateFormat.getDateInstance();
        String dateString = dateFormat.format(mDate);
        return dateString;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.balans_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(BalansMainActivity.this, BalansSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            handleselection("home");
        } else if (id == R.id.nav_profile) {
            handleselection("profile");
        } else if (id == R.id.nav_favorite_meals) {
            handleselection("favorite meals");
        } else if (id == R.id.nav_diet_plan) {
            handleselection("diet plan");
        } else if (id == R.id.nav_news) {
            handleselection("news");
        } else if (id == R.id.nav_share) {
            handleselection("share");
        } else if (id == R.id.nav_send) {
            handleselection("send");
        } else if (id == R.id.nav_people) {
            handleselection("people");
        }else if (id == R.id.nav_settings) {
            handleselection("settings");
        }else if (id == R.id.nav_support) {
            handleselection("support");
        }else if (id == R.id.nav_about) {
            handleselection("about");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleselection(String message) {
        View view = findViewById(R.id.text_view_meal);
        Snackbar snackbar = Snackbar.make(view, message + " feature is not yet supported", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
        if (mImageFile.length() > 0) {
            retreiveImage();
            textViewPlaceHolder.setVisibility(View.GONE);
            circularImageView.setVisibility(View.VISIBLE);
        }else{
            textViewPlaceHolder.setText(setPlaceHolderLetter(mUserName));
            circularImageView.setVisibility(View.GONE);
            textViewPlaceHolder.setVisibility(View.VISIBLE);
        }
        getLoaderManager().restartLoader(LOADER_TODAYS_MEALS, null, this);
    }

    private void setNewDataEveryMidnight(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Intent intent = getIntent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_MAIN_ACTIVITY, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Meals will be clear Every Midnight", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textViewUserName = headerView.findViewById(R.id.text_view_user_name);
        TextView textViewEmailAddress = headerView.findViewById(R.id.text_view_email_address);

        textViewUserName.setText(mUserName);
        textViewEmailAddress.setText(mEmailAddress);
    }

    private void updateUserNameAndEmail() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserName = preferences.getString("user_display_name", "");
        mEmailAddress = preferences.getString("user_email_address", "");
    }

    private String setPlaceHolderLetter(String name){
        return name.trim().substring(0,1);
    }

    private void selectProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.text_view_placeholder:
                selectProfilePicture();
                break;
            case R.id.circular_image_view:
                selectProfilePicture();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE){
            if (resultCode == RESULT_OK){
                if (data != null){
                    Uri selectedImageUri = data.getData();
                    //circularImageView.setImageURI(selectedImageUri);
                    saveImage(selectedImageUri);
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader loader = null;
        if(id == LOADER_TODAYS_MEALS){
            loader = createLoaderTodaysMeals();
        }
        return loader;
    }

    private CursorLoader createLoaderTodaysMeals() {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getReadableDatabase();
                final String[] mealColumns = {
                        MealInfoEntry.COLUMN_MEAL_ID,
                        MealInfoEntry.COLUMN_MEAL_TYPE,
                        MealInfoEntry.COLUMN_MEAL_COST,
                        MealInfoEntry.COLUMN_MEAL_DETAILS,
                        MealInfoEntry.COLUMN_MEAL_TIME_STAMP};
                final String selection = "strftime('%Y-%m-%d', datetime(" + MealInfoEntry.COLUMN_MEAL_TIME_STAMP + ", 'unixepoch')) == ?";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = simpleDateFormat.format(mDate);
                final String[] selectionArgs = new String[]{dateString};
                final String order = "(case " + MealInfoEntry.COLUMN_MEAL_TYPE + " when 'Breakfast' then 1 when 'Lunch' then 2 else 3 end)";

                Cursor cursor =  sqLiteDatabase.query(MealInfoEntry.TABLE_NAME, mealColumns, selection,
                        selectionArgs, null, null, order);
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LOADER_TODAYS_MEALS){
            loadTodaysMeals(cursor);
        }
    }

    private void loadTodaysMeals(Cursor data) {
        mMealsCursor = data;
        int mealIdPosition = mMealsCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_ID);
        final int mealTypePosition = mMealsCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_TYPE);
        final int mealCostPosition = mMealsCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_COST);
        int mealDetailsPosition = mMealsCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_DETAILS);
        int mealTimeStampPosition = mMealsCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_TIME_STAMP);

        DataManager dataManager = DataManager.getInstance();
        dataManager.mMeals.clear();
        while(mMealsCursor.moveToNext()){
            MealInfo meal = new MealInfo(
                    mMealsCursor.getInt(mealIdPosition),
                    mMealsCursor.getString(mealTypePosition),
                    mMealsCursor.getInt(mealCostPosition),
                    mMealsCursor.getString(mealDetailsPosition),
                    mMealsCursor.getLong(mealTimeStampPosition));
            dataManager.mMeals.add(meal);
        }
        displayMeals();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_TODAYS_MEALS){
            if (mMealsCursor != null){
                mMealsCursor.close();
            }
        }
    }
}
