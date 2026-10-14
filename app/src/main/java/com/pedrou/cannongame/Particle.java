// Particle.java
// A single particle used for the explosion effect.
// [BONUS] item 8 "Particulas ao acertar o alvo" from the extra resources PDF.
package com.pedrou.cannongame;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Particle {

    // simple gravity, in pixels per second squared
    private static final float GRAVITY = 900f;
    // each frame the particle shrinks slightly, giving a soft fade
    private static final float SHRINK = 0.985f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private float radius;
    private float life;       // remaining life, in seconds
    private float maxLife;    // initial life, used to compute the alpha
    private int color;

    public Particle(float x, float y, float velocityX, float velocityY,
                    float radius, int color, float maxLife) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.radius = radius;
        this.color = color;
        this.maxLife = maxLife;
        this.life = maxLife;
    }

    // updates the particle position and fades it out
    public void update(double interval) {
        x += velocityX * interval;
        y += velocityY * interval;
        velocityY += GRAVITY * interval;
        radius *= SHRINK;
        life -= interval;
    }

    // a particle is removed from the game once its life is over
    public boolean isAlive() {
        return life > 0f && radius > 0.5f;
    }

    // draws the particle with an alpha proportional to its remaining life
    public void draw(Canvas canvas) {
        int alpha = (int) (255 * Math.max(0f, life / maxLife));
        paint.setColor(color);
        paint.setAlpha(alpha);
        canvas.drawCircle(x, y, radius, paint);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
