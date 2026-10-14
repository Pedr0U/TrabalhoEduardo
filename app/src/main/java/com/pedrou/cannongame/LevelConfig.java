// LevelConfig.java
// Computes the parameters used by each level of the game.
// [BONUS] item 2 "Dificuldade Progressiva (niveis)".
package com.pedrou.cannongame;

public final class LevelConfig {

    // the last level: clearing it wins the game
    public static final int MAX_LEVEL = 5;

    // extra seconds awarded when a level is cleared
    public static final double LEVEL_CLEAR_BONUS_SECONDS = 8.0;

    // the countdown starts at 10 seconds, as in the book
    public static final double STARTING_TIME_SECONDS = 10.0;

    // number of targets that must be destroyed to clear the level
    public static int targetCount(int level) {
        return 9 + (level - 1);
    }

    // multiplier applied to the base target speed
    public static double targetSpeedFactor(int level) {
        return 1.0 + 0.15 * (level - 1);
    }

    // multiplier applied to the base target size (targets get smaller)
    public static double targetSizeFactor(int level) {
        return Math.max(0.55, 1.0 - 0.09 * (level - 1));
    }

    // multiplier applied to the base blocker speed
    public static double blockerSpeedFactor(int level) {
        return 1.0 + 0.20 * (level - 1);
    }

    // seconds deducted when the blocker is hit
    public static int missPenalty(int level) {
        return CannonView.MISS_PENALTY + (level - 1);
    }

    // seconds awarded for each destroyed target
    public static int hitReward(int level) {
        return CannonView.HIT_REWARD + ((level - 1) / 2);
    }

    // seconds a target is exposed before it is removed (0 = never removed)
    public static double exposureSeconds(int level) {
        return 0.0;
    }

    // horizontal drift amplitude of the targets, in pixels
    public static double targetAmplitude(int level, int screenWidth) {
        if (level <= 1) {
            return 0.0;
        }
        return Math.min(0.08 * screenWidth * (level - 1), 0.20 * screenWidth);
    }

    // seconds for a complete horizontal drift cycle
    public static double targetPeriod(int level) {
        return Math.max(1.6, 3.2 - 0.35 * (level - 1));
    }

    // pixels per second added to the target speed while it moves
    public static float targetAcceleration(int level, int screenHeight) {
        return (float) (0.10 * screenHeight * (level - 1));
    }

    // speed ceiling of a target
    public static float targetMaxSpeed(int level, int screenHeight) {
        return (float) (0.9 * screenHeight + 0.15 * screenHeight * level);
    }

    // movement pattern used by target number "index" of the given level
    public static int targetPattern(int level, int index) {
        if (level >= 3 && (index % 3 == 2)) {
            return Target.PATTERN_ZIGZAG;
        }
        return Target.PATTERN_SINE;
    }

    private LevelConfig() {
        // utility class: no instances
    }
}
