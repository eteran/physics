package com.sultanik.physics;

import com.sultanik.ui.GraphicsContext;

public class BasicParticle implements Particle {
    double x, y, prevX, prevY, accelX, accelY, density;
    boolean fixed;
    boolean rigid;
    public BasicParticle(Particle copy) {
        this(copy.getX(), copy.getY(), copy.getPreviousX(), copy.getPreviousY(), copy.getAccelX(), copy.getAccelY(), copy.isFixed());
    }
    public BasicParticle(double x, double y) {
        this(x, y, x, y, 0.0, 0.0, false);
    }
    public BasicParticle(double x, double y, double prevX, double prevY, double accelX, double accelY, boolean fixed) {
        this.x = x;
        this.y = y;
        this.prevX = prevX;
        this.prevY = prevY;
        this.accelX = accelX;
        this.accelY = accelY;
        this.fixed = fixed;
        rigid = false;
        density = 0.0;
    }
    public BasicParticle(double x, double y, double prevX, double prevY, double accelX, double accelY) {
        this(x, y, prevX, prevY, accelX, accelY, false);
    }
    public BasicParticle add(Particle addend) {
        BasicParticle copy = new BasicParticle(this);
        copy.setX(copy.getX() + addend.getX());
        copy.setY(copy.getY() + addend.getY());
        copy.setPreviousX(copy.getPreviousX() + addend.getPreviousX());
        copy.setPreviousY(copy.getPreviousY() + addend.getPreviousY());
        copy.setAccelX(copy.getAccelX() + addend.getAccelX());
        copy.setAccelY(copy.getAccelY() + addend.getAccelY());
        return copy;
    }
    public BasicParticle subtract(Particle subtrahend) {
        BasicParticle copy = new BasicParticle(this);
        copy.setX(copy.getX() - subtrahend.getX());
        copy.setY(copy.getY() - subtrahend.getY());
        copy.setPreviousX(copy.getPreviousX() - subtrahend.getPreviousX());
        copy.setPreviousY(copy.getPreviousY() - subtrahend.getPreviousY());
        copy.setAccelX(copy.getAccelX() - subtrahend.getAccelX());
        copy.setAccelY(copy.getAccelY() - subtrahend.getAccelY());
        return copy;
    }
    static double angleFromX(double x, double y) {
        if(x == 0.0) {
            if(y == 0.0)
                return 0.0;
            else if(y > 0.0)
                return Math.PI / 2.0;
            else
                return Math.PI / -2.0;
        } else if(y >= 0.0) {
            double a = Math.atan(y / x);
            if(x < 0.0)
                a = Math.PI + a;
            return a;
        } else {
            double a = Math.atan(-y / -x);
            if(x >= 0.0)
                a += Math.PI;
            return Math.PI + a;
        }
    }
    public BasicParticle rotate(double angle) {
        return rotate(angle, true);
    }
    public BasicParticle rotate(double angle, boolean preserveVelocity) {
        BasicParticle copy = new BasicParticle(this);
        if(getX() == 0.0 && getY() == 0.0)
            return copy;
        double length = Math.sqrt(getX() * getX() + getY() * getY());
        double a = angleFromX(getX(), getY());
        copy.setX(length * Math.cos(angle + a));
        copy.setY(length * Math.sin(angle + a));
        if(preserveVelocity) {
            /* now rotate the previous position */
            length = Math.sqrt(getPreviousX() * getPreviousX() + getPreviousY() * getPreviousY());
            a = angleFromX(getPreviousX(), getPreviousY());
            copy.setPreviousX(length * Math.cos(angle + a));
            copy.setPreviousY(length * Math.sin(angle + a));
        }
        return copy;
    }
    public BasicParticle rotate(double angle, Particle center) {
        return rotate(angle, center, true);
    }
    public BasicParticle rotate(double angle, Particle center, boolean preserveVelocity) {
        return this.subtract(center).rotate(angle, true).add(center);
    }
    public double getVelocity() {
        return Math.hypot(getX() - getPreviousX(), getY() - getPreviousY());
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
    public boolean isRigid() { return rigid || fixed; };
    public void setRigid(boolean rigid) { this.rigid = rigid;}
    public boolean isFixed() { return fixed; }
    public void setFixed(boolean fixed) { this.fixed = fixed; }
    public double getDensity() { return density; }
    public void setDensity(double density) { this.density = density; }
    public void paint(GraphicsContext graphicsContext) { }
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}