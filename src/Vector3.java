import java.awt.*;

// This class represents vectors in a 3D vector space.
public class Vector3 {

    public double x;
    public double y;
    public double z;

    // --- getter ---
    //public double getX() { return x; }
    //public double getY() { return y; }
    //public double getZ() { return z; }

    // --- constructor ---
    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // --- plus ---
    // Returns the sum of this vector and vector 'v'.
    public Vector3 plus(Vector3 v) {
        return new Vector3( x+v.x , y+v.y , z+v.z );
    }

    // --- times ---
    // Returns the product of this vector and 'd'.
    public Vector3 times(double d) {
        return new Vector3(x*d,y*d,z*d);
    }

    // --- minus ---
    // Returns the sum of this vector and -1*v.
    public Vector3 minus(Vector3 v) {
        return new Vector3( x-v.x , y-v.y , z-v.z );
    }

    // --- distanceTo ---
    // Returns the Euclidean distance of this vector
    // to the specified vector 'v'.
    public double distanceTo(Vector3 v) {
        Vector3 distancev = v.minus(this);
        return distancev.length();
    }

    // --- length ---
    // Returns the length (norm) of this vector.
    public double length() {
        return ( Math.sqrt( x*x + y*y + z*z ) );
    }

    // --- normalize ---
    // Normalizes this vector: changes the length of this vector such that it becomes one.
    // The direction and orientation of the vector is not affected.
    public void normalize() {
        double length = length();
        x /= length;
        y /= length;
        z /= length;
    }

    // --- drawAsDot ---
    // Draws a filled circle with the center at (x,y) coordinates of this vector
    // in the existing StdDraw canvas. The z-coordinate is not used.
    public void drawAsDot(double radius, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(x,y,radius);
    }
    public void drawAsCircle(double radius, Color color) {
        StdDraw.setPenColor(color);
        StdDraw.circle(x,y,radius);
    }

    // -- toString ---
    // Returns the coordinates of this vector in brackets as a string
    // in the form "[x,y,z]", e.g., "[1.48E11,0.0,0.0]".
    public String toString() {
        return "[" + x + ',' + y + ',' + z + ']';
    }

    // --- print ---
    // Prints the coordinates of this vector in brackets to the console (without newline)
    // in the form [x,y,z], e.g.,
    // [1.48E11,0.0,0.0]
    public void print() {
        System.out.printf(toString());
    }

    // --- focusView ---
    // centers the view around the vector
    public void focusView(double scale){
        StdDraw.setXscale(-1 * scale +x, scale +x);
        StdDraw.setYscale(-1 * scale +y, scale +y);
    }

}

