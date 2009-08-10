package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

import java.util.*;

public class Rope implements Body {
    LinkedList<Particle> links;
    HashSet<Constraint> constraints;
    HashSet<Force> forces;

    public Rope(BasicParticle p1, BasicParticle p2) {
        links = new LinkedList<Particle>();
        constraints = new HashSet<Constraint>();
        forces = new HashSet<Force>();
        links.addFirst(p2);
        links.addFirst(p1);
        constraints.add(new DistanceConstraint(p1, p2, Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY())));
        forces.add(new Drag(p1, p2));
    }

    public void addLink(BasicParticle p, double length) {
        Particle f = links.getFirst();
        links.addFirst(p);
        constraints.add(new DistanceConstraint(p, f, length));
        forces.add(new Drag(p, f));
    }

    public Collection<Particle> getParticles() {
        return links;
    }

    public Collection<Constraint> getConstraints() {
        return constraints;
    }

    public Collection<Force> getForces() {
        return forces;
    }

    public void paint(GraphicsContext graphicsContext) {
        if(links.size() <= 0)
            return;
        java.awt.geom.Point2D points[] = new java.awt.geom.Point2D[links.size()];
        int i=0;
        for(Particle p : links)
            points[i++] = new java.awt.geom.Point2D.Double(p.getX(), p.getY());
        graphicsContext.setColor(java.awt.Color.BLACK);
        graphicsContext.drawBezier(points);
        // for(Constraint c : constraints) {
        //     if(c instanceof DistanceConstraint) {
        //         DistanceConstraint dc = (DistanceConstraint)c;
        //         graphicsContext.drawLine(dc.getP1().getX(), dc.getP1().getY(), dc.getP2().getX(), dc.getP2().getY());
        //     }
        // }
    }
}