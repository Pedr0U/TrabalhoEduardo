// ParticleSystem.java
// Manages the collection of particles used by the explosion effect.
// [BONUS] item 8 "Particulas ao acertar o alvo" from the extra resources PDF.
package com.pedrou.cannongame;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {

    // safety cap so a very fast device cannot create an unbounded list
    private static final int MAX_PARTICLES = 400;

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    // creates a radial burst of particles centred on (x, y)
    public void burst(float x, float y, int color, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2.0 * Math.PI;
            float speed = 120f + random.nextFloat() * 380f;
            float radius = 3f + random.nextFloat() * 6f;
            float life = 0.45f + random.nextFloat() * 0.45f;

            particles.add(new Particle(x, y,
                    (float) (Math.cos(angle) * speed),
                    (float) (Math.sin(angle) * speed),
                    radius, color, life));
        }

        // drop the oldest particles if the cap is exceeded
        while (particles.size() > MAX_PARTICLES) {
            particles.remove(0);
        }
    }

    // updates every particle and removes the dead ones
    public void update(double interval) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.update(interval);
            if (!particle.isAlive()) {
                particles.remove(i);
            }
        }
    }

    // draws every particle
    public void draw(Canvas canvas) {
        for (Particle particle : particles) {
            particle.draw(canvas);
        }
    }

    // removes every particle (used when a new game starts)
    public void clear() {
        particles.clear();
    }

    public int size() {
        return particles.size();
    }
}
