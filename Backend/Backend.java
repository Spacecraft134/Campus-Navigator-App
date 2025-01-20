import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Backend handles graph operations such as loading data, finding shortest paths,
 * and retrieving locations, using a GraphADT for storage.
 */
public class Backend implements BackendInterface {

  private GraphADT<String, Double> graph;

  /*
   * Implementing classes should support the constructor below.
   * 
   * @param graph object to store the backend's graph data
   */
  public Backend(GraphADT<String, Double> graph) {
    this.graph = graph;
  }

  /**
   * Loads graph data from a dot file. If a graph was previously loaded, this method should first
   * delete the contents (nodes and edges) of the existing graph before loading a new one.
   * 
   * @param filename the path to a dot file to read graph data from
   * @throws IOException if there was any problem reading from this file
   */
  @Override
  public void loadGraphData(String filename) throws IOException {
    // Clears the graph
    List<String> allNodes = graph.getAllNodes();
    for (String node : allNodes) {
      graph.removeNode(node);
    }

    String line = "";
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.contains("->")) {
          String[] sides = line.split("->");
          if (sides.length == 2) {
            // Remove the quotes from the node
            String pred = sides[0].trim().replaceAll("\"", "");
            // Temp variable storing both edge and weight
            String edgeWithWeight = sides[1].trim();
            String[] edgeAndWeightParts = edgeWithWeight.split("\\[");
            // remove the quotes from the edge
            String succ = edgeAndWeightParts[0].trim().replaceAll("\"", "");
            
            // Getting the weight
            String weightString = "";
            if (edgeAndWeightParts.length > 1) {
              weightString = edgeAndWeightParts[1];
            }
            // Split at the "=" sign and take the second part
            String[] parts = weightString.split("=");
            if (parts.length > 1) {
              weightString = parts[1].trim();
            }
            weightString = weightString.replaceAll("[^\\d.]", "");
            double weight = 0.0;

	    // Making sure the weight is actually an integer, and if not throws an expection
            try {
              weight = Double.parseDouble(weightString);
            } catch (NumberFormatException e) {
              throw new IllegalArgumentException("Invalid weight in the DOT file: " + weightString);
            }
            
            // Insert nodes if they do not already exist, then add the edge between them
            if (!graph.containsNode(pred)) {
                graph.insertNode(pred);
            }
            if (!graph.containsNode(succ)) {
                graph.insertNode(succ);
            }

            graph.insertEdge(pred, succ, weight);
          }

        }
      }
      //Throw any error when reading from the file
    } catch (IOException e) {
      throw new IOException("Error reading from file: " + filename);
    }
  }

  /**
   * Returns a list of all locations (node data) available in the graph.
   * 
   * @return list of all location names
   */
  @Override
  public List<String> getListOfAllLocations() {
    return graph.getAllNodes();
  }

  /**
   * Return the sequence of locations along the shortest path from startLocation to endLocation, or
   * an empty list if no such path exists.
   * 
   * @param startLocation the start location of the path
   * @param endLocation   the end location of the path
   * @return a list with the nodes along the shortest path from startLocation to endLocation, or an
   *         empty list if no such path exists
   */
  @Override
  public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
    List<String> path = graph.shortestPathData(startLocation, endLocation);

    // If path is empty then returns new list
    if (path.isEmpty()) {
      return new ArrayList<>();
    } else {
      // return list with start and end location
      return path;
    }
  }

  /**
   * Return the walking times in seconds between each two nodes on the shortest path from
   * startLocation to endLocation, or an empty list of no such path exists.
   * 
   * @param startLocation the start location of the path
   * @param endLocation   the end location of the path
   * @return a list with the walking times in seconds between two nodes along the shortest path from
   *         startLocation to endLocation, or an empty list if no such path exists
   */
  @Override
  public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {

    List<String> path = findLocationsOnShortestPath(startLocation, endLocation);
    List<Double> times = new ArrayList<>();

    if(path == null || path.isEmpty()) {
	return times;
    }


    for (int i = 0; i < path.size() - 1; i++) {
        String previousNode = path.get(i);
        String currentNode = path.get(i + 1);

        // Getting the each 2 nodes from list and and putting it into times list
        if (graph.containsEdge(previousNode, currentNode)) {
          times.add(graph.getEdge(previousNode, currentNode));
        } else {
          // if no edge between two nodes on list, then no path is exists
          return new ArrayList<>();
        }
      }
    return times;
  }

  /**
   * Returns a list of the ten closest destinations that can be reached most quickly when starting
   * from the specified startLocation.
   * 
   * @param startLocation the location to find the closest destinations from
   * @return the ten closest destinations from the specified startLocation
   * @throws NoSuchElementException if startLocation does not exist, or if there are no other
   *                                locations that can be reached from there
   */
  @Override
  public List<String> getTenClosestDestinations(String startLocation)
      throws NoSuchElementException {
       if (!graph.getAllNodes().contains(startLocation)) {
        throw new NoSuchElementException("Starting location does not exist");
    }

    List<String> allNodes = new ArrayList<>(graph.getAllNodes());
    List<String> closestDestinations = new ArrayList<>();
    List<Double> costs = new ArrayList<>();
    double cost;

    // Collect destinations and their costs
    for (int i = 0; i < allNodes.size(); i++) {
        String location = allNodes.get(i);
        if (!location.equals(startLocation)) {
	    try {	
	      cost = graph.shortestPathCost(startLocation, location);
	    }
	    catch(NoSuchElementException e) {
		continue;
	    }
            if (cost > 0) {
                closestDestinations.add(location);
                costs.add(cost);
            }
        }
    }

    // Sort the destinations by cost using a simple bubble sort
    for (int i = 0; i < costs.size() - 1; i++) {
        for (int j = 0; j < costs.size() - i - 1; j++) {
            if (costs.get(j) > costs.get(j + 1)) {
                // Swap costs
                double tempCost = costs.get(j);
                costs.set(j, costs.get(j + 1));
                costs.set(j + 1, tempCost);

                // Swap corresponding destinations
                String tempDest = closestDestinations.get(j);
                closestDestinations.set(j, closestDestinations.get(j + 1));
                closestDestinations.set(j + 1, tempDest);
            }
        }
    }

    // Return the top 10 closest destinations
    List<String> closestTenDestinations = new ArrayList<>();
    for (int i = 0; i < closestDestinations.size() && i < 10; i++) {
        closestTenDestinations.add(closestDestinations.get(i));
    }

    return closestTenDestinations;
 }
}
