package ru.test.intech.androidsongsearch;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import ru.test.intech.androidsongsearch.data.Song;
import ru.test.intech.androidsongsearch.data.SongList;

public class SongsRecyclerAdapter extends RecyclerView.Adapter<SongsRecyclerAdapter.ViewHolder> {
    private boolean mUseTableLayout;
    private Cursor mCursor;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mSongView;
        TextView mArtistView;
        ImageView mImageView;

        ViewHolder(View view) {
            super(view);
            mSongView = (TextView)view.findViewById(R.id.songname_text);
            mArtistView =  (TextView)view.findViewById(R.id.artist_text);
            mImageView = (ImageView)view.findViewById(R.id.artwork);
        }
    }

    public SongsRecyclerAdapter(boolean useTableLayout){
        mUseTableLayout = useTableLayout;
    }

    public void setmUseTableLayout(boolean useTableLayout) {
        mUseTableLayout = useTableLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(mUseTableLayout) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.songtable_item, parent, false);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.songlist_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mSongView.setText(getSong());
        holder.mArtistView.setText(getArtist());

        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
        // which implements ImageAware interface)
        imageLoader.displayImage(getArtworkSmall(), holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        this.mCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    String getSong() {
        return mCursor.getString(mCursor.getColumnIndex(SongList.SongEntry.COLUMN_SONG_NAME));
    }

    String getArtist() {
        return mCursor.getString(mCursor.getColumnIndex(SongList.SongEntry.COLUMN_SONG_ARTIST));
    }

    String getArtworkSmall() {
        return mCursor.getString(mCursor.getColumnIndex(SongList.SongEntry.COLUMN_IMAGE_SMALL_URL));
    }

    String getArtworkLarge() {
        return mCursor.getString(mCursor.getColumnIndex(SongList.SongEntry.COLUMN_IMAGE_LARGE_URL));
    }

    String getSongUrl() {
        return mCursor.getString(mCursor.getColumnIndex(SongList.SongEntry.COLUMN_SONG_PREVIEW_URL));
    }

    public Song getItem(int position) {
        mCursor.moveToPosition(position);
        Song ret = new Song();
        ret.setName(getSong());
        ret.setArtist(getArtist());
        ret.setArtworkSmall(getArtworkSmall());
        ret.setArtworkLarge(getArtworkLarge());
        ret.setPreviewUrl(getSongUrl());
        return ret;
    }
}
