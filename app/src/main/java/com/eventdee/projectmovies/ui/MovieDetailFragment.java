package com.eventdee.projectmovies.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eventdee.projectmovies.R;
import com.eventdee.projectmovies.data.MovieContract;
import com.eventdee.projectmovies.data.MovieDbHelper;
import com.eventdee.projectmovies.network.ApiClient;
import com.eventdee.projectmovies.network.ApiInterface;
import com.eventdee.projectmovies.object.Movie;
import com.eventdee.projectmovies.object.Review;
import com.eventdee.projectmovies.object.ReviewsResponse;
import com.eventdee.projectmovies.object.Video;
import com.eventdee.projectmovies.object.VideosResponse;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovielistActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    public static String id;
    public static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    static final String API_KEY = "dfee17c08606a565102cfd4a7f64dbfa"; // key in your api key
    ArrayList<Video> videoArray = new ArrayList<Video>();
    ArrayList<Review> reviewArray = new ArrayList<Review>();
    CollapsingToolbarLayout collapsingToolbar;
    MovieDbHelper mDbHelper;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
//    private DummyContent.DummyItem mItem;
    private Movie mItem = new Movie();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            id = getArguments().getString(ARG_ITEM_ID);
        }

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mItem.getOriginalTitle());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        if (id != null) {
            startTask("details");
            startTask("videos");
            startTask("reviews");
        }

        return rootView;
    }

    private void startTask(String apiSelection) {
        if (isOnline()) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            switch (apiSelection) {
                case "details":
                    Call<Movie> call = apiService.getMovieDetails(id, API_KEY);
                    call.enqueue(new Callback<Movie>() {
                        @Override
                        public void onResponse(Call<Movie> call, Response<Movie> response) {
                            mItem.setOriginalTitle(response.body().getOriginalTitle());
                            mItem.setOverview(response.body().getOverview());
                            mItem.setReleaseDate(response.body().getReleaseDate());
                            mItem.setVoteAverage(response.body().getVoteAverage());
                            mItem.setBackdropPath(response.body().getBackdropPath());
                            mItem.setPosterPath(response.body().getPosterPath());
                            mItem.setRuntime(response.body().getRuntime());
                            mItem.setId(Integer.parseInt(id));
                            Log.d(LOG_TAG, "Movie Title: " + mItem.getOriginalTitle());
                            updateUi(mItem);
                        }

                        @Override
                        public void onFailure(Call<Movie> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(LOG_TAG, t.toString());
                        }
                    });
                    break;
                case "videos":
                    Call<VideosResponse> videosCall = apiService.getVideos(id, API_KEY);
                    videosCall.enqueue(new Callback<VideosResponse>() {
                        @Override
                        public void onResponse(Call<VideosResponse> call, Response<VideosResponse> response) {

                            videoArray = response.body().getResults();
                            final ArrayList<String> keys = new ArrayList<String>();
                            final ArrayList<String> names = new ArrayList<String>();

                            for (int i = 0; i < videoArray.size(); i++) {
                                String key = videoArray.get(i).getKey();
                                String name = videoArray.get(i).getName();
                                keys.add(key);
                                names.add(name);
                            }

                            Log.d(LOG_TAG, "YouTube Key: " + keys.size());

                            ExpandableHeightListView listViewVideo = (ExpandableHeightListView) getActivity().findViewById(R.id.listViewVideo);
                            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, names);
                            listViewVideo.setAdapter(adapter);
                            listViewVideo.setExpanded(true);

                            listViewVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + keys.get(position));
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    //check that there is a broswer installed to open this intent
                                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                        startActivity(intent);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<VideosResponse> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(LOG_TAG, t.toString());
                        }
                    });
                    break;
                case "reviews":
                    Call<ReviewsResponse> reviewsCall = apiService.getReviews(id, API_KEY);
                    reviewsCall.enqueue(new Callback<ReviewsResponse>() {
                        @Override
                        public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {

                            reviewArray = response.body().getResults();
//                            final ArrayList<String> reviews = new ArrayList<String>();
//
//                            for (int i = 0; i < reviewArray.size(); i++) {
//                                String review = reviewArray.get(i).getContent();
//                                reviews.add(review);
//                            }

                            Log.d(LOG_TAG, "Number of reviews: " + reviewArray.size());

                            ExpandableHeightListView listViewReview = (ExpandableHeightListView) getActivity().findViewById(R.id.listViewReview);
//                            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, reviews);

                            ArrayAdapter<Review> reviewAdapter = new ArrayAdapter<Review>(getContext(), 0, reviewArray) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    Review currentReview = reviewArray.get(position);
                                    if (convertView == null) {
                                        convertView = getActivity().getLayoutInflater().inflate(R.layout.review_detail, null, false);
                                        TextView author =
                                                (TextView) convertView.findViewById(R.id.author);
                                        TextView review =
                                                (TextView) convertView.findViewById(R.id.review);

                                        author.setText(currentReview.getAuthor());
                                        review.setText(currentReview.getContent());

                                        return convertView;
                                    }
                                    return convertView;
                                }
                            };
                            listViewReview.setAdapter(reviewAdapter);
                            listViewReview.setExpanded(true);
                        }

                        @Override
                        public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(LOG_TAG, t.toString());
                        }
                    });
                    break;
            }

        } else {
            Toast.makeText(getContext(), "Network is not available", Toast.LENGTH_LONG).show();
        }
    }

    //check if there is wifi or internet connectivity
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void updateUi(Movie movie) {

        TextView dTitle = (TextView) getActivity().findViewById(R.id.dTitle);
        TextView doverview = (TextView) getActivity().findViewById(R.id.doverview);
        TextView dreleaseDate = (TextView) getActivity().findViewById(R.id.dreleaseDate);
        TextView dvoteAverage = (TextView) getActivity().findViewById(R.id.dvoteAverage);
        ImageView backdropImageView = (ImageView) getActivity().findViewById(R.id.backdropImageView);
        ImageView posterImageView = (ImageView) getActivity().findViewById(R.id.posterImageView);
        ImageView backdropPlaceholder = (ImageView) getActivity().findViewById(R.id.backdropPlaceholder);
        TextView drunTime = (TextView) getActivity().findViewById(R.id.druntime);
        TextView titlePlaceholder = (TextView) getActivity().findViewById(R.id.titlePlaceholder);

        dTitle.setText(mItem.getOriginalTitle());
        doverview.setText(mItem.getOverview());
        dreleaseDate.setText(mItem.getReleaseDate().substring(0, 4));
        dvoteAverage.setText(String.valueOf(mItem.getVoteAverage()) + "/10");
        drunTime.setText(String.valueOf(mItem.getRuntime()) + " mins");

        collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(mItem.getOriginalTitle());
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w342/" + mItem.getBackdropPath()).into(backdropImageView);
        } else {
            backdropPlaceholder.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w342/" + mItem.getBackdropPath()).into(backdropPlaceholder);
            titlePlaceholder.setText(mItem.getOriginalTitle());
        }

        Picasso.with(getContext())
                .load("http://image.tmdb.org/t/p/w185/" + mItem.getPosterPath())
//                .placeholder(R.drawable.user_placeholder)
//                .error(R.drawable.user_placeholder_error)
                .into(posterImageView);

        final ImageView detailFavoriteIcon = (ImageView) getActivity().findViewById(R.id.detailFavoriteIcon);
        ArrayList<Movie> favorites = new ArrayList<Movie>();

        mDbHelper = new MovieDbHelper(this.getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] project = {MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH};
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, project, null, null, null, null, null);
        try {

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int posterPathColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String posterPath = cursor.getString(posterPathColumnIndex);

                movie = new Movie(posterPath, currentID, true);
                favorites.add(movie);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }

        if (favorites != null) {
            for (int i = 0; i < favorites.size(); i++) {
                if (mItem.getId().equals(favorites.get(i).getId())) {
                    detailFavoriteIcon.setAlpha((float) 1.0);
                    mItem.setFavorited(true);
                }
            }
        }

        detailFavoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItem.isFavorited()) {
                    detailFavoriteIcon.setAlpha((float) 0.3);
                    mItem.setFavorited(false);
                    mDbHelper = new MovieDbHelper(v.getContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mItem.getId(), null);
                } else {
                    detailFavoriteIcon.setAlpha((float) 1.0);
                    mItem.setFavorited(true);
                    // insert new entry in database
                    mDbHelper = new MovieDbHelper(v.getContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mItem.getId());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mItem.getOriginalTitle());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, mItem.getPosterPath());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, mItem.getOverview());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, mItem.getVoteAverage());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, mItem.getReleaseDate());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, mItem.getBackdropPath());

                    long newRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                    Log.v("Database", "New Row ID: " + newRowId);
                }
            }
        });
    }
}
