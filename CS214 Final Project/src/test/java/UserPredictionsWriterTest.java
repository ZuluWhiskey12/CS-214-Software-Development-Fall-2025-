import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class UserPredictionsWriterTest {
    @TempDir
    Path tmp;

    @Test
    void headerOnly() throws IOException {
        Path out = tmp.resolve("predictions.csv");

        new UserPredictionsWriter().write(out, List.of());

        String got = Files.readString(out).stripTrailing();
        assertEquals("song,user,predicted rating", got);
    }

    @Test
    void writesSingleRow() throws IOException {
        Path out = tmp.resolve("predictions.csv");

        PredictionRow row = new PredictionRow("songA", "alex", 3.0);
        new UserPredictionsWriter().write(out, List.of(row));

        String got = Files.readString(out).replace("\r\n", "\n").stripTrailing();
        String expected = "song,user,predicted rating\n" + "songA,alex,3";

        assertEquals(expected, got);
    }

    @Test
    void writesNaNPrediction() throws IOException {
        Path out = tmp.resolve("predictions.csv");

        PredictionRow row = new PredictionRow("songA", "alex", Double.NaN);
        new UserPredictionsWriter().write(out, List.of(row));

        String got = Files.readString(out).replace("\r\n", "\n").stripTrailing();
        String expected = "song,user,predicted rating\n" + "songA,alex,NaN";

        assertEquals(expected, got);
    }
}
