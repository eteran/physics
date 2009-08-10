package com.sultanik.physics;

public class Simulator {
    /**
     * The version of Physics.
     */
    public static final String VERSION  = "0.1";
    /**
     * The revision date for this version of Physics.
     */
    public static final String REV_DATE = "2009-08-10";

    HashSet<Body> bodies;
    HashSet<Force> forces;
    double timeStep;
    double t;

    private static final int	CONSTRAINT_SATISFICATION_ITERATIONS	= 100;
    private static final double	CONSTRAINT_SATISFICATION_THRESHOLD	= 0.001;

    public Simulator(double timeStep) {
        bodies = new HashSet<Body>();
        forces = new HashSet<Force>();
        timeStep = 0.01;
        t = 0.0;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public double getTimeStep() {
        return timeStep;
    }

    void accumulateForces(HashSet<Particle> particles, HashSet<Constraints> constraints) {
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
                p.setPreviousX(tmpX);
                p.setPreviousY(tmpY);
            }
        }
    }

    void satisfyConstraints(HashSet<Particle> particles, HashSet<Constraints> constraints) {
        double maxDiff = CONSTRAINT_SATISFICATION_THRESHOLD;
        for(int i=0; i <= CONSTRAINT_SATISFICATION_ITERATIONS && max_diff >= CONSTRAINT_SATISFICATION_THRESHOLD; i++) {
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

    public void simulate() {
        t += timeStep;
        HashSet<Particle> particles = new HashSet<Particle>();
        HashSet<Constraint> constraints = new HashSet<Constraint>();
        /* collect all of the particles and constraints for this step */
        for(Body body : bodies) {
            for(Particle p : body.getParticles())
                particles.add(p);
            for(Constraint c : body.getConstraints())
                constraints.add(c);
        }
        accumulateForces(particles, constraints);
        vertlet(particles);
        satisfyConstraints(particles, constraints);
    }

    public void simulate(double untilTime) {
        while(t <= untilTime)
            simulate();
    }

    public static void main(String[] args) {
        double startTime = System.currentTimeMillis();
        double runTime = 10.0; /* seconds */
        while(System.currentTimeMillis() < startTime + runTime * 1000.0)
            simulate((System.currentTimeMillis() - startTime) / 1000.0);
    }
}