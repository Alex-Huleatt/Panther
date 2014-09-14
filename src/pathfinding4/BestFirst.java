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

    private final boolean[][] map;

    private static final float sqrt2 = 1.414f;
    private final Point[][] prev;

    private final Point[] q;
    private final float[] costs;
    private int index;

    private Point dest;
    public static final int MAX_PATH_LENGTH = 100;

    public BestFirst(boolean[][] map) {
        this.map = map;
        this.prev = new Point[map.length][map[0].length];
        this.q = new Point[5000];
        this.costs = new float[5000];
    }

    public Point[] pathfind(Point start, Point finish) {
        init();
        index = 0;
        Point current;
        dest = start;
        for (int i = 7; i != -1; i--) {
            check(finish, i);
        }
        final int desx = dest.x;
        final int desy = dest.y;
        while (index != 0) {
            current = q[--index];
            if (current.x == desx && current.y == desy) {
                return reconstruct();
            }
            expand(current);
        }
        return null;
    }

    /**
     * Initialize class variables for use in pathfinding
     */
    public void init() {
        for (Point[] p : prev) {
            Arrays.fill(p, null);
        }
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
        if ((dir & 1) == 1) {
            check(p, (dir + 6) & 7);
            check(p, (dir + 2) & 7);
        }
        check(p, (dir + 7) & 7);
        check(p, (dir + 1) & 7);
        check(p, dir & 7);
    }

    private void check(Point parent, int dir) {
        final Point n = moveTo(parent, dir);
        if (validMove(n) && prev[n.x][n.y] == null) {
            add(n, distance(n, dest));
            prev[n.x][n.y] = parent;
        }

    }

    private static int abs(int x) {
        final int m = x >> 31;
        return x + m ^ m;
    }

    private static float distance(Point p1, Point p2) {
        final float dx = abs(p1.x - p2.x);
        final float dy = abs(p1.y - p2.y);
        return dx > dy ? (dy * sqrt2 + (dx - dy)) : (dx * sqrt2 + (dy - dx));
    }

    /**
     * Moves a point one cell along direction d.
     *
     * @param p
     * @param d
     * @return
     */
    private static Point moveTo(Point p, int d) {
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
            default:
                return new Point(p.x - 1, p.y - 1);
        }
    }

    private boolean validMove(Point p) {
        return (p.x >= 0 && p.x < map.length && p.y >= 0 && p.y < map[0].length && !map[p.x][p.y]);
    }

    /**
     * returns the direction from p1 to p2
     *
     * @param p1
     * @param p2
     * @return
     */
    private int dir(Point p1, Point p2) {
        final int dy = p2.y - p1.y;
        switch (p2.x - p1.x) {
            case -1: {
                switch (dy) {
                    case -1:
                        return 7;
                    case 0:
                        return 6;
                    default:
                        return 5;
                }
            }
            case 0: {
                switch (dy) {
                    case -1:
                        return 0;
                    case 0:
                        return -1;
                    default:
                        return 4;
                }
            }
            default:
                switch (dy) {
                    case -1:
                        return 1;
                    case 0:
                        return 2;
                    default:
                        return 3;
                }
        }
    }

    public void addObstacle(Point p) {
        map[p.x][p.y] = true;
    }

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

    private void add(Point p, float c) {
        int i = index;
        for (; i != 0 && c > costs[i - 1]; i--) {
            costs[i] = costs[i - 1];
            q[i] = q[i - 1];
        }
        costs[i] = c;
        q[i] = p;
        index++;
    }
}
