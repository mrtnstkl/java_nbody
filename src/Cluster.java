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
        Body[] newB = new Body[ bodies.length + 1 ];

        // copy old bodies to the new array
        for (int i = 0; i < bodies.length; i++)
            newB[i] = bodies[i];
        // add the new body
        newB[newB.length-1] = body;

        bodies = newB;
    }

    // --- add(array) --- //
    // adds multiple bodies to the cluster
    public void add(Body[] body){

        int oldSize = bodies.length;
        // create new body array with bigger size
        Body[] newB = new Body[ oldSize + body.length ];

        // copy old bodies to the new array
        for (int i = 0; i < oldSize; i++)
            newB[i] = bodies[i];

        // copy the new bodies to the array
        for (int i = 0; i < body.length; i++)
            newB[oldSize+i] = body[i];

        bodies = newB;
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
    // Calculates an initial velocity for generated bodies, taking the total mass of the cluster into account.
    public void calculateVelocities(){
        // -- cluster mass -------
        double totalMass = 0;
        for ( Body b : bodies )
            totalMass += b.getMass();

        // iterate through all bodies
        for ( Body body : bodies){
            // -- vector to center --------
            Vector3 vecToCenter = body.getPosition().minus(clusterCenter);
            vecToCenter.normalize();

            // -- perpendicular vector ----
            // generate a random vector that is perpendicular to the vector to the center of the cluster
            // the normal vector is a vector between the origin and a random point of the normal plane of vecToCenter
                // generate a random x and y coordinate for that point.
            Vector3 normalVector = new Vector3(Math.random()*2-1,Math.random()*2-1,0.0);
                // solve the normal plane equation for z
            normalVector.z = ( vecToCenter.x*normalVector.x + vecToCenter.y*normalVector.y ) / vecToCenter.z;

            // -- orbital velocity --------
            double orbitalVelocity = Math.sqrt(Simulation.G*totalMass/body.getPosition().distanceTo(clusterCenter));

            // velocity vector = cluster velocity + normal vector * orbital velocity
            body.setVelocity( clusterVelocity.plus( normalVector.times(orbitalVelocity/2) ) );
        }
    }

}


// ### IGNORE ###
/*

    ############################
    StdDraw.setScale(-100, 100); // scale = area of the tree
        StdDraw.setPenRadius(0.002);
        StdDraw.text(0,0,"GENERATING INITIAL VELOCITIES...");
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
    ##############
    // debug stuff for displaying movement vectors
    Vector3 lineEnd = body.getPosition().plus(normalVector.times(orbitalVelocity).times(500000));
    StdDraw.setPenColor(body.getColor());
    StdDraw.line(body.getPosition().x,body.getPosition().y,lineEnd.x,lineEnd.y);
    StdDraw.setPenColor(StdDraw.WHITE);
    StdDraw.point(body.getPosition().x,body.getPosition().y);

    StdDraw.show();
    StdDraw.pause( 500 );
    #############################

 */