import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.Stack;

public class KdTree {
    private Node root;
    private int size;
    
    public KdTree() {
        root = null;
        size = 0;
    }
    
    private static class Node { // node of points
        private Point2D p;
        private Node left = null;
        private Node right = null;
        private boolean isVertical = true; // direction indicator
        
        public Node(Point2D p) {
            this.p = p;
        }
    }
    
    public boolean isEmpty() { // is the set empty? 
        return size == 0;
    }

    public int size() { // number of points in the set 
        return size;
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        checkNull(p, "Insert point is null!");
        if (!contains(root, p)) {
            root  = insert(root, p);
        }
    }
    
    private Node insert(Node node, Point2D thatp) { // insert to the tree
        if (node == null) {
            size++;
            return new Node(thatp);
        }
        double cmp;
        if (node.isVertical) {
            cmp = thatp.x() - node.p.x();
        }
        else {
            cmp = thatp.y() - node.p.y();
        }
        if (cmp < 0) {
            node.left = insert(node.left, thatp);
            node.left.isVertical = !node.isVertical;
        }
        else { // points sits on the line go to right node.
            node.right = insert(node.right, thatp);
            node.right.isVertical = !node.isVertical;
        }
        return node;
    }
    
    public boolean contains(Point2D p) { // does the set contain point p? 
        checkNull(p, "Cant contain null points!");
        return contains(root, p);
    }
    
    private boolean contains(Node nodeToSearch, Point2D thatp) {
        if (nodeToSearch == null) {
            return false;
        }
        
        double cmp;
        if (nodeToSearch.isVertical) {
            cmp = thatp.x() - nodeToSearch.p.x();
        }
        else {
            cmp = thatp.y() - nodeToSearch.p.y();
        }

        if (cmp < 0) { // go to the left node
            return contains(nodeToSearch.left, thatp);
        }
        else if (cmp > 0) {
            return contains(nodeToSearch.right, thatp);
        }
        else { // sits on the line, could be this node or the right node.
            if (nodeToSearch.p.compareTo(thatp) == 0) { // if current node is the one
                return true;
            }
            if (nodeToSearch.right != null) { 
                if (nodeToSearch.right.p.compareTo(thatp) == 0) { // if right node is the one
                    return true;
                }
                return contains(nodeToSearch.right, thatp); // continue searching cuz it might sit on the line
            }
            return false;
        }
    }
    
    public void draw() {  // draw all points to standard draw 
        RectHV canvas = new RectHV(0, 0, 1, 1); // canvas is the node boundary
        draw(root, canvas);
    }
    
    private void draw(Node nodeToDraw, RectHV canvas) {  // draw all points to standard draw 
        if (nodeToDraw == null) return;
        
        StdDraw.setPenRadius(0.002);
        if (nodeToDraw.isVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(nodeToDraw.p.x(), canvas.ymin(), nodeToDraw.p.x(), canvas.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(canvas.xmin(), nodeToDraw.p.y(), canvas.xmax(), nodeToDraw.p.y());
        }
        StdDraw.setPenRadius(0.015);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.point(nodeToDraw.p.x(), nodeToDraw.p.y());
        
        draw(nodeToDraw.left, updatecanvas(canvas, nodeToDraw, 0));
        draw(nodeToDraw.right, updatecanvas(canvas, nodeToDraw, 1));
    }
    
    public Iterable<Point2D> range(RectHV rect) { // return all points that are inside the rectangle (or on the boundary)
        checkNull(rect, "Retangle is null!");
        Stack<Point2D> boundedps = new Stack<Point2D>();
        range(root, rect, boundedps);
        return boundedps;
    }
    
    private void range(Node node, RectHV rect, Stack<Point2D> boundedps) { // return all points that are inside the rectangle (or on the boundary)
        if (node == null) {
            return;
        }
        double pVal, recMinVal, recMaxVal;
        if (node.isVertical) { // set compare values
            pVal = node.p.x();
            recMinVal = rect.xmin();
            recMaxVal = rect.xmax();
        }
        else {
            pVal = node.p.y();
            recMinVal = rect.ymin();
            recMaxVal = rect.ymax();   
        }
        
        if (pVal > recMaxVal) { // if the value is bigger than the rect side, go left
            range(node.left, rect, boundedps);
        }
        else if (pVal < recMinVal) { // if the value is smaller than the rect side, go left
            range(node.right, rect, boundedps);
        }
        else {
            range(node.left, rect, boundedps); // if the cutting line is within the rect, search both sides.
            range(node.right, rect, boundedps);
            if (rect.contains(node.p)) { 
                boundedps.push(node.p);
            }
        }
        
    }
    
    public Point2D nearest(Point2D querypoint) { // a nearest neighbor in the set to point p; null if the set is empty 
        checkNull(querypoint, "Cant find points near null!");
        if (root == null) {
            return null;
        }
        Point2D nearestPoint = root.p;
        RectHV canvas = new RectHV(0, 0, 1, 1); // the canvas of current node
        nearestPoint = nearest(root, querypoint, nearestPoint, canvas);
        return nearestPoint;
    }
    
    private Point2D nearest(Node node, Point2D querypoint, Point2D nearestPoint, RectHV canvas) {
        if (node == null) {
            return nearestPoint;
        }
        double nearestdist = nearestPoint.distanceSquaredTo(querypoint), currentdist = querypoint.distanceSquaredTo(node.p), distToRect = canvas.distanceSquaredTo(querypoint);
        if (distToRect != 0 && distToRect > nearestdist) { // if the current node is outside of the canvas and further, no need to search.
            return nearestPoint;
        }
        
        if (currentdist < nearestdist) { // update the nearest point if current node is closer
            nearestPoint = node.p;
            nearestdist = currentdist;
        }
        
        if (node.isVertical) { // calculate distance to the cutting line.
            distToRect = querypoint.x() - node.p.x(); // negtive is on the left node
        }
        else {
            distToRect = querypoint.y() - node.p.y();
        }
        
        if (distToRect < 0) { // if query point is on the left/bot
            nearestPoint = nearest(node.left, querypoint, nearestPoint, updatecanvas(canvas, node, 0)); // search the current side
            nearestdist = nearestPoint.distanceSquaredTo(querypoint); // update nearest distance
            RectHV canvasNotIN = updatecanvas(canvas, node, 1); // the rect of the other side. it is the point range.
            if (canvasNotIN.distanceSquaredTo(querypoint) < nearestdist) { // search the other side, only if there can be a closer distance
                nearestPoint = nearest(node.right, querypoint, nearestPoint, canvasNotIN);
            }
        }
        else { // if query point is on the right/top
            nearestPoint = nearest(node.right, querypoint, nearestPoint, updatecanvas(canvas, node, 1)); // search the current side
            nearestdist = nearestPoint.distanceSquaredTo(querypoint); // update nearest distance
            RectHV canvasNotIN = updatecanvas(canvas, node, 0);
            if (canvasNotIN.distanceSquaredTo(querypoint) < nearestdist) { // search the other side, only if there can be a closer distance
                nearestPoint = nearest(node.left, querypoint, nearestPoint, canvasNotIN);
            }
        }
        return nearestPoint;
    }
    
    private RectHV updatecanvas(RectHV canvas, Node node, int lr) { // lr = 0: left node.
        // update the canvas: if the node is verticle, cut the current parent canvas to left and right.
        if (node.isVertical && lr == 0) return new RectHV(canvas.xmin(), canvas.ymin(), node.p.x(), canvas.ymax());
        if (!node.isVertical && lr == 0) return new RectHV(canvas.xmin(), canvas.ymin(), canvas.xmax(), node.p.y());
        if (node.isVertical && lr == 1) return new RectHV(node.p.x(), canvas.ymin(), canvas.xmax(), canvas.ymax());
        if (!node.isVertical && lr == 1) return new RectHV(canvas.xmin(), node.p.y(), canvas.xmax(), canvas.ymax()); 
        return canvas;
    }
    
    private static void checkNull(Object any, String message) {
        if (any == null) throw new IllegalArgumentException(message);
    }
    
    public static void main(String[] args) {
        // create initial board from file
        In in = new In("example.txt");
        int n = in.readInt();
        double x, y;
        
        KdTree points = new KdTree();
        for (int i = 0; i < n; i++) {
            x = in.readDouble();
            y = in.readDouble();
            Point2D point = new Point2D(x, y);
            points.insert(point);
        }
        
        StdOut.println("Size is " + points.size());
        points.draw();
        RectHV rect = new RectHV(0.3, 0.1, 0.9, 0.9);
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.setPenRadius(0.002);
        rect.draw();
        Iterable<Point2D> boundedpoints = points.range(rect);
        
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setPenRadius(0.015);
        for (Point2D eachpoint : boundedpoints) {
            StdDraw.point(eachpoint.x(), eachpoint.y());
        }
        Point2D point = new Point2D(0.001, 0.656);
        Point2D nearest = points.nearest(point);
        StdOut.println("Nearest point is " + nearest);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.015);
        StdDraw.point(nearest.x(), nearest.y());
        StdDraw.point(point.x(), point.y());
    }
}