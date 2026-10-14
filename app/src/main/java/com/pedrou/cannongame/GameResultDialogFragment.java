// GameResultDialogFragment.java
// Shows the results of a finished game and offers a "Reset Game" button.
//
// Chapter 6 of the book shows this dialog from an ANONYMOUS subclass of
// android.app.DialogFragment. The AndroidX version of DialogFragment does not
// allow that: the FragmentManager recreates fragments from their saved state,
// so the class must be public, static and have a public no-argument
// constructor. This class replaces the anonymous fragment of the book and
// keeps the same dialog: the title (you win / you lose), the statistics, the
// local ranking and the reset button.
package com.pedrou.cannongame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class GameResultDialogFragment extends DialogFragment {

    // The Activity that hosts the game implements this interface, so the
    // dialog can ask for a new game without keeping a reference to a View,
    // which would leak memory after a configuration change.
    public interface OnResetGameListener {
        void onResetGame();
    }

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    // the FragmentManager needs a public no-argument constructor
    public GameResultDialogFragment() {
    }

    // creates a dialog that shows the given title and message
    public static GameResultDialogFragment newInstance(String title,
                                                       String message) {
        GameResultDialogFragment fragment = new GameResultDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_TITLE, title);
        arguments.putString(ARG_MESSAGE, message);
        fragment.setArguments(arguments);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle bundle) {
        Bundle arguments = getArguments();
        String title = (arguments == null) ? "" : arguments.getString(ARG_TITLE,
                "");
        String message = (arguments == null) ? ""
                : arguments.getString(ARG_MESSAGE, "");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.reset_game,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyResetGame();
                    }
                });
        builder.setCancelable(false);

        return builder.create();
    }

    // asks the host Activity to start a new game
    private void notifyResetGame() {
        FragmentActivity activity = getActivity();
        if (activity instanceof OnResetGameListener) {
            ((OnResetGameListener) activity).onResetGame();
        }
    }
}
