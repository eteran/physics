package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;

public class Drag implements Force {

    Particle p1, p2;
    double magnitude;
    double angleOfAttack;

    static double DRAG_COEFFICIENT = 0.75;

    public Drag(Particle p1, Particle p2) {
        this.p1 = p1;
        this.p2 = p2;
        magnitude = 0.0;
        angleOfAttack = 0.0;
    }

    @Override
    public void applyForce(Simulator simulator) {
        double p1x = p1.getX() - p1.getPreviousX();
        double p1y = p1.getY() - p1.getPreviousY();
        double p2x = p2.getX() - p2.getPreviousX();
        double p2y = p2.getY() - p2.getPreviousY();

        double vx = p1x + p2x;
        double vy = p1y + p2y;

        double vangle = Math.atan2(vy, vx);
        double pangle = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());

        double angleDiff = vangle - pangle;
        while (angleDiff >= Math.PI / 2.0) {
            angleDiff -= Math.PI / 2.0;
        }
        while (angleDiff <= Math.PI / -2.0) {
            angleDiff += Math.PI / 2.0;
        }

        if (angleDiff >= 0) {
            angleOfAttack = pangle + Math.PI / 2.0;
        } else {
            angleOfAttack = pangle - Math.PI / 2.0;
        }

        magnitude = DRAG_COEFFICIENT * Math.abs(angleDiff) / (Math.PI / 2.0);

        double xaccel = magnitude * Math.cos(angleOfAttack);
        double yaccel = magnitude * Math.sin(angleOfAttack);
        p1.setAccelX(p1.getAccelX() + xaccel);
        p1.setAccelY(p1.getAccelY() + yaccel);
        p2.setAccelX(p2.getAccelX() + xaccel);
        p2.setAccelY(p2.getAccelY() + yaccel);
    }

    public void applyForceOld(Simulator simulator) {
        double p1x = -(p1.getX() - p1.getPreviousX());
        double p1y = -(p1.getY() - p1.getPreviousY());
        double p2x = -(p2.getX() - p2.getPreviousX());
        double p2y = -(p2.getY() - p2.getPreviousY());

        double fx = p1x + p2x;
        double fy = p1y + p2y;

        magnitude = DRAG_COEFFICIENT * Math.sqrt(fx * fx + fy * fy);

        angleOfAttack = 0.0;

        if (magnitude == 0.0) {
            return;
        }

        if (fx == 0.0) {
            angleOfAttack = Math.PI / 2.0;
            if (fy >= 0.0) {
                angleOfAttack *= -1.0;
            }
        } else {
            angleOfAttack = Math.PI + Math.atan(fy / fx);
        }

        p1.setAccelX(p1.getAccelX() + DRAG_COEFFICIENT * fx);
        p1.setAccelY(p1.getAccelY() + DRAG_COEFFICIENT * fy);
        p2.setAccelX(p2.getAccelX() + DRAG_COEFFICIENT * fx);
        p2.setAccelY(p2.getAccelY() + DRAG_COEFFICIENT * fy);
    }

    @Override
    public void paint(Simulator simulator, GraphicsContext graphicsContext) {
        graphicsContext.setColor(java.awt.Color.RED);
        graphicsContext.setLineThickness(0.5);
        //double length = 10.0 * magnitude1 * Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
        double x = p1.getX() + (p2.getX() - p1.getX()) / 2.0;
        double y = p1.getY() + (p2.getY() - p1.getY()) / 2.0;
        double x3 = x - 5 * magnitude * Math.cos(angleOfAttack);
        double y3 = y - 5 * magnitude * Math.sin(angleOfAttack);
        graphicsContext.drawLine(x, y, x3, y3);
        //x3 = p2.getX() - 5 * magnitude * Math.cos(angleOfAttack);
        //y3 = p2.getY() - 5 * magnitude * Math.sin(angleOfAttack);
        //graphicsContext.drawLine(p2.getX(), p2.getY(), x3, y3);
    }
}
//    double calculateMagnitude(Particle p) {
//        double dx = p.getX() - p.getPreviousX();
//        double dy = p.getY() - p.getPreviousY();
//        if(dx == 0.0 && dy == 0.0)
//        	return 0.0;
//        double va;
//        if(dx == 0.0) {
//            if(dy >= 0.0)
//                va = Math.PI / 2.0;
//            else
//                va = -Math.PI / 2.0;
//        } else if(dy == 0.0) {
//            if(dx >= 0.0)
//                va = 0.0;
//            else
//                va = Math.PI;
//        } else
//            va = Math.atan(dy / dx);
//        double difference = angleOfAttack - va;
//        return difference / (2.0 * Math.PI);
//    }
//    public void applyForce(Simulator simulator) {
//        magnitude1 = 0.0;
//        magnitude2 = 0.0;
//        angleOfAttack = 0.0;
//        double dx = p2.getX() - p1.getX();
//        double dy = p2.getY() - p1.getY();
//        if(dx == 0.0 && dy == 0.0)
//            return;
//        if(dx == 0.0) {
//            if(dy >= 0.0)
//                angleOfAttack = Math.PI / 2.0;
//            else
//                angleOfAttack = -Math.PI / 2.0;
//        } else if(dy == 0.0) {
//            if(dx >= 0.0)
//                angleOfAttack = 0.0;
//            else
//                angleOfAttack = Math.PI;
//        } else
//            angleOfAttack = Math.atan(dy / dx);
//        magnitude1 = calculateMagnitude(p1);
//        magnitude2 = calculateMagnitude(p2);
//        if(magnitude1 != 0.0 || magnitude2 != 0.0) {
//            //System.out.println("AoA: " + angleOfAttack + "\tMagnitude1: " + magnitude1 + "\tMagnitude2: " + magnitude2);
//            double length = Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
//            double x = -length * Math.cos(Math.PI - angleOfAttack);
//            double y = -length * Math.sin(Math.PI - angleOfAttack);
//            if(magnitude1 != 0.0) {
//                p1.setAccelX(p1.getAccelX() + magnitude1 * x);
//                p1.setAccelY(p1.getAccelY() + magnitude1 * y);
//            }
//            if(magnitude2 != 0.0) {
//                p2.setAccelX(p2.getAccelX() + magnitude2 * x);
//                p2.setAccelY(p2.getAccelY() + magnitude2 * y);
//            }
//        }
//    }
//    public void paint(Simulator simulator, GraphicsContext graphicsContext) {
//        if(magnitude1 != 0.0) {
//            graphicsContext.setColor(java.awt.Color.RED);
//            graphicsContext.setLineThickness(0.5);
//            double length = 10.0 * magnitude1 * Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
//            double x3 = p1.getX() - length * Math.cos(Math.PI - angleOfAttack);
//            double y3 = p1.getY() - length * Math.sin(Math.PI - angleOfAttack);
//            graphicsContext.drawLine(p1.getX(), p1.getY(), x3, y3);
//        }
//        if(magnitude2 != 0.0) {
//            graphicsContext.setColor(java.awt.Color.RED);
//            graphicsContext.setLineThickness(0.5);
//            double length = 10.0 * magnitude2 * Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
//            double x3 = p2.getX() - length * Math.cos(Math.PI - angleOfAttack);
//            double y3 = p2.getY() - length * Math.sin(Math.PI - angleOfAttack);
//            graphicsContext.drawLine(p2.getX(), p2.getY(), x3, y3);
//        }
//    }
