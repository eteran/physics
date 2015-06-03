package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;

import java.util.*;

public abstract class Constraint implements Iterable<Particle> {
    HashSet<Particle> particles;
    Body body;

    public Constraint(Body body) {
        this.particles = null;
        this.body = body;
    }

    public Constraint(Collection<Particle> particles) {
        this.particles = new HashSet<>(particles);
        body = null;
    }

    public Body getBody() {
        return body;
    }

    public Collection<Particle> getParticles() {
        if(body != null)
            return body.getParticles();
        else
            return particles;
    }

    public Iterator<Particle> iterator() {
        return getParticles().iterator();
    }

    double internalSatisfy() { return satisfy(); }

    protected abstract double satisfy();

    public void paint(GraphicsContext graphicsContext) { }
}