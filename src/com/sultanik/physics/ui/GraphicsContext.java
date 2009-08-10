package com.sultanik.physics.ui;

public interface GraphicsContext {
    public void clear();
    public void drawLine(double x1, double y1, double x2, double y2);
    public void setColor(java.awt.Color c);
}