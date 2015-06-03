package com.sultanik.physics;

import java.util.HashSet;

public class DistanceConstraint extends Constraint {
    double distance;
    Particle p1, p2;
    boolean fixLeft, fixRight;
    public DistanceConstraint(Particle p1, Particle p2, double distance) {
        super(new HashSet<Particle>());
        super.particles.add(p1);
        super.particles.add(p2);
        this.p1 = p1;
        this.p2 = p2;
        this.distance = distance;
        fixLeft = false;
        fixRight = false;
    }
    public void fixLeft(boolean fix) { fixLeft = fix; }
    public void fixRight(boolean fix) { fixRight = fix; }
    public Particle getP1() {
        return p1;
    }
    public Particle getP2() {
        return p2;
    }
    @Override
    protected double satisfy() {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double deltaLength = Math.hypot(deltaX, deltaY);
        double diff = (deltaLength - distance) / deltaLength;
        if((p1.isRigid() || fixLeft) && (p2.isRigid() || fixRight)) {
            return Math.abs(diff); /* there's nothing we can do if they're both fixed! */
        }
        if(deltaLength == 0.0) {
            /* TODO: intelligently avoid division by zero! */
            return distance;
        } else {
            if(p2.isRigid() || fixRight) {
                /* we can only move p1! */
                p1.setX(p1.getX() + deltaX * diff);
                p1.setY(p1.getY() + deltaY * diff);
            } else if(p1.isRigid() || fixLeft) {
                /* we can only move p2! */
                p2.setX(p2.getX() - deltaX * diff);
                p2.setY(p2.getY() - deltaY * diff);
            } else {
                /* we can move both particles */
                p1.setX(p1.getX() + deltaX * 0.5 * diff);
                p1.setY(p1.getY() + deltaY * 0.5 * diff);
                p2.setX(p2.getX() - deltaX * 0.5 * diff);
                p2.setY(p2.getY() - deltaY * 0.5 * diff);
            }
        }
        return 0.0;
    }
}