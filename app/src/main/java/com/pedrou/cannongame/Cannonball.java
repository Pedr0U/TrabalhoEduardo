// Cannonball.java
// Represents the Cannonball that the Cannon fires.
// Based on Chapter 6 (Cannon Game) of "Android 6 for Programmers".
package com.pedrou.cannongame;

import android.graphics.Canvas;
import android.graphics.Rect;

public class Cannonball extends GameElement {
    private float velocityX;
    private boolean onScreen;

    // constructor
    public Cannonball(CannonView view, int color, int soundId, int x,
                      int y, int radius, float velocityX, float velocityY) {
        super(view, color, soundId, x, y, 2 * radius, 2 * radius, velocityY);
        this.velocityX = velocityX;
        onScreen = true;
    }

    // get this Cannonball's radius
    private int getRadius() {
        return (shape.right - shape.left) / 2;
    }

    // test whether this Cannonball collides with the given GameElement
    //
    // [BONUS] item 11 (advanced collision): the book used the bounding boxes
    // of both elements (Rect.intersects). A Cannonball is drawn as a circle,
    // so a false positive could be registered in the empty corners of its
    // bounding box. Here the precise circle-versus-rectangle test is used.
    public boolean collidesWith(GameElement element) {
        return intersectsCircleRectangle(element.getShape()) && velocityX > 0;
    }

    // [BONUS] exact circle/rectangle intersection test
    private boolean intersectsCircleRectangle(Rect rectangle) {
        int radius = getRadius();
        int centerX = shape.left + radius;
        int centerY = shape.top + radius;

        // closest point of the rectangle to the circle's center
        int closestX = Math.max(rectangle.left, Math.min(centerX, rectangle.right));
        int closestY = Math.max(rectangle.top, Math.min(centerY, rectangle.bottom));

        int distanceX = centerX - closestX;
        int distanceY = centerY - closestY;

        return (distanceX * distanceX + distanceY * distanceY)
                <= (radius * radius);
    }

    // returns true if this Cannonball is on the screen
    public boolean isOnScreen() {
        return onScreen;
    }

    // reverses this Cannonball's horizontal velocity
    public void reverseVelocityX() {
        velocityX *= -1;
    }

    // updates this Cannonball's position
    @Override
    public void update(double interval) {
        super.update(interval); // update the vertical position

        // update the horizontal position
        shape.offset((int) (velocityX * interval), 0);

        // if this Cannonball goes off the screen
        if (shape.top < 0 || shape.left < 0
                || shape.bottom > view.getScreenHeight()
                || shape.right > view.getScreenWidth()) {
            onScreen = false; // mark it to be removed
        }
    }

    // draws this Cannonball on the given Canvas
    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(shape.left + getRadius(), shape.top + getRadius(),
                getRadius(), paint);
    }
}
