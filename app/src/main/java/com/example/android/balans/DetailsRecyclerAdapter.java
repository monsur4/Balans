package com.example.android.balans;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.balans.BalansDatabaseContract.MealInfoEntry;

import java.util.Calendar;
import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Created by OKUNIYI MONSURU on 2/17/2019.
 */

public class DetailsRecyclerAdapter extends RecyclerView.Adapter<DetailsRecyclerAdapter.ViewHolder> {
    Context mContext;
    Cursor mCursor;
    private int mIdPosition;
    private int mMealTypePosition;
    private int mMealCostPosition;
    private int mMealDetailsPosition;
    private int mMealTimeStampPosition;

    public DetailsRecyclerAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
        populateColumnPosition();
    }

    @NonNull
    @Override
    public DetailsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_details_recent_meal_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsRecyclerAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        int id = mCursor.getInt(mIdPosition);
        String mealType = mCursor.getString(mMealTypePosition);
        int mealCost = mCursor.getInt(mMealCostPosition);
        String mealDetails = mCursor.getString(mMealDetailsPosition);
        long mealTimeStamp = mCursor.getLong(mMealTimeStampPosition);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mealTimeStamp * 1000);
        String mealDayString = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int mealDay = calendar.get(Calendar.DAY_OF_WEEK);

        holder.mMealType.setText(mealType);
        holder.mMealCost.setText(String.valueOf(mealCost));
        holder.mMealDetails.setText(mealDetails);
        holder.mMealTimeStamp.setText(mealDayString);

        if (position != 0){
            mCursor.moveToPosition(position - 1);
            long previousMealTimeStamp = mCursor.getLong(mMealTimeStampPosition);
            calendar.setTimeInMillis(previousMealTimeStamp * 1000);
            int previousMealDay = calendar.get(Calendar.DAY_OF_WEEK);
            if (mealDay == previousMealDay){
                holder.mMealTimeStamp.setVisibility(View.GONE);
            }else {
                holder.mMealTimeStamp.setVisibility(View.VISIBLE);
            }
        }else{
            holder.mMealTimeStamp.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    private void populateColumnPosition() {
        if (mCursor == null)
            return;
        mIdPosition = mCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_ID);
        mMealTypePosition = mCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_TYPE);
        mMealCostPosition = mCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_COST);
        mMealDetailsPosition = mCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_DETAILS);
        mMealTimeStampPosition = mCursor.getColumnIndex(MealInfoEntry.COLUMN_MEAL_TIME_STAMP);
    }

    public void changeCursor(Cursor cursor){
        if (mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPosition();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mMealType;
        private final TextView mMealCost;
        private final TextView mMealDetails;
        private final TextView mMealTimeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            mMealType = itemView.findViewById(R.id.text_view_details_meal_type);
            mMealCost = itemView.findViewById(R.id.text_view_details_meal_cost);
            mMealDetails = itemView.findViewById(R.id.text_view_details_meal_details);
            mMealTimeStamp = itemView.findViewById(R.id.text_view_details_weekday);
        }
    }
}
