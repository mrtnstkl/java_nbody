import java.awt.*;

public class Cluster {

    private double  minMass;
    private double  maxMass;
    private double  clusterDistribution;
    private double  clusterRadius;
    private Vector3 clusterCenter;
    private Vector3 clusterVelocity;
    private Color   bodyColor = null;
    private boolean enable3D = true;

    public Body[] bodies = new Body[0];


    // --- constructors --- //
    // takes parameters for configuring the cluster
    public Cluster(double minMass, double maxMass, double clusterRadius, double clusterDistribution, boolean enable3D, Vector3 clusterCenter, Vector3 clusterVelocity ){
        this.minMass = minMass;
        this.maxMass = maxMass;
        this.clusterRadius = clusterRadius;
        this.clusterDistribution = clusterDistribution;
        this.enable3D = enable3D;
        this.clusterCenter = clusterCenter;
        this.clusterVelocity = clusterVelocity;
    }
    // constructors with missing parameters (use default configuration)
    public Cluster(double minMass, double maxMass, double clusterRadius, double clusterDistribution, boolean enable3D) {
        this(minMass,maxMass,clusterRadius,clusterDistribution,enable3D,new Vector3(0,0,0),new Vector3(0,0,0));
    }
    public Cluster(double minMass, double maxMass, double clusterRadius, double clusterDistribution){
        this(minMass,maxMass,clusterRadius,clusterDistribution,true);
    }
    public Cluster(){
        this(0.5*Simulation.M,1.5*Simulation.M, Simulation.LY, 0.6);
    }

    // --- setter --- //
    public void setBodyColor(Color bodyColor) { this.bodyColor = bodyColor; }

    // --- add ---
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

    // --- add(array) ---
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
        // if bodyColor has not been initialised, generate a random color
        if (bodyColor == null)
            color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));

        // -- position ----
        // generate 3d polar coordinates. requires a polar angle (elevation) and an equatorial angle (azimuth)
        double elevation = Math.random()*Math.PI, azimuth = Math.random()*Math.PI*2;
        // convert to cartesian coordinates
        Vector3 pos = new Vector3( Math.sin(elevation)*Math.cos(azimuth), Math.sin(elevation)*Math.sin(azimuth), (enable3D ? Math.cos(elevation) : 0.0) );
        // generate a random distance from the center of the cluster and multiply the position with it
        pos = pos.times(clusterRadius * Math.pow(Math.random(),clusterDistribution));
        // add position of the cluster to get coordinates in global space
        pos = pos.plus(clusterCenter);

        // create body object and return it
        return new Body(mass,pos,Vector3.zeroVec(),color);
    }

    // TODO: --- calculateVelocities ---
    // make all bodies in the cluster orbit the center
    public void calculateVelocities(){

    }

}
