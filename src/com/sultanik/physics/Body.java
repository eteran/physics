package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

import java.util.Collection;

public interface Body {
    public Collection<Particle> getParticles();
    public Collection<Constraint> getConstraints();
    public Collection<Force> getForces();
    public void paint(GraphicsContext graphicsContext);
}