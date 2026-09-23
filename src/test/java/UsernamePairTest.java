import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.TreeSet;

import org.junit.jupiter.api.Test;

public class UsernamePairTest {
    @Test
    void storeGivenOrder() {
        UsernamePair pair = new UsernamePair(new Username("alex"), new Username("zach"));

        assertEquals("alex", pair.user1().value());
        assertEquals("zach", pair.user2().value());
    }

    @Test
    void treeSetSorts() {
        UsernamePair pair1 = new UsernamePair(new Username("zach"), new Username("elizabeth"));
        UsernamePair pair2 = new UsernamePair(new Username("alex"), new Username("zach"));

        TreeSet<UsernamePair> sorted = new TreeSet<>();
        sorted.add(pair1);
        sorted.add(pair2);

        UsernamePair first = sorted.first();
        UsernamePair last = sorted.last();

        assertEquals("alex", first.user1().value());
        assertEquals("zach", last.user1().value());
    }
}
