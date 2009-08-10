package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

public interface Particle {
    public double getX();
    public double getY();
    public void setX(double x);
    public void setY(double y);
    public double getPreviousX();
    public double getPreviousY();
    public void setPreviousX(double x);
    public void setPreviousY(double y);
    public double getVelocity();
    public double getAccelX();
    public double getAccelY();
    public void setAccelX(double x);
    public void setAccelY(double y);
    public boolean isFixed();
    public void setFixed(boolean fixed);
    public double getDensity();
    public void setDensity(double density);
    public void paint(GraphicsContext graphicsContext);
}