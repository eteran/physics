package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

import java.util.*;

public class Rope implements Body {
    LinkedList<Particle> links;
    HashSet<Constraint> constraints;

    public Rope(BasicParticle p1, BasicParticle p2) {
        links = new LinkedList<Particle>();
        constraints = new HashSet<Constraint>();
        links.addFirst(p2);
        links.addFirst(p1);
        constraints.add(new DistanceConstraint(p1, p2, Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY())));
    }

    public void addLink(BasicParticle p, double length) {
        Particle f = links.getFirst();
        links.addFirst(p);
        constraints.add(new DistanceConstraint(p, f, length));
    }

    public Collection<Particle> getParticles() {
        return links;
    }

    public Collection<Constraint> getConstraints() {
        return constraints;
    }

    public void paint(GraphicsContext graphicsContext) {
        for(Constraint c : constraints) {
            if(c instanceof DistanceConstraint) {
                DistanceConstraint dc = (DistanceConstraint)c;
                graphicsContext.drawLine(dc.getP1().getX(), dc.getP1().getY(), dc.getP2().getX(), dc.getP2().getY());
            }
        }
    }
}