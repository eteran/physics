package com.sultanik.physics;

import com.sultanik.physics.ui;

public abstract class Gravity implements Force {
    public Gravity() { }
    public void applyForce(Particle particle) {
        particle.setAccelY(particle.getAccelY() - 9.8);
    }
    public void applyForce(Constraint constraint) { }
    public void paint(GraphicsContext graphicsContext) {
        graphicsContext.setColor(java.awt.Color.GREEN);
        graphicsContext.drawLine(particle.getX(), particle.getY(), particle.getX(), particle.getY() - 9.8);
    }
}