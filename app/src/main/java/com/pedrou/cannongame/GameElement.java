// GameElement.java
// Represents a rectangle-bounded game element.
// Based on Chapter 6 (Cannon Game) of "Android 6 for Programmers".
package com.pedrou.cannongame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameElement {

    protected Paint paint = new Paint(); // Paint used to draw this element
    protected CannonView view;           // the view that contains this element
    protected Rect shape;                // the element's rectangular bounds

    private float velocityY;             // vertical velocity of this element
    private int soundId;                 // sound associated with this element

    // public constructor
    public GameElement(CannonView view, int color, int soundId, int x,
                       int y, int width, int length, float velocityY) {
        this.view = view;
        paint.setColor(color);
        shape = new Rect(x, y, x + width, y + length); // set the bounds

        this.soundId = soundId;
        this.velocityY = velocityY;
    }

    // update this element's position and check for wall collisions
    public void update(double interval) {
        // update the vertical position
        shape.offset(0, (int) (velocityY * interval));

        // if this element collides with the top or bottom wall, reverse it
        if ((shape.top < 0 && velocityY < 0)
                || (shape.bottom > view.getScreenHeight() && velocityY > 0)) {
            velocityY *= -1; // reverse this element's velocity
        }
    }

    // draws this element on the given Canvas
    public void draw(Canvas canvas) {
        canvas.drawRect(shape, paint);
    }

    // plays the sound that corresponds to this type of element
    public void playSound() {
        view.playSound(soundId);
    }

    // ------------------------------------------------------------------
    // [BONUS] Accessors added to support the extra features described in
    // "Recursos Extras Sugeridos para o Cannon Game.pdf".
    // ------------------------------------------------------------------

    // returns the current vertical velocity
    public float getVelocityY() {
        return velocityY;
    }

    // sets the vertical velocity (used for progressive difficulty)
    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    // returns the sound id associated with this element
    public int getSoundId() {
        return soundId;
    }

    // sets the color used to draw this element
    public void setColor(int color) {
        paint.setColor(color);
    }

    // returns this element's bounding rectangle
    public Rect getShape() {
        return shape;
    }
}
