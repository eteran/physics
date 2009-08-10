package com.sultanik.physics;

import com.sultanik.physics.ui.GraphicsContext;

public class BasicParticle implements Particle {
    double x, y, prevX, prevY, accelX, accelY, density;
    boolean fixed;
    public BasicParticle(double x, double y, double prevX, double prevY, double accelX, double accelY, boolean fixed) {
        this.x = x;
        this.y = y;
        this.prevX = prevX;
        this.prevY = prevY;
        this.accelX = accelX;
        this.accelY = accelY;
        this.fixed = fixed;
        density = 0.0;
    }
    public BasicParticle(double x, double y, double prevX, double prevY, double accelX, double accelY) {
        this(x, y, prevX, prevY, accelX, accelY, false);
    }    
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public double getPreviousX() { return prevX; }
    public double getPreviousY() { return prevY; }
    public void setPreviousX(double x) { prevX = x; }
    public void setPreviousY(double y) { prevY = y; }
    public double getAccelX() { return accelX; }
    public double getAccelY() { return accelY; }
    public void setAccelX(double x) { accelX = x; }
    public void setAccelY(double y) { accelY = y; }
    public boolean isFixed() { return fixed; }
    public void setFixed(boolean fixed) { this.fixed = fixed; }
    public double getDensity() { return density; }
    public void setDensity(double density) { this.density = density; }
    public void paint(GraphicsContext graphicsContext) { }
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}