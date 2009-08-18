package com.sultanik.ui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

public class Bezier implements Shape {
    Point2D[] knots;
    int n;
    double w, h;
    double xOffset, yOffset;
    double[] dx, dy, Ax, Ay, Bi, B0, B1, B2, B3;
    int xPoints[];
    int yPoints[];
    Polygon arrowHead;
    LinkedList<Line2D> lines;
    public static int INTERPOLATION_QUANTA = 26;
    public static int HEAD_LENGTH = 15;
    public static int HEAD_WIDTH = 15;
    GeneralPath path;
    boolean drawArrow;

    public Bezier(Point2D... knots) {
        this(false, knots);
    }

    public Bezier(boolean drawArrow, Point2D... knots) {
        this.drawArrow = drawArrow;
        path = new GeneralPath();
        setKnots(knots);
    }

    public void setKnots(Point2D... knots) throws IllegalArgumentException {
        this.knots = knots;
        n = knots.length;
        if(n < 2) {
            throw new IllegalArgumentException("Error: a bezier curve must have at least two knots/control points!");
        } else if(n == 2) {
            /* we can just draw a straight line! */
            lines = new LinkedList<Line2D>();
            lines.add(new Line2D.Double(knots[0].getX(), knots[0].getY(), knots[1].getX(), knots[1].getY()));
            path.reset();
            path.moveTo((float)knots[0].getX(), (float)knots[0].getY());
            path.lineTo((float)knots[1].getX(), (float)knots[1].getY());
            return;
        }
        w = 0;
        h = 0;
        for(Point2D p : knots) {
            if(p.getX() > w)
                w = p.getX();
            if(p.getY() > h)
                h = p.getY();
        }
        int n1 = n+1;
        dx = new double[n1];
        dy = new double[n1];
        dx[0] = (knots[1].getX() - knots[0].getX()) / 3.0;
        dx[n] = (knots[n-1].getX() - knots[n-2].getX()) / 3.0;
        dy[0] = (knots[1].getY() - knots[0].getY()) / 3.0;
        dy[n] = (knots[n-1].getY() - knots[n-2].getY()) / 3.0;
        Ax = new double[n1];
        Ay = new double[n1];
        Bi = new double[n1];
        B0 = new double[INTERPOLATION_QUANTA];  B1 = new double[INTERPOLATION_QUANTA];  B2 = new double[INTERPOLATION_QUANTA];
        B3 = new double[INTERPOLATION_QUANTA];
        double t = 0;
        for(int i= 0; i< INTERPOLATION_QUANTA; i++) {
            double t1 = 1-t, t12 = t1*t1, t2 = t*t;
            B0[i] = t1*t12;
            B1[i] = 3*t*t12;
            B2[i] = 3*t2*t1;
            B3[i] = t*t2;
            t += .04;
        }
        findCPoints();
        lines = new LinkedList<Line2D>();
        calculateLines();
        arrowHead = null;
    }

    public Point2D[] getKnots() {
        return knots;
    }

    void findCPoints(){
        Bi[1] = -.25;
        Ax[1] = (knots[2].getX() - knots[0].getX() - dx[0])/4.0;
        Ay[1] = (knots[2].getY() - knots[0].getY() - dy[0])/4.0;
        for(int i = 2; i < n-1; i++) {
            Bi[i] = -1.0/(4.0 + Bi[i-1]);
            Ax[i] = -(knots[i+1].getX() - knots[i-1].getX() - Ax[i-1])*Bi[i];
            Ay[i] = -(knots[i+1].getY() - knots[i-1].getY() - Ay[i-1])*Bi[i];
        }
        for (int i = n-1; i > 0; i--) {
            dx[i] = Ax[i] + dx[i+1]*Bi[i];
            dy[i] = Ay[i] + dy[i+1]*Bi[i];
        }
    }

    void calculateLines() {
        float X,Y;
        float Xold = (float)knots[0].getX(), Yold = (float)knots[0].getY();
        path.reset();
        path.moveTo(Xold, Yold);
        for(int i=0; i < n-1; i++) {
            for(int k=0; k < INTERPOLATION_QUANTA; k++) {
                X = (float)(knots[i].getX()*B0[k] + (knots[i].getX()+dx[i])*B1[k] +
                          (knots[i+1].getX()-dx[i+1])*B2[k] + knots[i+1].getX()*B3[k]);
                Y = (float)(knots[i].getY()*B0[k] + (knots[i].getY()+dy[i])*B1[k] +
                             (knots[i+1].getY()-dy[i+1])*B2[k] + knots[i+1].getY()*B3[k]);
                lines.add(new Line2D.Float(Xold, Yold, X, Y));
                path.lineTo(X,Y);
            }
        }
    }

    public boolean intersectsLine(Line2D line) {
        for(Line2D segment : lines)
            if(segment.intersectsLine(line))
                return true;
        return false;
    }

    double[] rotate(double angle, double x, double y, double originX, double originY) {
        double ret[] = new double[2];
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        ret[0] = (x - originX) * c - (y - originY) * s;
        ret[1] = (y - originY) * c + (x - originX) * s;
        ret[0] += originX;
        ret[1] += originY;
        return ret;
    }

    public void drawInterpolated(Graphics g){
	Graphics2D g2d = (Graphics2D)g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g2d.drawPolyline(xPoints, yPoints, xPoints.length);
        g2d.draw(path);
        if(drawArrow && arrowHead == null && knots.length >= 2) {
            /* make the last segment form an arrow */
            double xDiff = knots[knots.length-1].getX() - knots[knots.length-2].getX();
            double yDiff = knots[knots.length-1].getY() - knots[knots.length-2].getY();
            double h = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
            double lastLineAngle;
            if(xDiff == 0) {
                if(yDiff > 0)
                    lastLineAngle = Math.PI / 2.0;
                else
                    lastLineAngle = Math.PI * 3.0 / 2.0;
            } else
                lastLineAngle = Math.atan(yDiff / xDiff);
            if(xDiff < 0)
                lastLineAngle += Math.PI;
            double p0[] = new double[2];
            double p1[] = new double[2];
            double p2[] = new double[2];
            double lastX = knots[knots.length-2].getX();
            double lastY = knots[knots.length-2].getY();
            double advance = (double)HEAD_LENGTH / 4.0;
            p0[0] = lastX + h + advance;
            p0[1] = lastY;
            p1[0] = lastX + h - HEAD_LENGTH + advance;
            p1[1] = lastY + HEAD_WIDTH / 2;
            p2[0] = p1[0];
            p2[1] = lastY - HEAD_WIDTH / 2;

            p0 = rotate(lastLineAngle, p0[0], p0[1], lastX, lastY);
            p1 = rotate(lastLineAngle, p1[0], p1[1], lastX, lastY);
            p2 = rotate(lastLineAngle, p2[0], p2[1], lastX, lastY);
            arrowHead = new Polygon();
            arrowHead.addPoint((int)p0[0], (int)p0[1]);
            arrowHead.addPoint((int)p1[0], (int)p1[1]);
            arrowHead.addPoint((int)p2[0], (int)p2[1]);
        }
        if(drawArrow && arrowHead != null)
            g2d.fillPolygon(arrowHead);
    }

    /* implement the Shape interface based off of our path */
    public boolean contains(double x, double y) {
        return path.contains(x, y);
    }

    public boolean contains(double x, double y, double w, double h) {
        return path.contains(x, y, w, h);
    }

    public boolean contains(Point2D p) {
        return path.contains(p);
    }

    public boolean contains(Rectangle2D r) {
        return path.contains(r);
    }

    public Rectangle getBounds() {
        return path.getBounds();
    }

    public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return path.getPathIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return path.getPathIterator(at, flatness);
    }

    public boolean intersects(double x, double y, double w, double h) {
        return path.intersects(x, y, w, h);
    }

    public boolean intersects(Rectangle2D r) {
        return path.intersects(r);
    }
}
