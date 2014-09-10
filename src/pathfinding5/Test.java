/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding5;

import java.util.Arrays;
import util.Point;

/**
 *
 * @author Alex
 */
public class Test {

    public static void main(String[] args) {
        double avg = 0;
        double avg2 = 0;
        int count = 100;
        int obs_count = 3000;
        for (int j = 0; j < count; j++) {
            boolean[][] map = new boolean[100][100];
            for (int i = 0; i < obs_count; i++) {
                int x = (int) (Math.random() * 100);
                int y = (int) (Math.random() * 100);
                if (map[x][y]) {
                    i--;
                } else {
                    map[x][y] = true;
                }
            }
            AStar jp = new AStar(map);
            int n = 10000;
            Point[] starts = new Point[n];
            Point[] finishes = new Point[n];
            for (int i = 0; i < n; i++) {
                int x = (int) (Math.random() * 100);
                int y = (int) (Math.random() * 100);
                int x2 = (int) (Math.random() * 100);
                int y2 = (int) (Math.random() * 100);
                if (!map[x][y] && !map[x2][y2]) {
                    starts[i] = new Point(x, y);
                    finishes[i] = new Point(x2, y2);
                } else {
                    i--;
                }
            }

            long t = System.currentTimeMillis();
            double r = 0.0;
            for (int i = 0; i < n; i++) {
                jp.pathfind(starts[i], finishes[i]);
            }
            avg += ((double) (System.currentTimeMillis() - t)) / n;
            avg2 += r / n;
        }
        System.out.println("Average time to find path: " + avg / count + "ms");
        
    }
    
}
