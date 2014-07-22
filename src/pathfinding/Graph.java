/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding;

import java.util.HashMap;
import util.IntDoubleHeap;
import util.MySet;
import util.Point;
import util.Stack;

/**
 *
 * @author Alex
 */
class Graph {

    private final MySet<Point> waypoints;
    private final byte[][] obstacle_map; //0 = empty, 1 = obstacle, 2 = waypoint
    private final int width;
    private final int height;
    private final Point[] updated_obstacles;
    private int updated_index;
    private HashMap<Edge, Double> edges;
    private final Point[] added_waypoints;
    private int waypoints_index;
    public static final double sqrt2 = Math.sqrt(2);
    private int num_obstacles;
    private double[][] adj_mat;
    private HashMap<Point, Integer> point_indices;
    private Point[] waypoint_array;

    public Graph(int width, int height) {
        this.width = width;
        this.height = height;
        this.obstacle_map = new byte[width][height];
        this.waypoints = new MySet<>();
        this.updated_obstacles = new Point[100];
        this.updated_index = 0;
        this.edges = new HashMap<>();
        this.added_waypoints = new Point[100];
        this.waypoints_index = 0;
        this.num_obstacles = 0;
    }

    public boolean isValid(int x, int y) {
        return (x < width && x >= 0 && y < height && y >= 0);
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public void checkAroundObstacle(int x, int y) {
        //check all corners
        //BOOM does it in 4 checks.
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                int tx = x + i;
                int ty = y + j;
                if (isValid(tx, ty)) {
                    //checking outside corners
                    if (obstacle_map[tx][ty] == 0 && isNotObstacleOrInvalid(tx, y) && isNotObstacleOrInvalid(x, ty)) {
                        obstacle_map[tx][ty] = 2;
                        Point newP = new Point(tx, ty);
                        waypoints.add(newP);
                        added_waypoints[waypoints_index++] = newP;
                    } else {
                        //checking inside corners
                        //if the corner is an obstacle, and the two orthongonal adjacent cells are empty
                        if (obstacle_map[tx][ty] == 1 && isNotObstacleOrInvalid(x + i, y) && isNotObstacleOrInvalid(x, y + j)) {
                            obstacle_map[x + i][y] = 2;
                            obstacle_map[x][y + j] = 2;
                            Point newP = new Point(x + i, y);
                            added_waypoints[waypoints_index++] = newP;
                            waypoints.add(newP);
                            Point newP2 = new Point(x, y + j);
                            waypoints.add(newP2);
                            edges.put(new Edge(newP, newP2), sqrt2);
                            added_waypoints[waypoints_index++] = newP2;
                        } else {
                            //check to see if the position is a waypoint and should not be
                            if (isWaypoint(tx, ty) && !shouldBeWaypoint(x, y)) {
                                Point newP = new Point(tx, ty);
                                obstacle_map[tx][ty] = 0;
                                waypoints.remove(newP);
                            }
                        }

                    }
                }
            }
        }
    }

    private boolean shouldBeWaypoint(int x, int y) {
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                if (isObstacle(x + i, y + j)) {
                    return true;
                }    //outside corner
                if (isWaypoint(x + i, y + j) && isObstacle(x + i, y) && isObstacle(x, y + j)) {
                    return true;
                } //inside corner
            }
        }
        return false;

    }

    private boolean isNotObstacleOrInvalid(int x, int y) {
        return (isValid(x, y) && obstacle_map[x][y] != 1);
    }

    private boolean isObstacle(int x, int y) {
        return (isValid(x, y) && obstacle_map[x][y] == 1);
    }

    private boolean isWaypoint(int x, int y) {
        return (isValid(x, y) && obstacle_map[x][y] == 2);
    }

    /**
     * Adds an array of obstacles to the graph, updating the waypoints as it
     * goes. Iterates through the list twice because it limits the number of
     * times waypoints are added and removed from the list. O(n), roughly.
     *
     * @param points
     * @param length
     */
    public void addObstacles(Point[] points, int length) {
        Point p;
        for (int i = 0; i < length; i++) {
            p = points[i];
            obstacle_map[p.x][p.y] = 1;
            updated_obstacles[updated_index++] = p;
            num_obstacles++;
        }
        for (int i = 0; i < length; i++) {
            p = points[i];
            checkAroundObstacle(p.x, p.y);
        }
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
            if (obstacle_map[x1][y1] != 0 && x1 != p1.x) {
                return false;
            }

            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (obstacle_map[x1][y1] != 0 && y1 != p1.y) {
                return false;
            }

        }
        return true;
    }

    private boolean fast_bresenham(Point p1, Point p2, int[][] temp_map) {
        if (temp_map != null && temp_map[p2.x][p2.y] == -1) {
            return false;
        }
        if (temp_map != null && temp_map[p2.x][p2.y] == 1) {
            return true;
        }
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        if (x1 == x2 && y1 == y2) {
            return true;
        }
        while (true) {
            temp_map[x1][y1] = 1;
            int e2 = err << 1;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if ((obstacle_map[x1][y1] != 0 || temp_map[x1][y1] == -1) && x1 != p1.x) {
                return false;
            }
            temp_map[x1][y1] = 1;
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (obstacle_map[x1][y1] != 0 || temp_map[x1][y1] == -1) {
                if (y1 != p1.y) {
                    return false;
                }
            }
        }
        temp_map[x1][y1] = 1;
        return true;
    }

    private boolean fast_isVisible(Point p1, Point p2, int[][] temp_map) {
        return ((fast_bresenham(p1, p2, temp_map) && bresenham(p2, p1)));
    }

    private boolean isVisible(Point p1, Point p2) {
        return (manhattan(p1, p2) < 2 || (Math.abs(p2.x - p1.x) == 1 && Math.abs(p2.y - p1.y) == 1)) || (bresenham(p1, p2));
    }

    public static int manhattan(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public void removeEdges() {
        HashMap<Edge, Double> toReplace = new HashMap<>(edges.size());
        if (edges.isEmpty()) {
            return;
        }
        for (int i = 0; i < updated_index; i++) {
            Point current = updated_obstacles[i];
            int[][] temp_map = new int[width][height];
            for (Edge e : edges.keySet()) {
                if (!isBetween(current, e, temp_map)) {
                    toReplace.put(e, edges.get(e));
                }
            }
        }
        edges = toReplace;
    }

    private boolean isBetween(Point p, Edge e, int[][] temp_map) {
        return fast_isVisible(p, e.p1, temp_map) && fast_isVisible(p, e.p2, temp_map) && potentiallyBetween(p, e.p1, e.p2);
    }

    public void addEdges() {
        for (int i = 0; i < waypoints_index; i++) {
            Point current = added_waypoints[i];
            int[][] temp_map = new int[width][height];
            for (Point p : waypoints) {
                Edge e = new Edge(p, current);
                if (edges.get(e) == null && !p.equals(current) && fast_isVisible(current, p, temp_map)) {
                    edges.put(e, (double) manhattan(p, current));
                }
            }
        }
        waypoints_index = 0;
    }

    private static boolean potentiallyBetween(Point p1, Point p2, Point p3) {
        return Math.min(p2.x, p3.x) < p1.x && Math.max(p2.x, p3.x) > p1.x && Math.min(p2.y, p3.y) < p1.y && Math.max(p2.y, p3.y) > p1.y && minDistanceToLine(p1, p2, p3) < 2;
    }

    /**
     *
     * @param p1
     * @param p2
     * @param p3
     * @return The minimum distance of p1 to the line created by p2 and p3.
     */
    public static double minDistanceToLine(Point p1, Point p2, Point p3) {
        double a = (p3.y - p2.y);
        double b = -1 * (p3.x - p2.x);
        double intercept = a / b * p2.x + p2.y;
        double c = intercept * -b;
        return Math.abs(a * p1.x + b * p2.y + c) / Math.sqrt(a * a + b * b);

    }

    /**
     * A more expensive operation O(n + m). Iterates through all edges and
     * waypoints. Memory-wise, it requires O(n**2).
     *
     * @return
     */
    public void buildMatrix() {
        int num_waypoints = waypoints.size();
        waypoint_array = new Point[num_waypoints];
        HashMap<Point, Integer> waypoint_indices = new HashMap<>();
        double[][] map = new double[num_waypoints][num_waypoints];
        int index = 0;
        for (Point p : waypoints) {
            waypoint_array[index] = p;
            waypoint_indices.put(p, index);
            index++;
        }
        for (Edge e : edges.keySet()) {
            double val = edges.get(e);
            int x = waypoint_indices.get(e.p1);
            int y = waypoint_indices.get(e.p2);
            map[x][y] = val;
            map[y][x] = val;
        }
        point_indices = waypoint_indices;
        adj_mat = map;
    }

    public double[][] getMat() {
        return adj_mat;
    }

    public HashMap<Point, Integer> getIndices() {
        return point_indices;
    }

    public static class Edge {

        Point p1;
        Point p2;

        public Edge(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Edge other = (Edge) obj;
            return ((other.p1.equals(p1) && other.p2.equals(p2)) || (other.p1.equals(p2) && other.p2.equals(p1)));
        }

        public boolean isPartOf(Point p) {
            return p.equals(p1) || p.equals(p2);
        }

        @Override
        public int hashCode() {
            return p1.hashCode() + p2.hashCode();
        }

        @Override
        public String toString() {
            return p1.toString() + "<-->" + p2.toString();
        }
    }

    @Override
    public String toString() {
        String str = "";
        str += "Graph size: " + width + " x " + height;
        str += "\n";
        str += "Number of obstacles: " + num_obstacles + "\n";
        str += "Number of vertices: " + waypoints.size() + "\n";
        str += "Number of edges: " + edges.size() + "\n";
        if (edges.size() > 0) {
            str += "Edges:";
            for (Edge e : edges.keySet()) {
                str += "\n\t" + e.toString();
            }
        }
        str += "\n";
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                str += obstacle_map[j][i] + " ";
            }
            str += "\n";
        }
        return str;
    }

    private static double heuristic(Point p, Point dest) {
        return manhattan(p, dest);
    }

    public Stack<Point> pathfind(Point start, Point dest) {
        IntDoubleHeap toVisit = new IntDoubleHeap(100);
        Stack<Point> path = new Stack<>();
        int[] visited = new int[adj_mat.length + 1];
        double[] costs = new double[adj_mat.length];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = -1;
        }
        //first, find all waypoints that the start can see.
        int[][] temp_map = new int[width][height];

        //base case that the start can see the dest.
        if (fast_isVisible(start, dest, temp_map)) {
            path.push(dest);
            path.push(start);
            return path;
        }

        //find all vertices that the start can see.
        for (int i = 0; i < waypoint_array.length; i++) {
            Point p = waypoint_array[i];
            if (fast_isVisible(start, p, temp_map)) {
                toVisit.add(point_indices.get(p), manhattan(start, p) + heuristic(dest, p));
                visited[i] = -2;
            }
        }

        //repeat for the finish.
        temp_map = new int[width][height]; //reuse variable for dest.
        int dest_index = waypoint_array.length;
        int current;
        while (true) {
            current = toVisit.pop();

            if (current == dest_index) {
                break;
            }
            double[] adj_arr = adj_mat[current];
            for (int i = 0; i < adj_arr.length; i++) {
                if (adj_arr[i] != 0 && visited[i] == -1) {
                    double cost = costs[current] + adj_arr[i] + heuristic(dest, waypoint_array[i]);
                    costs[i] = cost;
                    toVisit.add(i, cost);

                    visited[i] = current;
                }
            }
            if (fast_isVisible(dest, waypoint_array[current], temp_map) && visited[dest_index] == -1) {
                visited[dest_index] = current;
                toVisit.add(dest_index, visited[current] + manhattan(waypoint_array[current], dest));
            }

        }

        current = dest_index;
        path.push(dest);
        current = visited[current];
        while (current != -2) {
            path.push(waypoint_array[current]);
            current = visited[current];
        }
        path.push(start);
        return path;
    }
    
    
}
