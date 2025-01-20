import java.util.List;
import java.util.NoSuchElementException;

public class Frontend implements FrontendInterface {
    private BackendInterface backend;

    public Frontend(BackendInterface backend) {
        this.backend = backend;
    }

    @Override
    public String generateShortestPathPromptHTML() {
        StringBuilder result = new StringBuilder("<div>")
                // Append a label and input field for the 'start' location
                .append("<label for='start'>Start Location:</label>")
                .append("<input type='text' id='start' name='start' />")
                // Append a label and input field for the 'end' location
                .append("<label for='end'>End Location:</label>")
                .append("<input type='text' id='end' name='end' />")
                // Append a button with an 'onclick' event to trigger the 'findShortestPath' function
                .append("<input type='button' value='Find Shortest Path' onclick='findShortestPath()' />")
                .append("</div>");
        return result.toString();
    }

    @Override
    public String generateShortestPathResponseHTML(String start, String end) {
        List<String> path = backend.findLocationsOnShortestPath(start, end);
        List<Double> times = backend.findTimesOnShortestPath(start, end);
        if (path.isEmpty()) {
            return "<p>Path from " + start + " to " + end + " not found.</p>";
        }

        StringBuilder str = new StringBuilder("<div>");
        str.append("<p>Shortest path from " + start + " to " + end + ":</p>");
        str.append("<ol>");
        for (String l : path) {
            str.append("<li>" + l + "</li>");
        }
        str.append("</ol>");
        str.append("<p>Total travel time: "  + times.stream().mapToDouble(Double::doubleValue).sum() + " seconds</p>");
        str.append("</div>");
        
        return str.toString();
    }

    @Override
    public String generateTenClosestDestinationsPromptHTML() {
        return new StringBuilder()
            .append("<div><label for='from'>Starting Location:</label><input type='text' id='from' name='from' />")
            .append("<input type='button' value='Find Closest Destinations' onclick='findClosestDestinations()' />")
	    .append("</div>")
            .toString();
    }

    @Override
    public String generateTenClosestDestinationsResponseHTML(String start) {
	try {
	    List<String> closestDestinations = backend.getTenClosestDestinations(start);
            // Checks if list is null or empty which indicates no destination found
	                if (closestDestinations == null || closestDestinations.isEmpty()) {
	      return "<p>\"" + start + "\" is not reachable or does not exist.</p>";
            }

            StringBuilder str = new StringBuilder("<div>");
            str.append("<p>Ten closest destinations from " + start + ":</p>");
            str.append("<ul>");

            // Loop through each destination in the closestDestinations list
            for (String l : closestDestinations) {
                str.append("<li>" + l + "</li>"); // Add each destination as a list item
            }
            str.append("</ul>");
            str.append("</div>");

            return str.toString();
	} catch (NoSuchElementException e) {
	    return "<p>\"" + start + "\" is not reachable or does not exist.</p>";
	}
    }
}
