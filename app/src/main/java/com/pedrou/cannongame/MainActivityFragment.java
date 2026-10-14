// MainActivityFragment.java
// MainActivityFragment creates and manages a CannonView.
// Based on Chapter 6 (Cannon Game) of "Android 6 for Programmers".
package com.pedrou.cannongame;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainActivityFragment extends Fragment {
    private CannonView cannonView; // custom view that displays the game

    // called when the Fragment's view needs to be created
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // inflate the fragment_main.xml layout
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // get a reference to the CannonView
        cannonView = (CannonView) view.findViewById(R.id.cannonView);
        return view;
    }

    // set up the volume control once the Activity exists
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // allow the volume buttons to set the game volume
        if (getActivity() != null) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    // starts a new game; called by MainActivity when the results dialog asks
    // the player to play again
    public void startNewGame() {
        if (cannonView != null) {
            cannonView.newGame();
        }
    }

    // when MainActivity is paused, terminate the game
    @Override
    public void onPause() {
        super.onPause();
        if (cannonView != null) {
            cannonView.stopGame(); // terminates the game
        }
    }

    // when MainActivity is resumed, restart the game loop
    @Override
    public void onResume() {
        super.onResume();
        if (cannonView != null) {
            cannonView.resumeGame();
        }
    }

    // when the Fragment is destroyed, release the game resources
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cannonView != null) {
            cannonView.releaseResources();
        }
    }
}
