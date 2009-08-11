package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

public class Drag implements Force {
    Particle p1, p2;
    double magnitude1, magnitude2;
    double angleOfAttack;
    public Drag(Particle p1, Particle p2) {
        this.p1 = p1;
        this.p2 = p2;
        magnitude1 = 0.0;
        magnitude2 = 0.0;
        angleOfAttack = 0.0;
    }
    double calculateMagnitude(Particle p) {
        double dx = p.getX() - p.getPreviousX();
        double dy = p.getY() - p.getPreviousY();
        double va;
        if(dx == 0.0) {
            if(dy >= 0.0)
                va = Math.PI / 2.0;
            else
                va = -Math.PI / 2.0;
        } else if(dy == 0.0) {
            if(dx >= 0.0)
                va = 0.0;
            else
                va = Math.PI;
        } else
            va = Math.atan(dy / dx);
        double difference = angleOfAttack - va;
        return difference / (2.0 * Math.PI);
    }
    public void applyForce(Simulator simulator) {
        magnitude1 = 0.0;
        magnitude2 = 0.0;
        angleOfAttack = 0.0;
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        if(dx == 0.0 && dy == 0.0)
            return;
        if(dx == 0.0) {
            if(dy >= 0.0)
                angleOfAttack = Math.PI / 2.0;
            else
                angleOfAttack = -Math.PI / 2.0;
        } else if(dy == 0.0) {
            if(dx >= 0.0)
                angleOfAttack = 0.0;
            else
                angleOfAttack = Math.PI;
        } else
            angleOfAttack = Math.atan(dy / dx);
        magnitude1 = calculateMagnitude(p1);
        magnitude2 = calculateMagnitude(p2);
        if(magnitude1 != 0.0 || magnitude2 != 0.0) {
            //System.out.println("AoA: " + angleOfAttack + "\tMagnitude1: " + magnitude1 + "\tMagnitude2: " + magnitude2);
            double length = Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
            double x = -length * Math.cos(Math.PI - angleOfAttack);
            double y = -length * Math.sin(Math.PI - angleOfAttack);
            if(magnitude1 != 0.0) {
                p1.setAccelX(p1.getAccelX() + magnitude1 * x);
                p1.setAccelY(p1.getAccelY() + magnitude1 * y);
            }
            if(magnitude2 != 0.0) {
                p2.setAccelX(p2.getAccelX() + magnitude2 * x);
                p2.setAccelY(p2.getAccelY() + magnitude2 * y);
            }
        }
    }
    public void paint(Simulator simulator, GraphicsContext graphicsContext) {
        if(magnitude1 != 0.0) {
            graphicsContext.setColor(java.awt.Color.RED);
            graphicsContext.setLineThickness(0.5);
            double length = 10.0 * magnitude1 * Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
            double x3 = p1.getX() - length * Math.cos(Math.PI - angleOfAttack);
            double y3 = p1.getY() - length * Math.sin(Math.PI - angleOfAttack);
            graphicsContext.drawLine(p1.getX(), p1.getY(), x3, y3);
        }
    }
}