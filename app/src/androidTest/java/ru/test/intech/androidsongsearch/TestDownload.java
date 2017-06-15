package ru.test.intech.androidsongsearch;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.test.intech.androidsongsearch.data.SongList;
import ru.test.intech.androidsongsearch.search.SearchTask;


@RunWith(AndroidJUnit4.class)
public class TestDownload {

    @Test
    public void testAddLocation() {
        Context context = InstrumentationRegistry.getTargetContext();
        SearchTask searchTask = new SearchTask(context, null);

        String json = searchTask.downloadSearch("Rolling");
        Assert.assertTrue(json.length() > 0);
        searchTask.parseResultJSON(json);

        Cursor songsCursor = context.getContentResolver().query(
                SongList.SongEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        Assert.assertTrue(songsCursor.getCount() > 10);
    }
}
