import java.awt.*;
import java.time.DayOfWeek;


public class Simulation {

    // --- universal constants --- //
    public static final double G  = 6.6743e-11; // gravitational constant
    public static final double LY = 9.46073e15; // 1 light year
    public static final double AU = 150e9;      // 1 astronomical unit
    public static final double M  = 1.989e30;   // 1 solar mass

    public static final int DAY = 86400; // seconds in a day
    public static final int HOUR = 3600; // seconds in an hour
    public static final int MINUTE = 60; // seconds in one minute


    public static void main(String[] args) {

        // - VARIABLES - //
        double   size          = AU;    // size of the tree (edge length)
        int      drawInterval  = 1;     // drawing interval in seconds.
        double   approximation = 0.5;   // T
        double   timeSteps     = HOUR;  // controls how often forces are calculated in seconds / how much time passes with one iteration.
        boolean  recursiveDraw = false; // true: also draws all body nodes as rectangles (much slower than default).
        double   drawRadius    = 0.001; // StdDraw pen radius

        Body[]   simulationBodies = new Body[0];  // array that holds all bodies that will be simulated


        // --- DIFFERENT CONFIGURATIONS --- ///
        // 1 : solar system with sun, earth, moon, mars, mercury, venus
        // 2 : simple cluster with 10k bodies
        // 3 : cluster with 50k bodies
        // 4 : cluster with large area, 20k bodies (slow)
        // 5 : small amount of bodies(1k), viewed from a distance
        // 6 : 2 clusters with the same parameters
        // 7 : 2 clusters, one with lighter stars, the other with heavier stars
        // 8 : 4 clusters with different star distribution
        // 9 : 2 clusters. one of them is smaller and has a velocity, moving through the bigger cluster
        int config = 8;
        switch (config){
            case 1: {
                Body sun = new Body(1.989e30,new Vector3(0,0,0),new Vector3(0,0,0),StdDraw.YELLOW);
                Body earth = new Body(5.972e24,new Vector3(148e9,0,0),new Vector3(0,29.29e3,0),StdDraw.BOOK_LIGHT_BLUE);
                Body mercury = new Body(3.301e23,new Vector3(-46.0e9,0,0),new Vector3(0,-47.87e3,0),StdDraw.RED);
                Body moon = new Body(1700e3,new Vector3(148e9+365e6,0,0), new Vector3(0,29.29e3+1.0e3,0),StdDraw.GREEN );
                Body mars = new Body(6.418e23,new Vector3(-1.666*AU,0,0),new Vector3(0,-21970,0),StdDraw.PRINCETON_ORANGE);
                Body venus = new Body(3.301e23,new Vector3(0.728*AU,0,0),new Vector3(0,34780,0),StdDraw.LIGHT_GRAY);
                timeSteps = 20 * MINUTE;
                approximation = 1;
                drawInterval = (int)timeSteps * 10;
                size = 5*AU;
                drawRadius = 0.005;
                simulationBodies = new Body[] {sun,earth,moon,mars,mercury,venus};
                break;
            }
            case 2: {
                timeSteps = 6*HOUR;
                approximation = 0.8;
                size = 50*AU;
                Cluster c = new Cluster(1*M,1*M,size/2,0.8);
                c.generateBodies(10000);
                c.calculateVelocities();
                simulationBodies = c.getBodies();
                break;
            }
            case 3: {
                timeSteps = 3*HOUR;
                approximation = 0.5;
                size = 50*AU;
                Cluster c = new Cluster(5*M,10*M,size/2,0.3);
                c.generateBodies(50000);
                simulationBodies = c.getBodies();
                break;
            }
            case 4: {
                size = 1*LY;
                timeSteps = 300 * DAY;
                drawInterval= 600 * DAY;
                Cluster c = new Cluster(1*M,10*M,size/2,0.5 );
                c.generateBodies(20000);
                simulationBodies = c.getBodies();
                break;
            }
            case 5: {
                timeSteps = MINUTE;
                drawInterval = 10 * MINUTE;
                approximation = 1;
                size = 5*AU;
                Cluster c = new Cluster(1*M,1.5*M,size/8,0.5 );
                c.generateBodies(1000);
                simulationBodies = c.getBodies();
                break;
            }
            case 6: {
                timeSteps = 6*HOUR;
                approximation = 0.8;
                size = 50*AU;
                Cluster c1 = new Cluster(0.5*M,1.5*M,size/3,0.5, new Vector3(size*-0.2,size*-0.2, 0.0), Vector3.zeroVec() );
                Cluster c2 = new Cluster(0.5*M,1.5*M,size/3,0.5, new Vector3(size*+0.2,size*+0.2, 0.0), Vector3.zeroVec() );
                c1.setBodyColor(Color.ORANGE); c2.setBodyColor(Color.CYAN);
                c1.generateBodies(5000); c2.generateBodies(5000);
                simulationBodies = combineArrays(c1.getBodies(),c2.getBodies());
                break;
            }
            case 7: {
                timeSteps = 1* DAY;
                approximation = 0.9;
                size = 100*AU;
                Cluster c1 = new Cluster(0.5*M,1.5*M,size/3,0.5, new Vector3(size*-0.2,size*-0.2, 0.0), Vector3.zeroVec() );
                Cluster c2 = new Cluster(5*M,10*M,size/3,0.5, new Vector3(size*+0.15,size*+0.15, 0.0), Vector3.zeroVec() );
                c1.setBodyColor(Color.ORANGE); c2.setBodyColor(Color.CYAN);
                c1.generateBodies(5000); c2.generateBodies(5000);
                simulationBodies = combineArrays(c1.getBodies(),c2.getBodies());
                break;
            }
            case 8: {
                timeSteps = DAY;
                drawInterval = 2*DAY;
                approximation = 0.9;
                size = 100*AU;
                Cluster c3 = new Cluster(M,M,size/4,0, new Vector3(size*-0.2,size*+0.2, 0.0), Vector3.zeroVec() );
                Cluster c1 = new Cluster(M,M,size/4,0.2, new Vector3(size*+0.2,size*+0.2, 0.0), Vector3.zeroVec() );
                Cluster c4 = new Cluster(M,M,size/4,0.8, new Vector3(size*-0.2,size*-0.2, 0.0), Vector3.zeroVec() );
                Cluster c2 = new Cluster(M,M,size/4,5, new Vector3(size*+0.2,size*-0.2, 0.0), Vector3.zeroVec() );
                c1.setBodyColor(Color.ORANGE); c2.setBodyColor(Color.CYAN); c3.setBodyColor(Color.GREEN); c4.setBodyColor(Color.RED);
                c1.generateBodies(2000); c2.generateBodies(2000); c3.generateBodies(2000); c4.generateBodies(2000);
                simulationBodies = combineArrays(c1.getBodies(),c2.getBodies(),c3.getBodies(),c4.getBodies());
                break;
            }
            case 9: {
                timeSteps = DAY;
                approximation = 0.9;
                size = 200*AU;
                Cluster c1 = new Cluster(0.5*M,0.6*M,size/4,0.5, Vector3.zeroVec(), Vector3.zeroVec() );
                Cluster c2 = new Cluster(10*M,15*M,size/32,0.3, new Vector3(size/-3,size/-3,0), new Vector3(2e6,2e6,0) );
                c1.setBodyColor(Color.ORANGE); c2.setBodyColor(Color.CYAN);
                c1.generateBodies(6000); c2.generateBodies(3000);
                simulationBodies = combineArrays(c1.getBodies(),c2.getBodies());
                break;
            }
        }

        // -- create octtree -----------
        TreeNode simulationTree = new TreeNode(Vector3.zeroVec(),size,simulationBodies);

        // -- print configuration ------
        System.out.println(
                " -- Running Barnes-Hut Simulation --\n" +
                        " body count:    " + simulationBodies.length + '\n' +
                        " approximation: " + approximation + '\n' +
                        " tree size:     " + (size < 1000*AU ? size/AU + " AU" : size/LY + "LY" ) + '\n' +
                        " time steps   : " + timeSteps + "sec\n" +
                        " draw interval: " + (drawInterval < 60 ? drawInterval + " sec" :
                        ( drawInterval < 3600 ? drawInterval/60 + " min " + ( drawInterval%60 != 0 ? drawInterval%60 + "sec" : "" ) :
                                drawInterval/3600 + "h " + ( drawInterval%60 != 0 ? drawInterval%60 + "min" : "" ) ) )
        );

        // -- setup stdDraw ------------
        StdDraw.setCanvasSize(700,700);
        StdDraw.setScale(size/-2, size/2); // scale = area of the tree
        StdDraw.setPenRadius(drawRadius);
        StdDraw.enableDoubleBuffering();


        // -------- simulation loop --------- //
        double simulationClock = 0;
        double lastDraw = 0;
        while (true){

            // 1) drawing and printing
            if(simulationClock >= lastDraw+drawInterval) {
                //printTimestamp(simulationClock);
                StdDraw.clear(StdDraw.BLACK);
                if(recursiveDraw)
                    simulationTree.drawTree();
                else
                    for (Body b : simulationBodies){
                        if(!b.active) continue; // only draw if body is active
                        StdDraw.setPenColor(b.getColor());
                        StdDraw.point(b.getPosition().x, b.getPosition().y);
                    }
                StdDraw.show();
                lastDraw = simulationClock;
            }

            // 2) calculate forces and move bodies
            for(Body b : simulationBodies)
                if(b.active) b.move( simulationTree.calculateForce(b,approximation) , timeSteps );

            // just for fun/testing: reinitialise body[0] at mouse position when clicked
            if(StdDraw.isMousePressed()) simulationBodies[0] = new Body(simulationBodies[0].getMass(),new Vector3(StdDraw.mouseX(),StdDraw.mouseY(),0));

            // 3) rebuild tree
            simulationTree = new TreeNode(Vector3.zeroVec(),size,simulationBodies);

            simulationClock+= timeSteps;
        }

    }


    // --- printTimestamp --- //
    // prints a timestamp as days, hours and minutes
    private static void printTimestamp(double seconds){
        System.out.println(
                "day " + (int)seconds/DAY + "  " +
                (((int)seconds%DAY)/HOUR < 10 ?'0':"") + ((int)seconds%DAY)/HOUR + ":" +
                (((int)seconds%HOUR)/60 < 10 ? '0' : "" ) + ((int)seconds%HOUR)/60
        );
    }

    // --- combineArrays --- //
    // takes in multiple body arrays and combines them into one
    private static Body[] combineArrays(Body[]...bodyArrays){
        int newSize = 0;
        for(Body[] a : bodyArrays) newSize += a.length;

        Body[] newArray = new Body[newSize];

        int counter = 0;
        for(Body[] a : bodyArrays)
            for(Body b : a) newArray[counter++] = b;

        return newArray;
    }

}
