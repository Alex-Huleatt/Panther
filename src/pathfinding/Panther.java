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
    public static final double BUFFER_MULTIPLIER = .5;
    private boolean[][] map;
    private int num_obstacles;
    private boolean readyToAddEdges;

    public Panther(int length, int height) {
        g = new Graph(length, height);
        obstacle_buffer = new Point[(int) (length * height * BUFFER_MULTIPLIER)];
        buffer_count = 0;
        num_obstacles = 0;
    }

    public void addObstacle(Point p) {
        if (!map[p.x][p.y]) {
            num_obstacles++;
            map[p.x][p.y] = true;
            obstacle_buffer[buffer_count] = p;
            buffer_count++;
        }
    }

    public void flush() {
        g.addObstacles(obstacle_buffer, buffer_count);
        buffer_count = 0;
        readyToAddEdges = false;
    }

    public void removeEdges() {
        g.removeEdges();
        readyToAddEdges = true;
    }

    public void addEdges() {
        if (readyToAddEdges) {
            g.addEdges();
        }
        readyToAddEdges = false;
    }

    public boolean readyToAddEdges() {
        return readyToAddEdges;
    }

    public Stack<Point> path(Point start, Point dest) {
        return g.pathfind(start, dest);
    }
    
    public boolean intersecting(Point a, Point b, Point c, Point d) {
        if (a.equals(c) || a.equals(d) || b.equals(c) || b.equals(d)) {
            return true;
        }
        
        double m1 = ((double) b.y - a.y) / (b.x - a.x);
        double m2 = ((double) d.y - c.y) / (d.x - c.x);
        if (m1 == m2) return false;
        
        double b1 = -m1 * a.x + a.y;
        double b2 = -m2 * c.x + c.y;
        
        //y3 = m1 * x3 + b1
        //y3 = m2 * x3 + b2
        //m1 * x3 + b1 = m2 * x3 + b2
        //m1 * x3 = m2 * x3 + b2 - b1
        //(m1 * x3) - (m2 * x3) = b2 - b1
        //(m1 - m2) * x3 = b2 - b1
        //x3 = (b2 - b1) /(m1 - m2)
        
        double x3 = (b2 - b1) / (m1 - m2);
        double y3 = m1 * x3 + b1;
        return (x3 < Math.max(a.x, b.x) && x3 > Math.min(a.x,b.x) && y3 < Math.max(a.y, b.y));
                
              
    }
    
    

}
