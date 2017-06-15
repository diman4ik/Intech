package ru.test.intech.androidsongsearch.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "songs.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SONG_TABLE = "CREATE TABLE " + SongList.SongEntry.TABLE_NAME + " (" +
                SongList.SongEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SongList.SongEntry.COLUMN_SONG_ARTIST + " TEXT NOT NULL, " +
                SongList.SongEntry.COLUMN_SONG_NAME + " TEXT NOT NULL, " +
                SongList.SongEntry.COLUMN_IMAGE_SMALL_URL + " TEXT NOT NULL, " +
                SongList.SongEntry.COLUMN_IMAGE_LARGE_URL + " TEXT NOT NULL, " +
                SongList.SongEntry.COLUMN_SONG_PREVIEW_URL + " TEXT NOT NULL);";


        sqLiteDatabase.execSQL(SQL_CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}

