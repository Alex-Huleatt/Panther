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
public interface BuggingUnit {
    
    public Point currentPosition();
    public boolean isTraversable(int x, int y);
    public boolean isOOB(int x, int y);
    
}
