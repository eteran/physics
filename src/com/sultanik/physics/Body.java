package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;
import java.util.Collection;

public interface Body {
    public Collection<Particle> getParticles();
    public Collection<Constraint> getConstraints();
    public Collection<Force> getForces();
    public void addParticle(Particle particle);
    public void removeParticle(Particle particle);
    public void addConstraint(Constraint constraint);
    public void removeConstraint(Constraint constraint);
    public void addForce(Force force);
    public void removeForce(Force force);
    public void paint(GraphicsContext graphicsContext);
}