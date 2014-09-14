/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pathfinding;

import util.Point;

/**
 * Basic bugging algorithm for cheap pathfinding.
 *
 * @author Alex
 */
public class MBugger {

    private Point start;
    private Point finish;
    private int moveCount;
    private Point closest;
    private boolean reverse;
    
    /**
     * Code in the following range needs to be modified for specific purposes
     *
     */
    ///////////////////////////////////////////////////////////////////////////
    private BuggingUnit tb;

    public MBugger(BuggingUnit tb) {
        this.tb = tb;
        this.closest = null;
        reverse = true;
        moveCount = 0;
    }


    ///////////////////////////////////////////////////////////////////////////////   
    public void setStartAndFinish(Point start, Point finish) {
        this.start = start;
        this.finish = finish;
        closest = start;
    }

    public void reset() {
        closest = null;
        start = null;
        finish = null;
        moveCount = 0;
    }

    private double isOnLine(Point p) {
        double m = ((double) (finish.y - start.y)) / (finish.x - start.x);
        double b = -m * start.x + start.y;
        double ty = m * p.x + b;
        return Math.abs(ty - p.y);
    }

    /**
     * As long as this thing is called, and the map does not change, you are
     * either on the line, or hugging a wall.
     *
     * @return the next position to move to.
     */
    public Point nextMove() {
        moveCount++;
        Point me = tb.currentPosition();
        Point potential;
        if (isOnLine(me) < 2 && (closest == null || Point.manhattan(me, finish) <= Point.manhattan(finish, closest))) {
            //find the next spot that is on the line, return it.
            if ((potential = followLine(me)) != null) {
                return potential;
            }
        }
        //wall hug.
        if ((potential = bug(me)) != null) {
            return potential;
        }
        //if it gets here, there are major problems.
        //just in case, we'll modify the move count.
        moveCount--;
        return null;
    }

    private boolean recursed;

    public Point bug(Point me) {
        Point temp;
        Point obs1;
        Point obs2;
        for (int i = 0; i != 8; i++) {
            int d = (reverse) ? (i + 4) % 8 : i;
            int obs_d = (d + (((reverse) ? -1 : 1) * (2 - d % 2)) + 8) % 8;
            int obs_d2 = (obs_d + ((reverse) ? 1 : -1) + 8) % 8;
            temp = moveTo(me, d);
            obs1 = moveTo(me, obs_d);
            obs2 = moveTo(me, obs_d2);
            if (tb.isTraversable(temp.x, temp.y) && (isObstacle(obs1) || (d % 2 == 0 && isObstacle(obs2)))) {
                recursed = false;
                return temp;
            }

        }
        if (recursed) { //ideally, this can't be called.
            return null;
        }
        recursed = true;
        System.out.println("REVERSE DIRECTION!");
        reverse = !reverse;
        return bug(me);
    }

    public boolean isObstacle(Point p) {
        return !tb.isTraversable(p.x, p.y) && !tb.isOOB(p.x, p.y);
    }

    public Point followLine(Point me) {
        Point potential;
        Point backup = null;
        double dis;
        for (int i = 0; i < 8; i++) {
            potential = moveTo(me, i);
            dis = isOnLine(potential);
            if (dis < 2 && Point.manhattan(finish, potential) < Point.manhattan(me, finish)) {
                if (tb.isTraversable(potential.x, potential.y)) {
                    if ((int) dis != 0) {
                        backup = potential;
                    } else {
                        closest = potential;
                        return potential;
                    }
                }
            }
        }
        return backup;
    }

    private boolean isNextToObstacle(Point p) {
        for (int i = 0; i < 8; i++) {
            Point p2 = moveTo(p, i);
            if (!tb.isTraversable(p2.x, p2.y) && !tb.isOOB(p2.x, p2.y)) {
                return true;
            }
        }
        return false;
    }

    private Point moveTo(Point p, int d) {
        switch (d) {
            case 0:
                return new Point(p.x, p.y - 1);
            case 1:
                return new Point(p.x + 1, p.y - 1);
            case 2:
                return new Point(p.x + 1, p.y);
            case 3:
                return new Point(p.x + 1, p.y + 1);
            case 4:
                return new Point(p.x, p.y + 1);
            case 5:
                return new Point(p.x - 1, p.y + 1);
            case 6:
                return new Point(p.x - 1, p.y);
            case 7:
                return new Point(p.x - 1, p.y - 1);
            default:
                return null;
        }
    }

    public int getMoveCount() {
        return moveCount;
    }

    public double pathRatio() {
        return ((double) moveCount) / Point.manhattan(start, finish);
    }
    
    public void force_reverse() {
        reverse = !reverse;
    }
    
    public Point getClosest() {
        return closest;
    }
}
