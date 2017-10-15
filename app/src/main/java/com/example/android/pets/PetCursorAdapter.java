package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Created by kshyo on 2017-09-21.
 */

public class PetCursorAdapter extends CursorAdapter {



    public PetCursorAdapter(Context context, Cursor c) {

        super(context, c, 0 /* flags */);

    }


    @Override

    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // TODO: Fill out this method and return the list item view (instead of null)

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);


    }


    @Override

    public void bindView(View view, Context context, Cursor cursor) {
        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
        int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
        int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);


        // TODO: Fill out this method


        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        if (cursor != null) {
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            if (TextUtils.isEmpty(breed)) {
                breed = context.getString(R.string.unknown_breed);
            }
            String sGender = null;
            int weight = cursor.getInt(weightColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);

            if (gender == 1) {
                sGender = context.getString(R.string.gender_male);
            } else if (gender == 2) {
                sGender = context.getString(R.string.gender_female);
            } else {
                sGender = context.getString(R.string.gender_unknown);
            }

            String summary = PetEntry.COLUMN_PET_BREED + " : " + breed + "/ " +
                    PetEntry.COLUMN_PET_GENDER + " : " + sGender + "/ " +
                    PetEntry.COLUMN_PET_WEIGHT + " : " + weight;

            nameTextView.setText(name);
            summaryTextView.setText(summary);
        }

    }
}
