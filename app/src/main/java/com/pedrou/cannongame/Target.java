// Target.java
// Subclass of GameElement customized for the Target.
// Based on Chapter 6 (Cannon Game) of "Android 6 for Programmers".
package com.pedrou.cannongame;

import android.graphics.Canvas;

public class Target extends GameElement {
    private int hitReward; // the hit reward for this Target

    // ------------------------------------------------------------------
    // [BONUS] Fields for the extra features:
    //   - item 4  (smooth animations on the target: scale / fade)
    //   - item 10 (complex target movement: sine / zig-zag + acceleration)
    // ------------------------------------------------------------------

    // movement patterns
    public static final int PATTERN_SINE = 0;
    public static final int PATTERN_ZIGZAG = 1;

    private int baseX;              // horizontal position without the drift
    private int baseWidth;          // original width (before scaling)
    private int baseHeight;         // original height (before scaling)
    private double age;             // seconds this Target has been alive
    private float spawnPercent;     // 0 -> 1 while the spawn animation runs

    private int pattern = PATTERN_SINE;
    private double amplitude;        // horizontal drift amplitude in pixels
    private double period;           // seconds for a complete drift cycle
    private double phase;            // phase offset, so targets are not in sync
    private float acceleration;      // pixels/second^2 added to the speed
    private float maxSpeed;          // speed ceiling in pixels/second

    private static final double SPAWN_DURATION = 0.25; // seconds

    // constructor
    public Target(CannonView view, int color, int hitReward, int x, int y,
                  int width, int length, float velocityY) {
        super(view, color, CannonView.TARGET_SOUND_ID, x, y, width, length,
                velocityY);
        this.hitReward = hitReward;

        baseX = x;
        baseWidth = width;
        baseHeight = length;
        age = 0.0;
        spawnPercent = 0f;

        // harmless defaults so a Target works even without extra tuning
        amplitude = 0.0;
        period = 4.0;
        phase = 0.0;
        acceleration = 0f;
        maxSpeed = 0f;
    }

    // returns the hit reward for this Target
    public int getHitReward() {
        return hitReward;
    }

    // ------------------------------------------------------------------
    // [BONUS] Configuration used by CannonView.newGame()/nextLevel()
    // ------------------------------------------------------------------

    public void setMovement(int pattern, double amplitude, double period,
                            double phase, float acceleration, float maxSpeed) {
        this.pattern = pattern;
        this.amplitude = amplitude;
        this.period = period;
        this.phase = phase;
        this.acceleration = acceleration;
        this.maxSpeed = maxSpeed;
    }

    // updates the hit reward (used when the level increases)
    public void setHitReward(int hitReward) {
        this.hitReward = hitReward;
    }

    // ------------------------------------------------------------------
    // [BONUS] Movement: book behaviour (vertical bounce) plus progressive
    // acceleration and a sinusoidal / zig-zag horizontal drift.
    // ------------------------------------------------------------------
    @Override
    public void update(double interval) {
        // progressive acceleration: keep the direction, grow the magnitude
        float v = getVelocityY();
        if (acceleration > 0f) {
            float sign = (v < 0f) ? -1f : 1f;
            float magnitude = Math.abs(v) + acceleration * (float) interval;
            if (maxSpeed > 0f) {
                magnitude = Math.min(magnitude, maxSpeed);
            }
            setVelocityY(sign * magnitude);
        }

        super.update(interval); // book behaviour: update y and bounce

        age += interval;

        // horizontal drift around the column where this Target was created
        double offset;
        if (pattern == PATTERN_ZIGZAG) {
            // triangle wave between -1 and 1
            double t = (age / period) + (phase / (2.0 * Math.PI));
            double f = t - Math.floor(t); // fractional part, in [0, 1)
            offset = amplitude * (1.0 - 4.0 * Math.abs(f - 0.5));
        } else {
            offset = amplitude * Math.sin((2.0 * Math.PI * age / period) + phase);
        }

        int left = (int) (baseX + offset);
        shape.left = left;
        shape.right = left + baseWidth;

        // [BONUS] spawn animation
        if (spawnPercent < 1f) {
            spawnPercent = (float) Math.min(1.0,
                    spawnPercent + (interval / SPAWN_DURATION));
        }
    }

    // ------------------------------------------------------------------
    // [BONUS] Draw with the spawn animation (scale + fade).
    // ------------------------------------------------------------------
    @Override
    public void draw(Canvas canvas) {
        float scale = getDrawScale();
        int alpha = getDrawAlpha();

        int centerX = shape.centerX();
        int centerY = shape.centerY();
        int halfWidth = (int) (baseWidth * scale / 2f);
        int halfHeight = (int) (baseHeight * scale / 2f);

        paint.setAlpha(alpha);
        canvas.drawRect(centerX - halfWidth, centerY - halfHeight,
                centerX + halfWidth, centerY + halfHeight, paint);
        paint.setAlpha(255); // restore full opacity for other elements
    }

    // [BONUS] scale used while the spawn animation runs (0.6 -> 1.0)
    public float getDrawScale() {
        return 0.6f + 0.4f * spawnPercent;
    }

    // [BONUS] alpha used while the spawn animation runs (0 -> 255)
    public int getDrawAlpha() {
        return (int) (255 * Math.min(1f, spawnPercent * 1.5f));
    }
}
