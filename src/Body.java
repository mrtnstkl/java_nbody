import com.sun.org.apache.xpath.internal.objects.XString;

import java.awt.*;

public class Body {

    // --- variables --- //
    private double mass;
    private Vector3 position;
    private Vector3 velocity = new Vector3(0,0,0);
    private Color color = Color.white;

    public boolean active = true;

    // --- getter --- //
    public double getMass()
    { return mass; }
    public Vector3 getPosition()
    { return position; }
    public Vector3 getVelocity()
    { return velocity; }
    public Color getColor()
    { return color; }

    // --- setter --- //
    public void setVelocity(Vector3 velocity)
    { this.velocity = velocity; }

    // --- constructors --- //
    public Body(double mass,Vector3 position){
        this.mass = mass;
        this.position = position;
    }
    public Body(double mass,Vector3 position, Vector3 velocity, Color color){
        this.mass = mass;
        this.position = position;
        this.velocity = velocity;
        this.color = color;
    }
    // default constructor
    public Body(){
        this(1.989e30f, Vector3.zeroVec());
    }


    // --- move --- //
    // move the body
    public void move(){
        position = position.plus(velocity);
    }
    // --- move(force) ---//
    // calculate new velocity, then move the body
    public void move(Vector3 force){
        velocity = velocity.plus(force.times(1/mass));
        position = position.plus(velocity);
    }
    // --- move(force, time) --- //
    // move the body for a given amount of seconds.
    public void move(Vector3 force, double seconds){
        velocity = velocity.plus(force.times(1/mass).times(seconds));
        position = position.plus(velocity.times(seconds));
    }

    // --- draw --- //
    // draw the body as a filled circle
    public void draw(double radius){
        position.drawAsDot(radius, color);
    }

    // --- gravForce --- //
    // UNUSED
    // Returns a vector representing the gravitational force exerted by 'b' on this body.
    public Vector3 gravForce(Body b){
        Vector3 forceVector = b.position.minus(position); // vector from the body to the group
        forceVector.normalize(); // normalize the vector
        return forceVector.times( 6.6743e-11d * (mass*b.mass) / Math.pow( position.distanceTo(b.position), 2) );
    }

    // --- toString --- //
    // return a string with the mass, position and velocity
    @Override
    public String toString(){
        return "m: " + mass + "  pos: " + position + "  vel: " + velocity;
    }
}
