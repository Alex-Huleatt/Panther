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

    private final Point[][][] nexts;

    private boolean redone;

    public JumpPoint(boolean[][] map) {
        this.map = map;
        this.nexts = new Point[map.length][map[0].length][8];
        this.prev = new Point[map.length][map[0].length];
        this.cost = new double[map.length][map[0].length];
        this.closed_set = new boolean[map.length][map[0].length];
    }

    public Point[] pathfind(Point start, Point dest) {
        init();
        this.dest = dest;
        Point current = start;
        int desx = dest.x;
        int desy = dest.y;
        do {
            expand(current);
        } while ((current = pq.pop()) != null && (current.x != desx || current.y != desy));

        return reconstruct();
    }

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

    public static double distance(Point p1, Point p2) {
        int dx = Math.abs(p1.x - p2.x);
        int dy = Math.abs(p1.y - p2.y);
        int diff = Math.min(dx, dy);
        return diff * sqrt2 + (Math.max(dx, dy) - diff);
    }
    int c1 = 0;
    int c2 = 0;

    public void expand(Point p) {
        if (closed_set[p.x][p.y]) {
            return;
        }
        closed_set[p.x][p.y] = true;

        double myCost = cost[p.x][p.y];

        final Point[] neighbors = getNeighbors(p);
        double potential_cost;
        double current_cost;
        for (Point n : neighbors) {
            if (n != null && !n.equals(null_point)) {
                potential_cost = myCost + distance(n, p);
                current_cost = cost[n.x][n.y];
                if (prev[n.x][n.y] == null || (prev[n.x][n.y] != null && potential_cost < current_cost)) {
                    cost[n.x][n.y] = potential_cost;
                    prev[n.x][n.y] = p;
                    pq.add(n, potential_cost + octile(n, dest));
                }
            }
        }
    }

    private double octile(Point p1, Point p2) {
        int x = Math.abs(p2.x - p1.x);
        int y = Math.abs(p2.y - p1.y);
        return (Math.max(x, y) + octile_constant * Math.min(x, y));
    }

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

    public Point[] originN(Point p) {
        final Point[] neighbors = new Point[8];
        Point n;
        //special case, start node, expand in every direction
        for (int i = 0; i < 8; i++) {
            n = next(p, i);
            if (validMove(n)) {
                neighbors[i] = n;
            }
        }
        return neighbors;
    }

    public Point[] orthogN(Point p, int dir) {
        final Point[] neighbors = new Point[3];
        int count = 1;

        neighbors[0] = next(p, dir);

        if (isObs(moveTo(p, (dir + 2) & 7))) {
            neighbors[count++] = next(p, (dir + 1) & 7);
        }
        if (isObs(moveTo(p, (dir + 6) & 7))) {
            neighbors[count++] = next(p, (dir + 7) & 7);
        }
        return neighbors;
    }

    public Point[] diagN(Point p, int dir) {
        final Point[] neighbors = new Point[5];
        int count = 3;
        neighbors[0] = next(p, dir);

        neighbors[1] = next(p, (dir + 7) & 7);

        neighbors[2] = next(p, (dir + 1) & 7);

        if (isObs(moveTo(p, (dir + 3) & 7))) {
            neighbors[count++] = next(p, (dir + 2) & 7);
        }

        if (isObs(moveTo(p, (dir + 5) & 7))) {
            neighbors[count++] = next(p, (dir + 6) & 7);
        }

        return neighbors;
    }

    public boolean validMove(Point p) {
        return (p != null && !p.equals(null_point) && isValid(p.x, p.y) && !map[p.x][p.y]);
    }

    public Point next(Point p, int dir) {
        redone = false;
        if (!isValid(p.x, p.y)) {
            return null;
        }
        Point next = nexts[p.x][p.y][dir];
        if (next != null) {
            return next;
        }
        Point old = p;
        Point temp = moveTo(p, dir);
        int dx = temp.x - p.x;
        int dy = temp.y - p.y;
        while (validMove(temp)) {
            if (isJumpPoint(temp, dir) || isGoal(temp)) {
                nexts[old.x][old.y][dir] = temp;
                return temp;
            }
            temp.x += dx;
            temp.y += dy;
        }
        nexts[old.x][old.y][dir] = null_point;
        return null;
    }

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

                if (next(p, (dir + 1) & 7) != null) {
                    return true;
                }

                return (next(p, (dir + 7) & 7)!= null);
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

    public void init() {
        for (Point[] p : prev) {
            Arrays.fill(p, null);
        }
        for (boolean[] b : closed_set) {
            Arrays.fill(b, false);
        }

        pq = new PointDoubleHeap(1000);

    }

    public boolean between(Point p1, Point p2) {
        int x = (p1.x - p2.x) / 2 + p2.x;
        int y = (p1.y - p2.y) / 2 + p2.y;
        return map[x][y];
    }

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

    private void initNexts() {
        for (Point[][] arr2 : nexts) {
            for (Point[] arr : arr2) {
                Arrays.fill(arr, null);
            }
        }
        redone = true;
    }

    public void addObstacle(Point p) {
        map[p.x][p.y] = true;
        if (!redone) {
            initNexts();
        }
    }

}
