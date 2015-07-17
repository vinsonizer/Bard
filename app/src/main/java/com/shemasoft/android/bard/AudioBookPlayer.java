package com.shemasoft.android.bard;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * @author Jason Vinson
 *
 * Proxy to MediaPlayer to handle media control
 */
public class AudioBookPlayer {

    private static AudioBookPlayer audioBookPlayer;
    private static MediaPlayer mediaPlayer;
    private Context context;

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
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean playPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void ffwd() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 30000);
        }

    }

    public void rew() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
        }
    }

    public Integer getCurrentPosition() {
        Integer result = null;
        if (mediaPlayer != null) {
            result = mediaPlayer.getCurrentPosition();
        }
        return result;
    }

    public void seekTo(int millis) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(millis);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void next() {
        Toast.makeText(this.context, "Hit Next", Toast.LENGTH_SHORT).show();
    }

    public void prev() {
        Toast.makeText(this.context, "Hit Previous", Toast.LENGTH_SHORT).show();
    }

    public interface Callback {

        void onComplete(MediaPlayer mediaPlayer, Context context);

    }
}

