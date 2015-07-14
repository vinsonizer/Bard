package com.shemasoft.android.bard;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jv on 7/14/2015.
 */
public class AudioBookPlayerManager {

    private static AudioBookPlayerManager audioBookPlayerManager;
    private Context context;
    private boolean isPlaying = false;

    private AudioBookPlayerManager(Context context) {
        this.context = context;
    }

    public static AudioBookPlayerManager get(Context context) {
        if(audioBookPlayerManager == null) {
            audioBookPlayerManager = new AudioBookPlayerManager(context.getApplicationContext());
        }
        return audioBookPlayerManager;
    }

    public boolean playPause() {
        if(isPlaying) {
            Toast.makeText(this.context, "Hit Pause", Toast.LENGTH_SHORT).show();
            isPlaying = false;
        } else {
            Toast.makeText(this.context, "Hit Play", Toast.LENGTH_SHORT).show();
            isPlaying = true;
        }
        return isPlaying;
    }


    public void ffwd() {
        Toast.makeText(this.context, "Hit Fast Forward", Toast.LENGTH_SHORT).show();

    }

    public void rew() {
        Toast.makeText(this.context, "Hit Rewind", Toast.LENGTH_SHORT).show();

    }

    public void next() {
        Toast.makeText(this.context, "Hit Next", Toast.LENGTH_SHORT).show();
    }

    public void prev() {
        Toast.makeText(this.context, "Hit Previous", Toast.LENGTH_SHORT).show();
    }

    public void stop() {
        Toast.makeText(this.context, "Hit Stop", Toast.LENGTH_SHORT).show();
    }
}

