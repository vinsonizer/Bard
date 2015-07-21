package com.shemasoft.android.bard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.shemasoft.android.bard.model.AudioBookLibrary;

import java.io.File;
import java.io.IOException;

/**
 * @author Jason Vinson
 *
 * A Fragment for AudioBook information and playback controls...
 * likely one of the main fragments of this App
 */
public class PlayerFragment extends Fragment {

    public static final String EXTRA_BOOKINDEX = "com.shemasoft.android.bard.PlayerFragment.EXTRA_BOOKINDEX";
    private static final String TAG = "PlayerFragment";
    // Loaded AudioBook
    private AudioBook audioBook;
    // Cover Image for background
    private ImageView playerBookCoverImage;

    // Current position in file
    private TextView currentPosition;
    // Total duration of file
    private TextView totalDuration;

    // FFWD and REW buttons
    private ImageButton rewButton;
    private ImageButton ffwdButton;

    // Floating Action Button for play/pause toggle
    private ImageButton playPauseButton;

    // SeekBar to show current progress and seek in track
    private SeekBar seekBar;
    // Handler for SeekBar callbacks
    private boolean postSeekBarUpdates;

    private int bookIndex = 0;

    public static PlayerFragment newInstance(int bookIndex) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_BOOKINDEX, bookIndex);

        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);
        setHasOptionsMenu(true);

        // Get all View objects first and assign to member vars
        playerBookCoverImage = (ImageView) v.findViewById(R.id.player_bookCoverImage);
        currentPosition = (TextView)v.findViewById(R.id.player_currentPosition);
        totalDuration = (TextView)v.findViewById(R.id.player_totalDuration);
        playPauseButton = (ImageButton) v.findViewById(R.id.player_playPauseButton);
        rewButton = (ImageButton) v.findViewById(R.id.player_rewButton);
        ffwdButton = (ImageButton) v.findViewById(R.id.player_ffButton);
        seekBar = (SeekBar) v.findViewById(R.id.player_seekBar);


        if (null != getArguments())
            bookIndex = getArguments().getInt(EXTRA_BOOKINDEX);

        // First try to load the audiobook that is configured
        try {
            AudioBookLibrary library = AudioBookLibraryManager.get(getActivity()).load();
            if (library.getAudioBooks() != null) {
                audioBook = library.getAudioBooks().get(bookIndex);
            }
        } catch (Exception be) {
            Log.e(TAG, "Failed to load audiobook", be);
        }

        // If the audiobook loads, init the display components with the audiobook config
        if(audioBook != null) {

            getActivity().setTitle(audioBook.getTitle());
            if (audioBook.getCoverImagePath() != null) {
                File imgFile = new File(audioBook.getCoverImagePath());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    playerBookCoverImage.setImageBitmap(myBitmap);
                }
            }

            AudioBookFile currentFile = audioBook.getBookFiles().get(audioBook.getCurrentFileIndex());

            try {
                // TODO: refactor.  I don't like this...
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

                AudioBookPlayer.get(getActivity()).seekTo(audioBook.getCurrentPosition());
                currentPosition.setText(formatTime(audioBook.getCurrentPosition()));
                totalDuration.setText(formatTime(audioBook.getTotalDuration()));


                // TODO: Create custom images for play/pause etc.
                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isPlaying = AudioBookPlayer.get(getActivity()).playPause();
                        if (isPlaying) {
                            playPauseButton.setImageResource(R.drawable.pause);
                        } else {
                            playPauseButton.setImageResource(R.drawable.play);
                        }
                    }
                });

                rewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AudioBookPlayer.get(getActivity()).rew();
                    }
                });
                ffwdButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AudioBookPlayer.get(getActivity()).ffwd();
                    }
                });
                seekBar.setMax((int) currentFile.getFileDuration());

                // Create a Handler to do callbacks for the current position in the SeekBar
                final Handler seekBarHandler = new Handler();
                postSeekBarUpdates = true;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (postSeekBarUpdates) {
                            // Get Position
                            int bookCurrentPosition = AudioBookPlayer.get(getActivity()).getCurrentPosition();
                            // Set in the view
                            currentPosition.setText(formatTime(bookCurrentPosition));
                            // Set the SeekBar value
                            seekBar.setProgress(bookCurrentPosition / 1000);
                            // Update audioBook value so it will be persisted when stopped
                            audioBook.setCurrentPosition(bookCurrentPosition);
                            // Sleep for 1 second
                            seekBarHandler.postDelayed(this, 1000);
                        }
                    }

                });


                // TODO: Make this actually work
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

            // Should probably add an Else here that shows something about bad config...

        }

        return v;
    }

    /**
     * Convenience method for going to Preferences Activity
     */
    private void jumpToPrefs() {
        Intent i = new Intent(getActivity(), PreferencesActivity.class);
        startActivity(i);
    }


    // Simple milliseconds to HH:MM:SS formatter
    private String formatTime(long timeInMilliseconds) {
        long timeInSeconds = timeInMilliseconds / 1000;
        long seconds = timeInSeconds % 60;
        long minutes = timeInSeconds / 60 % 60;
        long hours = timeInSeconds / 60 / 60 % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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

    @Override
    public void onPause() {
        super.onPause();
        postSeekBarUpdates = false;
        AudioBookPlayer.get(getActivity()).stop();
        try {
            AudioBookLibrary library = AudioBookLibraryManager.get(getActivity()).load();
            library.getAudioBooks().add(bookIndex, audioBook);
            AudioBookLibraryManager.get(getActivity()).save(library);
        } catch (Exception e) {
            Log.e(TAG, "Failed to save state", e);
            Toast.makeText(getActivity(), "FIXME: Failed to persist", Toast.LENGTH_LONG);
        }
    }
}
