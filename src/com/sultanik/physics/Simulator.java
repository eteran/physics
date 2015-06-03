package com.sultanik.physics;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.sultanik.ui.FocusProvider;
import com.sultanik.ui.GraphicsContext;
import com.sultanik.ui.JCurses;
import com.sultanik.ui.RepaintListener;
import com.sultanik.ui.UserInterface;

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
    HashSet<Constraint> constraints;
    double timeStep;
    double t;
    Object simulationMutex = new Object();

    private static final int	CONSTRAINT_SATISFICATION_ITERATIONS	= 100;
    private static final double	CONSTRAINT_SATISFICATION_THRESHOLD	= 0.001;

    public Simulator(double timeStep) {
        listeners = new LinkedHashSet<>();
        bodies = new HashSet<>();
        forces = new HashSet<>();
        constraints = new HashSet<>();
        this.timeStep = timeStep;
        t = 0.0;
    }

    public void addListener(SimulationListener listener) {
        synchronized(listeners) {
            listeners.add(listener);
        }
    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    public void removeConstraint(Constraint constraint) {
        constraints.remove(constraint);
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

    public double currentTime() {
        return t;
    }

    void accumulateForces(HashSet<Particle> particles, HashSet<Constraint> constraints) {
        /* first, clear all of the previous forces */
        for(Particle p : particles) {
            p.setAccelX(0.0);
            p.setAccelY(0.0);
        }
        for(Force f : getForces())
            f.applyForce(this);
    }

    void vertlet(HashSet<Particle> particles) {
        for(Particle p : particles) {
            if(!p.isFixed()) {
                double tmpX = p.getX();
                double tmpY = p.getY();
                p.setX(2.0 * tmpX - p.getPreviousX() + p.getAccelX() * timeStep * timeStep);
                p.setY(2.0 * tmpY - p.getPreviousY() + p.getAccelY() * timeStep * timeStep);
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
        HashSet<Particle> particles = new HashSet<>();
        for(Body body : bodies)
            particles.addAll(body.getParticles());
        return particles;
    }

    public HashSet<Constraint> getConstraints() {
        HashSet<Constraint> constraints = new HashSet<>();
        for(Body body : bodies)
            constraints.addAll(body.getConstraints());
        constraints.addAll(this.constraints);
        return constraints;        
    }

    public HashSet<Body> getBodies() { 
        synchronized(getSimulationMutex()) {
            return bodies;
        }
    }
    public HashSet<Force> getForces() { 
        HashSet<Force> allForces = new HashSet<>(forces);
        for(Body b : bodies)
            allForces.addAll(b.getForces());
        return allForces;
    }

    public Object getSimulationMutex() {
        return simulationMutex;
    }

    public void simulate() {
        synchronized(simulationMutex) {
            t += timeStep;
            HashSet<Particle> particles = getParticles();
            HashSet<Constraint> constraints = getConstraints();

            accumulateForces(particles, constraints);
            vertlet(particles);
            satisfyConstraints(particles, constraints);
        }

        synchronized(listeners) {
            LinkedHashSet<SimulationListener> l = new LinkedHashSet<>(listeners);
            /* we need to iterate over l as opposed to listeners just
             * in case one of the listeners removes itself from the
             * listeners during this iteration (which would otherwise
             * cause a concurrent modification exception). */
            for(SimulationListener sl : l)
                sl.handleIteration(t);
        }
    }

    public void simulate(double untilTime) {
        while(t <= untilTime)
            simulate();
    }

    public void addForce(Force force) {
        forces.add(force);
    }

    public void addBody(Body body) {
        synchronized(simulationMutex) {
            bodies.add(body);
        }
    }

    public void removeBody(Body body) {
        synchronized(getSimulationMutex()) {
            bodies.remove(body);
        }
    }

    private static class Focuser implements FocusProvider {
        Particle p;
        public Focuser(Particle p) {
            this.p = p;
        }
        public java.awt.geom.Point2D getFocalPoint() {
            return new java.awt.geom.Point2D.Double(p.getX(), p.getY());
        }
    }

    public static class KeyHandler extends KeyAdapter {
        Grapple grapple;

        public KeyHandler(Grapple grapple) { this.grapple = grapple; }

        public void keyPressed(KeyEvent e) {
            synchronized(grapple.simulator.getSimulationMutex()) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    grapple.grapple();
                } else if(e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
                    if(grapple.isAttached())
                        grapple.detatchRope();
                    else
                        grapple.attachGrapple();
                } else if(e.getKeyCode() == KeyEvent.VK_UP
                          ||
                          e.getKeyCode() == KeyEvent.VK_LEFT) {
                    grapple.setAngle(grapple.getAngle() + 5.0);
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN
                          ||
                          e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    grapple.setAngle(grapple.getAngle() - 5.0);
                }
            }
        }
    }

    private static class Repainter implements RepaintListener {
        Simulator sim;
        public Repainter(Simulator sim) {this.sim = sim;}
        public void paint(GraphicsContext sg) {
            synchronized(sim.getSimulationMutex()) {
                for(Constraint c : sim.getConstraints())
                    c.paint(sg);
                for(Body b : sim.getBodies())
                    b.paint(sg);
                for(Particle p : sim.getParticles())
                    p.paint(sg);
                for(Force f : sim.getForces())
                    f.paint(sim, sg);
            }
        }
    }

    public static void main(String[] args) {
        double resolution = 0.02;
        Simulator sim = new Simulator(resolution);
        //UserInterface ui = new SwingInterface("Physics");
        UserInterface ui = new JCurses();
        ui.addRepaintListener(new Repainter(sim));

        double startTime = System.currentTimeMillis();
        double runTime = 120.0; /* seconds */
        double lastTime = startTime;

        sim.addForce(new Gravity());
        sim.addForce(new GroundFriction());
        // BasicParticle bp = new BasicParticle(11.0, 11.0, 10.8, 10.8, 0.0, 0.0);
        // Rope rope = new Rope(new BasicParticle(10.0, 10.0, 9.8, 9.8, 0.0, 0.0), bp);
        // for(int i=0; i<8; i++)
        //     rope.addLink(new BasicParticle(9.0 - (double)i, 9.0 - (double)i, 8.8 - (double)i, 8.8 - (double)i, 0.0, 0.0), 1.4142);
        // sim.addBody(rope);
        // //bp.setFixed(true);

        BasicParticle bp = new BasicParticle(2.0,150.0,1.9,150.0,0.0,0.0);
        //bp.setFixed(true);
        Grapple grapple = new Grapple(sim,
                                      bp,
                                      20.0,
                                      30.0,
                                      3.0,
                                      65.0);
        sim.addBody(grapple);
        Person p = new Person(grapple.getLocation());
        sim.addBody(p);
        sim.addConstraint(new DistanceConstraint(p.getRightHand(), grapple.getLocation(), 0.0));
        
        ui.addKeyListener(new KeyHandler(grapple));

        ui.setFocusProvider(new Focuser(grapple.getLocation()));

        while(System.currentTimeMillis() < startTime + runTime * 1000.0) {
            lastTime = System.currentTimeMillis();
            sim.simulate();
            ui.repaint();
            int sleepTime = (int)(resolution * 1000.0 - (System.currentTimeMillis() - lastTime) + 0.5);
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