/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding4;

import java.util.Arrays;
import util.Point;
import util.PointDoubleHeap;

/**
 *
 * @author Alex
 */
public class suboptimal {

    private final boolean[][] map;

    private static final double sqrt2 = 1.414;
    private final Point[][] prev;

    public final Point[] q;
    public final double[] costs;
    int index;

    private Point dest;
    public static final int MAX_PATH_LENGTH = 100;

    public suboptimal(boolean[][] map) {
        this.map = map;
        this.prev = new Point[map.length][map[0].length];
        this.q = new Point[1000];
        this.costs = new double[1000];
    }

    public void clear() {
        for (Point[] p : prev) {
            Arrays.fill(p, null);
        }
    }

    public Point[] pathfind(Point start, Point finish) {
        clear();
        index = 0;
        Point current;
        dest = start;
        for (int i = 0; i < 8; i++) {
            check(finish, i);
        }
        final int desx = dest.x;
        final int desy = dest.y;
        if (index != 0) {
            while (true) {
                current = q[--index];
                if (current.x == desx && current.y == desy) {
                    break;
                }
                expand(current);
                if (index == 0) {
                    break;
                }
            }
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
            case 1: {
                check(p, (dir + 6) & 7);
                check(p, (dir + 7) & 7);
                check(p, (dir + 8) & 7);
                check(p, (dir + 9) & 7);
                check(p, (dir + 10) & 7);
                return;
            }
            case 0: {
                check(p, (dir + 7) & 7);
                check(p, (dir + 8) & 7);
                check(p, (dir + 9) & 7);
            }
        }
    }

    private void check(Point parent, int dir) {
        final Point n = moveTo(parent, dir);
        if (validMove(n) && prev[n.x][n.y] == null) {
            prev[n.x][n.y] = parent;
            add(n, distance(n, dest));
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

    public void add(Point p, double c) {
        int i = index;
        for (; i > 0 && c > costs[i - 1]; i--) {
            costs[i] = costs[i - 1];
            q[i] = q[i - 1];
        }
        costs[i] = c;
        q[i] = p;
        index++;
    }

    public Point[] smooth(Point[] p) {
        int length = p.length;
        Point[] path = new Point[0];
        while (path.length != length) {
            int count = p.length;
            int dex = 1;
            int diff = 2;
            for (int i = 0; i < p.length;) {
                if (i + diff >= p.length) {
                    break;
                }
                if (bresenham(p[i], p[i + diff])) {
                    p[i + diff - 1] = null;
                    count--;
                    diff++;
                } else {
                    i += diff - 1;
                    diff = 2;
                }
            }
            path = new Point[count];
            dex = 0;
            for (int i = 0; i < p.length; i++) {
                if (p[i] != null) {
                    path[dex++] = p[i];
                }
            }
        }

        return path;
    }

    public Point[] smooth2(Point[] path) {
        int count = path.length;
        for (int i = 0; i < path.length; i++) {
            for (int j = path.length-1; j > i; j--) {
                if (bresenham(path[i],path[j])) {
                    for (int k = i+1; k < j; k++) {
                        path[k] = null;
                        
                        count--;
                    }
                    i = j-1;
                    break;
                }
            }
        }
        Point[] p = new Point[count];
        int c = 0;
        for (int i = 0; i < path.length; i++) {
            if(path[i]!=null) p[c++] = path[i];
        }
        return p;
    }

    private boolean bresenham(Point p1, Point p2) {
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        while (true) {
            int e2 = err << 1;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (map[x1][y1]) {
                return false;
            }

            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (map[x1][y1]) {
                return false;
            }

        }
        return true;
    }

}
