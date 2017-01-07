package com.eventdee.projectmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.eventdee.projectmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "movies.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link MovieDbHelper}.
     *
     * @param context of the app
     */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the movies table
        String SQL_CREATE_MOVIES_TABLE =  "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
                + MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
