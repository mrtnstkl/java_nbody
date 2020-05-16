import javax.swing.*;
import java.awt.*;

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
        double   size = AU; // size of the tree (edge length)
        int      drawInterval = 86400; // drawing interval in seconds
        double   approximation = 0.5; // T
        double   timeSteps = 1* DAY; // controls how often forces are calculated in seconds.
        Body[]   simulationBodies;
        TreeNode simulationTree;


        // --- DIFFERENT CONFIGURATIONS --- ///
        //  solar system
        if(false) {
            Body sun = new Body(1.989e30,new Vector3(0,0,0),new Vector3(0,0,0),StdDraw.YELLOW);
            Body earth = new Body(5.972e24,new Vector3(148e9,0,0),new Vector3(0,29.29e3,0),StdDraw.BOOK_LIGHT_BLUE);
            Body mercury = new Body(3.301e23,new Vector3(-46.0e9,0,0),new Vector3(0,-47.87e3,0),StdDraw.RED);
            Body moon = new Body(1700e3,new Vector3(148e9+365e6,0,0), new Vector3(0,29.29e3+1.0e3,0),StdDraw.GREEN );
            Body mars = new Body(6.418e23,new Vector3(-1.666*AU,0,0),new Vector3(0,-21970,0),StdDraw.PRINCETON_ORANGE);
            Body venus = new Body(3.301e23,new Vector3(0.728*AU,0,0),new Vector3(0,34780,0),StdDraw.LIGHT_GRAY);
            approximation = 0.9; // TODO: BUG: approximation below 1/0.9 causes division by zero
            timeSteps = HOUR;
            drawInterval = 1;
            size = 5*AU;
            simulationBodies = new Body[] {sun,earth, moon ,mars,mercury,venus};
        }
        // star cluster
        if(false) {
            size = 5*AU;
            drawInterval = 10 * MINUTE;
            Cluster c1 = new Cluster(0.5*M,1.5*M,size/2,0.5 );
            c1.generateBodies(10000);
            simulationBodies = c1.bodies;
        }
        // tiny unrealistic cluster
        if(false) {
            drawInterval = 6 * MINUTE;
            size = 0.5*AU;
            Cluster c1 = new Cluster(0.5*M,1.5*M,size/2,0.5 );
            c1.generateBodies(200);
            simulationBodies = c1.bodies;
        }
        // test cluster
        if(true) {
            drawInterval = 1;
            timeSteps = 5 * MINUTE;
            approximation = 0.8;
            Cluster c1 = new Cluster(0.5*M,1.5*M,AU/2,0.8 );
            //c1.setBodyColor(Color.ORANGE);
            c1.generateBodies(10000);
            simulationBodies = c1.bodies;
        }
        // ///////////////////////

        // create octtree
        simulationTree = new TreeNode(Vector3.zeroVec(),size,simulationBodies);

        // print current configuration of the simulation
        {
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
        }

        // setup stdDraw
        StdDraw.setCanvasSize(700,700);
        StdDraw.setScale(size/-2 - size/10, size/2 + size/10);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);


        // benchmark timer
        //long startTime = System.currentTimeMillis();

        // -------- simulation loop --------- //
        double simulationClock = 0;
        double lastDraw = 0;
        while (true){

            // drawing and printing
            if(simulationClock >= lastDraw+drawInterval) {
                //StdDraw.clear(StdDraw.BLACK); simulationTree.drawTree(); StdDraw.show();
                StdDraw.clear(StdDraw.BLACK); for (int i = 0; i < simulationBodies.length ; i++) simulationBodies[i].draw(size*0.002); StdDraw.show();
                lastDraw = simulationClock;
            }

            // debug: move body[0] with click
            if(StdDraw.isMousePressed()) simulationBodies[0] = new Body(simulationBodies[0].getMass(),new Vector3(StdDraw.mouseX(),StdDraw.mouseY(),0));

            // calculate forces and move bodies
            for (int i = 0; i < simulationBodies.length; i++)
                simulationBodies[i].move( simulationTree.calculateForce(simulationBodies[i],approximation) , timeSteps );

            // rebuild tree
            simulationTree = new TreeNode(new Vector3(0,0,0),size,simulationBodies);

            simulationClock+= timeSteps;
        }

        // benchmark results
        //long estimatedTime = System.currentTimeMillis() - startTime;
        //System.out.println("total time: " + estimatedTime + "ms | cycle average: " + estimatedTime/1000 + "ms" );
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

}
