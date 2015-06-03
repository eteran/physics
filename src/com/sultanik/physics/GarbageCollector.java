package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;

public class GarbageCollector implements SimulationListener {
    Simulator simulator;
    double interval;
    double lastCollectionTime;
    double width, height, xOffset, yOffset;
    Body body;

    private static final double MOTIONLESS_THRESHOLD = 0.001;

    public GarbageCollector(Body body, Simulator simulator, double interval) {
        this.body = body;
        this.simulator = simulator;
        this.interval = interval;
        lastCollectionTime = simulator.currentTime() - interval;
        width = -1.0;
        simulator.addListener(this);
    }
    public void cleanup() {
        System.out.println("Garbage collected!");
        simulator.removeBody(body);
        simulator.removeListener(this);
    }
    @Override
    public void handleIteration(double newTime) {
        /* see if we need to do a garbage collection */
        if(newTime - lastCollectionTime < interval) {
            return;
            /* we need to garbage collect! */
        }

        //System.out.println("Time: " + simulator.currentTime());
        //System.out.println("Garbage collecting!");
        lastCollectionTime = simulator.currentTime();

        /* first, see if the body is motionless */
        boolean motionless = true;
        for(Particle p : body.getParticles()) {
            if(p.getVelocity() > MOTIONLESS_THRESHOLD) {
                //System.out.println("Particle " + p + " is not motionless (v = " + p.getVelocity() + ")");
                motionless = false;
                break;
            }
        }
        if(motionless) {
            cleanup();
            return;
        }

        /* next, see if we are offscreen */
        if(width >= 0.0) {
            /* make sure that paint() has been called at least once */
            boolean offscreen = true;
            for(Particle p : body.getParticles()) {
                if((p.getX() >= xOffset && p.getX() <= xOffset + width)
                   ||
                   (p.getY() >= yOffset && p.getY() <= yOffset + height)) {
                    offscreen = false;
                    break;
                }
            }
            if(offscreen) {
                cleanup();
                return;
            }
        }
    }
    public void paint(GraphicsContext graphicsContext) {
        width = graphicsContext.getWidth();
        height = graphicsContext.getHeight();
        xOffset = graphicsContext.getXOffset();
        yOffset = graphicsContext.getYOffset();
    }
}