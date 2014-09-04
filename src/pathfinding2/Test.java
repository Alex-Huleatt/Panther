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
        boolean[][] map = new boolean[100][100];
        for (int i = 0; i < 1000; i++) {
            int x = (int) (Math.random() * 100);
            int y = (int) (Math.random() * 100);
            map[x][y] = true;
        }
        JumpPoint jp = new JumpPoint(map);
        for (int i = 0; i < 10000; i++) {
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
        System.out.println(jp.c1 + " " + jp.c2);
    }
}
