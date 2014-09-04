/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Alex
 */
public class Edge {

    public Point p1;
    public Point p2;

    public Edge(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        return ((other.p1.equals(p1) && other.p2.equals(p2)) || (other.p1.equals(p2) && other.p2.equals(p1)));
    }

    public boolean isPartOf(Point p) {
        return p.equals(p1) || p.equals(p2);
    }

    @Override
    public int hashCode() {
        return p1.hashCode() + p2.hashCode();
    }

    @Override
    public String toString() {
        return p1.toString() + "<-->" + p2.toString();
    }
}

