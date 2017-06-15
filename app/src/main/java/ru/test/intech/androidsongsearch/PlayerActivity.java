package ru.test.intech.androidsongsearch;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import ru.test.intech.androidsongsearch.data.Song;


public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    public static final String SONG_KEY = "songname_key";
    private MediaPlayer mPlayer = new MediaPlayer();
    private SeekBar mSongSeekbar;
    private ToggleButton mPlayButton;
    private final Handler mUpdateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        final Song song = getIntent().getParcelableExtra(SONG_KEY);

        ((TextView)findViewById(R.id.song_title_text)).setText(song.getName());
        ((TextView)findViewById(R.id.artist_text)).setText(song.getArtist());

        final ImageView image = (ImageView)findViewById(R.id.artwork);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(song.getArtworkLarge(), image);

        mPlayer.reset();

        try {
            mPlayer.setDataSource(song.getPreviewUrl());
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading track", Toast.LENGTH_LONG).show();
        }

        mSongSeekbar = (SeekBar)findViewById(R.id.song_seekbar);
        mSongSeekbar.setOnSeekBarChangeListener(this);

        mPlayButton = (ToggleButton)findViewById(R.id.startstop_button);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.isPlaying()) {
                    mUpdateHandler.removeCallbacks(mUpdateTimeTask);
                    mPlayer.pause();
                }
                else {
                    mPlayer.start();
                    updateProgressBar();
                }
            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mPlayButton.toggle();
                mSongSeekbar.setProgress(0);
                mSongSeekbar.setMax(100);
            }
        });

        final ProgressBar busyIndicator = (ProgressBar)findViewById(R.id.busy_indicator);

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mPlayButton.setVisibility(View.VISIBLE);
                busyIndicator.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mPlayer.isPlaying()) {
            mPlayButton.toggle();
            mPlayer.pause();
        }
    }

    public void updateProgressBar() {
        mUpdateHandler.postDelayed(mUpdateTimeTask, 100);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mUpdateHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mUpdateHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mPlayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
        mPlayer.seekTo(currentPosition);
        updateProgressBar();
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(mPlayer.isPlaying()) {
                long totalDuration = mPlayer.getDuration();
                long currentDuration = mPlayer.getCurrentPosition();
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                mSongSeekbar.setProgress(progress);
                mUpdateHandler.postDelayed(this, 100);
            }
        }
    };

    int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage =(((double)currentSeconds)/totalSeconds)*100;
        return percentage.intValue();
    }

    int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }
}
