package com.shemasoft.android.bard;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by jv on 7/14/2015.
 */
public class AudioBookPlayer {

    private static AudioBookPlayer audioBookPlayer;
    private Context context;
    private static MediaPlayer mediaPlayer;

    private AudioBookPlayer(Context context) {
        this.context = context;
    }

    public static AudioBookPlayer get(Context context) {
        if(audioBookPlayer == null) {
            audioBookPlayer = new AudioBookPlayer(context.getApplicationContext());
        }
        return audioBookPlayer;
    }

    public void setSource(String filePath, final Callback completionCallback) throws IOException {
        mediaPlayer = MediaPlayer.create(context, Uri.fromFile(new File(filePath)));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completionCallback.onComplete(mp, context);
            }
        });
    }

    public void close() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public boolean playPause() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        return mediaPlayer.isPlaying();
    }

    public void ffwd() {
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 30000);

    }

    public void rew() {
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int millis) {
        mediaPlayer.seekTo(millis);
    }

    public void next() {
        Toast.makeText(this.context, "Hit Next", Toast.LENGTH_SHORT).show();
    }

    public void prev() {
        Toast.makeText(this.context, "Hit Previous", Toast.LENGTH_SHORT).show();
    }

    public interface Callback {

        public void onComplete(MediaPlayer mediaPlayer, Context context);

    }
}

