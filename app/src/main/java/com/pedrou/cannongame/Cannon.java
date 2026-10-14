// Cannon.java
// Represents the Cannon and fires the Cannonball.
// Based on Chapter 6 (Cannon Game) of "Android 6 for Programmers".
package com.pedrou.cannongame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Cannon {
    private int baseRadius;                 // Cannon base's radius
    private int barrelLength;               // Cannon barrel's length
    private Point barrelEnd = new Point();  // endpoint of the Cannon's barrel
    private double barrelAngle;             // angle of the Cannon's barrel
    private Cannonball cannonball;          // the Cannon's Cannonball
    private Paint paint = new Paint();      // Paint used to draw the Cannon
    private CannonView view;                // view containing the Cannon

    // [BONUS] item 5 (swipe control): firing power multiplier, 1.0 = default
    private float power = 1.0f;
    // [BONUS] item 6 (day/night theme): colour of the Cannon
    private int cannonColor = Color.BLACK;

    // constructor
    public Cannon(CannonView view, int baseRadius, int barrelLength,
                  int barrelWidth) {
        this.view = view;
        this.baseRadius = baseRadius;
        this.barrelLength = barrelLength;
        paint.setStrokeWidth(barrelWidth); // set the barrel width
        paint.setColor(Color.BLACK);       // the Cannon's colour is black
        align(Math.PI / 2);                // the barrel starts facing "up"
    }

    // aligns the Cannon's barrel to the given angle
    public void align(double barrelAngle) {
        this.barrelAngle = barrelAngle;
        barrelEnd.x = (int) (barrelLength * Math.sin(barrelAngle));
        barrelEnd.y = (int) (-barrelLength * Math.cos(barrelAngle))
                + view.getScreenHeight() / 2;
    }

    // creates and fires a Cannonball in the direction the Cannon points
    public void fireCannonball() {
        // [BONUS] the swipe control scales the firing power
        double speed = CannonView.CANNONBALL_SPEED_PERCENT
                * view.getScreenWidth() * power;

        // x component of the Cannonball's velocity
        int velocityX = (int) (speed * Math.sin(barrelAngle));

        // y component of the Cannonball's velocity
        int velocityY = (int) (speed * -Math.cos(barrelAngle));

        // the Cannonball's radius
        int radius = (int) (view.getScreenHeight()
                * CannonView.CANNONBALL_RADIUS_PERCENT);

        // construct the Cannonball and position it inside the Cannon
        cannonball = new Cannonball(view, cannonColor,
                CannonView.CANNON_SOUND_ID, -radius,
                view.getScreenHeight() / 2 - radius, radius, velocityX,
                velocityY);

        cannonball.playSound(); // play the fire sound
    }

    // draws the Cannon on the Canvas
    public void draw(Canvas canvas) {
        // draw the Cannon's barrel
        canvas.drawLine(0, view.getScreenHeight() / 2, barrelEnd.x,
                barrelEnd.y, paint);

        // draw the Cannon's base
        canvas.drawCircle(0, view.getScreenHeight() / 2, baseRadius, paint);
    }

    // returns the Cannonball that this Cannon fired
    public Cannonball getCannonball() {
        return cannonball;
    }

    // removes the Cannonball from the game
    public void removeCannonball() {
        cannonball = null;
    }

    // ------------------------------------------------------------------
    // [BONUS] Accessors for the extra features.
    // ------------------------------------------------------------------

    // returns the current barrel angle in radians
    public double getBarrelAngle() {
        return barrelAngle;
    }

    // sets the firing power multiplier (item 5)
    public void setPower(float power) {
        this.power = power;
    }

    // returns the firing power multiplier
    public float getPower() {
        return power;
    }

    // changes the colour of the Cannon (item 6)
    public void setCannonColor(int color) {
        cannonColor = color;
        paint.setColor(color);
    }
}
