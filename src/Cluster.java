import java.awt.*;

public class Cluster {

    private double  minMass;
    private double  maxMass;
    private double  clusterDistribution;
    private double  clusterRadius;
    private Vector3 clusterCenter;
    private Vector3 clusterVelocity;
    private Color   bodyColor = null;

    private Body[] bodies = new Body[0];

    // --- getter --- //
    public Body[] getBodies()
    { return bodies; }

    // --- setter --- //
    public void setBodyColor(Color bodyColor) { this.bodyColor = bodyColor; }

    // --- constructors --- //
    // takes parameters for configuring the cluster
    public Cluster(double minMass, double maxMass, double clusterRadius, double clusterDistribution, Vector3 clusterCenter, Vector3 clusterVelocity ){
        this.minMass = minMass;
        this.maxMass = maxMass;
        this.clusterRadius = clusterRadius;
        this.clusterDistribution = clusterDistribution;
        this.clusterCenter = clusterCenter;
        this.clusterVelocity = clusterVelocity;
    }
    public Cluster(double minMass, double maxMass, double clusterRadius, double clusterDistribution) {
        this(minMass,maxMass,clusterRadius,clusterDistribution,new Vector3(0,0,0),new Vector3(0,0,0));
    }
    public Cluster(){
        this(0.5*Simulation.M,1.5*Simulation.M, Simulation.AU, 0.6);
    }


    // --- add --- //
    // adds a single body to the cluster
    public void add(Body body){
        // new body array with bigger size
        Body[] b = new Body[ bodies.length + 1 ];

        // copy old bodies to the new array
        for (int i = 0; i < bodies.length-1; i++)
            b[i] = bodies[i];
        // add the new body
        b[b.length-1] = body;

        bodies = b;
    }

    // --- add(array) --- //
    // adds multiple bodies to the cluster
    public void add(Body[] body){

        int oldSize = bodies.length;
        // create new body array with bigger size
        Body[] b = new Body[ oldSize + body.length ];

        // copy old bodies to the new array
        for (int i = 0; i < oldSize; i++)
            b[i] = bodies[i];

        // copy the new bodies to the array
        for (int i = 0; i < body.length; i++)
            b[oldSize+i] = body[i];

        bodies = b;
    }

    // --- generateBodies --- //
    // generates a set amount of bodies and adds them to the bodies array
    public void generateBodies(int amount){
        int oldSize = bodies.length;
        // new body array with bigger size
        Body[] b = new Body[ oldSize + amount ];

        // copy old bodies to the new array
        for (int i = 0; i < oldSize; i++)
            b[i] = bodies[i];
        bodies = b;

        // generate bodies
        for (int i = 0; i < amount; i++)
            bodies[oldSize + i] = generateBody();
    }

    // --- generateBody --- //
    // generates a single body and returns it
    private Body generateBody(){
        // -- mass --------
        // generate random mass between minMass and maxMass
        double mass = minMass + Math.random()*(maxMass-minMass);

        // -- color -------
        // set the color to bodyColor
        Color color = bodyColor;
        // if bodyColor has not been set, generate a random color
        if (bodyColor == null)
            color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));

        // -- position ----
        // generate 3d polar coordinates. requires a polar angle (elevation) and an equatorial angle (azimuth)
        double elevation = Math.random()*Math.PI, azimuth = Math.random()*Math.PI*2;
        // convert to cartesian coordinates
        Vector3 pos = new Vector3( Math.sin(elevation)*Math.cos(azimuth), Math.sin(elevation)*Math.sin(azimuth), Math.cos(elevation) );
        // generate a random distance from the center of the cluster and multiply the position with it
        pos = pos.times(clusterRadius * Math.pow(Math.random(),clusterDistribution));
        // add position of the cluster to get coordinates in global space
        pos = pos.plus(clusterCenter);

        // create body object and return it
        return new Body(mass,pos,clusterVelocity,color);
    }

    // --- calculateVelocities ---
    // STILL WORK IN PROGRESS. Doesn't work as intended yet. In theory:
    // make all bodies in the cluster orbit the center
    // this is done by calculating the total force exerted on each body. This is a vector.
    // By transforming the Formula f=G*m1*m2/d^2  to  d=sqrt(G*m1*m2/f),  a distance to a
    // virtual body with the mass of the entire cluster can be calculated.
    // The calculated values are then inserted into the formula for orbital velocity.
    public void calculateVelocities(){

        // get mass of the entire cluster
        double totalMass = 0.0;
        for ( Body b : bodies )
            totalMass += b.getMass();

        // iterate through all bodies
        for ( Body body : bodies){

            // calculate the force acted on the current body by the rest of the cluster
            Vector3 force = Vector3.zeroVec();
            for ( Body b : bodies ){
                if( b!=body) force = force.plus(body.gravForce(b));
            }

            // d=sqrt(G*m1*m2/f) -> calculate the distance to a virtual body
            double vDistance = Math.sqrt(Simulation.G*body.getMass()*totalMass/force.length());

            // calculate orbital velocity
            double orbitalVelocity = Math.sqrt(Simulation.G*body.getMass()/vDistance);

            // -- normal vector ----
            // generate a random normal vector
            // the normal vector is a vector between the origin and a random point of the normal plane of force
            // generate a random x and y coordinate for that point. then solve the normal plane equation for z
            Vector3 normalVector = new Vector3(Math.random()*2-1,Math.random()*2-1,0.0);
            force.normalize();
            normalVector.z = ( force.x*normalVector.x + force.y*normalVector.y ) / force.z;

            // set the velocity vector equal to the normal vector multiplied with the orbital velocity
            body.setVelocity( normalVector.times(orbitalVelocity) );
        }
    }

}
