import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;

public class FrontendTests {
    private Frontend fe;
    private BackendInterface be;

    @BeforeEach
    public void setup() {
        be = new Backend_Placeholder(new Graph_Placeholder());
        fe = new Frontend(be);
    }

    @Test
    public void roleTest1() {
        String result = fe.generateShortestPathPromptHTML();
        assertTrue(result.contains("id='start'"), "There must be an input with id 'start'");
        assertTrue(result.contains("id='end'"), "There must be an input with id 'end'");
        assertTrue(result.contains("Find Shortest Path"), "There must be a button with label 'Find Shortest Path'");
    }

    @Test
    public void roleTest2() {
        String result = fe.generateShortestPathResponseHTML("Union South", "Atmospheric, Oceanic and Space Sciences");
        assertTrue(result.contains("<ol>"), "There must be an <ol> tag.");
        assertTrue(result.contains("Total travel time"), "Total travel time is not mentioned.");

        result = fe.generateShortestPathResponseHTML("NonExistentStart", "NonExistentEnd");
        assertTrue(result.contains("not found"), "The response should say no path was found.");
    }

    @Test
    public void roleTest3() {
        String result = fe.generateTenClosestDestinationsResponseHTML("Union South");
        assertTrue(result.contains("<ul>"), "There must be a <ul> tag.");
    }
}
