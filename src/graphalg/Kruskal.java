/* Kruskal.java */

package graphalg;

import set.*;
//import graph.WUGraph;
import graph.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// modified quicksort to sort the edges
class QuickSortEdge {

    // Public method to start Quick Sort
    public static void quickSort(Edge[] array) {
        if (array == null || array.length == 0) {
            return;
        }
        quickSortHelper(array, 0, array.length - 1);
    }

    // Internal recursive method for Quick Sort
    private static void quickSortHelper(Edge[] array, int low, int high) {
        if (low < high) {
            // Partition the array and get the pivot index
            int pivotIndex = partition(array, low, high);

            // Recursively sort the two halves
            quickSortHelper(array, low, pivotIndex - 1);
            quickSortHelper(array, pivotIndex + 1, high);
        }
    }

    // Partition method
    private static int partition(Edge[] array, int low, int high) {
        int pivot = array[high].getWeight(); // Choose the last edge as the pivot
        int i = low - 1; // Index for the smaller edge

        for (int j = low; j < high; j++) {
            // If current edge weight is smaller than or equal to the pivot,
            if (array[j].getWeight() <= pivot) {
                i++;
                swap(array, i, j);
            }
        }


        swap(array, i + 1, high);

        return i + 1; // Return the pivot's index
    }

    // Swap two edges in the array
    private static void swap(Edge[] array, int i, int j) {
        Edge temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}


//Edge class stores the edges in an array without using VertexPair from graph file
//This also has weight paramter unlike Vertex pairs. This makes it easier to sort edges when we sort by weight
//
class Edge {
    Object vertex1, vertex2;
    int weight;

    public Edge(Object vertex1, Object vertex2, int weight) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge) {
            Edge other = (Edge) obj;

            return (((this.vertex1.equals(other.vertex1)) && (this.vertex2.equals(other.vertex2))) || ((this.vertex1.equals(other.vertex2)) && (this.vertex2.equals(other.vertex1))));

        } else {
            return false;
        }
    }
}

/**
 * The Kruskal class contains the method minSpanTree(), which implements
 * Kruskal's algorithm for computing a minimum spanning tree of a graph.
 */

public class Kruskal {


    // Helper method which gets all edges from graph g and into an array(edges)
    private static Edge[] getEdges(Object[] vertices, WUGraph g) {

        Set<Edge> set = new HashSet<Edge>();

        //iterates through the vertices which allows us to access its neighbors
        //if #unique edge weights == the number of edges in g the loops terminate early and method ends.
        for (int i = 0; i < vertices.length; i++) {

            //list of neighboring vertices and the corresponding edges weights which seperate the two vertices.
            Object[] connectedVertices = (g.getNeighbors(vertices[i])).neighborList;
            int[] tempWeights = (g.getNeighbors(vertices[i])).weightList;

            //second loop to iterate through the neighbors list and create each edge
            //runs from 0 to #neighboring vertices
            for (int k = 0; k < connectedVertices.length; k++) {
                Edge edgeToBeAdded = new Edge(vertices[i], connectedVertices[k], tempWeights[k]);

                set.add(edgeToBeAdded);


            }
            //Exit for loops early if the numEdges is equal to edgeCount or edges.length
            if (set.size() == g.edgeCount()) {
                break;
            }


        }

        return set.toArray(new Edge[0]);


    }

    /**
     * minSpanTree() returns a WUGraph that represents the minimum spanning tree
     * of the WUGraph g.  The original WUGraph g is NOT changed.
     *
     * @param g The weighted, undirected graph whose MST we want to compute.
     * @return A newly constructed WUGraph representing the MST of g.
     ***/
    public static WUGraph minSpanTree(WUGraph g) {
        WUGraph T = new WUGraph();

        Object[] vertices = g.getVertices();

        //add the vertices to T
        for (int i = 0; i < vertices.length; i++) {
            T.addVertex(vertices[i]);
        }

        // creates an array to hold all edges, size is the same as # edges in parameter g
        Edge[] edges = new Edge[g.edgeCount()];

        //a helper method to get all edges from g using getNeighbors
        edges = getEdges(vertices, g);


        //sorts the edges
        QuickSortEdge.quickSort(edges);

        //hash map so that every vertex can be mapped to a unique integer for use in the DisjointSet class
        HashMap<Object, Integer> vertexToIndex = new HashMap<>();

        //in the disjoint sets the index represents the different vertices
        DisjointSets disjointSets = new DisjointSets(vertices.length);

        // this fills in the hash tables with each vertex represented by a unique integer
        for (int i = 0; i < vertices.length; i++) {
            vertexToIndex.put(vertices[i], i);
        }


        //Using the sorted array of edges we start connecting vertices using union
        // Each time however it must check if it is forming a cycle.
        //to check if it is forming a cycle you have to make sure that the two vertices (you are union-ing)are not in the same set.
        //or in this case does not have the same root as the different sets are represented by trees.

        for (int i = 0; i < edges.length; i++) {

            //for readability vertex 1 and 2 is taken from edges which has been sorted to be the minimum unused edge
            Object vertex1 = edges[i].vertex1;
            Object vertex2 = edges[i].vertex2;
            //using hashmap get the mapped integer from each vertex
            int intEqV1 = vertexToIndex.get(vertex1);
            int intEqV2 = vertexToIndex.get(vertex2);

            //Get the roots of each vertex
            int root1 = disjointSets.find(intEqV1);
            int root2 = disjointSets.find(intEqV2);

            //make sure both vertices are not in the same root.
            if (root1 != root2) {  // Proceed only if they belong to different sets
                T.addEdge(vertex1, vertex2, edges[i].getWeight());
                disjointSets.union(root1, root2);  // Union roots, not indices
            }

        }

        return T;


    }


}