import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

public class DistanceMatrixTest {
    
    @Test
    void returnsStoredValues() {
        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<UsernamePair, Double> byPair = new TreeMap<>();
        byPair.put(new UsernamePair(alex, zach), 2.5);

        DistanceMatrix matrix = new DistanceMatrix(byPair);

        assertEquals(2.5, matrix.get(alex, zach));
    }

    @Test
    void returnsSameValueDespiteOrder() {
        Username alex = new Username("alex");
        Username zach = new Username("zach");

        Map<UsernamePair, Double> byPair = new TreeMap<>();
        byPair.put(new UsernamePair(alex, zach), 2.5);

        DistanceMatrix matrix = new DistanceMatrix(byPair);

        double distance1 = matrix.get(alex, zach);
        double distance2 = matrix.get(zach, alex);

        assertEquals(distance1, distance2);
    }

    @Test
    void returnNaNforMissingPair() {
        Username alex = new Username("alex");
        Username zach = new Username("zach");

        DistanceMatrix matrix = new DistanceMatrix(Map.of());

        double distance = matrix.get(alex, zach);
        assertTrue(Double.isNaN(distance));
    }

    @Test 
    void returnsNaNForSelfPair() {
        Username alex = new Username("alex");

        DistanceMatrix matrix = new DistanceMatrix(Map.of());

        double distance = matrix.get(alex, alex);
        assertTrue(Double.isNaN(distance));
    }
}
