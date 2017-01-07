package com.eventdee.projectmovies.data;

import android.provider.BaseColumns;

public class MovieContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MovieContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class MovieEntry implements BaseColumns {

        /** Name of database table for pets */
        public final static String TABLE_NAME = "movies";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "original_title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "backdrop_path";

    }
}
