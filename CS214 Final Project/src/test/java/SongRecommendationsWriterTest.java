import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SongRecommendationsWriterTest {
    @TempDir
    Path tmp;

    @Test
    void headerOnly() throws IOException {
        Path out = tmp.resolve("recomendations.csv");

        new SongRecommendationsWriter().write(out, List.of());

        String got = Files.readString(out).stripTrailing();
        assertEquals("user choice,recommendation", got);
    }

    @Test
    void writesSingleRow() throws IOException {
        Path out = tmp.resolve("recomendations.csv");

        SongRecommendation recommendation = new SongRecommendation("song1", "song2");
        new SongRecommendationsWriter().write(out, List.of(recommendation));

        String got = Files.readString(out).replace("\r\n", "\n").stripTrailing();
        String expected = "user choice,recommendation\n" + "song1,song2";

        assertEquals(expected, got);
    }
}
