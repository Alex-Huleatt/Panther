/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pathfinding6;


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
            Greed jp = new Greed(map);
            int n = 10000;
            Point[] starts = new Point[n];
            Point[] finishes = new Point[n];
            for (int i = 0; i < n; i++) {
                short x = (short) (Math.random() * 100);
                short y = (short) (Math.random() * 100);
                short x2 = (short) (Math.random() * 100);
                short y2 = (short) (Math.random() * 100);
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
