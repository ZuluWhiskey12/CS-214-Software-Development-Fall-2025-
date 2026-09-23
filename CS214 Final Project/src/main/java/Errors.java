import java.io.IOException;
import java.nio.file.Path;

/*
 * Layer: framework / error handling
 * Role: Factory for all exceptions and error messages (args, files, CSV content, PA3/PA4/PA5 conditions).
 * Depends on: Path, IOException
 * Used by: ArgsValidator, CsvDataReader, runners, IO helpers, and others
 */

 //Central place for building exceptions with consistent error messages
public final class Errors {
    private Errors() {
    }

    // Argument messages
    private static final String incorrectArgCount = "incorrect number of arguments";
    private static final String unsupportedArgs = "unsupported argument '%s'";
    private static final String badCsvExtension = "input and output paths must have .csv extension";
    // File messages
    private static final String inputNotFound = "input file does not exist: %s";
    private static final String inputEmpty = "input file is empty: %s";
    private static final String outputNotWritable = "output path is not writable: %s";
    // Content of CSV
    private static final String malformedRow = "CSV has malformed row";
    private static final String nonIntegerRating = "rating must be an integer";
    private static final String ratingOutOfRange = "rating out of range";

    //PA3 & PA4
    private static final String tooFewCooperativeUsers = "at least two cooperative users are required for song similarity";
    private static final String noPredictions = "no predictions to be made";

    //PA5
    private static final String noSongs = "must select at least one song for recommendations";
    private static final String noRecommendations = "no songs to recommend. Songs may have been removed. Try with a larger file or fewer selections.";
    private static final String tooFewDistinctRatings = "selected songs must have more than one distinct rating";
    private static final String selctionNotFound = "selected song not found in input data: %s";
    private static final String duplicateUserSelections = "duplicate song selections are not allowed";

    // Arguments
    public static IllegalArgumentException incorrectArgCount() {
        return new IllegalArgumentException(incorrectArgCount);
    }

    public static IllegalArgumentException unsupportedArgs(String flag) {
        return new IllegalArgumentException(unsupportedArgs.formatted(flag));
    }

    public static IllegalArgumentException badCsvExtension() {
        return new IllegalArgumentException(badCsvExtension);
    }

    // Files
    public static IOException inputNotFound(Path path) {
        return new IOException(inputNotFound.formatted(path));
    }

    public static IOException inputEmpty(Path path) {
        return new IOException(inputEmpty.formatted(path));
    }

    public static IOException outputNotWritable(Path path) {
        return new IOException(outputNotWritable.formatted(path));
    }

    // CSV Content
    public static IllegalArgumentException malformedRow() {
        return new IllegalArgumentException(malformedRow);
    }

    public static IllegalArgumentException nonIntegerRating() {
        return new IllegalArgumentException(nonIntegerRating);
    }

    public static IllegalArgumentException ratingOutOfRange() {
        return new IllegalArgumentException(ratingOutOfRange);
    }

    // PA3 & PA4
    public static IllegalArgumentException tooFewCooperativeUsers() {
        return new IllegalArgumentException(tooFewCooperativeUsers);
    }

    public static IllegalArgumentException noPredictions() {
        return new IllegalArgumentException(noPredictions);
    }

    //PA5
    public static IllegalArgumentException noSongs() {
        return new IllegalArgumentException(noSongs);
    }

    public static IllegalArgumentException noRecommendations() {
        return new IllegalArgumentException(noRecommendations);
    }

    public static IllegalArgumentException tooFewDistinctRatings() {
        return new IllegalArgumentException(tooFewDistinctRatings);
    }

    public static IllegalArgumentException selectionNotFound(String song) {
        return new IllegalArgumentException(selctionNotFound.formatted(song));
    }

    public static IllegalArgumentException duplicateUserSelection() {
        return new IllegalArgumentException(duplicateUserSelections);
    }
}
