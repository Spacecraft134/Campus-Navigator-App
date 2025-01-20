import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;

public class BackendTests {

  /**
   * Checks if the graph is loaded in correctly and returns all the nodes in the loaded graph
   */
  @Test
  public void roleTest1() {
    Graph_Placeholder testgraph = new Graph_Placeholder();
    Backend backend = new Backend(testgraph);

    try {
      // Load the graph data from the campus.dot file
      backend.loadGraphData("campus.dot");
    } catch (IOException e) {
      Assertions.fail("Failed to load graph data: " + e.getMessage());
    }

    // Verify that the graph contains the expected nodes
    List<String> nodes = backend.getListOfAllLocations();
    //Checks if these locations are in the list
    Assertions.assertTrue(nodes.contains("Union South"));
    Assertions.assertTrue(nodes.contains("Computer Sciences and Statistics"));
    Assertions.assertTrue(nodes.contains("Atmospheric, Oceanic and Space Sciences"));
  }
  
  /**
   * Tests the shortest path between two locations and the time it takes to get there
   */
  @Test
  public void roletest2() {
    Graph_Placeholder testgraph = new Graph_Placeholder();
    Backend backend = new Backend(testgraph);
    
    // Find the shortest path between two specified locations
    List<String> path = backend.findLocationsOnShortestPath("Union South", 
        "Computer Sciences and Statistics");
    //Checks if path is not empty
    Assertions.assertFalse(path.isEmpty(), "There should be a valid path.");
    //Checks if path first location is Union South
    Assertions.assertEquals("Union South", path.get(0), "First location should be Union South");
    //Checks the last location is Computer Sciences and Statistics
    Assertions.assertEquals("Computer Sciences and Statistics", path.get(path.size() - 1),
        "Last location should be Computer Sciences and Statistics.");
  }
  
  /**
   * Prints out the 10 closest destinations from the starting location
   */
  @Test
  public void roleTest3() {
    Graph_Placeholder testgraph = new Graph_Placeholder();
    Backend backend = new Backend(testgraph);
    
    // Get the ten closest destinations from the starting location
    List<String> closestDestinations = backend.getTenClosestDestinations("Union South");
    // Checks that the number of closest destinations does not exceed 10
    Assertions.assertTrue(closestDestinations.size() <= 10, "Should be at most 10 locations.");
    //Checks if it contains Atmospheric, Oceanic and Space Sciences
    Assertions.assertTrue(closestDestinations.contains("Atmospheric, Oceanic and Space Sciences"),
        "List should contain Atmospheric, Oceanic and Space Sciences");
  }

  /**
   * Test for loading an empty DOT file.
   * This test ensures that if an empty file is provided, the graph remains empty.
   */
 @Test
 public void testLoadEmptyGraph() {
    GraphADT<String, Double> graph = new Graph_Placeholder();
    Backend backend = new Backend(graph);
    
    // Test with an empty dot file
    try {
        backend.loadGraphData("empty.dot");
	Assertions.fail("IOException should be thrown for a non-existent file.");
    } catch (IOException e) {
        Assertions.assertTrue(e.getMessage().contains("Error reading from file"),
            "Error message should indicate file reading issue.");
    }
 }
/**
  * Test for finding times on the shortest path between two unconnected locations.
  * This test checks if the method correctly handles cases where no path exists between the start and end nodes.
  * 
  * Expected Behavior: The returned list should be empty if no path exists.
  */
 @Test
 public void testFindTimesOnShortestPathNoPath() {
    GraphADT<String, Double> graph = new Graph_Placeholder();
    Backend backend = new Backend(graph);
    
    // Insert nodes without connecting them with an edge
    graph.insertNode("A");
    graph.insertNode("B");
    
    // Try to find a path between two unconnected nodes
    List<Double> times = backend.findTimesOnShortestPath("A", "B");
    
    // Assert that the list is empty, indicating no path was found
    Assertions.assertTrue(times.isEmpty(), "The path list should be empty if no path exists.");
 }

    /**
     * Test the integration of shortest path generation between frontend and backend.
     * Ensures proper HTML is generated when a path exists.
     */
    @Test
    public void testShortestPathIntegration() throws Exception {
        GraphADT<String, Double> graph = new DijkstraGraph<>();;
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        // Add nodes and edges for this test
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertEdge("A", "B", 10.0);

        // Generate HTML response from frontend
        String htmlResponse = frontend.generateShortestPathResponseHTML("A", "B");

        // Validate that the HTML contains the expected path and total time
        Assertions.assertTrue(htmlResponse.contains("Shortest path from A to B:"));
        Assertions.assertTrue(htmlResponse.contains("<li>A</li>"));
        Assertions.assertTrue(htmlResponse.contains("<li>B</li>"));
        Assertions.assertTrue(htmlResponse.contains("10.0 seconds"));
    }

    /**
     * Test the integration for handling invalid shortest path queries.
     * Ensures proper error HTML is generated when no path exists.
     */
    @Test
    public void testInvalidShortestPathIntegration() throws Exception {
        // Initialize the GraphADT, Backend, and Frontend
        GraphADT<String, Double> graph = new DijkstraGraph<>();;
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        // Add nodes but no edges
        graph.insertNode("X");
        graph.insertNode("Y");

        try {
        // Generate HTML response from frontend (this should throw the exception)
        frontend.generateShortestPathResponseHTML("X", "Y");

        // If exception is not thrown, fail the test
        Assertions.fail("Expected NoSuchElementException, but it was not thrown.");
	} catch (NoSuchElementException e) {
        // Validate that the exception message is correct
        Assertions.assertEquals("No path exists between X and Y", e.getMessage());
	}
    }

    /**
     * Test the integration for closest destinations when the graph is empty.
     * Verifies that the frontend properly handles an empty graph.
     */
    @Test
    public void testClosestDestinationsEmptyGraphIntegration() {
        GraphADT<String, Double> graph = new DijkstraGraph<>();;
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

	try {
	 String htmlResponse = frontend.generateTenClosestDestinationsResponseHTML("A");
	 Assertions.assertTrue(htmlResponse.contains("\"A\""));
	}
	catch (NoSuchElementException e) {
	    //Correct   
	}
    }

     /**
     * Test the integration of shortest path when the graph contains cycles.
     * Verifies that the shortest path avoids infinite loops and calculates the correct cost.
     */
    @Test
    public void testShortestPathWithCycleIntegration() {
        GraphADT<String, Double> graph = new DijkstraGraph<>();;
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertEdge("A", "B", 3.0);
        graph.insertEdge("B", "C", 4.0);
        graph.insertEdge("C", "A", 2.0);

        String htmlResponse = frontend.generateShortestPathResponseHTML("A", "C");

        Assertions.assertTrue(htmlResponse.contains("Shortest path from A to C:"));
        Assertions.assertTrue(htmlResponse.contains("7.0 seconds")); // Path: A -> B -> C
    } 
}
