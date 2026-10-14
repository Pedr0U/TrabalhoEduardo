// CannonView.java
// Displays and controls the Cannon Game.
// Based on Chapter 6 (Cannon Game) of "Android 6 for Programmers".
//
// Sections marked with [BONUS] implement the optional extra resources listed
// in "Recursos Extras Sugeridos para o Cannon Game.pdf". Everything else keeps
// the structure and the behaviour of the book.
package com.pedrou.cannongame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Random;

public class CannonView extends SurfaceView
        implements SurfaceHolder.Callback {

    private static final String TAG = "CannonView"; // for logging errors

    // constants for game play (from the book)
    public static final int MISS_PENALTY = 2; // seconds deducted on a miss
    public static final int HIT_REWARD = 3;   // seconds added on a hit

    // constants for the Cannon
    public static final double CANNON_BASE_RADIUS_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_WIDTH_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_LENGTH_PERCENT = 1.0 / 10;

    // constants for the Cannonball
    public static final double CANNONBALL_RADIUS_PERCENT = 3.0 / 80;
    public static final double CANNONBALL_SPEED_PERCENT = 3.0 / 2;

    // constants for the Targets
    public static final double TARGET_WIDTH_PERCENT = 1.0 / 40;
    public static final double TARGET_LENGTH_PERCENT = 3.0 / 20;
    public static final double TARGET_FIRST_X_PERCENT = 3.0 / 5;
    public static final double TARGET_SPACING_PERCENT = 1.0 / 60;
    public static final double TARGET_PIECES = 9;
    public static final double TARGET_MIN_SPEED_PERCENT = 3.0 / 4;
    public static final double TARGET_MAX_SPEED_PERCENT = 6.0 / 4;

    // constants for the Blocker
    public static final double BLOCKER_WIDTH_PERCENT = 1.0 / 40;
    public static final double BLOCKER_LENGTH_PERCENT = 1.0 / 4;
    public static final double BLOCKER_X_PERCENT = 1.0 / 2;
    public static final double BLOCKER_SPEED_PERCENT = 1.0;

    // text size is 1/18 of the screen width
    public static final double TEXT_SIZE_PERCENT = 1.0 / 18;

    // milliseconds to wait when the SurfaceView is not ready to be drawn yet
    private static final long SURFACE_WAIT_MS = 16;

    // tag used to show the results dialog through the FragmentManager
    private static final String GAME_RESULT_TAG = "game_result";

    // constants for managing sounds
    public static final int TARGET_SOUND_ID = 0;
    public static final int CANNON_SOUND_ID = 1;
    public static final int BLOCKER_SOUND_ID = 2;
    public static final int FAIL_SOUND_ID = 3;   // [BONUS] item 3
    public static final int LEVEL_SOUND_ID = 4;  // [BONUS] item 3

    // ------------------------------------------------------------------
    // [BONUS] item 1 "Sistema de Pontuacao Melhorado"
    // ------------------------------------------------------------------
    private static final int POINTS_PER_HIT = 100;
    private static final int STREAK_STEP = 3;   // hits needed per +1 multiplier
    private static final int MAX_MULTIPLIER = 5;

    // ------------------------------------------------------------------
    // [BONUS] item 5 "Controle por Toque Direcional (Swipe Control)"
    // ------------------------------------------------------------------
    private static final float MIN_POWER = 0.4f;
    private static final float MAX_POWER = 1.8f;

    // ------------------------------------------------------------------
    // [BONUS] item 7 "Tela de Game Over e Restart aprimorada"
    // ------------------------------------------------------------------
    private static final double GAME_OVER_ANIM_SECONDS = 0.6;

    // [BONUS] item 3: volume of the continuous background track
    private static final float MUSIC_VOLUME = 0.35f;

    private CannonThread cannonThread;   // controls the game loop
    private Activity activity;           // used to show the dialog

    // game objects
    private Cannon cannon;
    private Blocker blocker;
    private ArrayList<Target> targets;
    private ParticleSystem particles;    // [BONUS] item 8

    // dimension variables
    private int screenWidth;
    private int screenHeight;

    // variables for the game loop and statistics
    private volatile boolean gameOver;
    private boolean gameWon;
    private boolean gameFinished;        // [BONUS] guards the end animation
    private double gameOverAnimTimer;     // [BONUS] seconds left in the animation
    private double timeLeft;             // time remaining, in seconds
    private int shotsFired;              // shots the user has fired
    private double totalElapsedTime;     // elapsed seconds

    // [BONUS] statistics used by the improved score system and the dialog
    private int score;                   // accumulated score
    private int level;                   // current level
    private int streak;                  // consecutive hits without a miss
    private int multiplier;              // current score multiplier
    private int targetsDestroyed;        // hits, used to compute the accuracy

    // [BONUS] item 6 "Modo Noturno / Modo Dia": active theme
    private GameTheme theme = GameTheme.DAY;

    // [BONUS] item 9 "Ranking Local (Highscores)"
    private HighScoreStore highScoreStore;

    // [BONUS] item 5: swipe tracking
    private float lastSwipeX;
    private float lastSwipeY;
    private boolean swipeActive;

    // constants and variables for managing sounds
    private SoundPool soundPool;
    private SparseIntArray soundMap;
    private MediaPlayer musicPlayer;      // [BONUS] item 3: continuous track
    private boolean soundEnabled = true;  // [BONUS] item 3: mute toggle

    // Paint variables used when drawing each item on the screen
    private Paint textPaint;         // Paint used to draw the main HUD text
    private Paint hudPaint;          // [BONUS] smaller text for the extra HUD
    private Paint backgroundPaint;   // Paint used to clear the drawing area
    private Paint horizonPaint;      // [BONUS] horizon band of the theme
    private Paint buttonPaint;       // [BONUS] HUD buttons

    // [BONUS] on-screen HUD buttons
    private Rect themeButton;
    private Rect muteButton;

    // constructor
    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs);          // call the superclass constructor
        activity = (Activity) context;  // store a reference to MainActivity

        // register the SurfaceHolder.Callback listener
        getHolder().addCallback(this);

        // configure the audio attributes for game audio
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);

        // initialize the SoundPool with the game's sound effects
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(5);
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();

        // create a map of sounds and pre-load them
        soundMap = new SparseIntArray(5);
        soundMap.put(TARGET_SOUND_ID,
                soundPool.load(context, R.raw.target_hit, 1));
        soundMap.put(CANNON_SOUND_ID,
                soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(BLOCKER_SOUND_ID,
                soundPool.load(context, R.raw.blocker_hit, 1));
        // [BONUS] item 3: extra sound effects
        soundMap.put(FAIL_SOUND_ID,
                soundPool.load(context, R.raw.fail, 1));
        soundMap.put(LEVEL_SOUND_ID,
                soundPool.load(context, R.raw.level_up, 1));

        // [BONUS] item 3: simple continuous background track
        musicPlayer = MediaPlayer.create(context, R.raw.music);
        if (musicPlayer != null) {
            musicPlayer.setLooping(true);
            musicPlayer.setVolume(MUSIC_VOLUME, MUSIC_VOLUME);
        }

        // [BONUS] item 9: local ranking
        highScoreStore = new HighScoreStore(context);

        // [BONUS] item 8: explosion particles
        particles = new ParticleSystem();

        textPaint = new Paint();
        hudPaint = new Paint();
        backgroundPaint = new Paint();
        horizonPaint = new Paint();
        buttonPaint = new Paint();
        buttonPaint.setAntiAlias(true);
        backgroundPaint.setColor(theme.skyColor);

        themeButton = new Rect();
        muteButton = new Rect();
    }

    // called when the size of the SurfaceView changes,
    // such as when it is first added to the View hierarchy
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;  // store the CannonView's width
        screenHeight = h; // store the CannonView's height

        // configure the text properties
        textPaint.setTextSize((int) (TEXT_SIZE_PERCENT * screenHeight));
        textPaint.setAntiAlias(true); // smoothes the text

        // [BONUS] smaller text used by the extra HUD lines
        hudPaint.setTextSize(textPaint.getTextSize() * 0.55f);
        hudPaint.setAntiAlias(true);

        // [BONUS] lay out the on-screen buttons
        layoutHudButtons();
    }

    // [BONUS] positions the two HUD buttons in the top-right corner
    private void layoutHudButtons() {
        int margin = (int) (screenWidth * 0.02);
        int buttonWidth = (int) (screenWidth * 0.14);
        int buttonHeight = (int) (screenHeight * 0.10);

        muteButton.set(screenWidth - buttonWidth - margin, margin,
                screenWidth - margin, margin + buttonHeight);

        themeButton.set(screenWidth - 2 * buttonWidth - 2 * margin, margin,
                screenWidth - buttonWidth - 2 * margin, margin + buttonHeight);
    }

    // returns the width of the game screen
    public int getScreenWidth() {
        return screenWidth;
    }

    // returns the height of the game screen
    public int getScreenHeight() {
        return screenHeight;
    }

    // plays a sound with the given soundId in soundMap
    public void playSound(int soundId) {
        if (!soundEnabled || soundPool == null || soundMap == null) {
            return;
        }
        int streamId = soundMap.get(soundId, 0);
        if (streamId != 0) {
            soundPool.play(streamId, 1, 1, 1, 0, 1f);
        }
    }

    // ------------------------------------------------------------------
    // [BONUS] item 6: current visual theme, used by the dialog
    // ------------------------------------------------------------------
    public GameTheme getTheme() {
        return theme;
    }

    // ------------------------------------------------------------------
    // [BONUS] item 2: creates the targets for the given level
    // ------------------------------------------------------------------
    private void createTargets(int level) {
        targets = new ArrayList<>();
        Random random = new Random();

        int count = LevelConfig.targetCount(level);
        double speedFactor = LevelConfig.targetSpeedFactor(level);
        double sizeFactor = LevelConfig.targetSizeFactor(level);

        int targetWidth = (int) (TARGET_WIDTH_PERCENT * screenWidth * sizeFactor);
        int targetLength = (int) (TARGET_LENGTH_PERCENT * screenHeight * sizeFactor);

        // Y coordinate of the targets (same row as the book)
        int targetY = (int) ((0.5 - TARGET_LENGTH_PERCENT / 2) * screenHeight);

        // keep the book's spacing, but shift the row left if a higher level
        // adds more targets than fit between 60% and the right edge
        double stepPercent = TARGET_WIDTH_PERCENT + TARGET_SPACING_PERCENT;
        double firstPercent = TARGET_FIRST_X_PERCENT;
        double neededLast = firstPercent + (count - 1) * stepPercent
                + TARGET_WIDTH_PERCENT;
        if (neededLast > 0.98) {
            firstPercent = Math.max(0.03,
                    0.98 - ((count - 1) * stepPercent + TARGET_WIDTH_PERCENT));
        }

        int targetX = (int) (firstPercent * screenWidth);

        for (int n = 0; n < count; n++) {
            // random speed between the minimum and the maximum, scaled by level
            double velocity = screenHeight * (random.nextDouble()
                    * (TARGET_MAX_SPEED_PERCENT - TARGET_MIN_SPEED_PERCENT)
                    + TARGET_MIN_SPEED_PERCENT) * speedFactor;

            // alternate the target colours
            int color = (n % 2 == 0) ? theme.targetEvenColor
                    : theme.targetOddColor;

            velocity *= -1; // reverse the initial velocity

            Target target = new Target(this, color,
                    LevelConfig.hitReward(level), targetX, targetY, targetWidth,
                    targetLength, (float) velocity);

            // [BONUS] complex movement (item 10)
            target.setMovement(LevelConfig.targetPattern(level, n),
                    LevelConfig.targetAmplitude(level, screenWidth),
                    LevelConfig.targetPeriod(level),
                    n * 0.7,
                    LevelConfig.targetAcceleration(level, screenHeight),
                    LevelConfig.targetMaxSpeed(level, screenHeight));

            targets.add(target);

            // position the next target further to the right
            targetX += (int) (stepPercent * screenWidth);
        }
    }

    // ------------------------------------------------------------------
    // [BONUS] item 2: creates the blocker for the given level
    // ------------------------------------------------------------------
    private void createBlocker(int level) {
        float speed = (float) (BLOCKER_SPEED_PERCENT * screenHeight
                * LevelConfig.blockerSpeedFactor(level));

        blocker = new Blocker(this, theme.blockerColor,
                LevelConfig.missPenalty(level),
                (int) (BLOCKER_X_PERCENT * screenWidth),
                (int) ((0.5 - BLOCKER_LENGTH_PERCENT / 2) * screenHeight),
                (int) (BLOCKER_WIDTH_PERCENT * screenWidth),
                (int) (BLOCKER_LENGTH_PERCENT * screenHeight),
                speed);
    }

    // ------------------------------------------------------------------
    // [BONUS] item 6: applies the current theme to the game objects
    // ------------------------------------------------------------------
    private void applyThemeColors() {
        if (cannon != null) {
            cannon.setCannonColor(theme.cannonColor);
        }
        if (blocker != null) {
            blocker.setColor(theme.blockerColor);
        }
        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                targets.get(i).setColor((i % 2 == 0) ? theme.targetEvenColor
                        : theme.targetOddColor);
            }
        }
        backgroundPaint.setColor(theme.skyColor);
    }

    // resets all the screen elements and starts a new game
    public void newGame() {
        // construct a new Cannon
        cannon = new Cannon(this,
                (int) (CANNON_BASE_RADIUS_PERCENT * screenHeight),
                (int) (CANNON_BARREL_LENGTH_PERCENT * screenWidth),
                (int) (CANNON_BARREL_WIDTH_PERCENT * screenHeight));

        // [BONUS] reset the extra statistics
        score = 0;
        level = 1;
        streak = 0;
        multiplier = 1;
        targetsDestroyed = 0;
        gameWon = false;
        gameFinished = false;
        gameOverAnimTimer = 0.0;
        particles.clear();
        swipeActive = false;

        applyThemeColors();
        createTargets(level);
        createBlocker(level);

        timeLeft = LevelConfig.STARTING_TIME_SECONDS;
        shotsFired = 0;
        totalElapsedTime = 0.0;

        gameOver = false;

        // start (or restart) the game loop only after the state is ready, so
        // the thread never draws a half-initialized game
        if (cannonThread == null || !cannonThread.isRunning()) {
            joinPreviousThread();
            cannonThread = new CannonThread(getHolder());
            cannonThread.setRunning(true);
            cannonThread.start();
        }

        hideSystemBars();
    }

    // makes sure the previous loop thread has really finished before a new one
    // starts, so that two threads never draw on the same SurfaceHolder
    private void joinPreviousThread() {
        CannonThread thread = cannonThread;
        if (thread == null || thread.isRunning()) {
            return;
        }
        try {
            thread.join(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Interrupted while joining the game thread", e);
        }
    }

    // ------------------------------------------------------------------
    // [BONUS] item 2: advances to the next level without ending the game
    // ------------------------------------------------------------------
    private void advanceLevel() {
        level++;
        timeLeft += LevelConfig.LEVEL_CLEAR_BONUS_SECONDS;
        streak = 0;
        multiplier = 1;

        playSound(LEVEL_SOUND_ID);
        particles.burst(screenWidth * 0.5f, screenHeight * 0.4f,
                theme.particleColor, 40);

        createTargets(level);
        createBlocker(level);
        applyThemeColors();
    }

    // called repeatedly by the CannonThread to update the game elements
    private void updatePositions(double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0; // convert to seconds

        // [BONUS] item 7: let the end-of-game animation play out
        if (gameOver) {
            particles.update(interval);
            if (!gameFinished) {
                gameOverAnimTimer -= interval;
                if (gameOverAnimTimer <= 0.0) {
                    gameFinished = true;
                    finishGame();
                }
            }
            return;
        }

        if (cannon == null || blocker == null) {
            return; // nothing to update yet
        }

        // update the Cannonball's position if it is on the screen
        if (cannon.getCannonball() != null) {
            boolean wasOnScreen = cannon.getCannonball().isOnScreen();
            cannon.getCannonball().update(interval);

            // [BONUS] item 1: a shot that leaves the screen without a hit
            // breaks the streak
            if (wasOnScreen && !cannon.getCannonball().isOnScreen()) {
                registerMiss();
            }
        }

        blocker.update(interval); // update the Blocker's position

        for (GameElement target : targets) {
            target.update(interval); // update each Target's position
        }

        particles.update(interval); // [BONUS] item 8

        timeLeft -= interval; // subtract from the time left

        // if the timer reached zero
        if (timeLeft <= 0.0) {
            timeLeft = 0.0;
            endGame(false); // the player lost
            return;
        }

        // if all the targets were destroyed
        if (targets.isEmpty()) {
            if (level >= LevelConfig.MAX_LEVEL) {
                endGame(true); // the player won the whole game
            } else {
                advanceLevel(); // [BONUS] item 2: go to the next level
            }
        }
    }

    // [BONUS] item 1: resets the streak when a shot is wasted
    private void registerMiss() {
        streak = 0;
        multiplier = 1;
    }

    // [BONUS] item 7: starts the end-of-game sequence
    private void endGame(boolean won) {
        if (gameOver) {
            return;
        }

        gameOver = true;
        gameWon = won;
        gameFinished = false;
        gameOverAnimTimer = GAME_OVER_ANIM_SECONDS;

        if (!won) {
            playSound(FAIL_SOUND_ID); // [BONUS] item 3: failure sound
        }

        // [BONUS] item 7/8: celebration or defeat particles
        particles.burst(screenWidth * 0.5f, screenHeight * 0.4f,
                theme.particleColor, won ? 60 : 20);
    }

    // [BONUS] item 7: stops the loop and shows the improved result dialog
    private void finishGame() {
        if (cannonThread != null) {
            cannonThread.setRunning(false);
        }

        boolean isNewHighScore = highScoreStore.isHighScore(score);
        highScoreStore.add(score, level); // [BONUS] item 9

        showGameOverDialog(gameWon ? R.string.win : R.string.lose,
                isNewHighScore);
    }

    // aligns the barrel and fires a Cannonball if a Cannonball is not already
    // on the screen
    public void alignAndFireCannonball(MotionEvent event) {
        // get the location of the touch in this view
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());

        // compute the touch's distance from the centre of the screen on the
        // y-axis
        double centerMinusY = (screenHeight / 2.0 - touchPoint.y);

        // calculate the angle the barrel makes with the horizontal
        double angle = Math.atan2(touchPoint.x, centerMinusY);

        // point the barrel at the point where the screen was touched
        cannon.align(angle);

        fireCannonball();
    }

    // [BONUS] split out of alignAndFireCannonball so the swipe control can
    // fire without re-aligning the barrel to the finger position
    private void fireCannonball() {
        if (cannon.getCannonball() == null
                || !cannon.getCannonball().isOnScreen()) {
            cannon.fireCannonball();
            ++shotsFired;
        }
    }

    // builds the message shown in the improved Game Over dialog
    private String buildResultsMessage(boolean isNewHighScore) {
        float accuracy = (shotsFired == 0) ? 0f
                : (100f * targetsDestroyed / shotsFired);

        String base = getResources().getString(R.string.results_format,
                shotsFired, (float) totalElapsedTime);

        String extra = getResources().getString(R.string.results_bonus_format,
                score, level, accuracy, highScoreStore.getHighScore());

        StringBuilder builder = new StringBuilder();
        builder.append(base).append("\n\n").append(extra);

        if (isNewHighScore) {
            builder.append("\n\n")
                    .append(getResources().getString(R.string.new_high_score));
        }

        builder.append("\n\n")
                .append(getResources().getString(R.string.ranking_header))
                .append('\n')
                .append(highScoreStore.getRankingText());

        return builder.toString();
    }

    // displays the results dialog when the game ends
    private void showGameOverDialog(final int messageId,
                                    final boolean isNewHighScore) {
        // the texts are built here, because they depend on the final statistics
        final String title = getResources().getString(messageId);
        final String message = buildResultsMessage(isNewHighScore);

        // in the GUI thread, use the FragmentManager to show the dialog
        activity.runOnUiThread(new Runnable() {
            public void run() {
                // never show the results twice for the same game
                if (isResultsDialogDisplayed()) {
                    return;
                }

                showSystemBars(); // leave immersive mode
                GameResultDialogFragment.newInstance(title, message)
                        .show(((FragmentActivity) activity)
                                        .getSupportFragmentManager(),
                                GAME_RESULT_TAG);
            }
        });
    }

    // draws the background of the play field
    private void drawBackground(Canvas canvas) {
        // clear the background using the current theme colour
        backgroundPaint.setColor(theme.skyColor);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
                backgroundPaint);

        // [BONUS] item 6: horizon band behind the targets
        horizonPaint.setColor(theme.horizonColor);
        canvas.drawRect(0, 0, canvas.getWidth(),
                (int) (screenHeight * 0.62f), horizonPaint);
    }

    // draws the game to the given Canvas
    public void drawGameElements(Canvas canvas) {
        if (canvas == null) {
            return; // the surface is not ready yet
        }

        drawBackground(canvas);

        if (cannon == null || blocker == null) {
            return;
        }

        textPaint.setColor(theme.textColor);
        hudPaint.setColor(theme.textColor);

        // display the time remaining (exactly as in the book)
        canvas.drawText(getResources().getString(
                R.string.time_remaining_format, timeLeft), 50, 100, textPaint);

        // [BONUS] item 1: the extra HUD lines
        float lineHeight = hudPaint.getTextSize() * 1.35f;
        float hudX = 50f;
        float hudY = 100f + lineHeight;

        canvas.drawText(getResources().getString(
                R.string.hud_score_format, score, multiplier), hudX, hudY,
                hudPaint);
        hudY += lineHeight;
        canvas.drawText(getResources().getString(
                R.string.hud_level_format, level, streak), hudX, hudY,
                hudPaint);
        hudY += lineHeight;
        canvas.drawText(getResources().getString(
                R.string.hud_high_format, highScoreStore.getHighScore()),
                hudX, hudY, hudPaint);
        hudY += lineHeight;
        canvas.drawText(getResources().getString(
                R.string.hud_power_format, cannon.getPower()), hudX, hudY,
                hudPaint);

        // [BONUS] item 6/3: the on-screen buttons
        drawButton(canvas, themeButton, theme.name);
        drawButton(canvas, muteButton,
                getResources().getString(soundEnabled
                        ? R.string.hud_sound_on : R.string.hud_sound_off));

        cannon.draw(canvas); // draw the Cannon

        // draw the GameElements
        if (cannon.getCannonball() != null
                && cannon.getCannonball().isOnScreen()) {
            cannon.getCannonball().draw(canvas);
        }

        blocker.draw(canvas); // draw the Blocker

        for (GameElement target : targets) {
            target.draw(canvas); // draw every Target
        }

        particles.draw(canvas); // [BONUS] item 8
    }

    // draws a rounded HUD button with a centred label
    private void drawButton(Canvas canvas, Rect bounds, String label) {
        buttonPaint.setColor(theme.buttonColor);
        buttonPaint.setAlpha(230);
        canvas.drawRoundRect(bounds.left, bounds.top, bounds.right,
                bounds.bottom, 18f, 18f, buttonPaint);

        hudPaint.setColor(theme.buttonTextColor);
        float textWidth = hudPaint.measureText(label);
        Paint.FontMetrics metrics = hudPaint.getFontMetrics();
        float textX = bounds.centerX() - textWidth / 2f;
        float textY = bounds.centerY()
                - (metrics.ascent + metrics.descent) / 2f;
        canvas.drawText(label, textX, textY, hudPaint);
        hudPaint.setColor(theme.textColor);
    }

    // checks whether the Cannonball collides with the Blocker or with any of
    // the Targets and handles the collisions
    public void testForCollisions() {
        if (cannon == null || blocker == null) {
            return;
        }

        // [BONUS] item 7: no scoring while the end-of-game animation runs
        if (gameOver) {
            return;
        }

        // remove any of the targets that the Cannonball collides with
        if (cannon.getCannonball() != null
                && cannon.getCannonball().isOnScreen()) {
            for (int n = 0; n < targets.size(); n++) {
                Target target = targets.get(n);
                if (cannon.getCannonball().collidesWith(target)) {
                    target.playSound(); // play the Target hit sound

                    // add the hit reward time to the remaining time
                    timeLeft += target.getHitReward();

                    // [BONUS] item 1: score with a progressive multiplier
                    targetsDestroyed++;
                    streak++;
                    multiplier = Math.min(MAX_MULTIPLIER,
                            1 + (streak / STREAK_STEP));
                    score += POINTS_PER_HIT * multiplier;

                    // [BONUS] item 8: explosion particles
                    particles.burst(target.getShape().centerX(),
                            target.getShape().centerY(), theme.particleColor,
                            18);

                    cannon.removeCannonball(); // remove it from the game
                    targets.remove(n);         // remove the Target that was hit
                    break;
                }
            }
        } else if (cannon.getCannonball() != null) {
            cannon.removeCannonball(); // it should not be on the screen
        }

        // check whether the ball collides with the Blocker
        if (cannon.getCannonball() != null
                && cannon.getCannonball().collidesWith(blocker)) {
            blocker.playSound(); // play the Blocker hit sound

            cannon.getCannonball().reverseVelocityX(); // reverse the ball

            // deduct the Blocker's miss penalty from the remaining time
            timeLeft -= blocker.getMissPenalty();

            // [BONUS] item 1: hitting the Blocker breaks the streak
            registerMiss();

            particles.burst(cannon.getCannonball().getShape().centerX(),
                    cannon.getCannonball().getShape().centerY(),
                    theme.blockerColor, 10);
        }
    }

    // stops the game: called by MainActivityFragment's onPause method
    public void stopGame() {
        if (cannonThread != null) {
            cannonThread.setRunning(false); // tell the thread to terminate
        }
        pauseMusic();
    }

    // true while the results dialog really is on screen.
    //
    // This is asked directly to the FragmentManager instead of being tracked in
    // a boolean field. The DialogFragment is owned by the FragmentManager and it
    // is destroyed when the Activity is stopped, so a boolean would stay true
    // for ever and the game would then refuse to start again.
    private boolean isResultsDialogDisplayed() {
        if (!(activity instanceof FragmentActivity)) {
            return false;
        }

        return ((FragmentActivity) activity).getSupportFragmentManager()
                .findFragmentByTag(GAME_RESULT_TAG) != null;
    }

    // restarts the game loop after the Activity resumes
    public void resumeGame() {
        // the player must answer the results dialog first
        if (isResultsDialogDisplayed()) {
            return;
        }

        // the game ended while the app was in the background and the results
        // dialog is gone: start a new game rather than leaving a dead surface
        if (gameOver) {
            newGame();
            startMusic();
            return;
        }

        if (cannonThread == null || !cannonThread.isRunning()) {
            joinPreviousThread();
            cannonThread = new CannonThread(getHolder());
            cannonThread.setRunning(true);
            cannonThread.start();
        }
        startMusic();
    }

    // releases resources: called by MainActivityFragment's onDestroy method
    public void releaseResources() {
        soundPool.release(); // release all the resources used by the SoundPool
        soundPool = null;

        // [BONUS] item 3: release the background music player
        if (musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    // called when the user touches the screen in this activity
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // [BONUS] the HUD buttons take precedence over firing
                if (handleHudTap(e)) {
                    return true;
                }
                // the book behaviour: aim at the touch point and fire
                swipeActive = true;
                lastSwipeX = e.getX();
                lastSwipeY = e.getY();
                if (!gameOver) {
                    alignAndFireCannonball(e);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                // [BONUS] item 5: swiping adjusts the angle and the power
                if (swipeActive && !gameOver) {
                    applySwipe(e);
                    fireCannonball();
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                swipeActive = false;
                return true;

            default:
                return super.onTouchEvent(e);
        }
    }

    // [BONUS] handles a tap on one of the on-screen HUD buttons
    private boolean handleHudTap(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();

        if (themeButton.contains(x, y)) {
            theme = GameTheme.next(theme); // [BONUS] item 6
            applyThemeColors();
            return true;
        }

        if (muteButton.contains(x, y)) {
            toggleSound(); // [BONUS] item 3
            return true;
        }

        return false;
    }

    // [BONUS] item 5: vertical drag changes the angle, horizontal drag the power
    private void applySwipe(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        float dx = x - lastSwipeX;
        float dy = y - lastSwipeY;
        lastSwipeX = x;
        lastSwipeY = y;

        // dragging up increases the angle, dragging down decreases it
        double angleDelta = -(dy / (double) screenHeight) * Math.PI;
        double angle = cannon.getBarrelAngle() + angleDelta;
        cannon.align(Math.max(0.0, Math.min(Math.PI, angle)));

        // dragging sideways changes the firing power
        float powerDelta = dx / (float) screenWidth;
        float power = cannon.getPower() + powerDelta;
        cannon.setPower(Math.max(MIN_POWER, Math.min(MAX_POWER, power)));
    }

    // [BONUS] item 3: turns the sound effects and the music on or off
    private void toggleSound() {
        soundEnabled = !soundEnabled;

        if (musicPlayer != null) {
            float volume = soundEnabled ? MUSIC_VOLUME : 0f;
            musicPlayer.setVolume(volume, volume);
            if (soundEnabled && !musicPlayer.isPlaying()) {
                musicPlayer.start();
            }
        }
    }

    private void startMusic() {
        if (musicPlayer != null && soundEnabled && !musicPlayer.isPlaying()) {
            musicPlayer.start();
        }
    }

    private void pauseMusic() {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.pause();
        }
    }

    // called when the surface changes size
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    // called when the surface is first created
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startMusic();

        // the dialog belongs to the FragmentManager, so ask it whether the
        // results are really on screen instead of trusting a saved boolean
        if (!isResultsDialogDisplayed()) {
            newGame(); // set up and start a new game
        }
    }

    // called when the surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // ensure that the thread terminates properly
        CannonThread thread = cannonThread;
        if (thread == null) {
            return;
        }

        thread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                thread.join(); // wait for the thread to finish
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
    }

    // Thread subclass to control the game loop
    private class CannonThread extends Thread {
        private SurfaceHolder surfaceHolder; // for manipulating the canvas
        private volatile boolean threadIsRunning = true; // running by default

        CannonThread(SurfaceHolder holder) {
            surfaceHolder = holder;
            setName("CannonThread");
        }

        // changes the running state
        public void setRunning(boolean running) {
            threadIsRunning = running;
        }

        public boolean isRunning() {
            return threadIsRunning;
        }

        // controls the game loop
        @Override
        public void run() {
            long previousFrameTime = System.currentTimeMillis();

            while (threadIsRunning) {
                Canvas canvas = null; // used for drawing

                try {
                    // get a Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);

                    // the surface may not be ready yet, for example when the
                    // thread is started from onResume before surfaceCreated
                    // has run. lockCanvas returns null in that case.
                    if (canvas == null) {
                        try {
                            Thread.sleep(SURFACE_WAIT_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            threadIsRunning = false;
                        }
                        // do not let the paused time count as game time
                        previousFrameTime = System.currentTimeMillis();
                        continue;
                    }

                    // lock the surfaceHolder for drawing
                    synchronized (surfaceHolder) {
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        totalElapsedTime += elapsedTimeMS / 1000.0;
                        updatePositions(elapsedTimeMS); // update the state
                        testForCollisions();    // test for collisions
                        drawGameElements(canvas); // draw using the canvas
                        previousFrameTime = currentTime; // update the time
                    }
                } finally {
                    // display the canvas's contents on the CannonView and
                    // enable the other threads to use the Canvas
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    // hide the system bars and the app bar
    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    // show the system bars and the app bar
    private void showSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
