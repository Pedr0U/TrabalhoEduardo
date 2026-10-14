// Blocker.java
// Subclass of GameElement customized for the Blocker.
// Based on Chapter 6 (Cannon Game) of "Android 6 for Programmers".
package com.pedrou.cannongame;

public class Blocker extends GameElement {
    private int missPenalty; // the miss penalty for this Blocker

    // constructor
    public Blocker(CannonView view, int color, int missPenalty, int x,
                   int y, int width, int length, float velocityY) {
        super(view, color, CannonView.BLOCKER_SOUND_ID, x, y, width, length,
                velocityY);
        this.missPenalty = missPenalty;
    }

    // returns the miss penalty for this Blocker
    public int getMissPenalty() {
        return missPenalty;
    }

    // ------------------------------------------------------------------
    // [BONUS] Progressive difficulty: the miss penalty grows with the level
    // and the blocker's vertical speed can be scaled.
    // ------------------------------------------------------------------

    // updates the miss penalty (used when the level increases)
    public void setMissPenalty(int missPenalty) {
        this.missPenalty = missPenalty;
    }
}
