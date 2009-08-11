package com.sultanik.physics.ui;

import com.sultanik.physics.*;

import java.awt.*;
import javax.swing.*;

public class SimulationPanel extends JPanel implements SimulationListener {
    SwingGraphics sg;
    Simulator simulator;

    public SimulationPanel(Simulator simulator) {
        super();
        this.simulator = simulator;
        simulator.addListener(this);
        sg = new SwingGraphics(null, 3.0, getWidth(), getHeight(), 0.0, 0.0);
    }

    public void handleIteration(double newTime) {
        repaint();
    }

    public void ensureFocus(double x, double y) {
        sg.ensureFocus(x, y);
    }

    public void paint(Graphics graphics) {
        synchronized(sg.focusMutex) {
            if(sg.nextXOffset >= 0.0)
                sg.xOffset = sg.nextXOffset;
            if(sg.nextYOffset >= 0.0)
                sg.yOffset = sg.nextYOffset;
            sg.nextXOffset = -1.0;
            sg.nextYOffset = -1.0;
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