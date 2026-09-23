import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SongSimilarityWriterTest {

    @TempDir Path tmp;

    @Test
    void headerOnly() throws IOException {
        Path output = tmp.resolve("userSimilarity.csv");

        new SongSimilarityWriter().write(output, new TreeMap<>());

        String got = Files.readString(output).stripTrailing();
        assertEquals("name1,name2,similarity", got);
    }

    @Test
    void writesPairsAndScores() throws IOException {
        Path output = tmp.resolve("pairs.csv");

        Map<SongPair, Double> test = new TreeMap<>();
        test.put(new SongPair(new Song("song1"), new Song("song2")), 1.5);
        test.put(new SongPair(new Song("song1"), new Song("song3")), 2.0);

        new SongSimilarityWriter().write(output, test);

        String expected = "name1,name2,similarity\n" + "song1,song2,1.5\n" + "song1,song3,2.0";
        String got = Files.readString(output).replace("\r\n", "\n").stripTrailing();

        assertEquals(expected, got);
    }
}
