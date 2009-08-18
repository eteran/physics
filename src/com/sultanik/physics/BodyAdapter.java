package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;

import java.util.*;

public class BodyAdapter implements Body {
    HashSet<Particle> particles;
    HashSet<Constraint> constraints;
    HashSet<Force> forces;

    public BodyAdapter() {
        particles = new HashSet<Particle>();
        constraints = new HashSet<Constraint>();
        forces = new HashSet<Force>();
    }

    public Collection<Particle> getParticles() {
        return particles;
    }
    public Collection<Constraint> getConstraints() {
        return constraints;
    }
    public Collection<Force> getForces() {
        return forces;
    }
    public void addParticle(Particle particle) {
        particles.add(particle);
    }
    public void removeParticle(Particle particle) {
        particles.remove(particle);
    }
    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }
    public void removeConstraint(Constraint constraint) {
        constraints.remove(constraint);
    }
    public void addForce(Force force) {
        forces.add(force);
    }
    public void removeForce(Force force) {
        forces.remove(force);
    }
    
    public void paint(GraphicsContext graphicsContext) { }
}