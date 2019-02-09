package com.example.android.balans;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OKUNIYI MONSURU on 12/26/2018.
 */

public class BalansAdapter extends RecyclerView.Adapter<BalansAdapter.ViewHolder> {
    
    private List<MealInfo> mMeals = new ArrayList<>();
    Context mContext;
    public BalansAdapter(Context mContext) {
        this.mContext = mContext;
        mMeals = DataManager.getInstance().getTotalMeals();
    }

    @Override
    public BalansAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_meal_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( BalansAdapter.ViewHolder holder, int position) {
        holder.mealTextView.setText(mMeals.get(position).getName());
        holder.costTextView.setText(Double.toString(mMeals.get(position).getCost()));
        holder.id = mMeals.get(position).getMealId();
    }

    @Override
    public int getItemCount() {
        return mMeals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mealTextView;
        private final TextView costTextView;
        private int id;

        public ViewHolder(View itemView) {
            super(itemView);
            mealTextView = itemView.findViewById(R.id.text_view_meal);
            costTextView = itemView.findViewById(R.id.text_view_cost);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (id != DataManager.TOTAL_ID) {
                        Intent intent = new Intent(mContext, BalansAddItemActivity.class);
                        intent.putExtra(BalansAddItemActivity.MEAL_ID, id);
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
