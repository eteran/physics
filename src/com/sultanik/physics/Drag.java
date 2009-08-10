package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

public class Drag implements Force {
    Particle p1, p2;
    double magnitude;
    double angleOfAttack;
    public Drag(Particle p1, Particle p2) {
        this.p1 = p1;
        this.p2 = p2;
        magnitude = 0.0;
        angleOfAttack = 0.0;
    }
    public void applyForce(Simulator simulator) {
        magnitude = 0.0;
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
            angleOfAttack = Math.asin(dy / (dy + dx));
        double dvx = (p2.getX() + p1.getX()) / 2.0 - (p2.getPreviousX() + p1.getPreviousX()) / 2.0;
        double dvy = (p2.getY() + p1.getY()) / 2.0 - (p2.getPreviousY() + p1.getPreviousY()) / 2.0;
        if(dvx == 0.0 && dvy == 0.0)
            return;
        double velocityAngle;
        if(dvx == 0.0) {
            if(dvy >= 0.0)
                velocityAngle = Math.PI / 2.0;
            else
                velocityAngle = -Math.PI / 2.0;
        } else if(dvy == 0.0) {
            if(dvx >= 0.0)
                velocityAngle = 0.0;
            else
                velocityAngle = Math.PI;
        } else
            velocityAngle = Math.asin(dvy / (dvy + dvx));
        double difference = angleOfAttack - velocityAngle;
        while(difference > 2.0 * Math.PI)
            difference -= 2.0 * Math.PI;
        while(difference < 0.0)
            difference += 2.0 * Math.PI;
        magnitude = difference / (2.0 * Math.PI);
        System.out.println("AoA: " + angleOfAttack + "\tVA: " + velocityAngle + "\tMagnitude: " + magnitude);
    }
    public void paint(Simulator simulator, GraphicsContext graphicsContext) {
        // if(magnitude != 0.0) {
        //     graphicsContext.setColor(java.awt.Color.RED);
        //     double length = magnitude * Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
        //     double x3 = p1.getX() + (p2.getX() - p1.getX()) / 2.0;
        //     double y3 = p1.getY() + (p2.getY() - p1.getY()) / 2.0;
        //     double x4 = x3 - length * Math.cos(Math.PI - angleOfAttack);
        //     double y4 = y3 + length * Math.sin(Math.PI - angleOfAttack);
        //     graphicsContext.drawLine(x3, y3, x4, y4);
        // }
    }
}