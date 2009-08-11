package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

import java.util.*;
import java.awt.Color;

public class Rope implements Body {
    LinkedList<Particle> links;
    HashSet<Constraint> constraints;
    HashSet<Force> forces;
    Color color;

    public Rope(Particle p1, Particle p2) {
        links = new LinkedList<Particle>();
        constraints = new HashSet<Constraint>();
        forces = new HashSet<Force>();
        links.addFirst(p2);
        links.addFirst(p1);
        constraints.add(new DistanceConstraint(p1, p2, Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY())));
        forces.add(new Drag(p1, p2));
        color = Color.BLACK;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addParticle(Particle particle) {
        links.add(particle);
    }
    public void removeParticle(Particle particle) {
        links.remove(particle);
    }
    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }
    public void removeConstraint(Constraint constraint) {
        constraints.remove(constraint);
    }
    public void addForce(Force force) {
        forces.add(force);
    }
    public void removeForce(Force force) {
        forces.remove(force);
    }

    public void addLink(BasicParticle p, double length) {
        Particle f = links.getFirst();
        links.addFirst(p);
        constraints.add(new DistanceConstraint(p, f, length));
        // TODO: uncomment this once I fix the drag force.
        //forces.add(new Drag(p, f));
    }

    public LinkedList<Particle> getLinks() {
        return links;
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
        graphicsContext.setLineThickness(2.0);
        java.awt.geom.Point2D points[] = new java.awt.geom.Point2D[links.size()];
        int i=0;
        for(Particle p : links)
            points[i++] = new java.awt.geom.Point2D.Double(p.getX(), p.getY());
        graphicsContext.setColor(color);
        graphicsContext.drawBezier(points);
        // for(Constraint c : constraints) {
        //     if(c instanceof DistanceConstraint) {
        //         DistanceConstraint dc = (DistanceConstraint)c;
        //         graphicsContext.drawLine(dc.getP1().getX(), dc.getP1().getY(), dc.getP2().getX(), dc.getP2().getY());
        //     }
        // }
    }
}