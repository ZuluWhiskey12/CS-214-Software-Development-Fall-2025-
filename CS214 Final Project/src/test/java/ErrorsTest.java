import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class ErrorsTest {

    @Test
    void incorrectArgCountError() {
        IllegalArgumentException e = Errors.incorrectArgCount();

        assertEquals("incorrect number of arguments", e.getMessage());
    }

    @Test
    void unsupportedArgsError() {
        IllegalArgumentException e = Errors.unsupportedArgs("-b");

        assertEquals("unsupported argument '-b'", e.getMessage());
    }

    @Test
    void badCsvExtensionError() {
        IllegalArgumentException e = Errors.badCsvExtension();

        assertEquals("input and output paths must have .csv extension", e.getMessage());
    }

    @Test
    void inputNotFoundError() {
        Path inputPath = Paths.get("missing.csv");
        IOException e = Errors.inputNotFound(inputPath);

        assertEquals("input file does not exist: missing.csv", e.getMessage());
    }

    @Test
    void inputEmptyError() {
        Path inputPath = Paths.get("empty.csv");
        IOException e = Errors.inputEmpty(inputPath);

        assertEquals("input file is empty: empty.csv", e.getMessage());
    }

    @Test
    void outputNotWritableError() {
        Path outputPath = Paths.get("out/dir");
        IOException e = Errors.outputNotWritable(outputPath);

        assertEquals("output path is not writable: out/dir", e.getMessage());
    }

    @Test
    void malformedRowError() {
        IllegalArgumentException e = Errors.malformedRow();

        assertEquals("CSV has malformed row", e.getMessage());
    }

    @Test
    void nonIntegerRatingError() {
        IllegalArgumentException e = Errors.nonIntegerRating();

        assertEquals("rating must be an integer", e.getMessage());
    }

    @Test
    void ratingOutOfRangeError() {
        IllegalArgumentException e = Errors.ratingOutOfRange();

        assertEquals("rating out of range", e.getMessage());
    }

    @Test
    void tooFewCooperativeUsersError() {
        IllegalArgumentException e = Errors.tooFewCooperativeUsers();

        assertEquals("at least two cooperative users are required for song similarity", e.getMessage());
    }

    @Test
    void noPredictions() {
        IllegalArgumentException e = Errors.noPredictions();

        assertEquals("no predictions to be made", e.getMessage());
    }

    @Test
    void noSongs() {
        IllegalArgumentException e = Errors.noSongs();

        assertEquals("must select at least one song for recommendations", e.getMessage());
    }

    @Test
    void noRecommendations() {
        IllegalArgumentException e = Errors.noRecommendations();

        assertEquals("no songs to recommend. Songs may have been removed. Try with a larger file or fewer selections.",
                e.getMessage());
    }

    @Test
    void tooFewDistinctRatings() {
        IllegalArgumentException e = Errors.tooFewDistinctRatings();

        assertEquals("selected songs must have more than one distinct rating", e.getMessage());
    }

    @Test
    void selectionNotFound() {
        String song1 = "song1";
        IllegalArgumentException e = Errors.selectionNotFound(song1);

        assertEquals("selected song not found in input data: song1", e.getMessage());
    }

    @Test
    void duplicateUserSelection() {
        IllegalArgumentException e = Errors.duplicateUserSelection();

        assertEquals("duplicate song selections are not allowed", e.getMessage());
    }
}
