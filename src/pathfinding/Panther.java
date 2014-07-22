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
    private boolean readyToAddEdges;

    public Panther(int length, int height) {
        g = new Graph(length, height);
        obstacle_buffer = new Point[(int) (length * height * BUFFER_MULTIPLIER)];
        buffer_count = 0;
    }

    public void addObstacle(Point p) {
        if (!map[p.x][p.y]) {
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
    
    

}
