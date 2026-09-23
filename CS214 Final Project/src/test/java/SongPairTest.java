import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.TreeSet;

import org.junit.jupiter.api.Test;

public class SongPairTest {

    @Test
    void storeGivenOrder() {
        SongPair pair = new SongPair(new Song("I Gotta Feeling"), new Song("Big Girls Don't Cry"));

        assertEquals("I Gotta Feeling", pair.song1().value());
        assertEquals("Big Girls Don't Cry", pair.song2().value());
    }

    @Test
    void treeSetSorts() {
        SongPair pair1 = new SongPair(new Song("Suit & Tie"), new Song("I Gotta Feeling"));
        SongPair pair2 = new SongPair(new Song("Big Girls Don't Cry"), new Song("Suit & Tie"));

        TreeSet<SongPair> sorted = new TreeSet<>();
        sorted.add(pair1);
        sorted.add(pair2);

        SongPair first = sorted.first();
        SongPair last = sorted.last();

        assertEquals("Big Girls Don't Cry", first.song1().value());
        assertEquals("Suit & Tie", last.song1().value());
    }
}
