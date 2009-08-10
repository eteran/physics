package com.sultanik.physics;

import com.sultanik.physics.ui;

import java.util.HashSet;

public abstract class Constraint implements Iterable<Particle> {
    HashSet<Particle> particles;

    public Constraint(Collection<Particle> particles) {
        this.particles = new HashSet<Particle>(particles);
    }

    public HashSet<Particle> getParticles() {
        return particles;
    }

    public Iterator<Particle> iterator() {
        return particles.iterator();
    }

    double internalSatisfy() { return satisfy(); }

    protected abstract double satisfy();

    public void paint(GraphicsContext graphicsContext) { }
}