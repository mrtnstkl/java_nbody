import com.sun.org.apache.xpath.internal.objects.XString;

import java.awt.*;


public class Body {

    // --- static star generation variables --- //
    private static double minMass = 1.0e30;
    private static double maxMass = 2.0e30;
    private static double clusterDistribution = 0.6;
    private static boolean enable3D = true;

    // --- variables --- //
    public int id = -1; // debugging
    private double mass;
    private Vector3 position;
    private Vector3 velocity = new Vector3(0,0,0);
    private Color color = Color.white;

    // --- getter --- //
    public double getMass()
    { return mass; }
    public Vector3 getPosition()
    { return position; }
    public Vector3 getVelocity()
    { return velocity; }
    public Color getColor()
    { return color; }

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
        this(1.989e30f, new Vector3(0,0,0));
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
        move();
    }

    // --- draw --- //
    // draw the body as a filled circle
    public void draw(){
        position.drawAsDot(Simulation.getRenderScale()*0.002, color);
    }

    // --- gravForce --- //
    // UNUSED
    // Returns a vector representing the gravitational force exerted by 'b' on this body.
    public Vector3 gravForce(Body b){
        Vector3 forceVector = b.position.minus(position); // vector from the body to the group
        forceVector.normalize(); // normalize the vector
        return forceVector.times( 6.6743e-11d * (mass*b.mass) / Math.pow( position.distanceTo(b.position), 2) );
    }


    /// ---++ STATIC GENERATION METHODS ++--- ///

    // --- generateBody --- //
    // generates and return a body with random position, mass and color
    public static Body generateBody(Vector3 clusterCenter, double clusterRadius) {
        Body b = new Body();

        // generate random mass between minMass and maxMass
        b.mass = minMass + Math.random()*(maxMass-minMass);
        // generate random color
        b.color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));

        // generate random position vector
        // TODO: switch to generation with polar coordinates for better star distribution
        b.position = new Vector3(Math.random()-0.5,Math.random()-0.5, enable3D ? Math.random()-0.5 : 0);
        b.position.normalize(); // normalised
        b.position = b.position.times(clusterRadius * Math.pow(Math.random(),clusterDistribution)); // and multiplied with a random distance from the center
        b.position = b.position.plus(clusterCenter); // get absolute position in space by adding clusterCenter

        // TODO: generate movement vectors
        // generate vector
        // v1: vector from body to center
        // v2: completely random vector
        // b.velocity: cross product of v1 and v2. this gives a vector that is perpendicular to v1
        Vector3 v1 = clusterCenter.minus(b.position);
        Vector3 v2 = new Vector3( Math.random()-0.5, Math.random()-0.5, Math.random()-0.5 );
        b.velocity = new Vector3(v1.y*v2.z - v1.z*v2.y , v1.z*v2.x - v1.x*v2.z, v1.x*v2.y - v1.y*v2.x);
        b.velocity.normalize();
        // calculate orbital velocity and multiply the velocity vector with it
        b.velocity = b.velocity.times( Math.sqrt( Simulation.G * 1.5e30*100*0.3 / b.position.distanceTo(clusterCenter) ) );

        return b;
    }

    // --- generateBodies --- //
    // generate multiple bodies and return array
    public static Body[] generateBodies (Vector3 clusterCenter, double clusterRadius, int amount ) {
        Body bodies[] = new Body[amount]; // the array that will be returned
        for (int i = 0; i < amount ; i++) { // iterate over all bodies and fill the array
            bodies[i] = generateBody(clusterCenter,clusterRadius);
            bodies[i].id = i;
        }
        return bodies;
    }


}
