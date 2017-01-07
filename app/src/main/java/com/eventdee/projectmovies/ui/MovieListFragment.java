package com.eventdee.projectmovies.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventdee.projectmovies.BuildConfig;
import com.eventdee.projectmovies.R;
import com.eventdee.projectmovies.data.MovieContract;
import com.eventdee.projectmovies.data.MovieDbHelper;
import com.eventdee.projectmovies.network.ApiClient;
import com.eventdee.projectmovies.network.ApiInterface;
import com.eventdee.projectmovies.object.Movie;
import com.eventdee.projectmovies.object.MoviesResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieListFragment extends Fragment {

    public static String LOG_TAG = MovieListFragment.class.getSimpleName();

    private MovieDbHelper mDbHelper;

    SwipeRefreshLayout mSwipeRefreshLayout;

    // TODO: Customize parameters
    private static int mColumnCount = 2;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    ArrayList<Movie> movieArray;

    private OnListFragmentInteractionListener mListener;

    private MyMovieRecyclerViewAdapter mAdapter;

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MovieListFragment newInstance(int sectionNumber) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        final View recyclerview = view.findViewById(R.id.list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent(view, recyclerview);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        refreshContent(view, recyclerview);

        return view;
    }

    private void refreshContent(View view, View recyclerview) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MoviesResponse> call = null;

        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 0:
                if (isOnline()){
                    call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DATABASE_API_KEY);
                    fetchMovies(recyclerview, call);
                } else {
                    view.findViewById(R.id.empty_state_container).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network is not available", Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                if (isOnline()){
                    call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DATABASE_API_KEY);
                    fetchMovies(recyclerview, call);
                } else {
                    view.findViewById(R.id.empty_state_container).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network is not available", Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if (isOnline()){
                    call = apiService.getUpcomingMovies(BuildConfig.THE_MOVIE_DATABASE_API_KEY);
                    fetchMovies(recyclerview, call);
                } else {
                    view.findViewById(R.id.empty_state_container).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network is not available", Toast.LENGTH_LONG).show();
                }
                break;
            case 3:
                mDbHelper = new MovieDbHelper(this.getContext());
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String[] project = {MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH};
                Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, project, null, null, null, null, null);
                try {

                    // Figure out the index of each column
                    int idColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                    int posterPathColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);

                    movieArray = new ArrayList<Movie>();

                    // Iterate through all the returned rows in the cursor
                    while (cursor.moveToNext()) {
                        // Use that index to extract the String or Int value of the word
                        // at the current row the cursor is on.
                        int currentID = cursor.getInt(idColumnIndex);
                        String posterPath = cursor.getString(posterPathColumnIndex);

                        Movie movie = new Movie(posterPath, currentID, true);
                        movieArray.add(movie);
                    }

                } finally {
                    // Always close the cursor when you're done reading from it. This releases all its
                    // resources and makes it invalid.
                    cursor.close();
                }

                if (movieArray.isEmpty() | movieArray == null){
                    view.findViewById(R.id.empty_state_container).setVisibility(View.GONE);
                    view.findViewById(R.id.empty_state_favorites_container).setVisibility(View.VISIBLE);
                } else {
                    if (recyclerview instanceof RecyclerView) {
                        Context context = view.getContext();
                        RecyclerView recyclerView = (RecyclerView) recyclerview;
                        recyclerView.setLayoutManager(new GridLayoutManager(context, getResources()
                                .getInteger(R.integer.grid_number_cols)));

                        mAdapter = new MyMovieRecyclerViewAdapter(movieArray, mListener);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
                break;
        }
    }

    private void fetchMovies(final View view, Call<MoviesResponse> call) {
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                ArrayList<Movie> favoritesArray = new ArrayList<Movie>();
                movieArray = response.body().getResults();
                Log.d(LOG_TAG, "Number of movies received: " + movieArray.size());
                // Set the adapter

                mDbHelper = new MovieDbHelper(view.getContext());
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

                        Movie movie = new Movie(posterPath, currentID, true);
                        favoritesArray.add(movie);
                    }

                } finally {
                    // Always close the cursor when you're done reading from it. This releases all its
                    // resources and makes it invalid.
                    cursor.close();
                }

                if (favoritesArray != null){
                    for (Movie movie: movieArray){
                        for (int i=0; i < favoritesArray.size(); i++){
                            if (movie.getId().equals(favoritesArray.get(i).getId())){
                                movie.setFavorited(true);
                            }
                        }
                    }
                }

                if (view instanceof RecyclerView) {
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new GridLayoutManager(context, getResources()
                            .getInteger(R.integer.grid_number_cols)));
                    mAdapter = new MyMovieRecyclerViewAdapter(movieArray, mListener);
                    recyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Movie item);
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
}
