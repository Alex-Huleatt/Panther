/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding;

import util.Point;
import util.Stack;

/**
 *
 * @author Alex
 */
public class Panther {

    private final Graph g;
    private final Point[] obstacle_buffer;
    private int buffer_count;
    public static final double BUFFER_MULTIPLIER = .4;
    private boolean[][] map;
    private int num_obstacles;
    private boolean readyToAddEdges;

    public Panther(int length, int height) {
        g = new Graph(length, height);
        obstacle_buffer = new Point[(int) (length * height * BUFFER_MULTIPLIER)];
        buffer_count = 0;
        num_obstacles = 0;
    }

    /**
     * Adds an obstacle to the obstacle buffer. Ensures that no duplicates are
     * added.
     *
     * @param p position of the obstacle to be added.
     */
    public void addObstacle(Point p) {
        if (!map[p.x][p.y]) {
            num_obstacles++;
            map[p.x][p.y] = true;
            obstacle_buffer[buffer_count] = p;
            buffer_count++;
        }
    }

    /**
     * Flushes the obstacle buffer into the graph, constructing new waypoints.
     */
    public void flush() {
        g.addObstacles(obstacle_buffer, buffer_count);
        buffer_count = 0;
        readyToAddEdges = false;
    }

    /**
     * Removes old edges from the graph that are no longer valid.
     */
    public void removeEdges() {
        g.removeEdges();
        readyToAddEdges = true;
    }

    /**
     * Adds new edges to the graph based on new waypoints.
     */
    public void addEdges() {
        if (readyToAddEdges) {
            g.addEdges();
        }
        readyToAddEdges = false;
    }

    /** 
     * @return True if it is safe to add edges to the graph.
     */
    public boolean readyToAddEdges() {
        return readyToAddEdges;
    }

    /**
     * Finds a path.
     * @param start start position.
     * @param dest finish position.
     * @return A stack of positions to travel to.
     */
    public Point[] path(Point start, Point dest) {
        return g.pathfind(start, dest);
    }

    //bunch of methods dealing with lines, intervals, and intersections after here.
    private static Point intersection(Point a, Point b, Point c, Point d) {
        //check vertical lines
        if (b.x == a.x) {
            if (c.x == d.x && a.x != c.x) { //two verticle lines check
                return null;
            }
            double x_val = a.x; //they have to intersect at that x value.
            double m2 = (d.y - c.y) / (d.x - c.x);
            double yInt2 = d.y - m2 * d.x;
            double y_val = m2 * x_val + yInt2;
            return new Point((int) x_val, (int) y_val);
        }
        if (c.x == d.x) { //if second line is vertical check.
            double x_val = c.x; //they have to intersect at that x value.
            double m1 = (b.y - a.y) / (b.x - a.x);
            double yInt1 = b.y - m1 * b.x;
            double y_val = m1 * x_val + yInt1;
            return new Point((int) x_val, (int) y_val);
        }        
        // y= mx + b
        double m1 = (b.y - a.y) / (b.x - a.x);
        //b.y = m1 * b.x + b
        double yInt1 = b.y - m1 * b.x;
        double m2 = (d.y - c.y) / (d.x - c.x);
        double yInt2 = d.y - m2 * d.x;
        //check if they are parallel
        if (m1 == m2 && yInt1 != yInt2) {
            return null;
        }
        double x_val = ((double) (yInt2 - yInt1)) / (m1 - m2);
        double y_val = m1 * x_val + yInt1;
        return new Point((int) x_val, (int) y_val);
    }
    private static boolean interval_check(int low1, int high1, int low2, int high2) {
        return (low1 <= low2 && low1 >= high2) || (low2 <= low1 && low2 >= high1);
    }
    private static boolean interval_check(Point p1, Point p2, Point p3, Point p4) {
        boolean b = interval_check(Math.min(p1.x, p2.x), Math.max(p1.x, p2.x), Math.min(p3.x, p4.x), Math.max(p3.x, p4.x));
        b &= interval_check(Math.min(p1.y, p2.y), Math.max(p1.y, p2.y), Math.min(p3.y, p4.y), Math.max(p3.y, p4.y));
        return b;
    }
    private static boolean isInInterval(int a, int b, int c) {
        return (c <= Math.max(a,b) && c >= Math.min(a,b));
    }  
    private static boolean pointIsInSegment(Point p1, Point p2, Point p3) {
        return isInInterval(p1.x,p2.x,p3.x) && isInInterval(p1.x,p2.x,p3.x);
    }
    
    /**
     * This method checks two paths and returns the positions where they
     * intersect. O(n * m), lengths of the arrays.
     *
     * @param p1
     * @param p2
     * @return Array of points representing the places the paths intersect.
     */
    public static Point[] intersections(Point[] p1, Point[] p2) {
        Point[] intersections = new Point[Math.min(p1.length, p2.length)];
        int index = 0;
        for (int i = 0; i < p1.length - 1; i++) {
            Point a = p1[i];
            Point b = p1[i + 1];
            for (int j = 0; j < p2.length - 1; j++) {
                //check if it is even possible that they connect.
                if (interval_check(a, b, p2[j], p2[j + 1])) {
                    //find the actual intersection point.
                    Point intersectionPoint = intersection(a, b, p2[j], p2[j + 1]);
                    //if it isn't null and is somewhere on both line segments.
                    if (intersectionPoint != null && pointIsInSegment(a,b,intersectionPoint) &&  pointIsInSegment(p2[j],p2[j+1],intersectionPoint)) {
                        intersections[index++] = intersectionPoint;
                    }
                }
            }
        }
        Point[] smaller = new Point[index];
        System.arraycopy(intersections, 0, smaller, 0, index);
        return smaller;
    }
    

    

}
