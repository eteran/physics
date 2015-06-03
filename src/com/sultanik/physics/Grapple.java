package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;
import java.util.Collection;
import java.util.HashSet;

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

    public Simulator getSimulator() {
        return simulator;
    }

    public void detatchRope() {
        if (rope == null) {
            return; /* there's no rope to detatch! */

        }
        rope.setColor(java.awt.Color.MAGENTA);
        /* check to see if the rope can be garbage collected every 3 seconds */
        new GarbageCollector(rope, simulator, 3.0);
        /* unlink the last link, if it is linked */
        rope.getLinks().getLast().setFixed(false);
        rope = null;
        grappled = false;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void grapple() {
        detatchRope();
        grappled = false;
        chainLinks = 1;

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
        if (rope != null) {
            grappled = true;
            getGrapple().setFixed(true);
        }
    }

    public boolean isAttached() {
        return (rope != null) && grappled;
    }

    public Particle getLocation() {
        return location;
    }

    public Particle getGrapple() {
        if (rope == null) {
            return null;
        } else {
            return rope.getLinks().getLast();
        }
    }

    @Override
    public Collection<Particle> getParticles() {
        HashSet<Particle> particles = new HashSet<>(super.getParticles());
        particles.add(location);
        return particles;
    }

    @Override
    public Collection<Constraint> getConstraints() {
        HashSet<Constraint> constraints = new HashSet<>(super.getConstraints());
        if (grappled) {
            DistanceConstraint dc = new DistanceConstraint(location, rope.getLinks().getFirst(), 0.0);
            constraints.add(dc);
        }
        return constraints;
    }

    @Override
    public void handleIteration(double newTime) {
        if (rope == null || grappled) {
            return;
        }
        if ((double)(chainLinks + 1) * chainLinkLength <= maxChainLength) {
            while (newTime - lastAddTime >= chainLinkLength / velocity) {
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
        } else {
            detatchRope();
        }
    }

    @Override
    public void paint(GraphicsContext graphicsContext) {
        graphicsContext.setColor(java.awt.Color.BLUE);
        graphicsContext.setLineThickness(0.5);
        double length = Math.max(graphicsContext.getWidth(), graphicsContext.getHeight());
        graphicsContext.drawLine(getLocation().getX(),
                getLocation().getY(),
                getLocation().getX() + length * Math.cos(Math.toRadians(angle)),
                getLocation().getY() + length * Math.sin(Math.toRadians(angle)));
        super.paint(graphicsContext);
        graphicsContext.setColor(java.awt.Color.BLACK);
        graphicsContext.drawString(Integer.toString((int)location.getX()) + "m", graphicsContext.getXOffset() + 2.0, graphicsContext.getYOffset() + graphicsContext.getHeight() - 5.0);
    }
}
