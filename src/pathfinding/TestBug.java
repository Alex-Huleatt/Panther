/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pathfinding;

import util.Point;

/**
 *
 * @author Alex
 */
public class TestBug {
    Point me;
    boolean[][] map;
    MBugger mb;
    
    public TestBug(Point me, boolean[][] map) {
        this.me = me;
        this.map = map;
        mb = new MBugger(this);
    }
    
    public void step() {
        me = mb.nextMove();
        System.out.println("I MOVE TO: " + me);
    }
    
    public void setGoal(Point p) {
        mb.setStartAndFinish(me, p);
    }
}
