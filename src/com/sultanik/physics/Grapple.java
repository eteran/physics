package com.sultanik.physics;

import java.util.*;

public class Grapple extends Rope implements SimulationListener {
    Particle location;
    double angle;
    double velocity;
    double chainLinkLength;
    Simulator simulator;
    double lastAddTime;
    int chainLinks;
    double maxChainLength;

    private static Particle getFirstParticle(Simulator simulator, Particle initialLocation, double angle, double velocity, double chainLinkLength) {
        double ang = Math.toRadians(angle);
        double x = initialLocation.getX() + chainLinkLength * Math.cos(ang);
        double y = initialLocation.getY() + chainLinkLength * Math.sin(ang);
        double xold = x - velocity * simulator.getTimeStep() * Math.cos(ang)
            - (initialLocation.getX() - initialLocation.getPreviousX());
        double yold = y - velocity * simulator.getTimeStep() * Math.sin(ang)
            - (initialLocation.getY() - initialLocation.getPreviousY());
        return new BasicParticle(x, y, xold, yold, 0.0, 0.0);
    }

    private static Particle getSecondParticle(Simulator simulator, Particle initialLocation, double angle, double velocity, double chainLinkLength) {
        double ang = Math.toRadians(angle);
        double x = initialLocation.getX();
        double y = initialLocation.getY();
        double xold = x - velocity * simulator.getTimeStep() * Math.cos(ang)
            - (initialLocation.getX() - initialLocation.getPreviousX());
        double yold = y - velocity * simulator.getTimeStep() * Math.sin(ang)
            - (initialLocation.getY() - initialLocation.getPreviousY());
        return new BasicParticle(x, y, xold, yold, 0.0, 0.0);
    }

    public Grapple(Simulator simulator, Particle initialLocation, double angle, double velocity, double chainLinkLength, double maxChainLength) {
        super(getSecondParticle(simulator, initialLocation, angle, velocity, chainLinkLength),
              getFirstParticle(simulator, initialLocation, angle, velocity, chainLinkLength));
        this.simulator = simulator;
        location = initialLocation;
        this.angle = angle;
        this.velocity = velocity;
        this.chainLinkLength = chainLinkLength;
        this.maxChainLength = maxChainLength;
        simulator.addListener(this);
        lastAddTime = simulator.currentTime();
        chainLinks = 1;
    }

    public Particle getLocation() {
        return location;
    }

    public Collection<Particle> getParticles() {
        HashSet<Particle> particles = new HashSet<Particle>(super.getParticles());
        particles.add(location);
        return particles;
    }

    // public Collection<Constraint> getConstraints() {
    //     HashSet<Constraint> constraints = new HashSet<Constraint>(super.getConstraints());
    //     constraints.add(new DistanceConstraint(location, links.getFirst(), 0.0));
    //     return constraints;
    // }

    public void handleIteration(double newTime) {
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
                super.addLink(new BasicParticle(x, y, xold, yold, 0.0, 0.0), chainLinkLength);
                lastAddTime += chainLinkLength / velocity;
            }
        }
    }
}