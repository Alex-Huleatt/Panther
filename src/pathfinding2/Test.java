/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding2;

import util.Point;

/**
 *
 * @author Alex
 */
public class Test {

    public static void main(String[] args) {
        double avg = 0;
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
            JumpPoint jp = new JumpPoint(map);
            int n = 100000;
            long t = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                int x = (int) (Math.random() * 100);
                int y = (int) (Math.random() * 100);
                int x2 = (int) (Math.random() * 100);
                int y2 = (int) (Math.random() * 100);
                if (!map[x][y] && !map[x2][y2]) {
                    jp.pathfind(new Point(x, y), new Point(x2, y2));
                } else {
                    i--;
                }
            }
            avg += ((double) (System.currentTimeMillis() - t)) / n;
        }
        System.out.println("Average time to find path: " + avg / count);
    }
}
