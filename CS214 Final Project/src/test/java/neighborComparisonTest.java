import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class neighborComparisonTest {
    
    @Test
    void closestNeighborIsFirst() {
        Username base = new Username("base");
        Username near = new Username("near");
        Username far = new Username("far");

        Map<UsernamePair, Double> byPair = new TreeMap<>();
        byPair.put(new UsernamePair(base, near), 1.0);
        byPair.put(new UsernamePair(base, far),  5.0);

        DistanceMatrix matrix = new DistanceMatrix(byPair);

        List<Username> neighbors = new ArrayList<>(List.of(far, near));
        neighbors.sort(new NeighborComparison(base, matrix));

        assertEquals(List.of(near, far), neighbors);
    }

    void tiesBrokenByUsername() {
        Username elizabath = new Username("elizabeth");
        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<UsernamePair, Double> byPair = new TreeMap<>();
        byPair.put(new UsernamePair(elizabath, alex), 2.0);
        byPair.put(new UsernamePair(elizabath, zach), 2.0);

        DistanceMatrix matrix = new DistanceMatrix(byPair);

        List<Username> neighbors = new ArrayList<>(List.of(zach, alex));
        neighbors.sort(new NeighborComparison(elizabath, matrix));

        assertEquals(List.of(alex, zach), neighbors);
    }
}
