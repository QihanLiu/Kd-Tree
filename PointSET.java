import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Stack;

public class PointSET {
    private SET<Point2D> points;
    
    public PointSET() { // construct an empty set of points 
        points = new SET<Point2D>();
    }
    public boolean isEmpty() { // is the set empty? 
        return points.isEmpty();
    }
    public int size() { // number of points in the set 
        return points.size();
    }
    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        checkNull(p, "Insert point is null!");
        points.add(p);
    }
    public boolean contains(Point2D p) { // does the set contain point p? 
        checkNull(p, "Cant contain null points!");
        return points.contains(p);
    }            
    public void draw() {  // draw all points to standard draw
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D point : points) {
            StdDraw.point(point.x(), point.y());
        }
    }
    public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
        checkNull(rect, "Retangle is null!");
        Stack<Point2D> boundedps = new Stack<Point2D>();
        for (Point2D point : points) {
            if (rect.contains(point)) {
                boundedps.push(point);
            }
        }
        return boundedps;
    }
    public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty 
        checkNull(p, "Cant find points near null!");
        Point2D nearestPoint = null;
        for (Point2D point : points) {
            if (nearestPoint == null || point.distanceSquaredTo(p) < nearestPoint.distanceSquaredTo(p)) {
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }
    private static void checkNull(Object any, String message) {
        if (any == null) throw new IllegalArgumentException(message);
    }
    
    public static void main(String[] args) {
        // create initial board from file
        In in = new In("example.txt");
        int n = in.readInt();
        double x, y;
        
        PointSET points = new PointSET();
        for (int i = 0; i < n; i++) {
            x = in.readDouble();
            y = in.readDouble();
            Point2D point = new Point2D(x, y);
            points.insert(point);
        }
        StdOut.println("Size is " + points.size());
        points.draw();
    }
}