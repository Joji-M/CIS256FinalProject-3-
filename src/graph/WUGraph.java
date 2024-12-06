package graph;

import dict.*;
import list.*;

/**
 * The WUGraph class represents a weighted, undirected graph. This graph supports
 * self-edges.
 *
 * Fields:
 * - vertexTable: Maps a vertex (Object) to its corresponding DListNode in vertices.
 * - adjacencyMap: Maps a DListNode (representing a vertex) to its adjacency list.
 * - edgeWeights: Maps VertexPair objects to edge weights.
 * - vertices: A DList containing all vertices in the graph.
 */

public class WUGraph {

  // Maps vertex -> DListNode in vertices
  private final HashTableChained vertexTable;

  // Maps DListNode -> adjacency list (DList)
  private final HashTableChained adjacencyMap;

  // Maps VertexPair -> weight
  private final HashTableChained edgeWeights;

  // List of all vertices in the graph
  private final DList vertices;

  /**
   * Constructs an empty weighted, undirected graph with no vertices or edges.
   *
   * Running time: O(1).
   */
  public WUGraph() {
    vertexTable = new HashTableChained();
    adjacencyMap = new HashTableChained();
    edgeWeights = new HashTableChained();
    vertices = new DList();
  }

  /**
   * Returns the number of vertices in the graph.
   *
   * @return the total number of vertices.
   * Running time: O(1).
   */
  public int vertexCount() {
    return vertices.length();
  }

  /**
   * Returns the total number of edges in the graph.
   *
   * @return the total number of edges.
   * Running time: O(1).
   */
  public int edgeCount() {
    return edgeWeights.size();
  }

  /**
   * Returns an array containing all the vertices in the graph.
   *
   * @return an array of vertices.
   * Running time: O(|V|), where |V| is the number of vertices.
   */
  public Object[] getVertices() {
    Object[] vertexArr = new Object[vertices.length()];
    DListNode currNode = vertices.front();
    int index = 0;

    while (currNode != null) {
      vertexArr[index++] = currNode.item;
      currNode = vertices.next(currNode);
    }

    return vertexArr;
  }

  /**
   * Adds a vertex to the graph. If the vertex already exists, the graph is unchanged.
   *
   * @param vertex the vertex to add.
   * Running time: O(1).
   */
  public void addVertex(Object vertex) {
    if (vertex == null || isVertex(vertex)) {
      return;
    }
    vertices.insertBack(vertex);
    DListNode newNode = vertices.back();
    vertexTable.insert(vertex, newNode);
    adjacencyMap.insert(newNode, new DList());
  }

  /**
   * Removes a vertex from the graph. Also removes all edges connected to the vertex.
   * If the vertex does not exist, the graph is unchanged.
   *
   * @param vertex the vertex to remove.
   * Running time: O(d), where d is the degree of the vertex.
   */
  public void removeVertex(Object vertex) {
    if (!isVertex(vertex)) {
      return;
    }

    Entry vertexEntry = vertexTable.find(vertex);
    DListNode vertexNode = (DListNode) vertexEntry.value();

    Entry adjEntry = adjacencyMap.find(vertexNode);
    DList adjList = (DList) adjEntry.value();

    DListNode curr = adjList.front();
    while (curr != null) {
      Object neighbor = curr.item;
      removeEdge(vertex, neighbor);
      curr = adjList.next(curr);
    }

    vertices.remove(vertexNode);
    vertexTable.remove(vertex);
    adjacencyMap.remove(vertexNode);
  }

  /**
   * Checks if a given object is a vertex in the graph.
   *
   * @param vertex the object to check.
   * @return true if the object is a vertex, false otherwise.
   * Running time: O(1).
   */
  public boolean isVertex(Object vertex) {
    return vertexTable.find(vertex) != null;
  }

  /**
   * Returns the degree of a vertex (number of edges connected to it).
   *
   * @param vertex the vertex whose degree is to be calculated.
   * @return the degree of the vertex, or 0 if the vertex does not exist.
   * Running time: O(1).
   */
  public int degree(Object vertex) {
    if (!isVertex(vertex)) {
      return 0;
    }

    Entry entry = vertexTable.find(vertex);
    DListNode node = (DListNode) entry.value();
    Entry adjEntry = adjacencyMap.find(node);
    DList adjList = (DList) adjEntry.value();

    return adjList.length();
  }

  /**
   * Returns the neighbors and edge weights of a vertex.
   *
   * @param vertex the vertex whose neighbors are to be returned.
   * @return a Neighbors object containing the neighbors and weights, or null if
   *         the vertex has no neighbors or does not exist.
   * Running time: O(d), where d is the degree of the vertex.
   */
  public Neighbors getNeighbors(Object vertex) {
    if (!isVertex(vertex)) {
      return null;
    }

    Entry entry = vertexTable.find(vertex);
    DListNode node = (DListNode) entry.value();

    Entry adjEntry = adjacencyMap.find(node);
    DList adjList = (DList) adjEntry.value();

    if (adjList.isEmpty()) {
      return null;
    }

    Object[] neighborList = new Object[adjList.length()];
    int[] weightList = new int[adjList.length()];
    DListNode currNode = adjList.front();
    int index = 0;

    while (currNode != null) {
      Object neighbor = currNode.item;
      neighborList[index] = neighbor;

      VertexPair pair = new VertexPair(vertex, neighbor);
      weightList[index] = (int) edgeWeights.find(pair).value();
      currNode = adjList.next(currNode);
      index++;
    }

    Neighbors neighbors = new Neighbors();
    neighbors.neighborList = neighborList;
    neighbors.weightList = weightList;
    return neighbors;
  }

  /**
   * Adds an edge between two vertices with the specified weight.
   * If the edge already exists, updates the weight.
   *
   * @param u      one endpoint of the edge.
   * @param v      the other endpoint of the edge.
   * @param weight the weight of the edge.
   * Running time: O(1).
   */
  public void addEdge(Object u, Object v, int weight) {
    if (!isVertex(u) || !isVertex(v)) {
      return;
    }

    VertexPair pair = new VertexPair(u, v);

    if (edgeWeights.find(pair) != null) {
      edgeWeights.remove(pair);
      edgeWeights.insert(pair, weight);
      return;
    }

    Entry entryU = vertexTable.find(u);
    DListNode nodeU = (DListNode) entryU.value();

    Entry entryV = vertexTable.find(v);
    DListNode nodeV = (DListNode) entryV.value();

    Entry adjEntryU = adjacencyMap.find(nodeU);
    DList adjListU = (DList) adjEntryU.value();
    adjListU.insertBack(v);

    if (!u.equals(v)) {
      Entry adjEntryV = adjacencyMap.find(nodeV);
      DList adjListV = (DList) adjEntryV.value();
      adjListV.insertBack(u);
    }

    edgeWeights.insert(pair, weight);
  }

  /**
   * Removes an edge between two vertices.
   *
   * @param u one endpoint of the edge.
   * @param v the other endpoint of the edge.
   * Running time: O(1).
   */
  public void removeEdge(Object u, Object v) {
    if (!isVertex(u) || !isVertex(v)) {
      return;
    }

    VertexPair pair = new VertexPair(u, v);
    Entry edgeEntry = edgeWeights.find(pair);
    if (edgeEntry == null) {
      return;
    }

    edgeWeights.remove(pair);

    Entry entryU = vertexTable.find(u);
    DListNode nodeU = (DListNode) entryU.value();
    Entry adjEntryU = adjacencyMap.find(nodeU);
    DList adjListU = (DList) adjEntryU.value();
    removeFromAdjList(adjListU, v);

    if (!u.equals(v)) {
      Entry entryV = vertexTable.find(v);
      DListNode nodeV = (DListNode) entryV.value();
      Entry adjEntryV = adjacencyMap.find(nodeV);
      DList adjListV = (DList) adjEntryV.value();
      removeFromAdjList(adjListV, u);
    }
  }

  /**
   * Checks if an edge exists between two vertices.
   *
   * @param u one endpoint of the edge.
   * @param v the other endpoint of the edge.
   * @return true if the edge exists, false otherwise.
   * Running time: O(1).
   */
  public boolean isEdge(Object u, Object v) {
    return edgeWeights.find(new VertexPair(u, v)) != null;
  }

  /**
   * Returns the weight of an edge, or 0 if the edge does not exist.
   *
   * @param u one endpoint of the edge.
   * @param v the other endpoint of the edge.
   * @return the weight of the edge, or 0 if the edge does not exist.
   * Running time: O(1).
   */
  public int weight(Object u, Object v) {
    Entry edgeEntry = edgeWeights.find(new VertexPair(u, v));
    return (edgeEntry == null) ? 0 : (int) edgeEntry.value();
  }

  /**
   * Removes an object from a DList if it exists.
   *
   * @param list the DList to remove the object from.
   * @param obj  the object to remove.
   */
  private void removeFromAdjList(DList list, Object obj) {
    DListNode curr = list.front();
    while (curr != null) {
      if (curr.item.equals(obj)) {
        list.remove(curr);
        break;
      }
      curr = list.next(curr);
    }
  }
}