package com.shemasoft.android.bard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    private ImageView playerBookCoverImage;
    private View navButtonBar;
    private boolean navButtonBarVisible = false;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);

        navButtonBar = (View)v.findViewById(R.id.player_navButtonBar);

        playerBookCoverImage = (ImageView)v.findViewById(R.id.playerBookCoverImage);
        setHasOptionsMenu(true);

        final ImageButton playPauseButton = (ImageButton)v.findViewById(R.id.player_playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPlaying = AudioBookPlayerManager.get(getActivity()).playPause();
                if(isPlaying) {
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });

        final ImageButton prevButton = (ImageButton)v.findViewById(R.id.player_prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioBookPlayerManager.get(getActivity()).prev();
            }
        });

        final ImageButton rewButton = (ImageButton)v.findViewById(R.id.player_rewButton);
        rewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioBookPlayerManager.get(getActivity()).rew();
            }
        });

        final ImageButton ffwdButton = (ImageButton)v.findViewById(R.id.player_ffwdButton);
        ffwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioBookPlayerManager.get(getActivity()).ffwd();
            }
        });

        final ImageButton nextButton = (ImageButton)v.findViewById(R.id.player_nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioBookPlayerManager.get(getActivity()).next();
            }
        });

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_controls:
                if(navButtonBarVisible) {
                    navButtonBar.setVisibility(View.GONE);
                    navButtonBarVisible = false;
                } else {
                    navButtonBar.setVisibility(View.VISIBLE);
                    navButtonBarVisible = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_player, menu);
    }
}
