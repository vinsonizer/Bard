package com.shemasoft.android.bard;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shemasoft.android.bard.model.AudioBook;
import com.shemasoft.android.bard.model.AudioBookFile;
import com.shemasoft.android.bard.model.BardException;

import java.io.File;
import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    private static final String TAG = "PlayerFragment";

    private ImageView playerBookCoverImage;
    private AudioBook audioBook;
    private boolean navButtonBarVisible = false;
    private TextView currentPosition;
    private TextView totalDuration;
    private View rewView;
    private View fwdView;
    private ImageButton playPauseButton;
    private SeekBar seekBar;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);
        setHasOptionsMenu(true);

        // Get all View objects first
        playerBookCoverImage = (ImageView)v.findViewById(R.id.playerBookCoverImage);
        currentPosition = (TextView)v.findViewById(R.id.player_currentPosition);
        totalDuration = (TextView)v.findViewById(R.id.player_totalDuration);
        playPauseButton = (ImageButton) v.findViewById(R.id.player_playPauseButton);
        rewView = (View)v.findViewById(R.id.player_rewView);
        fwdView = (View)v.findViewById(R.id.player_fwdView);
        seekBar = (SeekBar) v.findViewById(R.id.player_seekBar);

        initDisplay();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(null == audioBook) {
            try {
                Snackbar.make(getView(), "AudioBook Not Found", Snackbar.LENGTH_LONG)
                        .setAction("Setup Path", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                jumpToPrefs();
                            }
                        })
                        .setActionTextColor(Color.RED)
                        .show();
            } catch (Exception e) {
                Log.e(TAG, "Exception for snackbar", e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initDisplay();
    }

    private void jumpToPrefs() {
        Intent i = new Intent(getActivity(), PreferencesActivity.class);
        startActivity(i);
    }

    private void initDisplay() {
        try {
            audioBook = AudioBookManager.get(getActivity()).load();
        } catch (BardException be) {
            Log.e(TAG, "Failed to load audiobook", be);
        }

        if(audioBook != null) {
            if (audioBook.getCoverImagePath() != null) {
                File imgFile = new File(audioBook.getCoverImagePath());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    playerBookCoverImage.setImageBitmap(myBitmap);
                }
            }


            AudioBookFile currentFile = audioBook.getBookFiles().get(audioBook.getCurrentFileIndex());

            currentPosition.setText(formatTime(audioBook.getCurrentPosition()));
            totalDuration.setText(formatTime(currentFile.getFileDuration()));

            try {
                AudioBookPlayer.get(getActivity()).setSource(currentFile.getFilePath(), new AudioBookPlayer.Callback() {
                    @Override
                    public void onComplete(MediaPlayer mediaPlayer, Context context) {
                        int fileIndex = audioBook.getCurrentFileIndex() + 1;
                        if (audioBook.getBookFiles().size() > fileIndex) {
                            audioBook.setCurrentFileIndex(fileIndex);
                            try {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(audioBook.getBookFiles().get(fileIndex).getFilePath());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException ioe) {
                                mediaPlayer.stop();
                                Log.e(TAG, "Couldn't load next file", ioe);
                            }
                        } else {
                            mediaPlayer.stop();
                        }
                    }
                });


                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isPlaying = AudioBookPlayer.get(getActivity()).playPause();
                        if (isPlaying) {
                            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                        } else {
                            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                        }
                    }
                });

                rewView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AudioBookPlayer.get(getActivity()).rew();
                    }
                });
                fwdView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AudioBookPlayer.get(getActivity()).ffwd();
                    }
                });
                seekBar.setMax((int) currentFile.getFileDuration());
                final Handler seekBarHandler = new Handler();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int bookCurrentPosition = AudioBookPlayer.get(getActivity()).getCurrentPosition();
                        currentPosition.setText(formatTime(bookCurrentPosition));
                        seekBar.setProgress(bookCurrentPosition / 1000);
                        seekBarHandler.postDelayed(this, 1000);
                    }

                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    int p = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser) {
                            AudioBookPlayer.get(getActivity()).seekTo(progress);
                            p = progress;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seekBar.setProgress(p);
                    }
                });
            } catch (IOException ioe) {
                Log.e(TAG, "Couldn't load audio book file from " + currentFile, ioe);
                Toast.makeText(getActivity(), "Couldn't Load audio book from " + currentFile, Toast.LENGTH_LONG).show();
            }

        }
    }

    private String formatTime(long timeInMilliseconds) {
        StringBuilder timeString = new StringBuilder();
        long timeInSeconds = timeInMilliseconds / 1000;
        long seconds = timeInSeconds % 60;
        long minutes = timeInSeconds / 60 % 60;
        long hours = timeInSeconds / 60 / 60 % 60;
        timeString.append(String.format("%02d:", hours));
        timeString.append(String.format("%02d:", minutes));
        timeString.append(String.format("%02d", seconds));
        return timeString.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                jumpToPrefs();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_player, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioBookPlayer.get(getActivity()).close();
    }
}
