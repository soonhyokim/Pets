package com.example.android.pets.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * {@link ContentProvider} for Pets app.
 */

public class PetProvider extends ContentProvider {
    public static final int PETS = 100;
    public static final int PET_ID = 101;

    private static UriMatcher sUriMatcher;

    PetDbHelper mDbHelper;
    SQLiteDatabase db;
    /**
     * Tag for the log messages
     */

    public static final String LOG_TAG = PetProvider.class.getSimpleName();


    /**
     * Initialize the provider and the database helper object.
     */

    @Override

    public boolean onCreate() {

        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.

        // Make sure the variable is a global variable, so it can be referenced from other

        // ContentProvider methods.
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PETS_ID}. This URI is used to provide access to ONE single row
        // of the pets table.

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);

        mDbHelper = new PetDbHelper(getContext());
        return true;

    }


    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */

    @Override

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,

                        String sortOrder) {
        // Get readable database

        db = mDbHelper.getReadableDatabase();


        // This cursor will hold the result of the query

        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code

        int match = sUriMatcher.match(uri);

        switch (match) {

            case PETS:

                // For the PETS code, query the pets table directly with the given

                // projection, selection, selection arguments, and sort order. The cursor

                // could contain multiple rows of the pets table.

                // TODO: Perform database query on pets table
                cursor = db.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, null);
                break;

            case PET_ID:


                selection = PetEntry._ID + "=?";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};


                // This will perform a query on the pets table where the _id equals 3 to return a

                // Cursor containing that row of the table.

                cursor = db.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,

                        null, null, sortOrder);

                break;

            default:

                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
// if we want to be notified of any changes:
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                PetEntry.CONTENT_URI);
        return cursor;
    }


    /**
     * Insert new data into the provider with the given ContentValues.
     */

    @Override

    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case PETS:
                getContext().getContentResolver().notifyChange(uri, null);
                return insertPet(uri, contentValues);

            default:

                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }

    }


    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * <p>
     * for that specific row in the database.
     */

    private Uri insertPet(Uri uri, ContentValues values) {


        db = mDbHelper.getWritableDatabase();
        // TODO: Insert a new pet into the pets database table with the given ContentValues

        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);

        if (weight < 0 && weight != null) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        long id = db.insert(PetEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }


        // Once we know the ID of the new row in the table,

        // return the new URI with the ID appended to the end of it

        return ContentUris.withAppendedId(uri, id);

    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */

    @Override

    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                getContext().getContentResolver().notifyChange(uri, null);
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for");
        }

    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);

            if (weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
            ;
        }

        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);

            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        int newRowId = db.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
        return newRowId;
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */

    @Override

    public int delete(Uri uri, String selection, String[] selectionArgs) {


        db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:

                int rowsDeleted = db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);

                return rowsDeleted;
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }


    /**
     * Returns the MIME type of data for the content URI.
     */

    @Override

    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + "with match " + match);
        }

    }

}