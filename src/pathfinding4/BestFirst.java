/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding4;

import java.util.Arrays;
import util.Point;

/**
 *
 * @author Alex
 */
public class BestFirst {

    private final boolean[][] map; //obstacle map.

    private static final double sqrt2 = 1.414; //constant
    private final Point[][] prev;//2D array of parents

    private final Point[] q; //priority queue
    private final double[] costs; //vertex values
    private int index; //number of elements in queue.

    private Point dest; //point to look for (ends up being the starting position)
    public static final int MAX_PATH_LENGTH = 100; //length of the maximum path allowed.

    public BestFirst(boolean[][] map) {
        this.map = map;
        this.prev = new Point[map.length][map[0].length];
        this.q = new Point[1000];
        this.costs = new double[1000];
    }

    private void clear() {
        for (Point[] p : prev) { //empties the parent array
            Arrays.fill(p, null);
        }
    }

    public Point[] pathfind(Point start, Point finish) {
        clear();
        index = 0; //"clear" the priority queue
        Point current;
        dest = start;
        for (int i = 0; i < 8; i++) { //check all neighbors of the start
            check(finish, i);
        }
        final int desx = dest.x;
        final int desy = dest.y;
        if (index != 0) {
            while (true) {
                current = q[--index]; //pop first element of priority queue
                if (current.x == desx && current.y == desy) { //if at the destination, break
                    break;
                }
                expand(current); //expand on the current vertex
                if (index == 0) { //if there are no more vertices, break
                    break;
                }
            }
        }
        return reconstruct(); //reconstruct the path
    }

    /**
     * Reconstruct the path.
     *
     * @return
     */
    private Point[] reconstruct() {
        Point current = dest;
        final Point[] path_temp = new Point[MAX_PATH_LENGTH];
        int count = 0;
        int dir = -1;
        while (current != null) {
            Point next = prev[current.x][current.y];
            if (next == null || dir(current, next) != dir) {
                if (next != null) {
                    dir = dir(current, next);
                }
                path_temp[count++] = current;
            }
            current = next;
        }
        final Point[] path = new Point[count];
        System.arraycopy(path_temp, 0, path, 0, count);

        return path;
    }

    private void expand(Point p) {
        final int dir = dir(prev[p.x][p.y], p);
        switch (dir % 2) {
            case 1: { //diagonal case, 5 potential neighbors
                check(p, (dir + 6) & 7);
                check(p, (dir + 7) & 7);
                check(p, dir & 7);
                check(p, (dir + 9) & 7);
                check(p, (dir + 10) & 7);
                return;
            }
            case 0: { //orthogonal check, 3 potential neighbors
                check(p, (dir + 7) & 7);
                check(p, dir & 7);
                check(p, (dir + 9) & 7);
            }
        }
    }

    /**
     * This looks at a vertex to determine if it should be added to the priority queue.
     * @param parent
     * @param dir 
     */
    private void check(Point parent, int dir) {
        final Point n = moveTo(parent, dir);
        if (validMove(n) && prev[n.x][n.y] == null) { //If valid, and it doesn't have a parent
            prev[n.x][n.y] = parent;
            add(n, distance(n, dest));
        }
    }

    /**
     * Get the effective distance between 2 points
     *
     * @param p1
     * @param p2
     * @return
     */
    private static double distance(Point p1, Point p2) {
        final int dx = Math.abs(p1.x - p2.x);
        final int dy = Math.abs(p1.y - p2.y);
        final int diff = Math.min(dx, dy);
        return diff * sqrt2 + (Math.max(dx, dy) - diff);
    }

    /**
     * Moves a point one cell along direction d.
     *
     * @param p
     * @param d
     * @return
     */
    private Point moveTo(Point p, int d) {
        switch (d) {
            case 0:
                return new Point(p.x, p.y - 1);
            case 1:
                return new Point(p.x + 1, p.y - 1);
            case 2:
                return new Point(p.x + 1, p.y);
            case 3:
                return new Point(p.x + 1, p.y + 1);
            case 4:
                return new Point(p.x, p.y + 1);
            case 5:
                return new Point(p.x - 1, p.y + 1);
            case 6:
                return new Point(p.x - 1, p.y);
            case 7:
                return new Point(p.x - 1, p.y - 1);
            default:
                return null;
        }
    }

    private boolean validMove(Point p) {
        return valid(p) && !map[p.x][p.y];
    }

    private boolean valid(Point p) {
        return (p.x >= 0 && p.x < map.length && p.y >= 0 && p.y < map[0].length);
    }

    /**
     * returns the direction from p1 to p2
     *
     * @param p1
     * @param p2
     * @return
     */
    private int dir(Point p1, Point p2) {
        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;
        if (dx < 0) {
            if (dy < 0) {
                return 7;
            } else if (dy == 0) {
                return 6;
            }
            return 5;
        } else if (dx == 0) {
            if (dy < 0) {
                return 0;
            } else if (dy == 0) {
                return -1;
            }
            return 4;
        }
        if (dy < 0) {
            return 1;
        } else if (dy == 0) {
            return 2;
        }
        return 3;
    }

    public void addObstacle(Point p) {
        map[p.x][p.y] = true;
    }
    
    /**
     * Finds a path between two points, the converts it into an int[] using Point.serialize()
     * @param start
     * @param dest
     * @return 
     */
    public int[] pathAndSerialize(Point start, Point dest) {
        final Point[] path = pathfind(start, dest);
        if (path == null) {
            return null;
        }
        final int[] sd = new int[path.length];
        for (int i = 0; i < path.length; i++) {
            sd[i] = path[i].serialize();
        }
        return sd;
    }

    private void add(Point p, double c) {
        int i = index;
        for (; i > 0 && c > costs[i - 1]; i--) {
            costs[i] = costs[i - 1];
            q[i] = q[i - 1];
        }
        costs[i] = c;
        q[i] = p;
        index++;
    }
}
