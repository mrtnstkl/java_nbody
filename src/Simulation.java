import sun.reflect.generics.tree.Tree;

import java.awt.*;

public class Simulation {

    // --- universal constants --- //
    public static final double LY = 9.46073e15; // 1 light year
    public static final double AU = 150e9; // 1 astronomical unit
    public static final double G = 6.6743e-11; // gravitational constant
    public static final double M = 1.989e30; // 1 solar mass

    private static double renderScale; // canvas scale (edge length)
    public static double getRenderScale()
    { return renderScale; }

    public static void main(String[] args) {

        // - variables - //
        int amount; // amount of bodies
        double size = AU; // size of the tree (edge length)
        int drawInterval = 86400; // drawing interval in seconds
        double approximation = 0.5; // T
        TreeNode tree;
        Body[] bodies;

        renderScale = size*1.2;

        // --- DIFFERENT CONFIGURATIONS --- ///
        //  solar system
        if(false)
        {
            Body sun = new Body(1.989e30,new Vector3(0,0,0),new Vector3(0,0,0),StdDraw.YELLOW);
            Body earth = new Body(5.972e24,new Vector3(148e9,0,0),new Vector3(0,29.29e3,0),StdDraw.BLUE);
            Body mercury = new Body(3.301e23,new Vector3(-46.0e9,0,0),new Vector3(0,-47.87e3,0),StdDraw.RED);
            Body moon = new Body(1700e3,new Vector3(148e9+365e6,0,0), new Vector3(0,29.29e3+1.0e3,0),StdDraw.GRAY );
            Body mars = new Body(6.418e23,new Vector3(-1.666*AU,0,0),new Vector3(0,-21970,0),StdDraw.PRINCETON_ORANGE);
            Body venus = new Body(3.301e23,new Vector3(0.728*AU,0,0),new Vector3(0,34780,0),StdDraw.LIGHT_GRAY);

            drawInterval = 86400;
            amount = 5;
            size = 5*AU;
            bodies = new Body[] {sun,earth,mars,mercury,venus};
            tree = new TreeNode(new Vector3(0,0,0), size , bodies);
        }
        // star cluster
        if(false)
        {
            amount = 10000;
            size = 5*AU;
            approximation = 1;
            drawInterval = 86400/24/30;
            Body[] b = Body.generateBodies(new Vector3(0,0,0),size,amount); bodies = b;
            //b[0] = new Body(5e33,new Vector3(0,0,0)); // central black hole
            tree = new TreeNode(new Vector3(0,0,0), size , bodies);
        }
        // tiny unrealistic cluster
        if(false)
        {
            drawInterval = 86400/24/10;
            amount = 100;
            size = 0.5*AU;
            Body[] b = Body.generateBodies(new Vector3(0,0,0),size/2,amount); bodies = b;
            tree = new TreeNode(new Vector3(0,0,0), size , bodies);
        }
        if(true)
        {
            drawInterval = 86400/24/60;
            Cluster c1 = new Cluster(0.5*M,1.5*M,AU/2,0.8,true,new Vector3(0,0,0), new Vector3(0,0,0));
            c1.generateBodies(10000);
            bodies = c1.bodies;
            tree = new TreeNode(Vector3.zeroVector(),size,bodies);
        }
        // ///////////////////////

        // stdDraw setup
        StdDraw.setCanvasSize(1000,1000);
        StdDraw.setScale(size/-2 - size/10, size/2 + size/10);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);


        // benchmark timer
        //long startTime = System.currentTimeMillis();

        // -------- simulation loop --------- //
        double seconds = 0;
        while (true){

            // drawing and printing
            if(seconds%drawInterval==0)
            {
                System.out.println("day " + (int)seconds/86400);
                //StdDraw.clear(Color.black); tree.drawTree(); StdDraw.show();
                StdDraw.clear(StdDraw.BLACK); for (int i = 0; i < bodies.length ; i++) bodies[i].draw(); StdDraw.show();
            }

            // debug: move body[0] with click
            if(StdDraw.isMousePressed())
                bodies[0] = new Body(bodies[0].getMass(),new Vector3(StdDraw.mouseX(),StdDraw.mouseY(),0));

            // calculate forces and move bodies
            for (int i = 0; i < bodies.length; i++){
                // DEBUG: System.out.println("CALCULATING FORCE FOR " + i);
                bodies[i].move( tree.calculateForce(bodies[i],approximation).times(60.0) );
            }

            // rebuild tree
            tree = new TreeNode(new Vector3(0,0,0),size,bodies);


            seconds++;
        }

        // benchmark results
        //long estimatedTime = System.currentTimeMillis() - startTime;
        //System.out.println("total time: " + estimatedTime + "ms | cycle average: " + estimatedTime/1000 + "ms" );
    }


}
