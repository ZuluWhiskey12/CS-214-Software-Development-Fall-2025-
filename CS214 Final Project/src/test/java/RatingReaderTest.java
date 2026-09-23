import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class RatingReaderTest {

    @TempDir
    Path tmp;

    @Test
    void parsingRow() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "All Star,alex,4");

        List<RatingRow> rows = new RatingsReader().read(input);

        assertEquals(1, rows.size());
        assertEquals("All Star", rows.get(0).song());
        assertEquals("alex", rows.get(0).user());
        assertEquals(4, rows.get(0).rating());
    }

    @Test
    void trimExtraSpaces() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "All Star, alex , 4 ");

        List<RatingRow> rows = new RatingsReader().read(input);

        assertEquals("alex", rows.get(0).user());
    }

    @Test
    void emptyLinesShouldSkip() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, String.join("\n",
                "All Star,alex,4",
                "",
                "Sweet Home Alabama,cameron,2"));

        List<RatingRow> rows = new RatingsReader().read(input);

        assertEquals(2, rows.size());
    }

    @Test
    void notIntRating() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "All Star,alex,notInt");

        assertThrows(IllegalArgumentException.class, () -> new RatingsReader().read(input));
    }

    @Test
    void ratingLowerThanExpected() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "All Star,alex,0");

        assertThrows(IllegalArgumentException.class, () -> new RatingsReader().read(input));
    }

    @Test
    void ratingHigherThanExpected() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "All Star,alex,6");

        assertThrows(IllegalArgumentException.class, () -> new RatingsReader().read(input));
    }

    @Test
    void missingField() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "All Star,alex");

        assertThrows(IllegalArgumentException.class, () -> new RatingsReader().read(input));
    }

    @Test
    void extraField() throws IOException {
        Path input = tmp.resolve("input.csv");
        Files.writeString(input, "All Star,alex,5,extra");

        assertThrows(IllegalArgumentException.class, () -> new RatingsReader().read(input));
    }
}
