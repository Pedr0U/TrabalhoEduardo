// MainActivity.java
// MainActivity displays the MainActivityFragment.
// Based on Chapter 6 (Cannon Game) of "Android 6 for Programmers".
package com.pedrou.cannongame;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity
        implements GameResultDialogFragment.OnResetGameListener {

    // called when the app first launches
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // called by the results dialog when the user taps "Reset Game"
    @Override
    public void onResetGame() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment);

        if (fragment instanceof MainActivityFragment) {
            ((MainActivityFragment) fragment).startNewGame();
        }
    }
}
