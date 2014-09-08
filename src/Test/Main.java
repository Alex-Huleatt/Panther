/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test;

import util.Point;

/**
 *
 * @author Alex
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println(Point.deserialize((new Point(5,7).serialize())));
    }
}
