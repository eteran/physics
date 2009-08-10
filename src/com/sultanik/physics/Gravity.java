package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

public class Gravity implements Force {
    public Gravity() { }
    public void applyForce(Particle particle) {
        particle.setAccelY(particle.getAccelY() - 9.8);
    }
    public void applyForce(Constraint constraint) { }
    public void paint(Simulator simulator, GraphicsContext graphicsContext) {
        graphicsContext.setColor(java.awt.Color.GREEN);
        for(Particle p : simulator.getParticles())
            graphicsContext.drawLine(p.getX(), p.getY(), p.getX(), p.getY() - 9.8);
    }
}