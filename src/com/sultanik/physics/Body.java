package com.sultanik.physics;

import com.sultanik.physics.ui;

public interface Body {
    public Collection<Particle> getParticles();
    public Collection<Constraint> getConstraints();
    public void paint(GraphicsContext graphicsContext);
}