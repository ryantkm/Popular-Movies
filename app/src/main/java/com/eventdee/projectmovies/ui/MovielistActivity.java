package com.eventdee.projectmovies.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.eventdee.projectmovies.R;
import com.eventdee.projectmovies.data.MovieContract;
import com.eventdee.projectmovies.data.MovieDbHelper;
import com.eventdee.projectmovies.object.Movie;

public class MovielistActivity extends AppCompatActivity implements MovieListFragment.OnListFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private MyMovieRecyclerViewAdapter mAdapter;

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        TabLayout.Tab tabPopular = tabLayout.getTabAt(0);
        tabPopular.setIcon(R.drawable.selector_popular);
        TabLayout.Tab tabTopRated = tabLayout.getTabAt(1);
        tabTopRated.setIcon(R.drawable.selector_top_rated);
        TabLayout.Tab tabUpcoming = tabLayout.getTabAt(2);
        tabUpcoming.setIcon(R.drawable.selector_upcoming);
        TabLayout.Tab tabFavorites = tabLayout.getTabAt(3);
        tabFavorites.setIcon(R.drawable.selector_favorites);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movielist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_database_size) {
            // testing if the database is created properly
            displayDatabaseInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Movie item) {
        Log.v("info", item.toString());
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return MovieListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Popular";
                case 1:
                    return "Top Rated";
                case 2:
                    return "Upcoming";
                case 3:
                    return "Favorites";
                default:
                    return null;
            }
        }
    }

//    private void updateEmptyState() {
//        if (mAdapter.getItemCount() == 0) {
//            if (mSectionsPagerAdapter.getPageTitle(3)== "Favorites") {
//                findViewById(R.id.empty_state_container).setVisibility(View.GONE);
//                findViewById(R.id.empty_state_favorites_container).setVisibility(View.VISIBLE);
//            } else {
//                findViewById(R.id.empty_state_container).setVisibility(View.VISIBLE);
//                findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
//            }
//        } else {
//            findViewById(R.id.empty_state_container).setVisibility(View.GONE);
//            findViewById(R.id.empty_state_favorites_container).setVisibility(View.GONE);
//        }
//    }


    // this method is creating for testing if database is being created correctly
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        MovieDbHelper mDbHelper = new MovieDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
//            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
//            displayView.setText("Number of rows in pets database table: " + cursor.getCount());
            Toast.makeText(this, "Number of rows in pets database table: " + cursor.getCount(), Toast.LENGTH_LONG).show();
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
