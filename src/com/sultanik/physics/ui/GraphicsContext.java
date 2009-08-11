package com.sultanik.physics.ui;

public interface GraphicsContext {
    public void clear();
    public void drawLine(double x1, double y1, double x2, double y2);
    public void setColor(java.awt.Color c);
    public void drawBezier(java.awt.geom.Point2D... knots);
    public void setLineThickness(double pixels);
    public double getLineThickness();
    public void drawString(String text, double x, double y);
    public double getXOffset();
    public double getYOffset();
    public double getWidth();
    public double getHeight();
    public void ensureFocus(double x, double y);
}