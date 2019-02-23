package com.example.android.balans;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import static com.example.android.balans.BalansDatabaseContract.MealInfoEntry.*;

/**
 * Created by OKUNIYI MONSURU on 2/14/2019.
 */

public class BalansProvider extends ContentProvider {

    BalansOpenHelper mDbOpenHelper;

    private static final int MEALS = 10;
    private static final int SINGLE_MEALS = 11;
    private static final int TODAYS_MEALS = 12;
    private static final int WEEK_MEALS = 13;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI("com.example.android.balans", TABLE_NAME, MEALS);
        sUriMatcher.addURI("com.example.android.balans",
                TABLE_NAME + "/#", SINGLE_MEALS);
        sUriMatcher.addURI("com.example.android.balans",
                TABLE_NAME + "/*/#", WEEK_MEALS);

    }


    @Override
    public boolean onCreate() {
        mDbOpenHelper = new BalansOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String orderBy) {
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match){
            case MEALS:
                cursor = sqLiteDatabase.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, orderBy);
                break;
            case SINGLE_MEALS:

                break;
            case WEEK_MEALS:
                cursor =sqLiteDatabase.query(TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
                break;
            default:
                throw new IllegalArgumentException("Cannot perform this URI query " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match){
            case MEALS:
                long id = sqLiteDatabase.insert(BalansDatabaseContract.MealInfoEntry.TABLE_NAME,
                        null, contentValues);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Cannot resolve this URI insert " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match){
            case SINGLE_MEALS:
                int id = sqLiteDatabase.delete(BalansDatabaseContract.MealInfoEntry.TABLE_NAME, selection, selectionArgs);
                return id;
            default:
                throw new IllegalArgumentException("Cannot resolve this URI delete " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mDbOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match){
            case SINGLE_MEALS:
                int id = sqLiteDatabase.update(BalansDatabaseContract.MealInfoEntry.TABLE_NAME,
                        contentValues, selection, selectionArgs);
                return id;
            default:
                throw new IllegalArgumentException("Cannot resolve this URI update " + uri);
        }
    }
}
