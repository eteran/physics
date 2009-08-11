package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

import java.util.*;

public class Grapple extends BodyAdapter implements SimulationListener {
    Particle location;
    double angle;
    double velocity;
    double chainLinkLength;
    Simulator simulator;
    double lastAddTime;
    int chainLinks;
    double maxChainLength;
    boolean grappled;
    Rope rope;

    public Grapple(Simulator simulator, Particle initialLocation, double angle, double velocity, double chainLinkLength, double maxChainLength) {
        super();
        addParticle(initialLocation);
        this.simulator = simulator;
        location = initialLocation;
        //location.setRigid(true);
        this.angle = angle;
        this.velocity = velocity;
        this.chainLinkLength = chainLinkLength;
        this.maxChainLength = maxChainLength;
        rope = null;
        simulator.addListener(this);
        chainLinks = 1;
        grappled = false;
    }

    public void detatchRope() {
        if(rope == null)
            return; /* there's no rope to detatch! */
        rope.setColor(java.awt.Color.GRAY);
        /* check to see if the rope can be garbage collected every 3 seconds */
        rope.addConstraint(new GarbageCollector(rope, simulator, 3.0));
        /* unlink the last link, if it is linked */
        rope.getLinks().getLast().setFixed(false);
        rope = null;
        grappled = false;
    }

    public void grapple() {
        detatchRope();
        grappled = false;

        double ang = Math.toRadians(angle);
        double x = location.getX() + chainLinkLength * Math.cos(ang);
        double y = location.getY() + chainLinkLength * Math.sin(ang);
        double xold = x - velocity * simulator.getTimeStep() * Math.cos(ang)
            - (location.getX() - location.getPreviousX());
        double yold = y - velocity * simulator.getTimeStep() * Math.sin(ang)
            - (location.getY() - location.getPreviousY());
        BasicParticle p1 = new BasicParticle(x, y, xold, yold, 0.0, 0.0);

        x = location.getX();
        y = location.getY();
        xold = x - velocity * simulator.getTimeStep() * Math.cos(ang)
            - (location.getX() - location.getPreviousX());
        yold = y - velocity * simulator.getTimeStep() * Math.sin(ang)
            - (location.getY() - location.getPreviousY());
        BasicParticle p2 = new BasicParticle(x, y, xold, yold, 0.0, 0.0);

        lastAddTime = simulator.currentTime();

        rope = new Rope(p2, p1);
        simulator.addBody(rope);
    }

    public void attachGrapple() {
        if(rope != null) {
            grappled = true;
            getGrapple().setFixed(true);
        }
    }

    public Particle getLocation() {
        return location;
    }

    public Particle getGrapple() {
        if(rope == null)
            return null;
        else
            return rope.getLinks().getLast();
    }

    public Collection<Particle> getParticles() {
        HashSet<Particle> particles = new HashSet<Particle>(super.getParticles());
        particles.add(location);
        return particles;
    }

    public Collection<Constraint> getConstraints() {
        HashSet<Constraint> constraints = new HashSet<Constraint>(super.getConstraints());
        if(grappled) {
            DistanceConstraint dc = new DistanceConstraint(location, rope.getLinks().getFirst(), 0.0);
            constraints.add(dc);
        }
        return constraints;
    }

    public void handleIteration(double newTime) {
        if(rope == null || grappled)
            return;
        if((double)(chainLinks + 1) * chainLinkLength <= maxChainLength) {
            while(newTime - lastAddTime >= chainLinkLength / velocity) {
                /* add a new chain link */
                chainLinks++;
                double ang = Math.toRadians(angle);
                double x = location.getX();
                double y = location.getY();
                double xold = x - velocity * simulator.getTimeStep() * Math.cos(ang)
                    - (location.getX() - location.getPreviousX());
                double yold = y - velocity * simulator.getTimeStep() * Math.sin(ang)
                    - (location.getY() - location.getPreviousY());
                rope.addLink(new BasicParticle(x, y, xold, yold, 0.0, 0.0), chainLinkLength);
                lastAddTime += chainLinkLength / velocity;
            }
        }
    }

    public void paint(GraphicsContext graphicsContext) {
        super.paint(graphicsContext);
        graphicsContext.drawString("P", location.getX(), location.getY() - 2.0);
    }
}