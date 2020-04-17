import sun.reflect.generics.tree.Tree;

import java.awt.*;

public class TreeNode {

    private enum NodeStatus { EMPTY, BODY, PARENT;}

    // --- variables --- //
    private Vector3 position; // position of quadrant (center)
    private double size; // size of quadrant (length of edge)
    private NodeStatus status = NodeStatus.EMPTY;

    private TreeNode children[] = new TreeNode[8]; // array of child nodes

    private Body body;
    private double totalMass = 0;
    private Vector3 centerOfMass = new Vector3(0,0,0);


    // --- constructors --- //
    public TreeNode(Vector3 position, double size){
        this.position = position;
        this.size = size;
    }
    // constructor that adds bodies to the created tree
    public TreeNode(Vector3 position, double size, Body[] bodies){
        this.position = position;
        this.size = size;
        for (Body b : bodies) add(b);
    }


    // --- add --- //
    // recursively inserts a body into the tree
    public void add(Body body) {

        // only add body if it is inside the boundary of the tree
        if( body.getPosition().x > position.x+size/2 || body.getPosition().x < position.x-size/2 ||
            body.getPosition().y > position.y+size/2 || body.getPosition().y < position.y-size/2 ||
            body.getPosition().z > position.z+size/2 || body.getPosition().z < position.z-size/2  ) {
            // DEBUG: System.out.println(body.getPosition() + " is outside boundary");
            return;
        }

        // calculate new mass and center of mass
        // (com * M + pos * m) * (1 / (M + m))
        centerOfMass = (centerOfMass.times(totalMass) .plus(body.getPosition().times(body.getMass()))) .times(1.0/(totalMass+body.getMass()));
        totalMass += body.getMass();

        if(status==NodeStatus.EMPTY) { // node is empty
            status = NodeStatus.BODY; // change status
            this.body = body; // set body
        }
        else if(status==NodeStatus.BODY){ // node has a body

            status = NodeStatus.PARENT; // change node status

            // move the old body to the correct child node
            initChild(determineQuadrant(this.position,this.size,this.body.getPosition()));
            children[determineQuadrant(this.position,this.size,this.body.getPosition())].add(this.body);
            this.body = null;

            // add the new body to the correct child node
            initChild(determineQuadrant(this.position,this.size,body.getPosition()));
            children[determineQuadrant(this.position,this.size,body.getPosition())].add(body);
        }
        else { // node is a parent and has child nodes
            // add the new body to the correct child node
            initChild(determineQuadrant(this.position,this.size,body.getPosition()));
            children[determineQuadrant(this.position,this.size,body.getPosition())].add(body);
        }
    }

    // --- calculateForce --- //
    // returns a vector representing the gravitational force that this group of bodies exerts on b
    public Vector3 calculateForce(Body b, double approximation){
        Vector3 forceVector = new Vector3(0,0,0);

        if(b.getPosition().distanceTo(centerOfMass) < 1.0 || status == NodeStatus.EMPTY) // return 0-vector if body is compared to itself or the node empty
        {
            // DEBUG: System.out.println("calculateForce(): guard triggered");
            return forceVector;
        }

        // DEBUG
        /*
        if(b.getPosition().distanceTo(position)/size > approximation)
            System.out.println("calculateForce(): returning approximation");
        else if (status == NodeStatus.BODY)
            System.out.println("calculateForce(): returning body");
        */
        // /DEBUG

        if( b.getPosition().distanceTo(position)/size > approximation || status == NodeStatus.BODY ){ // r/d > T
            // far enough:
            forceVector = centerOfMass.minus(b.getPosition()); // vector from the body to the group
            forceVector.normalize(); // normalize the vector
            // multiply normalised vector with force and return it
            // DEBUG: System.out.println( "returning  " + b.id +" : " + totalMass + " " + b.getMass() + " " + b.getPosition().distanceTo(centerOfMass) + " | :" );
            return forceVector.times( Simulation.G * (totalMass*b.getMass()) / Math.pow( b.getPosition().distanceTo(centerOfMass), 2) );
        }
        else if (status == NodeStatus.PARENT){
            // add force vectors of all children if node has children
            // DEBUG: System.out.println("calculateForce(): stepping down");
            for (int i = 0; i < 8 ; i++) {
                if(children[i] != null) {
                    // add all force vectors of the children together
                    forceVector = forceVector.plus(children[i].calculateForce(b,approximation));
                }
            }
            // DEBUG: System.out.println("calculateForce(): stepping up");
        }
        return forceVector;
    }

    // --- drawTree --- //
    // recursively draws all bodies and body nodes
    public void drawTree(){
        centerOfMass.drawAsDot(0.8,StdDraw.BOOK_LIGHT_BLUE);
        if(status == NodeStatus.BODY){
            StdDraw.setPenColor(body.getColor());
            StdDraw.rectangle(position.x,position.y,size/2,size/2);
            body.draw();
        }
        if(status == NodeStatus.PARENT){
            for (int i = 0; i < 8 ; i++)
            {
                if(children[i] != null)
                    children[i].drawTree();
            }
        }
    }

    // --- countElements --- //
    // recursively counts all body nodes of the tree (currently unused)
    public int countElements(){
        int total=0;
        if(status == NodeStatus.BODY){
            return 1;
        }
        if(status == NodeStatus.PARENT){
            for (int i = 0; i < 8 ; i++)
            {
                if(children[i] != null)
                    total += children[i].countElements();
            }
        }
        return total;
    }



    /// ---++ PRIVATE HELPER METHODS ++--- ///

    // --- determineQuadrant --- //
    // takes a body and quadrant position and returns which sub-quadrant the body is in
    private int determineQuadrant(Vector3 qPosition, double size, Vector3 bodyPosition){
        if(bodyPosition.z <= qPosition.z){
            // node 0, 1, 2, 3
            if(bodyPosition.y <= qPosition.y) {
                // node 0, 1
                if(bodyPosition.x <= qPosition.x)
                    return 0;
                else
                    return 1;
            }
            else {
                // node 2, 3
                if (bodyPosition.x <= qPosition.x)
                    return 2;
                else
                    return 3;
            }
        }
        else{
            // node 4, 5, 6, 7
            if(bodyPosition.y <= qPosition.y) {
                // node 0, 1
                if(bodyPosition.x <= qPosition.x)
                    return 4;
                else
                    return 5;
            }
            else{
                // node 2, 3
                if(bodyPosition.x <= qPosition.x)
                    return 6;
                else
                    return 7;
            }
        }
    }

    // --- initChild --- //
    // initialise child node with the given index
    private void initChild(int childNumber){
        if(children[childNumber] != null)
            return;

        switch (childNumber) {
            case 0:
                children[0] = new TreeNode( new Vector3( position.x - size/4 , position.y - size/4 , position.z - size/4 ) , size/2 );
                break;
            case 1:
                children[1] = new TreeNode( new Vector3( position.x + size/4 , position.y - size/4 , position.z - size/4 ) , size/2 );
                break;
            case 2:
                children[2] = new TreeNode( new Vector3( position.x - size/4 , position.y + size/4 , position.z - size/4 ) , size/2 );
                break;
            case 3:
                children[3] = new TreeNode( new Vector3( position.x + size/4 , position.y + size/4 , position.z - size/4 ) , size/2 );
                break;
            case 4:
                children[4] = new TreeNode( new Vector3( position.x - size/4 , position.y - size/4 , position.z + size/4 ) , size/2 );
                break;
            case 5:
                children[5] = new TreeNode( new Vector3( position.x + size/4 , position.y - size/4 , position.z + size/4 ) , size/2 );
                break;
            case 6:
                children[6] = new TreeNode( new Vector3( position.x - size/4 , position.y + size/4 , position.z + size/4 ) , size/2 );
                break;
            case 7:
                children[7] = new TreeNode( new Vector3( position.x + size/4 , position.y + size/4 , position.z + size/4 ) , size/2 );
                break;
        }
    }


}
