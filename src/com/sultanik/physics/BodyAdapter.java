package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class BodyAdapter implements Body {

    HashSet<Particle> particles;
    HashSet<Constraint> constraints;
    HashSet<Force> forces;

    public BodyAdapter() {
        particles = new HashSet<>();
        constraints = new HashSet<>();
        forces = new HashSet<>();
    }

    @Override
    public Collection<Particle> getParticles() {
        return Collections.unmodifiableCollection(particles);
    }

    @Override
    public Collection<Constraint> getConstraints() {
        return Collections.unmodifiableCollection(constraints);
    }

    @Override
    public Collection<Force> getForces() {
        return Collections.unmodifiableCollection(forces);
    }

    @Override
    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    @Override
    public void removeParticle(Particle particle) {
        particles.remove(particle);
    }

    @Override
    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    @Override
    public void removeConstraint(Constraint constraint) {
        constraints.remove(constraint);
    }

    @Override
    public void addForce(Force force) {
        forces.add(force);
    }

    @Override
    public void removeForce(Force force) {
        forces.remove(force);
    }

    @Override
    public void paint(GraphicsContext graphicsContext) {
    }
}
