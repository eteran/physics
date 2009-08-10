package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

public class Gravity implements Force {
    public Gravity() { }
    public void applyForce(Simulator simulator) {
        for(Particle p : simulator.getParticles())
            p.setAccelY(p.getAccelY() - 9.8);
    }
    public void paint(Simulator simulator, GraphicsContext graphicsContext) {
        // graphicsContext.setColor(java.awt.Color.GREEN);
        // graphicsContext.setLineThickness(1.0);
        // for(Particle p : simulator.getParticles())
        //     graphicsContext.drawLine(p.getX(), p.getY(), p.getX(), p.getY() - 9.8);
    }
}