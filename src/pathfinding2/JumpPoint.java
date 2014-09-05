/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding2;

import java.util.Arrays;
import java.util.HashMap;
import util.Point;
import util.PointDoubleHeap;

/**
 *
 * @author Alex
 */
public final class JumpPoint {

    public boolean[][] map;

    public final Point[][] prev;
    public final double[][] cost;
    public final boolean[][] closed_set;

    public static final double sqrt2 = 1.414;
    private static final double octile_constant = .41421356237;

    private Point dest;

    private static final Point null_point = new Point(-1, -1);

    private PointDoubleHeap pq;

    private boolean redone;

    /**
     * Construct a new pathfinder thingy for a given map. The map can be updated
     * later too. with AddObstacle(Point p).
     *
     * @param map
     */
    public JumpPoint(boolean[][] map) {
        this.map = map;
        this.prev = new Point[map.length][map[0].length];
        this.cost = new double[map.length][map[0].length];
        this.closed_set = new boolean[map.length][map[0].length];
    }

    /**
     * Find a path
     *
     * @param start
     * @param dest
     * @return
     */
    public Point[] pathfind(Point start, Point dest) {
        init();
        this.dest = dest;
        Point current = start;
        int desx = dest.x;
        int desy = dest.y;
        do {
            expand(current);
            current = pq.pop();
        } while (current != null && (current.x != desx || current.y != desy));

        return reconstruct();
    }

    /**
     * Reconstruct the path.
     *
     * @return
     */
    private Point[] reconstruct() {
        Point current = dest;
        final Point[] path_temp = new Point[500];

        int count = 0;
        while (current != null) {
            path_temp[count++] = current;
            current = prev[current.x][current.y];
        }
        final Point[] path = new Point[count];
        for (int i = 0; i < count; i++) {
            path[i] = path_temp[(count - 1) - i];
        }
        return path;
    }

    /**
     * Get the practical distance between 2 points
     *
     * @param p1
     * @param p2
     * @return
     */
    public static double distance(Point p1, Point p2) {
        int dx = Math.abs(p1.x - p2.x);
        int dy = Math.abs(p1.y - p2.y);
        int diff = Math.min(dx, dy);
        return diff * sqrt2 + (Math.max(dx, dy) - diff);
    }

    /**
     * Expand a vertex, adding neighbors to the priority queue.
     *
     * @param p
     */
    public void expand(Point p) {
        if (closed_set[p.x][p.y]) {
            return;
        }
        closed_set[p.x][p.y] = true;
        final Point[] neighbors = getNeighbors(p);
        
        for (Point n : neighbors) {
            if (validMove(n) && !closed_set[n.x][n.y]) check(p,n);
        }
    }
    
    private void check(Point parent,Point n) {
        final double potential_cost = cost[parent.x][parent.y] + distance(parent, n);
        if (prev[n.x][n.y] == null || cost[n.x][n.y] > potential_cost) {
            prev[n.x][n.y] = parent;
            cost[n.x][n.y] = potential_cost;
            pq.add(n, potential_cost + octile(n, dest)*2);

        }
    }

    /**
     * heuristic for use with A
     *
     *
     * @param p1
     * @param p2
     * @return
     */
    private double octile(Point p1, Point p2) {
        int x = Math.abs(p2.x - p1.x);
        int y = Math.abs(p2.y - p1.y);
        return (Math.max(x, y) + octile_constant * Math.min(x, y));
    }

    /**
     * Return the neighbors of position p
     *
     * @param p
     * @return
     */
    public Point[] getNeighbors(Point p) {
        if (prev[p.x][p.y] == null) {
            return originN(p);
        }
        int dir = dir(prev[p.x][p.y], p);

        switch (dir % 2) {
            case 0:
                return orthogN(p, dir);
            case 1:
                return diagN(p, dir);
            default:
                return null;
        }
    }

    /**
     * Finds the neighbors of a cell given that the cell is the start point
     *
     * @param p
     * @return
     */
    public Point[] originN(Point p) {
        final Point[] neighbors = new Point[8];
        Point n;
        //special case, start node, expand in every direction
        for (int i = 0; i < 8; i++) {
            n = (i%2==0)?orth_next(p, i):diag_next(p,i);
            if (validMove(n)) {
                neighbors[i] = n;
            }
        }
        return neighbors;
    }

    /**
     * Finds the neighbors of a cell given that we are expanding in an
     * orthogonal direction
     *
     * @param p
     * @param dir
     * @return
     */
    public Point[] orthogN(Point p, int dir) {
        final Point[] neighbors = new Point[3];
        int count = 1;

        neighbors[0] = orth_next(p, dir);

        if (isObs(moveTo(p, (dir + 2) & 7))) {
            neighbors[count++] = diag_next(p, (dir + 1) & 7);
        }
        if (isObs(moveTo(p, (dir + 6) & 7))) {
            neighbors[count++] = diag_next(p, (dir + 7) & 7);
        }
        return neighbors;
    }

    /**
     * Finds the neighbors of a cell given that we are expanding in a diagonal
     * direction
     *
     * @param p
     * @param dir
     * @return
     */
    public Point[] diagN(Point p, int dir) {
        final Point[] neighbors = new Point[5];
        int count = 3;
        neighbors[0] = diag_next(p, dir);

        neighbors[1] = orth_next(p, (dir + 7) & 7);

        neighbors[2] = orth_next(p, (dir + 1) & 7);

        if (isObs(moveTo(p, (dir + 3) & 7))) {
            neighbors[count++] = diag_next(p, (dir + 2) & 7);
        }

        if (isObs(moveTo(p, (dir + 5) & 7))) {
            neighbors[count++] = diag_next(p, (dir + 6) & 7);
        }

        return neighbors;
    }

    /**
     * Returns true if this spot is a valid move.
     *
     * @param p
     * @return
     */
    public boolean validMove(Point p) {
        return (p != null && !p.equals(null_point) && isValid(p.x, p.y) && !map[p.x][p.y]);
    }

    /**
     * Finds the next important neighbor along a direction
     *
     * @param p
     * @param dir
     * @return
     */
    public Point orth_next(Point p, int dir) {
        if (!validMove(p)) {
            return null;
        }
        Point temp = moveTo(p, dir);
        int dx = temp.x - p.x;
        int dy = temp.y - p.y;
        int c = 0;
        while (validMove(temp)) {
            if (isJumpPoint(temp, dir) || isGoal(temp) || c == 4) {
                return temp;
            }
            temp.x += dx;
            temp.y += dy;
            c++;
        }
        return null;
    }
    
        public Point diag_next(Point p, int dir) {
//        redone = false;
//        if (!validMove(p)) {
//            return null;
//        }
//        Point temp = moveTo(p, dir);
//        int dx = temp.x - p.x;
//        int dy = temp.y - p.y;
//        while (validMove(temp)) {
//            if (isJumpPoint(temp, dir) || isGoal(temp)) {
//                return temp;
//            }
//            temp.x += dx;
//            temp.y += dy;
//        }
//        return null;
          Point t = moveTo(p,dir);
          if (validMove(t)) return t;
          return null;
    }

    /**
     * Returns true if the position is an important neighbor.
     *
     * @param p
     * @param dir
     * @return
     */
    public boolean isJumpPoint(Point p, int dir) {
        if (isObs(p)) {
            return false;
        }
        switch (dir % 2) {
            case 0: {

                if (isObs(moveTo(p, (dir + 2) & 7)) && validMove(moveTo(p, (dir + 1) & 7))) {
                    return true;
                }
                return isObs(moveTo(p, (dir + 6) & 7)) && validMove(moveTo(p, (dir + 7) & 7));
            }
            case 1: {
                if (isObs(moveTo(p, (dir + 3) & 7)) && validMove(moveTo(p, (dir + 2) & 7))) {
                    return true;
                }

                if (isObs(moveTo(p, (dir + 5) & 7)) && validMove(moveTo(p, (dir + 6) & 7))) {
                    return true;
                }

                if (orth_next(p, (dir + 1) & 7) != null) {
                    return true;
                }

                return (orth_next(p, (dir + 7) & 7) != null);
            }
        }
        return false;
    }

    public boolean isValid(int x, int y) {
        return (x < map.length && x >= 0 && y < map[0].length && y >= 0);
    }

    public boolean isObs(Point p) {
        return (isValid(p.x, p.y) && map[p.x][p.y]);
    }

    public boolean isGoal(Point p) {
        return (isValid(p.x, p.y) && p.equals(dest));
    }

    /**
     * Initialize class variables for use in pathfinding
     */
    public void init() {
        for (Point[] p : prev) {
            Arrays.fill(p, null);
        }
        for (boolean[] b : closed_set) {
            Arrays.fill(b, false);
        }

        pq = new PointDoubleHeap(1000);

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

    /**
     * returns the direction from p1 to p2
     *
     * @param p1
     * @param p2
     * @return
     */
    private int dir(Point p1, Point p2) {
        int dx = p2.x - p1.x;
        if (dx < 0) {
            dx = 0;
        } else if (dx == 0) {
            dx = 1;
        } else {
            dx = 2;
        }

        int dy = p2.y - p1.y;
        if (dy < 0) {
            dy = 0;
        } else if (dy == 0) {
            dy = 1;
        } else {
            dy = 2;
        }
        return (new int[][]{
            new int[]{7, 0, 1},
            new int[]{6, -1, 2},
            new int[]{5, 4, 3}})[dy][dx];
    }


    /**
     * Add an obstacle to the graph.
     *
     * @param p
     */
    public void addObstacle(Point p) {
        map[p.x][p.y] = true;
    }

}
