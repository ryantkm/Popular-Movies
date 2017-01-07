package com.eventdee.projectmovies.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eventdee.projectmovies.ui.MovieListFragment.OnListFragmentInteractionListener;
import com.eventdee.projectmovies.R;
import com.eventdee.projectmovies.data.MovieContract.MovieEntry;
import com.eventdee.projectmovies.data.MovieDbHelper;
import com.eventdee.projectmovies.object.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMovieRecyclerViewAdapter extends RecyclerView.Adapter<MyMovieRecyclerViewAdapter.ViewHolder> {

    private MovieDbHelper mDbHelper;

    private final ArrayList<Movie> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyMovieRecyclerViewAdapter(ArrayList<Movie> movies, OnListFragmentInteractionListener listener) {
        mValues = movies;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        Picasso.with(holder.mPosterView.getContext()).load("http://image.tmdb.org/t/p/w185/"+mValues.get(position).getPosterPath()).into(holder.mPosterView);

        if (holder.mItem.isFavorited()) {
            holder.mIconFavorite.setAlpha((float) 1.0);
        } else {
            holder.mIconFavorite.setAlpha((float) 0.3);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, String.valueOf(holder.mItem.getId()));
                context.startActivity(intent);
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.mIconFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mItem.isFavorited()) {
                    holder.mItem.setFavorited(false);
                    v.setAlpha((float) 0.3);

                    // delete entry in database
                    mDbHelper = new MovieDbHelper(v.getContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    db.delete(MovieEntry.TABLE_NAME,MovieEntry.COLUMN_MOVIE_ID + " = " + mValues.get(position).getId(), null );

                    Log.v("Database", "row deleted");
                } else {
                    holder.mItem.setFavorited(true);
                    v.setAlpha(1);

                    // insert new entry in database
                    mDbHelper = new MovieDbHelper(v.getContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(MovieEntry.COLUMN_MOVIE_ID, mValues.get(position).getId());
                    values.put(MovieEntry.COLUMN_MOVIE_TITLE, mValues.get(position).getOriginalTitle());
                    values.put(MovieEntry.COLUMN_MOVIE_POSTER_PATH, mValues.get(position).getPosterPath());
                    values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, mValues.get(position).getOverview());
                    values.put(MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, mValues.get(position).getVoteAverage());
                    values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, mValues.get(position).getReleaseDate());
                    values.put(MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, mValues.get(position).getBackdropPath());

                    long newRowId = db.insert(MovieEntry.TABLE_NAME, null, values);
                    Log.v("Database", "New Row ID: " + newRowId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mPosterView;
        public final ImageView mIconFavorite;
        public Movie mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPosterView = (ImageView) view.findViewById(R.id.listPoster);
            mIconFavorite = (ImageView) view.findViewById(R.id.iconFavorite);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
