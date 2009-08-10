package com.sultanik.physics;

import com.sultanik.physics.ui;

public interface Force {
    public void applyForce(Particle particle);
    public void applyForce(Constraint constraint);
    public void paint(GraphicsContext graphicsContext);
}