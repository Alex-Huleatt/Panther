/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding2;

import java.util.Arrays;
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
        for (int i = 0; i < 9; i+=2) {
            check(start, next(start, i));
            check(start, next(start,i+1));
        }
        closed_set[start.x][start.y] = true;
        int desx = dest.x;
        int desy = dest.y;
        Point current = pq.pop();
        while (current != null && (current.x != desx || current.y != desy)) {
            expand(current);
            current = pq.pop();
        } 

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
            //if (count > 100) System.out.println(current);
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
        evalNeighbors(p);
    }

    private void check(Point parent, Point n) {
        if (!validMove(n)) {
            return;
        }
        final double potential_cost = cost[parent.x][parent.y] + distance(parent, n);
        if (prev[n.x][n.y] == null || cost[n.x][n.y] > potential_cost) {
            prev[n.x][n.y] = parent;
            cost[n.x][n.y] = potential_cost;
            pq.add(n, potential_cost + octile(n, dest) * 2);

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
    public void evalNeighbors(Point p) {

        final int dir = dir(prev[p.x][p.y], p);

        switch (dir & 1) {
            case 0: {
                check(p, next(p, dir));

                if (isObs(moveTo(p, (dir + 2) & 7))) {
                    check(p, next(p, (dir + 1) & 7));
                }

                if (isObs(moveTo(p, (dir + 6) & 7))) {
                    check(p, next(p, (dir + 7) & 7));
                }
            }
            case 1: {
                check(p, next(p, dir));
                check(p, next(p, (dir + 7) & 7));
                check(p, next(p, (dir + 1) & 7));

                if (isObs(moveTo(p, (dir + 3) & 7))) {
                    check(p, next(p, (dir + 2) & 7));
                }

                if (isObs(moveTo(p, (dir + 5) & 7))) {
                    check(p,next(p, (dir + 6) & 7));
                }
            }
        }
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
    public Point next(Point p, int dir) {
        final int dx = dxs(dir);
        final int dy = dys(dir);
        final Point t = new Point(p.x + dx, p.y + dy);
        for (int i = 0; i < 4 && validMove(t); i++) {
            if (isJumpPoint(t, dir) || (t.x == dest.x && t.y == dest.y)) {
                return t;
            }
            t.x += dx;
            t.y += dy;

        }
        return t;
    }

//    public Point diag_next(Point p, int dir) {
//        Point t = moveTo(p, dir);
//        return (validMove(t)) ? t : null;
//    }
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

//                if (next(p, (dir + 1) & 7) != null) {
//                    return true;
//                }
//
//                return (next(p, (dir + 7) & 7) != null);
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
        for (double[] d : cost) {
            Arrays.fill(d,-1);
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

    public static int dxs(int dir) {
        switch (dir) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 1;
            case 3:
                return 1;
            case 4:
                return 0;
            case 5:
                return -1;
            case 6:
                return -1;
            case 7:
                return -1;
            default:
                return -1000000;
        }
    }

    public static int dys(int dir) {
        switch (dir) {
            case 0:
                return -1;
            case 1:
                return -1;
            case 2:
                return 0;
            case 3:
                return 1;
            case 4:
                return 1;
            case 5:
                return 1;
            case 6:
                return 0;
            case 7:
                return -1;
            default:
                return -1000000;
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
