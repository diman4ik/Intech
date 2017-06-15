package ru.test.intech.androidsongsearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import ru.test.intech.androidsongsearch.data.SuggestionsProvider;
import ru.test.intech.androidsongsearch.search.SearchTask;

public class MainActivity extends AppCompatActivity implements SearchTask.AsyncErrorListener {
    private final String LIST_MODE = "LIST_MODE";
    private boolean mShowAsGrid;
    private boolean mPortrait = true;
    private ProgressBar mBusyIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBusyIndicator = (ProgressBar)findViewById(R.id.busy_indicator);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(LIST_MODE, mShowAsGrid);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mShowAsGrid = savedInstanceState.getBoolean(LIST_MODE);
        mPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        changeView(mShowAsGrid, mPortrait);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenu = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView)searchMenu.getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() >= 5) {
                    performSearch(query);
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    searchView.setIconified(true);

                    SearchRecentSuggestions suggestions =
                            new SearchRecentSuggestions(MainActivity.this,
                                                        SuggestionsProvider.AUTHORITY,
                                                        SuggestionsProvider.MODE);
                    suggestions.saveRecentQuery(query, null);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor= searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(2);//2 is the index of col containing suggestion name.
                searchView.setQuery(suggestion,true);   //setting suggestion
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_changeview) {
            mShowAsGrid = !mShowAsGrid;
            changeView(mShowAsGrid, mPortrait);
            if(mShowAsGrid) {
                item.setIcon(getResources().getDrawable(R.drawable.ic_view_module_black_24dp));
            }
            else {
                item.setIcon(getResources().getDrawable(R.drawable.ic_view_list_black_24dp));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void performSearch(String term) {
        SearchTask searchTask = new SearchTask(this, this);
        searchTask.execute(term);
        mBusyIndicator.setVisibility(View.VISIBLE);
    }

    private void changeView(boolean showAsGrid, boolean portrait) {
        SongsFragment songsFragment =  ((SongsFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_songs));
        songsFragment.changeView(showAsGrid, portrait? 2 : 3);
    }

    @Override
    public void processError(String error) {
        if(error != null) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }

        mBusyIndicator.setVisibility(View.GONE);
    }
}
