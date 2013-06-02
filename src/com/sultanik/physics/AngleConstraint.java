package com.sultanik.physics;

import java.util.HashSet;

public class AngleConstraint extends Constraint {
    double minAngle, maxAngle;
    Particle p1, pivot, p2;
    public AngleConstraint(Particle p1, Particle pivot, Particle p2, double minAngle, double maxAngle) {
        super(new HashSet<Particle>());
        super.particles.add(p1);
        super.particles.add(pivot);
        super.particles.add(p2);
        this.p1 = p1;
        this.p2 = p2;
        this.pivot = pivot;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }
    public Particle getP1() {
        return p1;
    }
    public Particle getP2() {
        return p2;
    }
    public Particle getPivot() {
        return pivot;
    }
    /**
     * The number of radians <code>(x2, y2)</code> needs to be rotated
     * counter-clockwise in order to be parallel with <code>(x1,
     * y1)</code>.  Both points are rotated around the origin.
     */
    double angleBetween(double x1, double y1, double x2, double y2) {
        double a = (Math.atan2(y2, x2) - Math.atan2(y1, x1));
        while(a < 0.0)
            a += Math.PI * 2.0;
        return a;
    }
    double ad = 0.0;
    protected double satisfy() {
        if(p1.isRigid() && p2.isRigid())
            return 0.0;
        double diff = angleBetween(p1.getX() - pivot.getX(), p1.getY() - pivot.getY(), p2.getX() - pivot.getX(), p2.getY() - pivot.getY());

        ad = 0.0;

        if(diff < minAngle || diff > maxAngle) {
            /* are we closer to the min angle or the max angle? */
            double mindiff = diff - minAngle;
            double maxdiff = diff - maxAngle;
            if(mindiff > Math.PI)
                mindiff -= 2.0 * Math.PI;
            else if(mindiff < -Math.PI)
                mindiff += 2.0 * Math.PI;
            if(maxdiff > Math.PI)
                maxdiff -= 2.0 * Math.PI;
            else if(maxdiff < -Math.PI)
                maxdiff += 2.0 * Math.PI;
            double rotate = 0.0;
            if(Math.abs(mindiff) < Math.abs(maxdiff)) {
                rotate = mindiff;
            } else {
                rotate = -maxdiff;
            }
            ad = rotate;
            System.out.println("Rotating " + (int)(Math.toDegrees(rotate) + 0.5) + " degrees counter-clockwise...");
            
            if(rotate != 0.0) {
                //BasicParticle par1;
                BasicParticle par2;
//                if(p1 instanceof BasicParticle)
//                    par1 = (BasicParticle)p1;
//                else
//                    par1 = new BasicParticle(p1);
                if(p2 instanceof BasicParticle)
                    par2 = (BasicParticle)p1;
                else
                    par2 = new BasicParticle(p2);
            // if(p1.isRigid()) {
                BasicParticle rotated = par2.rotate(rotate, pivot);
                p2.setX(rotated.getX());
                p2.setY(rotated.getY());
                p2.setPreviousX(rotated.getPreviousX());
                p2.setPreviousY(rotated.getPreviousY());
            // } else if(p2.isRigid()) {
            //    BasicParticle rotated = par1.rotate(rotate, pivot);
            //    p1.setX(rotated.getX());
            //    p1.setY(rotated.getY());
            // } else {
            //     BasicParticle r1 = par1.rotate(angleDiff/-2.0, pivot);
            //     BasicParticle r2 = par2.rotate(angleDiff/2.0, pivot);
            //     p1.setX(r1.getX());
            //     p1.setY(r1.getY());
            //     p2.setX(r2.getX());
            //     p2.setY(r2.getY());
            // }
            }
        }
        return 0.0;
    }
    public void paint(com.sultanik.ui.GraphicsContext graphicsContext) {
        if(ad != 0.0) {
            graphicsContext.setColor(java.awt.Color.RED);
            graphicsContext.drawString(Integer.toString((int)(Math.toDegrees(ad) + 0.5)), pivot.getX() + Math.cos(ad), pivot.getY() + Math.sin(ad));
            graphicsContext.setColor(java.awt.Color.BLUE);
            double xdiff = p2.getX() - pivot.getX();
            double ydiff = p2.getY() - pivot.getY();
            double radius = Math.sqrt(xdiff * xdiff + ydiff * ydiff);
            double startAngle = BasicParticle.angleFromX(xdiff, ydiff);
            double endAngle = startAngle + ad;
            graphicsContext.drawArc(pivot.getX(), pivot.getY(), radius, startAngle, endAngle); 
            //graphicsContext.drawLine(pivot.getX(), pivot.getY(), pivot.getX() + radius * Math.cos(endAngle), pivot.getY() + radius * Math.sin(endAngle));
        }
    }
}