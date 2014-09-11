package pathfinding5;

import util.Point;

/**
 * Created by brentechols on 9/11/14.
 */
public class TestFunc {
    private static final double sqrt2 = 1.412;
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++) {
            Point p1 = new Point((int)(Math.random() * 100),(int)(Math.random() * 100));
            Point p2 = new Point((int)(Math.random() * 100),(int)(Math.random() * 100));

            distance1(p1, p2);
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);

        start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++) {
            Point p1 = new Point((int)(Math.random() * 100),(int)(Math.random() * 100));
            Point p2 = new Point((int)(Math.random() * 100),(int)(Math.random() * 100));

            distance2(p1, p2);
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private static double distance1(Point p1, Point p2) {
        final int dx = Math.abs(p1.x - p2.x);
        final int dy = Math.abs(p1.y - p2.y);
        final int diff = Math.min(dx, dy);
        return diff * sqrt2 + (Math.max(dx, dy) - diff);
    }

    private static double distance2(Point p1, Point p2) {
        final int dx = Math.abs(p1.x - p2.x);
        final int dy = Math.abs(p1.y - p2.y);
        return dx > dy ? (dy * sqrt2 + (dx - dy)) : (dx * sqrt2 + (dy - dx));
    }
}
