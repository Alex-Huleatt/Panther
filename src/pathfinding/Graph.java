/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding;

import java.util.HashMap;
import java.util.HashSet;
import util.Edge;
import util.IntDoubleHeap;
import util.MySet;
import util.Point;

/**
 *
 * @author Alex
 */
public class Graph {

    private final int width;
    private final int height;

    private final byte[][] terrain_map; //0 means empty, 1 means obstacle, 2 means vertex
    private final MySet<Point> vertices; //list of vertices in the graph so far

    private HashSet<Point> removed_vertices; //Vertices that were removed.
    private HashSet<Edge> edges; //All edges in the graph.
    private HashSet<Point> added_vertices; //Vertices that were recently addded.

    private Point[] vertex_array;
    private HashMap<Point, Integer> vertex_indices;

    private double[][] adj_mat;
    public static final double sqrt2 = 1.4142135623; //Constant value.

    public Graph(int width, int height) {
        this.width = width;
        this.height = height;
        this.terrain_map = new byte[width][height];
        this.removed_vertices = new HashSet<>();
        this.added_vertices = new HashSet<>();
        this.adj_mat = new double[0][0];
        this.vertices = new MySet<>();
        this.edges = new HashSet<>();
    }

    /**
     * Add more obstacles to the graph, creating new vertices.
     *
     * @param new_obstacles
     * @param toBreakAt
     */
    public void addObstacles(Point[] new_obstacles, int toBreakAt) {
        int index = 0;
        for (Point p : new_obstacles) {
            if (terrain_map[p.x][p.y] == 2) {
                removeVertex(p);
            }
            terrain_map[p.x][p.y] = 1;
            placeVertices(p);
            index++;
            if (index == toBreakAt) break;
        }
    }

    /**
     * Update the graph, removing all illegal edges.
     */
    public void removeEdges() {
        HashSet<Edge> edges_new = new HashSet<>(edges.size());
        for (Edge e : edges) {
            if (terrain_map[e.p1.x][e.p1.y] == 2 && terrain_map[e.p2.x][e.p2.y] == 2 && bresenham(e.p1, e.p2)) {
                edges_new.add(e);
            }
        }
        edges = edges_new;
    }

    /**
     * Update the graph, adding all new edges.
     */
    public void addEdges() {
        for (Point p : vertices) {
            for (Point p2 : added_vertices) {
                if (bresenham(p2, p) && vertices.contains(p2) && vertices.contains(p)) {
                    Edge e = new Edge(p, p2);
                    edges.add(new Edge(p, p2));
                }
            }

            for (Point p2 : removed_vertices) {
                if (bresenham(p, p2)) {
                    Point cast = rayCast(p, p2);
                    if (cast != null && terrain_map[cast.x][cast.y] == 2) {
                        Edge e = new Edge(cast, p);
                        edges.add(e);
                    }
                }
            }
        }
        removed_vertices = new HashSet<>();
        added_vertices = new HashSet<>();
    }

    /**
     * Convert the graph into a usable adjacency matrix, storing it in adj_mat.
     */
    public void buildMatrix() {
        int num_waypoints = vertices.size();
        vertex_array = new Point[num_waypoints];
        HashMap<Point, Integer> waypoint_indices = new HashMap<>();
        double[][] map = new double[num_waypoints][num_waypoints];
        int index = 0;
        for (Point p : vertices) {
            vertex_array[index] = p;
            waypoint_indices.put(p, index);
            index++;
        }
        for (Edge e : edges) {
            double val = distance(e.p1, e.p2);
            int x = waypoint_indices.get(e.p1);
            int y = waypoint_indices.get(e.p2);
            map[x][y] = val;
            map[y][x] = val;
        }
        vertex_indices = waypoint_indices;
        adj_mat = map;
    }

    /**
     * Assuming adj_mat is already built, find a path between two points.
     *
     * @param start starting position.
     * @param dest ending position.
     * @return Point[] of positions to visit, null if no path exists.
     */
    public Point[] pathfind(Point start, Point dest) {
        IntDoubleHeap toVisit = new IntDoubleHeap(100);
        Point[] path = new Point[100];
        int[] visited = new int[adj_mat.length + 1];
        double[] costs = new double[adj_mat.length];
        for (int i = 0; i < visited.length; i++) {
            visited[i] = -1;
        }
        //first, find all waypoints that the start can see.
        int[][] temp_map = new int[width][height];

        //base case that the start can see the dest.
        if (bresenham(start, dest)) {
            return new Point[]{dest, start};
        }

        //find all vertices that the start can see.
        for (int i = 0; i < vertex_array.length; i++) {
            Point p = vertex_array[i];
            if (bresenham(start, p)) {
                toVisit.add(vertex_indices.get(p), distance(start, p) + heuristic(dest, p));
                visited[i] = -2;
            }
        }

        //repeat for the finish.
        temp_map = new int[width][height]; //reuse variable for dest.
        int dest_index = vertex_array.length;
        int current;
        while (true) {
            if (toVisit.isEmpty()) {
                return null;
            }
            current = toVisit.pop();

            if (current == dest_index) {
                break;
            }
            double[] adj_arr = adj_mat[current];
            for (int i = 0; i < adj_arr.length; i++) {
                if (adj_arr[i] != 0 && visited[i] == -1) {
                    double cost = costs[current] + adj_arr[i] + heuristic(dest, vertex_array[i]);
                    costs[i] = cost;
                    toVisit.add(i, cost);

                    visited[i] = current;
                }
            }
            if (bresenham(dest, vertex_array[current]) && visited[dest_index] == -1) {
                visited[dest_index] = current;
                toVisit.add(dest_index, costs[current] + visited[current] + distance(vertex_array[current], dest));
            }

        }

        current = dest_index;
        path[0] = dest;
        current = visited[current];
        int index = 1;
        while (current != -2) {
            path[index++] = vertex_array[current];
            current = visited[current];
        }
        path[index++] = start;
        Point[] final_path = new Point[index];
        System.arraycopy(path, 0, final_path, 0, index);
        return final_path;
    }

    private boolean isValid(int x, int y) {
        return x < width && x >= 0 && y < height && y >= 0;
    }

    private void placeVertices(Point p) {
        int x = p.x;
        int y = p.y;
        int tx;
        int ty;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                tx = x + i;
                ty = y + j;
                Point temp = new Point(tx, ty);
                if (shouldBeWaypoint(tx,ty)) {
                    if (terrain_map[tx][ty] == 0) {
                        addVertex(temp);
                    }
                } else if (terrain_map[tx][ty] == 2) {
                    removeVertex(temp);
                }
            }
        }
    }

    private boolean shouldBeWaypoint(int x, int y) {
        if (terrain_map[x][y] == 1) return false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 && j != 0 && isValid(x + i, y + j) && terrain_map[x + i][y + j] == 1 && terrain_map[x+i][y] != 1 && terrain_map[x][y+j] != 1) {
                    return true;
                }
                else if (i != 0 || j != 0) {
                    if (terrain_map[x][y+j] == 1 && terrain_map[x+i][y] == 1 && terrain_map[x+i][y+j] == 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addVertex(Point p) {
        terrain_map[p.x][p.y] = 2;
        added_vertices.add(p);
        vertices.add(p);
    }

    private void removeVertex(Point p) {
        if (terrain_map[p.x][p.y] == 2) {
            terrain_map[p.x][p.y] = 0;
        }
        removed_vertices.add(p);
        vertices.remove(p);
        if (added_vertices.contains(p)) {
            added_vertices.remove(p);
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
            if (terrain_map[x1][y1] != 0 && x1 != p1.x) {
                return false;
            }

            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (terrain_map[x1][y1] != 0 && y1 != p1.y) {
                return false;
            }

        }
        return true;
    }

    private Point rayCast(Point p1, Point p2) {
        int dx = Math.abs(p2.x - p1.x);
        int dy = Math.abs(p2.y - p1.y);
        int sx = (p1.x < p2.x) ? 1 : -1;
        int sy = (p1.y < p2.y) ? 1 : -1;
        return rayCast_help(p2, dx, dy, sx, sy);
    }

    private Point rayCast_help(Point p, int dx, int dy, int sx, int sy) {
        int x1 = p.x;
        int y1 = p.y;
        int err = dx - dy;
        while (true) {
            int e2 = err << 1;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (!isValid(x1, y1)) {
                break;
            }
            if (terrain_map[x1][y1] != 0 && x1 != p.x) {
                return new Point(x1, y1);
            }

            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
            if (!isValid(x1, y1)) {
                break;
            }
            if (terrain_map[x1][y1] != 0 && y1 != p.y) {
                return new Point(x1, y1);
            }

        }
        return null;
    }

    public static double distance(Point p1, Point p2) {
        int dx = Math.abs(p1.x - p2.x);
        int dy = Math.abs(p1.y - p2.y);
        int diff = Math.min(dx, dy);
        return diff * Graph.sqrt2 + (Math.max(dx, dy) - diff);
    }

    private double heuristic(Point p1, Point p2) {
        return distance(p1, p2) * .9;
    }

    public Point[] getVertices() {
        Point[] v = new Point[vertices.size()];
        int i = 0;
        for (Point p : vertices) {
            v[i++] = p;
        }
        return v;
    }

    public Edge[] getEdges() {
        return edges.toArray(new Edge[0]);
    }
}
