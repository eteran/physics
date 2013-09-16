package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;

import java.util.*;

public class Person extends BodyAdapter {
    BasicParticle rightHand, leftHand;
    BasicParticle rightElbow, leftElbow;
    BasicParticle neck, waist;
    BasicParticle leftKnee, rightKnee;
    BasicParticle leftFoot, rightFoot;
    BasicParticle head;
    DistanceConstraint headConstraint;
    boolean broken;
    HashSet<Constraint> brokenConstraints;
    HashSet<Particle> brokenParticles;

    private class BreakingForce implements Force {
        public BreakingForce() { }
        public void applyForce(Simulator simulator) {
            for(Particle p : getParticles()) {
                if(p.getY() <= 0.0) {
                    System.out.println("Broken!");
                    setBroken(true);
                    removeForce(this);
                    break;
                }
            }
        }
        public void paint(Simulator simulator, GraphicsContext graphicsContext) { }
    }

    double neckLength = 0.2;
    double upperArmLength = 0.33;
    double lowerArmLength = 0.46;
    double torsoLength = 0.7;
    double upperLegLength = 0.33;
    double lowerLegLength = 0.46;

    public Person(Particle initialLocation) {
        super();

        // neckLength *= 3.0;
        // upperArmLength *= 3.0;
        // lowerArmLength *= 3.0;
        // torsoLength *= 3.0;
        // upperLegLength *= 3.0;
        // lowerLegLength *= 3.0;

        rightHand = new BasicParticle(initialLocation);
        rightElbow = rightHand.add(new BasicParticle(0, lowerArmLength));
        neck = rightElbow.add(new BasicParticle(0, upperArmLength));
        leftHand = new BasicParticle(initialLocation);
        leftElbow = leftHand.add(new BasicParticle(0, lowerArmLength));
        waist = neck.add(new BasicParticle(0,-torsoLength));
        leftKnee = waist.add(new BasicParticle(0,-upperLegLength));
        rightKnee = new BasicParticle(leftKnee);
        leftFoot = leftKnee.add(new BasicParticle(0,-lowerLegLength));
        rightFoot = new BasicParticle(leftFoot);
        head = neck.add(new BasicParticle(0, neckLength));
        addParticle(rightHand);
        addParticle(rightElbow);
        addParticle(leftHand);
        addParticle(leftElbow);
        addParticle(neck);
        addParticle(waist);
        addParticle(leftKnee);
        addParticle(rightKnee);
        addParticle(leftFoot);
        addParticle(rightFoot);
        addParticle(head);

        addForce(new BreakingForce());
        
        addConstraint(new DistanceConstraint(rightHand, rightElbow, lowerArmLength));
        addConstraint(new DistanceConstraint(rightElbow, neck, upperArmLength));
        addConstraint(new DistanceConstraint(leftHand, leftElbow, lowerArmLength));
        addConstraint(new DistanceConstraint(leftElbow, neck, upperArmLength));
        addConstraint(new DistanceConstraint(neck, waist, torsoLength));
        addConstraint(new DistanceConstraint(waist, leftKnee, upperLegLength));
        addConstraint(new DistanceConstraint(waist, rightKnee, upperLegLength));
        addConstraint(new DistanceConstraint(leftKnee, leftFoot, lowerLegLength));
        addConstraint(new DistanceConstraint(rightKnee, rightFoot, lowerLegLength));
        headConstraint = new DistanceConstraint(neck, head, neckLength);
        addConstraint(headConstraint);
        
        broken = false;

        //addConstraint(new AngleConstraint(rightHand, rightElbow, neck, 0.0, Math.PI / 2.0));
        
        //addForce(new Drag(rightHand, rightElbow));
    }
    public void setBroken(boolean broken) {
        this.broken = broken;
        if(broken == true) {
            brokenConstraints = new HashSet<Constraint>();
            brokenParticles = new HashSet<Particle>();
            BasicParticle neck2 = new BasicParticle(neck);
            brokenParticles.add(neck2);
            brokenConstraints.add(new DistanceConstraint(neck2, head, neckLength));
            BasicParticle neck3 = new BasicParticle(neck);
            brokenParticles.add(neck3);
            brokenConstraints.add(new DistanceConstraint(leftElbow, neck3, upperArmLength));
            BasicParticle neck4 = new BasicParticle(neck);
            brokenParticles.add(neck4);
            brokenConstraints.add(new DistanceConstraint(rightElbow, neck4, upperArmLength));
            brokenConstraints.add(new DistanceConstraint(waist, neck, torsoLength));
            BasicParticle waist2 = new BasicParticle(waist);
            brokenParticles.add(waist2);
            brokenConstraints.add(new DistanceConstraint(leftKnee, waist2, upperLegLength));
            BasicParticle waist3 = new BasicParticle(waist);
            brokenParticles.add(waist3);
            brokenConstraints.add(new DistanceConstraint(rightKnee, waist3, upperLegLength));
            BasicParticle leftElbow2 = new BasicParticle(leftElbow);
            brokenParticles.add(leftElbow2);
            brokenConstraints.add(new DistanceConstraint(leftHand, leftElbow2, lowerArmLength));
            BasicParticle rightElbow2 = new BasicParticle(rightElbow);
            brokenParticles.add(rightElbow2);
            brokenConstraints.add(new DistanceConstraint(rightHand, rightElbow2, lowerArmLength));
            BasicParticle leftKnee2 = new BasicParticle(leftKnee);
            brokenParticles.add(leftKnee2);
            brokenConstraints.add(new DistanceConstraint(leftFoot, leftKnee2, lowerLegLength));
            BasicParticle rightKnee2 = new BasicParticle(rightKnee);
            brokenParticles.add(rightKnee2);
            brokenConstraints.add(new DistanceConstraint(rightFoot, rightKnee2, lowerLegLength));
        }
    }
    public boolean isBroken() { return broken; }
    public Collection<Constraint> getConstraints() {
        if(!broken)
            return super.getConstraints();
        else
            return brokenConstraints;
    }
    public Collection<Particle> getParticles() {
        if(!broken) {
            return super.getParticles();
        } else {
            HashSet<Particle> p = new HashSet<Particle>(super.getParticles());
            p.addAll(brokenParticles);
            return p;
        }
    }
    public Particle getRightHand() {
        return rightHand;
    }
    public void paint(GraphicsContext graphicsContext) {
        super.paint(graphicsContext);
        graphicsContext.setColor(java.awt.Color.GREEN);
        graphicsContext.setLineThickness(2.0);
        for(Constraint c : getConstraints()) {
            if(c instanceof DistanceConstraint) {
                DistanceConstraint dc = (DistanceConstraint)c;
                if(dc == headConstraint) {
                    graphicsContext.fillOval(head.getX() - 0.1, head.getY() - 0.1, 0.2, 0.2);
                } else
                    graphicsContext.drawLine(dc.getP1().getX(), dc.getP1().getY(), dc.getP2().getX(), dc.getP2().getY());
            }
        }
    }
}