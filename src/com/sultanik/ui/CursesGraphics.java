package com.sultanik.ui;

import java.awt.Color;
import java.awt.geom.Point2D;

public class CursesGraphics implements GraphicsContext {
    double pixelsPerMeter;
    int width, height;
    double nextXOffset, nextYOffset;
    double xOffset, yOffset;
    double lineThickness;
    Object focusMutex = new Object();
    JCurses jCurses;

    public CursesGraphics(JCurses jCurses, double pixelsPerMeter, int width, int height, double xOffset, double yOffset) {
        this.jCurses = jCurses;
        this.pixelsPerMeter = pixelsPerMeter;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        nextXOffset = -1.0;
        nextYOffset = -1.0;
        lineThickness = 1.0;
    }

    public void ensureFocus(double x, double y) {
        synchronized(focusMutex) {
            nextXOffset = x - getWidth() / 2.0;
            if(nextXOffset < 0.0)
                nextXOffset = 0.0;
            nextYOffset = y - getHeight() / 2.0;
            if(nextYOffset < 0.0)
                nextYOffset = 0.0;
        }
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

    public void drawArc(double originX, double originY, double radius, double startAngle, double endAngle) {
    }

    public void clear() {
        jCurses.clear();
    }

    public void setLineThickness(double pixels) {
        lineThickness = pixels;
    }

    public void drawLine(double lx1, double ly1, double lx2, double ly2) {
        int x1 = (int)((lx1 - xOffset) * pixelsPerMeter + 0.5);
        int y1 = jCurses.getHeight() - (int)((ly1 - yOffset) * pixelsPerMeter + 0.5);
        int x2 = (int)((lx2 - xOffset) * pixelsPerMeter + 0.5);
        int y2 = jCurses.getHeight() - (int)((ly2 - yOffset) * pixelsPerMeter + 0.5);

        int Dx = x2 - x1; 
        int Dy = y2 - y1;
        boolean steep = (Math.abs(Dy) >= Math.abs(Dx));
        if(steep) {
            int tmp = x1;
            x1 = y1;
            y1 = tmp;
            tmp = x2;
            x2 = y2;
            y2 = tmp;
            // recompute Dx, Dy after swap
            Dx = x2 - x1;
            Dy = y2 - y1;
        }
        int xstep = 1;
        if (Dx < 0) {
            xstep = -1;
            Dx = -Dx;
        }
        int ystep = 1;
        if (Dy < 0) {
            ystep = -1;		
            Dy = -Dy; 
        }
        int TwoDy = 2*Dy; 
        int TwoDyTwoDx = TwoDy - 2*Dx; // 2*Dy - 2*Dx
        int E = TwoDy - Dx; //2*Dy - Dx
        int y = y1;
        int xDraw, yDraw;
        int x;
        int lastX = 0, lastY = 0;
        String lineChar = "X";
        for(x = x1; x != x2; x += xstep) {		
            if (steep) {			
                xDraw = y;
                yDraw = x;
            } else {			
                xDraw = x;
                yDraw = y;
            }
            // plot
            //            if(xDraw >= 0 && xDraw < jCurses.getWidth() && yDraw >= 0 && yDraw < jCurses.getHeight())
            //    jCurses.drawString("X", xDraw, yDraw);
            if(x != x1) {
                if(yDraw == lastY && ystep < 0)
                    lineChar = "_";
                else if(yDraw == lastY)
                    lineChar = "-";
                else if(xDraw == lastX)
                    lineChar = "|";
                else if((xDraw > lastX && yDraw > lastY)
                        ||
                        (xDraw < lastX && yDraw < lastY))
                    lineChar = "\\";
                else
                    lineChar = "/";
                if(lastX >= 0 && lastX < jCurses.getWidth() && lastY >= 0 && lastY < jCurses.getHeight())
                    jCurses.drawString(lineChar, lastX, lastY);
            }
            lastX = xDraw;
            lastY = yDraw;
            // next
            if (E > 0) {
                E += TwoDyTwoDx; //E += 2*Dy - 2*Dx;
                y = y + ystep;
            } else {
                E += TwoDy; //E += 2*Dy;
            }
        }
        if(lastX >= 0 && lastX < jCurses.getWidth() && lastY >= 0 && lastY < jCurses.getHeight())
            jCurses.drawString(lineChar, lastX, lastY);
    }

    public void drawBezier(Point2D... knots) {
        for(int i=0; i<knots.length - 1; i++)
            drawLine(knots[i].getX(), knots[i].getY(), knots[i+1].getX(), knots[i+1].getY());
    }

    public void fillOval(double x, double y, double w, double h) {
    }

    public void drawString(String text, double x, double y) {
        jCurses.drawString(text, (int)((x - xOffset) * pixelsPerMeter + 0.5), jCurses.getHeight() - (int)((y - yOffset) * pixelsPerMeter + 0.5));
    }

    public void setColor(Color c) {
        jCurses.setColor(c);
    }

	@Override
	public double getWidth(String text) {
		return text.length();
	}

	@Override
	public double getFontHeight() {
		return 1;
	}
}