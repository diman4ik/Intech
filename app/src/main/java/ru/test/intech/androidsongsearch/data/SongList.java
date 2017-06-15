package ru.test.intech.androidsongsearch.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class SongList {

    public static final String CONTENT_AUTHORITY = "ru.test.intech.androidsongsearch";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SONGS = "songs";


    public static final class SongEntry implements BaseColumns {
        public static final String TABLE_NAME = PATH_SONGS;

        public static final String COLUMN_SONG_NAME = "song_name";
        public static final String COLUMN_SONG_ARTIST = "song_artist";
        public static final String COLUMN_IMAGE_SMALL_URL = "song_image_small_url";
        public static final String COLUMN_IMAGE_LARGE_URL = "song_image_large_url";
        public static final String COLUMN_SONG_PREVIEW_URL = "song_preview_url";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONGS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SONGS;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
