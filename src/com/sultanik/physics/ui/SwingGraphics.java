package com.sultanik.physics.ui;

import java.awt.*;
import javax.swing.*;

public class SwingGraphics implements GraphicsContext {
    Graphics2D graphics;
    double pixelsPerMeter;
    int width, height;
    double xOffset, yOffset;

    public SwingGraphics(Graphics2D graphics, double pixelsPerMeter, int width, int height, double xOffset, double yOffset) {
        this.graphics = graphics;
        this.pixelsPerMeter = pixelsPerMeter;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void setGraphics(Graphics2D graphics) {
        this.graphics = graphics;
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

    public void clear() {
        graphics.clearRect(0, 0, width, height);
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        graphics.drawLine((int)((x1 - xOffset) * pixelsPerMeter + 0.5),
                          (int)((y1 - yOffset) * pixelsPerMeter + 0.5),
                          (int)((x2 - xOffset) * pixelsPerMeter + 0.5),
                          (int)((y2 - yOffset) * pixelsPerMeter + 0.5));
    }

    public void setColor(Color c) {
        graphics.setColor(c);
    }
}