package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

public interface Force {
    public void applyForce(Simulator simulator);
    public void paint(Simulator simulator, GraphicsContext graphicsContext);
}