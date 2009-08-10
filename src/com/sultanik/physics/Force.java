package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

public interface Force {
    public void applyForce(Particle particle);
    public void applyForce(Constraint constraint);
    public void paint(Simulator simulator, GraphicsContext graphicsContext);
}