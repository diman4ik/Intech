package ru.test.intech.androidsongsearch.search;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import ru.test.intech.androidsongsearch.data.SongList;

public class SearchTask extends AsyncTask<String, Void, String> {

    public interface AsyncErrorListener {
        void processError(String error);
    }

    private final String LOG_TAG = SearchTask.class.getSimpleName();

    private final Context mContext;
    private AsyncErrorListener mErrorListener;

    public SearchTask(Context context, AsyncErrorListener listner) {
        mContext = context;
        mErrorListener = listner;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String query = params[0];
        String result = downloadSearch(query);
        if(result != null) {
            if (!parseResultJSON(result)) {
                return "Error parsing search result";
            }
        }
        else {
            return "Error on search";
        }

        return null;
    }

    @Override
    protected void onPostExecute(String error) {
        if(mErrorListener != null) {
            mErrorListener.processError(error);
        }
    }

    public String downloadSearch(String query) {
        //https://itunes.apple.com/search?term=SEARCH_KEYWORD)
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final String FORECAST_BASE_URL = "https://itunes.apple.com/search?";
            final String QUERY_PARAM = "term";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            // opens input stream from the HTTP connection
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    public boolean parseResultJSON(String searchResult) {
        try {
            JSONObject songsJson = new JSONObject(searchResult);
            int count = songsJson.getInt("resultCount");
            JSONArray songsArray = songsJson.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(songsArray.length());

            for(int i = 0; i < songsArray.length(); i++) {
                JSONObject songJSON = songsArray.getJSONObject(i);

                String artist = songJSON.getString("artistName");
                String song = songJSON.getString("trackName");
                String imageSmallUrl = songJSON.getString("artworkUrl100");
                String imageLargeUrl = songJSON.getString("artworkUrl100");
                if(songJSON.has("artworkUrl600"))
                    imageLargeUrl = songJSON.getString("artworkUrl600");
                String songPreviewUrl =  songJSON.getString("previewUrl");

                ContentValues songValues = new ContentValues();
                songValues.put(SongList.SongEntry.COLUMN_SONG_ARTIST, artist);
                songValues.put(SongList.SongEntry.COLUMN_SONG_NAME, song);
                songValues.put(SongList.SongEntry.COLUMN_IMAGE_SMALL_URL, imageSmallUrl);
                songValues.put(SongList.SongEntry.COLUMN_IMAGE_LARGE_URL, imageLargeUrl);
                songValues.put(SongList.SongEntry.COLUMN_SONG_PREVIEW_URL, songPreviewUrl);

                cVVector.add(songValues);
            }

            if ( cVVector.size() > 0 ) {
                mContext.getContentResolver().delete(SongList.SongEntry.CONTENT_URI, null, null);
                mContext.getContentResolver().bulkInsert(SongList.SongEntry.CONTENT_URI, cVVector.toArray(new ContentValues[cVVector.size()]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

