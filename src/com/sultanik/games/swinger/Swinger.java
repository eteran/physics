package com.sultanik.games;

import java.awt.event.*;
import java.awt.*;

import com.sultanik.ui.*;
import com.sultanik.physics.*;

public class Swinger {
    private static class BuildingCluster {
        Building leftSceneBuilding;
        Building first, last;

        public BuildingCluster() {
            leftSceneBuilding = null;
            first = null;
            last = null;
        }

        public void add(double x, double height, double width) {
            new Building(this, last, x, height, width);
        }

        public void paint(GraphicsContext gc) {
            if(leftSceneBuilding != null)
                leftSceneBuilding.paint(gc);
        }
    }
    
    private static class Building {
        double height;
        Building left, right;
        double x, width;
        BuildingCluster bc;

        public Building(BuildingCluster bc, double x, double height, double width) {
            this(bc, null, x, height, width);
        }

        public Building(BuildingCluster bc, Building left, double x, double height, double width) {
            this.bc = bc;
            this.x = x;
            this.height = height;
            this.width = width;
            bc.last = this;
            if(left == null) {
                bc.first = this;
                bc.leftSceneBuilding = this;
            }
            if(left != null)
                left.right = this;
            bc.last = this;
            this.left = left;
        }

        public Building getLeft() { return left; }
        public Building getRight() { return right; }

        void internalPaint(GraphicsContext gc) {
            gc.setLineThickness(4.0);
            gc.setColor(Color.BLUE);
            gc.drawLine(x, 0, x, height);
            gc.drawLine(x, height, x + width, height);
            gc.drawLine(x + width, height, x + width, 0);
            gc.drawLine(x + width, 0, x, 0);
        }

        public void paint(GraphicsContext gc) {
            boolean wasFirst = false;
            if(bc.leftSceneBuilding == this) {
                bc.leftSceneBuilding = null;
                wasFirst = true;
            }
            if(x + width < gc.getXOffset()) {
                if(right == null) {
                    if(bc.leftSceneBuilding == null)
                        bc.leftSceneBuilding = bc.first;
                    return;
                } else
                    right.paint(gc);
            } else if(x > gc.getXOffset() + gc.getWidth()) {
                if(wasFirst) {
                    /* see if anyone to our left is available */
                    Building lsb = this;
                    while(lsb.left != null && lsb.left.x > gc.getXOffset() + gc.getWidth())
                        lsb = lsb.left;
                    if(lsb.x <= gc.getXOffset() + gc.getWidth()) {
                        /* we found a left neighbor that is on the screen */
                        bc.leftSceneBuilding = lsb;
                        lsb.paint(gc);
                        return;
                    }
                }
                if(bc.leftSceneBuilding == null)
                    bc.leftSceneBuilding = bc.first;
                return;
            } else {
                if(bc.leftSceneBuilding == null)
                    bc.leftSceneBuilding = this;
                internalPaint(gc);
                if(right != null)
                    right.paint(gc);
            }
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
        BuildingCluster bc;
        public Repainter(Simulator sim, Grapple grapple, BuildingCluster bc) {this.sim = sim;this.grapple = grapple; this.bc = bc;}
        public void paint(GraphicsContext sg) {
            synchronized(sim.getSimulationMutex()) {
                /* draw the buildings first */
                bc.paint(sg);
                /* draw the grapple second */
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
        /* construct the buildings */
        BuildingCluster bc = new BuildingCluster();
        bc.add(10.0, 100.0, 5.0);
        bc.add(30.0, 50.0, 5.0);

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

        ui.addRepaintListener(new Repainter(sim, grapple, bc));

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