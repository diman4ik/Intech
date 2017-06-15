package ru.test.intech.androidsongsearch;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import ru.test.intech.androidsongsearch.data.Song;
import ru.test.intech.androidsongsearch.data.SongList;

public class SongsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String BUNDLE_RECYCLER_LAYOUT = "SongsFragment.recycler.layout";
    static final int SONGS_LOADER = 0;
    private RecyclerView mSongsRecyclerView;
    private SongsRecyclerAdapter mSongsAdapter;

    public SongsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSongsAdapter = new SongsRecyclerAdapter(false);

        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);

        mSongsRecyclerView = (RecyclerView) rootView.findViewById(R.id.songlist_recycler_view);
        mSongsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager cardLayoutManager = new LinearLayoutManager(getActivity());
        mSongsRecyclerView.setLayoutManager(cardLayoutManager);
        mSongsRecyclerView.setAdapter(mSongsAdapter);

        mSongsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mSongsRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent answer = new Intent(getActivity(), PlayerActivity.class);
                Song song = mSongsAdapter.getItem(position);
                answer.putExtra(PlayerActivity.SONG_KEY, song);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fade_in, R.anim.fade_out);
                    getActivity().startActivity(answer, options.toBundle());
                }
                else {
                    getActivity().startActivity(answer);
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SONGS_LOADER, null, this);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mSongsRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mSongsRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                SongList.SongEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        mSongsAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSongsAdapter.changeCursor(null);
    }


    public void changeView(boolean showAsGrid, int numCols) {
        mSongsRecyclerView.setLayoutManager(showAsGrid ? new StaggeredGridLayoutManager(numCols, StaggeredGridLayoutManager.VERTICAL) : new LinearLayoutManager(getActivity()));
        mSongsAdapter.setmUseTableLayout(showAsGrid);
        mSongsRecyclerView.setAdapter(mSongsAdapter);
    }

    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
