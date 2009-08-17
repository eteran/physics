package com.sultanik.physics.ui;

import com.sultanik.physics.*;

import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;

public class SimulationPanel extends JPanel implements SimulationListener {
    SwingGraphics sg;
    Simulator simulator;
    FocusProvider focusProvider;

    public SimulationPanel(Simulator simulator) {
        super();
        this.simulator = simulator;
        simulator.addListener(this);
        sg = new SwingGraphics(null, 5.0, getWidth(), getHeight(), 0.0, 0.0);
        focusProvider = null;
        setFocusable(true);
        putClientProperty(com.sun.java.swing.SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(true));
        requestFocusInWindow();
    }

    public void setFocusProvider(FocusProvider focusProvider) {
        this.focusProvider = focusProvider;
    }

    public void handleIteration(double newTime) {
        repaint();
    }

    public void ensureFocus(double x, double y) {
        sg.ensureFocus(x, y);
    }

    public void paint(Graphics graphics) {
        synchronized(simulator.getSimulationMutex()) {
        if(focusProvider != null) {
            Point2D p = focusProvider.getFocalPoint();
            sg.xOffset = p.getX() - sg.getWidth() / 2.0;
            sg.yOffset = p.getY() - sg.getHeight() / 2.0;
            if(sg.xOffset < 0.0)
                sg.xOffset = 0.0;
            if(sg.yOffset < 0.0)
                sg.yOffset = 0.0;
        } else {
            synchronized(sg.focusMutex) {
                if(sg.nextXOffset >= 0.0)
                    sg.xOffset = sg.nextXOffset;
                if(sg.nextYOffset >= 0.0)
                    sg.yOffset = sg.nextYOffset;
                sg.nextXOffset = -1.0;
                sg.nextYOffset = -1.0;
            }
        }
        Graphics2D g2d = (Graphics2D)graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        sg.setGraphics(g2d);
        sg.setWidth(getWidth());
        sg.setHeight(getHeight());
        sg.clear();
        for(Constraint c : simulator.getConstraints())
            c.paint(sg);
        for(Body b : simulator.getBodies())
            b.paint(sg);
        for(Particle p : simulator.getParticles())
            p.paint(sg);
        for(Force f : simulator.getForces())
            f.paint(simulator, sg);
        }
    }
}