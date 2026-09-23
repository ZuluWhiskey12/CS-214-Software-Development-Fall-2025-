import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SongDataWriterTest {

    @TempDir
    Path tmp;

    @Test
    void headerOnly() throws IOException {
        Path out = tmp.resolve("output.csv");
        new SongDataWriter().write(out, new TreeMap<>());

        String got = Files.readString(out).stripTrailing();

        assertEquals("song,number of ratings,mean,standard deviation", got);
    }

    @Test
    void songWritesIntegers() throws IOException {
        SongStats songStats = new SongStats();
        songStats.add(2);
        songStats.add(2);

        Map<String, SongStats> songs = new TreeMap<>();
        songs.put("All Star", songStats);

        Path output = tmp.resolve("output.csv");
        new SongDataWriter().write(output, songs);

        String expected = "song,number of ratings,mean,standard deviation\n" + "All Star,2,2.0,0.0";
        String got = Files.readString(output).replace("\r\n", "\n").stripTrailing();

        assertEquals(expected, got);
    }

    @Test
    void songWritesDecimals() throws IOException {
        SongStats songStats = new SongStats();
        songStats.add(3);
        songStats.add(4);

        Map<String, SongStats> songs = new TreeMap<>();
        songs.put("All Star", songStats);

        Path output = tmp.resolve("output.csv");
        new SongDataWriter().write(output, songs);

        String expected = "song,number of ratings,mean,standard deviation\n" + "All Star,2,3.5,0.5";
        String got = Files.readString(output).replace("\r\n", "\n").stripTrailing();

        assertEquals(expected, got);
    }
}
