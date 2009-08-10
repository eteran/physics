package com.sultanik.physics;

import com.sultanik.physics.ui.*;

import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Simulator {
    /**
     * The version of Physics.
     */
    public static final String VERSION  = "0.1";
    /**
     * The revision date for this version of Physics.
     */
    public static final String REV_DATE = "2009-08-10";

    LinkedHashSet<SimulationListener> listeners;
    HashSet<Body> bodies;
    HashSet<Force> forces;
    double timeStep;
    double t;

    private static final int	CONSTRAINT_SATISFICATION_ITERATIONS	= 100;
    private static final double	CONSTRAINT_SATISFICATION_THRESHOLD	= 0.001;

    public Simulator(double timeStep) {
        listeners = new LinkedHashSet<SimulationListener>();
        bodies = new HashSet<Body>();
        forces = new HashSet<Force>();
        timeStep = 0.01;
        t = 0.0;
    }

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SimulationListener listener) {
        listeners.remove(listener);
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public double getTimeStep() {
        return timeStep;
    }

    void accumulateForces(HashSet<Particle> particles, HashSet<Constraint> constraints) {
        /* first, clear all of the previous forces */
        for(Particle p : particles) {
            p.setAccelX(0.0);
            p.setAccelY(0.0);
        }
        for(Force f : forces)
            for(Particle p : particles)
                f.applyForce(p);
        for(Force f : forces)
            for(Constraint c : constraints)
                f.applyForce(c);
    }

    void vertlet(HashSet<Particle> particles) {
        for(Particle p : particles) {
            if(!p.isFixed()) {
                double tmpX = p.getX();
                double tmpY = p.getY();
                p.setX(2.0 * tmpX - p.getPreviousX() + p.getAccelX() * timeStep * timeStep);
                p.setY(2.0 * tmpY - p.getPreviousY() + p.getAccelY() * timeStep * timeStep);
                System.out.println(p.toString());
                p.setPreviousX(tmpX);
                p.setPreviousY(tmpY);
            }
        }
    }

    void satisfyConstraints(HashSet<Particle> particles, HashSet<Constraint> constraints) {
        double maxDiff = CONSTRAINT_SATISFICATION_THRESHOLD;
        for(int i=0; i <= CONSTRAINT_SATISFICATION_ITERATIONS && maxDiff >= CONSTRAINT_SATISFICATION_THRESHOLD; i++) {
            maxDiff = 0.0;
            
            /* first, make sure that none of the particles are below ground! */
            for(Particle p : particles)
                if(p.getY() < 0.0)
                    p.setY(0.0);

            /* now apply all of the constraints */
            for(Constraint c : constraints) {
                double cost = c.internalSatisfy();
                if(cost > maxDiff)
                    maxDiff = cost;
            }
        }
    }

    public HashSet<Particle> getParticles() {
        HashSet<Particle> particles = new HashSet<Particle>();
        for(Body body : bodies)
            particles.addAll(body.getParticles());
        return particles;
    }

    public HashSet<Constraint> getConstraints() {
        HashSet<Constraint> constraints = new HashSet<Constraint>();
        for(Body body : bodies)
            constraints.addAll(body.getConstraints());
        return constraints;        
    }

    public HashSet<Body> getBodies() { return bodies; }
    public HashSet<Force> getForces() { return forces; }

    public void simulate() {
        t += timeStep;
        HashSet<Particle> particles = getParticles();
        HashSet<Constraint> constraints = getConstraints();

        accumulateForces(particles, constraints);
        vertlet(particles);
        satisfyConstraints(particles, constraints);

        for(SimulationListener sl : listeners)
            sl.handleIteration(t);
    }

    public void simulate(double untilTime) {
        while(t <= untilTime)
            simulate();
    }

    public void addForce(Force force) {
        forces.add(force);
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    public static void main(String[] args) {
        double resolution = 0.01;
        Simulator sim = new Simulator(resolution);
        JFrame frame = new JFrame("Physics");
        SimulationPanel sp = new SimulationPanel(sim);
        sp.setPreferredSize(new Dimension(640,480));
        frame.add(sp);
        frame.pack();
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        double startTime = System.currentTimeMillis();
        double runTime = 10.0; /* seconds */
        double lastTime = startTime;

        sim.addForce(new Gravity());
        sim.addBody(new Rope(new BasicParticle(10.0, 100.0, 10.0, 100.0, 0.0, 0.0),
                             new BasicParticle(20.0, 90.0, 20.0, 90.0, 0.0, 0.0)));

        while(System.currentTimeMillis() < startTime + runTime * 1000.0) {
            lastTime = System.currentTimeMillis();
            sim.simulate();
            int sleepTime = (int)(resolution * 1000.0 - (System.currentTimeMillis() - lastTime) + 0.5);
            //System.out.println("" + sleepTime);
            if(sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}