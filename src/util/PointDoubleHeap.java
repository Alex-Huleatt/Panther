/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 * A quick and dirty array-based heap implementation, designed for use as a
 * priority queue. 
 *
 * @author alexhuleatt
 */
public class PointDoubleHeap {

    private final int capacity;
    private int index;
    private final Point[] points;
    private final double[] costs;
    
    public PointDoubleHeap(int capacity) {
        this.capacity = capacity;
        points = new Point[capacity];
        costs = new double[capacity];
        index = 0;
    }

    @Override
    public String toString() {
        String str = "[";
        for (int i = 0; i < index-1; i++) {
            str += points[i] + ":" + costs[i]  + " ";
        }
        if (index > 0) str += points[index-1] + ":" + costs[index-1];
        str += "]";
        return str;
    }
    

    public void add(Point p, double cost) {
        int pos = index++;
        for (; pos > 0 && cost < costs[pos/2]; pos /= 2) {
           costs[pos] = costs[pos/2];
           points[pos] = points[pos/2];
        }
        costs[pos] = cost;
        points[pos] = p;
    }

    public Point pop() {
        if (index == 0) {
            return null;
        }
        final Point min_point = points[0];
        index--;
        if (index == 0) {
            return min_point;
        }
        points[0] = points[index];
        costs[0] = costs[index];

        int r = 0;
        int child = r * 2 + 1;
        while (child < index) {
            if (costs[child] > costs[child+1]) {
                child++;
            }
            if (costs[child] < costs[r]) {
                swap(r, child);
                r = child;
                child *= 2;
            } else return min_point;
            child++;
        }
        return min_point;
    }

    public boolean isEmpty() {
        return (index == 0);
    }
    
    private void swap(int a, int b) {
        Point temp_pair = points[a];
        double temp_cost = costs[a];

        points[a] = points[b];
        points[b] = temp_pair;
        
        costs[a] = costs[b];
        costs[b] = temp_cost;
    }
    
    public void clear() {
        index = 0;
    }
}
