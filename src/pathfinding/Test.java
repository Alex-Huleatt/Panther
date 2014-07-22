/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding;

import java.util.Arrays;
import util.Point;

/**
 *
 * @author Alex
 */
public class Test {

    public static void main(String[] args) {
        boolean[][] map = new boolean[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                map[i][j] = true;
            }
        }
        map[3][1] = false;
        map[3][2] = false;
        map[3][3] = false;
        map[0][3] = false;
        map[1][3] = false;
        map[2][3] = false;
        //map[3][0] = false;
        for (int i = 0; i < 10; i++) {
            System.out.print("|");
            for (int j = 0; j < 10; j++) {
                if (j == 0 && i == 0) {
                    System.out.print("s");
                } else {
                    if (j == 9 && i == 9) {
                        System.out.print("f");
                    } else {
                        System.out.print((map[j][i]) ? " " : 0);
                    }
                }
            }
            System.out.println("|");
        }
        TestBug tb = new TestBug(new Point(0, 0), map);
        tb.setGoal(new Point(9, 9));
        while (!tb.me.equals(new Point(9, 9))) {
            tb.step();
        }
        System.out.println(tb.mb.pathRatio());
    }
}
