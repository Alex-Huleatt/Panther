/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding3;

import java.util.Arrays;
import util.Point;
import util.PointDoubleHeap;

/**
 *
 * @author Alex
 */
public class AStar {

    private final boolean[][] map;

    private static final double sqrt2 = 1.414;
    private final PointDoubleHeap pq;
    private final Point[][] prev;
    private final double[][] cost;
    private final boolean[][] closed_set;

    private Point dest;
    private static final double octile_constant = sqrt2 - 1;
    public static final int MAX_PATH_LENGTH = 75;

    public AStar(boolean[][] map) {
        this.map = map;
        this.prev = new Point[map.length][map[0].length];
        this.closed_set = new boolean[map.length][map[0].length];
        this.cost = new double[map.length][map[0].length];
        this.pq = new PointDoubleHeap(3000);
    }

    public Point[] pathfind(Point start, Point finish) {
        init();
        Point current = finish;
        dest = start;
        int desx = dest.x;
        int desy = dest.y;
        do {
            expand(current);
            current = pq.pop();
        } while (current != null && (current.x != desx || current.y != desy));
        if (current == null) {
            return null;
        }
        return reconstruct();
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

        pq.clear();
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
        System.arraycopy(path_temp,0,path,0,count);
        return path;
    }

    private void expand(Point p) {
        if (closed_set[p.x][p.y]) {
            return;
        }
        closed_set[p.x][p.y] = true;
        if (prev[p.x][p.y] == null) {
            for (int i = 0; i < 8; i++) {
                Point n = moveTo(p, i);
                if (validMove(n)) {
                    prev[n.x][n.y] = p;
                    cost[n.x][n.y] = distance(p, n);
                    pq.add(n, distance(p, n) + octile(n, dest) * 1.3);
                }
            }
        } else {
            final int dir = dir(prev[p.x][p.y], p);
            switch (dir % 2) {
                case 1:
                    for (int i = 6; i <= 10; i++) {
                        check(p, (dir + i) & 7);
                    }
                    return;
                case 0: {
                    for (int i = 7; i <= 9; i++) {
                        check(p, (dir + i) & 7);
                    }
                }
            }
        }
    }

    private void check(Point parent, int dir) {
        final Point n = moveTo(parent, dir);
        final double potential_cost = cost[parent.x][parent.y] + distance(parent, n);
        if (validMove(n) && !closed_set[n.x][n.y]) {
            if (prev[n.x][n.y] == null || cost[n.x][n.y] > potential_cost) {
                pq.add(n, potential_cost + octile(n, dest) * 1.3);
                prev[n.x][n.y] = parent;
                cost[n.x][n.y] = potential_cost;
            }
        }
    }

    /**
     * Get the practical distance between 2 points
     *
     * @param p1
     * @param p2
     * @return
     */
    public static double distance(Point p1, Point p2) {
        final int dx = Math.abs(p1.x - p2.x);
        final int dy = Math.abs(p1.y - p2.y);
        final int diff = Math.min(dx, dy);
        return diff * sqrt2 + (Math.max(dx, dy) - diff);
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

    public boolean validMove(Point p) {
        return valid(p) && !map[p.x][p.y];
    }

    public boolean valid(Point p) {
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

    public void addObstacle(Point p) {
        map[p.x][p.y] = true;
    }
}
