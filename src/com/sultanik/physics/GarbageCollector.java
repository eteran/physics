package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

import java.util.*;

public class GarbageCollector extends Constraint {
    Simulator simulator;
    double interval;
    double lastCollectionTime;
    double width, height, xOffset, yOffset;

    private static final double MOTIONLESS_THRESHOLD = 0.001;

    public GarbageCollector(Body body, Simulator simulator, double interval) {
        super(body);
        body.addConstraint(this);
        this.simulator = simulator;
        this.interval = interval;
        lastCollectionTime = simulator.currentTime() - interval;
        width = -1.0;
    }
    public void cleanup() {
        System.out.println("Garbage collected!");
        simulator.removeBody(getBody());
        getBody().removeConstraint(this);
    }
    protected double satisfy() {
        /* see if we need to do a garbage collection */
        if(simulator.currentTime() - lastCollectionTime < interval)
            return 0.0;
        /* we need to garbage collect! */

        System.out.println("Time: " + simulator.currentTime());
        System.out.println("Garbage collecting!");
        lastCollectionTime = simulator.currentTime();

        /* first, see if the body is motionless */
        boolean motionless = true;
        for(Particle p : this) {
            if(p.getVelocity() > MOTIONLESS_THRESHOLD) {
                System.out.println("Particle " + p + " is not motionless (v = " + p.getVelocity() + ")");
                motionless = false;
                break;
            }
        }
        if(motionless) {
            cleanup();
            return 0.0;
        }

        /* next, see if we are offscreen */
        if(width >= 0.0) {
            /* make sure that paint() has been called at least once */
            boolean offscreen = true;
            for(Particle p : this) {
                if((p.getX() >= xOffset && p.getX() <= xOffset + width)
                   ||
                   (p.getY() >= yOffset && p.getY() <= yOffset + height)) {
                    offscreen = false;
                    break;
                }
            }
            if(offscreen) {
                cleanup();
                return 0.0;
            }
        }
        return 0.0;
    }
    public void paint(GraphicsContext graphicsContext) {
        width = graphicsContext.getWidth();
        height = graphicsContext.getHeight();
        xOffset = graphicsContext.getXOffset();
        yOffset = graphicsContext.getYOffset();
    }
}