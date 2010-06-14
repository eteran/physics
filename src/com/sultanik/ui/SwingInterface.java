package com.sultanik.ui;

import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import java.util.*;

public class SwingInterface extends JPanel implements UserInterface {
    SwingGraphics sg;
    FocusProvider focusProvider;
    LinkedHashSet<RepaintListener> listeners;

    public SwingInterface(String title) {
        super();
        listeners = new LinkedHashSet<RepaintListener>();
        JFrame frame = new JFrame(title);
        setPreferredSize(new Dimension(640,480));
        focusProvider = null;
        setFocusable(true);
        sg = new SwingGraphics(null, 5.0, getWidth(), getHeight(), 0.0, 0.0);
        //putClientProperty(com.sun.java.swing.SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(true));
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        requestFocusInWindow();
    }

    public void addRepaintListener(RepaintListener listener) {
        listeners.add(listener);
    }

    public void removeRepaintListener(RepaintListener listener) {
        listeners.remove(listener);
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
        Graphics2D g2d = (Graphics2D)graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        sg.setGraphics(g2d);
        sg.setWidth(getWidth());
        sg.setHeight(getHeight());
        sg.clear();

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

        for(RepaintListener listener : listeners)
            listener.paint(sg);
    }
}
