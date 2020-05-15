import java.awt.*;

public class Cluster {

    private double minMass;
    private double maxMass;
    private double clusterDistribution;
    private double clusterRadius;
    private Vector3 clusterCenter;
    private Vector3 clusterVelocity;
    private boolean enable3D = true;

    public Body[] bodies = new Body[0];

    // --- constructors --- //
    public Cluster(double minMass, double maxMass, double clusterRadius, double clusterDistribution, boolean enable3D, Vector3 clusterCenter, Vector3 clusterVelocity ){
        this.minMass = minMass;
        this.maxMass = maxMass;
        this.clusterRadius = clusterRadius;
        this.clusterDistribution = clusterDistribution;
        this.enable3D = enable3D;
        this.clusterCenter = clusterCenter;
        this.clusterVelocity = clusterVelocity;
    }
    public Cluster(double minMass, double maxMass, double clusterRadius, double clusterDistribution, boolean enable3D) {
        this(minMass,maxMass,clusterRadius,clusterDistribution,enable3D,new Vector3(0,0,0),new Vector3(0,0,0));
    }
    public Cluster(double minMass, double maxMass, double clusterRadius, double clusterDistribution){
        this(minMass,maxMass,clusterRadius,clusterDistribution,true);
    }
    public Cluster(){
        this(0.5*Simulation.M,1.5*Simulation.M, Simulation.LY, 0.6);
    }

    public void generateBodies(int amount, boolean initWithVelocities){
        int oldSize = bodies.length;
        // new body array with bigger size
        Body[] b = new Body[ oldSize + amount ];

        // copy array
        for (int i = 0; i < oldSize; i++)
            b[i] = bodies[i];
        bodies = b;

        // generate bodies
        for (int i = 0; i < amount; i++)
            bodies[oldSize + i] = generateBody(initWithVelocities);
    }
    public void generateBodies(int amount){
        generateBodies(amount, false);
    }

    private Body generateBody(boolean initWithVelocities){

        // generate random mass between minMass and maxMass
        double mass = minMass + Math.random()*(maxMass-minMass);
        // generate random color
        Color color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));

        // generate random position vector
        // TODO: switch to generation with polar coordinates for better star distribution
        Vector3 pos = new Vector3(Math.random()-0.5,Math.random()-0.5, enable3D ? Math.random()-0.5 : 0);
        pos.normalize(); // normalised
        pos = pos.times(clusterRadius * Math.pow(Math.random(),clusterDistribution)); // and multiplied with a random distance from the center
        pos = pos.plus(clusterCenter); // calculate absolute position in space by adding clusterCenter

        Vector3 vel = clusterVelocity;
        if(initWithVelocities) {
            // v1: vector from body to center
            // v2: completely random vector
            // vel: cross product of v1 and v2. this gives a vector that is perpendicular to v1
            Vector3 v1 = clusterCenter.minus(pos);
            Vector3 v2 = new Vector3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
            vel = new Vector3(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
            vel.normalize();
            // calculate orbital velocity and multiply the velocity vector with it
            vel = vel.times(Math.sqrt(Simulation.G * 1.5e30 * 100 * 0.3 / pos.distanceTo(clusterCenter)));
            vel = vel.plus(clusterVelocity); // calculate absolute velocity in space by adding clusterVelocity
        }

        Body b = new Body(mass,pos,vel,color);
        return b;
    }

}
