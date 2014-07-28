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
        double avg_error = avg_error(new Point(0,0));
        System.out.println("Average Difference: " + avg_error);
    }
    
    public static double avg_error(Point origin) {
        Point temp;
        double avg_error = 0;
        int cases = 0;
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                temp = new Point(j, i);
                double distance = Graph.distance(new Point(0, 0), temp);
                double manhattan_distance = Graph.manhattan_distance(new Point(0, 0), temp);
                avg_error += manhattan_distance - distance;
                cases++;
            }
        }
        return (avg_error/cases);
    }
}
