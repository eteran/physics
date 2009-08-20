package com.sultanik.games;

import java.awt.event.*;

import com.sultanik.ui.*;
import com.sultanik.physics.*;

public class Swinger {
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
            synchronized(grapple.getSimulator().getSimulationMutex()) {
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
                    grapple.setAngle(grapple.getAngle() + 3.0);
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN
                          ||
                          e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    grapple.setAngle(grapple.getAngle() - 3.0);
                }
            }
        }
    }

    private static class Repainter implements RepaintListener {
        Simulator sim;
        Grapple grapple;
        public Repainter(Simulator sim, Grapple grapple) {this.sim = sim;this.grapple = grapple;}
        public void paint(GraphicsContext sg) {
            synchronized(sim.getSimulationMutex()) {
                /* draw the grapple first */
                grapple.paint(sg);
                for(Constraint c : sim.getConstraints())
                    c.paint(sg);
                for(Body b : sim.getBodies())
                    if(b != grapple)
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
        UserInterface ui = new SwingInterface("Physics");
        //UserInterface ui = new JCurses();

        double startTime = System.currentTimeMillis();
        double runTime = 120.0; /* seconds */
        double lastTime = startTime;

        sim.addForce(new Gravity());
        sim.addForce(new GroundFriction());

        BasicParticle bp = new BasicParticle(2.0,150.0,1.9,150.0,0.0,0.0);
        //bp.setFixed(true);
        Grapple grapple = new Grapple(sim,
                                      bp,
                                      20.0,
                                      30.0,
                                      3.0,
                                      65.0);

        ui.addRepaintListener(new Repainter(sim, grapple));

        sim.addBody(grapple);
        Person p = new Person(grapple.getLocation());
        sim.addBody(p);
        sim.addConstraint(new DistanceConstraint(p.getRightHand(), grapple.getLocation(), 0.0));
        
        ui.addKeyListener(new KeyHandler(grapple));

        ui.setFocusProvider(new Focuser(grapple.getLocation()));

        boolean done = false;

        while(!done) {
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