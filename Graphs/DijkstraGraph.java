// === CS400 File Header Information ===
// Name: Rohith Ravikumar
// Email: rravikumar3@wisc.edu
// Group and Team:  P2.1708
// Lecturer: Gary
// Notes to Grader: None

import java.util.PriorityQueue;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * This class extends the BaseGraph data structure with additional methods for computing the total
 * cost and list of node data along the shortest path connecting a provided starting to ending
 * nodes. This class makes use of Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number> extends BaseGraph<NodeType, EdgeType>
    implements GraphADT<NodeType, EdgeType> {

  /**
   * While searching for the shortest path between two nodes, a SearchNode contains data about one
   * specific path between the start node and another node in the graph. The final node in this path
   * is stored in its node field. The total cost of this path is stored in its cost field. And the
   * predecessor SearchNode within this path is referened by the predecessor field (this field is
   * null within the SearchNode containing the starting node in its node field).
   *
   * SearchNodes are Comparable and are sorted by cost so that the lowest cost SearchNode has the
   * highest priority within a java.util.PriorityQueue.
   */
  protected class SearchNode implements Comparable<SearchNode> {
    public Node node;
    public double cost;
    public SearchNode predecessor;

    public SearchNode(Node node, double cost, SearchNode predecessor) {
      this.node = node;
      this.cost = cost;
      this.predecessor = predecessor;
    }

    public int compareTo(SearchNode other) {
      if (cost > other.cost)
        return +1;
      if (cost < other.cost)
        return -1;
      return 0;
    }
  }

  /**
   * Constructor that sets the map that the graph uses.
   */
  public DijkstraGraph() {
    super(new HashtableMap<>());
  }

  /**
   * This helper method creates a network of SearchNodes while computing the shortest path between
   * the provided start and end locations. The SearchNode that is returned by this method is
   * represents the end of the shortest path that is found: it's cost is the cost of that shortest
   * path, and the nodes linked together through predecessor references represent all of the nodes
   * along that shortest path (ordered from end to start).
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return SearchNode for the final end node within the shortest path
   * @throws NoSuchElementException when no path from start to end is found or when either start or
   *                                end data do not correspond to a graph node
   */
  protected SearchNode computeShortestPath(NodeType start, NodeType end) {

    if (!this.containsNode(start) || !this.containsNode(end)) {
      throw new NoSuchElementException(start + "or" + end + "Not found in the graph");
    }

    // Creating Queue and visited set as PlaceHolderMap, which will contain all the predecessors
    PriorityQueue<SearchNode> queue = new PriorityQueue<>();
    HashtableMap<NodeType, SearchNode> vistedSet = new HashtableMap<>();
    // Making sure to always update the new costs of the nodes
    HashtableMap<NodeType, Double> costs = new HashtableMap<>();
    Node startingNode = this.nodes.get(start);

    // Indicates this is the node with no pred and a value of 0(Only applies to starting)
    SearchNode startSearchNode = new SearchNode(startingNode, 0.0, null);
    queue.add(startSearchNode);
    costs.put(start, 0.0);

    // Dijkstra Algo
    while (!queue.isEmpty()) {
      // Get shortest costing node
      SearchNode searchCurrentNode = queue.poll();
      Node currentNode = searchCurrentNode.node;

      // If found the end return final end node
      if (currentNode.data.equals(end)) {
        return searchCurrentNode;
      }

      for (int i = 0; i < currentNode.edgesLeaving.size(); i++) {
        // Getting the edges from current node
        Edge edge = currentNode.edgesLeaving.get(i);
        // Getting the nodes from the current nodes(Data)
        Node succNode = edge.successor;
        NodeType succNodeData = succNode.data;

        // Finding the total costs from one node to another and making that newNode that cost
        double newCosts = searchCurrentNode.cost + edge.data.doubleValue();

        if (!costs.containsKey(succNodeData) || newCosts < costs.get(succNodeData)) {
          // If so add it in queue as visited, and create a searchNode of it
          // Making sure to update the current node, whatever its pred was and the total cost.
          SearchNode succSearchNode = new SearchNode(succNode, newCosts, searchCurrentNode);
          queue.add(succSearchNode);

          if (!costs.containsKey(succNodeData)) {
            costs.put(succNodeData, newCosts);
            vistedSet.put(succNodeData, succSearchNode);
          }
        }
      }
    }
    // If the loop exits the Dijkstra algo, it means there is no path found
    throw new NoSuchElementException("No path exists between " + start + " and " + end);
  }

  /**
   * Returns the list of data values from nodes along the shortest path from the node with the
   * provided start value through the node with the provided end value. This list of data values
   * starts with the start value, ends with the end value, and contains intermediary values in the
   * order they are encountered while traversing this shorteset path. This method uses Dijkstra's
   * shortest path algorithm to find this solution.
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return list of data item from node along this shortest path
   */
  public List<NodeType> shortestPathData(NodeType start, NodeType end) {
    SearchNode endNode = computeShortestPath(start, end);
    List<NodeType> path = new LinkedList<>();
    SearchNode currentNode = endNode;
    while (currentNode != null) {
      path.add(currentNode.node.data);
      currentNode = currentNode.predecessor;
    }
    Collections.reverse(path);
    return path;
  }

  /**
   * Returns the cost of the path (sum over edge weights) of the shortest path from the node
   * containing the start data to the node containing the end data. This method uses Dijkstra's
   * shortest path algorithm to find this solution.
   *
   * @param start the data item in the starting node for the path
   * @param end   the data item in the destination node for the path
   * @return the cost of the shortest path between these nodes
   */
  public double shortestPathCost(NodeType start, NodeType end) {
    SearchNode endNode = computeShortestPath(start, end);
    return endNode.cost;
  }

  // TODO: implement 3+ tests in step 4.

  @Test
  public void test1() {
    DijkstraGraph<String, Double> graph = new DijkstraGraph<>();
    graph.insertNode("A");
    graph.insertNode("B");
    graph.insertNode("M");
    graph.insertNode("E");
    graph.insertNode("I");
    graph.insertNode("D");
    graph.insertNode("F");
    graph.insertNode("G");
    graph.insertNode("H");
    graph.insertNode("L");

    graph.insertEdge("A", "B", 1.0);
    graph.insertEdge("A", "H", 7.0);
    graph.insertEdge("A", "M", 5.0);
    graph.insertEdge("B", "M", 3.0);
    graph.insertEdge("M", "I", 4.0);
    graph.insertEdge("M", "E", 3.0);
    graph.insertEdge("M", "F", 4.0);
    graph.insertEdge("I", "H", 2.0);
    graph.insertEdge("D", "A", 7.0);
    graph.insertEdge("D", "F", 4.0);
    graph.insertEdge("D", "G", 2.0);
    graph.insertEdge("F", "G", 9.0);
    graph.insertEdge("G", "H", 9.0);
    graph.insertEdge("G", "L", 7.0);
    graph.insertEdge("G", "A", 4.0);
    graph.insertEdge("H", "L", 2.0);
    graph.insertEdge("H", "I", 2.0);
    graph.insertEdge("H", "B", 6.0);

    // Shortest Path from D to I --> should return I as the final node
    DijkstraGraph<String, Double>.SearchNode endNode = graph.computeShortestPath("D", "I");
    Assertions.assertEquals("I", endNode.node.data, "The computed path is incorrect.");
  }


  @Test
  public void test2() {
    DijkstraGraph<String, Double> graph = new DijkstraGraph<>();
    graph.insertNode("A");
    graph.insertNode("B");
    graph.insertNode("M");
    graph.insertNode("E");
    graph.insertNode("I");
    graph.insertNode("D");
    graph.insertNode("F");
    graph.insertNode("G");
    graph.insertNode("H");
    graph.insertNode("L");

    graph.insertEdge("A", "B", 1.0);
    graph.insertEdge("A", "H", 7.0);
    graph.insertEdge("A", "M", 5.0);
    graph.insertEdge("B", "M", 3.0);
    graph.insertEdge("M", "I", 4.0);
    graph.insertEdge("M", "E", 3.0);
    graph.insertEdge("M", "F", 4.0);
    graph.insertEdge("I", "H", 2.0);
    graph.insertEdge("D", "A", 7.0);
    graph.insertEdge("D", "F", 4.0);
    graph.insertEdge("D", "G", 2.0);
    graph.insertEdge("F", "G", 9.0);
    graph.insertEdge("G", "H", 9.0);
    graph.insertEdge("G", "L", 7.0);
    graph.insertEdge("G", "A", 4.0);
    graph.insertEdge("H", "L", 2.0);
    graph.insertEdge("H", "I", 2.0);
    graph.insertEdge("H", "B", 6.0);

    // Shortest Path from D to B

    List<String> path = graph.shortestPathData("D", "B");
    // Should get four nodes: D --> G --> A --> B
    String correctPath = "[D, G, A, B]";
    Assertions.assertEquals(path.toString(), correctPath, "The path is not correct");

    double cost = graph.shortestPathCost("D", "B");
    Assertions.assertEquals(7.0, cost, "It should equal 7.0");
  }

  @Test
  public void test3() {
    DijkstraGraph<String, Double> graph = new DijkstraGraph<>();
    graph.insertNode("A");
    graph.insertNode("B");
    graph.insertNode("M");
    graph.insertNode("E");
    graph.insertNode("I");
    graph.insertNode("D");
    graph.insertNode("F");
    graph.insertNode("G");
    graph.insertNode("H");
    graph.insertNode("L");

    graph.insertEdge("A", "B", 1.0);
    graph.insertEdge("A", "H", 7.0);
    graph.insertEdge("A", "M", 5.0);
    graph.insertEdge("B", "M", 3.0);
    graph.insertEdge("M", "I", 4.0);
    graph.insertEdge("M", "E", 3.0);
    graph.insertEdge("M", "F", 4.0);
    graph.insertEdge("I", "H", 2.0);
    graph.insertEdge("D", "A", 7.0);
    graph.insertEdge("D", "F", 4.0);
    graph.insertEdge("D", "G", 2.0);
    graph.insertEdge("F", "G", 9.0);
    graph.insertEdge("G", "H", 9.0);
    graph.insertEdge("G", "L", 7.0);
    graph.insertEdge("G", "A", 4.0);
    graph.insertEdge("H", "L", 2.0);
    graph.insertEdge("H", "I", 2.0);
    graph.insertEdge("H", "B", 6.0);

    // Path that doesn't exists: E to L
    DijkstraGraph<String, Double>.SearchNode endNode = null;
    try {
       endNode = graph.computeShortestPath("E", "L");
    } catch (NoSuchElementException e) {
      // Expected since no path
    }
    Assertions.assertNull(endNode, "Should be null since no path from E to L");
  }
}
