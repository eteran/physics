package com.sultanik.physics.ui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class SwingGraphics implements GraphicsContext {
    Graphics2D graphics;
    double pixelsPerMeter;
    int width, height;
    double xOffset, yOffset;
    double lineThickness;

    public SwingGraphics(Graphics2D graphics, double pixelsPerMeter, int width, int height, double xOffset, double yOffset) {
        this.graphics = graphics;
        this.pixelsPerMeter = pixelsPerMeter;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        lineThickness = 1.0;
    }

    public void setGraphics(Graphics2D graphics) {
        this.graphics = graphics;
        setLineThickness(getLineThickness());
    }

    public double getLineThickness() {
        return lineThickness;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public double getXOffset() { return xOffset; }
    public double getYOffset() { return yOffset; }
    public double getWidth() { return (double)width / pixelsPerMeter; }
    public double getHeight() { return (double)height / pixelsPerMeter; }

    public void clear() {
        graphics.clearRect(0, 0, width, height);
    }

    public void setLineThickness(double pixels) {
        lineThickness = pixels;
	graphics.setStroke(new BasicStroke((float)pixels));
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        graphics.drawLine((int)((x1 - xOffset) * pixelsPerMeter + 0.5),
                          height - (int)((y1 - yOffset) * pixelsPerMeter + 0.5),
                          (int)((x2 - xOffset) * pixelsPerMeter + 0.5),
                          height - (int)((y2 - yOffset) * pixelsPerMeter + 0.5));
    }

    public void drawBezier(Point2D... knots) {
        Point2D k[] = new Point2D[knots.length];
        for(int i=0; i<knots.length; i++)
            k[i] = new Point2D.Double((knots[i].getX() - xOffset) * pixelsPerMeter,
                                      (double)height - (knots[i].getY() - yOffset) * pixelsPerMeter);
        (new Bezier(k)).drawInterpolated(graphics);
    }

    public void drawString(String text, double x, double y) {
        graphics.drawString(text,
                            (int)((x - xOffset) * pixelsPerMeter + 0.5),
                            height - (int)((y - yOffset) * pixelsPerMeter + 0.5));
    }

    public void setColor(Color c) {
        graphics.setColor(c);
    }
}